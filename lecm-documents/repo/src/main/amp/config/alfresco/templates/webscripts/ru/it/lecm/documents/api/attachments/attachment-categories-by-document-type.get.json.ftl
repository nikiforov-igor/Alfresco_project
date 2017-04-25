<#escape x as jsonUtils.encodeJSONString(x)>
{
    "categories": [
        <#list categories as item>
            {
                "name": "${item}"
            }<#if item_has_next>,</#if>
        </#list>
    ]
}
</#escape>
