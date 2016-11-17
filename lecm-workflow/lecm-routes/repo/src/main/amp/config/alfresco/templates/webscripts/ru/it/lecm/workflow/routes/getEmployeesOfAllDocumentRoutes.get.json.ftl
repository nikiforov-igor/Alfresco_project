<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "employees": [
        <#if employees??>
            <#list employees as employee>
            "${employee.nodeRef.toString()}"
                <#if employee_has_next>,</#if>
            </#list>
        </#if>
    ]
}
</#escape>