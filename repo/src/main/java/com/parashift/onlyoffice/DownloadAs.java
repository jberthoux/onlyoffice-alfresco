package com.parashift.onlyoffice;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.download.DownloadService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.download-as.get")
public class DownloadAs extends AbstractWebScript {

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

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        NodeRef downloadsNodeRef = util.getNodeByPath("/sys:system/sys:downloads");
        InputStream in;
        if (request.getParameter("nodeRef") != null) {
            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));
            if (request.getParameter("outputType") != null) {
                if (permissionService.hasPermission(nodeRef, PermissionService.READ) != AccessStatus.ALLOWED) {
                    throw new AccessDeniedException("Access denied. You do not have the appropriate permissions to perform this operation");
                }
                String outputType = request.getParameter("outputType");
                String srcType = request.getParameter("srcType");
                try {
                    String nodeName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
                    String convertedNodeName = nodeName.substring(0, nodeName.lastIndexOf(".") +1) + outputType;
                    Map<QName, Serializable> props = new HashMap<>(1);
                    props.put(ContentModel.PROP_NAME, convertedNodeName);
                    String downloadUrl = converterService.convert(util.getKey(nodeRef), srcType, outputType, util.getContentUrl(nodeRef));
                    NodeRef convertedNode = nodeService.createNode(downloadsNodeRef, ContentModel.ASSOC_CHILDREN,
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, convertedNodeName), ContentModel.TYPE_CONTENT, props).getChildRef();
                    URL url = new URL(downloadUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    in = connection.getInputStream();
                    ContentWriter writer = contentService.getWriter(convertedNode, ContentModel.PROP_CONTENT, true);
                    writer.setMimetype(mimetypeService.getMimetype(outputType));
                    writer.putContent(in);

                    JSONObject responseJson = new JSONObject();
                    responseJson.put("downloadUrl", util.getContentUrl(convertedNode));
                    response.setContentType("application/json; charset=utf-8");
                    response.setContentEncoding("UTF-8");
                    response.getWriter().write(responseJson.toString(3));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new WebScriptException("Unable to convert: " + e.getMessage());
                }
            } else {
                throw new WebScriptException("Output type is null");
            }
        } else {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not find required 'nodeRef' parameter");
        }
    }
}
