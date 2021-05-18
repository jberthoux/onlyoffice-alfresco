/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

if (model.widgets) {
    for (var i = 0; i < model.widgets.length; i++) {
        var widget = model.widgets[i];
        if (widget.id == "WebPreview" && url.args.nodeRef) {
            widget.options.pluginConditions = jsonUtils.toJSONString([{
                attributes: {
                    mimeType: widget.options.mimeType
                },
                plugins: [{
                    name: "onlyoffice",
                    attributes: {
                        nodeRef: url.args.nodeRef
                    }
                }]
            }]);
        }
    }
}