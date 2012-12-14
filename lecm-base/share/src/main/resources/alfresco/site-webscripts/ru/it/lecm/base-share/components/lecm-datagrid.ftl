
<#-- Макрос для включения включения всплывающего окна по клику на запись.
Окно будет вызывать по вызову метода viewAttributes. Пример см. в datagrid.get.html.ftl
Список параметров:
viewFormId(необязательный) - по умолчанию равен view-node-form. Идентификатор, использующийся для построения html для всплывающего окна
-->
<#macro viewForm viewFormId="view-node-form">
<script type="text/javascript">//<![CDATA[
	var viewDialog = null;

	function viewAttributes(nodeRef) {
		Alfresco.util.Ajax.request(
				{
					url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj:{
						htmlid:"NodeMetadata-" + nodeRef,
						itemKind:"node",
						itemId:nodeRef,
						formId:"${viewFormId}",
						mode:"view"
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
		var formEl = Dom.get("${viewFormId}-content");
		formEl.innerHTML = response.serverResponse.responseText;
		if (viewDialog != null) {
			viewDialog.show();
		}
	}

	function hideViewDialog() {
		if (viewDialog != null) {
			viewDialog.hide();
		}
	}

	function createDialog() {
		viewDialog = Alfresco.util.createYUIPanel("${viewFormId}",
				{
					width:"487px"
				});
	}

	function showEmployeeViewByLink(employeeLinkNodeRef) {
		var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeeLinkEmployee?nodeRef=" + employeeLinkNodeRef;
		var callback = {
			success:function (oResponse) {
				var oResults = eval("(" + oResponse.responseText + ")");
				if (oResults && oResults.nodeRef) {
					viewAttributes(oResults.nodeRef);
				} else {
					Alfresco.util.PopupManager.displayMessage(
							{
								text:me.msg("message.details.failure")
							});
				}
			},
			failure:function (oResponse) {
				Alfresco.util.PopupManager.displayMessage(
						{
							text:me.msg("message.details.failure")
						});
			}
		};
		YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	}

	//инициализация view-node-form для того, чтобы каждый раз самостоятельно не вызывать этот метод
	YAHOO.util.Event.onContentReady ("${viewFormId}", createDialog);
//]]>
</script>
<div id="${viewFormId}" class="yui-panel">
	<div id="${viewFormId}-head" class="hd">${msg("logicecm.view")}</div>
	<div id="${viewFormId}-body" class="bd">
		<div id="${viewFormId}-content"></div>
		<div class="bdft">
            <span id="${viewFormId}-cancel" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" tabindex="0" onclick="hideViewDialog();">${msg("button.close")}</button>
                </span>
            </span>
		</div>
	</div>
</div>
</#macro>

<#-- Макрос для подключения грида
Список параметров:
id(обязательный) - идентификатор, использующийся для построения html и передающийся в объект DataGrid. Лучше использовать args.htmlid (по аналогии с другими местами в Alfresco)
showViewForm(необязательный) - включать/не включать всплывающее окна по клику на запись
viewFormId(необязательный) - по умолчанию равен view-node-form. Идентификатор, использующийся для построения html для всплывающего окна
-->
<#macro datagrid id showViewForm=true viewFormId="view-node-form">
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<div id="${id}-body" class="datagrid">
	<div class="datagrid-meta">
		<#if showViewForm>
			<@viewForm viewFormId/>
		</#if>
		<h2 id="${id}-title"></h2>
		<div id="${id}-description" class="datagrid-description"></div>
	</div>
	<div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button" style="display:none">
		<div class="yui-u first align-center">
			<div class="item-select">&nbsp;</div>
			<div id="${id}-paginator" class="paginator"></div>
		</div>
		<div class="yui-u align-right">
			<div class="items-per-page" style="visibility: hidden;">
				<button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
			</div>
		</div>
	</div>

    <div id="${id}-toolbar" style="display: none; margin-bottom: 3px;">
         <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('actions.add')}</button>
               </span>
         </span>
    </div>
	<div id="${id}-grid" class="grid"></div>

	<div id="${id}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
		<div class="yui-u first align-center">
			<div class="item-select">&nbsp;</div>
			<div id="${id}-paginatorBottom" class="paginator"></div>
		</div>
	</div>

	<!-- Action Sets -->
	<div style="display:none">
		<!-- Action Set "More..." container -->
		<div id="${id}-moreActions">
			<div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span></span></a></div>
			<div class="more-actions hidden"></div>
		</div>

		<!-- Action Set Templates -->
		<div id="${id}-actionSet" class="action-set simple">
			<#if actionSet??>
                <#list actionSet as action>
                    <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="action-link ${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
                </#list>
            </#if>
		</div>
	</div>
</div>
<#nested>
</#macro>