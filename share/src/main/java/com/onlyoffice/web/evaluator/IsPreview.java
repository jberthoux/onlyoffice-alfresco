/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import com.google.gson.Gson;
import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;
import org.springframework.extensions.webscripts.DefaultURLHelper;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Response;

import java.io.Serializable;
import java.util.Map;

public class IsPreview implements ExtensionModuleEvaluator {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ScriptRemote remote;

    public void setRemote(ScriptRemote remote) {
        this.remote = remote;
    }

    @Override
    public boolean applyModule(RequestContext requestContext, Map<String, String> map) {
        String pageId = requestContext.getPageId();
        Map<String, Object> model = requestContext.getModel();
        if (pageId != null && model != null && pageId.equals("document-details"))
        {
            DefaultURLHelper url = (DefaultURLHelper) model.get("url");
            if (url != null && url.getArgs() != null) {
                String nodeRef = url.getArgs().get("nodeRef");
                if (nodeRef != null && !nodeRef.isEmpty()) {
                    Response response = remote.call("/parashift/onlyoffice/preview?nodeRef=" + nodeRef);
                    if (response.getStatus().getCode() == Status.STATUS_OK) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(response.getResponse());
                            Boolean preview = (Boolean) json.get("preview");
                            if (preview) return true;
                        } catch (Exception err) {
                            logger.error("Failed to parse response from Alfresco: ", err);
                        }
                    } else {
                        logger.error("Unable to retrieve preview information from Alfresco: " + response.getStatus().getCode());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String[] getRequiredProperties() { return new String[0]; }
}
