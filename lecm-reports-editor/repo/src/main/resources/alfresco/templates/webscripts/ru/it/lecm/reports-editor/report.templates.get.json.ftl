<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":{
        "templates":
            [
                <#if results??>
                    <#list results as row>
                        {
                            "templateName": "${row.name}",
                            "templateCode":"${row.code!""}",
                            "nodeRef": "${row.nodeRef!""}"
                        }
                        <#if row_has_next>,</#if>
                    </#list>
                </#if>
            ]
    }
}
</#escape>