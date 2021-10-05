/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import com.onlyoffice.web.scripts.OnlyofficeSettingsQuery;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IsConvertible extends BaseEvaluator {
    private OnlyofficeSettingsQuery onlyofficeSettings;

    public void setOnlyofficeSettings(OnlyofficeSettingsQuery onlyofficeSettings) {
        this.onlyofficeSettings = onlyofficeSettings;
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try
        {
            logger.error(jsonObject.toString());
            return hasPermission(jsonObject) && isConvertibleFormat(jsonObject);
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }
    }

    private boolean hasPermission (JSONObject jsonObject) {
        if (onlyofficeSettings.getConvertOriginal()) {
            JSONObject node = (JSONObject)jsonObject.get("node");
            if (node == null) {
                return false;
            }
            else {
                JSONObject perm = (JSONObject)node.get("permissions");
                JSONObject user = (JSONObject)perm.get("user");
                return (boolean) user.getOrDefault("Write", false);
            }
        } else {
            JSONObject parent = (JSONObject)jsonObject.get("parent");
            if (parent == null) {
                return false;
            }
            else {
                JSONObject perm = (JSONObject)parent.get("permissions");
                JSONObject user = (JSONObject)perm.get("user");
                return (boolean) user.getOrDefault("CreateChildren", false);
            }
        }
    }


    private boolean isConvertibleFormat(JSONObject jsonObject) {
        String fileName = (String) jsonObject.get("fileName");
        String docExt = fileName.substring(fileName.lastIndexOf(".") + 1).trim().toLowerCase();

        JSONArray supportedFormats = onlyofficeSettings.getSupportedFormats();

        for (int i = 0; i < supportedFormats.size(); i++) {
            JSONObject format = (JSONObject) supportedFormats.get(i);
            JSONArray outputTypes = (JSONArray) format.get("convertTo");

            if (format.get("name").equals(docExt)) {
                switch (format.get("type").toString()) {
                    case "WORD":
                        if (outputTypes.contains("docx")) return true;
                        break;
                    case "CELL":
                        if (outputTypes.contains("xlsx")) return true;
                        break;
                    case "SLIDE":
                        if (outputTypes.contains("pptx")) return true;
                        break;
                    default:
                        break;
                }
            }
        }

        return false;
    }
}