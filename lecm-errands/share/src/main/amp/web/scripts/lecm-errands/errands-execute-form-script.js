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

    Bubbling.on("errandsExecuteWFScriptLoaded", processForm);
    Bubbling.on("executeErrandButtonClick", executeErrand);

    function processForm(layer, args) {
        formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");
        processCloseChildCheckbox();
        var form = Dom.get(formId + "-form");
        Dom.addClass(form, "errands-execute-form");
        formButtons = Dom.get(formId + "-form-buttons");
        Event.onAvailable(formId + "-form-submit-button", function() {
            var saveReportElement = Dom.get(formId + "-form-submit-button");
            saveReportElement.innerHTML = Alfresco.util.message("button.save-report")
        });
    }

    function executeErrand(layer, args) {
        if (formId == args[1].formId && Dom.get(formId + "-form")) {
            // поле с формы процесса создания и формы редактирования
            var executeErrand = Selector.query('input[name="prop_lecmErrandWf_execute_1Execute"]', Dom.get(formId + "-form"), true);
            if (executeErrand) {
                executeErrand.value = true;
                var submitButton = Selector.query(".yui-submit-button", formButtons, true);
                submitButton.click();
            }
        }
    }

    function processCloseChildCheckbox() {
        Event.onContentReady(formId + "_assoc_packageItems-added", function () {
            var errandRef = Dom.get(formId + "_assoc_packageItems-added").value;
            Alfresco.util.Ajax.request({
                url: Alfresco.constants.PROXY_URI + "lecm/errands/api/hasChildOnLifeCycle",
                dataObj: {
                    nodeRef: errandRef
                },
                successCallback: {
                    fn: function (response) {
                        var hasChildOnLifeCycle = response.json.hasChildOnLifeCycle;
                        if (!hasChildOnLifeCycle) {
                            var closeChildField = Dom.get(formId + "_prop_" + "execute_1CloseChild");
                            if (closeChildField) {
                                Dom.setStyle(closeChildField.parentElement.parentElement.parentElement, "display", "none");
                            }
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }

})();