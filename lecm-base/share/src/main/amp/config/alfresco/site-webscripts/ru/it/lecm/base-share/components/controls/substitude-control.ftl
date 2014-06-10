<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}-value", function() {
	Alfresco.util.Ajax.jsonRequest(
			{
				url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
				method: "POST",
				dataObj: {
					nodeRef: "${form.arguments.itemId}",
					substituteString: "${params.formatString!}"
				},
				successCallback: {
					fn: function (response) {
						if (response.json != null && response.json.formatString != null) {
							YAHOO.util.Dom.get("${fieldHtmlId}-value").innerHTML = response.json.formatString;
						}
					},
					scope: this
				}
			});
});
//]]>
</script>

<div class="form-field">
	<div class="viewmode-field ">
		<span class="viewmode-label">${field.label?html}:</span>
		<span class="viewmode-value" id="${fieldHtmlId}-value"></span>
	</div>
</div>