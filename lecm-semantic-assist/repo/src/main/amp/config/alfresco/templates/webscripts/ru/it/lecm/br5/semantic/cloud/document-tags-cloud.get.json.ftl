{
	<#if  docTags??>
		<#assign keys = docTags?keys>
		<#list keys as key>
			"${key}": "${docTags[key]}"
			<#if key_has_next>,</#if>
		</#list>
	</#if>
}