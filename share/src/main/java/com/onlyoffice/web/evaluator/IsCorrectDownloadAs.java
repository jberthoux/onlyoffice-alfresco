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

public class IsCorrectDownloadAs extends BaseEvaluator {

    private static Set<String> textTypes = new HashSet<String>() {{
        add("doc");
        add("docm");
        add("docx");
        add("dot");
        add("dotm");
        add("dotx");
        add("epub");
        add("fb2");
        add("fodt");
        add("html");
        add("mht");
        add("odt");
        add("ott");
        add("pdf");
        add("rtf");
        add("txt");
        add("xps");
        add("xml");
    }};
    private static Set<String> cellTypes = new HashSet<String>() {{
        add("csv");
        add("fods");
        add("ods");
        add("ots");
        add("xls");
        add("xlsm");
        add("xlsx");
        add("xlt");
        add("xltm");
        add("xltx");
    }};
    private static Set<String> slideTypes = new HashSet<String>() {{
        add("fodp");
        add("odp");
        add("otp");
        add("pot");
        add("potm");
        add("potx");
        add("pps");
        add("ppsm");
        add("ppsx");
        add("ppt");
        add("pptm");
        add("pptx");
    }};

    @Override
    public boolean evaluate(JSONObject jsonObject) {
        try {
            String docName = jsonObject.get("displayName").toString();
            String docExt = docName.substring(docName.lastIndexOf(".") + 1);
            if (slideTypes.contains(docExt) || textTypes.contains(docExt) || cellTypes.contains(docExt)) {
                return true;
            }
            return false;
        } catch (Exception err) {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }
    }

}