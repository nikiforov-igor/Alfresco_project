(function () {
    YAHOO.Bubbling.on("routeOrganizationSelect", onRouteOrganizationSelect);
    YAHOO.Bubbling.on("routeUnitSelect", onRouteUnitSelect);
    YAHOO.Bubbling.on("routeStagesUpdate", onRouteStagesUpdate);

    var currentOrganization = null;
    var currentRoute = null;
    var currentState = {
        unit: false,
        stages:false
    };

    function onRouteOrganizationSelect(layer, args) {
        var organization = null;

        var selectedItems = args[1].selectedItems;
        if (selectedItems != null) {
            var keys = Object.keys(selectedItems);
            for (var i = 0; i < keys.length; i++) {
                organization = selectedItems[keys[i]];
            }
        }
        var formId = args[1].formId;

        if (formId != null) {
            if (organization && organization.nodeRef && organization.nodeRef.length > 0) {
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + '/lecm/orgstructure/api/getUnitByOrg',
                    dataObj: {
                        nodeRef: organization.nodeRef
                    },
                    successCallback: {
                        fn: function (response) {
                            var unit = new Alfresco.util.NodeRef(response.json.nodeRef);
                            LogicECM.module.Base.Util.enableControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc");
                            LogicECM.module.Base.Util.reInitializeControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc", {
                                additionalFilter: '@lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"' + organization.nodeRef + '\" AND NOT(@sys\\:node\\-uuid:\"' + unit.id + '\")',
                                resetValue:currentOrganization != organization.nodeRef
                            });
                            currentOrganization = organization.nodeRef;
                        }
                    },
                    failureMessage: Alfresco.util.message('message.failure')
                });
            } else {
                currentOrganization = null;
                LogicECM.module.Base.Util.reInitializeControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc", {
                    additionalFilter: 'PATH:\"//app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Структура_x0020_организации_x0020_и_x0020_сотрудников/cm:Организация/cm:Структура/cm:Холдинг/*//*\"',
                    resetValue: true,
                    currentValue: ""
                });
                LogicECM.module.Base.Util.disableControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc");
            }
            // обновим контрол с этапами
            YAHOO.Bubbling.fire("routeOrganizationSelected", {
                organization: currentOrganization
            });
        }
    }

    function onRouteUnitSelect(layer, args) {
        var formId = args[1].formId;
        if (formId !== null) {
            var selectedItems = args[1].selectedItems;
            if (selectedItems !== null) {
                var keys = Object.keys(selectedItems);
                currentState.unit = (keys !== null && keys.length > 0);
            } else {
                currentState.unit = false;
            }
            checkSelected(formId);
        }
    }

    function onRouteStagesUpdate(layer, args) {
        if (args !== null) {
            if (args[1].routeRef && args[1].routeRef.length > 0) {
                currentRoute = args[1].routeRef;
            }
            var formId = args[1].formId;
            if (currentRoute && currentRoute.length > 0 && currentRoute != 'undefined') {
                jQuery.ajax({
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/routes/isHasEmployees?routeRef=" + currentRoute,
                    type: "GET",
                    timeout: 30000,
                    async: false,
                    dataType: "json",
                    contentType: "application/json",
                    processData: false,
                    success: function (result) {
                        currentState.stages = (result && result.isHasEmployees);
                        checkSelected(formId);
                    },
                    error: function() {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "ERROR: can not perform field validation"
                        });
                    }
                });
            }
        }
    }

    function checkSelected(formId) {
        if (currentState.unit || currentState.stages) {
            LogicECM.module.Base.Util.disableControl(formId, "lecmWorkflowRoutes:routeOrganizationAssoc");
        } else {
            LogicECM.module.Base.Util.enableControl(formId, "lecmWorkflowRoutes:routeOrganizationAssoc");
        }
    }
})();