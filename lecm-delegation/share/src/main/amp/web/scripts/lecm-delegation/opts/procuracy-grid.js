if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Procuracy = LogicECM.module.Delegation.Procuracy || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.Procuracy.Grid = function (containerId) {
		return LogicECM.module.Delegation.Procuracy.Grid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.Procuracy.Grid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.Procuracy.Grid.prototype, {

		onActionEdit:function DataGrid_onActionEdit(item) {
			var me = this;
			// Intercept before dialog show
			var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
				Alfresco.util.populateHTML(
					[ p_dialog.id + "-form-container_h", this.msg("label.edit-row.title") ]
				);
			};

			var templateUrl = "lecm/components/form"
						+ "?itemKind={itemKind}"
						+ "&itemId={itemId}"
						+ "&mode={mode}"
						+ "&submitType={submitType}"
						+ "&showCancelButton=true"
						+ "&ignoreNodes={ignoreNodes}"
						+ "&showCaption=false";
			//если мы задали ИД-шник формы, то передаем и его
			if (this.options.editForm) {
				templateUrl += "&formId=" + this.options.editForm;
			}

			var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json",
				ignoreNodes: LogicECM.module.Delegation.Const.employee
			});

			// Using Forms Service, so always create new instance
			//var procuracyCanTransferRights = false;
			var procuracyRef = item.nodeRef;
			var delegationOptsRef = this.datagridMeta.nodeRef;
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions({
				width: "50em",
				templateUrl: url,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: doBeforeDialogShow,
					scope: this
				},
				doBeforeFormSubmit: {
					fn: function () {
						//procuracyCanTransferRights = YAHOO.util.Dom.get(editDetails.id + "_prop_lecm-d8n_procuracy-can-transfer-rights").checked;
					},
					scope: this
				},
				onSuccess: {
					fn: function DataGrid_onActionEdit_success(response) {
//						if (procuracyCanTransferRights) {
//							Alfresco.util.Ajax.request({
//								method: "POST",
//								url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/procuracy/transfer/rights",
//								dataObj: {
//									"procuracyRef": procuracyRef,
//									"delegationOptsRef": delegationOptsRef
//								},
//								requestContentType: "application/json",
//								responseContentType: "application/json",
//								successCallback: {
//									fn: function (response) {
//										YAHOO.Bubbling.fire("datagridRefresh", {
//											bubblingLabel: me.options.bubblingLabel
//										});
//									},
//									scope: this
//								},
//								failureCallback: {
//									fn: function () {
//										Alfresco.util.PopupManager.displayMessage({
//											text: 'не удалось установить флаг "передавать права руководителя"'
//										});
//									}
//								}
//							});
//						} else {
							// Reload the node's metadata
							YAHOO.Bubbling.fire("datagridRefresh", {
								bubblingLabel:me.options.bubblingLabel
							});
						//}

						Alfresco.util.PopupManager.displayMessage({
							text:this.msg("message.details.success")
						});
					},
					scope: this
				},
				onFailure:{
					fn:function DataGrid_onActionEdit_failure(response) {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:this.msg("message.details.failure")
							});
					},
					scope:this
				}
			}).show();
		},

//		onActionTransferRights: function (p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {
//			var dataObj = {
//				"procuracyRef": p_items.nodeRef,
//				"delegationOptsRef": this.datagridMeta.nodeRef
//			};
//			var scope = this;
//			Alfresco.util.Ajax.request({
//				method: "POST",
//				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/procuracy/transfer/rights",
//				dataObj: dataObj,
//				requestContentType: "application/json",
//				responseContentType: "application/json",
//				successCallback: {
//					fn: function (response) {
//						YAHOO.Bubbling.fire("datagridRefresh", {
//							bubblingLabel: scope.options.bubblingLabel
//						});
//					},
//					scope: this
//				},
//				failureCallback: {
//					fn: function () {
//						Alfresco.util.PopupManager.displayMessage({
//							text: 'не удалось установить флаг "передавать права руководителя"'
//						});
//					}
//				}
//			});
//		},

		onDelete: function (p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {
			var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
			var nodeRefs = [];
			for (var i = 0; i < items.length; ++i) {
				nodeRefs.push ({"nodeRef": items[i].nodeRef});
			}
			var scope = this;
			Alfresco.util.Ajax.request ({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/del/procuracies",
				dataObj: nodeRefs,
				requestContentType: "application/json",
				responseContentType: "application/json",
//				successMessage: "ololo!",
				successCallback: {
					fn: function (response) {
						YAHOO.Bubbling.fire("datagridRefresh", {
							bubblingLabel: scope.options.bubblingLabel
						});
					},
					scope: this
				},
				failureMessage: Alfresco.util.message("msg.delete_procuracy.failed")
			});
		}
	}, true);

})();
