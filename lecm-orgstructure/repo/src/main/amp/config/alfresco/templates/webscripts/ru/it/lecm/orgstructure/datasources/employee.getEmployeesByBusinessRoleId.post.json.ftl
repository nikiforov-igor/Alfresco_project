{"employees":[<#list employees as employee>"${employee.nodeRef.toString()}"<#if employee_has_next>,</#if></#list>]}
<#--
{
    employees:
    [
    <#list employees as employee>
        "${employee.nodeRef.toString()}"<#if employee_has_next>,</#if>
    </#list>
    ]
}
-->