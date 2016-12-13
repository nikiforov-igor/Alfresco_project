<#escape x as jsonUtils.encodeJSONString(x)>
{
    "success": ${success?string},
    "data":
        {
            "items":
                [
                    <#list data as item>
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
                ]
        }
}
</#escape>
