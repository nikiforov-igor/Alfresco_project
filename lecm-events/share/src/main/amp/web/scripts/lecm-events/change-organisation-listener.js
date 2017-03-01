/* global Alfresco, YAHOO, LogicECM */

(function () {
	var componentReadyFormId, controlReadyId,
		forms = Alfresco.util.ComponentManager.find({
			name: 'Alfresco.FormUI'
		}),
        currentOrganization = null,
		resourceForms = forms.filter(function (form) {
			var isProperForm = form.options.fields && form.options.fields.some(function (field) {
				return field.id === 'change-organisation-script';
			});
			return isProperForm ? form : null;
		});

	if (resourceForms && resourceForms.length) {
		componentReadyFormId = resourceForms[0].id.replace("-form", "");
	}
	controlReadyId = LogicECM.module.Base.Util.getComponentReadyElementId(componentReadyFormId, "lecm-events-dic:resources-responsible-assoc");

    YAHOO.util.Event.onAvailable(controlReadyId, function () {
        YAHOO.Bubbling.on("organisationSelected", function (layer, args) {
            var organization,
                selectedItems = args[1].selectedItems,
                formId = args[1].formId;

            if (selectedItems) {
                organization = selectedItems[Object.keys(selectedItems)[0]];
            }

            if (formId) {
                if (organization && organization.nodeRef) {
                    if (currentOrganization == null) {
                        currentOrganization = organization.nodeRef;
                    }
                    LogicECM.module.Base.Util.enableControl(formId, "lecm-events-dic:resources-responsible-assoc");
                    LogicECM.module.Base.Util.reInitializeControl(formId, 'lecm-events-dic:resources-responsible-assoc', {
                        childrenDataSource: 'lecm/employees/EVENTS_RESPONSIBLE_FOR_RESOURCES/byOrg/' + organization.nodeRef.replace('://', '/') + '/picker',
                        resetValue: currentOrganization != organization.nodeRef
                    });
                    currentOrganization = organization.nodeRef;
                } else {
                    currentOrganization = null;
                    LogicECM.module.Base.Util.reInitializeControl(formId, 'lecm-events-dic:resources-responsible-assoc', {
                        resetValue: true
                    });
                    LogicECM.module.Base.Util.disableControl(formId, "lecm-events-dic:resources-responsible-assoc");
                }
            }
        });
    });
})();
