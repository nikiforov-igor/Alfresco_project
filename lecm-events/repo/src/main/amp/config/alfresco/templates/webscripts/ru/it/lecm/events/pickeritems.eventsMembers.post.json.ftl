<#import "/ru/it/lecm/pickerresults.lib.ftl" as pickerResultsLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":
    {
        <#if parent??>
            <@pickerResultsLib.renderParent parent />
        </#if>
        "items":
        [
            <#list results as row>
            {
                "type": "${row.item.typeShort}",
                "isContainer": ${row.item.isContainer?string},
                "name": "${row.visibleName!row.item.properties.name!""}",
                "selectedName": "${row.selectedVisibleName!row.item.properties.name!""}",
                "path": "${row.path!""}",
                "simplePath": "${row.simplePath!""}",
                "title": "${row.titleVisibleName!row.item.properties.title!""}",
                "description": "${row.item.properties.description!""}",
                    <#if row.item.properties.modified??>"modified": "${xmldate(row.item.properties.modified)}",</#if>
                    <#if row.item.properties.modifier??>"modifier": "${row.item.properties.modifier}",</#if>
                    <#if row.item.siteShortName??>"site": "${row.item.siteShortName}",</#if>
                "displayPath": "${row.item.displayPath!""}",
                    <#if additionalProperties?? && additionalProperties?is_sequence>
                        <#list additionalProperties as prop>
                        "${prop?replace(":", "_")}": "<#if row.item.properties[prop]??>${row.item.properties[prop]?string}</#if>",
                        </#list>
                    </#if>
                    <#if row.selectable?exists>
                    "selectable" : ${row.selectable?string},
                    </#if>
                "nodeRef": "${row.item.nodeRef}",
                "memberStatus": "${row.memberStatus!"EMPTY"}",
                "memberDeclineReason": "${row.memberDeclineReason!""}",
                "memberFromDate": <#if row.memberFromDate??>"${row.memberFromDate?string("dd.MM.yyyy HH:mm")}"<#else>""</#if>,
                "memberMandatory": <#if row.memberMandatory??>${row.memberMandatory?string}<#else>false</#if>
            }<#if row_has_next>,</#if>
            </#list>
        ]
    }
}
</#escape>