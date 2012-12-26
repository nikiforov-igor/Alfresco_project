<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="subscriptions-type-grid">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
            <!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
            <script type="text/javascript">//<![CDATA[
            function createDatagrid() {
                new LogicECM.module.Base.DataGrid('${id}').setOptions(
                        {
                            usePagination:true,
//                            showExtendSearchBlock:true,
                            actions: [
                                {
                                    type:"action-link-${id}",
                                    id:"onActionEdit",
                                    permission:"edit",
                                    label:"${msg("actions.edit")}"
                                }
                                <#--{-->
                                    <#--type:"action-link-${id}",-->
                                    <#--id:"onActionVersion",-->
                                    <#--permission:"edit",-->
                                    <#--label:"${msg("actions.version")}"-->
                                <#--}-->
							<#--{-->
							<#--type:"action-link-${id}",-->
							<#--id:"onActionDelete",-->
							<#--permission:"delete",-->
							<#--label:"${msg("actions.delete-row")}",-->
							<#--evaluator: function (rowData) {-->
							<#--var itemData = rowData.itemData;-->
							<#--return itemData["assoc_lecm-orgstr_employee-main-position"] == undefined ||-->
							<#--itemData["assoc_lecm-orgstr_employee-main-position"].value.length == 0;-->
							<#--}-->
							<#--}-->
                            ],
						<#--bubblingLabel: "${id}",-->
                            showCheckboxColumn: false
//                            attributeForShow:"lecm-orgstr:employee-last-name"
                        }).setMessages(${messages});
            }

            function init() {
                createDatagrid();
            }

            YAHOO.util.Event.onDOMReady(init);
            //]]></script>
		</@grid.datagrid>
        </div>
    </div>
</div>
