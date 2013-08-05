<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "errandsCount": ${errandsCount},
    "errands": [
        <#list errandsIssuedByMe as errand>
        {
            "nodeRef": "${errand.getNodeRef().toString()}",
            "description": "${errand.properties["lecm-errands:content"]?js_string}",
            "title": "${errand.properties["lecm-errands:title"]?js_string}",
            "statusMessage": "${errand.properties["lecm-statemachine:status"]}",
            "dueDate": "${errand.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}",
            "startDate": "${errand.properties["cm:created"]?string("dd/MM/yyyy")}",
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