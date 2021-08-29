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
        var config = ${config};

        config.events = {
            "onAppReady": onAppReady,
            "onMetaChange": onMetaChange
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

