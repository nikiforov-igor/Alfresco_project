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

<#if field.control.params.showViewIncompleteWarning?? && field.control.params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
<#else>
	<#assign showViewIncompleteWarning = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<div class="form-field">
    <#if disabled>
        <div id="${controlId}" class="viewmode-field">
            <#if showViewIncompleteWarning && (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
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
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
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
    <#assign optionSeparator="|">
    <#assign labelSeparator=":">


    new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if disabled>
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
        <#if field.control.params.treeNodeSubstituteString??>
            treeNodeSubstituteString: "${field.control.params.treeNodeSubstituteString}",
	    </#if>
        <#if field.control.params.treeNodeTitleSubstituteString??>
            treeNodeTitleSubstituteString: "${field.control.params.treeNodeTitleSubstituteString}",
	    </#if>
        <#if field.control.params.rootNodeRef??>
            rootNodeRef: "${field.control.params.rootNodeRef}",
        </#if>
	    <#if field.control.params.treeItemType??>
		    treeItemType: "${field.control.params.treeItemType}",
	    </#if>
        <#if field.control.params.changeItemsFireAction??>
	        changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
	    </#if>
        <#if args.ignoreNodes??>
            ignoreNodes: "${args.ignoreNodes}".split(","),
	    </#if>
        showCreateNewLink: ${showCreateNewLink?string},
		<#if field.control.params.createNewMessage??>
			createNewMessage: "${field.control.params.createNewMessage}",
		<#elseif field.control.params.createNewMessageId??>
			createNewMessage: "${msg(field.control.params.createNewMessageId)}",
	    </#if>
	    showSearch: ${showSearch?string},
        plane: ${plane?string},
        currentValue: "${field.value!''}",
        showSelectedItemsPath: ${showSelectedItemsPath?string},
        <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
	    <#if field.control.params.fireAction?? && field.control.params.fireAction != "">
	    fireAction: {
		    <#list field.control.params.fireAction?split(optionSeparator) as typeValue>
			    <#if typeValue?index_of(labelSeparator) != -1>
				    <#assign type=typeValue?split(labelSeparator)>
				    <#if type[0] == "addItem">
					    addItem: "${type[1]}",
				    </#if>
				    <#if type[0] == "cancel">
					    cancel: "${type[1]}",
				    </#if>
			    </#if>
		    </#list>
	    },
	    </#if>
        itemType: "${field.endpointType ! field.control.params.endpointType}"
    }).setMessages( ${messages} );
</script>