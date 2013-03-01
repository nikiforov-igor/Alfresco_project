
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign compactMode = field.control.params.compactMode!false>
<#assign controlContainerId = controlId + "-container">
<#assign controlPickerId = controlId + "-picker">
<#assign controlPickerLabel = controlId + "-label">
<#assign controlTimeField = controlId + "-time-field">

<#if field.control.params.selectedValueContextProperty??>
	<#if context.properties[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
	<#elseif args[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
	<#elseif context.properties[field.control.params.selectedValueContextProperty]??>
		<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
	</#if>
</#if>


<script type="text/javascript">//<![CDATA[

var picker;
var htmlNode;

var markupToDraw = '<@compress single_line=true>
	<@htmlMarkup field/>
</@compress>';

function Shedule_DrawPicker(instance) {
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
		fireAction: {
			ok: "${controlPickerLabel}"
		},
		itemFamily: "node",
		displayMode: "${field.control.params.displayMode!"items"}",
		itemType: "${field.endpointType}",
		multipleSelectMode: ${field.control.params.multipleSelectMode},
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
	}

	htmlNode = Dom.get("${controlContainerId}");
	if (htmlNode) {
		htmlNode.innerHTML = "";
		htmlNode.innerHTML = markupToDraw;
		var pickerNode = Dom.get("${controlPickerId}-" + instance.value);
		pickerNode.checked = 1;
	}

	YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
}

function Shedule_PickerOKPressed(layer, args) {
	var scope = this;
	var picker = args[1];
	var selectedItems = picker.getSelectedItems();
	var nodeRefObj = []
	for (var i = 0; i < selectedItems.length; i++) {
		nodeRefObj.push({nodeRef: selectedItems[i]})
	}

	Alfresco.util.Ajax.request({
		method: "POST",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/shedule/get/parentSheduleStdTime",
		dataObj: nodeRefObj,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var results = response.json;
				var htmlOutput = "${msg("label.shedule.picker.parent-shedule")}:<br>";
				if (results != null) {
					for (var i = 0; i < results.length; i++) {
						var result = results[i];
						if (result) {
							if (result.type == "COMMON") {
								<#-- TODO: Сделать локализацию сообщений -->
								htmlOutput += "c " + result.begin + " до " + result.end + "<br>";
							} else if (result.type == "SPECIAL") {
								htmlOutput += "особый<br>";
							} else {
								htmlOutput += "отсутствует<br>";
							}
						}

					}
					var htmlNode = Dom.get("${controlTimeField}");
					if (htmlNode) {
						htmlNode.innerHTML = "";
						htmlNode.innerHTML = htmlOutput;
					}
				}
			},
			scope: this
		}
	});
}

YAHOO.Bubbling.on("${controlPickerLabel}", Shedule_PickerOKPressed, this);

Shedule_DrawPicker({ value: '1'});

//]]></script>


<div class="form-field" id="${controlContainerId}">
	<@htmlMarkup field/>
</div>

<#macro htmlMarkup field>
	<#assign pickerId = controlId + "-picker">
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
				<input type="radio" name="picker-instance" value="1" id="${controlPickerId}-1" onclick="Shedule_DrawPicker(this);" checked> ${msg(field.control.params.pickerLabel1)}<br>
				<input type="radio" name="picker-instance" value="2" id="${controlPickerId}-2" onclick="Shedule_DrawPicker(this);" > ${msg(field.control.params.pickerLabel2)}
			</div>
			<div id="${controlId}-currentValueDisplay" class="current-values"></div>

			<#if field.disabled == false>
				<input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
				<input type="hidden" id="${controlId}-added" name="${field.name}_added" />
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
				<div id="${controlId}-itemGroupActions" class="show-picker"></div>
				<div id="${pickerId}" class="picker yui-panel">
				   <div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>
				   <div id="${pickerId}-body" class="bd">
					  <div class="picker-header">
						 <div id="${pickerId}-folderUpContainer" class="folder-up"><button id="${pickerId}-folderUp"></button></div>
						 <div id="${pickerId}-navigatorContainer" class="navigator">
							<button id="${pickerId}-navigator"></button>
							<div id="${pickerId}-navigatorMenu" class="yuimenu">
							   <div class="bd">
								  <ul id="${pickerId}-navigatorItems" class="navigator-items-list">
									 <li>&nbsp;</li>
								  </ul>
							   </div>
							</div>
						 </div>
						 <div id="${pickerId}-searchContainer" class="search">
							<input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
							<span class="search-button"><button id="${pickerId}-searchButton">${msg("form.control.object-picker.search")}</button></span>
						 </div>
					  </div>
					  <div class="yui-g">
						 <div id="${pickerId}-left" class="yui-u first panel-left">
							<div id="${pickerId}-results" class="picker-items">
							   <#nested>
							</div>
						 </div>
						 <div id="${pickerId}-right" class="yui-u panel-right">
							<div id="${pickerId}-selectedItems" class="picker-items"></div>
						 </div>
					  </div>
					  <div class="bdft">
						 <button id="${controlId}-ok" tabindex="0">${msg("button.ok")}</button>
						 <button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
					  </div>
				   </div>
				</div>
			</#if>
			<div id="${controlTimeField}" class="form-field"> </div>
		</div>
	</#if>
</#macro>
