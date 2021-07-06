/*
   Copyright (c) Ascensio System SIA 2021. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import com.onlyoffice.web.scripts.OnlyofficeSettingsQuery;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class IsEditableMimetype extends BaseEvaluator {
    private OnlyofficeSettingsQuery onlyofficeSettings;

    private static Set<String> baseMimetypes = new HashSet<String>() {{
        add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }};

    public void setOnlyofficeSettings(OnlyofficeSettingsQuery onlyofficeSettings) {
        this.onlyofficeSettings = onlyofficeSettings;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            JSONObject node = (JSONObject)jsonObject.get("node");
            if (node == null)
            {
                return false;
            }
            else
            {
                String mimetype = (String)node.get("mimetype");
                if (mimetype == null || !baseMimetypes.contains(mimetype))
                {
                    if (!onlyofficeSettings.getEditableMimetypes().contains(mimetype)) {
                        return false;
                    }
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return true;
    }
}
