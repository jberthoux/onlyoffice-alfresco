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
    <link rel="shortcut icon" href="${url.context}/res/components/images/filetypes/${documentType}.ico" type="image/vnd.microsoft.icon" />
    <link rel="icon" href="${url.context}/res/components/images/filetypes/${documentType}.ico" type="image/vnd.microsoft.icon" />

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
    </script>
    <script type="text/javascript" src="${url.context}/res/js/alfresco.js"></script>
    <script type="text/javascript" src="${url.context}/res/modules/document-picker/document-picker.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/object-finder/object-finder.js"></script>
    <script type="text/javascript" src="${url.context}/res/components/common/common-component-style-filter-chain.js"></script>

    <#if theme = 'default'>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/yui/assets/skins/default/skin.css" />
    <#else>
        <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/yui/assets/skin.css" />
    </#if>
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/base.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-layout.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/presentation.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-picker/document-picker.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/components/object-finder/object-finder.css" />
    <link rel="stylesheet" type="text/css" href="${url.context}/res/css/yui-fonts-grids.css" group="template-common" />
</head>

<body id="Share" class="yui-skin-${theme} alfresco-share claro">
    <div id="placeholder"></div>
    <script>
        var documentPicker = new Alfresco.module.DocumentPicker("onlyoffice-editor-docPicker", Alfresco.ObjectRenderer);
        documentPicker.setOptions({
           displayMode: "items",
           itemFamily: "node",
           itemType: "cm:content",
           multipleSelectMode: false,
           parentNodeRef: ${folderNode},
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

        var linkWithoutNewParameter = null;
        var onAppReady = function (event) {
            if (${demo?c}) {
                 docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
            }
            linkWithoutNewParameter = document.location.href.substring(0, document.location.href.lastIndexOf("nodeRef")) + "nodeRef=workspace://SpacesStore/"
                + editorConfig.document.key.substring(0, editorConfig.document.key.lastIndexOf("_"));
            window.history.pushState({}, {}, linkWithoutNewParameter);
        };

        var getCookie = function (name) {
            var value = document.cookie;
            var parts = value.split(name);
            if (parts.length === 2) return parts.pop().split(';').shift().substring(1);
        };

        var onMetaChange = function (event) {
            var favorite = !!event.data.favorite;
                        fetch("${favorite} ", {
                            method: "POST",
                            headers: new Headers({
                                'Content-Type': 'application/json',
                                'Alfresco-CSRFToken': decodeURIComponent(getCookie('Alfresco-CSRFToken'))
                            })
                        })
                        .then(response => {
                            var title = document.title.replace(/^\☆/g, "");
                            document.title = (favorite ? "☆" : "") + title;
                            docEditor.setFavorite(favorite);
                        });
        };

        var onRequestHistoryClose = function () {
            document.location.href = linkWithoutNewParameter;
        };

        var onRequestHistory = function () {
            var xhr = new XMLHttpRequest();
            var historyUri = "${historyUrl}";
            xhr.open("GET", historyUri + "&alf_ticket=" + "${ticket}", false);
            xhr.send();
            if (xhr.status == 200) {
                var hist = JSON.parse(xhr.responseText);
                docEditor.refreshHistory({
                    currentVersion: hist[0].version,
                    history: hist.reverse()
                });
            }
        };

        var onRequestHistoryData = function (event) {
            var xhr = new XMLHttpRequest();
            var historyUri = "${historyUrl}";
            var version = event.data;
            xhr.open("GET", historyUri + "&version=" + version + "&alf_ticket=" + "${ticket}", false);
            xhr.send();
            if (xhr.status == 200) {
                var response = JSON.parse(xhr.responseText);
                if (response !== null) {
                    docEditor.setHistoryData(response);
                } else {
                    docEditor.setHistoryData([]);
                }
            }
        };

        var onRequestInsertImage = function (event) {
            documentPicker.singleSelectedItem = null;
            documentPicker.docEditorCommand = event.data.c;
            documentPicker.docEditorEvent = docEditor.insertImage;
            documentPicker.onShowPicker();
        };

        var onRequestMailMergeRecipients = function () {

        }

        var editorConfig = ${editorConfig};

        editorConfig.events = {
            "onAppReady": onAppReady,
            "onMetaChange": onMetaChange,
            "onRequestHistoryClose": onRequestHistoryClose,
            "onRequestHistory": onRequestHistory,
            "onRequestHistoryData": onRequestHistoryData,
            "onRequestInsertImage": onRequestInsertImage,
            "onRequestMailMergeRecipients", onRequestMailMergeRecipients
        };

        if (/android|avantgo|playbook|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|midp|mmp|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
            .test(navigator.userAgent)) {
            editorConfig.type='mobile';
        }

        var docEditor = new DocsAPI.DocEditor("placeholder", editorConfig);
        if(editorConfig.document.info.favorite){
            var title = document.title.replace(/^\☆/g, "");
            document.title = (editorConfig.document.info.favorite ? "☆" : "") + title;
        }
    </script>
</body>
</html>

