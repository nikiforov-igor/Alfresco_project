<#assign documentRef = "${form.arguments.itemId}"?string/>
<#assign id = documentRef?replace("workspace://SpacesStore/", "")?replace("-", "")/>

<div id="${id}-view-metadata-form" class="metadata-form-control"></div>

<script type="text/javascript">//<![CDATA[
	//<![CDATA[
	(function() {
		function init() {
			var nodeRef = "${documentRef}";
			var data = {
				htmlid: "mainFormDocumentMetadata-" + "${id}",
				nodeRef: nodeRef
			};
			Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/metadata",
						dataObj: data,
						successCallback: {
							fn: function (response) {
								YAHOO.util.Dom.get("${id}-view-metadata-form").innerHTML = response.serverResponse.responseText;
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
		}
		YAHOO.util.Event.onContentReady("${id}-view-metadata-form", init, true);
	})();
	//]]>
</script>