/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.ColumnsGrid
 */
(function () {
	LogicECM.module.ReportsEditor.ColumnsGrid = function (containerId) {
		LogicECM.module.ReportsEditor.ColumnsGrid.superclass.constructor.call(this, containerId);
		this.activeSourceColumns = [];

		YAHOO.Bubbling.on("copyColumnToReportSource", this.onCopyLock, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.ReportsEditor.ColumnsGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.ColumnsGrid.prototype, {

		activeSourceColumns: [],
		mainDataGridLabel: null,
		doubleClickLock: false,

		onActionAdd: function (item) {
			if (!this.doubleClickLock) {
				this.doubleClickLock = true;
			// добавляем столбец в набор данных (копируем в отчет)
			YAHOO.Bubbling.fire("copyColumnToReportSource", {
				sourceId: this.datagridMeta.nodeRef,
					columnId: item.nodeRef,
					bubblingLabel: this.mainDataGridLabel
			});
			}
		},

		onCopyLock: function() {
			this.doubleClickLock = false;
		},

		onActionDelete: function (p_items, owner, actionsConfig) {
			this.onDelete(p_items, owner, actionsConfig, function () {YAHOO.Bubbling.fire("updateReportSourceColumns");}, null);
		}
	}, true);
})();