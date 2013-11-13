<#escape x as (x!"")?js_string>
<@printGroup result 0/>
<#macro printGroup group indent>
<#local space="" />
<#list 0..indent as i>
<#local space = space + "    "/>
</#list>
<#if (group?size > 0)>
<#list group as childGroup>
${space}Группа: ${childGroup.name}
<#if childGroup.subGroups?? >
<#assign subIndent=indent+1 />
<@printGroup childGroup.subGroups subIndent/>
</#if>
</#list>
</#if>
</#macro>
</#escape>