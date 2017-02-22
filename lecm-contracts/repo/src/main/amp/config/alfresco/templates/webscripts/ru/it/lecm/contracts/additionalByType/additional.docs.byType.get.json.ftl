<#escape x as jsonUtils.encodeJSONString(x)!''>
[
    <#list docs as doc>
    {
        "nodeRef": "${doc.getNodeRef().toString()}"
    }
        <#if doc_has_next>,</#if>
    </#list>
]
</#escape>