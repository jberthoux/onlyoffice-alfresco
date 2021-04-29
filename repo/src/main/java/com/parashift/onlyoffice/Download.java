package com.parashift.onlyoffice;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.*;
import org.springframework.stereotype.Component;
import org.springframework.extensions.surf.util.URLEncoder;

import java.io.*;
import java.util.Map;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.download.get")
public class Download extends AbstractWebScript {

    @Autowired
    ContentService contentService;

    @Autowired
    NodeService nodeService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    JwtManager jwtManager;

    @Autowired
    ConfigManager configManager;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        if (request.getParameter("nodeRef") != null) {

            if (jwtManager.jwtEnabled()) {
                String jwth = (String) configManager.getOrDefault("jwtheader", "");
                String header = request.getHeader(jwth.isEmpty() ? "Authorization" : jwth);
                String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : header;

                if (token == null || token == "") {
                    throw new SecurityException("Expected JWT");
                }

                if (!jwtManager.verify(token)) {
                    throw new SecurityException("JWT verification failed");
                }
            }

            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));

            if (permissionService.hasPermission(nodeRef, PermissionService.READ) != AccessStatus.ALLOWED) {
                throw new AccessDeniedException("Access denied. You do not have the appropriate permissions to perform this operation");
            }

            ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);

            response.setHeader("Content-Length", String.valueOf(reader.getSize()));
            response.setHeader("Content-Type", reader.getMimetype());
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8\'\'" + URLEncoder.encode((String) properties.get(ContentModel.PROP_NAME)));

            reader.getContent(response.getOutputStream());
        } else {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not find required 'nodeRef' parameter");
        }
    }
}
