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
                if (currentOrganization == null) {
                    currentOrganization = organization.nodeRef;
                }
                
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + '/lecm/orgstructure/api/getUnitByOrg',
                    dataObj: {
                        nodeRef: currentOrganization
                    },
                    successCallback: {
                        scope: this,
                        fn: function (response) {
                            var unit = new Alfresco.util.NodeRef(response.json.nodeRef);
                            LogicECM.module.Base.Util.readonlyControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc", false);
                            YAHOO.util.Event.onAvailable(LogicECM.module.Base.Util.getComponentReadyElementId(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc"), function() {
                                LogicECM.module.Base.Util.reInitializeControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc", {
                                    additionalFilter: '@lecm\\-orgstr\\-aspects\\:linked\\-organization\\-assoc\\-ref:\"' + organization.nodeRef + '\" AND NOT(@sys\\:node\\-uuid:\"' + unit.id + '\")'
                                });
                            }, this);
                            currentOrganization = organization.nodeRef;

                            YAHOO.Bubbling.fire("routeOrganizationSelected", {
                                organization: currentOrganization
                            });
                        }
                        
                    },
                    failureMessage: Alfresco.util.message('message.failure')
                });
            } else {
                currentOrganization = null;
                LogicECM.module.Base.Util.readonlyControl(formId, "lecmWorkflowRoutes:routeOrganizationUnitAssoc", true);
                YAHOO.Bubbling.fire("routeOrganizationSelected", {
                    organization: currentOrganization
                });
            }
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
        LogicECM.module.Base.Util.readonlyControl(formId, "lecmWorkflowRoutes:routeOrganizationAssoc", currentState.unit || currentState.stages);
    }
})();