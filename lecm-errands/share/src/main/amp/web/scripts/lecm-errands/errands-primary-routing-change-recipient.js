(function()
{
    if (YAHOO.Bubbling.addLayer("primaryRoutingRecipientChanged")) {
        YAHOO.Bubbling.on("primaryRoutingRecipientChanged", onPrimaryRoutingRecipientChanged);
    }

    function onPrimaryRoutingRecipientChanged(layer, args) {
        var formId = args[1].formId;

        if (formId) {
            var recipientValue = null;
            if (args[1].selectedItems) {
                var keys = Object.keys(args[1].selectedItems);
                if (keys.length == 1) {
                    recipientValue = keys[0];
                }
            }

            if (recipientValue) {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isEmployeeHasBusinessRole",
                        dataObj: {
                            employeeNodeRef: recipientValue,
                            roleId: 'RVZ'
                        },
                        successCallback: {
                            fn: function (response) {
                                var additionalFilter;
                                if (response.json) {
                                    additionalFilter = "@lecm\\-errands\\-dic\\:errand\\-type\\-manual\\-selection:true AND @lecm\\-errands\\-dic\\:errand\\-type\\-launch\\-review:true"
                                } else {
                                    additionalFilter = "@lecm\\-errands\\-dic\\:errand\\-type\\-manual\\-selection:true AND @lecm\\-errands\\-dic\\:errand\\-type\\-launch\\-review:false"
                                }

                                LogicECM.module.Base.Util.reInitializeControl(formId, "lecm-errands:type-assoc", {
                                    additionalFilter: additionalFilter
                                });
                            }
                        },
                        failureMessage: {
                            fn: function (response) {
                                alert(response.responseText);
                            }
                        }
                    });
            }
        }
    }
})();
