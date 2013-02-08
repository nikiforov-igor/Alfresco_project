<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if "list" == page>
	"nodeRef": "${nodeRef}",
	"itemType": "${itemType}",
	"isEngineer": ${isEngineer?string},
	"isBoss": ${isBoss?string}
<#elseif "opts" == page>
	"isEngineer": ${isEngineer?string},
	"isBoss": ${isBoss?string},
	"hasSubordinate": ${hasSubordinate?string}
</#if>
}
</#escape>
