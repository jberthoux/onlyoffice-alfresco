(function () {
    YAHOO.Bubbling.fire("registerAction", {
        actionName: "onMultipleDownloadAs",
        fn: function (record, owner) {
            var scope = this;
            var multipleDownloadPost = function (nodeListToConvert) {
                waitDialog = Alfresco.util.PopupManager.displayMessage({
                    text : "",
                    spanClass : "wait",
                    displayTime : 0
                });
                var checkboxList = document.getElementsByClassName("checkbox-download-as");
                for (var check of checkboxList) {
                    if (!check.checked) {
                        nodeListToConvert.splice(checkboxList.indexOf(check), 1);
                    }
                }
                var data = {list : nodeListToConvert};
                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "parashift/onlyoffice/download-as",
                    responseContentType: "application/json",
                    dataObj: data,
                    successMessage: scope.msg("alfresco.document.onlyoffice.action.download-as.msg.success-multi"),
                    failureMessage: scope.msg("alfresco.document.onlyoffice.action.download-as.msg.failure-multi"),
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
            var textInputType = ["doc", "docm", "docx", "dot", "dotm", "dotx", "epub", "fb2", "fodt", "html", "mht", "odt", "ott", "pdf", "rtf", "txt", "xps", "xml"];
            var cellInputType = ["csv", "fods", "ods", "ots", "xls", "xlsm", "xlsx", "xlt", "xltm", "xltx"];
            var slideInputType = ["fodp", "odp", "otp", "pot", "potm", "potx", "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx"];

            var textOutputType = ["docx", "bmp", "epub", "fb2", "gif", "html", "jpg", "odt", "pdf", "pdfa", "png", "rtf", "txt"];
            var cellOutputType = ["xlsx", "bmp", "csv", "gif", "jpg", "ods", "pdf", "pdfa", "png"];
            var slideOutputType = ["pptx", "bmp", "gif", "jpg", "odp", "pdf", "pdfa", "png"];

            var getConvertTypeArray = function (docExt) {
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

            var correctNodes = [];
            var dataToConvert = [];
            for (var nodeRecord of record) {
                var ext = nodeRecord.displayName.substring(nodeRecord.displayName.lastIndexOf(".") + 1);
                if (nodeRecord.jsNode.mimetype != undefined && getConvertTypeArray(ext) != null) {
                    correctNodes.push(nodeRecord);
                    dataToConvert.push({nodeRef: nodeRecord.nodeRef, srcType: ext, outputType: ext});
                }
            }

            var prompt = new YAHOO.widget.SimpleDialog("prompt", {
                close:true,
                constraintoviewport: true,
                draggable: false,
                effect: null,
                modal: true,
                visible: false,
                zIndex: this.zIndex++
            });
            var buttons= [
                {
                    text : Alfresco.util
                        .message(this.msg("alfresco.document-onlyoffice-download-as.form.submit-button")),
                        handler : function onAction_success() {
                        multipleDownloadPost(dataToConvert);
                        this.destroy();
                    }
                },
                {
                    text : Alfresco.util
                        .message("button.cancel"),
                        handler : function onAction_cancel() {
                        this.destroy();
                    },
                    isDefault : true
                } ];
            prompt.setHeader(Alfresco.util.message(this.msg("actions.document.onlyoffice-download-as")));
            prompt.setBody("");
            prompt.cfg.queueProperty("buttons", buttons);
            prompt.render(document.body);
            var form = document.getElementById("prompt_c").getElementsByClassName("bd")[0].children[0];
            var formLabel = document.createElement("label");
            formLabel.style.display = "block";
            formLabel.style.width = "auto";
            formLabel.style.textAlign = "left";
            formLabel.innerText = this.msg("alfresco.document-onlyoffice-download-as.form.field.select-type");
            form.style.overflowY = "auto";
            form.style.maxHeight = "250px";
            form.appendChild(formLabel);
            for (var node of correctNodes) {
                var div = document.createElement("div");
                div.style.position = "relative";
                div.style.marginLeft = "-40px";
                var checkbox = document.createElement("input");
                checkbox.id = "checkbox-download-as-" + node.nodeRef;
                checkbox.className = "checkbox-download-as";
                checkbox.type = "checkbox";
                checkbox.checked = true;
                Object.assign(checkbox.style, {
                    display: "inline-block",
                    marginTop: "10px",
                    position: "absolute",
                    left: "60px"
                });
                div.appendChild(checkbox);
                var fileName = document.createElement("p");
                fileName.innerText = node.displayName;
                Object.assign(fileName.style, {
                    textAlign: "left",
                    textOverflow: "ellipsis",
                    display: "inline-block",
                    width: "30%",
                    overflow: "hidden",
                    marginTop: "5px",
                    marginLeft: "20px"
                });
                checkbox.after(fileName);
                var label = document.createElement("p");
                label.innerText = this.msg("alfresco.document-onlyoffice-download-as.form.field.p.convert-into");
                Object.assign(label.style, {
                    overflow: "hidden",
                    marginLeft: "6%",
                    marginRight: "6%",
                    display: "inline-block"
                });
                fileName.after(label);
                var select =  document.createElement("select");
                select.id = "select-download-as-" + correctNodes.indexOf(node);
                select.style.width = "60px";
                select.style.marginTop = "5px";
                select.style.position = "absolute";
                select.onchange = function (event) {
                    dataToConvert[event.target.id.split("-")[3]].outputType = event.target.value;
                };
                var selectOptions = getConvertTypeArray(node.displayName.substring(node.displayName.lastIndexOf(".") + 1));
                for (var option of selectOptions) {
                    var downloadOption = document.createElement("option");
                    downloadOption.innerText = option;
                    select.append(downloadOption);
                }
                label.after(select);
                form.appendChild(div);
            }
            prompt.center();
            prompt.show();
            YAHOO.util.Event.removeListener(prompt.close, "click");
            YAHOO.util.Event.on(prompt.close, "click", function(){
                this.destroy();
            }, prompt, true);
        }
    });
})();