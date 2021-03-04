package com.parashift.onlyoffice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/*
    Copyright (c) Ascensio System SIA 2020. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.editablemimetypes.get")
public class EditableMimetypes extends AbstractWebScript {

    @Autowired
    ConfigManager configManager;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        JSONObject responseJson = new JSONObject();
        try {
            Set<String> editableMimetypes = configManager.getEditableSet();
            responseJson.put("mimetypes", editableMimetypes);

            response.setContentType("application/json; charset=utf-8");
            response.setContentEncoding("UTF-8");
            response.getWriter().write(responseJson.toString());
        } catch (JSONException e) {
            throw new WebScriptException("Unable to serialize JSON: " + e.getMessage());
        }
    }
}

