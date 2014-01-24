<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign params = field.control.params>

<#assign controlId = fieldHtmlId + "-cntrl">
<#if params.firstPlane?? && params.firstPlane == "true">
    <#assign firstPlane = true>
<#else>
    <#assign firstPlane = false>
</#if>
<#if params.firstShowSearch?? && params.firstShowSearch == "false">
	<#assign firstShowSearch = false>
<#else>
	<#assign firstShowSearch = true>
</#if>

<#assign secondControlId = fieldHtmlId + "-second-cntrl">
<#if params.secondPlane?? && params.secondPlane == "true">
	<#assign secondPlane = true>
<#else>
	<#assign secondPlane = false>
</#if>
<#if params.secondShowSearch?? && params.secondShowSearch == "false">
	<#assign secondShowSearch = false>
<#else>
	<#assign secondShowSearch = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<div class="form-field">
    <#if disabled>
        <div id="${controlId}" class="viewmode-field">
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

	                <span class="tree-picker-button">
						<input type="button" id="${secondControlId}-tree-picker-button" name="-" value="..."/>
					</span>
                </div>

                <@renderTreePickerDialogHTML controlId firstPlane firstShowSearch/>
                <@renderTreePickerDialogHTML secondControlId secondPlane secondShowSearch/>
            </#if>

            <div class="clear"></div>
        </div>
    </#if>

    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>

