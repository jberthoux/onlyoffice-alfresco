/*
   Copyright (c) Ascensio System SIA 2020. All rights reserved.
   http://www.onlyoffice.com
*/

package com.onlyoffice.web.evaluator;

import com.onlyoffice.web.scripts.EditableMimetypesQuery;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class IsViewMimetype extends BaseEvaluator {
    private EditableMimetypesQuery editableMimetypes;

    private static Set<String> baseViewMimetypes = new HashSet<String>() {{
        add("application/pdf");
        add("application/vnd.ms-excel");
        add("application/vnd.ms-powerpoint");
        add("application/msword");
        add("application/rtf");
        add("application/x-rtf");
        add("text/richtext");
        add("application/vnd.oasis.opendocument.spreadsheet");
        add("application/vnd.oasis.opendocument.presentation");
        add("application/vnd.oasis.opendocument.text");
        add("text/csv");
        add("text/plain");
    }};

    public void setEditableMimetypes(EditableMimetypesQuery editableMimetypes) {
        this.editableMimetypes = editableMimetypes;
    }

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
                String mimetype = (String)node.get("mimetype");

                Set<String> viewMimetypes = new HashSet<String>();
                viewMimetypes.addAll(baseViewMimetypes);
                viewMimetypes.removeAll(editableMimetypes.requestMimetypesFromRepo());
                if (mimetype == null || !viewMimetypes.contains(mimetype))
                {
                    return false;
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
