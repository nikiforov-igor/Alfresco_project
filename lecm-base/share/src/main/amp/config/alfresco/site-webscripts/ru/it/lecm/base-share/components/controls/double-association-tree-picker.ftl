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
<#if params.firstShowCreateNewLink?? && params.firstShowCreateNewLink == "true">
	<#assign firstShowCreateNewLink = true>
<#else>
	<#assign firstShowCreateNewLink = false>
</#if>

<#assign firstAllowedNodesScript = "">
<#if field.control.params.firstAllowedNodesScript?has_content>
    <#assign firstAllowedNodesScript = field.control.params.firstAllowedNodesScript?string>
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
<#if params.secondShowCreateNewLink?? && params.secondShowCreateNewLink == "true">
    <#assign secondShowCreateNewLink = true>
<#else>
    <#assign secondShowCreateNewLink = false>
</#if>

<#if params.mixTypes?? && params.mixTypes?string == "true">
    <#assign mixTypes = true>
<#else>
    <#assign mixTypes = false>
</#if>

<#assign endpointMany = field.endpointMany>
<#if field.control.params.endpointMany??>
    <#assign endpointMany = (field.control.params.endpointMany == "true")>
</#if>

<#assign showAssocViewForm = false>
<#if params.showAssocViewForm?? && params.showAssocViewForm == "true">
	<#assign showAssocViewForm = true>
</#if>

<#assign secondShowAssocViewForm = false>
<#if params.secondShowAssocViewForm?? && params.secondShowAssocViewForm == "true">
	<#assign secondShowAssocViewForm = true>
</#if>

<#assign secondAllowedNodesScript = "">
<#if field.control.params.secondAllowedNodesScript?has_content>
    <#assign secondAllowedNodesScript = field.control.params.secondAllowedNodesScript?string>
</#if>

<#if params.clearFormsOnStart?? && params.clearFormsOnStart == "true">
	<#assign clearFormsOnStart = true>
<#else>
	<#assign clearFormsOnStart = false>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>
<#assign readonly = false>

<#if params.selectedValueContextProperty??>
	<#if context.properties[params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[params.selectedValueContextProperty]>
	<#elseif args[params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = args[params.selectedValueContextProperty]>
	<#elseif context.properties[params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[params.selectedValueContextProperty]>
	</#if>
</#if>

<#if form.arguments[field.name]?has_content>
	<#assign renderPickerJSSelectedValue = form.arguments[field.name]/>
<#elseif form.arguments['readonly_' + field.name]?has_content>
	<#assign renderPickerJSSelectedValue=form.arguments['readonly_' + field.name]>
	<#assign readonly = true>
<#elseif params.selectedItemsFormArgs??>
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
(function() {
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/association-tree/association-tree-view.js'
		], createDoublePicker);
    }

    var doubleAssociationPickerSelectedItems = {};

    function createDoublePicker() {

	    var fistControl = new LogicECM.module.AssociationTreeViewer("${fieldHtmlId}", "-first");
	    fistControl.setOptions({
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
            <#if field.control.params.firstSortProp??>
                sortProp: "${field.control.params.firstSortProp}",
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
			<#if params.firstPathNameSubstituteString??>
				pathNameSubstituteString: "${params.firstPathNameSubstituteString}",
			</#if>
		    showSearch: ${firstShowSearch?string},
	        plane: ${firstPlane?string},
	        currentValue: "${field.value!''}",
	        <#if renderPickerJSSelectedValue??>
		        selectedValue: "${renderPickerJSSelectedValue}",
	        </#if>
	        <#if firstAllowedNodesScript?has_content>
		        allowedNodesScript: "${firstAllowedNodesScript}",
	        </#if>
		    <#if params.firstItemType??>
		        itemType: "${params.firstItemType}",
		    <#else>
			    itemType: "${params.endpointType! field.endpointType}",
		    </#if>
			<#if params.markNodes?? && params.markNodes == "true">
			    markNodes: true,
			</#if>
		    showCreateNewLink: ${firstShowCreateNewLink?string},
            additionalFilter: "${params.firstAdditionalFilter!''}",
            showAssocViewForm: ${showAssocViewForm?string},
		    clearFormsOnStart: ${clearFormsOnStart?string},
			fieldId: "${field.configName}-first",
			formId: "${args.htmlid}",
			<#if params.selectedItemsNameSubstituteString?has_content>
			selectedItemsNameSubstituteString: "${params.selectedItemsNameSubstituteString}",
			</#if>
			checkType: ${(!mixTypes)?string}
	    });
	    fistControl.setMessages(${messages});

	    var secondControl = new LogicECM.module.AssociationTreeViewer("${fieldHtmlId}", "-second");
	    secondControl.setOptions({
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
            <#if field.control.params.secondSortProp??>
                sortProp: "${field.control.params.secondSortProp}",
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
			    itemType: "${params.endpointType! field.endpointType}",
		    </#if>
			<#if params.markNodes?? && params.markNodes == "true">
				markNodes: true,
			</#if>
            <#if args.ignoreNodes??>
                ignoreNodes: "${args.ignoreNodes}".split(","),
            </#if>
			<#if secondAllowedNodesScript?has_content>
		        allowedNodesScript: "${secondAllowedNodesScript}",
	        </#if>
		    showCreateNewLink: ${secondShowCreateNewLink?string},
            additionalFilter: "${params.secondAdditionalFilter!''}",
		    showSelectedItemsPath: false,
		    multipleSelectMode: ${endpointMany?string},
            showAssocViewForm: ${secondShowAssocViewForm?string},
		    showSearch: ${secondShowSearch?string},
		    plane: ${secondPlane?string},
		    currentValue: "${field.value!''}",
		    clearFormsOnStart: ${clearFormsOnStart?string},
			fieldId: "${field.configName}-second",
			formId: "${args.htmlid}",
			<#if params.selectedItemsNameSubstituteString?has_content>
			selectedItemsNameSubstituteString: "${params.selectedItemsNameSubstituteString}",
			</#if>
			checkType: ${(!mixTypes)?string}
	    });
	    secondControl.setMessages(${messages});
		<#if readonly>
			LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}-first', true);
			LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}-second', true);
		</#if>
	}
	YAHOO.util.Event.onDOMReady(init);
})();
</script>
