<#escape x as jsonUtils.encodeJSONString(x)>
{
"hasDuplicate": ${hasDuplicate?string},
"duplicates": [
    <#list duplicates as duplicate>
    {
    "nodeRef": "${duplicate.nodeRef.toString()}",
    "fullName": "${duplicate.properties["lecm-contractor:fullname"]!''}",
    "shortName": "${duplicate.properties["lecm-contractor:shortname"]!''}"
    }<#if duplicate_has_next>,</#if>
    </#list>
]
}
</#escape>
