<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">
<#assign parentScheduleControlId = controlId + "-parentSchedule">
<#assign parentScheduleFieldId = parentScheduleControlId + "-field">
<#assign individualScheduleControlId = controlId + "-individual">
<#assign individualScheduleFieldId = individualScheduleControlId + "-field">

<script type="text/javascript">//<![CDATA[
(function() {
	YAHOO.util.Event.onContentReady("${fieldHtmlId}", Orgstructure_makeSchedulesRequest, true);
	YAHOO.util.Event.onContentReady("view-node-form", function() {
		var viewNodeForm = YAHOO.util.Dom.get("view-node-form");
		viewNodeForm.style.width = "60em";
	}, true);
})();

function Orgstructure_makeSchedulesRequest() {
	var employeeNodeRef = "${form.arguments.itemId}";

	var dataObj = {
		nodeRef: employeeNodeRef
	}

	Alfresco.util.Ajax.request({
		method: "POST",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/wcalendar/schedule/get/employeeScheduleStdTime",
		dataObj: dataObj,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					var individualScheduleNode = YAHOO.util.Dom.get("${individualScheduleControlId}");
					if (result.type == "SPECIAL") {
						individualScheduleNode.innerHTML = "${msg('label.special')}";
					} else if (result.type == "COMMON") {
						individualScheduleNode.innerHTML = "${msg('label.from')} " + result.begin + " ${msg('label.to')} " + result.end;
					} else {
						individualScheduleNode.innerHTML = "${msg('label.employee-shedule.none')}";
					}
				}
			},
			scope: this
		}
	});

	Alfresco.util.Ajax.request({
		method: "POST",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/wcalendar/schedule/get/parentScheduleStdTime",
		dataObj: dataObj,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					var parentScheduleNode = YAHOO.util.Dom.get("${parentScheduleControlId}");
					if (result.type == "SPECIAL") {
						parentScheduleNode.innerHTML = "${msg('label.special')}";
					} else if (result.type == "COMMON") {
						parentScheduleNode.innerHTML = "${msg('label.from')} " + result.begin + " ${msg('label.to')} " + result.end;
					} else {
						parentScheduleNode.innerHTML = "${msg('label.employee-shedule.none')}";
					}
				}
			},
			scope: this
		}
	});
}
//]]></script>

<div class="set" id="${fieldHtmlId}">
	<div class="form-field" id="${parentScheduleFieldId}">
		<div class="viewmode-field">
			<span class="viewmode-label">${msg("label.shedule-control.parent")}:</span>
			<span class="viewmode-value" id="${parentScheduleControlId}"></span>
		</div>
	</div>

	<div class="form-field" id="individualScheduleFieldId">
		<div class="viewmode-field">
			<span class="viewmode-label">${msg("label.shedule-control.individual")}:</span>
			<span class="viewmode-value" id="${individualScheduleControlId}"></span>
		</div>
	</div>
</div>
