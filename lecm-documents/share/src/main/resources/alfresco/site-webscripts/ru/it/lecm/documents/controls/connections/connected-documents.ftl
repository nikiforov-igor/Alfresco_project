<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<div class="form-field with-grid" id="connected-documents-${controlId}">
	<input type="hidden" id="${fieldHtmlId}" name="${fieldHtmlId}" value="${field.value?html}" />
	<label for="${controlId}">${msg("label.connected.documents")}:</label>

	<div class="add-connection" style="float:none">
        <span id="${controlId}-add-connection-button" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.connection.add")}">${msg("button.connection.add")}</button>
           </span>
        </span>
	</div>

	<@grid.datagrid containerId false>
		<script type="text/javascript">//<![CDATA[
		(function () {
			YAHOO.util.Event.onDOMReady(function (){
				var control = new LogicECM.module.Connection.ConnectedDocuments("${fieldHtmlId}").setMessages(${messages});
				control.setOptions({
					primaryDocumentNodeRef: "${form.arguments.itemId}",
					datagridBublingLabel: "${containerId}"
				});

				var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
					usePagination: true,
					pageSize: 10,
					showExtendSearchBlock: false,
					actions: [
						{
							type: "datagrid-action-link-${containerId}",
							id: "onActionDelete",
							permission: "delete",
							label: "${msg("actions.delete-row")}",
							confirmFunction: function () {
								YAHOO.Bubbling.fire("connectionsUpdate", {});
							}
						}],

					datagridMeta: {
						itemType: "lecm-connect:connection",
						datagridFormId: "connected-documents",
						createFormId: "",
						actionsConfig: {
							fullDelete: true
						},
						searchConfig: {
							filter: 'lecm\\-connect\\:primary\\-document\\-assoc\\-ref:"${form.arguments.itemId}"'
						}
					},

					dataSource:"${"lecm/search"}",

					allowCreate: false,
					showActionColumn: true,
					showCheckboxColumn: false,
					bubblingLabel: "${containerId}"
				}).setMessages(${messages});

				datagrid.draw();
			});

		})();
		//]]></script>
	</@grid.datagrid>
</div>