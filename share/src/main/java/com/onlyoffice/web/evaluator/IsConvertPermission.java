/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

public class IsConvertPermission extends BaseEvaluator {

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try
        {
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
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }
    }

}