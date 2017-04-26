{
    "categories": [
        <#list categories as item>
            {
                "name": "${item?json_string}"
            }<#if item_has_next>,</#if>
        </#list>
    ]
}
