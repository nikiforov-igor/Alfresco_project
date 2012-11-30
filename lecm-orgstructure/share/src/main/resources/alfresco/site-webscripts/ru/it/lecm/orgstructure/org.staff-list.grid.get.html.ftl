<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>

<div class="yui-t1" id="orgstructure-staff-grid-with-tree">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id>
			<script type="text/javascript">//<![CDATA[
			(function () {
				function init() {
					LogicECM.module.Base.DataGrid.prototype.onActionEmployeeAdd = function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnDeleteComplete) {
						var me = this;
						this.createDialogShow({itemType:"lecm-orgstr:employee-link", nodeRef:p_item.nodeRef}, function (employeeRef){
							// создаем ассоциацию
							var onSuccess = function ObjectFinder__loadSelectedItems_onSuccess(response) {
								var createdAssoc = response.json.createdAssoc;
								if (createdAssoc){
									YAHOO.Bubbling.fire("dataItemUpdated",
											{
												item:p_item,
												bubblingLabel:me.options.bubblingLabel
											});
									Alfresco.util.PopupManager.displayMessage(
											{
												text:this.msg("message.updated")
											});
								}
							};
							var onFailure = function onFailure(response) {
								this.onDelete([{nodeRef:employeeRef}], owner, {fullDelete:true}, fnDeleteComplete);
								Alfresco.util.PopupManager.displayMessage(
										{
											text:"Ошибка"
										});
							};
							Alfresco.util.Ajax.jsonRequest(
									{
										url: Alfresco.constants.PROXY_URI + "lecm/base/createAssoc",
										method: "POST",
										dataObj:
										{
											source:p_item.nodeRef,
											target:employeeRef,
											assocType:"lecm-orgstr:element-member-employee-assoc"
										},
										successCallback:
										{
											fn: onSuccess,
											scope: this
										},
										failureCallback:
										{
											fn: onFailure,
											scope: this
										}
									});
						}.bind(me));
					};
					LogicECM.module.Base.DataGrid.prototype.onActionEmployeeDelete = function DataGridActions_onActionEmployeeDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
						var me = this,
								items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
						var staffRow = items[0];
						// Получаем для штатного расписания ссылку на сотрудника
						var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
						var callback = {
							success:function (oResponse) {
								var oResult = eval("(" + oResponse.responseText + ")");
								if (oResult) {
									var linkRef = oResult.nodeRef;
									me.onDelete([oResult], owner, {fullDelete:true}, fnDeleteComplete);
								}
							},
							failure:function (oResponse) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text:"Ошибка!"
										});
							}
						};
						YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					};
					// EXTEND DATAGRID HERE
					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:${showSearchBlock?string}
							}).setMessages(${messages});
				}

				YAHOO.util.Event.onDOMReady(init);
			})
					();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
	<div id="alf-filters">
		<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:staff-list"
						nodePattern="lecm-orgstr_element-full-name" drawEditors=false>
		</@orgTree.tree>
	</div>
</div>
