<#import "/ru/it/lecm/search/item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
"isMoveDown": "${isMoveDown?string}",
"firstNodeRef": "${firstNodeRef}",
"secondNodeRef": "${secondNodeRef}",
"secondItem": <#if secondItem?has_content>{<@itemLib.itemJSON secondItem />}<#else>""</#if>
}
</#escape>