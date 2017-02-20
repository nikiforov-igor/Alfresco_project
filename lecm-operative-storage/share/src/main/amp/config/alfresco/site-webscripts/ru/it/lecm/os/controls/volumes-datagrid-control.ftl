<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-volumes-list">
<#assign objectId = field.name?replace("-", "_")>

<#assign itemEditFormId = (field.control.params.itemEditFormId) ! "">

<#assign allowCreate = true>
<#if field.control.params.allowCreate?? && field.control.params.allowCreate == "false">
	<#assign allowCreate = false>
</#if>
<#assign inArchive = false>

<#if form.fields["prop_lecm-os_nomenclature-case-status"]??>
	<#assign status = form.fields["prop_lecm-os_nomenclature-case-status"].value>
	<#assign allowCreate = status == "PROJECT" || status == "OPEN">
	<#assign inArchive = status == "ARCHIVE">
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
	<#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign allowEdit = "true"/>
<#if field.control.params.allowEdit??>
	<#assign allowEdit = field.control.params.allowEdit?lower_case/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
	<#assign showActions = field.control.params.showActions/>
</#if>

<#assign useBubbling = "true"/>
<#if field.control.params.useBubbling??>
	<#assign useBubbling = field.control.params.useBubbling?lower_case/>
<#else>
	<#assign useBubbling = "true"/>
</#if>

<#if useBubbling = "false">
	<#assign bubblingId = ""/>
<#else>
	<#assign bubblingId = containerId/>
</#if>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
	<#assign usePagination = field.control.params.usePagination/>
</#if>

<div class="control with-grid" id="${controlId}">
	<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
		<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<@grid.datagrid containerId false>
		<script type="text/javascript">//<![CDATA[
		(function () {
			function init() {
				LogicECM.module.Base.Util.loadScripts([
					'scripts/lecm-base/components/advsearch.js',
					'scripts/lecm-base/components/lecm-datagrid.js'
				], createDatagrid);
			}
			YAHOO.util.Event.onDOMReady(init);
			function createDatagrid() {
				LogicECM.module.Base.DataGridControl_${objectId} = function(htmlId) {
					return LogicECM.module.Base.DataGridControl_${objectId}.superclass.constructor.call(this, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation"]);
				};

				YAHOO.extend(LogicECM.module.Base.DataGridControl_${objectId}, LogicECM.module.Base.DataGrid, {
                    onActionPrintReport: function(item) {
                        LogicECM.module.Base.Util.printReport(item.nodeRef, "nomenclature-case-card");
                    },

					onActionEditExt: function(item) {
						// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
						if (this.editDialogOpening) {
							return;
						}
						this.editDialogOpening = true;

						var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
						var templateRequestParams = {
							itemKind: "node",
							formId: this.options.editForm,
							itemId: item.nodeRef,
							mode: "edit",
							submitType: "json",
							showCancelButton: true
						};


						// Using Forms Service, so always create new instance
						var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
						editDetails.setOptions(
							{
								width: this.options.editFormWidth,
								templateUrl:templateUrl,
								templateRequestParams:templateRequestParams,
								actionUrl:null,
								destroyOnHide:true,
								doBeforeDialogShow:{
									fn: function(p_form, p_dialog) {
										var contId = p_dialog.id + "-form-container";
										if (item.type && item.type != "") {
											Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
										}
										p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
										this.editDialogOpening = false;

										p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
									},
									scope:this
								},
								onSuccess:{
									fn:function DataGrid_onActionEdit_success(response) {
										// Reload the node's metadata
										YAHOO.Bubbling.fire("datagridRefresh",
											{
												bubblingLabel: this.options.bubblingLabel
											});

										Alfresco.util.PopupManager.displayMessage({
											text: this.msg("message.details.success")
										});

										this.editDialogOpening = false;
										var logText = '#initiator изменил том номенклатурного дела.';
										this.createBJRecord(item.nodeRef, logText);
									},
									scope:this
								},
								onFailure:{
									fn:function DataGrid_onActionEdit_failure(response) {
										Alfresco.util.PopupManager.displayMessage(
											{
												text:this.msg("message.details.failure")
											});
										this.editDialogOpening = false;
									},
									scope:this
								}
							}).show();
					},

					createBJRecord: function(nodeRef, desc, category) {
						Alfresco.util.Ajax.jsonPost({
							url: Alfresco.constants.PROXY_URI + 'lecm/business-journal/api/record/create',
							dataObj: {
								mainObject: "${form.arguments.itemId}",
								category: category || 'EDIT',
								description: desc,
								objects: [nodeRef]
							},
							scope: this
						});
					},

					onCreated: function(layer, args) {
						var obj = args[1];
						var logText = '#initiator добавил том номенклатурного дела.'
						this.createBJRecord(obj.nodeRef, logText, 'OS_ADD_VOLUME');
					},

					onDeleted: function(layer, args) {
						var obj = args[1];
						var logText = '#initiator удалил том номенклатурного дела.'
						this.createBJRecord(obj.items[0].nodeRef, logText, 'OS_REMOVE_VOLUME');
					}

				}, true);

				var datagrid = new LogicECM.module.Base.DataGridControl_${objectId}('${containerId}').setOptions({
					usePagination: ${usePagination?string},
					<#if field.control.params.overrideSortingWith??>
						overrideSortingWith: ${field.control.params.overrideSortingWith?string},
					</#if>
					showExtendSearchBlock: false,
						actions: [


							{
								type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>-custom</#if>",
								id: "onActionEditExt",
								permission: "edit",
								label: "${msg("actions.edit")}"
							},

							<#if allowCreate>
							{
								type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
								id: "onActionDelete",
								permission: "delete",
								label: "${msg("actions.delete-row")}"
							}
							</#if>
						],
						<#if inArchive>
							editForm: '${itemEditFormId}',
						</#if>
						datagridMeta: {

							itemType: "${field.control.params.itemType!field.endpointType!""}",
							useChildQuery: true,
							datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
							createFormId: "${field.control.params.createFormId!""}",
							nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
							actionsConfig: {
								fullDelete: "${field.control.params.fullDelete!"false"}"
							},
							sort: "${field.control.params.sort!""}"
						},
						dataSource:"${field.control.params.ds!"lecm/search"}",
						<#if bubblingId != "">
							bubblingLabel: "${bubblingId}",
						<#else>
							bubblingLabel: "custom",
						</#if>
						<#if field.control.params.height??>
							height: ${field.control.params.height},
						</#if>
						<#if field.control.params.configURL??>
							configURL: "${field.control.params.configURL}",
						</#if>
						<#if field.control.params.repoDatasource??>
							repoDatasource: ${field.control.params.repoDatasource},
						</#if>
						allowCreate: ${allowCreate?string},
						showActionColumn: ${showActions?string},
						showCheckboxColumn: false
						<#if field.control.params.fixedHeader??>
							,fixedHeader: ${field.control.params.fixedHeader}
						</#if>
					}).setMessages(${messages});

					YAHOO.Bubbling.on("nodeCreated", datagrid.onCreated, datagrid);
					YAHOO.Bubbling.on("dataItemsDeleted", datagrid.onDeleted, datagrid);

				datagrid.draw();
			}
		})();
		//]]></script>
	</@grid.datagrid>
</div>
<div class="clear"></div>
