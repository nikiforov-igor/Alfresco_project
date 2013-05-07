<script type="text/javascript">//<![CDATA[

(function() {
	YAHOO.util.Event.onContentReady("workflow-form_assoc_packageItems-added", Approval_getPresentStringHidden, true);
})();

function Approval_getPresentStringHidden() {
	var packageItemsAdded = YAHOO.util.Dom.get("workflow-form_assoc_packageItems-added");
	Alfresco.util.Ajax.request({
		method: "GET",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/GetDocumentDataByTaskId?nodeRef=" + packageItemsAdded.value,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					var outputNode = YAHOO.util.Dom.get("${fieldHtmlId}");
					outputNode.value =  "Согласование по документу " + result.presentString;
				}
			},
			scope: this
		}
	});
}

//]]></script>

 <input type="hidden" id="${fieldHtmlId}" name="${field.name}"/>
