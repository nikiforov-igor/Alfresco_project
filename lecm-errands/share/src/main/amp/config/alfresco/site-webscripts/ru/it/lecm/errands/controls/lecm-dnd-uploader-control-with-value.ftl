<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container-with-value.ftl">

<#assign params = field.control.params/>

<div class="control dnd-uploader editmode">
    <input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value}"/>
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

<#assign showAttsLabel = true/>
<#assign showAttsList = true/>
<#if params.showAttsLabel?? && params.showAttsLabel == "false">
    <#assign showAttsLabel = false/>
</#if>
<#assign suppressRefreshEvent = "false"/>
<#if params.suppressRefreshEvent?? && params.suppressRefreshEvent == "true">
    <#assign suppressRefreshEvent = "true"/>
</#if>
<#assign showPreview = "false"/>
<#if params.showPreview?? && params.showPreview == "true">
    <#assign showPreview = "true"/>
</#if>
<#if params.showAttsList?? && params.showAttsList == "false">
    <#assign showAttsList = false/>
</#if>
<#if showAttsLabel>
    <div class="label-div">
        <label>
        ${field.label?html}:
            <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
</#if>
    <div class="container">
        <div class="buttons-div">
        <@renderDndUploaderContainerHTML fieldHtmlId field form suppressRefreshEvent showPreview/>
        </div>
        <div class="value-div">
        <#if showAttsList>
            <ul id="${fieldHtmlId}-attachments" class="attachments-list"></ul>
        </#if>
        </div>
    </div>
</div>
<div class="clear"></div>
