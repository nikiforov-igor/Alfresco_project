<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"items": [
		<#if (fields?? && fields?size > 0)>
			<#list fields as field>
			    {
			        "name": "${field.name.toPrefixString()}",
			        "title": "${field.title!''}",
			        "type": "${field.dataType.title!''}"
				}<#if field_has_next>,</#if>
			</#list><#if (attributes?? && attributes?size > 0)>,</#if>
		</#if>
		<#if (attributes?? && attributes?size > 0)>
			<#list attributes as attr>
				{
					"name": "${attr.name.toPrefixString()}",
					"title": "${attr.title!''}",
					"type": "${attr.targetClass.title!''}"
				}<#if attr_has_next>,</#if>
			</#list>
		</#if>
	]
}
</#escape>