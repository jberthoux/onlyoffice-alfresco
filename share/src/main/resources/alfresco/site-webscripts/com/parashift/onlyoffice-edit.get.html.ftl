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

        fetch("http://192.168.88.94/share/proxy/alfresco/slingshot/doclib/node-templates")
            .then(r=>r.json())
            .then(data=>{
                let templates = [];
                for(node of data.data){
                    if(node.name.substring(node.name.length-4) == config.document.fileType){
                        template = {
                            image: "${share}proxy/alfresco/api/node/workspace/SpacesStore/" + node.nodeRef.split("/SpacesStore/")[1] +"/content/thumbnails/doclib?ph=true",
                            title: node.name,
                            url: config.editorConfig.createUrl + "&parentNodeRef=" + node.nodeRef
                        };
                        templates.push(template);
                    }
                }
            config.editorConfig.templates = templates;
        });

        config.events = {
            "onAppReady": onAppReady
        };

        var docEditor = new DocsAPI.DocEditor("placeholder", config);
    </script>
</body>
</html>

