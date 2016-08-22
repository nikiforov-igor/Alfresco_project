(function () {
    YAHOO.Bubbling.on("organisationSelected", onOrganisationSelected);

    function onOrganisationSelected(layer, args) {
        var organization = null;

        var selectedItems = args[1].selectedItems;
        if (selectedItems != null) {
            organization = selectedItems[Object.keys(selectedItems)[0]]
        }

        var formId = args[1].formId;

        if (formId != null) {
            if (organization && organization.nodeRef) {
                LogicECM.module.Base.Util.enableControl(formId, "lecm-events-dic:resources-responsible-assoc");
                YAHOO.util.Event.onAvailable(LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-events-dic:resources-responsible-assoc"), function() {
                    YAHOO.Bubbling.fire("refreshItemList", {
                        formId: formId,
                        fieldId: "lecm-events-dic:resources-responsible-assoc",
                        childrenDataSource: 'lecm/employees/EVENTS_RESPONSIBLE_FOR_RESOURCES/byOrg/' + organization.nodeRef.replace('://', '/') + '/picker'
                    });
                }, this);
            } else {
                LogicECM.module.Base.Util.disableControl(formId, "lecm-events-dic:resources-responsible-assoc");
            }
        }
    }

})();