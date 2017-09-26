<#escape x as jsonUtils.encodeJSONString(x)>
{
"items": [
    <#list items as item>
        <#if item??>{
        "previosDocRef" : "${documentRef}",
        "title": "${item.title!''}",
        "nodeRef": "${item.nodeRef}",
        "docType": "${item.docType}",
        "status": "${item.status!''}",
        "hasAccess": ${item.hasAccess?string},
        "hasChildElements": ${item.hasChildElements?string}
        }<#if item_has_next>,</#if>
        </#if>
    </#list>
]
}
</#escape>
