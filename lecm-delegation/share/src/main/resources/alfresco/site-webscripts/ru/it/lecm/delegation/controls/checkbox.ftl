<#assign isTrue=false>
<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<div class="form-field">
<#if form.mode == "view">
	<div class="viewmode-field">
		<span class="viewmode-label">${field.label?html}:</span>
		<span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
	</div>
<#else>
	<label for="${fieldHtmlId}-entry">&nbsp;</label>
	<input class="formsCheckBox" id="${fieldHtmlId}" type="checkbox" tabindex="0" name="${field.name}" <#if field.description??>title="${field.description}"</#if>
		<#if isTrue> value="true" checked="checked"<#else> value="false"</#if>
		<#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
		<#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
		<#if field.control.params.style??>style="${field.control.params.style}"</#if>
		onchange='javascript:var checkboxEl = YAHOO.util.Dom.get("${fieldHtmlId}"); checkboxEl.value = checkboxEl.checked;'
	/>
	<label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}</label>
	<@formLib.renderFieldHelp field=field />
	<div id="error-message-container"></div>
</#if>
</div>