<script type="text/javascript">
    <#if params.selectedValueContextProperty??>
        <#if context.properties[params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[params.selectedValueContextProperty]>
        <#elseif args[params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = args[params.selectedValueContextProperty]>
        <#elseif context.properties[params.selectedValueContextProperty]??>
            <#assign renderPickerJSSelectedValue = context.properties[params.selectedValueContextProperty]>
        </#if>
    </#if>
    <#if !(renderPickerJSSelectedValue??) && params.selectedItemsFormArgs??>
	    <#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
	    <#list selectedItemsFormArgs as selectedItemsFormArg>
		    <#if form.arguments[selectedItemsFormArg]??>
		        <#if !renderPickerJSSelectedValue??>
			        <#assign renderPickerJSSelectedValue = ""/>
		        </#if>
			    <#if (renderPickerJSSelectedValue?length > 0)>
				    <#assign renderPickerJSSelectedValue = renderPickerJSSelectedValue + ","/>
			    </#if>
			    <#assign renderPickerJSSelectedValue = renderPickerJSSelectedValue + form.arguments[selectedItemsFormArg]/>
		    </#if>
	    </#list>
    </#if>

    var fistControl = new LogicECM.module.AssociationTreeViewer("${fieldHtmlId}");
    fistControl.setOptions({
        <#if disabled>
            disabled: true,
        </#if>
        <#if params.firstRootLocation??>
            rootLocation: "${params.firstRootLocation}",
        </#if>
        <#if field.mandatory??>
            mandatory: ${field.mandatory?string},
        <#elseif field.endpointMandatory??>
            mandatory: ${field.endpointMandatory?string},
        </#if>
        multipleSelectMode: ${field.endpointMany?string},

        <#if params.firstNameSubstituteString??>
            nameSubstituteString: "${params.firstNameSubstituteString}",
        </#if>
	    <#if params.firstSelectedItemsNameSubstituteString??>
		    selectedItemsNameSubstituteString: "${params.firstSelectedItemsNameSubstituteString}",
	    </#if>
        <#if params.firstTreeNodeSubstituteString??>
            treeNodeSubstituteString: "${params.firstTreeNodeSubstituteString}",
	    </#if>
        <#if params.firstTreeNodeTitleSubstituteString??>
            treeNodeTitleSubstituteString: "${params.firstTreeNodeTitleSubstituteString}",
	    </#if>
	    <#if params.firstTreeItemType??>
		    treeItemType: "${params.firstTreeItemType}",
	    </#if>
        <#--<#if params.changeItemsFireAction??>-->
	        <#--changeItemsFireAction: "${params.changeItemsFireAction}",-->
	    <#--</#if>-->
        <#if params.firstCreateDialogClass??>
            createDialogClass: "${params.firstCreateDialogClass}",
        </#if>
        <#if params.firstPickerButtonLabel??>
	        pickerButtonLabel: "${params.firstPickerButtonLabel}",
        </#if>
        <#if params.firstPickerButtonTitle??>
	        pickerButtonTitle: "${params.firstPickerButtonTitle}",
        <#elseif params.firstPickerButtonTitleCode??>
	        pickerButtonTitle: "${msg(params.firstPickerButtonTitleCode)}",
        </#if>
	    showSearch: ${firstShowSearch?string},
        plane: ${firstPlane?string},
        currentValue: "${field.value!''}",
        <#if renderPickerJSSelectedValue??>
	        selectedValue: "${renderPickerJSSelectedValue}",
        </#if>
	    <#if params.firstItemType??>
	        itemType: "${params.firstItemType}",
	    <#else>
		    itemType: "${field.endpointType! params.endpointType}",
	    </#if>
	    showCreateNewLink: false,
	    clearFormsOnStart: false
    });
    fistControl.setMessages(${messages});

    var secondControl = new LogicECM.module.AssociationTreeViewer("${fieldHtmlId}");
    secondControl.setOptions({
	    prefixPickerId: "${secondControlId}",
	    <#if disabled>
		    disabled: true,
	    </#if>
	    <#if params.secondRootLocation??>
		    rootLocation: "${params.secondRootLocation}",
	    </#if>
	    <#if field.mandatory??>
		    mandatory: ${field.mandatory?string},
	    <#elseif field.endpointMandatory??>
		    mandatory: ${field.endpointMandatory?string},
	    </#if>
	    <#if params.secondNameSubstituteString??>
		    nameSubstituteString: "${params.secondNameSubstituteString}",
	    </#if>
	    <#if params.secondSelectedItemsNameSubstituteString??>
		    selectedItemsNameSubstituteString: "${params.secondSelectedItemsNameSubstituteString}",
	    </#if>
	    <#if params.secondTreeNodeSubstituteString??>
		    treeNodeSubstituteString: "${params.secondTreeNodeSubstituteString}",
	    </#if>
	    <#if params.secondTreeNodeTitleSubstituteString??>
		    treeNodeTitleSubstituteString: "${params.secondTreeNodeTitleSubstituteString}",
	    </#if>
	    <#if params.secondTreeItemType??>
		    treeItemType: "${params.secondTreeItemType}",
	    </#if>
	    <#--<#if params.changeItemsFireAction??>-->
	    <#--changeItemsFireAction: "${params.changeItemsFireAction}",-->
	    <#--</#if>-->
	    <#if params.secondCreateDialogClass??>
		    createDialogClass: "${params.secondCreateDialogClass}",
	    </#if>
	    <#if params.secondPickerButtonLabel??>
		    pickerButtonLabel: "${params.secondPickerButtonLabel}",
	    </#if>
	    <#if params.secondPickerButtonTitle??>
		    pickerButtonTitle: "${params.secondPickerButtonTitle}",
	    <#elseif params.secondPickerButtonTitleCode??>
		    pickerButtonTitle: "${msg(params.secondPickerButtonTitleCode)}",
	    </#if>
	    <#if renderPickerJSSelectedValue??>
		    selectedValue: "${renderPickerJSSelectedValue}",
	    </#if>
	    <#if params.secondItemType??>
		    itemType: "${params.secondItemType}",
	    <#else>
		    itemType: "${field.endpointType! params.endpointType}",
	    </#if>
	    showCreateNewLink: false,
	    showSelectedItemsPath: false,
	    multipleSelectMode: ${field.endpointMany?string},
	    showSearch: ${secondShowSearch?string},
	    plane: ${secondPlane?string},
	    currentValue: "${field.value!''}",
	    clearFormsOnStart: false
    });
    secondControl.setMessages(${messages});
</script>