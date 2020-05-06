package com.parashift.onlyoffice;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.*;
import org.springframework.extensions.surf.util.Pair;
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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/*
    Copyright (c) Ascensio System SIA 2019. All rights reserved.
    http://www.onlyoffice.com
*/

public class Converter extends AbstractContentTransformer2 {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MimetypeService mimetypeService;

    @Autowired
    NodeService nodeService;

    @Autowired
    ConfigManager configManager;

    @Autowired
    JwtManager jwtManager;

    @Autowired
    Util util;

    private static Map<String, Pair<String, ContentReader>> OnGoingConversions = new HashMap<String, Pair<String, ContentReader>>();

    private static Map<String, Set<String>> TransformableDict = new HashMap<String, Set<String>>() {{
        put("application/vnd.oasis.opendocument.text", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("image/png");
        }});
        put("application/msword", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("image/png");
        }});

        put("application/vnd.oasis.opendocument.spreadsheet", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            add("image/png");
        }});
        put("application/vnd.ms-excel", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            add("image/png");
        }});

        put("application/vnd.oasis.opendocument.presentation", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("image/png");
        }});
        put("application/vnd.ms-powerpoint", new HashSet<String>() {{
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("image/png");
        }});

        put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", new HashSet<String>() {{
            add("image/png");
        }});
        put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new HashSet<String>() {{
            add("image/png");
        }});
        put("application/vnd.openxmlformats-officedocument.presentationml.presentation", new HashSet<String>() {{
            add("image/png");
        }});
    }};

    private static Set<String> ConvertBackList = new HashSet<String>() {{
        add("text/plain");
        add("text/csv");
    }};

    public String GetModernMimetype(String mimetype) {
        if (TransformableDict.containsKey(mimetype)) {
            return TransformableDict.get(mimetype).iterator().next();
        } else {
            return null;
        }
    }

    public Pair<String, ContentReader> GetConversion(String key) {
        if (OnGoingConversions.containsKey(key)) {
            return OnGoingConversions.get(key);
        } else {
            return null;
        }
    }    

    @Override
    public boolean isTransformableMimetype(String sourceMimetype, String targetMimetype, TransformationOptions options) {
        if (!TransformableDict.containsKey(sourceMimetype)) return false;
        return TransformableDict.get(sourceMimetype).contains(targetMimetype);
    }

    public boolean shouldConvertBack(String mimeType) {
        return ConvertBackList.contains(mimeType);
    }

    @Override
    protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        NodeRef ref = options.getSourceNodeRef();
        String srcMime = reader.getMimetype();
        String srcType = mimetypeService.getExtension(srcMime);
        String outType = mimetypeService.getExtension(writer.getMimetype());
        String key = util.getKey(ref) + "." + srcType;

        logger.info("Received conversion request from " + srcType + " to " + outType);

        try {
            OnGoingConversions.put(key, new Pair<String, ContentReader>(srcMime, reader));
            String url = convert(key, srcType, outType, util.getConversionUrl(key));
            saveFromUrl(url, writer);
        } catch (Exception ex) {
            logger.info("Conversion failed: " + ex.getMessage());
            throw ex;
        } finally {
            if (OnGoingConversions.containsKey(key)) {
                OnGoingConversions.remove(key);
            }
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

            if (outType.contains("png")) {
                JSONObject thumb = new JSONObject();
                thumb.put("aspect", 1);
                thumb.put("first", true);
                thumb.put("height", 100);
                thumb.put("width", 100);
                body.put("thumbnail", thumb);
            }

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
                request.setHeader((String) configManager.getOrDefault("jwtheader", "Authorization"), "Bearer " + headerToken);
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