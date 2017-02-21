<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if subscription??>
		"nodeRef": "${subscription.getNodeRef().toString()}",
		"name": "${subscription.getName()}",
		"description": "${subscription.properties["lecm-subscr:description"]!''}"
	</#if>
}
</#escape>