/* global Alfresco, YAHOO, LogicECM */

(function () {
	var formId, controlReadyId,
		forms = Alfresco.util.ComponentManager.find({
			name: 'Alfresco.FormUI'
		}),
		resourceForms = forms.filter(function (form) {
			var isProperForm = form.options.fields && form.options.fields.some(function (field) {
				return field.id === 'change-organisation-script';
			});
			return isProperForm ? form : null;
		});

	if (resourceForms && resourceForms.length) {
		formId = resourceForms[0].id;
	}
	controlReadyId = LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-events-dic:resources-responsible-assoc");

	YAHOO.util.Event.onAvailable(controlReadyId, function () {
		YAHOO.Bubbling.on("organisationSelected", function (layer, args) {
			var organization, controls,
				selectedItems = args[1].selectedItems,
				formId = args[1].formId;

			if (selectedItems) {
				organization = selectedItems[Object.keys(selectedItems)[0]];
			}

			if (formId) {

				controls = Alfresco.util.ComponentManager.find({id: formId + "_assoc_lecm-events-dic_resources-responsible-assoc"});

				if (controls && controls.length) {
					Object.keys(controls[0].selectedItems).forEach(function (item) {
						delete this.selectedItems[item];
					}, controls[0]);
					controls[0].singleSelectedItem = null;
					controls[0].updateSelectedItems();
					controls[0].updateAddButtons();
					controls[0].updateFormFields();
				}

				if (organization && organization.nodeRef) {
					LogicECM.module.Base.Util.enableControl(formId, "lecm-events-dic:resources-responsible-assoc");
					YAHOO.Bubbling.fire("refreshItemList", {
						formId: formId,
						fieldId: "lecm-events-dic:resources-responsible-assoc",
						childrenDataSource: 'lecm/employees/EVENTS_RESPONSIBLE_FOR_RESOURCES/byOrg/' + organization.nodeRef.replace('://', '/') + '/picker'
					});
				} else {
					LogicECM.module.Base.Util.disableControl(formId, "lecm-events-dic:resources-responsible-assoc");
				}
			}
		});
	});
})();
