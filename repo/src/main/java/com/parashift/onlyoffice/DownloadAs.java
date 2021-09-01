package com.parashift.onlyoffice;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.download.DownloadStorage;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.download.DownloadService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.download-as.post")
public class DownloadAs extends AbstractWebScript {

    private static final String MIMETYPE_ZIP = "application/zip";
    private static final String TEMP_FILE_PREFIX = "alf";
    private static final String ZIP_EXTENSION = ".zip";

    @Autowired
    Converter converterService;

    @Autowired
    Util util;

    @Autowired
    PermissionService permissionService;

    @Autowired
    NodeService nodeService;

    @Autowired
    ContentService contentService;

    @Autowired
    MimetypeService mimetypeService;

    @Autowired
    DownloadService downloadService;

    @Autowired
    DownloadStorage downloadStorage;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        NodeRef downloadsContainerNodeRef = downloadStorage.getOrCreateDowloadContainer();
        response.setContentType("application/json; charset=utf-8");
        response.setContentEncoding("UTF-8");
        JSONObject responseJson = new JSONObject();
        if (request.getContent().getContent().length() != 2) {
            try {
                JSONParser parser = new JSONParser();
                org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(request.getContent().getContent());
                responseJson.put("downloadUrl", createZipWithMultipleFiles(downloadsContainerNodeRef, (JSONArray) json.get("list")));
                response.getWriter().write(responseJson.toString(3));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                throw new WebScriptException("Json parse exception: " + e.getMessage());
            }
        } else if (request.getParameter("nodeRef") != null) {
            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));
            if (request.getParameter("outputType") != null) {
                if (permissionService.hasPermission(nodeRef, PermissionService.READ) != AccessStatus.ALLOWED) {
                    throw new AccessDeniedException("Access denied. You do not have the appropriate permissions to perform this operation");
                }
                String outputType = request.getParameter("outputType");
                String srcType = request.getParameter("srcType");
                try {
                    NodeRef convertedNode = convertNode(nodeRef, outputType, srcType, downloadsContainerNodeRef);
                    if (convertedNode != null) {
                        responseJson.put("downloadUrl", util.getContentUrl(convertedNode));
                    } else {
                        throw new WebScriptException("ConvertNodeRef is null.");
                    }
                    response.getWriter().write(responseJson.toString(3));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new WebScriptException("Unable to convert: " + e.getMessage());
                }
            } else {
                throw new WebScriptException("Output type is null");
            }
        } else {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not find required 'nodeRef' parameter or request body");
        }
    }

    private String createZipWithMultipleFiles(NodeRef downloadsContainerNodeRef, JSONArray nodeRefToConvert) {
        Map<QName, Serializable> props = new HashMap<>(1);
        props.put(ContentModel.PROP_NAME, "content.zip");
        NodeRef zipContentNodeRef = nodeService.createNode(downloadsContainerNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "content.zip"),
                ContentModel.TYPE_CONTENT, props).getChildRef();

        try {
            File zip = TempFileProvider.createTempFile(TEMP_FILE_PREFIX, ZIP_EXTENSION);
            FileOutputStream stream = new FileOutputStream(zip);
            CheckedOutputStream checksum = new CheckedOutputStream(stream, new Adler32());
            BufferedOutputStream buff = new BufferedOutputStream(checksum);
            ZipArchiveOutputStream out = new ZipArchiveOutputStream(buff);
            out.setEncoding("UTF-8");
            out.setMethod(ZipArchiveOutputStream.DEFLATED);
            out.setLevel(Deflater.BEST_COMPRESSION);

            for (int i=0; i < nodeRefToConvert.size(); i++) {
                org.json.simple.JSONObject object = (org.json.simple.JSONObject) nodeRefToConvert.get(i);
                NodeRef nodeToConvert = new NodeRef(object.get("nodeRef").toString());
                if (permissionService.hasPermission(nodeToConvert, PermissionService.READ) != AccessStatus.ALLOWED) {
                    throw new AccessDeniedException("Access denied. You do not have the appropriate permissions to perform this operation. NodeRef= " + nodeToConvert.toString());
                }
                String srcType = object.get("srcType").toString();
                String outputType = object.get("outputType").toString();
                NodeRef convertedNode = nodeToConvert;
                if (!srcType.equals(outputType)) {
                    convertedNode = convertNode(nodeToConvert, outputType,
                            srcType, downloadsContainerNodeRef);
                }
                ContentReader reader = contentService.getReader(convertedNode, ContentModel.PROP_CONTENT);
                if (reader != null) {
                    InputStream is = reader.getContentInputStream();
                    ZipArchiveEntry zipEntry = new ZipArchiveEntry(nodeService.getProperty(convertedNode, ContentModel.PROP_NAME).toString());
                    out.putArchiveEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = is.read(bytes)) >= 0) {
                        out.write(bytes, 0, length);
                    }
                    out.closeArchiveEntry();
                    is.close();
                }
            }
            out.close();
            buff.close();
            checksum.close();
            stream.close();
            ContentWriter writer = contentService.getWriter(zipContentNodeRef, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MIMETYPE_ZIP);
            writer.putContent(zip);
            return util.getContentUrl(zipContentNodeRef);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebScriptException("Error " + e.getMessage());
        }
    }
    private NodeRef convertNode(NodeRef nodeToConvert, String outputType, String srcType, NodeRef downloadsContainerNodeRef) {
        try {
            InputStream in;
            String nodeName = nodeService.getProperty(nodeToConvert, ContentModel.PROP_NAME).toString();
            String convertedNodeName = nodeName.substring(0, nodeName.lastIndexOf(".") + 1) + outputType;
            Map<QName, Serializable> props = new HashMap<>(1);
            props.put(ContentModel.PROP_NAME, convertedNodeName);
            String downloadUrl = converterService.convert(util.getKey(nodeToConvert), srcType, outputType, util.getContentUrl(nodeToConvert));
            NodeRef convertedNode = nodeService.createNode(downloadsContainerNodeRef, ContentModel.ASSOC_CHILDREN,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, convertedNodeName), ContentModel.TYPE_CONTENT, props).getChildRef();
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();
            ContentWriter writer = contentService.getWriter(convertedNode, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(mimetypeService.getMimetype(outputType));
            writer.putContent(in);
            return convertedNode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
