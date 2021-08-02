package com.parashift.onlyoffice;

 /*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "webscript.onlyoffice.mentions.post")
public class Mentions extends AbstractWebScript {
    @Autowired
    ActionService actionService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    NodeService nodeService;

    @Autowired
    PersonService personService;

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        if(request.getParameter("nodeRef") !=null ){
            NodeRef nodeRef = new NodeRef(request.getParameter("nodeRef"));
            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(request.getContent().getContent());
                String link = (String) json.get("link");
                List<String> emails = (List<String>) json.get("emails");
                JSONArray responseJson = new JSONArray();
                for(String email : emails) {
                    Map<String, Serializable> aParams = new HashMap<>();
                    aParams.put(MailActionExecuter.PARAM_TO, email);
                    aParams.put(MailActionExecuter.PARAM_SUBJECT, "You were mentioned in an Alfresco comment.");
                    aParams.put(MailActionExecuter.PARAM_TEXT, "Link to the comment in the document: " + link);
                    Action action = actionService.createAction(MailActionExecuter.NAME);
                    action.setParameterValues(aParams);
                    action.setExecuteAsynchronously(true);
                    actionService.executeAction(action, nodeRef);
                    NodeRef peopleStore = personService.getPeopleContainer();
                    for(ChildAssociationRef assoc : nodeService.getChildAssocs(peopleStore)){
                        NodeRef person = assoc.getChildRef();
                        if(person !=null && nodeService.getProperty(person, ContentModel.PROP_EMAIL).toString().equals(email)){
                            PersonService.PersonInfo personInfo = personService.getPerson(person);
                            if(personInfo != null){
                                permissionService.setPermission(nodeRef, personInfo.getUserName(), PermissionService.CONSUMER, true);
                                responseJson.put(personInfo.getFirstName() + " " + personInfo.getLastName());
                            }
                        }
                    }
                }
                response.setContentType("application/json; charset=utf-8");
                response.setContentEncoding("UTF-8");
                response.getWriter().write(responseJson.toString(3));
            } catch (ParseException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
