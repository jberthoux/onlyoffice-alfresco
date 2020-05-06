/*
    Copyright (c) Ascensio System SIA 2020. All rights reserved.
    http://www.onlyoffice.com
*/

if (model.widgets) {
    for (var i = 0; i < model.widgets.length; i++) {
        var widget = model.widgets[i];
        if (widget.id == "WebPreview") {
            pObj = eval('(' + remote.call("/parashift/onlyoffice/prepare?nodeRef=" + url.args.nodeRef + "&preview=true") + ')');

            if (pObj.previewEnabled && pObj.onlyofficeUrl && pObj.mime) {
                model.onlyofficeUrl = pObj.onlyofficeUrl;

                widget.options.pluginConditions = jsonUtils.toJSONString([{
                    attributes: {
                        mimeType: pObj.mime
                    },
                    plugins: [{
                        name: "onlyoffice",
                        attributes: {
                            config: pObj.config
                        }
                    }]
                }]);
            }
        }
    }
}