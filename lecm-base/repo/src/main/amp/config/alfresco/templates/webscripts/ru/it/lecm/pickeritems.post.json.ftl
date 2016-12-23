<#macro renderParent node indent="   ">
    <#escape x as jsonUtils.encodeJSONString(x)>
    ${indent}"parent":
    ${indent}{
    <#if (node != rootNode) && node.parent??>
        <@renderParent node.parent indent+"   " />
    </#if>
        ${indent}"type": "${node.typeShort}",
        ${indent}"isContainer": ${node.isContainer?string},
        ${indent}"name": "${node.properties.name!""}",
        ${indent}"title": "${node.properties.title!""}",
        ${indent}"description": "${node.properties.description!""}",
        <#if node.properties.modified??>${indent}"modified": "${xmldate(node.properties.modified)}",</#if>
        <#if node.properties.modifier??>${indent}"modifier": "${node.properties.modifier}",</#if>
        ${indent}"displayPath": "${node.displayPath!""}",
        ${indent}"nodeRef": "${node.nodeRef}"
    ${indent}},
    </#escape>
</#macro>


<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":
    {
<#if parent??>
    <@renderParent parent />
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
            "hasAccess": ${row.hasAccess?string},
            "hasWritePermission": <#if row.item.hasPermission??>${row.item.hasPermission("Write")?string}<#else>false</#if>
            }<#if row_has_next>,</#if>

        </#list>
        ]
    }
}
</#escape>