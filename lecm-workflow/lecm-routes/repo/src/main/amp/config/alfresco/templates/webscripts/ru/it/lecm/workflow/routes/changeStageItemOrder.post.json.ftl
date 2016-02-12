<#import "/ru/it/lecm/search/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
"firstNodeRef": "${firstNodeRef?string}",
"secondNodeRef": "${(secondNodeRef??)?string(secondNodeRef, 'null')}",
"firstItem": {<@itemLib.itemJSON firstItem />},
<#if secondItem??>
"secondItem": {<@itemLib.itemJSON secondItem />}
<#else>
"secondItem": null
</#if>
}
</#escape>
