package com.parashift.onlyoffice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptException;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class ConvertAction extends ActionExecuterAbstractBase {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Converter converterService;

    @Autowired
    NodeService nodeService;

    @Autowired
    ContentService contentService;

    @Autowired
    MimetypeService mimetypeService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    Util util;

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        if (nodeService.exists(actionedUponNodeRef)) {
            if (permissionService.hasPermission(actionedUponNodeRef, PermissionService.READ) == AccessStatus.ALLOWED) {

                ContentReader reader = contentService.getReader(actionedUponNodeRef, ContentModel.PROP_CONTENT);
                String mime = reader.getMimetype();
                String targetMimeParam = converterService.GetModernMimetype(mime);
                if (targetMimeParam == null) {
                    try {
                        reader.getContentInputStream().close();
                    } catch (Exception e) {
                        logger.error("Error close stream", e);
                    }
                    logger.debug("Files of " + mime + " MIME-type cannot be converted");
                    return;
                }

                ChildAssociationRef parentAssoc = nodeService.getPrimaryParent(actionedUponNodeRef);
                if (parentAssoc == null || parentAssoc.getParentRef() == null) {
                    logger.debug("Couln't find parent folder");
                    return;
                }

                NodeRef parentRef = parentAssoc.getParentRef();

                if (permissionService.hasPermission(parentRef, PermissionService.CREATE_CHILDREN) == AccessStatus.ALLOWED) {
                    String nodeName = (String) nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
                    String baseName = nodeName.substring(0, nodeName.lastIndexOf('.'));
                    String ext = mimetypeService.getExtension(targetMimeParam);
                    String newName = baseName + "." + ext;

                    NodeRef node = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, newName);

                    Integer i = 0;
                    while (node != null) {
                        i++;
                        newName = baseName + " (" + i + ")." + ext;
                        node = nodeService.getChildByName(parentRef, ContentModel.ASSOC_CONTAINS, newName);
                    }

                    logger.debug("Converting '" + nodeName + "' -> '" + newName + "'");
                    logger.debug("Creating new node");
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
                    props.put(ContentModel.PROP_NAME, newName);

                    node = this.nodeService.createNode(parentRef, ContentModel.ASSOC_CONTAINS,
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, newName), ContentModel.TYPE_CONTENT, props)
                            .getChildRef();

                    util.ensureVersioningEnabled(node);

                    ContentWriter writer = this.contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
                    writer.setMimetype(targetMimeParam);

                    try {
                        logger.debug("Invoking .transform()");
                        converterService.transform(reader, writer, new TransformationOptions(actionedUponNodeRef, null, node, null));
                    } catch (Exception ex) {
                        if (!writer.isClosed()) {
                            try {
                                writer.getContentOutputStream().close();
                            } catch (Exception e) {
                                logger.error("Error close stream", e);
                            }
                        }

                        if (nodeService.exists(node)) {
                            logger.debug("Deleting created node");
                            nodeService.deleteNode(node);
                        }

                        throw new WebScriptException("Conversion failed", ex);
                    } finally {
                        if (!reader.isClosed()) {
                            try {
                                reader.getContentInputStream().close();
                            } catch (Exception e) {
                                logger.error("Error close stream", e);
                            }
                        }
                    }
                } else {
                    throw new SecurityException("User have no permission to create node");
                }
            } else {
                throw new SecurityException("User have no read access");
            }
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) { }
}

