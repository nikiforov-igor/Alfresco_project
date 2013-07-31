<#assign showLabel = true>
<#if field.control.params.showLabel??>
	<#if field.control.params.showLabel == "false">
		<#assign showLabel = false>
	</#if>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
	<#if field.control.params.mandatory == "true">
		<#assign mandatory = true>
	</#if>
<#elseif field.mandatory??>
	<#assign mandatory = field.mandatory>
</#if>

<div class="form-field">
	<div class="viewmode-field">
		<#if showLabel>
			<#if mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
		</#if>

		<#if field.value?is_number>
			<#assign fieldValue=field.value?c>
		<#else>
			<#assign fieldValue=field.value>
		</#if>
		<span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
	</div>
</div>