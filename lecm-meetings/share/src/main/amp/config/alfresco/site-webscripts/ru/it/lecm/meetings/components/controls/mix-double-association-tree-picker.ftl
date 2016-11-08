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

<#assign endpointMany = field.endpointMany>
<#if field.control.params.endpointMany??>
    <#assign endpointMany = (field.control.params.endpointMany == "true")>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<#if disabled>
    <div id="${controlId}" class="control association-tree-picker viewmode">
	    <div class="label-div">
		    <#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
		    <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
		    </#if>
		    <label>${field.label?html}:</label>
	    </div>
	    <div class="container">
		    <div class="value-div">
			    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
			    <span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
		    </div>
	    </div>
    </div>
<#else>
	<div class="control association-tree-picker editmode">
		<div class="label-div">
			<label for="${controlId}">
			${field.label?html}:
				<#if field.endpointMandatory!false || field.mandatory!false>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div id="${controlId}" class="container">
			<#if field.disabled == false>
				<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
				<input type="hidden" id="${controlId}-selectedItems"/>

				<div id="${controlId}-itemGroupActions" class="buttons-div">
					<input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
					<input type="button" id="${secondControlId}-tree-picker-button" name="-" value="..."/>
				</div>

				<@renderTreePickerDialogHTML controlId firstPlane firstShowSearch/>
				<@renderTreePickerDialogHTML secondControlId secondPlane secondShowSearch/>
			</#if>

			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>

		</div>
	</div>
</#if>
<div class="clear"></div>

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
    <#elseif form.arguments[field.name]?has_content>
        <#assign renderPickerJSSelectedValue = form.arguments[field.name]/>
    </#if>
	<#assign allowedNodesFirst = "">
	<#assign allowedNodesSecond = "">

	<#if params.firstAllowedNodesFormArgs??>
		<#assign firstAllowedNodesFormArgs = params.firstAllowedNodesFormArgs?split(",")>
		<#list firstAllowedNodesFormArgs as firstAllowedNodesFormArg>
			<#if form.arguments[firstAllowedNodesFormArg]??>
				<#if (allowedNodesFirst?length > 0)>
					<#assign allowedNodesFirst = allowedNodesFirst + ","/>
				</#if>
				<#assign allowedNodesFirst = allowedNodesFirst + form.arguments[firstAllowedNodesFormArg]/>
			</#if>
		</#list>
	</#if>
	<#if params.secondAllowedNodesFormArgs??>
		<#assign secondAllowedNodesFormArgs = params.secondAllowedNodesFormArgs?split(",")>
		<#list secondAllowedNodesFormArgs as secondAllowedNodesFormArg>
			<#if form.arguments[secondAllowedNodesFormArg]??>
				<#if (allowedNodesSecond?length > 0)>
					<#assign allowedNodesSecond = allowedNodesSecond + ","/>
				</#if>
				<#assign allowedNodesSecond = allowedNodesSecond + form.arguments[secondAllowedNodesFormArg]/>
			</#if>
		</#list>
	</#if>
