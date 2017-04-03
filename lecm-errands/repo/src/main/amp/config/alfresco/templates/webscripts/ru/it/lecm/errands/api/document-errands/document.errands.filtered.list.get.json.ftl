<#macro renderErrand errand>
	<#escape x as jsonUtils.encodeJSONString(x)!''>
		"nodeRef": "${errand.getNodeRef().toString()}",
		"description": "<#if errand.properties["lecm-errands:content"]?has_content>${errand.properties["lecm-errands:content"]?string}</#if>",
		"title": "${errand.properties["lecm-errands:title"]?string}",
		"statusMessage": "${errand.properties["lecm-statemachine:status"]}",
        <#if errand.properties["lecm-errands:limitation-date"]?has_content>
		    "dueDate": "${errand.properties["lecm-errands:limitation-date"]?string("dd.MM.yyyy")}",
        </#if>
		"limitationDateText": "${errand.properties["lecm-errands:limitation-date-text"]!""}",
		"startDate": "${errand.properties["cm:created"]?string("dd.MM.yyyy")}",
		"isImportant": "${errand.properties["lecm-errands:is-important"]?string}",
		"isExpired": "${errand.properties["lecm-errands:is-expired"]?string}",
		"executor": "${errand.assocs["lecm-errands:executor-assoc"][0].getNodeRef().toString()}",
		"executorName": "${errand.assocs["lecm-errands:executor-assoc"][0].properties["lecm-orgstr:employee-short-name"]?string}",
		"initiator": "${errand.assocs["lecm-errands:initiator-assoc"][0].getNodeRef().toString()}",
		"initiatorName": "${errand.assocs["lecm-errands:initiator-assoc"][0].properties["lecm-orgstr:employee-short-name"]?string}"
	</#escape>
</#macro>

{
"meErrands": [
	<#list meErrands as errand>
	{
		<@renderErrand errand />
	}<#if errand_has_next>,</#if>
	</#list>
],
"issuedMeErrands": [
	<#list issuedMeErrands as errand>
	{
		<@renderErrand errand />
	}<#if errand_has_next>,</#if>
	</#list>
],
"controlledMeErrands": [
	<#list controlledMeErrands as errand>
	{
		<@renderErrand errand />
	}<#if errand_has_next>,</#if>
	</#list>
],
"otherErrands": [
	<#list otherErrands as errand>
	{
		<@renderErrand errand />
	}<#if errand_has_next>,</#if>
	</#list>
]
}