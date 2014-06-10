<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{

	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-forms-editor/lecm-form-field-component.js'
		], createControl);
	}

	function createControl() {
		var control = new LogicECM.module.FormsEditor.FieldComponent("${fieldHtmlId}").setMessages(${messages});
		control.setOptions({
		<#if disabled>
			disabled: true,
		</#if>

		<#if field.mandatory??>
			mandatory: ${field.mandatory?string},
		<#elseif field.endpointMandatory??>
			mandatory: ${field.endpointMandatory?string},
		</#if>

			itemNodeRef: "${form.arguments.itemId!''}",
		<#if (field.value?? && field.value?length > 0)>
			value: ${field.value}
		</#if>
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="form-field">
<#if form.mode == "view">
	<div class="viewmode-field">
		<#if field.mandatory && !(field.value?is_number) && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
		<#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
			<#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
		<#else>
			<#if field.value?is_number>
				<#assign fieldValue=field.value?c>
			<#else>
				<#assign fieldValue=field.value?html>
			</#if>
		</#if>
		<span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
	</div>
<#else>
	<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<input id="${fieldHtmlId}" type="hidden" name="${field.name}" <#if disabled>disabled="true"</#if>/>
	<select id="${fieldHtmlId}-select" <#if disabled>disabled="true"</#if>>
		<option>${msg("label.control.default")}</option>
	</select>
	<table class="formFieldControlParamsTable"><tbody id="${fieldHtmlId}-params"></tbody></table>
	<div id="${fieldHtmlId}-hidden-params"></div>
</#if>
</div>