(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
    var formId;

    Bubbling.on('errandsWFChangeDueDateScriptLoaded', init);
    Bubbling.on('requestDueDateChangeTaskFormScriptLoaded', process);

    function init(layer, args) {
        formId = args[1].formId;
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
                            var changeChildDueDateField = Dom.get(formId + "_prop_lecmErrandWf_changeDueDateChangeChildDueDate");
                            if (!changeChildDueDateField) {
                                changeChildDueDateField = Dom.get(formId + "_prop_lecmErrandWf_requestDueDateChange_1ChildDueDate");
                            }
                            if (changeChildDueDateField) {
                                changeChildDueDateField.value = false;
                                Dom.setStyle(changeChildDueDateField.parentElement.parentElement.parentElement, "display", "none");
                            }
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }
    function process(){
        formId = args[1].formId;
        LogicECM.module.Base.Util.loadCSS([
            'css/lecm-errands/errands-request-change-duedate-form.css'
        ]);
        var rejectReasonElement = Dom.get(formId + "_prop_lecmErrandWf_requestDueDateChangeTask_1RejectReason");
        var resultElement = Dom.get(formId + "_prop_lecmErrandWf_requestDueDateChangeTask_1Result");
        if (resultElement) {
            var resultControl = resultElement.parentElement.parentElement;
            Dom.setStyle(resultControl, "padding-left", "190px");
            if (rejectReasonElement) {
                var rejectReasonControl = rejectReasonElement.parentElement.parentElement.parentElement;
                Event.on(resultElement, "change", function () {
                    Dom.setStyle(rejectReasonControl, "display", resultElement.value == "REJECTED" ? "block" : "none");
                });
            }
        }




    }
})();