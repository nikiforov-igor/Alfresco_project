<#escape x as jsonUtils.encodeJSONString(x)>
[
	<#list attachments as attachment>
	{
	"nodeRef": "${attachment.nodeRef}",
	"name": "${attachment.name}"
	}<#if attachment_has_next>,</#if>
	</#list>
]
</#escape>