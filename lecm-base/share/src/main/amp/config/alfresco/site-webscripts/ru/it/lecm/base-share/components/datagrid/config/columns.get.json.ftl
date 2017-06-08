<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if error??>
	"error": "${error}"
	<#else>
	"columns":
	[
		<#list columns as col>
		{
		"type": "${col.type}",
		"name": "${col.name}",
		"formsName": "<#if col.type == "association">assoc_<#else>prop_</#if>${col.name?replace(":", "_")}",
		"label": "${col.label!""}",
			<#if col.nameSubstituteString??>
			"nameSubstituteString":"${col.nameSubstituteString}",
			</#if>
			<#if col.dataType??>
			"dataType": "${col.dataType}",
			<#else>
			"dataType": "${col.endpointType}",
			</#if>
		<#if col.sortable??>
        "sortable": ${col.sortable?string}
		<#else>
        "sortable": true
		</#if>
		}<#if col_has_next>,</#if>
		</#list>
	]
	</#if>
}
</#escape>