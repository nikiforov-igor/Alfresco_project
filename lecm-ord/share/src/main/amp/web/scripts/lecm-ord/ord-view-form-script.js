(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Selector = YAHOO.util.Selector;

    if (Bubbling.addLayer("ordViewFormScriptLoaded")) {
        Bubbling.on('ordViewFormScriptLoaded', processField);
    }

    function processField(layer, args) {
        var formId = args[1].formId;
        var itemNodeRef = args[1].nodeRef;
        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
            dataObj: {
                nodeRef: itemNodeRef,
                substituteString: "{lecm-statemachine:status}"
            },
            successCallback: {
                fn: function (response) {
                    if (response && response.json.formatString) {
                        var status = response.json.formatString;
                        var statusOk = status == "На исполнении" || status == "Работа завершена";
                        if (!statusOk) {
                            Event.onContentReady(formId + "_prop_lecm-eds-aspect_execution-state", function () {
                                Event.onContentReady(formId + "_prop_lecm-review-ts_doc-review-state", function () {
                                    var executionStateControl = Dom.get(formId + "_prop_lecm-eds-aspect_execution-state-cntrl");
                                    if (executionStateControl) {
                                        Dom.removeClass(executionStateControl, "hidden");
                                    }
                                    var reviewStateControl = Dom.get(formId + "_prop_lecm-review-ts_doc-review-state-cntrl");
                                    if (reviewStateControl) {
                                        Dom.addClass(reviewStateControl, "hidden");
                                    }
                                });
                            });
                        }
                    }
                },
                scope: this
            },
            failureMessage: Alfresco.util.message("message.details.failure"),
            scope: this
        });
    }
})();