<#assign htmlId = args.htmlid>
<#assign formId = htmlId + '-form'>
<#assign buttonsContainerId = 'point-buttons-container'>

<script type="text/javascript">//<![CDATA[
(function() {
		function isExecuteAvailable (){
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                dataObj: {
                    nodeRef: "${form.arguments.itemId}",
                    substituteString: "{lecm-protocol-ts:errand-assoc/sys:node-uuid},{../../../lecm-statemachine:status},{../../../lecm-eds-document:document-type-assoc/lecm-doc-dic-dt:registration-required}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response) {
                            var results, isDocOk;
                            if (response.json.formatString) {
                                results = response.json.formatString.split(",");
                                isDocOk = !results[0] && !(results[1] == Alfresco.util.message("lecm.protocol.statuses.approved") && results[2] == "true");
                            }
                            if (isDocOk) {
                                Alfresco.util.Ajax.jsonRequest({
                                    method: "GET",
                                    url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/protocol/CheckPointExecutedStatus',
                                    dataObj: {
                                        pointRef: "${form.arguments.itemId}"
                                    },
                                    successCallback: {
                                        fn: function (response) {
                                            if (response) {
                                                var isExecuted = response.json.isExecuted;
                                                if ("true" !== isExecuted) {
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
                        }
                    },
                    scope: this
                },
                failureMessage: "${msg('message.failure')}",
                scope: this
            });

		}

		function executePoint (){
			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/protocol/SetPointExecutedStatus',
				dataObj: {
					pointRef: "${form.arguments.itemId}"
				},
                successCallback: {
                     fn:function(response){

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
												 label: "${msg('protocol.point.execute.button')}",
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
								 label: "${msg('protocol.point.execute.button')}",
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

		YAHOO.util.Event.onDOMReady(isExecuteAvailable);

})();
//]]></script>

<div class="form-field">
	<div id='${buttonsContainerId}'></div>
</div>