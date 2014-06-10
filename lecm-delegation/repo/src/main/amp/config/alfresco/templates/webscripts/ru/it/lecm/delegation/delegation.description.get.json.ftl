<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if "list" == page>
	"nodeRef": "${nodeRef}",
	"itemType": "${itemType}",
	"isEngineer": ${isEngineer?string},
	"isBoss": ${isBoss?string}
<#elseif "opts" == page>
	<#if employee??>
	"employee": "${employee?string}",
	<#else/>
	"employee": null,
	</#if>
	"isEngineer": ${isEngineer?string},
	"isBoss": ${isBoss?string},
	"hasSubordinate": ${hasSubordinate?string}
</#if>
}
</#escape>
