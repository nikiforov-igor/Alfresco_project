<#assign controlId = fieldHtmlId + "-cntrl">
<#assign controlPickerId = controlId + "-picker">
<#assign controlContainerId = controlId + "-container">

<#assign controlPickerLabel = controlId + "-label">
<#assign controlTimeField = controlId + "-time-field">


<#if field.control.params.plane1?? && field.control.params.plane1 == "true">
    <#assign plane1 = true>
<#else>
    <#assign plane1 = false>
</#if>

<#if field.control.params.plane2?? && field.control.params.plane2 == "true">
    <#assign plane2 = true>
<#else>
    <#assign plane2 = false>
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

<script type="text/javascript">//<![CDATA[

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

var picker;
var htmlNode;

var markupToDraw = [];
markupToDraw[1] = '<@compress single_line=true>
	<@htmlMarkup field plane1 showSearch/>
</@compress>';

markupToDraw[2] = '<@compress single_line=true>
	<@htmlMarkup field plane2 showSearch/>
</@compress>';

LogicECM.module.WCalendar.Schedule.drawPicker = function Schedule_DrawPicker(instance) {

    picker = new LogicECM.module.AssociationTreeViewer( "${fieldHtmlId}" ).setOptions({
        <#if disabled>
            disabled: true,
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
        <#if args.allowedNodes??>
            allowedNodes: "${args.allowedNodes}".split(","),
        </#if>
		ignoreNodesInTreeView: false,
        showCreateNewLink: ${showCreateNewLink?string},
        currentValue: "${field.value!''}",
        showSelectedItemsPath: ${showSelectedItemsPath?string},
        <#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
	    fireAction: {
			<#if field.control.params.fireAction?? && field.control.params.fireAction != "">
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
			</#if>
			ok: "${controlPickerLabel}"
	    },

       showSearch: ${showSearch?string}
    }).setMessages( ${messages} );


	if (instance.value == 1) {
		picker.setOptions({
			rootLocation: "${field.control.params.startLocation1}",
			itemType: "${field.control.params.itemType1}",
			plane: ${plane1?string}
		});
	} else if (instance.value == 2) {
		picker.setOptions({
			rootLocation: "${field.control.params.startLocation2}",
			itemType: "${field.control.params.itemType2}",
			plane: ${plane2?string}
		});
	}

	htmlNode = Dom.get("${controlContainerId}");
	if (htmlNode) {
		htmlNode.innerHTML = "";
		htmlNode.innerHTML = markupToDraw[instance.value];
		var pickerNode = Dom.get("${controlPickerId}-" + instance.value);
		pickerNode.checked = 1;
	}

	YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
}

LogicECM.module.WCalendar.Schedule.pickerOKPressed = function Schedule_PickerOKPressed(layer, args) {
	var scope = this,
		picker = args[1],
		htmlNode = Dom.get("${controlTimeField}"),
		selectedItems = picker.getSelectedItems();

	if (htmlNode) {
		htmlNode.innerHTML = "";
	}

	if (selectedItems.length > 0) {
		Alfresco.util.Ajax.request({
			method: "POST",
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/schedule/get/parentScheduleStdTime",
			dataObj: { nodeRef: selectedItems[0] },
			requestContentType: "application/json",
			responseContentType: "application/json",
			successCallback: {
				fn: function (response) {
					var result = response.json;
					var htmlOutput = "${msg("label.schedule.picker.parent-schedule")}:<br>";
					if (result != null) {
						if (result) {
							if (result.type == "COMMON") {
								htmlOutput += "c " + result.begin + " до " + result.end + "<br>";
							} else if (result.type == "SPECIAL") {
								htmlOutput += "особый<br>";
							} else {
								htmlOutput += "отсутствует<br>";
							}
						}
						var htmlNode = Dom.get("${controlTimeField}");
						if (htmlNode) {
							htmlNode.innerHTML = htmlOutput;
						}
					}
				},
				scope: this
			}
		});
	}
}

YAHOO.Bubbling.on("${controlPickerLabel}", LogicECM.module.WCalendar.Schedule.pickerOKPressed, this);

LogicECM.module.WCalendar.Schedule.drawPicker({ value: '1'});

//]]></script>


<div class="form-field" id="${controlContainerId}">
	<@htmlMarkup field plane1 showSearch/>
</div>


<#macro htmlMarkup field plane showSearch>
	<#if disabled>
		<div id="${controlId}" class="viewmode-field">
			<#if showViewIncompleteWarning && (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
		</div>
	<#else>
		<label style="height: 50px; word-wrap: break-word;" for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<div>
			<div><input type="radio" name="picker-instance" value="1" id="${controlPickerId}-1" onclick="LogicECM.module.WCalendar.Schedule.drawPicker(this);" checked > ${msg(field.control.params.pickerLabel1)}</div>
			<div><input type="radio" name="picker-instance" value="2" id="${controlPickerId}-2" onclick="LogicECM.module.WCalendar.Schedule.drawPicker(this);" > ${msg(field.control.params.pickerLabel2)}</div>
			<div style="clear: both"></div>
		</div>
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
                <#include "/ru/it/lecm/base-share/components/controls/association-tree-picker-dialog.inc.ftl">
                <@renderTreePickerDialogHTML controlId plane showSearch/>
			</#if>
			<div class="clear"></div>
			<div id="${controlTimeField}" class="form-field"> </div>
		</div>
	</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</#macro>
