<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if delegationOpts??>
	"delegationOpts": "${delegationOpts}",
<#else/>
	"delegationOpts": null,
</#if>
<#if isActive??>
	"isActive": ${isActive?string}
<#else/>
	"isActive": null
</#if>
}
</#escape>
