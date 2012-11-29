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

<#if field.control.params.showCreateNewLink?? && field.control.params.showCreateNewLink == "false">
    <#assign showCreateNewLink = false>
<#else>
    <#assign showCreateNewLink = true>
</#if>

<#if field.control.params.showSearch?? && field.control.params.showSearch == "false">
	<#assign showSearch = false>
<#else>
	<#assign showSearch = true>
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
	            <#if field.mandatory??>
		            mandatory: ${field.mandatory?string},
	            <#elseif field.endpointMandatory??>
		            mandatory: ${field.endpointMandatory?string},
	            </#if>
                multipleSelectMode: ${field.endpointMany?string},
                itemType: "${field.endpointType}",
                currentValue: "${field.value!''}",
                itemFamily: "node",
                maxSearchResults: ${field.control.params.maxSearchResults!'1000'},
                selectedValueNodeRef: "${fieldValue}",
	            <#if field.control.params.selectedItemsNameSubstituteString??>
		            selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
	            </#if>
                nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}"
            });


    new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
            <#if form.mode == "view" || field.disabled>
                disabled: true,
            </#if>
            <#if field.control.params.startLocation??>
                rootLocation: "${field.control.params.startLocation}",
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
            <#if field.control.params.parentNodeRef??>
                rootNodeRef: "${field.control.params.parentNodeRef}",
            </#if>
            showCreateNewLink: ${showCreateNewLink?string},
	        showSearch: ${showSearch?string},
            changeItemsFireAction: "refreshAutocompleteItemList_${fieldHtmlId}",
            plane: true,
            currentValue: "${field.value!''}",
            itemType: "${field.endpointType}"
        }).setMessages( ${messages} );
})();
//]]></script>

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
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
        <input type="hidden" id="${controlId}-selectedItems"/>

        <div id="${controlId}-autocomplete">
            <input id="${controlId}-autocomplete-input" type="text" class="autocomplete-input"/>
            <span class="tree-picker-button">
                <input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
            </span>
            <#if showCreateNewLink>
                <span class="create-new-button">
                    <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                </span>
            </#if>
            <div id="${controlId}-autocomplete-container"></div>

            <@renderTreePickerDialogHTML controlId true showSearch/>
        </div>

        <div class="<#if field.endpointMany>autocompleteCurrentValueDisplay<#else>autocompleteCurrentValueDisplayInvisible</#if>" id="${controlId}-currentValueDisplayDiv" >
            <span id="${controlId}-currentValueDisplay" class="viewmode-value" />
        </div>
    </#if>
    <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
</div>