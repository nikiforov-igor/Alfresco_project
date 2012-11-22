<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#if field.control.params.plane?? && field.control.params.plane == "true">
    <#assign plane = true>
<#else>
    <#assign plane = false>
</#if>

<#if field.control.params.refillable?? && field.control.params.refillable == "false">
    <#assign showCreateNewLink = false>
<#else>
    <#assign showCreateNewLink = true>
</#if>

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
                <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
                <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
                <div id="${controlId}-itemGroupActions" class="show-picker">
                    <span class="tree-picker-button">
                        <input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
                    </span>
                    <#if showCreateNewLink>
                        <span class="create-new-button">
                            <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                        </span>
                    </#if>
                </div>

                <@renderTreePickerDialogHTML controlId plane/>
            </#if>
        </div>
    </#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>

<script type="text/javascript">
    <#if field.control.params.selectedValueContextProperty??>
        <#if context.properties[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
        <#elseif args[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
        <#elseif context.properties[field.control.params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
        </#if>
    </#if>

    new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>
            disabled: true,
        </#if>
        <#if field.control.params.rootLocation??>
            rootLocation: "${field.control.params.rootLocation}",
        </#if>
        <#if field.control.params.bigItemIcon??>
            bigItemIcon: "${field.control.params.bigItemIcon}",
        </#if>
        <#if field.control.params.smallItemIcon??>
            smallItemIcon: "${field.control.params.smallItemIcon}",
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
        <#if field.control.params.rootNodeRef??>
            rootNodeRef: "${field.control.params.rootNodeRef}",
        </#if>
        showCreateNewLink: ${showCreateNewLink?string},
        plane: ${plane?string},
        currentValue: "${field.value!''}",
        <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
        itemType: "${field.endpointType}"
    }).setMessages( ${messages} );
</script>