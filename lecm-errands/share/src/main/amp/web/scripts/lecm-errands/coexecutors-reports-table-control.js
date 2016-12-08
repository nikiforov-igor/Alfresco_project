/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */

LogicECM.errands = LogicECM.errands || {};

(function () {
	LogicECM.errands.CoexecutorsReportsTS = function (htmlId) {
		LogicECM.errands.CoexecutorsReportsTS.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.errands.CoexecutorsReportsTS, LogicECM.module.DocumentTable);

	YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsTS.prototype, {
		createDataGrid: function() {
			var actions = [];
			var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
			Alfresco.util.Ajax.jsonRequest(
				{
					method: Alfresco.util.Ajax.GET,
					url: Alfresco.constants.PROXY_URI + "lecm/errands/api/getCurrentEmployeeRoles?errandNodeRef=" + encodeURIComponent(this.options.documentNodeRef),
					successCallback: {
						fn: function (response) {
							var me = response.config.scope;
							var roles = response.json;
							if(roles){
								if (!me.options.disabled) {
									if (roles.isExecutor) {
										actions.push({
											type: actionType,
											id: "onActionAcceptCoexecutorReport",
											permission: "edit",
											label: me.msg("actions.coexecutor.report.accept"),
											evaluator: me.showActionsEvaluator
										});
										actions.push({
											type: actionType,
											id: "onActionDeclineCoexecutorReport",
											permission: "edit",
											label: me.msg("actions.coexecutor.report.decline"),
											evaluator: me.showActionsEvaluator
										});
									}
									if (roles.isCoexecutor) {
										actions.push({
											type: actionType,
											id: "onActionEditCoexecutorReport",
											permission: "edit",
											label: me.msg("actions.edit"),
											evaluator: me.editActionEvaluator
										});
									}
								}
							}
							var currentUser;
							Alfresco.util.Ajax.jsonGet({
								url:Alfresco.constants.PROXY_URI +"/lecm/orgstructure/api/getCurrentEmployee",
								successCallback: {
									fn: function (response) {
										var me = response.config.scope;
										if(response && response.json.nodeRef){
											currentUser = response.json.nodeRef;
											me.realCreateDatagrid(actions,currentUser);
										}
									}
								},
								failureCallback: {
									fn:function (response) {
										var me = response.config.scope;
										Alfresco.util.PopupManager.displayMessage(
											{
												text:me.msg("message.details.failure")
											});
									}
								},
								scope:me
							});

						}
					},
					failureCallback: {
						fn:function (response) {
							var me = response.config.scope;
							Alfresco.util.PopupManager.displayMessage(
								{
									text:me.msg("message.details.failure")
								});
						}
					},
					scope: this
				});

		},

		realCreateDatagrid: function(actions,currentUser) {
			if (this.tableData != null && this.tableData.rowType != null) {

				var datagrid = new LogicECM.errands.CoexecutorsReportsDatagrid(this.options.containerId).setOptions({
					usePagination: true,
					showExtendSearchBlock: false,
					formMode: this.options.mode,
					actions: actions,
					currentUser: currentUser,
					datagridMeta: {
						itemType: this.tableData.rowType,
						datagridFormId: this.options.datagridFormId,
						createFormId: "",
						nodeRef: this.tableData.nodeRef,
						parent: this.tableData.nodeRef,
						actionsConfig: {
							fullDelete: true
						},
						sort: "lecm-document:indexTableRow",
						useChildQuery: false,
						searchConfig: {
							filter: 'NOT @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" AND NOT (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND NOT @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"'+currentUser+'")'
						}
					},
					bubblingLabel: this.options.bubblingLabel,
					showActionColumn: this.options.showActions,
					showOtherActionColumn: true,
					showCheckboxColumn: false,
					attributeForShow: this.options.attributeForShow,
					pageSize: this.tableData.pageSize != null && this.tableData.pageSize > 0 ? this.tableData.pageSize : 10,
					useCookieForSort: false,
					overrideSortingWith: this.options.isTableSortable,
					refreshAfterCreate: this.options.refreshAfterCreate,
					editFormTitleMsg: this.options.editFormTitleMsg,
					createFormTitleMsg: this.options.createFormTitleMsg,
					viewFormTitleMsg: this.options.viewFormTitleMsg,
					expandable: this.options.expandable,
					expandDataSource: this.options.expandDataSource
				}).setMessages(this.options.messages);
			}

			if (this.tableData != null) {
				datagrid.tableDataNodeRef = this.tableData.nodeRef;
			}
			datagrid.deleteMessageFunction = this.options.deleteMessageFunction;
			datagrid.draw();

			YAHOO.util.Event.on(this.id + "-cntrl-show-declined", "change", function() {
				var filter = 'NOT @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" AND NOT (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND NOT @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"'+currentUser+'")';
				if (this.checked) {
					filter = '@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"DECLINE" OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ACCEPT" OR (@lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"PROJECT" AND @lecm\\-errands\\-ts\\:coexecutor\\-assoc\\-ref:"'+currentUser+'")' +
						'OR @lecm\\-errands\\-ts\\:coexecutor\\-report\\-status:"ONCONTROL"';

				}
				var datagridMeta = datagrid.datagridMeta;
				datagridMeta.searchConfig.filter = filter;
				datagrid.search.performSearch(datagridMeta);
			});

			this.datagrid = datagrid;
		},

		showActionsEvaluator: function(rowData) {
			var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];

			return status != null && status.value == "ONCONTROL";
		},
		editActionEvaluator: function(rowData){
			var status = rowData.itemData["prop_lecm-errands-ts_coexecutor-report-status"];
			var coexecutor = rowData.itemData["assoc_lecm-errands-ts_coexecutor-assoc"];
			return status != null && status.value == "PROJECT" && coexecutor.value == this.options.currentUser;
		}
	}, true);
})();

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector,
		Bubbling = YAHOO.Bubbling;

	LogicECM.errands.CoexecutorsReportsDatagrid = function (htmlId) {
		return LogicECM.errands.CoexecutorsReportsDatagrid.superclass.constructor.call(this, htmlId);
	};

	YAHOO.lang.extend(LogicECM.errands.CoexecutorsReportsDatagrid, LogicECM.module.DocumentTableDataGrid);

	YAHOO.lang.augmentObject(LogicECM.errands.CoexecutorsReportsDatagrid.prototype, {

		onActionAcceptCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
			var nodeRef = arguments[0].nodeRef;
			if (nodeRef != null) {
				Alfresco.util.Ajax.jsonRequest(
					{
						method: Alfresco.util.Ajax.GET,
						url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/accept?nodeRef=" + nodeRef,
						successCallback: {
							fn: function (response) {
								var me = response.config.scope;
								if (response.json.success) {
									me._itemUpdate(nodeRef);
								} else {
									Alfresco.util.PopupManager.displayMessage(
										{
											text:me.msg("message.details.failure")
										});
								}
							}
						},
						failureCallback: {
							fn:function (response) {
								var me = response.config.scope;
								Alfresco.util.PopupManager.displayMessage(
									{
										text:me.msg("message.details.failure")
									});
							}
						},
						scope: this
					});
			}
		},

		onActionDeclineCoexecutorReport: function (me, asset, owner, actionsConfig, confirmFunction) {
			var nodeRef = arguments[0].nodeRef;
			if (nodeRef != null) {
				Alfresco.util.Ajax.jsonRequest(
					{
						method: Alfresco.util.Ajax.GET,
						url: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/decline?nodeRef=" + nodeRef,
						successCallback: {
							fn: function (response) {
								var me = response.config.scope;
								if (response.json.success) {
									me._itemUpdate(nodeRef);
								} else {
									Alfresco.util.PopupManager.displayMessage(
										{
											text:me.msg("message.details.failure")
										});
								}
							}
						},
						failureCallback: {
							fn:function (response) {
								var me = response.config.scope;
								Alfresco.util.PopupManager.displayMessage(
									{
										text:me.msg("message.details.failure")
									});
							}
						},
						scope: this
					});
			}
		},
		onActionEditCoexecutorReport: function DataGrid_onActionEdit(item) {
			if (this.editDialogOpening) {
				return;
			}
			this.editDialogOpening = true;
			var me = this;

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json",
				showCancelButton: true
			};
			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails" + Alfresco.util.generateDomId());
			editDetails.setOptions({
				width: this.options.editFormWidth,
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				actionUrl: Alfresco.constants.PROXY_URI + "lecm/errands/coexecutorReport/route?nodeRef=" + item.nodeRef,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (p_form, p_dialog) {
						var contId = p_dialog.id + "-form-container";
						if (item.type && item.type != "") {
							Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
						}
						p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
						this.editDialogOpening = false;
						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
						//setup form
						var form = Dom.get(p_form.formId);
						Dom.addClass(form,"errands-coexecutor-report-form");
						var connectionControl = Selector.query(".control.association-search",form,true);
						var button = Selector.query(".container .buttons-div input",connectionControl, true);
						button.value = Alfresco.util.message("button.create-connection");
						var visibleValueDiv = Selector.query(".container .value-div .control-selected-values.mandatory-highlightable",connectionControl,true);
						Dom.setStyle(visibleValueDiv,"display","none");
						Event.addListener(visibleValueDiv,'DOMSubtreeModified',function(){
							if(!visibleValueDiv.hasChildNodes()){
								Dom.setStyle(visibleValueDiv,"display","none");
							}else{
								Dom.setStyle(visibleValueDiv,"display","block");
							}
						});
						var formButtons = Dom.get(p_form.formId + "-buttons");
						var saveReportElement = Dom.get(p_form.formId + "-submit-button");
						saveReportElement.innerHTML = Alfresco.util.message("button.save-report");

						var routeReportButton = Dom.get(p_dialog.id + "_route-report-button");
						Event.addListener(routeReportButton,"click",function(){
							var routeReport = Selector.query('input[name="prop_lecm-errands-ts_coexecutor-report-is-route"]', form, true);
							routeReport.value = true;
							saveReportElement.click();
						});


					},
					scope: this
				},
				onSuccess: {
					fn: function (response) {
						Bubbling.fire("datagridRefresh", {
							bubblingLabel: me.options.bubblingLabel
						});
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.details.success")
						});
						this.editDialogOpening = false;
						editDetails.hide();
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.details.failure")
						});
						this.editDialogOpening = false;
						editDetails.hide();
					},
					scope: this
				}
			}).show();

		}
		}, true)

})();
