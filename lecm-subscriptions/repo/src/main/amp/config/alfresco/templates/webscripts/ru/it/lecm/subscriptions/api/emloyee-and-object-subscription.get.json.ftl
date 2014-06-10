<#escape x as x?js_string>
{
	<#if subscription??>
		"nodeRef": "${subscription.getNodeRef().toString()}",
		"name": "${subscription.getName()}",
		"description": "${subscription.properties["lecm-subscr:description"]!''}"
	</#if>
}
</#escape>