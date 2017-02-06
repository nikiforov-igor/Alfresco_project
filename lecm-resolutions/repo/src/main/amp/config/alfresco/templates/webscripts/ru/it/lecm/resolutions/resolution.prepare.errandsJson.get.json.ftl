<#import "/ru/it/lecm/search/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "fromJson": ${fromJson?string},
    "items": [
        <#if items??>
            <#list items as item>
            {
            "itemData": {
                <#list item.nodeData?keys as key>
                    <#assign itemData = item.nodeData[key]>
                    "${key}":
                        <#if itemData?is_sequence>
                        [
                            <#list itemData as data>
                                <@itemLib.renderData data /><#if data_has_next>,</#if>
                            </#list>
                        ]
                        <#else>
                            <@itemLib.renderData itemData />
                        </#if>
                        <#if key_has_next>,</#if>
                </#list>
            }
            }<#if item_has_next>,</#if>
            </#list>
        </#if>
    ]
}
</#escape>