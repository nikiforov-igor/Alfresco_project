<#escape x as x?js_string>
<br />

<table border="0" cellpadding="5" cellspacing="0" id="w_table">
    <tr>
        <td>Название:</td>
        <td>Статус:</td>
        <td>ID:</td>
        <td>Действие:</td>
    </tr>
    <#list documents as document>
        <tr>
            <td>${document.name}</td>
            <td>${document.status}</td>
            <td>${document.taskId}</td>
            <td>
            <#list document.states as state>
                <button onclick="workflowForm.show('${document.nodeRef}', '${state.workflowId}', '${document.taskId}', '${state.actionId}')" >${state.label}</button>
            </#list>
            </td>
        </tr>
    </#list>
</table>
</#escape>