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

    Bubbling.on('errandCoexecutorReportExpandScriptLoaded', process);

    function process(layer, args) {
        formId = args[1].formId;
        prepareReportActions(layer, args);
    }


    function prepareReportActions(layer, args){

        var dataTable;
        dataTable = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
    }


})();