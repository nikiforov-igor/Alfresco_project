<#escape x as jsonUtils.encodeJSONString(x)>
	{
		"nodeRef": "${settings.getNodeRef()}",
        "lecm-busjournal:archiver-deep": "${settings.properties["lecm-busjournal:archiver-deep"]}",
		"lecm-busjournal:archiver-period": "${settings.properties["lecm-busjournal:archiver-period"]}"
	}
</#escape>