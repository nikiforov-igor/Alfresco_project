<#escape x as (x!"")?js_string>
<#list  result as employee>
Пользователь: ${employee.firstName} ${employee.middleName} ${employee.lastName} (${employee.login})
<#if (employee.direct?size > 0)>Назначенные роли: <#list  employee.direct as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.unit?size > 0)>
Роли от подразделения: <#list  employee.unit as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.workgroup?size > 0)>
Роли от рабочих групп: <#list  employee.workgroup as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.position?size > 0)>
Роли от должностей: <#list  employee.position as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.delegateRoles?size > 0)>
Делегированные роли: <#list employee.delegateRoles as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if>

</#list>
</#escape>