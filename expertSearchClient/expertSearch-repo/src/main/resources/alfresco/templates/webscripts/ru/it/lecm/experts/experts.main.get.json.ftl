<#escape x as x?js_string>
[
    <#list  experts as ex>
    {
    lname: "${ex.lname}",
    fname: "${ex.fname}",
    ref: "${ex.nodeRef}",
    ulink: "${ex.ulink}"
    }
        <#if ex_has_next>,</#if>
    </#list>
]
</#escape>