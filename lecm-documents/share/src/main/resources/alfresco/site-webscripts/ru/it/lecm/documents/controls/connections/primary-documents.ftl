<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<div class="form-field with-grid" id="primary-documents-${controlId}">
	<label for="${controlId}">${msg("label.connected.with.documents")}:</label>

	<@grid.datagrid containerId false>
		<script type="text/javascript">//<![CDATA[
		(function () {
			YAHOO.util.Event.onDOMReady(function (){
				var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
					usePagination: true,
					pageSize: 10,
					showExtendSearchBlock: false,
					actions: [],

					datagridMeta: {
						itemType: "lecm-connect:connection",
						datagridFormId: "connected-with-documents",
						createFormId: "",
						searchConfig: {
							filter: 'lecm\\-connect\\:connected\\-document\\-assoc\\-ref:"${form.arguments.itemId}"'
						}
					},

					dataSource:"${"lecm/search"}",

					allowCreate: false,
					showActionColumn: false,
					showCheckboxColumn: false,
					bubblingLabel: "${containerId}"
				}).setMessages(${messages});

				datagrid.draw();
			});

		})();
		//]]></script>
	</@grid.datagrid>
</div>