function getHiddenWorkflowNames()
{
	var hiddenWorkflowNames = [],
		hiddenWorkflows = config.scoped["LECMStatemachineWorkflow"]["hidden-workflows"].childrenMap["workflow"];
	if (hiddenWorkflows)
	{
		for (var hi = 0, hil = hiddenWorkflows.size(); hi < hil; hi++)
		{
			hiddenWorkflowNames.push(hiddenWorkflows.get(hi).attributes["name"]);
		}
	}
	return hiddenWorkflowNames;
}

function sortByTitle(workflow1, workflow2)
{
	var title1 = (workflow1.title || workflow1.name).toUpperCase(),
		title2 = (workflow2.title || workflow2.name).toUpperCase();
	return (title1 > title2) ? 1 : (title1 < title2) ? -1 : 0;
}

function getWorkflowDefinitions()
{
	var hiddenWorkflowNames = getHiddenWorkflowNames(),
		connector = remote.connect("alfresco"),
		result = connector.get("/api/workflow-definitions?exclude=" + hiddenWorkflowNames.join(","));
	if (result.status == 200)
	{
		var workflows = eval('(' + result + ')').data;
		workflows.sort(sortByTitle);
		return workflows;
	}
	return [];
}