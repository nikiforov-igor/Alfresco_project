<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="document-forms-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			var datagrid;

			function initFormEditor() {
				createFormsDatagrid();
				showFormsDatagrid();
			}

			function createFormsDatagrid() {
				datagrid = new LogicECM.module.FormsEditor.DataGrid('${id}').setOptions(
						{
							usePagination: false,
							actions: [
								{
									type: "datagrid-action-link-${bubblingLabel!''}",
									id: "onActionEdit",
									permission: "edit",
									label: "${msg("actions.edit")}"
								},
								{
									type: "datagrid-action-link-${bubblingLabel!''}",
									id: "onActionDelete",
									permission: "delete",
									label: "${msg("actions.delete-row")}"
								}
							],
							bubblingLabel: "${bubblingLabel!''}",
							showCheckboxColumn: false,
							attributeForShow: "cm:name",
							excludeColumns: ["lecm-forms-editor:form-evaluator", "lecm-forms-editor:form-id"],
							editFormWidth: "70em"
						}).setMessages(${messages});
			}

			function showFormsDatagrid() {
				Alfresco.util.Ajax.jsonGet(
						{
							url: Alfresco.constants.PROXY_URI + "/lecm/docforms/root?modelName=" + encodeURIComponent("${doctype!""}"),
							successCallback: {
								fn: function (response) {
									var oResults = response.json;
									if (oResults != null && oResults.nodeRef != null) {
										datagrid.options.datagridMeta = {
											itemType: "lecm-forms-editor:form",
											nodeRef: oResults.nodeRef,
											actionsConfig:{
												fullDelete:true
											}
										};
										datagrid.draw();
									}
								},
								scope: this
							},
							failureMessage: "message.failure"
						});
			}

			YAHOO.util.Event.onDOMReady(initFormEditor);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
