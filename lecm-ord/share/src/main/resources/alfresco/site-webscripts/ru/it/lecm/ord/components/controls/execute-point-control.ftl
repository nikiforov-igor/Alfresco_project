<#assign htmlId = args.htmlid>
<#assign formId = htmlId + '-form'>
<#assign buttonsContainerId = 'point-buttons-container'>

<script type="text/javascript">//<![CDATA[
(function() {
	<#if form.mode == "edit">
		function checkPointExecutedStatus (){
			Alfresco.util.Ajax.jsonRequest({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/ord/CheckPointExecutedStatus',
				dataObj: {
					pointRef: "${form.arguments.itemId}"
				},
				successCallback: {
					fn: function(response) {
							if (response) {
								var isExecuted = response.json.isExecuted;
								if ("true" !== isExecuted){
									createExecutedPointButton();
								}
							}
					}
				},
				failureMessage: "${msg('message.failure')}",
				execScripts: true,
				scope: this
			});
		}

		function executePoint (){
			Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/ord/SetPointExecutedStatus',
				dataObj: {
					pointRef: "${form.arguments.itemId}"
				},
				failureMessage: "${msg('message.failure')}",
				execScripts: true,
				scope: this
			});
		}

		function createExecutedPointButton(){
			var simpleDialog = Alfresco.util.ComponentManager.get("${htmlId}");
			var executePointButton = new YAHOO.widget.Button({
											 container: "${buttonsContainerId}",
											 type: "submit",
											 label: "${msg('ord.item.execute.button')}",
											 onclick: {
											  fn: function(){executePoint();},
											  scope: this
											 }
											});
		}

		YAHOO.util.Event.onDOMReady(checkPointExecutedStatus);

	</#if>
})();
//]]></script>

<div class="form-field">
	<#if form.mode == "edit">
		<div id='${buttonsContainerId}' style="margin-bottom: 10px;"></div>
	</#if>
</div>