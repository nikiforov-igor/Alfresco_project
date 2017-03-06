(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Util = LogicECM.module.Base.Util;

    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-ord/ord-date-set-with-icon.css'
    ]);

    if (Bubbling.addLayer("ordItemCreateFormScriptLoaded")) {
        Bubbling.on('ordItemCreateFormScriptLoaded', processForm);
    }
    if (Bubbling.addLayer("ordItemEditFormScriptLoaded")) {
        Bubbling.on('ordItemEditFormScriptLoaded', processForm);
    }
    if (Bubbling.addLayer("ordItemReportRequiredChanged")) {
        Bubbling.on('ordItemReportRequiredChanged', processController);
    }

    function processForm(layer, args) {
        var formId = args[1].formId;
        var form = Alfresco.util.ComponentManager.get(formId);
        var documentFormId = formId.substring(0, formId.indexOf("_assoc_"));
        var formMode = form.options.templateRequestParams.mode;
        var tableRef = formMode == "create" ? form.options.templateRequestParams.destination : form.options.templateRequestParams.itemId;
        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
            dataObj: {
                nodeRef: tableRef,
                substituteString: "{..lecm-ord-table-structure:items-assoc/lecm-ord:controller-assoc-ref},{..lecm-ord-table-structure:items-assoc/lecm-document:subject-assoc-ref}"
            },
            successCallback: {
                fn: function (response) {
                    if (response && response.json.formatString) {
                        var data = response.json.formatString.split(",");
                        var controller = data[0];
                        var subject = data[1];
                        if (!controller) {
                            controller = Dom.get(documentFormId + "_assoc_lecm-ord_controller-assoc").value;
                        }
                        if (controller) {
                            Util.reInitializeControl(formId, "lecm-ord-table-structure:controller-assoc", {
                                "selectedValue": controller
                            });
                        }
                        if (!subject) {
                            subject = Dom.get(documentFormId + "_assoc_lecm-document_subject-assoc").value;
                        }
                        if (subject) {
                            Util.reInitializeControl(formId, "lecm-ord-table-structure:subject-assoc", {
                                "selectedValue": subject
                            });
                        }
                    }
                },
                scope: this
            },
            failureMessage: Alfresco.util.message("message.details.failure"),
            scope: this
        });
        if (layer == "ordItemCreateFormScriptLoaded") {
            var itemNumberField = Dom.get(formId + "_prop_lecm-document_indexTableRow");
            if (!itemNumberField.value) {
                var itemNumber = 1;
                var itemsTempTable = Dom.get(documentFormId + "_assoc_lecm-ord_temp-items-assoc");
                if (itemsTempTable) {
                    if (itemsTempTable.value) {
                        var valueArr = itemsTempTable.value.split(",");
                        itemNumber = valueArr.length + 1;
                    }
                    itemNumberField.value = itemNumber;
                } else {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + "lecm/ord/items/getNumber",
                        dataObj: {
                            tableDataRef: tableRef
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response && response.json.number) {
                                    itemNumber = parseInt(response.json.number);
                                    itemNumberField.value = itemNumber;
                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
                }
            }
        }
    }

    function processController(layer, args) {
        var formId = args[1].formId;
        var fieldHtmlId = formId + '_prop_' + args[1].fieldId.replace('\:', '_');
        var value = Dom.get(fieldHtmlId).value == "true";
        if (value) {
            Util.enableControl(formId, "lecm-ord-table-structure:controller-assoc");
        } else {
            Util.reInitializeControl(formId, "lecm-ord-table-structure:controller-assoc", {
                "selectedValue": null,
                "disabled": true
            });
        }
    }
})();