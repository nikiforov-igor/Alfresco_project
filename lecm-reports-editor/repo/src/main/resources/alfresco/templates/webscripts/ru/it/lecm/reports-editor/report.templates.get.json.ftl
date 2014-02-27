<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":{
        "templates":
            [
                <#if results??>
                    <#list results as row>
                        {
                            "templateName": "${row.properties.name!""}",
                            "templateCode":"${row.properties["lecm-rpeditor:templateCode"]!""}",
                            "nodeRef": "${row.nodeRef}"
                        }
                        <#if row_has_next>,</#if>
                    </#list>
                </#if>
            ]
    }
}
</#escape>