package com.parashift.onlyoffice;

import org.alfresco.service.cmr.repository.*;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Service
public class Converter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MimetypeService mimetypeService;

    @Autowired
    ConfigManager configManager;

    @Autowired
    JwtManager jwtManager;

    @Autowired
    Util util;

    private static Set<String> ConvertBackList = new HashSet<String>() {{
        add("application/vnd.oasis.opendocument.text");
        add("application/vnd.oasis.opendocument.spreadsheet");
        add("application/vnd.oasis.opendocument.presentation");
        add("text/plain");
        add("text/csv");
        add("application/rtf");
        add("application/x-rtf");
        add("text/richtext");
    }};

    public String GetModernMimetype(String mimetype) {
        List<String> mimeConvertToWord = configManager.getListDefaultProperty("docservice.mime.convert.word");
        List<String> mimeConvertToCell = configManager.getListDefaultProperty("docservice.mime.convert.cell");
        List<String> mimeConvertToSlide = configManager.getListDefaultProperty("docservice.mime.convert.slide");

        if (mimeConvertToWord.contains(mimetype)) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (mimeConvertToCell.contains(mimetype)) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if (mimeConvertToSlide.contains(mimetype)) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }

        return null;
    }

    public boolean shouldConvertBack(String mimeType) {
        return ConvertBackList.contains(mimeType);
    }

    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        NodeRef ref = options.getSourceNodeRef();
        String srcMime = reader.getMimetype();
        String srcType = mimetypeService.getExtension(srcMime);
        String outType = mimetypeService.getExtension(writer.getMimetype());
        String key = util.getKey(ref) + "." + srcType;
        logger.info("Received conversion request from " + srcType + " to " + outType);

        try {
            String url = convert(key, srcType, outType, util.getContentUrl(ref));
            saveFromUrl(url, writer);
        } catch (Exception ex) {
            logger.info("Conversion failed: " + ex.getMessage());
            throw ex;
        }
    }

    public String convert(String key, String srcType, String outType, String url) throws SecurityException, Exception {
        try (CloseableHttpClient httpClient = GetClient()) {
            JSONObject body = new JSONObject();
            body.put("async", false);
            body.put("embeddedfonts", true);
            body.put("filetype", srcType);
            body.put("outputtype", outType);
            body.put("key", key);
            body.put("url", url);

            StringEntity requestEntity = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
            HttpPost request = new HttpPost(util.getEditorInnerUrl() + "ConvertService.ashx");
            request.setEntity(requestEntity);
            request.setHeader("Accept", "application/json");

            if (jwtManager.jwtEnabled()) {
                String token = jwtManager.createToken(body);
                JSONObject payloadBody = new JSONObject();
                payloadBody.put("payload", body);
                String headerToken = jwtManager.createToken(body);
                body.put("token", token);
                request.setHeader(jwtManager.getJwtHeader(), "Bearer " + headerToken);
            }

            logger.debug("Sending POST to Docserver: " + body.toString());
            try(CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getStatusLine().getStatusCode();

                if(status != HttpStatus.SC_OK) {
                    throw new HttpException("Docserver returned code " + status);
                } else {
                    String content = IOUtils.toString(response.getEntity().getContent(), "utf-8");

                    logger.debug("Docserver returned: " + content);
                    JSONObject callBackJSon = null;
                    try{
                        callBackJSon = new JSONObject(content);
                    } catch (Exception e) {
                        throw new Exception("Couldn't convert JSON from docserver: " + e.getMessage());
                    }

                    if (!callBackJSon.isNull("error") && callBackJSon.getInt("error") == -8) throw new SecurityException();
                    
                    if (callBackJSon.isNull("endConvert") || !callBackJSon.getBoolean("endConvert") || callBackJSon.isNull("fileUrl")) {
                        throw new Exception("'endConvert' is false or 'fileUrl' is empty");
                    }
                    return callBackJSon.getString("fileUrl");
                }
            }
        }
    }

    private void saveFromUrl(String fileUrl, ContentWriter writer) throws Exception {
        try (CloseableHttpClient httpClient = GetClient()) {
            logger.debug("Getting file from " + fileUrl);
            HttpGet request = new HttpGet(fileUrl);

            try(CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getStatusLine().getStatusCode();

                if(status != HttpStatus.SC_OK) {
                    throw new HttpException("Server returned " + status);
                } else {
                    writer.putContent(response.getEntity().getContent());
                }
            }
        }
    }

    private CloseableHttpClient GetClient() throws Exception {
        CloseableHttpClient httpClient;

        String cert = (String) configManager.getOrDefault("cert", "no");
        if (cert.equals("true")) {
            logger.debug("Ignoring SSL");
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } else {
            httpClient = HttpClients.createDefault();
        }

        return httpClient;
    }
}