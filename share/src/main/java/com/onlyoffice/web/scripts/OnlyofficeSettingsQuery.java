/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.scripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Response;
import java.util.HashSet;
import java.util.Set;

public class OnlyofficeSettingsQuery {
    private static Set<String> editableMimetypes = new HashSet<String>();
    private static Boolean convertOriginal = false;
    private static long timeLastRequest = 0;
    private ScriptRemote remote;

    public void setRemote(ScriptRemote remote) {
        this.remote = remote;
    }

    private void requestOnlyofficeSettingsFromRepo() {
        if ((System.nanoTime() - timeLastRequest)/1000000000 > 10) {
            Set<String> editableMimetypes = new HashSet<>();
            Response response = remote.call("/parashift/onlyoffice/onlyoffice-settings");
            if (response.getStatus().getCode() == Status.STATUS_OK) {
                timeLastRequest = System.nanoTime();
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response.getResponse());

                    JSONArray mimetypes = (JSONArray) json.get("editableMimetypes");
                    for (Object mimetype : mimetypes) {
                        editableMimetypes.add((String) mimetype);
                    }
                    this.editableMimetypes = editableMimetypes;
                    this.convertOriginal = (Boolean) json.get("convertOriginal");
                } catch (Exception err) {
                    throw new AlfrescoRuntimeException("Failed to parse response from Alfresco: " + err.getMessage());
                }
            }
            else
            {
                throw new AlfrescoRuntimeException("Unable to retrieve editable mimetypes information from Alfresco: " + response.getStatus().getCode());
            }
        }
    }

    public Set<String> getEditableMimetypes() {
        requestOnlyofficeSettingsFromRepo();
        return editableMimetypes;
    }

    public Boolean getConvertOriginal() {
        requestOnlyofficeSettingsFromRepo();
        return convertOriginal;
    }
}
