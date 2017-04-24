<#escape x as jsonUtils.encodeJSONString(x)>
{
    <#if kind == "node">
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
    <#elseif kind == "type">
    "categories": [
        <#list items as item>
            {
                "name": "${item}"
            }<#if item_has_next>,</#if>
        </#list>
    ]
    </#if>

}
</#escape>
