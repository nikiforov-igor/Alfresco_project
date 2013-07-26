<#escape x as x?js_string>
{
    "myErrandsCount": ${myErrandsCount},
    "errandsIssuedByMeCount": ${errandsIssuedByMeCount}
    <#if latestErrandNoderef?exists>
        ,
        "latestErrandNoderef": "${latestErrandNoderef}",
        "latestErrandStartDate": "${latestErrandStartDate?date}"
    </#if>
    ,
    "myErrands": [
        <#list myErrands as errand>
        {
            "nodeRef": "${errand.getNodeRef().toString()}",
            "description": "${errand.properties["lecm-errands:content"]}",
            "title": "${errand.properties["lecm-errands:title"]}",
            "statusMessage": "${errand.properties["lecm-statemachine:status"]}",
            "dueDate": "${errand.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}",
            "startDate": "${errand.properties["cm:created"]?string("dd/MM/yyyy")}",
            "isImportant": "${errand.properties["lecm-errands:is-important"]?string}",
            "isExpired": "${errand.properties["lecm-errands:is-expired"]?string}"
        }<#if errand_has_next>,</#if>
        </#list>
    ],
    "errandsIssuedByMe": [
        <#list errandsIssuedByMe as errand>
        {
            "nodeRef": "${errand.getNodeRef().toString()}",
            "description": "${errand.properties["lecm-errands:content"]}",
            "title": "${errand.properties["lecm-errands:title"]}",
            "statusMessage": "${errand.properties["lecm-statemachine:status"]}",
            "dueDate": "${errand.properties["lecm-errands:limitation-date"]?string("dd/MM/yyyy")}",
            "startDate": "${errand.properties["cm:created"]?string("dd/MM/yyyy")}",
            "isImportant": "${errand.properties["lecm-errands:is-important"]?string}",
            "isExpired": "${errand.properties["lecm-errands:is-expired"]?string}"
        }<#if errand_has_next>,</#if>
        </#list>
    ]
}
</#escape>