<#escape x as jsonUtils.encodeJSONString(x)>
{
    "success": ${success?string},
    "data":
        {
            "items":
                [
                    <#list data.items as item>
                    {
                        "reportText": "${item.reportText}",
                        "attachments":
                            [
                            <#list item.attachments as attachment>
                                {
                                    "name": "${attachment.name}",
                                    "link": "${attachment.link}"
                                }<#if attachment_has_next>,</#if>
                            </#list>
                            ],
                        "connections":
                            [
                            <#list item.connections as connection>
                                {
                                    "name": "${connection.name}",
                                    "link": "${connection.link}"
                                }<#if connection_has_next>,</#if>
                            </#list>
                            ]
                    }<#if item_has_next>,</#if>
                    </#list>
                ],
                "formAttachments":
                [
                    <#list data.formAttachments as attachment>
                    {
                    "name": "${attachment.name}",
                    "link": "${attachment.link}"
                    }<#if attachment_has_next>,</#if>
                    </#list>
                ],
                "formConnections":
                [
                    <#list data.formConnections as attachment>
                    {
                    "name": "${attachment.name}",
                    "link": "${attachment.link}"
                    }<#if attachment_has_next>,</#if>
                    </#list>
                ],
                "formText": "${data.formText}"
        }
</#escape>
