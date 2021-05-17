package com.parashift.onlyoffice;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.preview.get")
public class Preview extends AbstractWebScript {

    @Autowired
    ConfigManager configManager;

    @Autowired
    NodeService nodeService;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        if (request.getParameter("nodeRef") != null) {
            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);

            String docTitle = (String) properties.get(ContentModel.PROP_NAME);
            String docExt = docTitle.substring(docTitle.lastIndexOf(".") + 1).trim().toLowerCase();

            Boolean supportedType = configManager.getDocType(docExt) != null;
            Boolean preview = configManager.getAsBoolean("webpreview", "false");

            JSONObject responseJson = new JSONObject();
            try {
                responseJson.put("preview", preview && supportedType);
                response.setContentType("application/json; charset=utf-8");
                response.setContentEncoding("UTF-8");

                response.getWriter().write(responseJson.toString());
            } catch (JSONException e) {
                throw new WebScriptException("Unable to serialize JSON: " + e.getMessage());
            }
        } else {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not find required 'nodeRef' parameter");
        }
    }
}

