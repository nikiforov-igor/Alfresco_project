<#assign isTrue=false>
<#if field.value??>
    <#if field.value?is_boolean>
        <#assign isTrue=field.value>
    <#elseif field.value?is_string && field.value == "true">
        <#assign isTrue=true>
    </#if>
</#if>

<#macro readOnly>
    <div class="read-only-important">
        <#if isTrue>${msg("form.control.field-important")}</#if>
    </div>
</#macro>

<div class="form-field field-important">
    <#if form.mode == "view">
        <@readOnly/>
    <#else>
        <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>" />
        <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>
            <@readOnly/>
        <#else>
            <input class="formsCheckBox" id="${fieldHtmlId}-entry" type="checkbox" tabindex="0" name="-" <#if field.description??>title="${field.description}"</#if>
                <#if isTrue> value="true" checked="checked"</#if>
                   <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>
                   <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                   <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                   onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("${fieldHtmlId}-entry").checked;' />
            <label for="${fieldHtmlId}-entry" class="checkbox">${field.label?html}</label>
        </#if>
        <@formLib.renderFieldHelp field=field />
    </#if>
</div>