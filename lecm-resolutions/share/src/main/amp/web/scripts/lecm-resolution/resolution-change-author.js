(function() {

    YAHOO.Bubbling.on('authorChanged', reInit);

    function reInit(layer, args) {
        var obj = args[1];
        var nodeRef;
        var formId = obj.formId;
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
})();