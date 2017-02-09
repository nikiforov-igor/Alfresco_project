(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-execute-form.css'
    ]);
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId;
    var formButtons;
    var scriptLayer;

    Bubbling.on("errandsExecuteWFScriptLoaded", processForm);
    Bubbling.on("editExecutionReportScriptLoaded", processForm);
    Bubbling.on("executeErrandButtonClick", executeErrand);

    function processForm(layer, args) {
        scriptLayer = layer;
        formId = args[1].formId;
        if (scriptLayer == "errandsExecuteWFScriptLoaded") {
            Event.onContentReady(formId + "_assoc_packageItems-added", function () {
                var errandRef = Dom.get(formId + "_assoc_packageItems-added").value;
                processCloseChildCheckbox(errandRef, "lecmErrandWf_execute_1CloseChild");
                processReportTextField(errandRef, "lecmErrandWf_execute_1ReportText");
                processNewReportProject(errandRef);
            });
        } else {
            var form = Alfresco.util.ComponentManager.get(formId);
            if (form) {
                var errandRef = form.options.nodeRef;
                processCloseChildCheckbox(errandRef, "lecm-errands_execution-report-close-child");
                processReportTextField(errandRef, "lecm-errands_execution-report");
            }
        }


        var form = Dom.get(formId + "-form");
        Dom.addClass(form, "errands-execute-form");
        formButtons = Dom.get(formId + "-form-buttons");
        Event.onAvailable(formId + "-form-submit-button", function () {
            var saveReportElement = Dom.get(formId + "-form-submit-button");
            saveReportElement.innerHTML = Alfresco.util.message("button.save-report")
        });
    }
    function processNewReportProject (errandRef) {
        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
            dataObj: {
                nodeRef: errandRef,
                substituteString: "{lecm-errands:execution-report-status}"
            },
            successCallback: {
                fn: function (response) {
                    if (response && response.json.formatString) {
                        var reportStatus = response.json.formatString;
                        if (reportStatus != "PROJECT") {
                            //clearFormData!
                        }
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.details.failure")
        });
    }
    function executeErrand(layer, args) {
        if (formId == args[1].formId && Dom.get(formId + "-form")) {
            // поле с формы процесса создания и формы редактирования
            var executeErrand = Selector.query(scriptLayer == "errandsExecuteWFScriptLoaded" ? 'input[name="prop_lecmErrandWf_execute_1Execute"]'
                : 'input[name="prop_lecm-errands_execution-report-is-execute"]', Dom.get(formId + "-form"), true);
            if (executeErrand) {
                executeErrand.value = true;
                var submitButton = Selector.query(".yui-submit-button", formButtons, true);
                submitButton.click();
            }
        }
    }


    function processReportTextField(errandRef, field) {

        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
            dataObj: {
                nodeRef: errandRef,
                substituteString: "{lecm-errands:report-required}"
            },
            successCallback: {
                fn: function (response) {
                    if (response && response.json.formatString) {
                        var isReportRequired = response.json.formatString;
                        if (isReportRequired == "true") {
                            var reportTextReadyElId = LogicECM.module.Base.Util.getComponentReadyElementId(formId, field.replace("_", ":"));
                            Event.onContentReady(reportTextReadyElId, function () {
                                YAHOO.Bubbling.fire("registerValidationHandler",
                                    {
                                        fieldId: formId + "_prop_" + field,
                                        handler: Alfresco.forms.validation.mandatory,
                                        when: "change"
                                    });
                            });
                        }
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.details.failure")
        });
    }

    function processCloseChildCheckbox(errandRef, field) {

        Alfresco.util.Ajax.request({
            url: Alfresco.constants.PROXY_URI + "lecm/errands/api/hasChildOnLifeCycle",
            dataObj: {
                nodeRef: errandRef
            },
            successCallback: {
                fn: function (response) {
                    var hasChildOnLifeCycle = response.json.hasChildOnLifeCycle;
                    if (!hasChildOnLifeCycle) {
                        var closeChildField = Dom.get(formId + "_prop_" + field);
                        if (closeChildField) {
                            Dom.setStyle(closeChildField.parentElement.parentElement.parentElement, "display", "none");
                        }
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });
    }
})();