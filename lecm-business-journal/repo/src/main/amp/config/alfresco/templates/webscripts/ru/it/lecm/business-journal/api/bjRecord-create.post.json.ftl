<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	<#if record??>
        "nodeRef": "${record.getNodeRef().toString()}",
		"lecm-busjournal:bjRecord-date": "${record.properties["lecm-busjournal:bjRecord-date"]?datetime?string}",
		"lecm-busjournal:bjRecord-description": "${record.properties["lecm-busjournal:bjRecord-description"]}",
		"lecm-busjournal:bjRecord-initiator": "${record.properties["lecm-busjournal:bjRecord-initiator"]}",
		"lecm-busjournal:bjRecord-mainObject": "${record.properties["lecm-busjournal:bjRecord-mainObject"]}",
		"lecm-busjournal:bjRecord-secondaryObj1": "${record.properties["lecm-busjournal:bjRecord-secondaryObj1"]!""}",
		"lecm-busjournal:bjRecord-secondaryObj2": "${record.properties["lecm-busjournal:bjRecord-secondaryObj2"]!""}",
		"lecm-busjournal:bjRecord-secondaryObj3": "${record.properties["lecm-busjournal:bjRecord-secondaryObj3"]!""}",
		"lecm-busjournal:bjRecord-secondaryObj4": "${record.properties["lecm-busjournal:bjRecord-secondaryObj4"]!""}",
		"lecm-busjournal:bjRecord-secondaryObj5": "${record.properties["lecm-busjournal:bjRecord-secondaryObj5"]!""}",
		<#if record.assocs["lecm-busjournal:bjRecord-objType-assoc"]??>
	    "lecm-busjournal:bjRecord-objType-assoc": "${record.assocs["lecm-busjournal:bjRecord-objType-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-evCategory-assoc"]??>
	    "lecm-busjournal:bjRecord-evCategory-assoc": "${record.assocs["lecm-busjournal:bjRecord-evCategory-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-secondaryObj1-assoc"]??>
	    "lecm-busjournal:bjRecord-secondaryObj1-assoc": "${record.assocs["lecm-busjournal:bjRecord-secondaryObj1-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-secondaryObj2-assoc"]??>
	    "lecm-busjournal:bjRecord-secondaryObj2-assoc": "${record.assocs["lecm-busjournal:bjRecord-secondaryObj2-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-secondaryObj3-assoc"]??>
	    "lecm-busjournal:bjRecord-secondaryObj3-assoc": "${record.assocs["lecm-busjournal:bjRecord-secondaryObj3-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-secondaryObj4-assoc"]??>
	    "lecm-busjournal:bjRecord-secondaryObj4-assoc": "${record.assocs["lecm-busjournal:bjRecord-secondaryObj4-assoc"][0].getNodeRef().toString()}",
		</#if>
		<#if record.assocs["lecm-busjournal:bjRecord-secondaryObj5-assoc"]??>
	    "lecm-busjournal:bjRecord-secondaryObj5-assoc": "${record.assocs["lecm-busjournal:bjRecord-secondaryObj5-assoc"][0].getNodeRef().toString()}",
		</#if>
		"lecm-busjournal:bjRecord-initiator-assoc": "${record.assocs["lecm-busjournal:bjRecord-initiator-assoc"][0].getNodeRef().toString()}",
		"lecm-busjournal:bjRecord-mainObject-assoc": "${record.assocs["lecm-busjournal:bjRecord-mainObject-assoc"][0].getNodeRef().toString()}"
	</#if>
}
</#escape>
