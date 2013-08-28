<#assign hideValue = false>
<#if field.control.params.hideValue??>
    <#assign hideValue = true>
</#if>
<div class="form-field field-status">
<#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>
    <div class="read-only-status">
        <#if field.mandatory && !(field.value?is_number) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        ${field.value}
    </div>
<#else>
    <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <input id="${fieldHtmlId}" name="${field.name}" tabindex="0"
        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
        <#if !hideValue>
            <#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
        </#if>
        <#if field.description??>title="${field.description}"</#if>
        <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
    />
    <@formLib.renderFieldHelp field=field />
</#if>
</div>