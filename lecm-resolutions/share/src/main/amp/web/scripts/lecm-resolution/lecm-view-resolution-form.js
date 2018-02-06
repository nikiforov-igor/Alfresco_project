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
        if (baseDocument && baseDocument.value) {
            showSet(formId, "reviewers-hidden");

            if (nodeRef) {
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
                                if (status == Alfresco.util.message("lecm.resolutions.statemachine-status.draft") ||
                                    status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-rework") ||
                                    status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-approvment")) {
                                    showSet(formId, "reviewers-list-hidden");
                                } else if (status == Alfresco.util.message("lecm.resolutions.statemachine-status.on-execution") ||
                                    status == Alfresco.util.message("lecm.resolutions.statemachine-status.completec")) {
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