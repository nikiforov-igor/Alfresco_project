<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params>
<#assign controlJsName = "AssociationControl">
<#if params.controlJsName??>
    <#assign controlJsName = params.controlJsName?string>
</#if>
<#assign plane = false>
<#if params.plane?? && params.plane == "true">
	<#assign plane = true>
</#if>
<#assign onlyTree = false>
<#if params.onlyTree?? && params.onlyTree == "true">
	<#assign onlyTree = true>
</#if>
<#assign showPath = true>
<#if params.showPath?? && params.showPath == "false">
	<#assign showPath = false>
</#if>

<#assign showPath = true>
<#if params.showPath?? && params.showPath == "false">
	<#assign showPath = false>
</#if>
<#assign hideLabel = false/>
<#if field.control.params.hideLabel?? && field.control.params.hideLabel == "true">
	<#assign hideLabel = true/>
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

<#assign showParentNodeInTreeView = true>
<#if params.showParentNodeInTreeView?? && params.showParentNodeInTreeView == "false">
	<#assign showParentNodeInTreeView = false>
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

<#assign blockChangeItemOnInit = false>
<#if params.blockChangeItemOnInit?? && params.blockChangeItemOnInit == "true">
	<#assign blockChangeItemOnInit = true>
</#if>

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
<#elseif args["allowedNodesScript"]??>
	<#assign allowedScript = args["allowedNodesScript"] />
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<#if disabled>
<div id="${controlId}" class="control association-control viewmode">
	<div class="label-div<#if hideLabel> hidden</#if>">
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
<div id="${controlId}-edt" class="control association-control editmode">
	<div class="label-div<#if hideLabel> hidden</#if>">
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
	<#assign readonly = false>

	<#assign defaultValue = "">
	<#if form.mode == "create" && !field.disabled>
		<#if form.arguments[field.name]?has_content>
			<#assign defaultValue=form.arguments[field.name]>
		<#elseif form.arguments['readonly_' + field.name]?has_content>
			<#assign defaultValue=form.arguments['readonly_' + field.name]>
			<#assign readonly = true>
		<#elseif params.defaultValue??>
			<#assign defaultValue=params.defaultValue>
		<#elseif params.selectedItemsFormArgs??>
			<#assign selectedItemsFormArgs = params.selectedItemsFormArgs?split(",")>
			<#list selectedItemsFormArgs as selectedItemsFormArg>
				<#if form.arguments[selectedItemsFormArg]??>
					<#if (defaultValue?length > 0)>
						<#assign defaultValue = defaultValue + ","/>
					</#if>
					<#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
				</#if>
			</#list>
		</#if>
	</#if>

	(function() {
		function init() {
			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-base/components/lecm-association-control.js',
				'modules/simple-dialog.js'
			], [
                'css/lecm-base/components/controls/association-control-picker.css'
            ], createControl);
		}
		function createControl(){
			new LogicECM.module.AssociationControl("${fieldHtmlId}", "${controlJsName}").setOptions({
			<#if disabled>
				disabled: true,
			</#if>
			<#if params.rootLocationArg??>
                rootLocation: "${form.arguments[params.rootLocationArg]}",
			<#elseif params.rootLocation??>
                rootLocation: "${params.rootLocation}",
			</#if>
                showParentNodeInTreeView: ${showParentNodeInTreeView?string},
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
			<#if params.treeSortProp??>
				treeSortProp: "${params.treeSortProp}",
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
            <#if (allowedScript?? && allowedScript != "")>
                allowedNodesScript: "${allowedScript}",
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
			<#if params.doNotCheckAccess??>
                doNotCheckAccess: ${params.doNotCheckAccess?string},
			</#if>
			<#if params.childrenDataSource??>
				childrenDataSource: "${params.childrenDataSource}",
			</#if>
			<#if params.pickerItemsScript??>
                pickerItemsScript: "${params.pickerItemsScript}",
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
			<#if params.rootNodeScript??>
                rootNodeScript: "${params.rootNodeScript}",
			</#if>
        		onlyTreeNodeSelectable: ${onlyTree?string},
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
			<#if field.control.params.showInaccessible ??>
                showInaccessible: ${field.control.params.showInaccessible?string},
			</#if>
			<#if field.control.params.viewUrl??>
                viewUrl: "${field.control.params.viewUrl}",
			</#if>
				itemType: "${params.endpointType ! params.itemType ! field.endpointType}",
				additionalFilter: "${params.additionalFilter!''}",
				showAssocViewForm: ${showAssocViewForm?string},
				checkType: ${checkType?string},
				useDeferedReinit: ${useDeferedReinit?string},
				fieldId: "${field.configName}",
				formId: "${args.htmlid}",
                blockChangeItemOnInit: ${blockChangeItemOnInit?string}
			}).setMessages( ${messages} );
			<#if readonly>
				LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
			</#if>
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
</script>
