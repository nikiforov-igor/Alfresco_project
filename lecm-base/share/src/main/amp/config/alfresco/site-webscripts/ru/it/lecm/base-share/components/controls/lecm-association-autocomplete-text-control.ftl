<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-picker-dialog.inc.ftl">

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign readonly = false>

<#assign autoCompleteJsName = field.control.params.autoCompleteJsName ! "${args.htmlid}-${fieldHtmlId}-auto-complete">
<#assign treeViewJsName = field.control.params.treeViewJsName ! "${args.htmlid}-${fieldHtmlId}-tree-view">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>
<#if !(fieldValue)?has_content>
	<#if form.arguments[field.name]?has_content>
		<#assign fieldValue = form.arguments[field.name]/>
	<#elseif form.arguments['readonly_' + field.name]?has_content>
		<#assign fieldValue=form.arguments['readonly_' + field.name]>
		<#assign readonly = true>
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

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function() {

	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-association-autocomplete-text.js',
            'scripts/lecm-base/components/association-tree/association-tree-view.js'
		], createControl);
	}

	function createControl() {
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
			sortProp: "${field.control.params.sortProp!'cm:name'}",
			additionalFilter: "${field.control.params.additionalFilter!''}",
			useDynamicLoading: ${useDynamicLoading?string},
			fieldId: "${field.configName}",
			formId: "${args.htmlid}"
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
        <#if field.control.params.sortProp??>
            sortProp: "${field.control.params.sortProp}",
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
			currentValue: "${(fieldValue!'')?js_string}",
			itemType:"${field.control.params.itemType!field.endpointType}",
			additionalFilter: "${field.control.params.additionalFilter!''}",
            clearFormsOnStart: false,
			fieldId: "${field.configName}",
			formId: "${args.htmlid}"
		}).setMessages( ${messages} );
	<#if readonly>
		LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
	</#if>
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#if disabled>
	<div class="control association-autocomplete-text viewmode">
		<div class="label-div">
			<#if showViewIncompleteWarning && isFieldMandatory && !(fieldValue?is_number) && fieldValue?string == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${fieldValue?html}" />
				<span id="${controlId}-Dom.get(this.currentValueHtmlId).value" class="mandatory-highlightable">${fieldValue?html}</span>
			</div>
		</div>
	</div>
<#else>
	<div class="control association-autocomplete-text editmode">
		<div class="label-div">
			<label for="${controlId}-autocomplete-input">
			${field.label?html}:
				<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
		</div>
		<div class="container">
			<div class="buttons-div">
				<input type="button" id="${controlId}-tree-picker-button" name="${field.name}-tree-picker-button" value="..."/>
				<#if showCreateNewLink>
					<span class="create-new-button">
                        <input type="button" id="${controlId}-tree-picker-create-new-button" name="-" value=""/>
                    </span>
				</#if>
			</div>
			<div class="value-div">
				<input id="${fieldHtmlId}" type="text" class="autocomplete-input" name="${field.name}" value="${fieldValue?html}"/>
			</div>
		</div>
		<div id="${controlId}-autocomplete-container"></div>
		<@renderTreePickerDialogHTML controlId true showSearch/>
	</div>
</#if>
<div class="clear"></div>
