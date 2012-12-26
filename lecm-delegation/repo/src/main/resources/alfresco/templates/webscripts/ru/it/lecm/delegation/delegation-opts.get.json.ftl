<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if delegationOpts??>
	"delegationOpts": "${delegationOpts}"
<#else/>
	"delegationOpts": null
</#if>
}
</#escape>
