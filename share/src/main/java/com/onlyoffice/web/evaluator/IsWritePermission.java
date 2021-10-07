/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

public class IsWritePermission extends BaseEvaluator {

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try
        {
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
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator", err);
        }
    }

}