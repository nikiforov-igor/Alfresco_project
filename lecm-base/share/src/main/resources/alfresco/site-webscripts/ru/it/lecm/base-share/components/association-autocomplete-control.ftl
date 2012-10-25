<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
    var control = new LogicECM.module.AssociationAutoComplete("${fieldHtmlId}").setMessages(${messages});
    control.setOptions(
            {
                <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>
                    disabled: true,
                </#if>
                <#if field.control.params.parentNodeRef??>
                    parentNodeRef: "${field.control.params.parentNodeRef}",
                </#if>
                multipleSelectMode: ${field.endpointMany?string},
                itemType: "${field.endpointType}",
                currentValue: "${field.value!''}",
                itemFamily: "node",
                maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                selectedValueNodeRef: "${fieldValue}",
                nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
                openSubstituteSymbol: "{",
                closeSubstituteSymbol: "}"
            });
})();
//]]></script>

<style type="text/css">
    #${fieldHtmlId}-autocomplete {padding-bottom:2em;}
</style>

<div class="form-field">
    <#if form.mode == "view">
        <div class="viewmode-field">
            <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
            </#if>
            <span class="viewmode-label">${field.label?html}:</span>
            <span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value"></span>
        </div>
    <#else>
        <label for="${fieldHtmlId}-autocomplete-input">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>
        <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added" value="${fieldValue}"/>

        <div id="${fieldHtmlId}-autocomplete">
            <input id="${fieldHtmlId}-autocomplete-input" type="text"/>
            <div id="${fieldHtmlId}-autocomplete-container"></div>
        </div>

        <div id="${fieldHtmlId}-selected-elements"></div>
    </#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>