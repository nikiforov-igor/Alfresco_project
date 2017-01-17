<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign selectedValue = "">
<#assign params = field.control.params>
<#if field.control.params.plane?? && field.control.params.plane == "true">
    <#assign plane = true>
<#else>
    <#assign plane = false>
</#if>

<#assign readonly = false>
<#assign defaultValue=field.control.params.defaultValue!"">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue=form.arguments[field.name]>
<#elseif form.arguments['readonly_' + field.name]?has_content>
	<#assign defaultValue=form.arguments['readonly_' + field.name]>
	<#assign readonly = true>
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

<#if field.control.params.showAssocViewForm?? && field.control.params.showAssocViewForm == "true">
	<#assign showAssocViewForm = true>
<#else>
	<#assign showAssocViewForm = false>
</#if>
<#if field.control.params.checkType?? && field.control.params.checkType == "false">
	<#assign checkType = false>
<#else>
	<#assign checkType = true>
</#if>

<#assign optionSeparator="|">
<#assign labelSeparator=":">

<#if field.control.params.selectedValueContextProperty??>
    <#if context.properties[field.control.params.selectedValueContextProperty]??>
        <#assign selectedValue = context.properties[field.control.params.selectedValueContextProperty]>
    <#elseif args[field.control.params.selectedValueContextProperty]??>
        <#assign selectedValue = args[field.control.params.selectedValueContextProperty]>
    <#elseif context.properties[field.control.params.selectedValueContextProperty]??>
        <#assign selectedValue = context.properties[field.control.params.selectedValueContextProperty]>
    </#if>
</#if>

<#if selectedValue == "" && params.selectedItemsFormArgs??>
    <#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
    <#list selectedItemsFormArgs as selectedItemsFormArg>
        <#if form.arguments[selectedItemsFormArg]??>
            <#if !selectedValue??>
                <#assign selectedValue = ""/>
            </#if>
            <#if (selectedValue?length > 0)>
                <#assign selectedValue = selectedValue + ","/>
            </#if>
            <#assign selectedValue = selectedValue + form.arguments[selectedItemsFormArg]/>
        </#if>
    </#list>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if disabled>
    <div id="${controlId}" class="control association-tree-picker viewmode">
        <div class="label-div">
            <#if showViewIncompleteWarning && (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
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
                <#if showCreateNewButton>
                <span class="create-new-button">
                    <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                </span>
                </#if>
            </div>

            <@renderTreePickerDialogHTML controlId plane showSearch/>
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
            'scripts/lecm-base/components/association-tree/association-tree-view.js',
            'modules/simple-dialog.js'
   		], createAssociationTreeViewer);
   	}
 	function createAssociationTreeViewer(){
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
        <#if field.control.params.sortProp??>
            sortProp: "${field.control.params.sortProp}",
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
	    showCreateNewButton: ${showCreateNewButton?string},
		<#if field.control.params.createNewMessage??>
			createNewMessage: "${field.control.params.createNewMessage}",
		<#elseif field.control.params.createNewMessageId??>
			createNewMessage: "${msg(field.control.params.createNewMessageId)}",
	    </#if>
        <#if field.control.params.createDialogClass??>
            createDialogClass: "${field.control.params.createDialogClass}",
        </#if>
	    showSearch: ${showSearch?string},
        plane: ${plane?string},
        currentValue: "${field.value!''}",
        <#if selectedValue?has_content>
            selectedValue: "${selectedValue}",
        </#if>
        showSelectedItemsPath: ${showSelectedItemsPath?string},
        <#if field.control.params.defaultValueDataSource??>
            defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
        </#if>
        <#if field.control.params.useStrictFilterByOrg??>
            useStrictFilterByOrg: "${field.control.params.useStrictFilterByOrg?string}",
        </#if>
        <#if field.control.params.doNotCheckAccess??>
            doNotCheckAccess: ${field.control.params.doNotCheckAccess?string},
        </#if>
        <#if field.control.params.childrenDataSource??>
            childrenDataSource: "${field.control.params.childrenDataSource}",
        </#if>
        <#if defaultValue?has_content>
            defaultValue: "${defaultValue?string}",
        </#if>
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
        itemType: "${field.control.params.endpointType ! field.endpointType}",
	    additionalFilter: "${field.control.params.additionalFilter!''}",
	    showAssocViewForm: ${showAssocViewForm?string},
	    checkType: ${checkType?string},
		<#if field.configName??>
			fieldId: "${field.configName}",
		</#if>
		<#if args.htmlid??>
			formId: "${args.htmlid}"
		</#if>
    }).setMessages( ${messages} );
	<#if readonly>
		LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
	</#if>
 	}
 	YAHOO.util.Event.onDOMReady(init);
})();
</script>
