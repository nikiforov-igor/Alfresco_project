{
	<#if docList??>
		"docList":[
			<#list docList as tag>
				"${tag}"
			<#if tag_has_next>,</#if>
			</#list>
		]
	</#if>
}