<#escape x as jsonUtils.encodeJSONString(x)>
	{
		"nodeRef": "${settings.getNodeRef()}",
        "archiverDeep": "${settings.properties["lecm-busjournal:archiver-deep"]}",
		"archiverPeriod": "${settings.properties["lecm-busjournal:archiver-period"]}"
	}
</#escape>