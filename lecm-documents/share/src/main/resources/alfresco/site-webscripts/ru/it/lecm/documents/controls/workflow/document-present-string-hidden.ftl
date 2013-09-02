<#assign leftPartText = "">
<#if field.control.params.leftPartTextCode??>
	<#assign leftPartText = msg(field.control.params.leftPartTextCode)>
<#elseif field.control.params.leftPartText??>
	<#assign leftPartText = field.control.params.leftPartText>
</#if>

<#assign rightPartText = "">
<#if field.control.params.rightPartTextCode??>
	<#assign rightPartText = msg(field.control.params.rightPartTextCode)>
<#elseif field.control.params.rightPartText??>
	<#assign rightPartText = field.control.params.rightPartText>
</#if>

<script type="text/javascript">//<![CDATA[

(function() {
	YAHOO.Bubbling.on("afterSetItems", getDocumentPresentStringHidden);
})();

function getDocumentPresentStringHidden(layer, args) {
	var nodeRef = args[1].items;
	if (nodeRef != null) {
		Alfresco.util.Ajax.request({
			method: "GET",
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/getProperties?nodeRef=" + nodeRef,
			requestContentType: "application/json",
			responseContentType: "application/json",
			successCallback: {
				fn: function (response) {
					var result = response.json[0];
					if (result != null && result["ext-present-string"] != null) {
						var outputNode = YAHOO.util.Dom.get("${fieldHtmlId}");
						if (outputNode != null) {
							outputNode.value =  "${leftPartText} " + result["ext-present-string"] + " ${rightPartText}";
						}
					}
				},
				scope: this
			}
		});
	}
}

//]]></script>

 <input type="hidden" id="${fieldHtmlId}" name="${field.name}"/>
