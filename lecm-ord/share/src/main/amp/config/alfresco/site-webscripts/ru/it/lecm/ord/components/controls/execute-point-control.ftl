<#assign htmlId = args.htmlid>
<#assign formId = htmlId + '-form'>
<#assign buttonsContainerId = 'point-buttons-container'>

<script type="text/javascript">//<![CDATA[
(function() {
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
			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/ord/SetPointExecutedStatus',
				dataObj: {
					pointRef: "${form.arguments.itemId}"
				},
                successCallback: {
                     fn:function(response){
                         YAHOO.Bubbling.fire("dataItemUpdated",
                         {
                            item: response.json,
                         }); 
                     },
                     scope: this
                },
				failureMessage: "${msg('message.failure')}",
				execScripts: true,
				scope: this
			});
		}

		function createExecutedPointButton(){
			<#if form.mode == "edit">
				var executePointButton = new YAHOO.widget.Button({
												 container: "${buttonsContainerId}",
												 type: "submit",
												 label: "${msg('ord.item.execute.button')}",
												 onclick: {
												  fn: function(){executePoint();},
												  scope: this
												 }
												});
			</#if>
			<#if form.mode == "view">
				var executePointButton = new YAHOO.widget.Button({
								 container: "${buttonsContainerId}",
								 type: "button",
								 label: "${msg('ord.item.execute.button')}",
								 onclick: {
								  fn: function(){
									  executePoint();
									  var formContainer = Dom.get("${formId}-container");
									  if (formContainer) {
										  var panel = formContainer.offsetParent;
										  var closeButton = YAHOO.util.Selector.query("a.container-close", panel, true);
										  if (closeButton) {
											  closeButton.click();
										  }
									  }
								  },
								  scope: this
								 }
								});
			</#if>
		}

		YAHOO.util.Event.onDOMReady(checkPointExecutedStatus);

})();
//]]></script>

<div class="form-field">
	<div id='${buttonsContainerId}'></div>
</div>