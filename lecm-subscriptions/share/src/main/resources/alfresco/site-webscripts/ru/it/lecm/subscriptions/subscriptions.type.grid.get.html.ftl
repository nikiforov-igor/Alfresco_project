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
                            usePagination: true,
                            actions: [
                                {
                                    type: "action-link-${id}",
                                    id: "onActionEdit",
                                    permission: "edit",
                                    label: "${msg("actions.edit")}"
                                },
                                {
                                    type: "action-link-${id}",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}"
                                }
                            ],
	                        bubblingLabel: "${bubblingLabel!''}",
                            showCheckboxColumn: true,
	                        attributeForShow:"cm:name",
	                        advSearchFormId: "${advSearchFormId!''}"
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
