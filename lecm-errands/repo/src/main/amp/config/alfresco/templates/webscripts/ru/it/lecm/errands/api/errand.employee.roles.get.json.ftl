<#escape x as jsonUtils.encodeJSONString(x)!''>
	{
		"isInitiator": ${isInitiator?string},
		"isExecutor": ${isExecutor?string},
		"isController": ${isController?string},
		"isCoexecutor": ${isCoexecutor?string}
	}
</#escape>