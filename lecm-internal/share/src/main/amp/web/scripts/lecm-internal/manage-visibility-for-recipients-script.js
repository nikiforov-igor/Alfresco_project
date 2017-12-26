(function () {
    var Dom = YAHOO.util.Dom;
    var Selector = YAHOO.util.Selector;
    YAHOO.Bubbling.on("recipientsVisibilityManagerLoaded", process);

    function process(layer, args) {
        var fieldsToHide = args[1].fieldsToHide;
        var formID = args[1].formId;
        var documentRef = args[1].nodeRef;
        var documentStatuses = args[1].documentStatuses ? args[1].documentStatuses : "Направлен";

        if (fieldsToHide && formID && documentRef) {
            fieldsToHide = fieldsToHide.split(",");

            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if (response && response.json.nodeRef) {
                            var currentUserNodeRef = response.json.nodeRef;
                            Alfresco.util.Ajax.jsonPost({
                                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                dataObj: {
                                    nodeRef: documentRef,
                                    substituteString: "{lecm-statemachine:status}[[SEPARATOR]]{lecm-eds-document:recipients-assoc-ref}"
                                },
                                successCallback: {
                                    scope: this,
                                    fn: function (response) {
                                        if (response && response.json.formatString) {
                                            var responseFields = response.json.formatString.split("[[SEPARATOR]]");
                                            var currentDocumentStatus = responseFields[0];
                                            var recipents = responseFields[1];
                                            if (currentDocumentStatus && documentStatuses.indexOf(currentDocumentStatus) != -1 &&
                                                recipents && currentUserNodeRef && recipents.indexOf(currentUserNodeRef) != -1) {

                                                Alfresco.util.Ajax.request(
                                                    {
                                                        url: Alfresco.constants.PROXY_URI + "lecm/eds/global-settings/api/getSettingsNode",
                                                        successCallback: {
                                                            scope: this,
                                                            fn: function (response) {
                                                                var oResults = eval("(" + response.serverResponse.responseText + ")");
                                                                if (oResults && oResults.isHideProps) {
                                                                    hideFields(fieldsToHide, formID);
                                                                }
                                                            }
                                                        },
                                                        failureMessage: Alfresco.util.message("message.details.failure"),
                                                        scope: this
                                                    });
                                            }
                                        }
                                    }
                                },
                                failureMessage: Alfresco.util.message("message.details.failure"),
                                scope: this
                            });
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.details.failure"),
                scope: this
            });
        }
    }

    function hideFields(fieldsToHide, formID) {
        var form = Dom.get(formID + "-form-container");
        if (form) {
            fieldsToHide.forEach(function (field) {
                var els = Selector.query("[id^='" + formID + "_" + field + "'][class*='control']", form);
                if (els) {
                    els.forEach(function(el) {
                        Dom.setStyle(el, "display", "none");
                        Dom.addClass(el, "hidden");
                    });
                }
            });
        }
    }
})();