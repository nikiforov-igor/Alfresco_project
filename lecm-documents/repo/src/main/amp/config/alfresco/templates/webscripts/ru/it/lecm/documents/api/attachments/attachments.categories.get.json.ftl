<#escape x as jsonUtils.encodeJSONString(x)>
{
    "categories": [
        <#list items as item>
            <#if item.category??>
                {
                    "nodeRef": "${item.category.nodeRef}",
                    "name": <#if isMlSupported && item.category.properties["cm:title"]?has_content>"${item.category.properties["cm:title"]}"<#else>"${item.category.name}"</#if>,
                    "path": "${item.category.displayPath}/${item.category.name}",
                    "isReadOnly": ${item.isReadOnly?string}
                }<#if item_has_next>,</#if>
            </#if>
        </#list>
    ]
}
</#escape>
