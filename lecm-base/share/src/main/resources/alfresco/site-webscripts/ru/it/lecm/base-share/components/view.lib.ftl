<#macro viewForm formId="view-node-form">
	<script type="text/javascript">//<![CDATA[
	var viewDialog = null;

	function viewAttributes(nodeRef, setId) {
		Alfresco.util.Ajax.request(
				{
					url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj:{
						htmlid:"NodeMetadata-" + nodeRef,
						itemKind:"node",
						itemId:nodeRef,
						formId:"${formId}",
						mode:"view",
						setId: (setId != undefined && setId != null) ? setId : "common"
					},
					successCallback:{
						fn:showViewDialog
					},
					failureMessage:"message.failure",
					execScripts:true
				});
		return false;
	}

	function showViewDialog(response) {
		var formEl = Dom.get("${formId}-content");
		formEl.innerHTML = response.serverResponse.responseText;
		if (viewDialog != null) {
			Dom.setStyle("${formId}", "display", "block");
			viewDialog.show();
		}
	}

	function hideViewDialog(layer, args) {
		var mayHide = false;
		if (viewDialog != null) {
			if (args == undefined || args == null) {
				mayHide = true;
			} else if (args[1] && args[1].panel && args[1].panel.id == viewDialog.id){
				mayHide = true
			}
			if (mayHide){
				viewDialog.hide();
				Dom.setStyle("${formId}", "display", "none");
			}
		}
	}

	function createDialog() {
		viewDialog = Alfresco.util.createYUIPanel("${formId}",
				{
					width: "50em"
				});
		YAHOO.Bubbling.on("hidePanel", hideViewDialog);
	}

	//инициализация view-node-form для того, чтобы каждый раз самостоятельно не вызывать этот метод
	YAHOO.util.Event.onContentReady ("${formId}", createDialog);
	//]]>
	</script>
	<div id="${formId}" class="yui-panel">
		<div id="${formId}-head" class="hd">${msg("logicecm.view")}</div>
		<div id="${formId}-body" class="bd">
			<div id="${formId}-content"></div>
			<div class="bdft">
	            <span id="${formId}-cancel" class="yui-button yui-push-button">
	                <span class="first-child">
	                    <button type="button" tabindex="0" onclick="hideViewDialog();">${msg("button.close")}</button>
	                </span>
	            </span>
			</div>
		</div>
	</div>
</#macro>

<#function showViewLink name nodeRef>
	<#return "<a href=\"javascript:void(0);\" onclick=\"viewAttributes('" + nodeRef + "')\">" + name + "</a>">
</#function>