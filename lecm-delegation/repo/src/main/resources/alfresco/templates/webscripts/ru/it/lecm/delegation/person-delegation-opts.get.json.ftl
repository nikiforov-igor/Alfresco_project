<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if person??>
	"person": "${person}",
<#else/>
	"person": null,
</#if>
<#if employee??>
	"employee": "${employee}",
<#else/>
	"employee": null,
</#if>
<#if delegationOpts??>
	"delegationOpts": "${delegationOpts}"
<#else/>
	"delegationOpts": null
</#if>
}
</#escape>
