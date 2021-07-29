package com.parashift.onlyoffice;


import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.favourites.FavouritesService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/
@Component(value = "webscript.onlyoffice.favourite.post")
public class Favourite  extends AbstractWebScript {

    @Autowired
    FavouritesService favouritesService;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        if(request.getParameter("nodeRef") != null){
            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));
            String username = AuthenticationUtil.getFullyAuthenticatedUser();
            if(favouritesService.isFavourite(username, nodeRef)){
                favouritesService.removeFavourite(username, nodeRef);
            } else {
                favouritesService.addFavourite(username, nodeRef);
            }
        }
    }
}
