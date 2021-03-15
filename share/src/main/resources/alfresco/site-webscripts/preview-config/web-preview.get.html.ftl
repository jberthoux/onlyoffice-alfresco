<!--
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
-->

<#if onlyofficeUrl??>
    <@markup id="onlyoffice-preview" target="js" action="after" scope="global">
        <script id="scriptApi" type="text/javascript" src="${onlyofficeUrl}OfficeWeb/apps/api/documents/api.js"></script>
    </@markup>
</#if>