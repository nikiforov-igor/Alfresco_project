<#escape x as jsonUtils.encodeJSONString(x)>
<#if report??>
    {
        "nodeRef": "${report.nodeRef}",
        "text": "${report.properties["lecm-errands-ts:execution-report-text"]!""}",
        "connectedDocuments": [
            <#list report.assocs["lecm-errands-ts:execution-report-connected-document-assoc"] as connectedDocs>
                {
                    "nodeRef": "${connectedDocs.nodeRef}",
                    "presentString": "${connectedDocs.properties["lecm-document:present-string"]!""}"
                }<#if connectedDocs_has_next>,</#if>
            </#list>
        ],
        "attachments": [
            <#list report.assocs["lecm-errands-ts:execution-report-attachment-assoc"] as attachment>
                {
                    "nodeRef": "${attachment.nodeRef}",
                    "name": "${attachment.name!""}"
                }<#if attachment_has_next>,</#if>
            </#list>
        ]
    }
</#if>
</#escape>