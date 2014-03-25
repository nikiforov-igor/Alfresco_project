<#assign id = args.htmlid>

<style type="text/css">
	.incoming-members-dashlet {
		padding: 5px 10px;
	}

	.incoming-members-dashlet .form-container .form-field {
		margin: 0;
	}

	.incoming-members-dashlet .form-container .viewmode-field {
		margin: 0;
	}
</style>

<div class="dashlet document bordered members">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body">
	    <div id="${id}-results" class="incoming-members-dashlet"></div>
    </div>

	<script type="text/javascript">//<![CDATA[
	(function(){
		function init(){
			Alfresco.util.Ajax.request(
					{
						url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj:{
							htmlid: "${id}-${nodeRef}",
							itemKind: "node",
							itemId: "${nodeRef}",
							formId: "document-members",
							mode:"view"
						},
						successCallback:{
							fn:function(response){
								YAHOO.util.Dom.get("${id}-results").innerHTML = response.serverResponse.responseText;
							}
						},
						failureMessage: "message.failure",
						execScripts: true,
						htmlId: "${id}-${nodeRef}"
					});
		}
		YAHOO.util.Event.onContentReady("${id}-results", init, true);
	})();
	//]]></script>
</div>