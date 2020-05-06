/*
    Copyright (c) Ascensio System SIA 2019. All rights reserved.
    http://www.onlyoffice.com
*/

var readonly = url.args.readonly ? "readonly=1&" : "";
pObj = eval('(' + remote.call("/parashift/onlyoffice/prepare?" + readonly + "nodeRef=" + url.args.nodeRef + "&new=" + url.args.new) + ')');
model.onlyofficeUrl = pObj.onlyofficeUrl;
model.docTitle = pObj.document.title;
delete (pObj.onlyofficeUrl);
if (pObj.mime) {
    delete (pObj.mime);
}
model.config = JSON.stringify(pObj);
