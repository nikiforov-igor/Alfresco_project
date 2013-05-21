<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": <#if next?? && (next?size > 0)>"true"<#else>"false"</#if>,
	"items": [
		<#list notifications as item>
		{
			"nodeRef": "${item.nodeRef}",
			"formingDate": "${item.properties["lecm-notf:forming-date"]?datetime?string("MM/dd/yyyy HH:mm:ss")}",
			"readDate": <#if item.properties["lecm-notf-active-channel:read-date"]??>"${item.properties["lecm-notf-active-channel:read-date"]?string("dd.MM.yyyy HH:mm")}"<#else>null</#if>,
			"isRead": "<#if item.properties["lecm-notf-active-channel:is_read"]??>${item.properties["lecm-notf-active-channel:is_read"]?string}<#else>false</#if>",
			"description": "${item.properties["lecm-notf:description"]}"
		}<#if item_has_next>,</#if>
		</#list>
	]
}
</#escape>