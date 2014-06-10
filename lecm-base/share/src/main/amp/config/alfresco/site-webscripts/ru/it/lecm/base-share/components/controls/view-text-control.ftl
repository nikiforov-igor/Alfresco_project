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

<div class="control view-text viewmode">
	<#if showLabel>
		<div class="label-div">
		<#if field.mandatory && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
		</#if>
			<label>${field.label?html}:</label>
		</div>
	</#if>
	<div class="container">
		<div class="value-div">
			<#if field.value?is_number>
				<#assign fieldValue=field.value?c>
			<#else>
				<#assign fieldValue=field.value>
			</#if>
			<#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if>
		</div>
	</div>
</div>