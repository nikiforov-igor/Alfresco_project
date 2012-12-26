<#assign fieldValue = "">
<#if field.control.params.contextProperty??>
	<#if context.properties[field.control.params.contextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.contextProperty]>
	<#elseif args[field.control.params.contextProperty]??>
		<#assign fieldValue = args[field.control.params.contextProperty]>
	</#if>
<#elseif context.properties[field.name]??>
	<#assign fieldValue = context.properties[field.name]>
<#else>
	<#assign fieldValue = field.value>
</#if>

<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
<input type="hidden" id="${fieldHtmlId}" name="-"
       <#if field.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />