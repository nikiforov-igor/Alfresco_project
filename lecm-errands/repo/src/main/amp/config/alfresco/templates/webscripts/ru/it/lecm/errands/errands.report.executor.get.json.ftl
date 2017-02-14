<#escape x as jsonUtils.encodeJSONString(x)>
{
    "text": "${reportText!""}",
    "connectedDocuments": [
        <#if connectedDocuments??>
            <#list connectedDocuments as connectedDoc>
                {
                "nodeRef": "${connectedDoc.doc.nodeRef}",
                "presentString": "${connectedDoc.doc.properties["lecm-document:present-string"]!""}",
                "viewUrl": "${connectedDoc.viewPage!"document"}"
                }<#if connectedDoc_has_next>,</#if>
            </#list>
        </#if>
    ],
    "attachments": [
        <#if attachments??>
            <#list attachments as attachment>
                {
                "nodeRef": "${attachment.nodeRef}",
                "name": "${attachment.name!""}"
                }<#if attachment_has_next>,</#if>
            </#list>
        </#if>
    ]
}
</#escape>