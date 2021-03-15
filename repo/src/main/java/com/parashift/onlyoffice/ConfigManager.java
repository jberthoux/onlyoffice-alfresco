package com.parashift.onlyoffice;

import org.alfresco.service.cmr.attributes.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
}

