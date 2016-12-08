(function() {
	YAHOO.Bubbling.on('osUnitCopyItemChanged', onChanged);

	function onChanged(layer, args) {
		YAHOO.Bubbling.unsubscribe('osUnitCopyItemChanged', onChanged);
		var obj = args[1];

		YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Нельзя скопировать в выбранный раздел',
				fieldId: obj.formId + '_' + obj.fieldId,
				handler: validationHandler.bind(obj),
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