(function() {
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-meetings/components/mix-association-tree-view.js'
		], createDoublePicker);
    }

    var doubleAssociationPickerSelectedItems = {};

    function createDoublePicker() {

	    var fistControl = new LogicECM.module.MixAssociationTreeViewer("${fieldHtmlId}", "-first");
	    fistControl.setOptions({
            fieldId: "${field.configName}",
            formId: "${args.htmlid}",
	        <#if disabled>
	            disabled: true,
	        </#if>
	        <#if params.firstRootLocation??>
	            rootLocation: "${params.firstRootLocation}",
	        </#if>
			<#if params.firstXPathLocation??>
				xPathLocation: "${params.firstXPathLocation}",
			</#if>
			<#if params.firstXPathLocationRoot??>
				xPathLocationRoot: "${params.firstXPathLocationRoot}",
			</#if>
	        <#if field.mandatory??>
	            mandatory: ${field.mandatory?string},
	        <#elseif field.endpointMandatory??>
	            mandatory: ${field.endpointMandatory?string},
	        </#if>
	        multipleSelectMode: ${endpointMany?string},

	        <#if params.firstNameSubstituteString??>
	            nameSubstituteString: "${params.firstNameSubstituteString}",
	        </#if>
            <#if params.firstPickerItemsScript??>
                pickerItemsScript: "${params.firstPickerItemsScript}",
	        </#if>
            <#if field.control.params.sortProp??>
                sortProp: "${field.control.params.sortProp}",
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
	        <#if params.firstChangeItemsFireAction??>
		        changeItemsFireAction: "${params.firstChangeItemsFireAction}",
		    </#if>
	        <#if params.firstCreateDialogClass??>
	            createDialogClass: "${params.firstCreateDialogClass}",
	        </#if>
	        <#if params.firstPickerButtonLabel??>
		        pickerButtonLabel: "${params.firstPickerButtonLabel}",
	        </#if>
            <#if params.firstUseStrictFilterByOrg??>
                useStrictFilterByOrg: "${params.firstUseStrictFilterByOrg?string}",
            </#if>
            <#if params.firstIgnoreNodesInTreeView??>
                ignoreNodesInTreeView: ${params.firstIgnoreNodesInTreeView?string},
            </#if>
	        <#if params.firstPickerButtonTitle??>
		        pickerButtonTitle: "${params.firstPickerButtonTitle}",
	        <#elseif params.firstPickerButtonTitleCode??>
		        pickerButtonTitle: "${msg(params.firstPickerButtonTitleCode)}",
	        </#if>
            <#if args.ignoreNodes??>
                ignoreNodes: "${args.ignoreNodes}".split(","),
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
            additionalFilter: "${params.firstAdditionalFilter!''}",
			<#if params.itemTypeSubstituteStrings??>
				itemTypeSubstituteStrings: "${params.itemTypeSubstituteStrings}",
			</#if>
			<#if (allowedNodesFirst?length > 0)>
				allowedNodes: "${allowedNodesFirst}".split(","),
			</#if>
		    clearFormsOnStart: true
	    });
	    fistControl.setMessages(${messages});

	    var secondControl = new LogicECM.module.MixAssociationTreeViewer("${fieldHtmlId}", "-second");
	    secondControl.setOptions({
            fieldId: "${field.configName}-second",
            formId: "${args.htmlid}",
		    prefixPickerId: "${secondControlId}",
		    <#if disabled>
			    disabled: true,
		    </#if>
		    <#if params.secondRootLocation??>
			    rootLocation: "${params.secondRootLocation}",
		    </#if>
			<#if params.secondXPathLocation??>
				xPathLocation: "${params.secondXPathLocation}",
			</#if>
			<#if params.secondXPathLocationRoot??>
				xPathLocationRoot: "${params.secondXPathLocationRoot}",
			</#if>
		    <#if field.mandatory??>
			    mandatory: ${field.mandatory?string},
		    <#elseif field.endpointMandatory??>
			    mandatory: ${field.endpointMandatory?string},
		    </#if>
		    <#if params.secondNameSubstituteString??>
			    nameSubstituteString: "${params.secondNameSubstituteString}",
		    </#if>
            <#if params.secondPickerItemsScript??>
                pickerItemsScript: "${params.secondPickerItemsScript}",
            </#if>
            <#if field.control.params.sortProp??>
                sortProp: "${field.control.params.sortProp}",
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
            <#if params.secondChangeItemsFireAction??>
                changeItemsFireAction: "${params.secondChangeItemsFireAction}",
            </#if>
		    <#if params.secondCreateDialogClass??>
			    createDialogClass: "${params.secondCreateDialogClass}",
		    </#if>
		    <#if params.secondPickerButtonLabel??>
			    pickerButtonLabel: "${params.secondPickerButtonLabel}",
		    </#if>
            <#if params.secondUseStrictFilterByOrg??>
                useStrictFilterByOrg: "${params.secondUseStrictFilterByOrg?string}",
            </#if>
            <#if params.secondIgnoreNodesInTreeView??>
                ignoreNodesInTreeView: ${params.secondIgnoreNodesInTreeView?string},
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
            <#if args.ignoreNodes??>
                ignoreNodes: "${args.ignoreNodes}".split(","),
            </#if>
		    showCreateNewLink: false,
            additionalFilter: "${params.secondAdditionalFilter!''}",
		    showSelectedItemsPath: false,
		    multipleSelectMode: ${endpointMany?string},
		    showSearch: ${secondShowSearch?string},
		    plane: ${secondPlane?string},
		    currentValue: "${field.value!''}",
			<#if params.itemTypeSubstituteStrings??>
				itemTypeSubstituteStrings: "${params.itemTypeSubstituteStrings}",
			</#if>
			<#if (allowedNodesSecond?length > 0)>
				allowedNodes: "${allowedNodesSecond}".split(","),
			</#if>
		    clearFormsOnStart: true
	    });
	    secondControl.setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
</script>