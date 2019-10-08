package com.parashift.onlyoffice;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.UrlUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;

/*
   Copyright (c) Ascensio System SIA 2019. All rights reserved.
   http://www.onlyoffice.com
*/

@Service
public class Util {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    SysAdminParams sysAdminParams;

    @Autowired
    NodeService nodeService;

    @Autowired
    VersionService versionService;

    public static final QName EditingHashAspect = QName.createQName("onlyoffice:editing-hash");

    public String getKey(NodeRef nodeRef) {
        Version v = versionService.getCurrentVersion(nodeRef);
        return nodeRef.getId() + "_" + v.getVersionLabel();
    }

    public String getContentUrl(NodeRef nodeRef) {
        return  UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/api/node/content/workspace/SpacesStore/" + nodeRef.getId() + "?alf_ticket=" + authenticationService.getCurrentTicket();
    }

    public String getCallbackUrl(NodeRef nodeRef) {
        String hash = null;
        hash = (String) nodeService.getProperty(nodeRef, EditingHashAspect);

        if (hash == null) {
            hash = getHash();
            nodeService.setProperty(nodeRef, EditingHashAspect, hash);
        }

        return UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/parashift/onlyoffice/callback?nodeRef=" + nodeRef.toString() + "&cb_key=" + hash;
    }

    public String getConversionUrl(String key) {
        return UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/parashift/onlyoffice/converter?key=" + key + "&alf_ticket=" + authenticationService.getCurrentTicket();
    }

    public String getTestConversionUrl() {
        return UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/parashift/onlyoffice/convertertest?alf_ticket=" + authenticationService.getCurrentTicket();
    }

    private String getHash() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
