<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if connectionTypes??>
	"connectionTypes":
	[
		<#list connectionTypes as connectionType>
		{
		"nodeRef": "${connectionType.nodeRef}",
		"name": "${connectionType.properties.name}"
		}
			<#if connectionType_has_next>,</#if>
		</#list>
	]
	</#if>
}
</#escape>