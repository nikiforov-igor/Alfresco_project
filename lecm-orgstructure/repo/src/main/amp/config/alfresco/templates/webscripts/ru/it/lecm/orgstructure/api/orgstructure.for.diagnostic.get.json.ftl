<#escape x as jsonUtils.encodeJSONString(x)!''>
    <#list  result as employee>
    ${msg('label.user')}: ${employee.firstName} ${employee.middleName} ${employee.lastName} (${employee.login})
    <#if (employee.direct?size > 0)>${msg('label.assigned.roles')}: <#list  employee.direct as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.unit?size > 0)>
    ${msg('label.unit.roles')}: <#list  employee.unit as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.workgroup?size > 0)>
    ${msg('label.workgroup.roles')}: <#list  employee.workgroup as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.position?size > 0)>
    ${msg('label.position.roles')}: <#list  employee.position as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if><#if (employee.delegateRoles?size > 0)>
    ${msg('label.delegated.roles')}: <#list employee.delegateRoles as role>${role.roleName} (${role.roleCode})<#if role_has_next>, </#if></#list></#if>
    </#list>
</#escape>