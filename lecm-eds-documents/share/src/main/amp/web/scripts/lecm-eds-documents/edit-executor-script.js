(function () {
    var Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    Bubbling.on('editExecutorCreateFormScriptLoaded', checkPermission);
    Bubbling.on('editExecutorEditFormScriptLoaded', checkPermission);

    function checkPermission(layer, args) {
        var formId = args[1].formId;
        var componentReadyElId = LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-eds-document:executor-assoc");
        Event.onContentReady(componentReadyElId, function () {
            LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", true);

            /*1. сотрудник, включенный в роль Выбирающий Исполнителя*/
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
                dataObj: {
                    roleId: "DA_CHOOSER_EXECUTOR"
                },
                successCallback: {
                    fn: function (response) {
                        var isCurrentEmployeeHasBusinessRole = response.json;
                        if (isCurrentEmployeeHasBusinessRole) {
                            /*2. сотрудник, являющийся Составителем документа (для формы редактирования)*/
                            if (layer == "editExecutorEditFormScriptLoaded") {
                                Alfresco.util.Ajax.jsonGet({
                                    url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
                                    successCallback: {
                                        fn: function (response) {
                                            if (response && response.json.nodeRef) {
                                                var currentUser = response.json.nodeRef;
                                                var form = Alfresco.util.ComponentManager.get(formId);
                                                if (form) {
                                                    var docRef = form.options.nodeRef;
                                                    if (docRef) {
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
                                                                            LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", false);
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            failureMessage: Alfresco.util.message("message.failure")
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    failureMessage: Alfresco.util.message("message.failure")
                                });
                            } else {
                                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-eds-document:executor-assoc", false);
                            }
                        }
                    }
                },
                failureMessage: Alfresco.util.message("message.failure")
            });
        });
    }
})();