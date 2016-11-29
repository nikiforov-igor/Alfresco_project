<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-picker-dialog.inc.ftl">


<#assign defaultValue=field.control.params.defaultValue!"">

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<#assign autoCompleteJsName = field.control.params.autoCompleteJsName ! "${args.htmlid}-${fieldHtmlId}-auto-complete">
<#assign treeViewJsName = field.control.params.treeViewJsName ! "${args.htmlid}-${fieldHtmlId}-tree-view">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if form.mode == "create" && !field.disabled && fieldValue?string == "">
    <#if field.control.params.selectedItemsFormArgs??>
        <#assign selectedItemsFormArgs = field.control.params.selectedItemsFormArgs?split(",")>
        <#list selectedItemsFormArgs as selectedItemsFormArg>
            <#if form.arguments[selectedItemsFormArg]??>
                <#if (fieldValue?length > 0)>
                    <#assign fieldValue = fieldValue + ","/>
                </#if>
                <#assign fieldValue = fieldValue + form.arguments[selectedItemsFormArg]/>
            </#if>
        </#list>
    <#elseif form.arguments[field.name]?has_content>
        <#assign fieldValue = form.arguments[field.name]/>
    </#if>
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

<#assign useDynamicLoading = true>
<#if field.control.params.useDynamicLoading?? && field.control.params.useDynamicLoading == "false">
	<#assign useDynamicLoading = false>
</#if>

<#assign showMandatoryIndicator = true>
<#if field.control.params.showMandatoryIndicator?? && field.control.params.showMandatoryIndicator == "false">
	<#assign showMandatoryIndicator = false>
</#if>

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<#if field.control.params.checkType?? && field.control.params.checkType == "false">
    <#assign checkType = false>
<#else>
    <#assign checkType = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>
<#assign allowedScript = ""/>
<#if (field.control.params.allowedNodesScript?? && field.control.params.allowedNodesScript != "")>
    <#assign allowedScript = field.control.params.allowedNodesScript/>
    <#if (allowedScript?index_of("?") > 0)>
        <#assign res = allowedScript?matches("(\\{\\w+\\})")/>
        <#list res as m>
            <#assign paramName = "${m?replace('{','')?replace('}','')}"/>
            <#if field.control.params["param_" + "${paramName}"]??>
                <#assign paramCode = field.control.params["param_" + "${paramName}"]/>
                    <#if form.arguments[paramCode]??>
                       <#assign allowedScript = allowedScript?replace(m, form.arguments[paramCode])/>
                    <#else>
                        <#assign allowedScript = ""/>
                    </#if>
            </#if>
        </#list>
    </#if>
</#if>

<#assign endpointMany = field.endpointMany>
<#if field.control.params.endpointMany??>
	<#assign endpointMany = (field.control.params.endpointMany == "true")>
</#if>

<#assign hideLabel = false/>
<#if field.control.params.hideLabel?? && field.control.params.hideLabel == "true">
	<#assign hideLabel = true/>
