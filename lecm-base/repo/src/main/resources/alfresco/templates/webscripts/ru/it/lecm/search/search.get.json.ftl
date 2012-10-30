<#macro dateFormat date>${date?string("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</#macro>
<#import "../common/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
"totalRecords": ${data.paging.totalRecords?c},
"startIndex": ${data.paging.startIndex?c},
"items":
[
    <#list data.items as item>
    {
        <@itemLib.itemJSON item />
    }<#if item_has_next>,</#if>
    </#list>
]
}
</#escape>