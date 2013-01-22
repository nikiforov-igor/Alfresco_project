<#escape x as jsonUtils.encodeJSONString(x)>
{
	"nodeRef": "${nodeRef}",
	"itemType": "${itemType}",
<#if isEngineer>
	"isEngineer": true,
<#else/>
	"isEngineer": false,
</#if>
<#if isBoss>
	"isBoss": true
<#else/>
	"isBoss": false
</#if>
}
</#escape>
