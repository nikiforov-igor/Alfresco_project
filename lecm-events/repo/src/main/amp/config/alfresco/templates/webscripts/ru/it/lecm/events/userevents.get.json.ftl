<#if !limit?exists><#assign limit = -1></#if>
<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if events?exists>
	"events": [
        <#list events as event>
            <#if event_index?string == limit?string><#break></#if>
			{
				"nodeRef": "${event.nodeRef}",
				"title": "${event.title}",
				"description": "${event.description}",
				"allday": "${event.allday?string}",
				"where": "${event.where?string}",
				"startAt": {
					"iso8601": "${event.start}",
					"legacyTime": "${event.legacyTimeFrom}"
				},
				"endAt": {
					"iso8601": "${event.end}",
					"legacyTime": "${event.legacyTimeTo}"
				},

				"permissions": {
					"isEdit": ${event.canEdit?string},
					"isDelete": ${event.canDelete?string}
				}
			}<#if event_has_next>,</#if>
        </#list>
	]
    </#if>
}
</#escape>
