package com.parashift.onlyoffice.scripts;

import com.parashift.onlyoffice.JwtManager;
import com.parashift.onlyoffice.Util;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.editor-api.post")
public class EditorApi extends AbstractWebScript {

    @Autowired
    NodeService nodeService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    ContentService contentService;

    @Autowired
    MimetypeService mimetypeService;

    @Autowired
    Util util;

    @Autowired
    JwtManager jwtManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        Map<String, String> templateVars = request.getServiceMatch().getTemplateVars();
        String type = templateVars.get("type");
        switch (type.toLowerCase()) {
            case "insert":
                insert(request, response);
                break;
            case "save-as":
                saveAs(request, response);
                break;
            default:
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "API Not Found");
        }
    }

    private void insert(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try {
            JSONObject requestData = new JSONObject(request.getContent().getContent());
            JSONArray nodes = requestData.getJSONArray("nodes");
            List<Object> responseJson = new ArrayList<>();

            for (int i = 0; i < nodes.length(); i++) {
                JSONObject data = new JSONObject();

                NodeRef node = new NodeRef(nodes.getString(i));

                if (permissionService.hasPermission(node, PermissionService.READ) == AccessStatus.ALLOWED) {
                    Map<QName, Serializable> properties = nodeService.getProperties(node);
                    String docTitle = (String) properties.get(ContentModel.PROP_NAME);
                    String fileType = docTitle.substring(docTitle.lastIndexOf(".") + 1).trim().toLowerCase();

                    if (requestData.has("command")) {
                        data.put("c", requestData.get("command"));
                    }
                    data.put("fileType", fileType);
                    data.put("url", util.getContentUrl(node));
                    if (jwtManager.jwtEnabled()) {
                        try {
                            data.put("token", jwtManager.createToken(data));
                        } catch (Exception e) {
                            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Token creation error", e);
                        }
                    }

                    responseJson.add(data);
                }
            }

            response.setContentType("application/json");
            response.getWriter().write(responseJson.toString());
        } catch (JSONException e) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request", e);
        }
    }

    private void saveAs(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try {
            JSONObject requestData = new JSONObject(request.getContent().getContent());

            String title = requestData.getString("title");
            String ext = requestData.getString("ext");
            String url = requestData.getString("url");
            String saveNode = requestData.getString("saveNode");

            if (title.isEmpty() || ext.isEmpty() || url.isEmpty() || saveNode.isEmpty()) {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Required query parameters not found");
            }

            NodeRef folderNode = new NodeRef(saveNode);

            if (permissionService.hasPermission(folderNode, PermissionService.CREATE_CHILDREN) != AccessStatus.ALLOWED) {
                throw new WebScriptException(Status.STATUS_FORBIDDEN, "User don't have the permissions to create child node");
            }

            String fileName = util.getCorrectName(folderNode, title, ext);

            NodeRef nodeRef = nodeService.createNode(
                    folderNode,
                    ContentModel.ASSOC_CONTAINS,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, fileName),
                    ContentModel.TYPE_CONTENT,
                    Collections.<QName, Serializable> singletonMap(ContentModel.PROP_NAME, fileName)).getChildRef();

            ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(mimetypeService.getMimetype(ext));

            url = util.replaceDocEditorURLToInternal(url);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            try (InputStream in = connection.getInputStream()) {
                writer.putContent(in);
                util.ensureVersioningEnabled(nodeRef);
            } finally {
                connection.disconnect();
            }
        } catch (JSONException e) {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request", e);
        }
    }
}

