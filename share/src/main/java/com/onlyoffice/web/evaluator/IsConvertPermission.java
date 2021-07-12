/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import com.onlyoffice.web.scripts.OnlyofficeSettingsQuery;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

public class IsConvertPermission extends BaseEvaluator {
    private OnlyofficeSettingsQuery onlyofficeSettings;

    public void setOnlyofficeSettings(OnlyofficeSettingsQuery onlyofficeSettings) {
        this.onlyofficeSettings = onlyofficeSettings;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try
        {
            if (onlyofficeSettings.getConvertOriginal()) {
                JSONObject node = (JSONObject)jsonObject.get("node");
                if (node == null)
                {
                    return false;
                }
                else
                {
                    JSONObject perm = (JSONObject)node.get("permissions");
                    JSONObject user = (JSONObject)perm.get("user");
                    return (boolean)user.getOrDefault("Write", false);
                }
            } else {
                JSONObject parent = (JSONObject)jsonObject.get("parent");
                if (parent == null)
                {
                    return false;
                }
                else
                {
                    JSONObject perm = (JSONObject)parent.get("permissions");
                    JSONObject user = (JSONObject)perm.get("user");
                    return (boolean)user.getOrDefault("CreateChildren", false);
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }
    }

}