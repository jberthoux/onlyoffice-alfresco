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
                var wordConvertType = ["docx", "dotx", "pdf", "pdfa", "odt", "ott", "txt", "rtf", "html", "fb2", "epub"];
                var cellConvertType = ["xlsx", "xltx", "pdf", "pdfa", "ods", "ots", "csv"];
                var slideConvertType = ["pptx", "potx", "pdf", "pdfa", "odp", "otp", "png", "jpg"];

                var action = this.getAction(record, owner),
                    params = action.params,
                    config = {
                            title: this.msg(action.label)
                        },
                    displayName = record.displayName;

                delete params["function"];
                var docExt = displayName.substring(displayName.lastIndexOf(".") + 1);
                var options = [];
                if (wordConvertType.includes(docExt)) {
                    options = wordConvertType;
                } else if (cellConvertType.includes(docExt)) {
                    options = cellConvertType;
                } else {
                    options = slideConvertType;
                }
                var getDownloadUrl = function (nodeRef, outputType) {
                    Alfresco.util.Ajax.jsonGet({
                        url : Alfresco.constants.PROXY_URI + "parashift/onlyoffice/download-as?nodeRef=" + nodeRef + "&srcType=" + docExt + "&outputType=" + outputType,
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
                        getDownloadUrl(obj.nodeRef, options[0]);
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
                if (params.successMessage) {
                    config.successMessage = this.msg(params.successMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + options[0]);
                }
                if (params.failureMessage) {
                    config.failureMessage = this.msg(params.failureMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + options[0]);
                }
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
                        Object.assign(fileName.style, {
                            textOverflow: "ellipsis",
                            display: "inline-block",
                            width: "40%",
                            overflow: "hidden",
                            marginTop: "10px"
                        });
                        select.before(fileName);
                        var label = document.createElement("p");
                        label.innerText = scope.msg("alfresco.document-onlyoffice-download-as.form.field.p.convert-into");
                        Object.assign(label.style, {
                            overflow: "hidden",
                            marginLeft: "10%",
                            marginRight: "10%",
                            display: "inline-block"
                        });
                        select.before(label);
                        submitButton.innerText = scope.msg("alfresco.document-onlyoffice-download-as.form.submit-button");
                        select.remove(select.children[0]);
                        for (var option of options) {
                            var downloadOption = document.createElement("option");
                            downloadOption.innerText = option;
                            select.append(downloadOption);
                        }
                        select.onchange = function(event){
                            config.failureMessage = scope.msg(params.failureMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + event.target.value);
                            config.successMessage = scope.msg(params.successMessage, displayName, displayName.substring(0, displayName.lastIndexOf(".") + 1) + event.target.value);
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
                            closeButton.addEventListener("click", function(){
                                waitDialog.destroy();
                            });
                        }
                        var cancelButton = document.getElementById(config.properties.htmlid + "-form-cancel-button");
                        if (cancelButton) {
                            cancelButton.addEventListener("click", function(){
                                waitDialog.destroy();
                            });
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