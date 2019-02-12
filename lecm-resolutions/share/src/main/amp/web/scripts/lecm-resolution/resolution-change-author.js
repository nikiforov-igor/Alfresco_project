(function() {
    var Dom = YAHOO.util.Dom;
    var formId;

    YAHOO.Bubbling.on('authorChanged', reInit);
    YAHOO.Bubbling.on('afterFormRuntimeInit', onAfterFormRuntimeInit);

    function reInit(layer, args) {
        var obj = args[1];
        var nodeRef;
        formId = obj.formId;
        if (obj.selectedItems) {
            for (var prop in obj.selectedItems) {
                nodeRef = prop;
                break;
            }
        }

        if (nodeRef && formId) {
            doReInit(formId, nodeRef, true);
        }
    }

    /**
     * Переинициализация формы с поручениями
     *
     * @param formId
     * @param initiatorRef  - автор резолюции
     * @param refreshValue
     * @param refreshFormId - форма с поручениями, которую нужно переинициализировать,
     * если не указано значение, то обновляются все формы с поручениями
     */
    function doReInit(formId, initiatorRef, refreshValue, refreshFormId) {
        var url = 'lecm/errands/executorsByInitiator/' + Alfresco.util.NodeRef(initiatorRef).id + '/picker';

        YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
            formId: formId,
            fieldId: "lecm-resolutions:errands-json",
            subFieldId: "lecm-errands:executor-assoc",
            options: {
                childrenDataSource: url,
                resetValue: refreshValue
            },
            refreshFormId: refreshFormId
        });

        YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
            formId: formId,
            fieldId: "lecm-resolutions:errands-json",
            subFieldId: "lecm-errands:coexecutors-assoc",
            options: {
                childrenDataSource: url,
                resetValue: refreshValue
            },
            refreshFormId: refreshFormId
        });
    }

    function onAfterFormRuntimeInit(layer, args) {
        var multiFormComponents = Alfresco.util.ComponentManager.find({name: "LogicECM.module.eds.MultiFormControl"});

        if (formId && multiFormComponents && multiFormComponents.length) {
            var authorResolutionRef = Dom.get(formId + "_assoc_lecm-resolutions_author-assoc").value;
            var multiFormComponent = multiFormComponents[0];

            if (args[1] && args[1].runtime && args[1].runtime.formId.indexOf(formId + "_prop_lecm-resolutions_errands-json-line-") == 0
                && authorResolutionRef && multiFormComponent.isClickAddButton) {

                doReInit(formId, authorResolutionRef, true, args[1].runtime.formId);

            }
        }
    }
})();