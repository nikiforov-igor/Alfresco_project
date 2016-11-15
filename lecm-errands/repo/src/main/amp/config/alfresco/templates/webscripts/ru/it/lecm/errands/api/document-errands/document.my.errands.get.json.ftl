<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "errandsCount": ${errandsCount?string},
    <#if latestErrandNodeRef?exists>
        "latestErrandNodeRef": "${latestErrandNodeRef}",
        "latestErrandStartDate": "${latestErrandStartDate?string("dd.MM.yyyy")}",
    </#if>
    "errands": [
        <#list errandsList as errand>
        {
            "nodeRef": "${errand.getNodeRef().toString()}",
            "description": "<#if errand.properties["lecm-errands:content"]?has_content>${errand.properties["lecm-errands:content"]?string}</#if>",
            "title": "${errand.properties["lecm-errands:title"]}",
            "statusMessage": "${errand.properties["lecm-statemachine:status"]}",
            "dueDate": "<#if errand.properties["lecm-errands:limitation-date"]?has_content>${errand.properties["lecm-errands:limitation-date"]?string("dd.MM.yyyy")}</#if>",
            "startDate": "${errand.properties["cm:created"]?string("dd.MM.yyyy")}",
            "isImportant": "${errand.properties["lecm-errands:is-important"]?string}",
            "isExpired": "${errand.properties["lecm-errands:is-expired"]?string}",
            "executor": "${errand.assocs["lecm-errands:executor-assoc"][0].getNodeRef().toString()}",
            "executorName": "${errand.assocs["lecm-errands:executor-assoc"][0].properties["lecm-orgstr:employee-short-name"]?string}",
            "initiator": "${errand.assocs["lecm-errands:initiator-assoc"][0].getNodeRef().toString()}",
            "initiatorName": "${errand.assocs["lecm-errands:initiator-assoc"][0].properties["lecm-orgstr:employee-short-name"]?string}"
        }<#if errand_has_next>,</#if>
        </#list>
    ]
}
</#escape>