<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"chief": "${chief?js_string}",
	"newTasksSecretary": "${newTasksSecretary?js_string}",
	"oldTasksSecretary": <#if oldTasksSecretary??>"${oldTasksSecretary?js_string}"<#else>null</#if>
}
</#escape>
