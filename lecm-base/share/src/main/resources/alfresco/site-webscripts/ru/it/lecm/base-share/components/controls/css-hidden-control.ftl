<#assign css= field.control.params.css!"">
<#assign cssFile = field.control.params.cssFile!"">

<#if css?string != "">
	<style>${css?string}</style>
</#if>

<#if cssFile?string != "">
	<link rel="stylesheet" type="text/css" href="/share/res/css/${cssFile}" />
</#if>
