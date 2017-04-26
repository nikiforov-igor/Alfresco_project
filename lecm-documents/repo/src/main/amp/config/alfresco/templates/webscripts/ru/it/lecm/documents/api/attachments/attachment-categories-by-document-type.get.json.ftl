<#macro json_string string>${string?js_string?replace("\\'", "\'")?replace("\\>", ">")}</#macro>
{
    "categories": [
        <#list categories as item>
            {
                "name": "<@json_string "${item}"/>"
            }<#if item_has_next>,</#if>
        </#list>
    ]
}
