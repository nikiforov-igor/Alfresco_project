<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
	<#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign showActions = "true"/>
<#if params.showActions??>
	<#assign showActions = params.showActions/>
</#if>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
	<#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<script type="text/javascript">//<![CDATA[
(function() {
	function drawForm(){
		var control = new LogicECM.errands.CoexecutorsReportsTS("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
					currentValue: "${field.value!""}",
					messages: ${messages},
					bubblingLabel: "${bubblingId}",
					containerId: "${containerId}",
					datagridFormId: "${params.datagridFormId!"datagrid"}",
					attributeForShow: "${attributeForShow}",
					mode: "${form.mode?string}",
					disabled: ${field.disabled?string},
					isTableSortable: ${isTableSortable?string},
					externalCreateId: "${form.arguments.externalCreateId!""}",
					refreshAfterCreate: false,
				<#if params.deleteMessageFunction??>
					deleteMessageFunction: "${params.deleteMessageFunction}",
				</#if>
				<#if params.editFormTitleMsg??>
					editFormTitleMsg: "${params.editFormTitleMsg}",
				</#if>
				<#if params.createFormTitleMsg??>
					createFormTitleMsg: "${params.createFormTitleMsg}",
				</#if>
				<#if params.viewFormTitleMsg??>
					viewFormTitleMsg: "${params.viewFormTitleMsg}",
				</#if>
					expandable: true,
					expandDataSource: "components/form?formId=table-structure-expand",
					documentNodeRef: "${form.arguments.itemId}",
					showActions: true
				});
	}
	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/advsearch.js',
			'scripts/lecm-base/components/lecm-datagrid.js',
			'scripts/documents/tables/lecm-document-table.js',
			'scripts/lecm-errands/coexecutors-reports-table-control.js'
		],
		[
			'css/lecm-errands/coexecutors-reports-table-control.css'
		], drawForm);
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="form-field with-grid coexecutors-report" id="${controlId}">
	<div class="show-declined-block">
		<input type="checkbox" id="${controlId}-show-declined">
		<label for="${controlId}-show-declined">${msg("errands.label.showDeclined")}</label>
	</div>
	<@grid.datagrid containerId false/>
	<div id="${controlId}-container">
		<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
	</div>
</div>