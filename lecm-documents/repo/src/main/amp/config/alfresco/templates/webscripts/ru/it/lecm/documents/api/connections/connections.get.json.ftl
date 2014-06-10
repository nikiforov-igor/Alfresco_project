<#import "connections.lib.ftl" as connectionsLib />

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"hasNext": "${hasNext?string}",
	"items": <@connectionsLib.renderConnections items />
}
</#escape>