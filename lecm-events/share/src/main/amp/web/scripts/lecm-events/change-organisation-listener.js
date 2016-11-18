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

            var controls = Alfresco.util.ComponentManager.find({id: formId + "_assoc_lecm-events-dic_resources-responsible-assoc"});

            if (controls && controls.length) {
                var controlSelectedItems = Object.keys(controls[0].selectedItems);
                controlSelectedItems.forEach(function (item) {
                    delete this.selectedItems[item];
                }, controls[0]);
                controls[0].singleSelectedItem = null;
                controls[0].updateSelectedItems();
                controls[0].updateAddButtons();
                controls[0].updateFormFields();
            }

            if (organization && organization.nodeRef) {
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-events-dic:resources-responsible-assoc", false);
                YAHOO.util.Event.onAvailable(LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecm-events-dic:resources-responsible-assoc"), function() {
                    YAHOO.Bubbling.fire("refreshItemList", {
                        formId: formId,
                        fieldId: "lecm-events-dic:resources-responsible-assoc",
                        childrenDataSource: 'lecm/employees/EVENTS_RESPONSIBLE_FOR_RESOURCES/byOrg/' + organization.nodeRef.replace('://', '/') + '/picker'
                    });
                }, this);
            } else {
                LogicECM.module.Base.Util.readonlyControl(formId, "lecm-events-dic:resources-responsible-assoc", true);
            }
        }
    }

})();
