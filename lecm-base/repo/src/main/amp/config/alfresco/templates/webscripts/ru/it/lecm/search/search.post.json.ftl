<#macro dateFormat date>${date?string("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</#macro>
<#import "item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "versionable": ${data.versionable?string},
    "totalRecords": ${data.paging.totalRecords?c},
    "startIndex": ${data.paging.startIndex?c},
    "metadata":
    {
        "permissions":
        {
            "userAccess":
            {
                <#list data.userAccess?keys as perm>
                    <#if data.userAccess[perm]?is_boolean>
                        "${perm?string}": ${data.userAccess[perm]?string}<#if perm_has_next>,</#if>
                    </#if>
                </#list>
            }
        }
    },
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