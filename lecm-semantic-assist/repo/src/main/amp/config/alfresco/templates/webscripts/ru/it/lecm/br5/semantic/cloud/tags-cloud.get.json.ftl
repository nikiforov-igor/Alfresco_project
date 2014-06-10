<#--{
	<#assign keys = tagsList?keys>
	<#list keys as key>
		"${key}":[
        <#assign keys2 = tagsList[key]?keys>
		<#list keys2 as key2>
				"${key2}","${tagsList[key][key2]}" <#if key2_has_next>,</#if>
		</#list>
		]
		<#if key_has_next>,</#if>
	</#list>
} -->

{
	<#if  tagsList??>
		<#assign keys = tagsList?keys>
		<#list keys as key>
			"${key}": "${tagsList[key]}"
			<#if key_has_next>,</#if>
		</#list>
	</#if>
}