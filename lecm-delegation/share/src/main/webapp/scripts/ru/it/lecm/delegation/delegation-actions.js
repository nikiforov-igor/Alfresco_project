(function (){
	LogicECM.module.Delegation.Actions = {};
	LogicECM.module.Delegation.Actions.prototype = {

		/**
		 * ACTIONS WHICH ARE LOCAL TO THE DATAGRID COMPONENT
		*/

		/**
		* Edit Data Item pop-up
		*
		* @method onActionEdit
		* @param item {object} Object literal representing one data item
		*/
		onActionEdit: function DataGrid_onActionEdit(item) {
			var scope = this;

			// Intercept before dialog show
			var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
				Alfresco.util.populateHTML([ p_dialog.id + "-dialogTitle", this.msg("label.edit-row.title") ]);

			/**
			* No full-page edit view for v3.3
			*
			// Data Item Edit Page link button
			Alfresco.util.createYUIButton(p_dialog, "editDataItem", null, {
				type: "link",
				label: scope.msg("label.edit-row.edit-dataitem"),
				href: scope.getActionUrls(item).editMetadataUrl
			});
			*/
			};

			var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",{
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json"
			});

			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions({
				width: "34em",
				templateUrl: templateUrl,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: doBeforeDialogShow,
					scope: this
				},
				onSuccess: {
					fn: function DataGrid_onActionEdit_success(response) {
						// Reload the node's metadata
						Alfresco.util.Ajax.jsonPost( {
							url: Alfresco.constants.PROXY_URI + "slingshot/datalists/item/node/" + new Alfresco.util.NodeRef(item.nodeRef).uri,
							dataObj: this._buildDataGridParams(),
							successCallback: {
								fn: function DataGrid_onActionEdit_refreshSuccess(response) {
									// Fire "itemUpdated" event
									YAHOO.Bubbling.fire("dataItemUpdated", {
										item: response.json.item
									});
									// Display success message
									Alfresco.util.PopupManager.displayMessage( {
										text: this.msg("message.details.success")
									});
								},
								scope: this
							},
							failureCallback: {
								fn: function DataGrid_onActionEdit_refreshFailure(response) {
									Alfresco.util.PopupManager.displayMessage( {
										text: this.msg("message.details.failure")
									});
								},
								scope: this
							}
						});
					},
					scope: this
				},
				onFailure: {
					fn: function DataGrid_onActionEdit_failure(response) {
						Alfresco.util.PopupManager.displayMessage( {
							text: this.msg("message.details.failure")
						});
					},
					scope: this
				}
			}).show();
		},

		onActionDelete: function () {
			Alfresco.util.PopupManager.displayMessage({
				text: "onActionDelete"
			});
		},

		onActionRevoke: function () {
			Alfresco.util.PopupManager.displayMessage({
				text: "onActionRevoke"
			});
		},

		onActionPropogate: function () {
			Alfresco.util.PopupManager.displayMessage({
				text: "onActionPropogate"
			});
		}
	};
})();
