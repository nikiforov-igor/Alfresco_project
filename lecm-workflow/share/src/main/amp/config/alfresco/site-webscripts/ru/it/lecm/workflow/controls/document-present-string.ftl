<script type="text/javascript">//<![CDATA[

(function() {
	YAHOO.util.Event.onContentReady("${fieldHtmlId}-output", Approval_getPresentString, true);
})();

function Approval_getPresentString() {
	var myID = "${fieldHtmlId}";
	var outputNode = YAHOO.util.Dom.get("${fieldHtmlId}-output");
	var IDElements = myID.split("_");
	IDElements.splice(-2, 2);
	var commonID = IDElements.join("_");
	var formID = commonID + "-form";
	var form = YAHOO.util.Dom.get(formID);
	var action = form.action;
	var actionElements = action.split("/");
	var taskIDEscaped = actionElements[actionElements.length - 2];
	Alfresco.util.Ajax.request({
		method: "GET",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/GetDocumentDataByTaskId?taskID=" + taskIDEscaped,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					outputNode.innerHTML =  "<h2>" + result.presentStringWithLink + "</h2>";
				}
			},
			scope: this
		}
	});
}

//]]></script>

<div id="${fieldHtmlId}-output"></div>
<br>
