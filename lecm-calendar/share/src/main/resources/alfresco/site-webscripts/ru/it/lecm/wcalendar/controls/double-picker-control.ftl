<#include "/ru/it/lecm/base-share/components/controls/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign compactMode = field.control.params.compactMode!false>
<#assign controlContainerId = controlId + "-container">
<#assign controlPickerId = controlId + "-picker">

<#if field.control.params.selectedValueContextProperty??>
	<#if context.properties[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
	<#elseif args[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
	<#elseif context.properties[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
	</#if>
</#if>

<#macro htmlMarkup field>
	<#if form.mode == "view">
		<div id="${controlId}" class="viewmode-field">
			<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
		</div>
	<#else>
		<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<div id="${controlId}" class="object-finder">
			<div>
				<input type="radio" name="picker-instance" value="1" id="${controlPickerId}-1" onclick="LogicECM.module.Shedule.DrawPicker(this);" checked>${msg(field.control.params.pickerLabel1)}<br>
				<input type="radio" name="picker-instance" value="2" id="${controlPickerId}-2" onclick="LogicECM.module.Shedule.DrawPicker(this);" >${msg(field.control.params.pickerLabel2)}
			</div>
			<div id="${controlId}-currentValueDisplay" class="current-values"></div>

			<#if field.disabled == false>
				<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
				<input type="hidden" id="${controlId}-added" name="${field.name}_added" />
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
				<div id="${controlId}-itemGroupActions" class="show-picker"></div>
				<@renderPickerHTML controlId />
			</#if>
		</div>
	</#if>
</#macro>

<div class="form-field" id="${controlContainerId}">
	<@htmlMarkup field/>
</div>

<script type="text/javascript">//<![CDATA[

if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Shedule = LogicECM.module.Shedule || {};

(function() {
var picker;
var htmlNode;

var markupToDraw = '<@compress single_line=true>
	<@htmlMarkup field/>
</@compress>';

LogicECM.module.Shedule.DrawPicker = function Shedule_DrawPicker(instance) {
	picker = new LogicECM.module.ObjectFinder("${controlId}", "${fieldHtmlId}").setOptions({
		<#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled: true,</#if>
		field: "${field.name}",
		compactMode: ${compactMode?string},
		<#if field.mandatory??>
			mandatory: ${field.mandatory?string},
		<#elseif field.endpointMandatory??>
			mandatory: ${field.endpointMandatory?string},
		</#if>
		<#if field.control.params.startLocation??>
			startLocation: "${field.control.params.startLocation}",
			<#if form.mode == "edit" && args.itemId??>currentItem: "${args.itemId?js_string}",</#if>
			<#if form.mode == "create" && form.destination?? && form.destination?length &gt; 0>currentItem: "${form.destination?js_string}",</#if>
		</#if>
		<#if field.control.params.xPathLocation??>
			xPathLocation: "${field.control.params.xPathLocation}",
		</#if>
		<#if field.control.params.xPathLocationRoot??>
			xPathLocationRoot: "${field.control.params.xPathLocationRoot?js_string}",
		</#if>
		<#if field.control.params.startLocationParams??>
			startLocationParams: "${field.control.params.startLocationParams?js_string}",
		</#if>
		<#if field.control.params.numberOfHiddenLayers??>
			numberOfHiddenLayers: "${field.control.params.numberOfHiddenLayers?number}",
		</#if>
		currentValue: "${field.value}",
		<#if field.control.params.valueType??>valueType: "${field.control.params.valueType}",</#if>
		<#if renderPickerJSSelectedValue??>selectedValue: "${renderPickerJSSelectedValue}",</#if>
		<#if field.control.params.selectActionLabelId??>selectActionLabelId: "${field.control.params.selectActionLabelId}",</#if>

		<#if field.control.params.showTargetLink??>
			showLinkToTarget: ${field.control.params.showTargetLink},
			<#if page?? && page.url.templateArgs.site??>
				targetLinkTemplate: "${url.context}/page/site/${page.url.templateArgs.site!""}/document-details?nodeRef={nodeRef}",
			<#else>
				targetLinkTemplate: "${url.context}/page/document-details?nodeRef={nodeRef}",
			</#if>
		</#if>
		<#if field.control.params.allowNavigationToContentChildren??>
			allowNavigationToContentChildren: ${field.control.params.allowNavigationToContentChildren},
		</#if>
		<#if field.control.params.nameSubstituteString??>
			nameSubstituteString: "${field.control.params.nameSubstituteString}",
		</#if>
		itemFamily: "node",
		displayMode: "${field.control.params.displayMode!"items"}",
		itemType: "${field.endpointType}",
		multipleSelectMode: ${field.endpointMany?string},
		parentNodeRef: "alfresco://company/home",
		selectActionLabel: "${field.control.params.selectActionLabel!msg("button.select")}",
		minSearchTermLength: ${field.control.params.minSearchTermLength!'1'},
		maxSearchResults: ${field.control.params.maxSearchResults!'1000'}
   }).setMessages(
      ${messages}
   );

	if (instance.value == 1) {
		picker.setOptions ({
			<#if field.control.params.rootNode1??>
				rootNode: "${field.control.params.rootNode1}",
			</#if>
			startLocation: "${field.control.params.startLocation1}",
			itemType: "${field.control.params.itemType1}"
		});
	} else if (instance.value == 2) {
		picker.setOptions ({
			<#if field.control.params.rootNode1??>
				rootNode: "${field.control.params.rootNode2}",
			</#if>
			startLocation: "${field.control.params.startLocation2}",
			itemType: "${field.control.params.itemType2}"
		});
	} else {
		alert("WTF? radioButton.value = " + radioButton.value);
	}
	htmlNode = Dom.get("${controlContainerId}");
	if (htmlNode) {
		htmlNode.innerHTML = "";
		htmlNode.innerHTML = markupToDraw;
		var pickerNode = Dom.get("${controlPickerId}-" + instance.value);
		pickerNode.checked = 1;
	}
}

LogicECM.module.Shedule.DrawPicker({ value: '1'});
})();

//]]></script>

