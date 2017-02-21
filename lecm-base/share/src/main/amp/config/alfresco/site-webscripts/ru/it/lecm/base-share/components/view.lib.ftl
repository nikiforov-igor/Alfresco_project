<#-- DEPRECATED! UNUSED! -->
<#macro viewForm formId="view-node-form">
	<script type="text/javascript">//<![CDATA[
	var viewDialog = null;

	function viewAttributes(nodeRef, setId, title, viewFormId) {
		var obj = {
			htmlid:nodeRef.replace("workspace://SpacesStore/","").replace("-",""),
			itemKind:"node",
			itemId:nodeRef,
			formId: viewFormId != null ? viewFormId : "${formId}",
			mode:"view"
		};
		if (setId != null) {
			obj.setId = setId;
		}

		Alfresco.util.Ajax.request(
				{
					url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj: obj,
					successCallback:{
                        fn:function(response) {
                            var formEl = Dom.get("${formId}-content");
                            formEl.innerHTML = response.serverResponse.responseText;
                            if (viewDialog != null) {
                                Dom.setStyle("${formId}", "display", "block");
                                var message = title ? Alfresco.messages.global[title] : "${msg("logicecm.view")}";
                                var titleElement = Dom.get("${formId}-head");
                                if (titleElement) {
                                    titleElement.innerHTML = message;
                                }
                                viewDialog.show();
                            }
                        }
					},
					failureMessage:"message.failure",
					execScripts:true
				});
		return false;
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
					width: "60em"
				});
		YAHOO.Bubbling.on("hidePanel", hideViewDialog);
	}

    function showEmployeeViewByLink(employeeLinkNodeRef, title) {
		Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getEmployeeByLink",
			dataObj: {
				nodeRef: employeeLinkNodeRef
			},
			successCallback: {
				fn: function (oResponse) {
	                if (oResponse.json && oResponse.json.nodeRef) {
		                viewAttributes(oResponse.json.nodeRef, null, title);
			        } else {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message("message.details.failure")
						});
					}
				}
			},
			failureMessage: Alfresco.util.message("message.details.failure")
		});
    }

	//инициализация view-node-form для того, чтобы каждый раз самостоятельно не вызывать этот метод
	YAHOO.util.Event.onContentReady ("${formId}", createDialog);
	//]]>
	</script>
	<div id="${formId}" class="yui-panel hidden1"><#--отрисовываем форму скрытой всегда - при показе ей явным образом выставляется display:block-->
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

<#function showViewLink name nodeRef titleId>
	<#return "<a href=\"javascript:void(0);\" onclick=\"viewAttributes('" + nodeRef + "', null, \'" + titleId + "\')\">" + name + "</a>">
</#function>
