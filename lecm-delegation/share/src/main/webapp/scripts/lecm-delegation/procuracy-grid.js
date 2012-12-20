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
		onActionEdit:function DataGrid_onActionEdit(rowData) {
			Alfresco.util.PopupManager.displayPrompt ({
			   title: "Редактируем существующую доверенность",
			   text: YAHOO.lang.JSON.stringify(rowData)
			});
		},

		onActionCreate: function DataGrid_onActionCreate(rowData) {
			Alfresco.util.PopupManager.displayPrompt ({
			   title: "Создаем новую доверенность",
			   text: YAHOO.lang.JSON.stringify(rowData)
			});
		},

		canCreateProcuracy: function DataGrid_canCreateProcuracy () {
			var scope = this;
			return function (rowData) {
				var nodeRef = Alfresco.util.NodeRef (rowData.nodeRef);
				return typeof nodeRef.id == "undefined";
			}
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
