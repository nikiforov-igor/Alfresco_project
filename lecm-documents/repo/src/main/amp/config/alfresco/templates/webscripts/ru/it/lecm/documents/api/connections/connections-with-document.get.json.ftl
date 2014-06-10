<#import "connections.lib.ftl" as connectionsLib />

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"items": <@connectionsLib.renderConnections items />
}
</#escape>