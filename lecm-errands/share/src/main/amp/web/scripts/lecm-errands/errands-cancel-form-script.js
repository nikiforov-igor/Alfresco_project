(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;
    var formId;

    Bubbling.on('requestCancelTaskFormScriptLoaded', init);
    Bubbling.on('errandCancelFormScriptLoaded', init);
    Bubbling.on('errandsRequestCancelResultChanged', onResultValueChanged);

    function init(layer, args) {
        formId = args[1].formId;
        if (layer == "errandCancelFormScriptLoaded") {
            processCancelChildCheckbox("lecmErrandWf_cancel_1CancelChildren");
        } else if (layer == "requestCancelTaskFormScriptLoaded") {
            processCancelChildCheckbox("lecmErrandWf_requestCancelTask_1CancelChildren");
        }
    }

    function processCancelChildCheckbox(field) {
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
                            var cancelChildField = Dom.get(formId + "_prop_" + field);
                            if (cancelChildField) {
                                cancelChildField.value = false;
                                Dom.setStyle(cancelChildField.parentElement.parentElement.parentElement, "display", "none");
                            }
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }

    function onResultValueChanged(layer, args) {
        var formId = args[1].formId;
        var value = args[1].selectedValue;
        if (value == "CANCEL_ERRAND") {
            LogicECM.module.Base.Util.readonlyControl(formId, "lecmErrandWf:requestCancelTask_1CancelReason", false);
            LogicECM.module.Base.Util.enableControl(formId, "lecmErrandWf:requestCancelTask_1CancelChildren");
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1NewExecutor");
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1RejectReason");
        } else if (value == "REJECTED") {
            LogicECM.module.Base.Util.readonlyControl(formId, "lecmErrandWf:requestCancelTask_1CancelReason", true);
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1CancelChildren");
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1NewExecutor");
            LogicECM.module.Base.Util.enableControl(formId, "lecmErrandWf:requestCancelTask_1RejectReason");
        } else if (value == "CHANGE_EXECUTOR") {
            LogicECM.module.Base.Util.readonlyControl(formId, "lecmErrandWf:requestCancelTask_1CancelReason", true);
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1CancelChildren");
            LogicECM.module.Base.Util.disableControl(formId, "lecmErrandWf:requestCancelTask_1RejectReason");
            LogicECM.module.Base.Util.enableControl(formId, "lecmErrandWf:requestCancelTask_1NewExecutor");
        }
    }
})();