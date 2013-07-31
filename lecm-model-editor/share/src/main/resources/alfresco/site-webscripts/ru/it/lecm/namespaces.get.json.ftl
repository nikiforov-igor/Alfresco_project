[
<#list namespaces?keys as key>
{"uri": "${namespaces[key]}", "prefix": "${key}"}<#if key_has_next>,</#if>
</#list>
]