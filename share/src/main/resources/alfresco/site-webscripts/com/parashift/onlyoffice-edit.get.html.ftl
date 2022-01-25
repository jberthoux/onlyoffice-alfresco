<!--
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
-->
<#--<#import "/alfresco/templates/org/alfresco/include/alfresco-template.ftl" as common />-->
<#--<#import "/alfresco/site-webscripts/org/alfresco/components/manage-permissions/manage-permissions.get.html.ftl" as manage_permissions />-->
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>

    <title>${docTitle!} - ONLYOFFICE</title>

    <#if doRedirect>
        <script type="text/javascript">
           document.location.href = "${page.url.uri}?nodeRef=${nodeRef!}";
        </script>
    </#if>

    <link href="${url.context}/res/components/onlyoffice/onlyoffice.css" type="text/css" rel="stylesheet">

    <!--Change the address on installed ONLYOFFICE™ Online Editors-->
    <script id="scriptApi" type="text/javascript" src="${onlyofficeUrl!}OfficeWeb/apps/api/documents/api.js"></script>
    <link rel="shortcut icon" href="${url.context}/res/components/images/filetypes/${documentType!}.ico" type="image/vnd.microsoft.icon" />
    <link rel="icon" href="${url.context}/res/components/images/filetypes/${documentType!}.ico" type="image/vnd.microsoft.icon" />

    <!-- Alfresco web framework common resources -->
    <script type="text/javascript" src="${url.context}/res/js/yui-common.js"></script>
    <script type="text/javascript" src="${url.context}/noauth/messages.js?locale=${locale}"></script>
    <script type="text/javascript" src="${url.context}/res/js/bubbling.v2.1.js"></script>
    <script type="text/javascript">
        YAHOO.Bubbling.unsubscribe = function(layer, handler, scope) {
            this.bubble[layer].unsubscribe(handler, scope);
        };
    </script>
    <script type="text/javascript">
    <!-- Alfresco web framework constants -->
        Alfresco.constants = Alfresco.constants || {};
        Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context?js_string}/proxy/alfresco/";
        Alfresco.constants.PROXY_URI_RELATIVE = "${url.context?js_string}/proxy/alfresco/";
        Alfresco.constants.THEME = "${theme}";
        Alfresco.constants.USERNAME = "${(user.name!"")?js_string}";
        Alfresco.constants.SITE = "<#if page??>${(page.url.templateArgs.site!"")?url?js_string}</#if>";
        Alfresco.constants.URL_CONTEXT = "${url.context?js_string}/";
        Alfresco.constants.URL_RESCONTEXT = "${url.context?js_string}/res/";
        Alfresco.constants.URL_PAGECONTEXT = "${url.context?js_string}/page/";
        Alfresco.constants.URL_SERVICECONTEXT = "${url.context?js_string}/service/";
        Alfresco.constants.URI_TEMPLATES =
            {
                "remote-site-page": "/site/{site}/{pageid}/p/{pagename}",
                "remote-page": "/{pageid}/p/{pagename}",
                "share-site-page": "/site/{site}/{pageid}/ws/{webscript}",
                "sitedashboardpage": "/site/{site}/dashboard",
                "contextpage": "/context/{pagecontext}/{pageid}",
                "sitepage": "/site/{site}/{pageid}",
                "userdashboardpage": "/user/{userid}/dashboard",
                "userpage": "/user/{userid}/{pageid}",
                "userprofilepage": "/user/{userid}/profile",
                "userdefaultpage": "/user/{pageid}",
                "consoletoolpage": "/console/{pageid}/{toolid}",
                "consolepage": "/console/{pageid}",
                "share-page": "/{pageid}/ws/{webscript}"
            };

        Alfresco.constants.JS_LOCALE = "${locale}";
        Alfresco.constants.CSRF_POLICY = {
            enabled: ${((config.scoped["CSRFPolicy"]["filter"].getChildren("rule")?size > 0)?string)!false},
            cookie: "${config.scoped["CSRFPolicy"]["client"].getChildValue("cookie")!""}",
            header: "${config.scoped["CSRFPolicy"]["client"].getChildValue("header")!""}",
            parameter: "${config.scoped["CSRFPolicy"]["client"].getChildValue("parameter")!""}",
            properties: {}
        };
        <#if config.scoped["CSRFPolicy"]["properties"]??>
            <#assign csrfProperties = (config.scoped["CSRFPolicy"]["properties"].children)![]>
            <#list csrfProperties as csrfProperty>
        Alfresco.constants.CSRF_POLICY.properties["${csrfProperty.name?js_string}"] = "${(csrfProperty.value!"")?js_string}";
            </#list>
        </#if>

        Alfresco.constants.IFRAME_POLICY = {
            sameDomain: "${config.scoped["IFramePolicy"]["same-domain"].value!"allow"}",
            crossDomainUrls: [<#list (config.scoped["IFramePolicy"]["cross-domain"].childrenMap["url"]![]) as c>
                "${c.value?js_string}"<#if c_has_next>,</#if>
            </#list>]
        };

        Alfresco.constants.HIDDEN_PICKER_VIEW_MODES = [
            <#list config.scoped["DocumentLibrary"]["hidden-picker-view-modes"].children as viewMode>
                <#if viewMode.name?js_string == "mode">"${viewMode.value?js_string}"<#if viewMode_has_next>,</#if></#if>
            </#list>
        ];
    </script>
    <script type="text/javascript" src="${url.context}/res/js/alfresco.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/document-picker/document-picker.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/object-finder/object-finder.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/common/common-component-style-filter-chain.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/documentlibrary/tree.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/documentlibrary/global-folder.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/documentlibrary/copy-move-to.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/documentlibrary/permissions.js"></script>
    <script type="text/javascript" src="${url.context}/res/templates/manage-permissions/template.manage-permissions.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/manage-permissions/manage-permissions.js"></script>
    <script type="text/javascript"  src="${url.context}/res/modules/roles-tooltip.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/people-finder/authority-finder.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/manage-permissions/manage-permissions.js"></script>

    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-fonts-grids.css" />
    <#if theme = 'default'>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" />
    <#else>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" />
    </#if>
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/manage-permissions/manage-permissions.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/documentlibrary/global-folder.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/tree.css">
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-picker/document-picker.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/object-finder/object-finder.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/roles-tooltip.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/people-finder/authority-finder.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/manage-permissions/manage-permissions.css" />
</head>

<body id="Share" class="yui-skin-${theme} alfresco-share claro">

<div id="manage-permissions" style="
    margin: auto;
    top: calc( 30% - 160px);
    left: calc(50% - 376px);
    position: absolute;
    z-index:3;
    background-color:white;
    border-radius: 5px;
    ">
  <#assign id="doc-manage-permissions">
  <div id="${id}-body" class="permissions">
      <div id="${id}-managepermissions"></div>
  </div>
</div>

<div id="popup"></div>

    <div id="placeholder"></div>
    <script>
        var documentPicker = new Alfresco.module.DocumentPicker("onlyoffice-editor-docPicker", Alfresco.ObjectRenderer);
        documentPicker.setOptions({
           displayMode: "items",
           itemFamily: "node",
           itemType: "cm:content",
           multipleSelectMode: false,
           parentNodeRef: "${folderNode!}",
           restrictParentNavigationToDocLib: true
        });
        documentPicker.onComponentsLoaded(); // Need to force the component loaded call to ensure setup gets completed.

        YAHOO.Bubbling.on("onDocumentsSelected", function(eventName, payload) {
            if (payload && payload[1].items) {
                var items = [];

                for (var i = 0; i < payload[1].items.length; i++) {
                    items.push(payload[1].items[i].nodeRef);
                }

                if (items.length > 0) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "parashift/onlyoffice/editor-api/insert",
                        dataObj: {
                             command: documentPicker.docEditorCommand,
                             nodes: items
                        },
                        successCallback: {
                            fn: function(response) {
                                documentPicker.docEditorEvent(response.json[0]);
                            },
                            scope: this
                        }
                    });
                }
            }
        });

        var copyMoveTo = new Alfresco.module.DoclibCopyMoveTo("onlyoffice-editor-copyMoveTo");
        copyMoveTo.setOptions({
            mode: "move",
            siteId: Alfresco.constants.SITE,
            path: "/",
            files: {
                "node": {}
            },
            parentId: "${folderNode!}",
            title: "${msg("onlyoffice.editor.dialog.save-as.title")}",
            zIndex: 1000
        });

        var onAppReady = function (event) {
            if (${(demo!false)?c}) {
                 docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
            }
        };

        var onMetaChange = function (event) {
            var favorite = event.data.favorite;

            Alfresco.util.Ajax.jsonPost({
                url:  Alfresco.constants.PROXY_URI + "${favorite!}",
                successCallback: {
                    fn: function () {
                        docEditor.setFavorite(favorite);
                    },
                    scope: this
                }
            });
        };

        var onRequestHistoryClose = function () {
            document.location.reload();
        };

        var onRequestHistory = function () {
            Alfresco.util.Ajax.jsonGet({
                url:  Alfresco.constants.PROXY_URI + "${historyUrl!}",
                successCallback: {
                    fn: function (response) {
                        var hist = response.json;
                        docEditor.refreshHistory({
                            currentVersion: hist[0].version,
                            history: hist.reverse()
                        });
                    },
                    scope: this
                }
            });
        };

        var onRequestHistoryData = function (event) {
            var version = event.data;

            Alfresco.util.Ajax.jsonGet({
                url:  Alfresco.constants.PROXY_URI + "${historyUrl!}" + "&version=" + version,
                successCallback: {
                    fn: function (response) {
                        var hist = response.json;
                        docEditor.setHistoryData(response.json);
                    },
                    scope: this
                }
            });
        };

        var onRequestInsertImage = function (event) {
            documentPicker.singleSelectedItem = null;
            documentPicker.docEditorCommand = event.data.c;
            documentPicker.docEditorEvent = docEditor.insertImage;
            documentPicker.onShowPicker();
        };

        var onRequestMailMergeRecipients = function () {
            documentPicker.singleSelectedItem = null;
            documentPicker.docEditorCommand = null;
            documentPicker.docEditorEvent = docEditor.setMailMergeRecipients;
            documentPicker.onShowPicker();
        };

        var onRequestCompareFile = function () {
            documentPicker.singleSelectedItem = null;
            documentPicker.docEditorCommand = null;
            documentPicker.docEditorEvent = docEditor.setRevisedFile;
            documentPicker.onShowPicker();
        };

        var onRequestSharingSettings = function (event){
            document.getElementById("popup").style = "width: 100%; height: 100%; display: block; background-color: black;  opacity:0.2; z-index: 2; position:absolute";
            function getQueryParametr(queryParametr) {
                var p_url=location.search.substring(1);
                var parametr=p_url.split("&");
                var values= new Array();
                for(i in parametr) {
                    var j=parametr[i].split("=");
                    values[j[0]]=unescape(j[1]);
                }
                return values[queryParametr]
            }

            var nodeRefFromQueryString = getQueryParametr("nodeRef");
            var mp = new Alfresco.component.ManagePermissions("${id}").setOptions({
                "site":Alfresco.constants.SITE ,
                "nodeRef":nodeRefFromQueryString
            });
            function onPermissionsTemplateLoaded(response)
            {
                var permissionsEl = Dom.get("${id}" + "-managepermissions"),
                    permissionsContainerEl = Dom.get("${id}" + "-body"),
                    nodeRef = nodeRefFromQueryString;

                var url;
                if (nodeRef.uri)
                {
                    url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + nodeRef.uri;
                }
                else
                {
                    var nodeRefObj = Alfresco.util.NodeRef(nodeRef);
                    url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + nodeRefObj.storeType + "/" + nodeRefObj.storeId + "/" + nodeRefObj.id;
                }

                // Write response to DOM and switch to permissions mode (hiding channels UI):
                permissionsEl.innerHTML = response.serverResponse.responseText;
                Dom.addClass(permissionsContainerEl, "managePermissions");

                Alfresco.util.Ajax.jsonGet(
                    {
                        url: url,
                        successCallback:
                            {
                                fn: function consoleAdmin_getPermissionsDataSuccess(response)
                                {
                                    if (response.json !== undefined)
                                    {
                                        var nodeDetails = response.json.item;

                                        // Fire event to inform any listening components that the data is ready
                                        YAHOO.Bubbling.fire("nodeDetailsAvailable",
                                            {
                                                nodeDetails: nodeDetails,
                                                metadata: response.json.metadata
                                            });
                                    }
                                    mp.onReady();
                                    document.getElementsByClassName("center")[0].style = "padding-bottom: 1em;";
                                    document.getElementById("${id}-okButton-button").onclick = hideDisplay;
                                    document.getElementById("${id}-cancelButton-button").onclick = hideDisplay;
                                    function hideDisplay(event){
                                        document.getElementById("popup").style.display = "none";
                                    }
                                },
                                scope: this
                            },
                        failureMessage: "Failed to load data for permission details"
                    })

                // Override the default behaviour, ready for when the permissions page is done.
                Alfresco.component.ManagePermissions.prototype._navigateForward = function()
                {
                    // reverse the displays, so the permissions Div is hidden and channels div is shown
                    Dom.removeClass(permissionsContainerEl, "managePermissions");

                    // empty the permissions container
                    permissionsEl.innerHTML = "";
                }
            }

            Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.URL_SERVICECONTEXT + "components/manage-permissions/manage-permissions?nodeRef=" + nodeRefFromQueryString + "&htmlid=" + "${id}",
                    successCallback:
                        {
                            fn: onPermissionsTemplateLoaded,
                            scope: this
                        },
                    failureMessage: Alfresco.util.message("channelAdmin.template.error", this.name),
                    execScripts: true
                });

        }

        var onRequestSaveAs = function (event) {
            var title = event.data.title.substring(0, event.data.title.lastIndexOf("."));
            var ext = event.data.title.split(".").pop();
            var url = event.data.url;

            var time = 600;
            function insertFileNameInput () {
                if (!copyMoveTo.widgets.dialog && time > 0) {
                    time--;
                    setTimeout(insertFileNameInput, 100);
                } else if (!copyMoveTo.fileNameInput) {

                    copyMoveTo.widgets.dialog.hide();
                    copyMoveTo.widgets.okButton.set("label", "${msg('button.save')}");
                    var fileNameDiv = document.createElement("div");
                    fileNameDiv.classList.add("wrapper");
                    var fileNameLabel = document.createElement("h3");
                    fileNameLabel.classList.add("fileNameLabel");
                    fileNameLabel.innerHTML = "${msg('label.name')}:";
                    var fileNameInput = document.createElement("input");
                    fileNameInput.id = "fileNameInput";
                    fileNameInput.name = "fileNameInput";

                    fileNameInput.type = "text";
                    fileNameInput.value = title;
                    fileNameDiv.append(fileNameLabel);

                    fileNameDiv.append(fileNameInput);
                    copyMoveTo.widgets.dialog.body.prepend(fileNameDiv);
                    copyMoveTo.fileNameInput = true;

                    copyMoveTo.widgets.dialog.show();
                }
                if (copyMoveTo.fileNameInput) {
                    document.getElementById("fileNameInput").value = title;
                    document.getElementById("fileNameInput").classList.remove("invalid");
                }

            };

            copyMoveTo.showDialog();

            insertFileNameInput();

            copyMoveTo.onOK = function () {
                title = document.getElementById("fileNameInput").value;

                if (!title) {
                    document.getElementById("fileNameInput").classList.add("invalid");
                    return;
                }

                if (this.selectedNode) {
                    var requestData = {
                        title: title,
                        ext: ext,
                        url: url,
                        saveNode: this.selectedNode.data.nodeRef
                    };

                    copyMoveTo.widgets.escapeListener.disable();
                    copyMoveTo.widgets.dialog.hide();

                    var waitDialog = Alfresco.util.PopupManager.displayMessage({
                        text : "",
                        spanClass : "wait",
                        displayTime : 0
                    });

                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "parashift/onlyoffice/editor-api/save-as",
                        dataObj: requestData,
                        successMessage: "${msg('onlyoffice.editor.dialog.save-as.message.success')}",
                        successCallback: {
                            fn: function () {
                                waitDialog.destroy();
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                var errorMessage = "";
                                if (response.serverResponse.status == 403) {
                                    errorMessage = "${msg('onlyoffice.editor.dialog.save-as.message.error.forbidden')}";
                                } else {
                                    errorMessage = "${msg('onlyoffice.editor.dialog.save-as.message.error.unknown')}";
                                }
                                waitDialog.destroy();
                                Alfresco.util.PopupManager.displayMessage({
                                    text: errorMessage
                                });
                            },
                            scope: this
                        }
                    });
                }
            };
        };

        var editorConfig = ${editorConfig!};

        editorConfig.events = {
            "onAppReady": onAppReady,
            "onMetaChange": onMetaChange,
            "onRequestHistoryClose": onRequestHistoryClose,
            "onRequestHistory": onRequestHistory,
            "onRequestHistoryData": onRequestHistoryData,
            "onRequestInsertImage": onRequestInsertImage,
            "onRequestMailMergeRecipients": onRequestMailMergeRecipients,
            "onRequestCompareFile": onRequestCompareFile,
            "onRequestSaveAs": onRequestSaveAs
        };
        if (${(isCanShareRights!false)?c}) {
            editorConfig.events["onRequestSharingSettings"]= onRequestSharingSettings;
        }

        if (/android|avantgo|playbook|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|midp|mmp|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
            .test(navigator.userAgent)) {
            editorConfig.type='mobile';
        }

    if ((editorConfig.document.fileType === "docxf" || editorConfig.document.fileType === "oform")
        && DocsAPI.DocEditor.version().split(".")[0] < 7) {
        Alfresco.util.PopupManager.displayMessage({
            text : Alfresco.util.message("onlyoffice.editor.old-version-for-docxf-and-oform"),
            spanClass : "",
            displayTime : 0
        });
    } else {
        var docEditor = new DocsAPI.DocEditor("placeholder", editorConfig);
    }
    </script>
</body>
</html>

