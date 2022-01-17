<!--
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
-->
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=utf-8'>

    <title>${docTitle} - ONLYOFFICE</title>

    <link href="${url.context}/res/components/onlyoffice/onlyoffice.css" type="text/css" rel="stylesheet">

    <!--Change the address on installed ONLYOFFICE™ Online Editors-->
    <script id="scriptApi" type="text/javascript" src="${onlyofficeUrl}OfficeWeb/apps/api/documents/api.js"></script>

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

    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-fonts-grids.css" />
    <#if theme = 'default'>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" />
    <#else>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" />
    </#if>
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/documentlibrary/global-folder.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/documentlibrary/tree.css">
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-picker/document-picker.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/object-finder/object-finder.css" />
</head>

<body id="Share" class="yui-skin-${theme} alfresco-share claro">
    <div>
        <div id="placeholder"></div>
    </div>

    <script>

    var onAppReady = function (event) {
        if (${demo?c}) {
             docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
        }
    };

        var onRequestSaveAs = function (event) {
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

    var config = ${editorConfig!};

    config.events = {
        "onAppReady": onAppReady,
        "onRequestSaveAs": onRequestSaveAs
    };

     if ((config.document.fileType === "docxf" || config.document.fileType === "oform")
         && DocsAPI.DocEditor.version().split(".")[0] < 7) {
         Alfresco.util.PopupManager.displayMessage({
             text : Alfresco.util.message("onlyoffice.editor.old-version-for-docxf-and-oform"),
             spanClass : "",
             displayTime : 0
         });
     } else {
         var docEditor = new DocsAPI.DocEditor("placeholder", config);
     }

    </script>
</body>
</html>

