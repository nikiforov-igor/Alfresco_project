<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<div class="form-field">
<#if disabled>
	<div id="${controlId}" class="viewmode-field">
		<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
		<span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
	</div>
<#else>
	<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<div id="${controlId}" class="object-finder">

		<div id="${controlId}-currentValueDisplay" class="current-values"></div>

		<#if field.disabled == false>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
				<span class="create-new-button">
                    <input type="button" id="${controlId}-create-new-button" name="-" value=""/>
                </span>
			</div>
			<div id="${controlId}-create-menu"></div>
		</#if>

		<div class="clear"></div>
	</div>
</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>

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


	new LogicECM.module.AssociationCreateControl("${fieldHtmlId}").setOptions({
		parentNodeRef: "${form.arguments.itemId}",
		<#if disabled>
			disabled: true,
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
		<#if field.control.params.changeItemsFireAction??>
			changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
		</#if>
		<#if field.control.params.createNewMessage??>
			createNewMessage: "${field.control.params.createNewMessage}",
		<#elseif field.control.params.createNewMessageId??>
			createNewMessage: "${msg(field.control.params.createNewMessageId)}",
		</#if>
		<#if field.control.params.fullDelete??>
			fullDelete: ${field.control.params.fullDelete},
		</#if>
		currentValue: "${field.value!''}",
		<#if renderPickerJSSelectedValue??>
			selectedValue: "${renderPickerJSSelectedValue}",
		</#if>
		<#if field.control.params.itemTypes??>
			itemTypes: "${field.control.params.itemTypes}".split(",")
		<#else>
			itemTypes: ["${field.endpointType}"]
		</#if>
	}).setMessages( ${messages} );
</script>