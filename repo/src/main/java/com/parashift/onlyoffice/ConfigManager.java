package com.parashift.onlyoffice;

import org.alfresco.service.cmr.attributes.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Service
public class ConfigManager {

    @Autowired
    AttributeService attributeService;

    @Autowired
    @Qualifier("global-properties")
    Properties globalProp;

    private static Map<String, String> demoData = new HashMap<String, String>(){{
        put("url", "https://onlinedocs.onlyoffice.com/");
        put("header", "AuthorizationJWT");
        put("secret", "sn2puSUF7muF5Jas");
        put("trial", "30");
    }};

    public void set(String key, String value) {
        attributeService.setAttribute(value == null || value.isEmpty() ? null : value, formKey(key));
    }

    public Object get(String key) {
        String formedKey = formKey(key);
        Object value = attributeService.getAttribute(formedKey);

        if (value == null) {
            value = globalProp.get(formedKey);
        }

        return value;
    }

    public Object getOrDefault(String key, Object defaultValue) {
        String formedKey = formKey(key);
        Object value = attributeService.getAttribute(formedKey);

        if (value == null) {
            value = globalProp.getOrDefault(formedKey, defaultValue);
        }

        return value;
    }

    public Boolean getAsBoolean(String key, Object defaultValue) {
        String formedKey = formKey(key);
        Object value = attributeService.getAttribute(formedKey);

        if (value == null) {
            value = globalProp.getOrDefault(formedKey, defaultValue);
        }

        return (value != null && ((String)value).equals("true")) ? true : false;
    }

    public Set<String> getEditableSet() {
        Set<String> editableSet = new HashSet<>();
        if (getAsBoolean("formatODT", "false")){
            editableSet.add("application/vnd.oasis.opendocument.text");
        }
        if (getAsBoolean("formatODS", "false")){
            editableSet.add("application/vnd.oasis.opendocument.spreadsheet");
        }
        if (getAsBoolean("formatODP", "false")){
            editableSet.add("application/vnd.oasis.opendocument.presentation");
        }
        if (getAsBoolean("formatCSV", "true")){
            editableSet.add("text/csv");
        }
        if (getAsBoolean("formatTXT", "true")){
            editableSet.add("text/plain");
        }
        if (getAsBoolean("formatRTF", "false")){
            editableSet.add("application/rtf");
            editableSet.add("application/x-rtf");
            editableSet.add("text/richtext");
        }
        return editableSet;
    }

    private String formKey(String key) {
        return "onlyoffice." + key;
    }

    public boolean selectDemo(Boolean demo) {
        set("demo", demo.toString());
        if (demo) {
            String demoStart = (String) getOrDefault("demoStart", null);
            if (demoStart == null || demoStart.isEmpty()) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                set("demoStart", dateFormat.format(date));
            }
            return true;
        }
        return false;
    }

    public Boolean demoEnabled() {
        return getAsBoolean("demo", "false");
    }

    public Boolean demoAvailable(Boolean forActivate) {
        String demoStart = (String) get("demoStart");
        if (demoStart != null && !demoStart.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                Calendar date = Calendar.getInstance();
                date.setTime(dateFormat.parse(demoStart));
                date.add(Calendar.DATE, Integer.parseInt(demoData.get("trial")));
                if (date.after(Calendar.getInstance())) {
                    return true;
                } else {
                    return false;
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return forActivate;
    }

    public Boolean demoActive() {
        return demoEnabled() && demoAvailable(false);
    }

    public String getDemo(String key) {
        return demoData.get(key);
    }
}

