<#escape x as x?js_string>
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