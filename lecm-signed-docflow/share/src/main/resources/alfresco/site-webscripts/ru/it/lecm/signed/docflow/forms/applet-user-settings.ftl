<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#attempt>
	<#import "/ru/it/lecm/signed/docflow/components/crypto.ftl" as crypto/>
	<@crypto.initApplet/>
<#recover>
</#attempt>

<#assign htmlId = args.htmlid>

<#assign formId = htmlId + "-form">
<#assign formContainerId = formId + "-container">


<div id="${formContainerId}">
<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_operator-code"] />
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_partner-key"] />
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_applet-container"] />
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_applet-key"] />
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_auth-type"] />
	<@formLib.renderField field = form.fields["prop_lecm-signed-docflow_applet-cert"] />
</@>
</div>
