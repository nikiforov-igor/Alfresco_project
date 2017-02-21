<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<@build nodes=units/>
]
</#escape>

<#macro build nodes>
	<#list nodes as unit>
		{
			"fullName": "${unit.fullName}",
			"shortName": "${unit.shortName}",
			"code": "${unit.code}",
			"type": "${unit.type}",
			"active": "${unit.active?string}",
			"nodeRef": "${unit.nodeRef}",
			"subUnits":[
				<#list  unit.subUnits as n>
					<@build nodes=[n]/><#if n_has_next>,</#if>
				</#list>
			]
		}<#if unit_has_next>,</#if>
	</#list>
</#macro>
