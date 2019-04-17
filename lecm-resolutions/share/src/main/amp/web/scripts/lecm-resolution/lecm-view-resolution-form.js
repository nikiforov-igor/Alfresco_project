(function () {
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

    Bubbling.on('resolutionViewFormScriptLoaded', init);

    function init(layer, args) {
        var formId = args[1].formId;
        var nodeRef = args[1].nodeRef;

        var baseDocument = Dom.get(formId + "_assoc_lecm-resolutions_base-document-assoc");
        var value = (baseDocument && baseDocument.value);
        if (value && nodeRef) {
            Alfresco.util.Ajax.jsonPost(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: value,
                        substituteString: "{@doc.hasAspect('lecm-review-ts:review-aspect')}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response.json && response.json.formatString && response.json.formatString == "true") {
                                showSet(formId, "reviewers-hidden");
                                    Alfresco.util.Ajax.jsonPost({
                                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                        dataObj: {
                                            nodeRef: nodeRef,
                                            substituteString: "{lecm-statemachine:status}"
                                        },
                                        successCallback: {
                                            fn: function (response) {
                                                if (response && response.json.formatString) {
                                                    var status = response.json.formatString;
                                                    if (status == "Черновик" || status == "На доработке" || status == "На утверждении" ||
                                                        status == Alfresco.util.message("lecm.resolutions.statemachine-status.draft") ||
                                                        status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-rework") ||
                                                        status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-approvment")) {
                                                        showSet(formId, "reviewers-list-hidden");
                                                    } else if (status == "На исполнении" || status == "Завершено" ||
                                                        status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-execution") ||
                                                        status == Alfresco.util.message("lecm.resolutions.statemachine-status.completed")) {
                                                        showSet(formId, "reviewers-table-hidden");
                                                    }
                                                }
                                            },
                                            scope: this
                                        },
                                        failureMessage: Alfresco.util.message("message.details.failure"),
                                        scope: this
                                    });
                            }
                        },
                        scope: this
                    }
                });
        }
    }

    function showSet(formId, setId) {
        var queryTemplate = 'div[class^=\"{formId}-form-panel {targetClass}\"]';
        var sets = Selector.query(Substitute(queryTemplate, {
            targetClass: setId,
            formId: formId
        }));

        if (sets && sets.length) {
            Dom.removeClass(sets[0], 'hidden1');
        }
    }
})();