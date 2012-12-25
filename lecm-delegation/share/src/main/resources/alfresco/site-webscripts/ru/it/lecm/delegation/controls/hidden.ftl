<#-- Renders a hidden form field for edit and create modes only -->
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

<#if form.mode == "edit" || form.mode == "create">
<#assign hiddenFieldValue><#if fieldValue?is_number>${fieldValue?c}<#elseif fieldValue?is_boolean/>${fieldValue?string}<#else/>${fieldValue?html}</#if></#assign>
<input type="hidden" name="${field.name}" value="${hiddenFieldValue}"/>
</#if>