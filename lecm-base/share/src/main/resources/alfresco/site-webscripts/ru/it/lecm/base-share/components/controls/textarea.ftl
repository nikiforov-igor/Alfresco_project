<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=2></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#assign fieldValue=field.value?html>
         <#if fieldValue == "">
            <#assign fieldValue=msg("form.control.novalue")>
         </#if>
            <textarea class="viewmode-value<#if field.control.params.styleClass??> ${field.control.params.styleClass}</#if>" id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0" readonly="1"
                   <#if field.description??>title="${field.description}"</#if>
                   <#if field.control.params.style??>style="${field.control.params.style}"</#if>
            >${fieldValue}</textarea>
         
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0"
                <#if field.description??>title="${field.description}"</#if>
                <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>
        <@formLib.renderFieldHelp field=field />   
    </#if>
   
</div>