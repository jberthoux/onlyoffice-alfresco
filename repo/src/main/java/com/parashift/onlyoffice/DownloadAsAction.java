package com.parashift.onlyoffice;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class DownloadAsAction extends ActionExecuterAbstractBase {

    public static final String PARAM_DOWNLOAD_OUTPUT_TYPE_NAME = "select_type";

    @Autowired
    Converter converterService;

    @Autowired
    NodeService nodeService;

    @Autowired
    Util util;

    @Autowired
    PermissionService permissionService;

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        if (action.getParameterValue(PARAM_DOWNLOAD_OUTPUT_TYPE_NAME) != null) {
            if (permissionService.hasPermission(actionedUponNodeRef, PermissionService.READ) != AccessStatus.ALLOWED) {
                throw new AccessDeniedException("Access denied. You do not have the appropriate permissions to perform this operation");
            }
            String outputType = (String) action.getParameterValue(PARAM_DOWNLOAD_OUTPUT_TYPE_NAME);
            String srcType = nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME).toString();
            srcType = srcType.substring(srcType.lastIndexOf(".") + 1).trim().toLowerCase();
            try {
                converterService.convert(util.getKey(actionedUponNodeRef), srcType, outputType, util.getContentUrl(actionedUponNodeRef));
            } catch (Exception e) {
                e.printStackTrace();
                throw new WebScriptException("Unable to convert: " + e.getMessage());
            }
        } else {
            throw new WebScriptException("Output type is null");
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        for (String s : new String[]{PARAM_DOWNLOAD_OUTPUT_TYPE_NAME}) {
            paramList.add(new ParameterDefinitionImpl(s, DataTypeDefinition.TEXT, false, getParamDisplayLabel(s)) {
            });
        }
    }
}

