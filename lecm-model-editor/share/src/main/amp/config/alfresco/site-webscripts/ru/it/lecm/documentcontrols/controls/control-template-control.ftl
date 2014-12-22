<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<#if form.mode == "view">
	<div class="control control-template-control viewmode">
		<div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning">
				<img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/>
			</span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
	</div>
<#else>
	<div class="control control-template-control editmode">
		<div class="label-div">
			<label for="${fieldHtmlId}">${field.label?html}:
				<#if field.mandatory>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div class="container">
			<div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
			<div class="value-div">
				<select id="${fieldHtmlId}" name="${field.name}${nameSuffix}"
					<#if field.description??>title="${field.description}"</#if>
					<#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
					<option value="" selected="selected"></option>
				</select>
				<table class="formFieldControlParamsTable">
					<tbody id="${fieldHtmlId}-params"></tbody>
				</table>
				<div id="${fieldHtmlId}-hidden-params"></div>
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>
<@inlineScript group="lecm-controls-editor">
(function() {
	function() initControl() {
		var control = new LogicECM.module.ControlsEditor.ControlTemplateControl('${fieldHtmlId}');
		control.setMessages(${messages});
		control.setOptions({

		});
	}

	LogicECM.module.Base.Util.loadScripts(['scripts/lecm-controls-editor/control-template-control.js'], initControl);
})();
</@>
