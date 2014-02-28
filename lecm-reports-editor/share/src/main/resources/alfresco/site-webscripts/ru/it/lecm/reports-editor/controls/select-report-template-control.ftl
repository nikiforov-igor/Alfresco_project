<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#if field.control.params.notSelectedOptionLabel??>
    <#assign notSelectedText = field.control.params.notSelectedOptionLabel>
<#elseif field.control.params.notSelectedOptionLabelCode??>
    <#assign notSelectedText = msg(field.control.params.notSelectedOptionLabelCode)>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
    var control = new LogicECM.module.ReportsEditor.SelectReportTemplateCtrl("${fieldHtmlId}").setMessages(${messages});
    control.setOptions(
            {
                reportNodeRef: "${form.arguments.itemId}",
                ctrlValue: "${field.control.params.ctrlValue!"nodeRef"}",
                mandatory: ${isFieldMandatory?string},
                itemType: "${field.endpointType!''}",
                itemFamily: "node",
                maxSearchResults: 30,
                <#if field.control.params.fromParent??>
                    fromParent:"${field.control.params.fromParent?string}",
                </#if>
                oldValue: "${fieldValue}",
                selectedValue: "${fieldValue}",
                nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name} ({lecm-rpeditor:templateCode})'}",
                notSelectedOptionShow: ${field.control.params.notSelectedOption?string},
                notSelectedText: "${notSelectedText?string}",
                fieldId: "${fieldId}"
            });
})();
//]]></script>

<div class="form-field">
    <label for="${fieldHtmlId}-added">${field.label?html}:<#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
    <div id="${fieldHtmlId}-controls" class="selectone-control">
        <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
                <#if field.description??>title="${field.description}"</#if>
                <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
            <#if field.control.params.notSelectedOption?? && (field.control.params.notSelectedOption  == "true")>
                <option value="">
                    <#if field.control.params.notSelectedOptionLabel??>
                            ${field.control.params.notSelectedOptionLabel}
                        <#elseif field.control.params.notSelectedOptionLabelCode??>
                    ${msg(field.control.params.notSelectedOptionLabelCode)}
                    </#if>
                </option>
            </#if>
        </select>
    </div>
</div>