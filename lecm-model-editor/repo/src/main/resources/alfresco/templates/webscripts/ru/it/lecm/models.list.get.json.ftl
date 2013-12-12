<#escape x as x?js_string>
{
	"metadata": {
		"parent": "${modelRoot.nodeRef}"
	},
    "items": [
		<#list models as model>
		    {
				<#if model.id??>
			        "id": "${model.id}",
				</#if>
				<#if model.nodeRef??>
					"nodeRef": "${model.nodeRef}",
				</#if>
			    "title": "${model.title!""}",
			    "description": "${model.description!""}",
				"isActive": ${model.isActive?string}
		    }<#if model_has_next>,</#if>
	    </#list>
	]
}
</#escape>