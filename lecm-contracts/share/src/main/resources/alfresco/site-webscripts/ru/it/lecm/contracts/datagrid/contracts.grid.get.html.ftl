<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.Contracts.DataGrid('${id}').setOptions({
                    usePagination: true,
                    pageSize: 20,
                    showExtendSearchBlock: true,
                    actions: [
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts'}",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts'}",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        }
                    ],
                    allowCreate: false,
                    showActionColumn: true,
                    showCheckboxColumn: true,
                    bubblingLabel: "${bubblingLabel!"contracts"}",
                    attributeForShow:"cm:name"
                }).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-contract:document",
                            nodeRef: LogicECM.module.Contracts.SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:true
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: '+PATH:"' + LogicECM.module.Contracts.SETTINGS.draftPath + '//*"'
                                        + ' OR +PATH:"' + LogicECM.module.Contracts.SETTINGS.documentPath + '//*"'
                            }
                        },
                        bubblingLabel: "${bubblingLabel!"contracts"}"
                    });
                });
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
