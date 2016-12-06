(function() {
    YAHOO.Bubbling.on('resolutionControllerChanged', onResolutionControllerChange);

    function onResolutionControllerChange(layer, args) {
        var recipientField = Dom.get(args[1].formId + '_prop_lecm-resolutions_closers');
        if (recipientField && recipientField.tagName == "SELECT") {
            if (args[1].selectedItems && Object.keys(args[1].selectedItems).length) {
                recipientField.options[1].disabled = false;
                recipientField.options[2].disabled = false;
            } else {
                recipientField.options[1].disabled = true;
                recipientField.options[2].disabled = true;
                recipientField.selectedIndex = 0;
            }
        }

        var errandsCount = Dom.get(args[1].formId + '_prop_lecm-resolutions_errands-json-count');
        if (errandsCount && parseInt(errandsCount.value)) {
            Alfresco.util.PopupManager.displayPrompt({
                title: Alfresco.util.message('title.resolution.controller.change'),
                text: Alfresco.util.message('message.resolution.controller.change'),
                close: false,
                modal: true,
                buttons: [
                    {
                        text: Alfresco.util.message('button.yes'),
                        handler: function () {
                            var controllerValue = "";
                            if (args[1].selectedItems) {
                                var keys = Object.keys(args[1].selectedItems);
                                if (keys.length == 1) {
                                    controllerValue = keys[0];
                                }
                            }

                            YAHOO.Bubbling.fire("reInitializeSubFormsControls", {
                                formId: args[1].formId,
                                fieldId: "lecm-resolutions:errands-json",
                                subFieldId: "lecm-errands:controller-assoc",
                                options: {
                                    currentValue: controllerValue,
                                    defaultValue: controllerValue
                                }
                            });
                            this.destroy();
                        }
                    }, {
                        text: Alfresco.util.message('button.no'),
                        handler: function () {
                            this.destroy();
                        },
                        isDefault: true
                    }]
            });
        }
    }
})();