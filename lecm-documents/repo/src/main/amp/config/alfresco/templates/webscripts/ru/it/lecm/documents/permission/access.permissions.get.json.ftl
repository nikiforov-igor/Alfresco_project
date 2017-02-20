<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#if permissions??>
        <#list permissions as permission>
        {
            "id" :"${permission}"
        }<#if permission_has_next>,</#if>
        </#list>
    </#if>
]
</#escape>