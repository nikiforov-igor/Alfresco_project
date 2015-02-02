<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/object-finder/lecm-object-finder.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-association-search.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-documents-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/utils/search-queries.js"></@script>

<#assign id = args.htmlid>
<#assign datagridId = "nomenclature">
<#assign additionalDatagridId = id + "-list-datagrid-additional">
<#assign bubblingId = datagridId>



<@grid.datagrid datagridId false>
	<script type="text/javascript">//<![CDATA[
		YAHOO.util.Event.onContentReady ('${id}', function () {
			var js = ['scripts/os/nomenclature/nomenclature-datagrid.js'];
			var css = ['css/os/nomenclature-datagrid.css'];
			LogicECM.module.Base.Util.loadResources(js, css, process);

			function process(){

				var datagrid = new LogicECM.module.Nomenclature.Datagrid('${datagridId}');

				datagrid.setOptions({
					bubblingLabel: '${bubblingId}',
					createItemBtnMsg: 'Добавить сотрудника',
					createFormTitleMsg: 'Сотрудник',
					usePagination: false,
					showExtendSearchBlock: false,
					showCheckboxColumn: true,
					forceSubscribing: true,
					allowCreate: false,
					showActionColumn: true,
					dataSource: "/lecm/os/nomenclature/datasource/ndList",
					sort: 'os-aspects:sort-value',
					excludeColumns: [
						"lecm-os:nomenclature-unit-section-status",
						"lecm-os:nomenclature-case-status",
						"lecm-os:nomenclature-year-section-status",
						"lecm-os:nomenclature-case-to-archive",
						"lecm-os:nomenclature-year-section-status-fake",
						"os-aspects:sort-value"
					],
					actions: [
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionDeleteNDSection",
							permission:"edit",
							confirmFunction: datagrid.deleteNDSection_Promt,
							label:"${msg("actions.delete-row")}",
							evaluator: datagrid.deleteNDSectionEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionCopyNDSection",
							permission:"edit",
							label:"${msg("actions.copy-row")}",
							evaluator: datagrid.deleteNDSectionEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionMoveNDSection",
							permission:"edit",
							label:"${msg("actions.move-row")}",
							evaluator: datagrid.deleteNDSectionEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionCopyNomenclatureCase",
							permission:"edit",
							label:"${msg("actions.copy-row")}",
							evaluator: datagrid.nomenclatureCaseEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionMoveNomenclatureCase",
							permission:"edit",
							label:"${msg("actions.move-row")}",
							evaluator: datagrid.nomenclatureCaseEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionMarkToDeleteNomenclatureCase",
							permission:"edit",
							label:"${msg("actions.mark-to-destroy")}",
							evaluator: datagrid.markToDeleteEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionDestroyNomenclatureCase",
							permission:"edit",
							label:"${msg("actions.destroy")}",
							evaluator: datagrid.destroyEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionEdit",
							permission:"edit",
							label:"${msg("actions.edit")}"
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionOpenND",
							permission:"edit",
							label:"${msg("actions.openND")}",
							evaluator: datagrid.openNDEvaluator
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionCloseND",
							permission:"edit",
							label:"${msg("actions.closeND")}",
							evaluator: datagrid.closeNDEvaluator
						},
						{
							type: "datagrid-action-link-${bubblingId}",
							id: "onActionApproveNomenclatureYear",
							permission: "edit",
							label: "${msg("actions.approve-nd-year")}",
							evaluator: datagrid.approveNomenclatureYearEvaluator
						},
						{
							type: "datagrid-action-link-${bubblingId}",
							id: "onActionDeleteNomenclatureYear",
							permission: "edit",
							label: "${msg("actions.delete-nd-year")}",
							evaluator: datagrid.deleteNomenclatureYearEvaluator,
							confirmFunction: datagrid.deleteYearSection_Prompt
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionArchiveND",
							permission:"edit",
							label:"${msg("actions.archiveND")}",
							evaluator: datagrid.archiveNDEvaluator
						},
						{
							type: "datagrid-action-link-${bubblingId}",
							id: "onActionCloseNomenclatureYear",
							permission: "edit",
							label: "${msg("actions.close-nd-year")}",
							evaluator: datagrid.closeNomenclatureYearEvaluator,
							confirmFunction: datagrid.closeYearSection_Prompt
						},
						{
							type:"datagrid-action-link-${bubblingId}",
							id:"onActionDeleteND",
							permission:"edit",
							label:"${msg("actions.deleteND")}",
							evaluator: datagrid.deleteNDEvaluator,
							confirmFunction: datagrid.deleteND_Propmt
						}
					]
				});

				datagrid.setMessages(${messages});
				datagrid.draw();
			}
		});
	//]]></script>
</@grid.datagrid>