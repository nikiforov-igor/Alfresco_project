(function() {
	YAHOO.Bubbling.on('osUnitCopyItemChanged', onChanged);

	var formArgs = {};

	function onChanged(layer, args) {
		var obj = args[1];
        formArgs = obj;
        
        var form = Alfresco.util.ComponentManager.find({id: (obj.formId + '-form')})[0];
        if (form && form.formsRuntime) {
            addValidation(null, [null, {
                action: "beforeFormRuntimeInit",
                component: form,
                decrepitate: false,
                eventGroup: obj.formId,
                flagged: false,
                runtime: form.formsRuntime,
                stop: false
            }]);
        } else {
            YAHOO.Bubbling.on("beforeFormRuntimeInit", addValidation);
        }
	}

    function addValidation(layer, args) {
        YAHOO.Bubbling.unsubscribe('beforeFormRuntimeInit', addValidation);

        YAHOO.Bubbling.fire('registerValidationHandler', {
            message: 'Нельзя скопировать в выбранный раздел',
            fieldId: formArgs.formId + '_' + formArgs.fieldId,
            handler: validationHandler.bind(formArgs),
            when: 'onchange'
        });
    }

	function validationHandler(field, args, event, form, silent, message) {
		var valid = false;

		var dialog = Alfresco.util.ComponentManager.find({id: this.formId})[0];
		var items = dialog.options.selectedItems.join();

		if(!field.value) {
			return false;
		}

		$.ajax({
			url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/canCopyUnits?" + "items=" + items + "&dest=" + field.value,
			context: this,
			success: function (response) {
					valid = response && response.canCopy;
				},
			async: false
		});

		return valid;
	}

})();