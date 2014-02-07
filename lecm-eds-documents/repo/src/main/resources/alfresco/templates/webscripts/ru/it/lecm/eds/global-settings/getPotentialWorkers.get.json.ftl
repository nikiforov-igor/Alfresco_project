<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "employeesCount": "${employeesCount}",
    "employees": [
        <#list employeesList as employee>
        {
            "nodeRef": "${employee.getNodeRef().toString()}"
        }<#if employee_has_next>,</#if>
        </#list>
    ]
}
</#escape>