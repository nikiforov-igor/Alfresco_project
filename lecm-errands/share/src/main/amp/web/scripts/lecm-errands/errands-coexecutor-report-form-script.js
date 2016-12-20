(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-coexecutor-report-form.css'
    ]);
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId;
    var formButtons;

    Bubbling.on("errandsWFCoexecutorReportScriptLoaded", process);
    Bubbling.on("routeReportButtonClick", submitForm);
    Bubbling.on('errandsEFCoexecutorReportScriptLoaded', process);

    function process(layer, args) {
        formId = args[1].formId;
        setUpForm(layer, args);
    }

    function setUpForm(layer, args) {
        if(formId == args[1].formId &&  Dom.get(formId + "-form")) {
            //меняет текст кнопки
            var form = Dom.get(formId + "-form");
            Dom.addClass(form, "errands-coexecutor-report-form");
            formButtons = Dom.get(formId + "-form-buttons");
            Event.onAvailable(formId + "-form-submit-button", function() {
                var saveReportElement = Dom.get(formId + "-form-submit-button");
                saveReportElement.innerHTML = Alfresco.util.message("button.save-report")
            });
        }
    }

    function submitForm(layer, args) {
        if (formId == args[1].formId && Dom.get(formId + "-form")) {
            // поле с формы процесса создания и формы редактирования
            var routeReport = Selector.query('input[name="prop_lecmErrandWf_coexecutorReport_1RouteReport"]', Dom.get(formId + "-form"), true);
            if (!routeReport) {
                routeReport = Selector.query('input[name="prop_lecm-errands-ts_coexecutor-report-is-route"]', Dom.get(formId + "-form"), true);
            }
            routeReport.value = true;
            var submitButton = Selector.query(".yui-submit-button", formButtons, true);
            submitButton.click();
        }
    }

})();