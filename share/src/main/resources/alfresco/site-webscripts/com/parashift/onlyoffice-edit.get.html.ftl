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
        <iframe id="frame"
                width="65%"
                height="50%"
                align="top"
                allow="display-capture"
                src="${share}page/context/mine/manage-permissions?nodeRef=workspace://SpacesStore/${nodeRef}#bd">
        </iframe>
        <div id="black-overlay"></div>
    </div>
    <script>
        var onAppReady = function (event) {
            if (${demo?c}) {
                 docEditor.showMessage("${msg("alfresco.document.onlyoffice.action.edit.msg.demo")}");
            }
        };
        document.getElementById("black-overlay").addEventListener("click", function (event) {
            event.target.style.display = "none";
            document.getElementById("frame").style.display = "none";
        });

        var onRequestSharingSettings = function () {
            document.getElementById("black-overlay").style.display = "block";
            var frame = document.getElementById("frame");
            frame.style.display = "block";
            frame.contentWindow.document.getElementsByClassName("sticky-footer")[0].style.display = "none";
            frame.contentWindow.document.getElementById("alf-hd").style.display = "none";
        };

        var config = ${config};

        config.events = {
            "onAppReady": onAppReady,
            "onRequestSharingSettings": onRequestSharingSettings
        };

        if (/android|avantgo|playbook|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|midp|mmp|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\|plucker|pocket|psp|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i
            .test(navigator.userAgent)) {
            config.type='mobile';
        }
        var docEditor = new DocsAPI.DocEditor("placeholder", config);
    </script>
</body>
</html>

