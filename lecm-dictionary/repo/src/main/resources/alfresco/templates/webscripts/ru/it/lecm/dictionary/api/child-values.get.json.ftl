<#escape x as x?js_string>
[
    <#list children as child>
        {
            "name": "${child.getName()}",
            "nodeRef": "${child.getNodeRef()}"
        }
        <#if child_has_next>,</#if>
    </#list>
]
</#escape>



