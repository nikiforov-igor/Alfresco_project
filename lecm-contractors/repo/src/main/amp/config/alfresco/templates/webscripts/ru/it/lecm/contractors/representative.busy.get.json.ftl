[
<#list result as representative>
"${representative["nodeRef"]}"
<#if representative_has_next>,</#if>
</#list>
]