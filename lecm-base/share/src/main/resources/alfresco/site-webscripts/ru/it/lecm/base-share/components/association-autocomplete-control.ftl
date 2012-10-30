<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-control/association-tree-picker-dialog.inc.ftl">

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

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
                <#if field.control.params.startLocation??>
                    startLocation: "${field.control.params.startLocation}",
                </#if>
                <#if field.control.params.itemIcon??>
                    itemIcon: "${field.control.params.itemIcon}",
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


    new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if form.mode == "view" || field.disabled>
            disabled: true,
        </#if>
        <#if field.control.params.startLocation??>
            rootLocation: "${field.control.params.startLocation}",
        </#if>
        <#if field.control.params.bigItemIcon??>
            bigItemIcon: "${field.control.params.bigItemIcon}",
        </#if>
        <#if field.control.params.itemIcon??>
            smallItemIcon: "${field.control.params.itemIcon}",
        </#if>
        <#if field.mandatory??>
            mandatory: ${field.mandatory?string},
        <#elseif field.endpointMandatory??>
            mandatory: ${field.endpointMandatory?string},
        </#if>
            multipleSelectMode: ${field.endpointMany?string},

        <#if field.control.params.nameSubstituteString??>
            nameSubstituteString: "${field.control.params.nameSubstituteString}",
        </#if>
        <#if field.control.params.parentNodeRef??>
            rootNodeRef: "${field.control.params.parentNodeRef}",
        </#if>
            changeItemsFireAction: "refreshAutocompleteItemList",
            showCreateNewLink: true,
            plane: true,
            currentValue: "${field.value!''}",
            itemType: "${field.endpointType}"
        }).setMessages( ${messages} );
})();
//]]></script>

<style type="text/css">
    #${controlId}-autocomplete {padding-bottom:1em;}
    #${controlId}-autocomplete-input {width:20em;}
    #${controlId}-tree-picker-button {margin-left:21em;}
</style>

<div class="form-field">
    <#if form.mode == "view">
        <div class="viewmode-field">
            <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
            </#if>
            <span class="viewmode-label">${field.label?html}:</span>
            <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
        </div>
    <#else>
        <label for="${controlId}-autocomplete-input">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added" value="${fieldValue}"/>

        <div id="${controlId}-autocomplete">
            <input id="${controlId}-autocomplete-input" type="text"/>
            <input type="button" id="${controlId}-tree-picker-button" name="-" value="${field.control.params.selectActionLabel!msg("button.select")}"/>
            <div id="${controlId}-autocomplete-container"></div>

            <@renderTreePickerDialogHTML controlId true/>
        </div>

        <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
    </#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>