(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Selector = YAHOO.util.Selector;
    var formId;
    if (Bubbling.addLayer("registerORDWFScriptLoaded")) {
        Bubbling.on('registerORDWFScriptLoaded', init);
    }

    function init(layer, args) {
        formId = args[1].formId;
        Event.onContentReady(formId + "-form-submit-button", function () {
            var submitButtonElement = Dom.get(formId + "-form-submit-button");
            submitButtonElement.innerHTML = Alfresco.util.message("ord.register.dialog.button.continue");
        });
        processMessage();
    }

    function processMessage() {
        Event.onContentReady(formId + "_assoc_packageItems-added", function () {
            var nodeRef = Dom.get(formId + "_assoc_packageItems-added").value;
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/ord/register/validate",
                dataObj: {
                    nodeRef: nodeRef
                },
                successCallback: {
                    fn: function (response) {
                        var haveNotPointsWithController = response.json.haveNotPointsWithController;
                        var haveNotPointsWithDueDate = response.json.haveNotPointsWithDueDate;
                        var haveNotPointsWithControllerAndDueDate = response.json.haveNotPointsWithControllerAndDueDate;
                        var haveNotPoints = false;
                        if (response.json.haveNotPoints) {
                            haveNotPoints = true;
                        }
                        Selector.query(".form-field", Dom.get(formId), true).innerHTML = haveNotPoints ? Alfresco.util.message("ord.register.dialog.haveNotPoints.message") : haveNotPointsWithController ? Alfresco.util.message("ord.register.dialog.haveNotPointsWithController.message") :
                            haveNotPointsWithDueDate ? Alfresco.util.message("ord.register.dialog.haveNotPointsWithDueDate.message") : haveNotPointsWithControllerAndDueDate ?
                                Alfresco.util.message("ord.register.dialog.haveNotPointsWithControllerAndDueDate.message") : Alfresco.util.message("ord.register.dialog.common.message");

                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }

})();