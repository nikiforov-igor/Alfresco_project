<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign gridId = "group-actions"/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">

<div class="form-field with-grid group-actions-grid" id="group-actions-${controlId}">

<@grid.datagrid containerId false gridId+"form">
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                actions: [
                        {
                            type: "datagrid-action-link-group-actions-bubbling-label",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },{
                            type: "datagrid-action-link-group-actions-bubbling-label",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        }
                ],
                datagridMeta: {
                    itemType: "lecm-group-actions:action",
                    nodeRef: "${nodeRef!""}",
                    datagridFormId: "datagrid",
                    actionsConfig: {
                        fullDelete: "true"
                    },
                    sort: "lecm-group-actions:order|true"
                },
                showActionColumn: true,
                showCheckboxColumn: false,
                bubblingLabel: "group-actions-bubbling-label",
                allowCreate: true
            });
            datagrid.draw();
        });

    })();
    //]]></script>

</@grid.datagrid>
</div>