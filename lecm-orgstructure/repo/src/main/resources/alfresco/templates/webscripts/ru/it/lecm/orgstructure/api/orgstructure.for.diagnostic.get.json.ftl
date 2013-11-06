<#escape x as x?js_string>
<#list  result as employee>
User: ${employee.firstName} ${employee.middleName} ${employee.lastName} (${employee.login})
<#if (employee.roles?size > 0)>Roles: <#list  employee.roles as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if>
<#if (employee.delegateRoles?size > 0)>Delegates:<#list employee.delegateRoles as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if>
</#list>
</#escape>