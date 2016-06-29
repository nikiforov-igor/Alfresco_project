<script type="text/javascript">//<![CDATA[

(function() {
	YAHOO.util.Event.onContentReady("${fieldHtmlId}-output", getReservationTaskMessageByTaskID, true);
})();

function getReservationTaskMessageByTaskID() {
	var myID = "${fieldHtmlId}";
	var outputNode = YAHOO.util.Dom.get("${fieldHtmlId}-output");
	
	var nodeRef = location.search.replace('?nodeRef=','');
	Alfresco.util.Ajax.request({
		method: "GET",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/workflow/reservation/GetReservationTaskMessageByNodeRef?nodeRef=" + nodeRef,
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					outputNode.innerHTML = result.reservationTaskMessage;
				}
			},
			scope: this
		}
	});
}

//]]></script>

<div id="${fieldHtmlId}-output"></div>
<br>
