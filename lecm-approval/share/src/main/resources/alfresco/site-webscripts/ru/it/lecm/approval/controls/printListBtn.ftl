<#assign controlId = fieldHtmlId + "-cntrl">
<#assign reportId = "approval-list">

<div class="form-field">
<#escape x as x?js_string>
	<div id="${controlId}" class="yui-skin-sam">
		<button id="${controlId}-print-button" type="button" onclick="LogicECM.util.printNode('${form.arguments.itemId}')">${msg("button.print")}</button>
	</div>
</#escape>

	<script type="text/javascript">
		(function() {
			window.LogicECM = window.LogicECM || {};
			window.LogicECM.util = window.LogicECM.util || {};

			window.LogicECM.util.printNode = window.LogicECM.util.printNode || function(nodeRef) {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/${reportId}?ID=" + encodeURI(nodeRef),
					successCallback: {
						fn: function ApprovalListPrint_onSuccess(response) {
							window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText, "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");

							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.component.Base.prototype.msg("approvalList.report.success")
							});
						}
					},
					failureCallback: {
						fn: function ApprovalListPrint_onFailure(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.component.Base.prototype.msg("approvalList.report.failure")
							});
						}
					}
				});
			}
		})();
	</script>
</div>
