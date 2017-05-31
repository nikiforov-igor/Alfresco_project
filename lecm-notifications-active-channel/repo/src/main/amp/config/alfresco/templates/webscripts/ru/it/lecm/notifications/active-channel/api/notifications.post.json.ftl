<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": "${next?string}",
	"items": [
		<#list notifications as notification>
		{
			"nodeRef": "${notification.item.nodeRef}",
			"formingDate": "${notification.item.properties["lecm-notf:forming-date"]?datetime?string("MM/dd/yyyy HH:mm:ss")}",
			"readDate": <#if notification.item.properties["lecm-notf-active-channel:read-date"]??>"${notification.item.properties["lecm-notf-active-channel:read-date"]?string("dd.MM.yyyy HH:mm")}"<#else>null</#if>,
			"isRead": "<#if notification.item.properties["lecm-notf-active-channel:is_read"]??>${notification.item.properties["lecm-notf-active-channel:is_read"]?string}<#else>false</#if>",
        	"isEnabled": <#if notification.isEnable??>${notification.isEnable?string}<#else>true</#if>,
			"description": "<#if notification.item.properties["lecm-notf:description"]??>${notification.item.properties["lecm-notf:description"]}</#if>",
        	"template": "<#if notification.item.properties["lecm-notf:from-template"]??>${notification.item.properties["lecm-notf:from-template"]}</#if>"
		}<#if notification_has_next>,</#if>
		</#list>
	]
}
</#escape>
