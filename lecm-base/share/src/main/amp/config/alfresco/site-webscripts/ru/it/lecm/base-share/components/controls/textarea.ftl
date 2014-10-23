<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=2></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<#if form.mode == "view">
<div class="control textarea viewmode">
   <div class="label-div">
       <#if field.mandatory && field.value == "">
       <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
       </#if>
       <label>${field.label?html}:</label>
   </div>
   <div class="container">
       <div class="value-div">
           <#assign fieldValue=field.value?html>
           <#if fieldValue == "">
               <#assign fieldValue=msg("form.control.novalue")>
           </#if>
           <span class="<#if field.control.params.styleClass??> ${field.control.params.styleClass}</#if>" id="${fieldHtmlId}" name="${field.name}" tabindex="0"
                 <#if field.description??>title="${field.description}"</#if>
                 <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                   >
               <#list fieldValue?split("\n") as value>
                   <p>${value}</p>
               </#list>
           </span>
       </div>
   </div>
</div>
<#else>
<div class="control textarea editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">
            ${field.label?html}:
            <#if field.mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
        <div class="value-div">
            <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" cols="${columns}" tabindex="0"
                        <#if field.description??>title="${field.description}"</#if>
                        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                        <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${defaultValue?html}</textarea>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>
