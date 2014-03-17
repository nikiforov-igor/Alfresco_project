<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#assign defaultValue=field.control.params.defaultValue!"">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue=form.arguments[field.name]>
</#if>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if field.control.params.showCreateNewButton?? && field.control.params.showCreateNewButton == "false">
    <#assign showCreateNewButton = false>
<#else>
    <#assign showCreateNewButton = true>
</#if>

<#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
	<#assign notSelectedOptionShow = true>
<#else>
	<#assign notSelectedOptionShow = false>
</#if>

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

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if notSelectedOptionShow>
    <#if field.control.params.notSelectedOptionLabel??>
        <#assign notSelectedText = field.control.params.notSelectedOptionLabel>
    <#elseif field.control.params.notSelectedOptionLabelCode??>
        <#assign notSelectedText = msg(field.control.params.notSelectedOptionLabelCode)>
    </#if>
<#else>
    <#assign notSelectedText = "">
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
    var control = new LogicECM.module.AssociationSelectOne("${fieldHtmlId}").setMessages(${messages});
    control.setOptions(
            {
	            <#if disabled>
		            disabled: true,
	            </#if>
                <#if field.control.params.parentNodeRef??>
                    parentNodeRef: "${field.control.params.parentNodeRef}",
                </#if>
                <#if field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation}",
                </#if>
                mandatory: ${isFieldMandatory?string},
                itemType: "${ field.control.params.endpointType ! field.endpointType! }",
                itemFamily: "node",
                maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                oldValue: "${fieldValue}",
                selectedValueNodeRef: "${fieldValue}",
                nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
                showCreateNewButton: ${showCreateNewButton?string},
	            notSelectedOptionShow: ${notSelectedOptionShow?string},
	            notSelectedText: "${notSelectedText?string}",
	            <#if field.control.params.primaryCascading??>
                    primaryCascading: ${field.control.params.primaryCascading},
		        </#if>
                <#if field.control.params.changeItemsFireAction??>
                    changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
                </#if>
	            <#if field.control.params.defaultValueDataSource??>
		            defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
	            </#if>
                <#if defaultValue?has_content>
                    defaultValue: "${defaultValue?string}",
                </#if>
                fieldId: "${fieldId}"
            });
})();
//]]></script>

<div class="form-field">
    <#if disabled>
        <div class="viewmode-field">
	        <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value}" />

            <#if isFieldMandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
            </#if>
            <span class="viewmode-label">${field.label?html}:</span>
            <span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value"></span>
        </div>
    <#else>
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
                <#if notSelectedOptionShow>
                    <option value="">
                        <#if field.control.params.notSelectedOptionLabel??>
                            ${field.control.params.notSelectedOptionLabel}
                        <#elseif field.control.params.notSelectedOptionLabelCode??>
                            ${msg(field.control.params.notSelectedOptionLabelCode)}
                        </#if>
                    </option>
                </#if>
            </select>
            <#if showCreateNewButton>
            <div class="show-picker">
                <span class="create-new-button">
                    <input type="button" id="${fieldHtmlId}-selectone-create-new-button" name="-"/>
                </span>
            </div>
            </#if>
        </div>

    </#if>
</div>