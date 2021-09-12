(function () {
    var textInputType = ["doc", "docm", "docx", "dot", "dotm", "dotx", "epub", "fb2", "fodt", "html", "mht", "odt", "ott", "pdf", "rtf", "txt", "xps", "xml"];
    var cellInputType = ["csv", "fods", "ods", "ots", "xls", "xlsm", "xlsx", "xlt", "xltm", "xltx"];
    var slideInputType = ["fodp", "odp", "otp", "pot", "potm", "potx", "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx"];

    var textOutputType = ["docx", "bmp", "epub", "fb2", "gif", "html", "jpg", "odt", "pdf", "pdfa", "png", "rtf", "txt"];
    var cellOutputType = ["xlsx", "bmp", "csv", "gif", "jpg", "ods", "pdf", "pdfa", "png"];
    var slideOutputType = ["pptx", "bmp", "gif", "jpg", "odp", "pdf", "pdfa", "png"];

    var getConvertTypes = function (docExt) {
        if (textInputType.includes(docExt)) {
            switch (docExt) {
                case "mht": {
                    textOutputType.splice(textOutputType.indexOf("html"), 1);
                    break;
                }
                case "pdf": {
                    return ["bmp", "gif", "jpg", "png"];
                }
                case "xps": {
                    return ["bmp", "gif", "jpg", "pdf", "pdfa", "png"];
                }
                default: {
                    return textOutputType;
                }
            }
        } else if (cellInputType.includes(docExt)) {
            return cellOutputType;
        } else if (slideInputType.includes(docExt)) {
            return slideOutputType;
        } else {
            return null;
        }
    };

    YAHOO.Bubbling.fire("registerAction", {
        actionName: "onOnlyofficeDownloadAs",
        fn: function (record, owner) {
            var multipleDownloadPost = function (requestData) {
                var waitDialog = Alfresco.util.PopupManager.displayMessage({
                    text : "",
                    spanClass : "wait",
                    displayTime : 0
                });

                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "parashift/onlyoffice/download-as",
                    responseContentType: "application/json",
                    dataObj: requestData,
                    successMessage: Alfresco.util.message("alfresco.document.onlyoffice.action.download-as.msg.success-multi"),
                    failureMessage: Alfresco.util.message("alfresco.document.onlyoffice.action.download-as.msg.failure-multi"),
                    successCallback: {
                        fn: function (response) {
                            waitDialog.destroy()
                            window.open(response.json.downloadUrl);
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function exampleFailure() {
                            waitDialog.destroy()
                        },
                        obj: record,
                        scope: this
                    }
                });
            };

            var nodeList = [];
            if (Array.isArray(record)) {
                for (var node of record) {
                    nodeList.push(node);
                }
            } else {
                nodeList.push(record);
            }

            var body = [];
            body.push(
                "<label>" + this.msg("alfresco.document-onlyoffice-download-as.form.field.select-type") + "</label>",
                "<div class='download-as-main'>"
            );

            for (var node of nodeList) {
                var ext = node.displayName.substring(node.displayName.lastIndexOf(".") + 1);
                var showCheckbox = nodeList.length == 1 ? "hidden" : "";

                if (node.jsNode.mimetype != undefined && getConvertTypes(ext) != null) {
                    body.push(
                        "<div class='download-as-file'>",
                        "<input type='checkbox' class='download-as-checkbox " + showCheckbox + "' checked='true' value='" + node.nodeRef + "'>",
                        "<p class='download-as-filename'>" + node.displayName + "</p>",
                        "<p class='download-as-convert-label'>" + this.msg("alfresco.document-onlyoffice-download-as.form.field.p.convert-into") + "</p>",
                        "<select class='download-as-select'>"
                    );

                    var convertTypes = getConvertTypes(ext);
                    for (var type of convertTypes) {
                        body.push("<option>" + type + "</option>");
                    }

                    body.push(
                        "</select>",
                        "</div>"
                    );
                }
            }

            body.push("</div>");

            var prompt = new YAHOO.widget.SimpleDialog("prompt", {
                close:true,
                constraintoviewport: true,
                draggable: false,
                effect: null,
                modal: true,
                visible: false,
                zIndex: this.zIndex++,
                buttons: [
                    {
                        text: Alfresco.util.message("alfresco.document-onlyoffice-download-as.form.submit-button"),
                        handler: function onAction_success() {
                            var requestData = [];
                            var fileList = document.getElementsByClassName("download-as-file");

                            for (var file of fileList) {
                                var checkbox = file.querySelector(".download-as-checkbox");
                                if (checkbox.checked) {
                                    var select = file.querySelector(".download-as-select");
                                    requestData.push({
                                        nodeRef: checkbox.value,
                                        outputType: select.value
                                    });
                                }
                            }

                            if (requestData.length > 0) {
                                this.destroy();
                                multipleDownloadPost(requestData);
                            }
                        }
                    },
                    {
                        text : Alfresco.util.message("button.cancel"),
                        handler : function onAction_cancel() {
                            this.destroy();
                        },
                        isDefault : true
                    }
                ]
            });

            prompt.setHeader(Alfresco.util.message("actions.document.onlyoffice-download-as"));
            prompt.setBody(body.join(""));
            prompt.render(document.body);
            prompt.center();
            prompt.show();

            YAHOO.util.Event.removeListener(prompt.close, "click");
            YAHOO.util.Event.on(prompt.close, "click", function() {
                this.destroy();
            }, prompt, true);
        }
    });
})();