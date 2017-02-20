<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list dynamicRoles as role>
	{
	    "id": "${role.id}",
	    "name": "${role.name}",
        "emloyees": [
            <#list role.employees as employee>
                {
                    "nodeRef": "${employee.nodeRef!''}",
                    "name": "${employee.name!''}"
                }<#if employee_has_next>,</#if>
            </#list>
        ]
	}<#if role_has_next>,</#if>
    </#list>
]
</#escape>