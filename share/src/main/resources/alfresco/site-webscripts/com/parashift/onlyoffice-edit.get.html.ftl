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
</head>

<body>
    <div>
        <div id="placeholder"></div>
    </div>
    <script>
        var linkWithoutNewParameter = null;
        var onAppReady = function (event) {
            if (${demo?c}) {
                 docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
            }
            linkWithoutNewParameter = document.location.href.substring(0, document.location.href.lastIndexOf("nodeRef")) + "nodeRef=workspace://SpacesStore/"
                + config.document.key.substring(0, config.document.key.lastIndexOf("_"));
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
        var config = ${config};

        config.events = {
            "onAppReady": onAppReady,
            "onMetaChange": onMetaChange,
            "onRequestHistoryClose": onRequestHistoryClose,
            "onRequestHistory": onRequestHistory,
            "onRequestHistoryData": onRequestHistoryData
        };

        if (/android|avantgo|playbook|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|midp|mmp|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
            .test(navigator.userAgent)) {
            config.type='mobile';
        }

        var docEditor = new DocsAPI.DocEditor("placeholder", config);
        if(config.document.info.favorite){
            var title = document.title.replace(/^\☆/g, "");
            document.title = (config.document.info.favorite ? "☆" : "") + title;
        }
    </script>
</body>
</html>

