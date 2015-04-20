<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if members?exists>
	"members": [
        <#list members as member>
            {
                "nodeRef": "${member.nodeRef}",
                "name": "${member.properties["lecm-orgstr:employee-last-name"]!""} ${member.properties["lecm-orgstr:employee-first-name"]!""} ${member.properties["lecm-orgstr:employee-middle-name"]!""}"
            }<#if member_has_next>,</#if>
        </#list>
	]
    </#if>
}
</#escape>
