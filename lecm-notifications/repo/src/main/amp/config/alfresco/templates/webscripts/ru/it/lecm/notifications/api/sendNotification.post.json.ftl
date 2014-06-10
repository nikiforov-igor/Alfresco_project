<#escape x as x?js_string>
{
	"success": <#if success??>${success?string}<#else>false</#if>
}
</#escape>