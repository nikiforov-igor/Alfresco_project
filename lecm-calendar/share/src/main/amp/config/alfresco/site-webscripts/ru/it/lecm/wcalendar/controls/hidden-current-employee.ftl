<script type="text/javascript">//<![CDATA[
var hiddenAdded = new YAHOO.util.Element("${fieldHtmlId}-added");
hiddenAdded.on('contentReady', Absence_DrawNodeRefInHidden, this);

function Absence_DrawNodeRefInHidden() {
		Alfresco.util.Ajax.request({
		method: "GET",
		url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/get/currentEmployee",
		requestContentType: "application/json",
		responseContentType: "application/json",
		successCallback: {
			fn: function (response) {
				var result = response.json;
				if (result != null) {
					hiddenAdded.set('value', result.nodeRef);
				}
			},
			scope: this
		}
	});
}
//]]></script>

<#if form.mode == "edit" || form.mode == "create">
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed" value=""/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added" value=""/>
</#if>