</#if>

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.CurrentModules = LogicECM.CurrentModules || {};
    function init() {
        LogicECM.module.Base.Util.loadScripts([
                    'scripts/lecm-base/components/association-tree/association-tree-view.js',
                    'scripts/lecm-base/components/lecm-association-autocomplete.js'
                ],
                createControls,
                ['container',  'datasource']);
	}
	function createControls(){
    LogicECM.CurrentModules["${autoCompleteJsName}"] = new LogicECM.module.AssociationAutoComplete("${fieldHtmlId}");
    LogicECM.CurrentModules["${autoCompleteJsName}"].setMessages(${messages});
    LogicECM.CurrentModules["${autoCompleteJsName}"].setOptions({
    <#if disabled>
        disabled: true,
    </#if>
    <#if field.control.params.lazyLoading?? && field.control.params.lazyLoading == "true">
        lazyLoading: ${field.control.params.lazyLoading?string},
    </#if>
    <#if field.control.params.parentNodeRef??>
        parentNodeRef: "${field.control.params.parentNodeRef}",
    </#if>
    <#if field.control.params.startLocation??>
        startLocation: "${field.control.params.startLocation}",
    </#if>
        mandatory: ${isFieldMandatory?string},
    <#if args.ignoreNodes??>
        ignoreNodes: "${args.ignoreNodes}".split(","),
    </#if>
    <#if args.allowedNodes??>
        allowedNodes: "${args.allowedNodes}".split(","),
    </#if>
    <#if (allowedScript?? && allowedScript != "")>
        allowedNodesScript: "${allowedScript}",
    </#if>
        multipleSelectMode: ${endpointMany?string},
        <#if field.control.params.itemType??>
	        itemType: "${field.control.params.itemType}",
        <#elseif field.endpointType??>
	        itemType: "${field.endpointType!""}",
        </#if>
        currentValue: "${field.value!''}",
        itemFamily: "node",
    <#if field.control.params.maxSearchResults??>
        maxSearchResults: ${field.control.params.maxSearchResults},
    </#if>
        selectedValueNodeRef: "${fieldValue}",
    <#if field.control.params.selectedItemsNameSubstituteString??>
        selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
    </#if>
    <#if field.control.params.childrenDataSource??>
	    childrenDataSource: "${field.control.params.childrenDataSource}",
    </#if>
    <#if field.control.params.defaultValueDataSource??>
	    defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
    </#if>
    <#if field.control.params.useStrictFilterByOrg??>
        useStrictFilterByOrg: "${field.control.params.useStrictFilterByOrg?string}",
    </#if>
    <#if field.control.params.doNotCheckAccess??>
        doNotCheckAccess: ${field.control.params.doNotCheckAccess?string},
    </#if>
    <#if defaultValue?has_content>
	    defaultValue: "${defaultValue?string}",
    </#if>
        nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
        additionalFilter: "${field.control.params.additionalFilter!''}",
    <#if field.control.params.showAssocViewForm??>
        showAssocViewForm: ${field.control.params.showAssocViewForm?string},
    </#if>
	    useDynamicLoading: ${useDynamicLoading?string},
    <#if field.control.params.changeItemsFireAction??>
	    changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
    </#if>
	    fieldId: "${field.configName}",
	    formId: "${args.htmlid}",
		checkType: ${checkType?string}
    });

    LogicECM.CurrentModules["${treeViewJsName}"] = new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" );
    LogicECM.CurrentModules["${treeViewJsName}"].setOptions({
    <#if form.mode == "view" || field.disabled>
        disabled: true,
    </#if>
    <#if field.control.params.lazyLoading?? && field.control.params.lazyLoading == "true">
        lazyLoading: ${field.control.params.lazyLoading?string},
    </#if>
    <#if field.control.params.startLocation??>
        rootLocation: "${field.control.params.startLocation}",
    </#if>
        mandatory: ${isFieldMandatory?string},
        multipleSelectMode: ${endpointMany?string},

    <#if field.control.params.nameSubstituteString??>
        nameSubstituteString: "${field.control.params.nameSubstituteString}",
    </#if>
    <#if field.control.params.selectedItemsNameSubstituteString??>
        selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
    </#if>
    <#if field.control.params.sortProp??>
        sortProp: "${field.control.params.sortProp}",
    </#if>
    <#if field.control.params.parentNodeRef??>
        rootNodeRef: "${field.control.params.parentNodeRef}",
    </#if>
	<#-- при выборе сотрудника в контроле отображать, доступен ли он в данный момент и если недоступен, то показывать его автоответ -->
	<#if field.control.params.employeeAbsenceMarker??>
        employeeAbsenceMarker: "${field.control.params.employeeAbsenceMarker}",
    </#if>
    <#if args.ignoreNodes??>
        ignoreNodes: "${args.ignoreNodes}".split(","),
    </#if>
    <#if args.allowedNodes??>
        allowedNodes: "${args.allowedNodes}".split(","),
    </#if>
    <#if allowedScript??>
        allowedNodesScript: "${allowedScript}",
    </#if>
    <#if field.control.params.childrenDataSource??>
	    childrenDataSource: "${field.control.params.childrenDataSource}",
    </#if>
    <#if field.control.params.defaultValueDataSource??>
	    defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
    </#if>
    <#if field.control.params.useStrictFilterByOrg??>
        useStrictFilterByOrg: "${field.control.params.useStrictFilterByOrg?string}",
    </#if>
    <#if field.control.params.doNotCheckAccess??>
        doNotCheckAccess: ${field.control.params.doNotCheckAccess?string},
    </#if>
	<#if field.control.params.createNewMessage??>
		createNewMessage: "${field.control.params.createNewMessage}",
	<#elseif field.control.params.createNewMessageId??>
		createNewMessage: "${msg(field.control.params.createNewMessageId)}",
	</#if>
        showCreateNewLink: ${showCreateNewLink?string},
		showCreateNewButton: ${showCreateNewButton?string},
        showSearch: ${showSearch?string},
	    changeItemsFireAction: "refreshAutocompleteItemList_${fieldHtmlId}",
        plane: true,
    <#if field.control.params.showAssocViewForm??>
        showAssocViewForm: ${field.control.params.showAssocViewForm?string},
    </#if>
        selectedValue: "${fieldValue!''}",
        currentValue: "${field.value!''}",
        checkType: ${checkType?string},
        <#if field.control.params.itemType??>
            itemType: "${field.control.params.itemType}",
        <#elseif field.endpointType??>
            itemType: "${field.endpointType!""}",
        </#if>
        additionalFilter: "${field.control.params.additionalFilter!''}",
	    fieldId: "${field.configName}",
	    formId: "${args.htmlid}"
    }).setMessages( ${messages} );
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>


<#if disabled>
<div class="control association-autocomplete viewmode">
    <div class="label-div<#if hideLabel> hidden</#if>">
        <#if showViewIncompleteWarning && isFieldMandatory && !(fieldValue?is_number) && fieldValue?string == "">
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
<div class="control association-autocomplete editmode">
    <div class="label-div<#if hideLabel> hidden</#if>">
        <label for="${controlId}-autocomplete-input">
            ${field.label?html}:
            <#if isFieldMandatory && showMandatoryIndicator><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
        <input type="hidden" id="${controlId}-selectedItems"/>
    </div>
    <div class="container">
        <div class="buttons-div">
            <input type="button" id="${controlId}-tree-picker-button" name="${field.name}-tree-picker-button" value="..."/>
            <#if showCreateNewButton>
                <span class="create-new-button">
                    <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                </span>
            </#if>
        </div>
        <div class="value-div">
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <input id="${controlId}-autocomplete-input" name="${field.name}-autocomplete-input" type="text" class="mandatory-highlightable"/>
            <div id="${controlId}-currentValueDisplay" class="container control-selected-values <#if !endpointMany>hidden1</#if>"></div>
        </div>
    </div>
    <div id="${controlId}-autocomplete-container"></div>
    <@renderTreePickerDialogHTML controlId true showSearch/>
</div>
</#if>
<div class="clear"></div>
