<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if isActive??>
	"isActive": ${isActive?string}
<#else/>
	"isActive": null
</#if>
}
</#escape>
