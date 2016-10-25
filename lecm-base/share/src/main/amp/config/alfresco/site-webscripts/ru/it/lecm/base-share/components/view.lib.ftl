

<#macro viewForm formId="view-node-form">
</#macro>

<#function showViewLink name nodeRef titleId>
	<#return "<a href=\"javascript:void(0);\" onclick=\"LogicECM.module.Base.Util.viewAttributes('" + nodeRef + "', null, \'" + titleId + "\')\">" + name + "</a>">
</#function>