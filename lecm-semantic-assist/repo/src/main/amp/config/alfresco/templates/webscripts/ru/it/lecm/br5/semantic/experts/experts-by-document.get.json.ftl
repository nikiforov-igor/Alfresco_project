{
	<#if  dataExperts??>
		<#assign keys = dataExperts?keys>
		<#list keys as key>
			"${key}": [
				<#list dataExperts[key] as mapAttrs>{
					<#assign keys2 = mapAttrs?keys>
					<#list keys2 as key3>
						"${key3}":"${mapAttrs[key3]}"
					<#if key3_has_next>,</#if>
					</#list>
				}
				<#if mapAttrs_has_next>,</#if>
				</#list>
			]
		<#if key_has_next>,</#if>
		</#list>
	</#if>
}


