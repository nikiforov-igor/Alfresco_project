<#-- Передает явно указанное в field.control.params.fieldValue значение в hidden-поле -->

<#assign fieldValue = "" >

<#if field.control.params.argName??>
	<#if args[field.control.params.argName]??>
		<#assign fieldValue = args[field.control.params.argName]>
	</#if>
</#if>

<input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${fieldValue}" />