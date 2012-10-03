
<#if error?exists>
<div class="error">${error}</div>
<#elseif form?exists>
    <#assign formId=args.htmlid?js_string + "-form">
    <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
    <#if formUI == "true">
        <@formLib.renderFormsRuntime formId=formId />
    </#if>

    <@formLib.renderFormContainer formId=formId>
        <#list form.structure as item>
            <#if item.kind == "set">
                <@formLib.renderSet set=item />
            <#else>
                <@formLib.renderField field=form.fields[item.id] />
            </#if>
        </#list>
    </@>
<#else>
<div class="form-container">${msg("form.not.present")}</div>
</#if>