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
        var componentReadyElId = LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-eds-document:executor-assoc");
        Event.onContentReady(componentReadyElId, function () {
            LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", true);
        });
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
            dataObj: {
                roleId: "DA_CHOOSER_EXECUTOR"
            },
            successCallback: {
                fn: function (response) {
                    var isCurrentEmployeeHasBusinessRole = response.json;
                    if (isCurrentEmployeeHasBusinessRole) {
                        Event.onContentReady(componentReadyElId, function () {
                            LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", false);
                        });
                    } else {
                        if (layer == "editExecutorEditFormScriptLoaded") {
                            var form = Alfresco.util.ComponentManager.get(formId);
                            if (form) {
                                var docRef = form.options.nodeRef;
                                if (docRef) {
                                    Alfresco.util.Ajax.jsonGet({
                                        url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                        successCallback: {
                                            fn: function (response) {
                                                if (response && response.json.nodeRef) {
                                                    var currentUser = response.json.nodeRef;
                                                    Alfresco.util.Ajax.jsonPost({
                                                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                                        dataObj: {
                                                            nodeRef: docRef,
                                                            substituteString: "{lecm-document:author-assoc-ref}"
                                                        },
                                                        successCallback: {
                                                            fn: function (response) {
                                                                if (response && response.json.formatString) {
                                                                    var documentCompiler = response.json.formatString;
                                                                    if (currentUser == documentCompiler) {
                                                                        Event.onContentReady(componentReadyElId, function () {
                                                                            LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", false);
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
                    }
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });

    }
})();