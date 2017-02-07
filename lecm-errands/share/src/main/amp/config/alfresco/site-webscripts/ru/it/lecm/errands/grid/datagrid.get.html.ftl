<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-list-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/utils/search-queries.js"></@script>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="documents-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.Errands.DataGrid('${id}').setOptions({
                    usePagination: true,
                    pageSize: 20,
                    showExtendSearchBlock: true,
                    actions: [
                        {
                            type: "datagrid-action-link-${bubblingLabel!'errands'}",
                            id: "onActionEdit",
                            permission: "",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-${bubblingLabel!'errands'}",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        },
	                    {
		                    type:"datagrid-action-link-${bubblingLabel!'errands'}",
		                    id:"onActionDuplicate",
		                    permission:"create",
		                    label:"${msg("actions.duplicate-row")}"
	                    }
                    ],
                    allowCreate: false,
                    showActionColumn: true,
                    showCheckboxColumn: false,
                    bubblingLabel: "${bubblingLabel!"errands"}",
                    attributeForShow:"${attributeForShow!"cm:name"}",
                    excludeColumns: ["lecm-errands:is-important", "lecm-errands:baseDocString", "lecm-errands:is-expired"],
                    nowrapColumns: ["lecm-errands:number"]
                }).setMessages(${messages});

                var query = <#if query?? && (query?length > 0)>"${query}"<#else>""</#if>;

                var filter = _generatePropertyFilterStr(query.length > 0 ? query : LogicECM.module.Documents.FILTER , "${filterProperty}");
                var archiveFolders = _generatePathsFilterStr(LogicECM.module.Documents.SETTINGS.archivePath);

                var formId = <#if formId?? && (formId?length > 0)>
                                (("_" + "${formId}").split(" ").join("_"))
                            <#else>
                                ""
                            </#if>;
                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-errands:document",
                            datagridFormId: "datagrid" + formId,
                            nodeRef: LogicECM.module.Documents.SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:true,
                                trash: false
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: (filter.length > 0 ?  filter + " AND " : "")
                                        + '(PATH:"' + LogicECM.module.Documents.SETTINGS.draftPath + '//*"'
                                        + ' OR PATH:"' + LogicECM.module.Documents.SETTINGS.documentPath + '//*"'
                                        + ((archiveFolders.length > 0)? (" OR " + archiveFolders + "") : "") + ')'

                            }
                        },
                        bubblingLabel: "${bubblingLabel!"errands"}"
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
