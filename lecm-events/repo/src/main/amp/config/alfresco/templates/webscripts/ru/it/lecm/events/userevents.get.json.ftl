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
				"typeTitle": "${event.typeTitle?string}",
				"userMemberStatus": "${event.userMemberStatus}",
				"userIsInitiator": ${event.userIsInitiator?string},
				"startAt": {
					"iso8601": "${event.start}",
					"legacyTime": "${event.legacyTimeFrom}"
				},
				"endAt": {
					"iso8601": "${event.end}",
					"legacyTime": "${event.legacyTimeTo}"
				},

                "members": [
                    <#list event.members as member>
                        {
                            "nodeRef": "${member.nodeRef}",
                            "name": "${member.properties["lecm-orgstr:employee-short-name"]}"
                        }<#if member_has_next>,</#if>
                    </#list>
                ],

                "invitedMembers": [
                    <#list event.invitedMembers as invited>
                        {
                            "nodeRef": "${invited.nodeRef}",
                            "name": "${invited.properties["lecm-representative:surname"]} ${invited.properties["lecm-representative:firstname"]} ${invited.properties["lecm-representative:middlename"]}"
                        }<#if invited_has_next>,</#if>
                    </#list>
                ],

				"permissions": {
					"isEdit": ${event.canEdit?string},
					"isDelete": ${event.canDelete?string}
				}
			}<#if event_has_next>,</#if>
        </#list>
	],
    </#if>
	<#if nonWorkingDays??>
		"nonWorkingDays": [
			<#list nonWorkingDays as day>
		        "${day?string("yyyy-MM-dd")}"<#if day_has_next>,</#if>
			</#list>
		]
	</#if>
}
</#escape>
