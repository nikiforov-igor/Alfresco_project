if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Procuracy = LogicECM.module.Delegation.Procuracy || {};

(function () {

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
		/**
		 * Edit Data Item pop-up
		 *
		 * @method onActionEdit
		 * @param rowData {object} Object literal representing one data item
		 */
		//		onActionEdit:function DataGrid_onActionEdit(rowData) {
		//			Alfresco.util.PopupManager.displayPrompt ({
		//			   title: "Редактируем существующую доверенность",
		//			   text: YAHOO.lang.JSON.stringify(rowData)
		//			});
		//		},

		//		onActionCreate: function DataGrid_onActionCreate(rowData) {
		//			Alfresco.util.PopupManager.displayPrompt ({
		//			   title: "Создаем новую доверенность",
		//			   text: YAHOO.lang.JSON.stringify(rowData)
		//			});
		//			// Intercept before dialog show
		//			var doBeforeDialogShow = function (p_form, p_dialog) {
		//				Alfresco.util.populateHTML(
		//					[ p_dialog.id + "-form-container_h", this.msg("label.edit-row.title") ]
		//				);
		//			};
		//		},

		//		canCreateProcuracy: function DataGrid_canCreateProcuracy () {
		//			var scope = this;
		//			return function (rowData) {
		//				var nodeRef = Alfresco.util.NodeRef (rowData.nodeRef);
		//				return typeof nodeRef.id == "undefined";
		//			}
		//		},
		onActionEdit:function DataGrid_onActionEdit(item) {
			var scope = this;
			// Intercept before dialog show
			var doBeforeDialogShow = function (p_form, p_dialog) {
				Alfresco.util.populateHTML ([ p_dialog.id + "-form-container_h", this.msg("label.edit-row.title") ]);
			};

			var templateUrl = "components/form"
							+ "?itemKind={itemKind}"
							+ "&itemId={itemId}"
							+ "&formId={formId}"
							+ "&mode={mode}"
							+ "&submitType={submitType}"
							+ "&showCancelButton=true";

			var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
				itemKind: "node",
				itemId: item.nodeRef,
				formId: "editProcuracy",
 				mode: "edit",
				submitType: "json"
			});

			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions ({
				width: "50em",
				templateUrl: url,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: doBeforeDialogShow,
					scope: this
				},
				onSuccess: {
					fn: function (response) {
						// Reload the node's metadata
						Bubbling.fire("datagridRefresh", {
							bubblingLabel: scope.options.bubblingLabel
						});
					},
					scope: this
				},
				onFailure: {
					fn: function (response) {
						Alfresco.util.PopupManager.displayMessage ({
							text: this.msg ("message.details.failure")
						});
					},
					scope: this
				}
			}).show ();
		},

		canEditProcuracy: function DataFrid_canEditProcuracy () {
			var scope = this;
			return function (rowData) {
				var nodeRef = Alfresco.util.NodeRef (rowData.nodeRef);
				return typeof nodeRef.id != "undefined";
			}
		}

	}, true);

})();
