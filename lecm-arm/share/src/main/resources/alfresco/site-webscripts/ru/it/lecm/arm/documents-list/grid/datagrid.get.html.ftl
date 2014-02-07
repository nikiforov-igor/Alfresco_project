<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.ARM.DataGrid('${id}').setOptions({
                    usePagination: true,
                    useDynamicPagination:true,
                    pageSize: 10,
                    showExtendSearchBlock: false,
                    actions: [],
                    allowCreate: false,
                    showActionColumn: false,
                    showCheckboxColumn: true,
                    bubblingLabel: "documents-arm",
                    excludeColumns: ["lecm-document:present-string"]
                }).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-document:base",
                            datagridFormId: "datagrid",
                            nodeRef: null,
                            actionsConfig:{
                                fullDelete:true,
                                trash: false
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: 'PATH:"' + LogicECM.module.ARM.SETTINGS.draftPath + '//*"'
                                    + ' OR PATH:"' + LogicECM.module.ARM.SETTINGS.documentPath + '//*"'
                            }
                        },
                        bubblingLabel: "documents-arm"
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
