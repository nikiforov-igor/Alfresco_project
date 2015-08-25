<#include "/org/alfresco/components/component.head.inc">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign addFieldsFormId = controlId + "-addFields">


<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign isFieldMandatory = false>
<#if field.mandatory??>
	<#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
	<#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#if disabled>
	<div class="control arm-settings-fields viewmode">
		<div class="label-div">
			<#if isFieldMandatory && field.value == "">
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
	<div class="control arm-settings-fields editmode">
		<div class="label-div">
			<label for="${controlId}">
			${field.label?html}:
				<#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
		</div>
		<div class="container" id="${controlId}">
			<#if field.disabled == false>

				<div class="buttons-div">
					<span class="add-from-model-button">
	                    <input type="button" id="${controlId}-add-from-model-button" name="-" value="" title="${msg('title.field.chooseTypes')}"/>
	                </span>
				</div>
			</#if>
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable control-select-types" ></div>
			</div>
		</div>

		<#if !disabled>
			<div id="${addFieldsFormId}" class="yui-panel visible-hidden">
				<div id="${addFieldsFormId}-head" class="hd">${msg("label.arm.field.add.fromModel")}</div>
				<div id="${addFieldsFormId}-body" class="bd">
					<div id="${addFieldsFormId}-content" class="add-fields-form-content"></div>
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
            LogicECM.module.Base.Util.loadResources([
	            'scripts/lecm-base/components/lecm-select-types-control.js'
            ],
            [
	            'css/lecm-arm/arm-select-types-control.css'
            ], createControl);
		}
	
		function createControl() {
			new LogicECM.module.ARM.SettingsAddTypes("${fieldHtmlId}").setOptions({
				parentNodeRef: "${form.arguments.itemId}",
				<#if disabled>
					disabled: true,
				</#if>
				<#if field.control.params.dataSource??>
					dataSource: "${field.control.params.dataSource}",
				</#if>
				<#if form.destination??>
					destination: "${form.destination}",
				</#if>
				addFieldsFormId: "${addFieldsFormId}",
				itemId: "${form.arguments.itemId}",
				currentValue: "${field.value!''}",
				currentValues: "${field.value!''}".split(","),
				<#if field.control.params.multipleSelectMode??>
					multipleSelectMode: ${field.control.params.multipleSelectMode},
				<#else>
					multipleSelectMode: true,
				</#if>
				<#if renderPickerJSSelectedValue??>
					selectedValue: "${renderPickerJSSelectedValue}",
				</#if>
			}).setMessages( ${messages} );
		}

		YAHOO.util.Event.onDOMReady(init);
	})();
</script>