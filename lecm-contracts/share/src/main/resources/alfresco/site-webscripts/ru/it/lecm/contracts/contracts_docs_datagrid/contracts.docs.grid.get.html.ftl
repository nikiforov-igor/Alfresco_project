<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.Contracts.DocsDataGrid('${id}').setOptions({
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
                    showCheckboxColumn: false,
                    bubblingLabel: "${bubblingLabel!"contracts"}",
                    attributeForShow:"cm:name"
                }).setMessages(${messages});

                var filter = generateFilterStr(LogicECM.module.Contracts.FILTER);

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-additional-document:additionalDocument",
                            nodeRef: LogicECM.module.Contracts.SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:true
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: (filter.length > 0 ? " (" + filter + " ) AND " : "")
                                        + '(+PATH:"' + LogicECM.module.Contracts.SETTINGS.draftPath + '//*"'
                                        + ' OR +PATH:"' + LogicECM.module.Contracts.SETTINGS.documentPath + '//*")'

                            }
                        },
                        bubblingLabel: "${bubblingLabel!"contracts"}"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

            function generateFilterStr(filter) {
                if (filter) {
                    var re = /\s*,\s*/;
                    var statuses = filter.split(re);

                    var resultFilter = "";

                    for (var i = 0; i < statuses.length; i++) {
                        if (resultFilter.length > 0) {
                            resultFilter += " OR ";
                        }
                        resultFilter += "+lecm\\-statemachine:status:\'" + statuses[i] + "\'";
                    }
                    return resultFilter;
                }
                return "";
            }

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
