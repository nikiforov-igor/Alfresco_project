<#include "/org/alfresco/components/component.head.inc">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign addFieldsFormId = controlId + "-addFields">

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
	<div id="${controlId}" class="object-finder with-two-buttons arm-settings-fields-control">

		<div id="${controlId}-currentValueDisplay" class="current-values"></div>

		<#if field.disabled == false>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
				<span class="create-field-button">
                    <input type="button" id="${controlId}-create-new-button" name="-" value="" title="${msg('title.field.create')}"/>
                </span>

				<span class="add-from-model-button">
                    <input type="button" id="${controlId}-add-from-model-button" name="-" value="" title="${msg('title.field.addFromModel')}"/>
                </span>
			</div>

			<div id="${controlId}-create-menu"></div>

			<#if !disabled>
				<div id="${addFieldsFormId}" class="yui-panel" style="visibility: hidden">
					<div id="${addFieldsFormId}-head" class="hd">${msg("label.arm.field.add.fromModel")}</div>
					<div id="${addFieldsFormId}-body" class="bd">
						<div id="${addFieldsFormId}-content" style="height: 350px;	overflow-y: auto;"></div>
						<div class="bdft">
					<span id="${addFieldsFormId}-add" class="yui-button yui-push-button">
		                <span class="first-child">
		                    <button type="button" tabindex="1">${msg("button.add")}</button>
		                </span>
		            </span>
		            <span id="${addFieldsFormId}-cancel" class="yui-button yui-push-button">
		                <span class="first-child">
		                    <button type="button" tabindex="0">${msg("button.close")}</button>
		                </span>
		            </span>
						</div>
					</div>
				</div>
			</#if>
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

	new LogicECM.module.ARM.SettingsAddFields("${fieldHtmlId}").setOptions({
		parentNodeRef: "${form.arguments.itemId}",
		<#if disabled>
			disabled: true,
		</#if>

		addFieldsFormId: "${addFieldsFormId}",
		currentValue: "${field.value!''}",
		<#if renderPickerJSSelectedValue??>
			selectedValue: "${renderPickerJSSelectedValue}",
		</#if>
	}).setMessages( ${messages} );
</script>