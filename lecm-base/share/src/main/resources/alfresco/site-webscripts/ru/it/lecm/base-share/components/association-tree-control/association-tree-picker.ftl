<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">

<div class="form-field">
<#if form.mode == "view">
    <div id="${controlId}" class="viewmode-field">
        <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
    </div>
<#else>
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <div id="${controlId}" class="object-finder">

        <div id="${controlId}-currentValueDisplay" class="current-values"></div>

        <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"  value="${field.value!""}"/>
            <div id="${controlId}-itemGroupActions" class="show-picker">
                <input type="button" id="${controlId}-orgchart-picker-button" name="-"
                value="${field.control.params.selectActionLabel!msg("button.select")}" onclick="showTreePicker();"/>
            </div>

            <@renderOrgchartPickerDialogHTML controlId />
        </#if>
    </div>
</#if>
</div>

<script type="text/javascript">
    new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>
            disabled: true,
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
        <#if field.control.params.roteNodeRef??>
            roteNodeRef: "${field.control.params.roteNodeRef}",
        </#if>
        itemType: "${field.endpointType}",
        mode: "picker"
    }).setMessages( ${messages} );
</script>