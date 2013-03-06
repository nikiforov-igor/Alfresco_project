<#escape x as jsonUtils.encodeJSONString(x)>
{
    "categories": [
        <#list categories as category>
            {
                "nodeRef": "${category.nodeRef}",
                "name": "${category.name}"
            }<#if category_has_next>,</#if>
        </#list>
    ]
}
</#escape>