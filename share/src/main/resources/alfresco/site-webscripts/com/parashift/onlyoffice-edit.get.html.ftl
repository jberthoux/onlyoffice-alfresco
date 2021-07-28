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
        var onAppReady = function (event) {
            if (${demo?c}) {
                 docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
            }
        };

        var config = ${config};

        var onOutdatedVersion = function(event){
            location.reload(true);
        };
        config.events = {
            "onAppReady": onAppReady,
            "onOutdatedVersion": onOutdatedVersion
        };

        var hist = ${historyObj}.history;

        if (hist) {
            config.events['onRequestHistory'] = function () {
                for(historyVersion of hist){
                    historyVersion.serverVersion = docEditor.version;
                    var date = new Date(historyVersion.created).toISOString().replace('T', ' ');
                    date = date.substring(0, date.length-5);
                    historyVersion.created = date;
                    historyVersion.changes.created = date;
                }
                docEditor.refreshHistory({
                    currentVersion: hist.length,
                    history: hist.reverse()
                });
            };
            config.events['onRequestHistoryClose'] = function () {
                document.location.reload();
            };
        }

        var docEditor = new DocsAPI.DocEditor("placeholder", config);
    </script>
</body>
</html>

