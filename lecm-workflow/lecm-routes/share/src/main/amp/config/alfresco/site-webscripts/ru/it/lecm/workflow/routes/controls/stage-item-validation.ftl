<script type='text/javascript'>
    (function () {
        var subscribed = false;

        YAHOO.Bubbling.on("beforeFormRuntimeInit", function (layer, args) {
            if (!subscribed) {
                var formUi = args[1].component,
                        formId = formUi.parentId,
                        fieldId = "${field.control.params.fieldId}",
                        simpleDialog = Alfresco.util.ComponentManager.get(formId),
                        routeStage = simpleDialog.options.templateRequestParams.destination,
                        routeOrganization = simpleDialog.options.templateRequestParams.routeOrganization;

                LogicECM.module.Base.Util.reInitializeControl(formId, fieldId, {
                    allowedNodesScript: "lecm/workflow/routes/getEmployeesForStage?stage=" + routeStage + (routeOrganization ? "&organization=" + routeOrganization : "")
                });
                subscribed = true;
            }
        });
    })();
</script>