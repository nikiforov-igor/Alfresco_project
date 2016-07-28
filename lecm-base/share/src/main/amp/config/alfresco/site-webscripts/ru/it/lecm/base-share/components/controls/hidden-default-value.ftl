<#assign defaultValue="">
<#if form.mode == "create">
	<#if field.control.params.defaultValue??>
		<#assign defaultValue=field.control.params.defaultValue>
	<#elseif form.arguments[field.name]?has_content>
		<#assign defaultValue=form.arguments[field.name]>
	</#if>
	<#if defaultValue?string != "">
		<input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${defaultValue?html}" />
	</#if>
</#if>