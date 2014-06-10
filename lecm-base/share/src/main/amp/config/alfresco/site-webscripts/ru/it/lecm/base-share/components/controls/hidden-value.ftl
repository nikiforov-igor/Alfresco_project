<#-- Передает явно указанное в field.control.params.fieldValue значение в hidden-поле -->

<#assign fieldValue = "" >

<#if field.control.params.fieldValue??>
	<#assign fieldValue = field.control.params.fieldValue>
</#if>

<input type="hidden" name="${field.name}" id="${fieldHtmlId}" value="${fieldValue?html}" />
