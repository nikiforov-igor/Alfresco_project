<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params>

<#assign plane = false>
<#if params.plane?? && params.plane == "true">
	<#assign plane = true>
</#if>

<#assign showPath = true>
<#if params.showPath?? && params.showPath == "false">
	<#assign showPath = false>
</#if>

<#assign showAutocomplete = true>
<#if params.showAutocomplete?? && params.showAutocomplete == "false">
	<#assign showAutocomplete = false>
</#if>

<#assign showCreateNewLink = false>
<#if params.showCreateNewLink?? && params.showCreateNewLink == "true">
	<#assign showCreateNewLink = true>
</#if>

<#assign showCreateNewButton = false>
<#if params.showCreateNewButton?? && params.showCreateNewButton == "true">
	<#assign showCreateNewButton = true>
</#if>

<#assign showSearch = false>
<#if params.showSearch?? && params.showSearch == "true">
	<#assign showSearch = true>
</#if>

<#assign showViewIncompleteWarning = true>
<#if params.showViewIncompleteWarning?? && params.showViewIncompleteWarning == "false">
	<#assign showViewIncompleteWarning = false>
</#if>

<#assign showAssocViewForm = false>
<#if params.showAssocViewForm?? && params.showAssocViewForm == "true">
	<#assign showAssocViewForm = true>
</#if>

<#assign checkType = true>
<#if params.checkType?? && params.checkType == "false">
	<#assign checkType = false>
</#if>

<#assign endpointMany = field.endpointMany>
<#if field.control.params.endpointMany??>
    <#assign endpointMany = (field.control.params.endpointMany == "true")>
</#if>

<#assign useDeferedReinit = false>
<#if params.useDeferedReinit?? && params.useDeferedReinit == "true">
    <#assign useDeferedReinit = true>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<#if disabled>
<div id="${controlId}" class="control association-control viewmode">
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
<div class="control association-control editmode">
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
			<#if showAutocomplete>
				<input id="${controlId}-autocomplete-input" type="text" class="mandatory-highlightable"/>
			</#if>
			<div id="${controlId}-currentValueDisplay" class="control-selected-values <#if showAutocomplete>hidden1<#else>mandatory-highlightable</#if>"></div>
		</div>
	</div>
	<div id="${controlId}-autocomplete-container"></div>
</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">
	<#assign optionSeparator="|">
	<#assign labelSeparator=":">

	<#assign defaultValue = "">
	<#if form.mode == "create" && !field.disabled>
		<#if params.selectedItemsFormArgs??>
			<#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
			<#list selectedItemsFormArgs as selectedItemsFormArg>
				<#if form.arguments[selectedItemsFormArg]??>
					<#if (defaultValue?length > 0)>
						<#assign defaultValue = defaultValue + ","/>
					</#if>
					<#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
				</#if>
			</#list>

		<#elseif form.arguments[field.name]?has_content>
			<#assign defaultValue=form.arguments[field.name]>
		</#if>
	</#if>

	(function() {
		function init() {
			LogicECM.module.Base.Util.loadScripts([
				'scripts/lecm-base/components/lecm-association-control.js',
				'modules/simple-dialog.js'
			], createControl);
		}
		function createControl(){
			new LogicECM.module.AssociationControl("${fieldHtmlId}").setOptions({
			<#if disabled>
				disabled: true,
			</#if>
			<#if params.rootLocation??>
				rootLocation: "${params.rootLocation}",
			</#if>
			<#if field.mandatory??>
				mandatory: ${field.mandatory?string},
			<#elseif field.endpointMandatory??>
				mandatory: ${field.endpointMandatory?string},
			</#if>
				multipleSelectMode: ${endpointMany?string},

			<#if params.nameSubstituteString??>
				nameSubstituteString: "${params.nameSubstituteString}",
			</#if>
			<#if params.sortProp??>
				sortProp: "${params.sortProp}",
			</#if>
			<#if params.selectedItemsNameSubstituteString??>
				selectedItemsNameSubstituteString: "${params.selectedItemsNameSubstituteString}",
			</#if>
			<#if params.treeNodeSubstituteString??>
				treeNodeSubstituteString: "${params.treeNodeSubstituteString}",
			</#if>
			<#if params.treeNodeTitleSubstituteString??>
				treeNodeTitleSubstituteString: "${params.treeNodeTitleSubstituteString}",
			</#if>
			<#if params.treeItemType??>
				treeItemType: "${params.treeItemType}",
			</#if>
			<#if params.changeItemsFireAction??>
				changeItemsFireAction: "${params.changeItemsFireAction}",
			</#if>
			<#-- при выборе сотрудника в контроле отображать, доступен ли он в данный момент и если недоступен, то показывать его автоответ -->
			<#if field.control.params.employeeAbsenceMarker??>
				employeeAbsenceMarker: "${field.control.params.employeeAbsenceMarker}",
			</#if>
			<#if args.ignoreNodes??>
				ignoreNodes: "${args.ignoreNodes}".split(","),
			</#if>
			<#if params.treeIgnoreNodesScript??>
				treeIgnoreNodesScript: "${params.treeIgnoreNodesScript}",
			</#if>
				showCreateNewLink: ${showCreateNewLink?string},
				showCreateNewButton: ${showCreateNewButton?string},
			<#if params.createNewMessage??>
				createNewMessage: "${params.createNewMessage}",
			<#elseif params.createNewMessageId??>
				createNewMessage: "${msg(params.createNewMessageId)}",
			</#if>
			<#if params.createDialogClass??>
				createDialogClass: "${params.createDialogClass}",
			</#if>
				showSearch: ${showSearch?string},
				plane: ${plane?string},
				showPath: ${showPath?string},
				showAutocomplete: ${showAutocomplete?string},
				currentValue: "${field.value!''}",
			<#if params.defaultValueDataSource??>
				defaultValueDataSource: "${params.defaultValueDataSource}",
			</#if>
			<#if params.useStrictFilterByOrg??>
				useStrictFilterByOrg: "${params.useStrictFilterByOrg?string}",
			</#if>
			<#if params.childrenDataSource??>
				childrenDataSource: "${params.childrenDataSource}",
			</#if>
			<#if params.treeBranchesDatasource??>
				treeBranchesDatasource: "${params.treeBranchesDatasource}",
			</#if>
			<#if params.useObjectDescription??>
				useObjectDescription: "${params.useObjectDescription}",
			</#if>
			<#if defaultValue?has_content>
				defaultValue: "${defaultValue?string}",
			</#if>
			<#if params.fireAction?? && params.fireAction != "">
				fireAction: {
					<#list params.fireAction?split(optionSeparator) as typeValue>
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
				itemType: "${params.endpointType ! field.endpointType}",
				additionalFilter: "${params.additionalFilter!''}",
				showAssocViewForm: ${showAssocViewForm?string},
				checkType: ${checkType?string},
				useDeferedReinit: ${useDeferedReinit?string},
				fieldId: "${field.configName}",
				formId: "${args.htmlid}"
			}).setMessages( ${messages} );
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
</script>
