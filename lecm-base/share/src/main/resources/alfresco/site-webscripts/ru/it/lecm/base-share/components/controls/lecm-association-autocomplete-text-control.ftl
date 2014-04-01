<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-picker-dialog.inc.ftl">

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

<#if field.control.params.showViewIncompleteWarning?? && field.control.params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
<#else>
	<#assign showViewIncompleteWarning = true>
</#if>

<#assign useDynamicLoading = true>
<#if field.control.params.useDynamicLoading?? && field.control.params.useDynamicLoading == "false">
	<#assign useDynamicLoading = false>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function() {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	LogicECM.CurrentModules["${autoCompleteJsName}"] = new LogicECM.module.AssociationAutoCompleteText("${fieldHtmlId}");
	LogicECM.CurrentModules["${autoCompleteJsName}"].setMessages(${messages});
	LogicECM.CurrentModules["${autoCompleteJsName}"].setOptions({
	<#if disabled>
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
	<#if args.ignoreNodes??>
		ignoreNodes: "${args.ignoreNodes}".split(","),
	</#if>
    <#if args.allowedNodes??>
        allowedNodes: "${args.allowedNodes}".split(","),
    </#if>
		itemType: "${field.control.params.itemType!field.endpointType}",
		itemFamily: "node",
	<#if field.control.params.maxSearchResults??>
		maxSearchResults: ${field.control.params.maxSearchResults},
	</#if>
	<#if field.control.params.childrenDataSource??>
		childrenDataSource: "${field.control.params.childrenDataSource}",
	</#if>
		nameSubstituteString: "${field.control.params.nameSubstituteString!'{cm:name}'}",
		additionalFilter: "${field.control.params.additionalFilter!''}",
		useDynamicLoading: ${useDynamicLoading?string}
	});

	LogicECM.CurrentModules["${treeViewJsName}"] = new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" );
	LogicECM.CurrentModules["${treeViewJsName}"].setOptions({
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
	<#if field.control.params.childrenDataSource??>
		childrenDataSource: "${field.control.params.childrenDataSource}",
	</#if>
	<#if field.control.params.defaultValueDataSource??>
		defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
	</#if>
		showCreateNewLink: ${showCreateNewLink?string},
		showSearch: ${showSearch?string},
		changeItemsFireAction: "refreshAutocompleteItemList_${fieldHtmlId}",
		plane: true,
		setCurrentValue: false,
		currentValue: "${field.value!''}",
		itemType:"${field.control.params.itemType!field.endpointType}",
		additionalFilter: "${field.control.params.additionalFilter!''}"
	}).setMessages( ${messages} );
})();
//]]></script>

<div class="form-field">
<#if disabled>
	<div class="viewmode-field">
		<#if showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
		<span class="viewmode-label">${field.label?html}:</span>
		<span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value">${field.value!""}</span>
		<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
	</div>
<#else>
	<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

	<div class="autocomplete-block">
		<div id="${controlId}-autocomplete">
			<input id="${fieldHtmlId}" type="text" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
			<div class="show-picker">
                    <span class="tree-picker-button">
                        <input type="button" id="${controlId}-tree-picker-button" name="-" value="..."/>
                    </span>
				<#if showCreateNewLink>
					<span class="create-new-button">
                        <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                    </span>
				</#if>
			</div>
			<div id="${controlId}-autocomplete-container"></div>

			<@renderTreePickerDialogHTML controlId true showSearch/>
		</div>
	</div>
</#if>
</div>