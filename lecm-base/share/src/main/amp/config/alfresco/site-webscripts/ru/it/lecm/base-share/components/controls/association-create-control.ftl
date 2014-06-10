<#include "/org/alfresco/components/component.head.inc">

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if disabled>
	<div class="control association-create viewmode">
		<div class="label-div">
			<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
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
<div class="control association-create editmode">
	<div class="label-div">
		<label for="${controlId}">
		${field.label?html}:
			<#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
		</label>
	</div>
	<div class="container" id="${controlId}">
		<#if field.disabled == false>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div class="buttons-div">
				<span class="create-new-button"	>
	                <input type="button" id="${controlId}-create-new-button" name="-" value=""/>
	            </span>
			</div>
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
                'scripts/lecm-arm/controls/arm-settings-fields-control.js',
                'scripts/lecm-base/components/lecm-association-create-control.js'
			], createControl);
		}

		function createControl() {
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
		}

		YAHOO.util.Event.onDOMReady(init);
	})();
</script>