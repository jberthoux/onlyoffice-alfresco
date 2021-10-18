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
        try {
            if (onlyofficeSettings.getConvertOriginal()) {
                JSONObject node = (JSONObject)jsonObject.get("node");
                if (node != null && node.containsKey("permissions")) {
                    JSONObject perm = (JSONObject)node.get("permissions");
                    if (perm != null && perm.containsKey("user")) {
                        JSONObject user = (JSONObject)perm.get("user");
                        if (user != null && (boolean)user.getOrDefault("Write", false)) {
                            return true;
                        }
                    }
                }
            } else {
                JSONObject parent = (JSONObject)jsonObject.get("parent");
                if (parent != null && parent.containsKey("permissions")) {
                    JSONObject perm = (JSONObject)parent.get("permissions");
                    if (perm != null && perm.containsKey("user")) {
                        JSONObject user = (JSONObject)perm.get("user");
                        if (user != null && (boolean)user.getOrDefault("CreateChildren", false)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } catch (Exception err) {
            throw new AlfrescoRuntimeException("Failed to run action evaluator", err);
        }
    }
}