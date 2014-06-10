<#escape x as x?js_string>
[
	<#if (forms?? && forms?size > 0)>
		<#list forms as form>
		{
			"nodeRef": "${form.nodeRef}",
			"evaluator": "${form.properties["lecm-forms-editor:form-evaluator"]!''}",
			"id": "${form.properties["lecm-forms-editor:form-id"]!''}",
			"template": "${form.properties["lecm-forms-editor:form-template"]!''}"
		}<#if form_has_next>,</#if>
		</#list>
	</#if>

]
</#escape>