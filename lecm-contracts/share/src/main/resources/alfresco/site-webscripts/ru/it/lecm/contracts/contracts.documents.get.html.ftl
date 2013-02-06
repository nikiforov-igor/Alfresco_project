<#escape x as x?js_string>
<br/>

<table border="0" cellpadding="5" cellspacing="0" id="w_table">
	<tr>
		<td>Название:</td>
		<td>Статус:</td>
		<td>ID:</td>
		<td>Действие:</td>
		<td>Процессы:</td>
	</tr>
	<#list documents as document>
		<tr>
			<td>${document.name}</td>
			<td>${document.status}</td>
			<td>${document.taskId}</td>
			<td>
				<#list document.states as state>
					<button onclick="workflowForm.show('trans', '${document.nodeRef}', '${state.workflowId}', '${document.taskId}', '${state.actionId}')">${state.label}</button>
				</#list>
			</td>
            <td>
                <#list document.workflows as workflow>
                    <button onclick="workflowForm.show('user','${document.nodeRef}', '${workflow.workflowId}', '${document.taskId}', '${workflow.id}', '<#list workflow.assignees as assignee>${assignee}<#if assignee_has_next>,</#if></#list>')">${workflow.label}</button>
                </#list>
            </td>
		</tr>
	</#list>
</table>
</#escape>