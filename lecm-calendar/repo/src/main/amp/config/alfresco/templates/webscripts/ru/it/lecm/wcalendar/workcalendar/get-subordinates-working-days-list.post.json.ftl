<#--<#escape x as jsonUtils.encodeJSONString(x)>-->
<#assign keys = result?keys>
{
  <#list keys as arg>
   "${arg}": [
	<#list result[arg] as date>
		"${date}"<#if date_has_next>,</#if>
	</#list>
	]<#if arg_has_next>,</#if>
  </#list>
}

<#--</#escape>-->
