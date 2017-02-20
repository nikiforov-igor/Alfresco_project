<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "success": <#if success??>${success?string}<#else>false</#if>
}
</#escape>