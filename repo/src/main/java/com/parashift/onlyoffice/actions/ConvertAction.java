package com.parashift.onlyoffice.actions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parashift.onlyoffice.util.ConfigManager;
import com.parashift.onlyoffice.util.ConvertManager;
import com.parashift.onlyoffice.util.Util;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
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

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

public class ConvertAction extends ActionExecuterAbstractBase {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConvertManager converterService;

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

    @Autowired
    ConfigManager configManager;

    @Autowired
    CheckOutCheckInService checkOutCheckInService;

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
        if (nodeService.exists(actionedUponNodeRef)) {
            if (permissionService.hasPermission(actionedUponNodeRef, PermissionService.READ) == AccessStatus.ALLOWED) {
                if (!checkOutCheckInService.isCheckedOut(actionedUponNodeRef) &&
                        !checkOutCheckInService.isWorkingCopy(actionedUponNodeRef)) {
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
                        logger.debug("Couldn't find parent folder");
                        return;
                    }

                    NodeRef nodeFolder = parentAssoc.getParentRef();
                    String nodeName = (String) nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
                    String title = nodeName.substring(0, nodeName.lastIndexOf('.'));
                    String ext = mimetypeService.getExtension(targetMimeParam);

                    String newName = util.getCorrectName(nodeFolder, title, ext);

                    NodeRef writeNode = null;
                    Boolean deleteNode = false;
                    Boolean checkoutNode = false;

                    if (configManager.getAsBoolean("convertOriginal", "false")) {
                        logger.debug("Updating node");
                        if (permissionService.hasPermission(actionedUponNodeRef, PermissionService.WRITE) == AccessStatus.ALLOWED) {
                            util.ensureVersioningEnabled(actionedUponNodeRef);
                            checkoutNode = true;
                            writeNode = checkOutCheckInService.checkout(actionedUponNodeRef);
                            nodeService.setProperty(writeNode, ContentModel.PROP_NAME, newName);
                        } else {
                            throw new SecurityException("User don't have the permissions to update this node");
                        }
                    } else {
                        logger.debug("Creating new node");
                        if (permissionService.hasPermission(nodeFolder, PermissionService.CREATE_CHILDREN) == AccessStatus.ALLOWED) {
                            Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
                            props.put(ContentModel.PROP_NAME, newName);
                            deleteNode = true;
                            writeNode = this.nodeService.createNode(nodeFolder, ContentModel.ASSOC_CONTAINS,
                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, newName), ContentModel.TYPE_CONTENT, props)
                                    .getChildRef();

                            util.ensureVersioningEnabled(writeNode);
                        } else {
                            throw new SecurityException("User don't have the permissions to create child node");
                        }
                    }

                    ContentWriter writer = null;

                    try {
                        logger.debug("Invoking .transform()");
                        writer = this.contentService.getWriter(writeNode, ContentModel.PROP_CONTENT, true);
                        writer.setMimetype(targetMimeParam);
                        converterService.transform(reader, writer, new TransformationOptions(actionedUponNodeRef, null, writeNode, null));
                        if (checkoutNode) {
                            logger.debug("Checking in node");
                            checkOutCheckInService.checkin(writeNode, null);
                        }
                    } catch (Exception ex) {
                        if (!writer.isClosed()) {
                            try {
                                writer.getContentOutputStream().close();
                            } catch (Exception e) {
                                logger.error("Error close stream", e);
                            }
                        }

                        if (deleteNode && nodeService.exists(writeNode)) {
                            logger.debug("Deleting created node");
                            nodeService.deleteNode(writeNode);
                        }

                        throw new AlfrescoRuntimeException("Conversion failed", ex);
                    } finally {
                        if (!reader.isClosed()) {
                            try {
                                reader.getContentInputStream().close();
                            } catch (Exception e) {
                                logger.error("Error close stream", e);
                            }
                        }

                        if (nodeService.exists(writeNode) && checkOutCheckInService.isCheckedOut(writeNode)) {
                            logger.debug("Finally: cancelCheckout()");
                            checkOutCheckInService.cancelCheckout(writeNode);
                        }
                    }
                } else {
                    throw new AlfrescoRuntimeException("The node is already locked");
                }
            } else {
                throw new SecurityException("User don't have the permissions to read this node");
            }
        }
    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) { }
}

