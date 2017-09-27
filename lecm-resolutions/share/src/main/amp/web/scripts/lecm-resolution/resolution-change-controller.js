(function() {
    var controllerChangedPromptVisible = false;
    var lastSelectedResolutionController = null;

    YAHOO.Bubbling.on('resolutionControllerChanged', onResolutionControllerChange, {editForm: false});
    YAHOO.Bubbling.on('resolutionEditControllerChanged', onResolutionControllerChange, {editForm: true});

    function onResolutionControllerChange(layer, args, param) {
        var controllerValue = "";
        if (args[1].selectedItems) {
            var keys = Object.keys(args[1].selectedItems);
            if (keys.length == 1) {
                controllerValue = keys[0];
            }
        }

        var recipientField = Dom.get(args[1].formId + '_prop_lecm-resolutions_closers');
        if (recipientField && recipientField.tagName == "SELECT") {
            if (controllerValue) {
                recipientField.options[1].disabled = false;
                recipientField.options[2].disabled = false;
            } else {
                recipientField.options[1].disabled = true;
                recipientField.options[2].disabled = true;
                recipientField.selectedIndex = 0;
            }
        }

        var errandsCount = Dom.get(args[1].formId + '_prop_lecm-resolutions_errands-json-count');
        if (errandsCount && parseInt(errandsCount.value) && !controllerChangedPromptVisible
            && (!param.editForm || (lastSelectedResolutionController !== null && lastSelectedResolutionController !== controllerValue))) {

            controllerChangedPromptVisible = true;
            Alfresco.util.PopupManager.displayPrompt({
                title: Alfresco.util.message('title.resolution.controller.change'),
                text: Alfresco.util.message('message.resolution.controller.change'),
                close: false,
                modal: true,
                buttons: [
                    {
                        text: Alfresco.util.message('button.yes'),
                        handler: function () {
                            YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
                                formId: args[1].formId,
                                fieldId: "lecm-resolutions:errands-json",
                                subFieldId: "lecm-errands:controller-assoc",
                                options: {
                                    currentValue: controllerValue,
                                    defaultValue: controllerValue,
                                    resetValue: !controllerValue.length
                                }
                            });
                            this.destroy();
                            controllerChangedPromptVisible = false;
                        }
                    }, {
                        text: Alfresco.util.message('button.no'),
                        handler: function () {
                            this.destroy();
                            controllerChangedPromptVisible = false;
                        },
                        isDefault: true
                    }]
            });
        }
        lastSelectedResolutionController = controllerValue;
    }
})();