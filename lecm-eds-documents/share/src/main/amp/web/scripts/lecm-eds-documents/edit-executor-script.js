(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

    Bubbling.on('editExecutorCreateFormScriptLoaded', checkPermission);
    Bubbling.on('editExecutorEditFormScriptLoaded', checkPermission);

    function checkPermission(layer, args) {
        var formId = args[1].formId;
        var componentReadyElId = LogicECM.module.Base.Util.getComponentReadyElementId(formID, "lecm-eds-document:executor-assoc");
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
            dataObj: {
                roleId: "DA_CHOOSER_EXECUTOR"
            },
            successCallback: {
                fn: function (response) {
                    var isCurrentEmployeeHasBusinessRole = response.json;
                    if (!isCurrentEmployeeHasBusinessRole) {
                        Event.onContentReady(componentReadyElId, function () {
                            LogicECM.module.Base.Util.readOnlyControl(formId, "lecm-eds-document:executor-assoc", true);
                        });
                    } else {
                        if (layer = "editExecutorEditFormScriptLoaded") {
                            var docRef = Alfresco.util.ComponentManager.get(formID).options.nodeRef;
                            Alfresco.util.Ajax.jsonGet({
                                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                successCallback: {
                                    fn: function (response) {
                                        var me = response.config.scope;
                                        if (response && response.json.nodeRef) {
                                            var currentUser = response.json.nodeRef;
                                            Alfresco.util.Ajax.jsonPost({
                                                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                dataObj: {
                                                    nodeRef: docRef,
                                                    substituteString: "{lecm-document:author-assoc}"
                                                },
                                                successCallback: {
                                                    fn: function (response) {
                                                        var me = response.config.scope;
                                                        if (response && response.json.formatString) {
                                                            var documentCompiler = response.json.formatString;
                                                            if (!currentUser.equals(documentCompiler)) {
                                                                Event.onContentReady(componentReadyElId, function () {
                                                                    LogicECM.module.Base.Util.readOnlyControl(formId, "lecm-eds-document:executor-assoc", true);
                                                                });
                                                            }
                                                        }
                                                    }
                                                },
                                                failureMessage: Alfresco.util.message("message.failure")
                                            });
                                        }
                                    }
                                },
                                failureMessage: Alfresco.util.message("message.failure")
                            });

                        }
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });

    }
})();