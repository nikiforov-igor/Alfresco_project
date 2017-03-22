(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Selector = YAHOO.util.Selector;

    if (Bubbling.addLayer("ordItemViewFormScriptLoaded")) {
        Bubbling.on('ordItemViewFormScriptLoaded', processField);
    }

    function processField(layer, args) {
        var formId = args[1].formId;
        var formContainer = Dom.get(formId + "-form-container");
        var formButtons = Selector.query(".ft .button-group", formContainer.parentElement.parentElement, true);
        var itemNodeRef = args[1].nodeRef;
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
            successCallback: {
                fn: function (response) {
                    if (response && response.json.nodeRef) {
                        var currentUser = response.json.nodeRef;
                        Alfresco.util.Ajax.jsonPost({
                            url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                            dataObj: {
                                nodeRef: itemNodeRef,
                                substituteString: "{lecm-ord-table-structure:item-status-assoc-text-content},{lecm-ord-table-structure:controller-assoc-ref},{lecm-ord-table-structure:executor-assoc-ref},{lecm-ord-table-structure:report-required},{../..lecm-ord-table-structure:items-assoc/lecm-ord:controller-assoc-ref}"
                            },
                            successCallback: {
                                fn: function (response) {
                                    if (response && response.json.formatString) {
                                        var data = response.json.formatString.split(",");
                                        var itemStatus = data[0];
                                        var itemController = data[1];
                                        var executor = data[2];
                                        var report_required = data[3] == "true";
                                        var ordController = data[4];
                                        if (itemStatus == "На исполнении") {
                                            var actionButtonElement = Selector.query("." + formId + "-form-panel.buttons.hidden1 > span", formContainer, true);
                                            var closeViewFormButton = formButtons.firstElementChild;
                                            if (currentUser == executor && !report_required) {
                                                actionButtonElement.firstElementChild.firstElementChild.innerHTML = Alfresco.util.message("ord.item.execute.button");
                                                formButtons.insertBefore(actionButtonElement, closeViewFormButton);
                                                Event.addListener(actionButtonElement, "click", function () {
                                                    Bubbling.fire("onActionExecutePoint", {
                                                        nodeRef: itemNodeRef
                                                    });
                                                    closeViewFormButton.click();
                                                });
                                            } else if ((currentUser == itemController && itemController != executor) || (currentUser == ordController && ordController != executor)) {
                                                actionButtonElement.firstElementChild.firstElementChild.innerHTML = Alfresco.util.message("ord.item.complete.button");
                                                formButtons.insertBefore(actionButtonElement, closeViewFormButton);
                                                Event.addListener(actionButtonElement, "click", function () {
                                                    Bubbling.fire("onActionCompletePoint", {
                                                        nodeRef: itemNodeRef
                                                    });
                                                    closeViewFormButton.click();
                                                });

                                            }
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
            },
            failureMessage: Alfresco.util.message("message.details.failure"),
            scope: this
        });

    }
})();