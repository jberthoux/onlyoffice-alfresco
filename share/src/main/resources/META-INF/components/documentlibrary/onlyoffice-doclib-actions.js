/*
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
*/

(function () {
    YAHOO.Bubbling.fire("registerAction",
    {
        actionName: "onOnlyofficeCreateDocx",
        fn: function (obj) { openAndRefresh(obj, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"); }
    });
    YAHOO.Bubbling.fire("registerAction",
    {
        actionName: "onOnlyofficeCreateXlsx",
        fn: function (obj) { openAndRefresh(obj, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); }
    });
    YAHOO.Bubbling.fire("registerAction",
    {
        actionName: "onOnlyofficeCreatePptx",
        fn: function (obj) { openAndRefresh(obj, "application/vnd.openxmlformats-officedocument.presentationml.presentation"); }
    });
    YAHOO.Bubbling.fire("registerAction", {
            actionName: "onActionWaitingFormDialog",
            fn: function action(record, owner) {
                var textInputType = ["doc", "docm", "docx", "dot", "dotm", "dotx", "epub", "fb2", "fodt", "html", "mht", "odt", "ott", "pdf", "rtf", "txt", "xps", "xml"];
                var cellInputType = ["csv", "fods", "ods", "ots", "xls", "xlsm", "xlsx", "xlt", "xltm", "xltx"];

                var textOutputType = ["docx", "bmp", "epub", "fb2", "gif", "html", "jpg", "odt", "pdf", "pdfa", "png", "rtf", "txt"];
                var cellOutputType = ["xlsx", "bmp", "csv", "gif", "jpg", "ods", "pdf", "pdfa", "png"];
                var slideOutputType = ["pptx", "bmp", "gif","jpg", "odp", "pdf", "pdfa", "png"];

                var action = this.getAction(record, owner),
                    params = action.params,
                    config = {
                            title: this.msg(action.label)
                        },
                    displayName = record.displayName;

                delete params["function"];
                var docExt = displayName.substring(displayName.lastIndexOf(".") + 1);
                var outputOptions = [];
                if (textInputType.includes(docExt)) {
                    switch (docExt) {
                        case "mht": {
                            textOutputType.splice(textOutputType.indexOf("html"), 1);
                            break;
                        }
                        case "pdf": {
                            outputOptions = ["bmp", "gif", "jpg", "png"];
                            break;
                        }
                        case "xps": {
                            outputOptions = ["bmp", "gif", "jpg", "pdf", "pdfa", "png"];
                            break;
                        }
                        default: {
                            outputOptions = textOutputType;
                            break;
                        }
                    }
                } else if (cellInputType.includes(docExt)) {
                    outputOptions = cellOutputType;
                } else {
                    outputOptions = slideOutputType;
                }
                var getDownloadUrl = function (nodeRef, outputType) {
                    Alfresco.util.Ajax.jsonPost({
                        url : Alfresco.constants.PROXY_URI + "parashift/onlyoffice/download-as?nodeRef=" + nodeRef + "&srcType=" + docExt + "&outputType=" + outputType,
                        successMessage: scope.msg(params.successMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + outputType),
                        failureMessage: scope.msg(params.successMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + outputType),
                        successCallback : {
                            fn : function (response) {
                                window.open(response.json.downloadUrl);
                            },
                            scope : this
                        },
                        failureCallback : {
                            fn : function() {},
                            scope : this
                        }
                    });
                };
                config.success = {
                    fn: function(response, obj) {
                        getDownloadUrl(obj.nodeRef, outputOptions[0]);
                        YAHOO.Bubbling.fire("metadataRefresh", obj);
                    },
                    obj: record,
                    scope: this
                };
                config.failure = {
                        fn: function(response, obj) {
                            YAHOO.Bubbling.fire("metadataRefresh", obj);
                        },
                        obj: record,
                        scope: this
                    };
                config.properties = params;
                Alfresco.util.PopupManager.displayForm(config);
                waitDialog = Alfresco.util.PopupManager.displayMessage({
                    text : "",
                    spanClass : "wait",
                    displayTime : 0
                });
                var scope = this;
                YAHOO.Bubbling.on("beforeFormRuntimeInit", function PopupManager_displayForm_onBeforeFormRuntimeInit(layer, args) {
                        var panel = document.getElementById(config.properties.htmlid + "-panel");
                        var select = document.getElementById(config.properties.htmlid + "_prop_select_type");
                        select.style.position = "absolute";
                        select.style.marginTop = "10px";
                        var submitButton = document.getElementById(config.properties.htmlid + "-form-submit-button");
                        var fileName = document.createElement("p");
                        fileName.innerText = displayName;
                        fileName.className = "fileName-download-as-oneFile";
                        select.before(fileName);
                        var label = document.createElement("p");
                        label.innerText = scope.msg("alfresco.document-onlyoffice-download-as.form.field.p.convert-into");
                        label.className = "label-download-as-oneFile";
                        select.before(label);
                        submitButton.innerText = scope.msg("alfresco.document-onlyoffice-download-as.form.submit-button");
                        select.remove(select.children[0]);
                        for (var option of outputOptions) {
                            var downloadOption = document.createElement("option");
                            downloadOption.innerText = option;
                            select.append(downloadOption);
                        }
                        select.onchange = function(event){
                             config.success = {
                                fn: function(response, obj) {
                                    getDownloadUrl(obj.nodeRef, event.target.value);
                                    YAHOO.Bubbling.fire("metadataRefresh", obj);
                                },
                                obj: record,
                                scope: this
                            };
                        };
                        var closeButton = panel.getElementsByClassName("container-close")[0];
                        if (closeButton) {
                            closeButton.onclick = function () {
                                waitDialog.destroy();
                            }
                        }
                        var cancelButton = document.getElementById(config.properties.htmlid + "-form-cancel-button");
                        if (cancelButton) {
                            cancelButton.onclick = function () {
                                waitDialog.destroy();
                            }
                        }
                    },
                    {
                        config: config
                    });
            }
    });

    function openAndRefresh(obj, mime) {
        window.open("onlyoffice-edit?nodeRef=" + obj.nodeRef + "&new=" + mime);
        setTimeout(function() { YAHOO.Bubbling.fire("metadataRefresh", obj); }, 1000);
    }
})();