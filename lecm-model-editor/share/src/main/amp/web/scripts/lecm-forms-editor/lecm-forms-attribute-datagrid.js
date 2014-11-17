/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function () {
	LogicECM.module.FormsEditor.AttributesDatagrid = function (htmlId) {
		return LogicECM.module.FormsEditor.AttributesDatagrid.superclass.constructor.call(this, htmlId);
	};

	YAHOO.extend(LogicECM.module.FormsEditor.AttributesDatagrid, LogicECM.module.Base.DataGrid);


	YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.AttributesDatagrid.prototype,
		{
			getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
				var html = "";
				if (!oRecord) {
					oRecord = this.getRecord(elCell);
				}
				if (!oColumn) {
					oColumn = this.getColumn(elCell.parentNode.cellIndex);
				}

				if (oRecord && oColumn) {
					if (!oData) {
						oData = oRecord.getData("itemData")[oColumn.field];
					}

					if (oData) {
						if (oColumn.field == "prop_lecm-forms-editor_attr-control") {
							if (oData.value != null && oData.value.length > 0) {
								var json = JSON.parse(oData.value);
								if (json != null && json.displayName != null) {
									html += json.displayName;
								}
							}
						}
					}
				}
				return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
			}
		}, true);
})();