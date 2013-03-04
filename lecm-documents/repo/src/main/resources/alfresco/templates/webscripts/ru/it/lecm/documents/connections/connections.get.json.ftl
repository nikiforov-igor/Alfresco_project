<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": <#if next?? && (next?size > 0)>"true"<#else>"false"</#if>,
	"items": [
		<#list connections as item>
		{
			"nodeRef": "${item.nodeRef}",
			"name": "${item.properties["cm:name"]}"
            <#--"type": {-->
                <#--<#if item.targetAssocs["lecm-connect:connection-type-assoc"]??>-->
                    <#--"nodeRef": "${item.targetAssocs["lecm-connect:connection-type-assoc"].nodeRef}",-->
                    <#--"name": "${item.targetAssocs["lecm-connect:connection-type-assoc"].properties["cm:name"]}-->
                <#--</#if>-->
            <#--}-->
		}<#if item_has_next>,</#if>
		</#list>
	]
}
</#escape>