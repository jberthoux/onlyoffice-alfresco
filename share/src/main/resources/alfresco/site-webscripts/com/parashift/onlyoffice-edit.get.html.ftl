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

        var replaceActionLink = function(href, linkParam) {
            var link;
            var actionIndex = href.indexOf("&actionType=");
            if (actionIndex != -1) {
                link = href.substring(0, actionIndex) + "&actionType=" + encodeURIComponent(linkParam.type) + "&actionData=" + encodeURIComponent(linkParam.data);
            } else {
                link = href + "&actionType=" + encodeURIComponent(linkParam.type) + "&actionData=" + encodeURIComponent(linkParam.data);
            }
            return link;
        };

        var onRequestUsers = function () {
          docEditor.setUsers({
              "users": ${mentions}
          });
        };

        var onMakeActionLink = function (event) {
            var actionData = event.data.action;
            docEditor.setActionLink(replaceActionLink(location.href, actionData));
        };

        var onRequestSendNotify = function (event) {
            var comment = event.data.message;
            var emails = event.data.emails;
            var replacedActionLink = replaceActionLink(location.href, event.data.actionLink.action);
            var data = {
                comment : comment,
                emails: emails,
                link: replacedActionLink
            };
            fetch("${mentionUrl}", {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            })
                .then((response) => response.json())
                .then((data) => {
                    var updatedSharing = config.document.info.sharingSettings;
                    for(fullname of data) {
                        updatedSharing.push({"permissions": "Read Only", "user": fullname});
                    }
                    docEditor.setSharingSettings({
                        "sharingSettings": updatedSharing
                    });
            })
        };

        var onRequestSharingSettings = function () {

        };

        var config = ${config};

        config.events = {
            "onAppReady": onAppReady,
            "onRequestUsers": onRequestUsers,
            "onMakeActionLink": onMakeActionLink,
            "onRequestSendNotify": onRequestSendNotify,
            "onRequestSharingSettings": onRequestSharingSettings
        };

        var docEditor = new DocsAPI.DocEditor("placeholder", config);
    </script>
</body>
</html>

