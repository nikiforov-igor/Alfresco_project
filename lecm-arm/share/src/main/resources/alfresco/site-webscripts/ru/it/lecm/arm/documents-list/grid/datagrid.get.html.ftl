<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<#assign filtersId = "arm-filters-toolbar-" + id/>

<div id="${filtersId}">
<@comp.baseToolbar filtersId false false false>
</@comp.baseToolbar>
</div>

<script type="text/javascript">//<![CDATA[
function initFilters() {
    new LogicECM.module.ARM.Filters("${filtersId}").setMessages(${messages}).setOptions({
        bubblingLabel: "documents-arm"
    });
}
YAHOO.util.Event.onContentReady("${filtersId}", initFilters);
//]]></script>

<div class="yui-t1" id="arm-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.ARM.DataGrid('${id}').setOptions({
                    usePagination: true,
                    useDynamicPagination:true,
                    pageSize: 20,
                    showExtendSearchBlock: false,
                    actions: [],
                    allowCreate: false,
                    showActionColumn: false,
                    showCheckboxColumn: true,
                    bubblingLabel: "documents-arm"
                }).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-document:base",
                            datagridFormId: "datagrid-arm",
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

			function initArmGrid() {
				createDatagrid();
			}

			YAHOO.util.Event.onDOMReady(initArmGrid);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
