<#include "/org/alfresco/components/component.head.inc">
<#include "/ru/it/lecm/base-share/components/controls/association-tree-picker-dialog.inc.ftl">

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
<#assign sortSelected = false>
<#if params.sortSelected?? && params.sortSelected == "true">
    <#assign  sortSelected = true>
</#if>
<#assign  verticalListClass = "">
<#if params.verticalList?? && params.verticalList == "true">
<#assign  verticalListClass = "vertical">
</#if>

<#assign readonly = false>
<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>

<#if disabled>
<div id="${controlId}" class="control association-token-control ${verticalListClass} viewmode">
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
<div class="control association-token-control editmode">
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
            <#if disabled>
                <div id="${controlId}-currentValueDisplay" class="control-selected-values <#if showAutocomplete>hidden1<#else>mandatory-highlightable</#if>"></div>
            <#else>
                <div id="${controlId}-diagram" class="member-control-diagram">
                    <div id="${controlId}-diagram-container" class="member-control-diagram-container">
                        <div id="${controlId}-diagram-header" class="member-control-diagram-header">
                            <#assign calControlId = fieldHtmlId + "-date-cntrl">
                            <#assign calFieldHtmlId = fieldHtmlId + "-calendar">
                            <#assign currentValue = defaultValue?js_string>
                            <#if  !currentValue?has_content && !disabled >
                                <#assign currentValue = field.control.params.defaultValue!""?js_string>
                                <#if currentValue == "now">
                                    <#if field.control.params.defaultTime?? >
                                        <#assign currentValue = .now?string("yyyy-MM-dd'T'" + field.control.params.defaultTime + ":00.000")>
                                    <#else>
                                        <#assign currentValue = .now?string("yyyy-MM-dd")>
                                    </#if>
                                </#if>
                            </#if>
                            <script type="text/javascript">//<![CDATA[
                            (function () {
                                function init() {
                                    var resources = [
                                        'scripts/lecm-base/components/lecm-date-picker.js'
                                    ]
                                    if ($.timepicker === undefined) {
                                        resources.push('scripts/lecm-base/third-party/jquery-ui-1.10.3.custom.js');
                                        resources.push('scripts/lecm-base/third-party/jquery-ui-timepicker-addon.js');
                                        resources.push('scripts/lecm-base/third-party/jquery-ui-sliderAccess.js');
                                    }
                                    LogicECM.module.Base.Util.loadResources(resources, [
                                        'css/lecm-calendar/jquery-ui-1.10.3.custom.css',
                                        'css/lecm-calendar/jquery-ui-timepicker-addon.css'
                                    ], createDatePicker, ["button", "calendar"]);
                                }

                                function createDatePicker() {
                                    var picker = new LogicECM.DatePicker("${calControlId}", "${calFieldHtmlId}").setOptions(
                                            {
                                                changeFireAction: "setMemberCalendarDate",
                                                showTime: false,
                                                fieldId: "${field.configName}",
                                                formId: "${args.htmlid}"
                                            }).setMessages(
                                    ${messages}
                                    );
                                    picker.draw();
                                }

                                YAHOO.util.Event.onAvailable('${calFieldHtmlId}', init, this, true);
                            })();
                            //]]></script>

                            <div id="${calControlId}-cal-cell" class="member-control-diagram-header-first-cell">
                                <div id="${calControlId}-prevDate" class="member-control-diagram-header-prevdate"></div>
                                <div id="${calControlId}" class="datepicker"></div>
                                <input id="${calFieldHtmlId}" type="hidden" name="${field.name}-calendar" value="${defaultValue?html}"/>
                                <div class="date-entry-container only-date member-control-diagram-header-calendar-container">
                                    <input id="${calControlId}-date" name="-" type="text" class="member-control-diagram-header-calendar date-entry mandatory-highlightable"
                                           <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
                                           <#else>tabindex="0"</#if> />
                                </div>
                                <div id="${calControlId}-nextDate" class="member-control-diagram-header-nextdate"></div>
                                <div id="${calControlId}-pointDate" class="member-control-diagram-header-pointdate"></div>
                            </div>
                        </div>
                        <div id="${controlId}-diagram-content"></div>
                    </div>
                </div>
            </#if>
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
		<#elseif form.arguments['readonly_' + field.name]?has_content>
			<#assign defaultValue=form.arguments['readonly_' + field.name]>
			<#assign readonly = true>
        </#if>
    </#if>

    (function() {
        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-association-token-control.js',
                'scripts/lecm-events/controls/lecm-events-members-control.js',
                'modules/simple-dialog.js'
            ], [
                'css/lecm-base/components/lecm-association-token-control.css',
                'css/lecm-base/components/lecm-events-members-control.css',
                'css/lecm-events/event-members-control.css'
            ], createControl);
        }
        function createControl(){
            new LogicECM.module.Calendar.MembersControl("${fieldHtmlId}").setOptions({
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
				allDayConfigName: "${params.allDayConfigName!'lecm-events:all-day'}",
				fromDateConfigName: "${params.fromDateConfigName!'lecm-events:from-date'}",
				toDateConfigName: "${params.toDateConfigName!'lecm-events:to-date'}",
                sortSelected: ${sortSelected?string},
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
            <#if defaultValue?has_content>
                defaultValue: "${defaultValue?string}",
            </#if>
                eventNodeRef: "${form.arguments.itemId}",
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
                fieldId: "${field.configName}",
                formId: "${args.htmlid}"
            }).setMessages( ${messages} );
		<#if readonly>
			LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
		</#if>
        }
        YAHOO.util.Event.onDOMReady(init);
    })();
</script>
