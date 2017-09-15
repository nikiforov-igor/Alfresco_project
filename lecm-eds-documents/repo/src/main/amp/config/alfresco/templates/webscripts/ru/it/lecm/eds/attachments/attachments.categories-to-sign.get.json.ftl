<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "items": [
        <#list items as item>
        {
            <#if item.category?? && item.category.node??>
            "category": {
            "nodeRef": "${item.category.node.nodeRef}",
            "name": <#if isMlSupported && item.category.node.properties?? && item.category.node.properties["cm:title"]?has_content>"${item.category.node.properties["cm:title"]}"<#else>"${item.category.node.name}"</#if>,
            "isReadOnly": ${item.category.isReadOnly?string}
            },
            </#if>
        "attachments": [
            <#list item.attachments as attachment>
            {
            "nodeRef": "${attachment.nodeRef}",
            "name": "${attachment.name}",
            "owner": "${attachment.properties["cm:creator"]}"
            }<#if attachment_has_next>,</#if>
            </#list>
        ]
        }<#if item_has_next>,</#if>
        </#list>
    ]
}
</#escape>
