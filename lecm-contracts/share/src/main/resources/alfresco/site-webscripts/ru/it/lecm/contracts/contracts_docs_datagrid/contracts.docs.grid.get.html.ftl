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
                    useDynamicPagination:true,
                    pageSize: 20,
                    showExtendSearchBlock: false,
                    actions: [
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts-documents'}",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts-documents'}",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        }
                    ],
                    allowCreate: false,
                    showActionColumn: true,
                    showCheckboxColumn: false,
                    bubblingLabel: "${bubblingLabel!"contracts-documents"}",
                    attributeForShow:"lecm-additional-document:number"
                }).setMessages(${messages});

                var filter = generateFilterStr(LogicECM.module.Contracts.FILTER);
                var archiveFolders = generateArchiveFoldersStr(LogicECM.module.Contracts.SETTINGS.archivePath);

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
                                        + '(PATH:"' + LogicECM.module.Contracts.SETTINGS.draftPath + '//*"'
                                        + ' OR PATH:"' + LogicECM.module.Contracts.SETTINGS.documentPath + '//*"'
                                        + (archiveFolders.length > 0 ? " OR " + archiveFolders +"" : "") + ')'

                            }
                        },
                        bubblingLabel: "${bubblingLabel!"contracts-documents"}"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

            function generateFilterStr(filter) {
                if (filter) {
                    var re = /\s*,\s*/;
                    var types = filter.split(re);

                    var resultFilter = "";

                    for (var i = 0; i < types.length; i++) {
                        if (resultFilter.length > 0) {
                            resultFilter += " OR ";
                        }
                        resultFilter += "+lecm\\-additional\\-document:additionalDocumentType\\-text\\-content:\'" + types[i] + "\'";
                    }
                    return resultFilter;
                }
                return "";
            }

            function generateArchiveFoldersStr(paths) {
                if (paths) {
                    var archPaths = paths.split(",");
                    var result = "";
                    for (var i = 0; i < archPaths.length; i++) {
                        if (result.length > 0) {
                            result += " OR ";
                        }
                        result += 'PATH:"' + archPaths[i] + '//*"' ;
                    }
                    return result;
                }
                return "";
            }

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
