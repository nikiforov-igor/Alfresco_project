<#include "/org/alfresco/components/component.head.inc">
<#include "association-search-control-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">

<#if field.control.params.showViewIncompleteWarning?? && field.control.params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
<#else>
	<#assign showViewIncompleteWarning = true>
</#if>

<#if field.control.params.showSelectedItems?? && field.control.params.showSelectedItems == "false">
	<#assign showSelectedItems = false>
<#else>
	<#assign showSelectedItems = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if disabled>
<div id="${controlId}" class="control association-search viewmode">
	<div class="label-div">
		<#if showViewIncompleteWarning && (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div">
			<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
			<span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
		</div>
	</div>
</div>
<#else>
<div class="control association-search editmode">
	<div class="label-div">
		<label for="${controlId}">
			${field.label?html}:
			<#if field.endpointMandatory!false || field.mandatory!false>
				<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
			</#if>
		</label>
	</div>
	<div id="${controlId}" class="container">
		<#if field.disabled == false>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<@renderSearchPickerHTML controlId/>
		</#if>

		<div class="value-div">
			<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
			<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
		</div>
	</div>
</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">
	<#if field.control.params.selectedValueContextProperty??>
		<#if context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		<#elseif args[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
		<#elseif context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		</#if>
	</#if>
	<#assign optionSeparator="|">
	<#assign labelSeparator=":">

	(function () {
		
		function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/lecm-association-search.js'
			], createControl);
		}

		function createControl() {

			new LogicECM.module.AssociationSearchViewer( "${fieldHtmlId}" ).setOptions({
				<#if disabled>
					disabled: true,
				</#if>
				<#if field.control.params.rootLocation??>
					rootLocation: "${field.control.params.rootLocation}",
				</#if>
				<#if field.mandatory??>
					mandatory: ${field.mandatory?string},
				<#elseif field.endpointMandatory??>
					mandatory: ${field.endpointMandatory?string},
				</#if>
				multipleSelectMode: ${field.endpointMany?string},

				<#if field.control.params.nameSubstituteString??>
					nameSubstituteString: "${field.control.params.nameSubstituteString}",
				</#if>
                <#if field.control.params.sortProp??>
                    sortProp: "${field.control.params.sortProp}",
                </#if>
				<#if field.control.params.selectedItemsNameSubstituteString??>
					selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
				</#if>
				<#if field.control.params.rootNodeRef??>
					rootNodeRef: "${field.control.params.rootNodeRef}",
				</#if>
				<#if field.control.params.childrenDataSource??>
					childrenDataSource: "${field.control.params.childrenDataSource}",
				</#if>
				<#if field.control.params.changeItemsFireAction??>
					changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
				</#if>
				<#if args.ignoreNodes??>
					ignoreNodes: "${args.ignoreNodes}".split(","),
				</#if>
				currentValue: "${field.value!''}",
				<#if renderPickerJSSelectedValue??>
					selectedValue: "${renderPickerJSSelectedValue}",
				</#if>
				<#if field.control.params.fireAction?? && field.control.params.fireAction != "">
					fireAction: {
						<#list field.control.params.fireAction?split(optionSeparator) as typeValue>
							<#if typeValue?index_of(labelSeparator) != -1>
								<#assign type=typeValue?split(labelSeparator)>
								<#if type[0] == "addItem">
									addItem: "${type[1]}",
								</#if>
								<#if type[0] == "cancel">
									cancel: "${type[1]}",
								</#if>
							</#if>
						</#list>
					},
				</#if>
				showSelectedItems: ${showSelectedItems?string},
				<#if field.control.params.additionalFilter?has_content>
					additionalFilter:"${field.control.params.additionalFilter}",
				</#if>
				<#if field.control.params.itemType??>
					itemType: "${field.control.params.itemType}"
				<#else>
				itemType: "${field.endpointType}"
				</#if>
			}).setMessages( ${messages} );
		}

		YAHOO.util.Event.onDOMReady(init);
		
	})();


</script>