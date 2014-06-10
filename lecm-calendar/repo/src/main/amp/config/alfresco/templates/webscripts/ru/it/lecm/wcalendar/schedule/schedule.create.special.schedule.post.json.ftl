<#escape x as jsonUtils.encodeJSONString(x)>
{
<#if nodeRef??>
	"persistedObject": "${nodeRef}",
	"message": "Successfully persisted form for item [type]lecm-sched:schedule"
<#else/>
	"persistedObject": "null",
	"message": ""
</#if>
}
</#escape>