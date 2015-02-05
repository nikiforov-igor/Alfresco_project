<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"nodes": [
		<#if results??>
			<#list results as row>
			"${row.nodeRef.toString()}"
				<#if row_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>