<script type="text/javascript">//<![CDATA[
(function () {
	var initEventFromDate = null;
	var initEventToDate = null;
	var eventFromDate = null;
	var eventToDate = null;

	function initBubbling() {
		YAHOO.Bubbling.on("changeEventFromDate", onChangeFromDate);
		YAHOO.Bubbling.on("changeEventToDate", onChangeToDate);
	}
	function onChangeFromDate(layer, args) {
		if (args[1].date != null) {
			if (initEventFromDate == null) {
				initEventFromDate = args[1].date;
			}
			eventFromDate = args[1].date;
			updateDisplayText();
		}
	}
	function onChangeToDate(layer, args) {
		if (args[1].date != null) {
			if (initEventToDate == null) {
				initEventToDate = args[1].date;
			}
			eventToDate = args[1].date;
			updateDisplayText();
		}
	}

	function updateDisplayText() {
		if ((initEventFromDate != null && initEventFromDate != eventFromDate) ||
				(initEventToDate != null && initEventToDate != eventToDate)) {
			YAHOO.util.Dom.setStyle("${fieldHtmlId}-parent", "display", "block");
		} else {
			YAHOO.util.Dom.setStyle("${fieldHtmlId}-parent", "display", "none");
		}
	}

	YAHOO.util.Event.onContentReady("${fieldHtmlId}-parent", initBubbling);
})();
//]]></script>

<div id="${fieldHtmlId}-parent" class="control date viewmode hidden1">
	<div class="label-div"></div>
	<div class="container">
		<div class="value-div">
            ${msg("message.event.changeDate")}
		</div>
	</div>
</div>
<div class="clear"></div>