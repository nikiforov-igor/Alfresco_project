<#escape x as jsonUtils.encodeJSONString(x)!''>
[
	<#if (forms?? && forms?size > 0)>
		<#list forms as form>
		{
			"id": "${form.properties["lecm-forms-editor:id"]!''}",
			"formId": "${form.properties["lecm-forms-editor:form-id"]!''}",
			"nodeRef": "${form.nodeRef}",
			"evaluator": "${form.properties["lecm-forms-editor:form-evaluator"]!''}",
			"template": "${form.properties["lecm-forms-editor:form-template"]!''}"
		}<#if form_has_next>,</#if>
		</#list>
	</#if>

]
</#escape>
