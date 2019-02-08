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
            doReInit(formId, nodeRef);
        }
    }

    function doReInit(formId, initiatorRef) {
        var url = 'lecm/errands/executorsByInitiator/' + Alfresco.util.NodeRef(initiatorRef).id + '/picker';

        YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
            formId: formId,
            fieldId: "lecm-resolutions:errands-json",
            subFieldId: "lecm-errands:executor-assoc",
            options: {
                childrenDataSource: url,
                resetValue: true
            }
        });

        YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
            formId: formId,
            fieldId: "lecm-resolutions:errands-json",
            subFieldId: "lecm-errands:coexecutors-assoc",
            options: {
                childrenDataSource: url,
                resetValue: true
            }
        });
    }

    function onAfterFormRuntimeInit(layer, args) {
        if (formId) {
            var authorResolutionRef = Dom.get(formId + "_assoc_lecm-resolutions_author-assoc").value;
            if (args[1] && args[1].runtime && args[1].runtime.formId.indexOf(formId + "_prop_lecm-resolutions_errands-json-line-") == 0 && authorResolutionRef) {
                doReInit(formId, authorResolutionRef)
            }
        }
    }
})();