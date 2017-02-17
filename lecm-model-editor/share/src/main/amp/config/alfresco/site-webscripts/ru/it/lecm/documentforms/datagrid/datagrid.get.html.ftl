<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />
</@>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
	<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-forms-editor/lecm-forms-datagrid.js"></@script>
</@>

<!-- Historic Properties Viewer -->


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="document-forms-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			var datagrid;

			function initFormEditor() {
				createFormsDatagrid();
				showFormsDatagrid();
			}

			function createFormsDatagrid() {
                LogicECM.module.FormsEditor.DataGrid.prototype.onActionExportXML = function (item) {
					document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + item.nodeRef;
                };
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
								},
                                {
                                    type:"datagrid-action-link-${bubblingLabel}",
                                    id:"onActionExportXML",
                                    permission:"edit",
                                    label:"${msg("actions.export-xml")}"
                                }
							],
							bubblingLabel: "${bubblingLabel!''}",
							showCheckboxColumn: false,
							attributeForShow: "cm:name",
							excludeColumns: ["lecm-forms-editor:id", "lecm-forms-editor:form-id", "lecm-forms-editor:form-evaluator"],
							editFormWidth: "70em"
						}).setMessages(${messages});
			}

			function showFormsDatagrid() {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "/lecm/docforms/root",
                    dataObj: {
                        modelName: "${doctype!""}"
                    },
					successCallback: {
                        scope: this,
						fn: function (response) {
							var oResults = response.json;
							if (oResults && oResults.nodeRef) {
								datagrid.options.datagridMeta = {
									useFilterByOrg: false,
									itemType: "lecm-forms-editor:form",
									nodeRef: oResults.nodeRef,
									actionsConfig: {
										fullDelete: true
									}
								};
								datagrid.draw();
							}
						}
					},
					failureMessage: "${msg("message.failure")}"
				});
			}

			YAHOO.util.Event.onDOMReady(initFormEditor);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
