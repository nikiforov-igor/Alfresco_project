<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#if field.control.params.plane?? && field.control.params.plane == "true">
    <#assign plane = true>
<#else>
    <#assign plane = false>
</#if>

<#if field.control.params.showCreateNewLink?? && field.control.params.showCreateNewLink == "false">
    <#assign showCreateNewLink = false>
<#else>
    <#assign showCreateNewLink = true>
</#if>

<#if field.control.params.showCreateNewButton?? && field.control.params.showCreateNewButton == "false">
	<#assign showCreateNewButton = false>
<#else>
	<#assign showCreateNewButton = true>
</#if>

<#if field.control.params.showSelectedItemsPath?? && field.control.params.showSelectedItemsPath == "false">
	<#assign showSelectedItemsPath = false>
<#else>
	<#assign showSelectedItemsPath = true>
</#if>

<#if field.control.params.showSearch?? && field.control.params.showSearch == "false">
	<#assign showSearch = false>
<#else>
	<#assign showSearch = true>
</#if>

<div class="form-field">
    <#if form.mode == "view">
        <div id="${controlId}" class="viewmode-field">
            <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
            </#if>
            <span class="viewmode-label">${field.label?html}:</span>
            <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
        </div>
    <#else>
        <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
        <div id="${controlId}" class="object-finder">

            <div id="${controlId}-currentValueDisplay" class="current-values"></div>

            <#if field.disabled == false>
                <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
                <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
	            <input type="hidden" id="${controlId}-selectedItems"/>

                <div id="${controlId}-itemGroupActions" class="show-picker">
                    <span class="tree-picker-button">
                        <input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
                    </span>
                    <#if showCreateNewButton>
                        <span class="create-new-button">
                            <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                        </span>
                    </#if>
                </div>

                <@renderTreePickerDialogHTML controlId plane showSearch/>
            </#if>

            <div class="clear"></div>
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
        <#if field.mandatory??>
            mandatory: ${field.mandatory?string},
        <#elseif field.endpointMandatory??>
            mandatory: ${field.endpointMandatory?string},
        </#if>
        multipleSelectMode: ${field.endpointMany?string},

        <#if field.control.params.nameSubstituteString??>
            nameSubstituteString: "${field.control.params.nameSubstituteString}",
        </#if>
	    <#if field.control.params.selectedItemsNameSubstituteString??>
		    selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
	    </#if>
        <#if field.control.params.rootNodeRef??>
            rootNodeRef: "${field.control.params.rootNodeRef}",
        </#if>
	    <#if field.control.params.treeItemType??>
		    treeItemType: "${field.control.params.treeItemType}",
	    </#if>
        showCreateNewLink: ${showCreateNewLink?string},
	    showSearch: ${showSearch?string},
        plane: ${plane?string},
        currentValue: "${field.value!''}",
        showSelectedItemsPath: ${showSelectedItemsPath?string},
        <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
        itemType: "${field.endpointType}"
    }).setMessages( ${messages} );
</script>