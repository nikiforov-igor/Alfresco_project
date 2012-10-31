<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if field.control.params.refillable?? && field.control.params.refillable == "false">
    <#assign showCreateNewLink = false>
<#else>
    <#assign showCreateNewLink = true>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
    var control = new LogicECM.module.AssociationSelectOne("${fieldHtmlId}").setMessages(${messages});
    control.setOptions(
            {
                <#if field.control.params.parentNodeRef??>
                    parentNodeRef: "${field.control.params.parentNodeRef}",
                </#if>
                <#if field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation}",
                </#if>

                itemType: "${field.endpointType}",
                itemFamily: "node",
                maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                selectedValueNodeRef: "${fieldValue}",
                nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
                openSubstituteSymbol: "{",
                closeSubstituteSymbol: "}",
                showCreateNewButton: ${showCreateNewLink?string}
            });
})();
//]]></script>

<style type="text/css">
    #${fieldHtmlId}-added {width:20em; position: absolute;}
    #${fieldHtmlId}-selectone-create-new-button {margin-left:21em;}
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
        <label for="${fieldHtmlId}-added">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>
        <div id="${fieldHtmlId}-controls">
            <select id="${fieldHtmlId}-added" name="${field.name}_added" tabindex="0"
                    <#if field.description??>title="${field.description}"</#if>
                    <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                    <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                    <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                    <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
                <#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
                    <option value="">
                        <#if field.control.params.notSelectedOptionLabel??>
                            ${field.control.params.notSelectedOptionLabel}
                        <#elseif field.control.params.notSelectedOptionLabelCode??>
                            ${msg(field.control.params.notSelectedOptionLabelCode)}
                        </#if>
                    </option>
                </#if>
            </select>
            <#if showCreateNewLink>
                <input type="button" id="${fieldHtmlId}-selectone-create-new-button" name="-" value="${msg("logicecm.base.create-new-button.label")}"/>
            </#if>
        </div>

    </#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>