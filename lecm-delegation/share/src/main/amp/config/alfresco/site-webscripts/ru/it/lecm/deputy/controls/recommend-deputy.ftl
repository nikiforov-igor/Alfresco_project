<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#include "/org/alfresco/components/component.head.inc">
<#assign params = field.control.params>
<#assign controlId = fieldHtmlId + "-cntrl">

<#if params.dataSource??>
	<#assign dataSource = params.dataSource>
<#else>
	<#assign dataSource = 'lecm/deputy/list'>
</#if>

<#if params.targetField??>
	<#assign targetField = params.targetField?replace(":", "_")>
</#if>

<#if params.targetEvent??>
	<#assign targetEvent = params.targetEvent>
</#if>

<#assign useCurrentUser = false/>
<#if field.control.params.useCurrentUser?? && field.control.params.useCurrentUser == "true">
	<#assign useCurrentUser = true/>
</#if>

<#assign hideLabel = false/>
<#if field.control.params.hideLabel?? && field.control.params.hideLabel == "true">
	<#assign hideLabel = true/>
</#if>

<div class="control<#if !useCurrentUser> hidden</#if>" id="${controlId}">
	<div class="label-div<#if hideLabel> hidden</#if>">
	<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
		<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
	</label>
	</div>
<div class="recomend-deputy-grid-container container">
	<@grid.datagrid controlId false>

		<script type="text/javascript">//<![CDATA[
			(function () {

				YAHOO.Bubbling.on('datagridVisible', createDisablerDiv);

				function createDisablerDiv(layer, obj) {
					var disabler = document.getElementById('${controlId}-grid-disable-overlay');
					if(!disabler) {
						var grid = document.getElementById('${controlId}-grid');
						disabler = document.createElement('div');

						disabler.id = '${controlId}-grid-disable-overlay';
						disabler.className = 'disable-overlay hidden';

						grid.insertBefore(disabler, grid.firstChild);
					}
				}


				function init() {
					LogicECM.module.Base.Util.loadResources([
						'scripts/lecm-base/components/advsearch.js',
						'scripts/lecm-base/components/lecm-datagrid.js',
						'scripts/lecm-deputy/recomend-deputy-datagrid.js'
					], [
						'css/lecm-deputy/recomend-deputy-grid.css'
					], createDatagrid);
				}

				YAHOO.util.Event.onDOMReady(init);

				function createDatagrid() {

					var datagrid = new LogicECM.module.Deputy.RecomendGrid('${controlId}').setOptions({
						<#if targetField??>
							targetField: '${targetField}',
						</#if>
						<#if targetEvent??>
							targetEvent: '${targetEvent}',
						</#if>
						useCurrentUser: ${useCurrentUser?string},
						bubblingLabel: "recomend-deputies-datagrid",
						usePagination: false,
						formId: '${args.htmlid}',
						fieldId: '${field.configName}',
						dataSource: '${dataSource}<#if args["nodeRef"]??>?docNodeRef=${args["nodeRef"]}</#if>',
						allowCreate: false,
						expandable: false,
						showCheckboxColumn: false,
						showActionColumn: false,
						datagridMeta:{
							itemType: "lecm-deputy:deputy",
							datagridFormId: "recomend-deputy-grid",
							parent: '${args["nodeRef"]}'
						}
					});
					datagrid.draw();
				}
			})();
		//]]></script>

	</@grid.datagrid>
	<div id="send-to-employee-message" class="hidden">
		<span>${msg("form.redirect.to.deputy")}</span><span id="employeeName"></span><span id="cancel-deputy"><a id="${controlId}-cancel-link">${msg("form.deputy.cancel")}</a></span>
	</div>
</div>

</div>
<div class="clear"></div>