<#assign hideValue = false>
<#if field.control.params.hideValue??>
    <#assign hideValue = true>
</#if>

<#assign nameSuffix = "">
<#if field.control.params.nameSuffix??>
    <#assign nameSuffix = field.control.params.nameSuffix>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign mandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign mandatory = field.mandatory>
</#if>

<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<#if form.mode == "view">
    <div class="control textfield viewmode">
        <div class="label-div">
            <#if mandatory && !(field.value?is_number) && field.value == "">
                <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
                                                  title="${msg("form.field.incomplete")}"/><span>
            </#if>
            <label>${field.label?html}:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
                    <#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
                <#else>
                    <#if field.value?is_number>
                        <#assign fieldValue=field.value?c>
                    <#else>
                        <#assign fieldValue=field.value?html>
                    </#if>
                </#if>
                <span><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
            </div>
        </div>
    </div>
<#else>
    <div class="control textfield editmode">
        <div class="label-div">
            <label for="${fieldHtmlId}">${field.label?html}:
                <#if mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
                </#if>
            </label>
        </div>
        <div class="container">
            <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
            <div class="value-div">
                <input id="${fieldHtmlId}" name="${field.name}${nameSuffix}" tabindex="0"
                       <#if field.control.params.password??>type="password"<#else>type="text"</#if>
                       <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                       <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                       <#if !hideValue>
                          <#if defaultValue?is_number>value="${defaultValue?c}"<#else>value="${defaultValue?html}"</#if>
                       </#if>
                       <#if field.description??>title="${field.description}"</#if>
                       <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
                       <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                       <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
            </div>
        </div>
    </div>
</#if>
<div class="clear"></div>
