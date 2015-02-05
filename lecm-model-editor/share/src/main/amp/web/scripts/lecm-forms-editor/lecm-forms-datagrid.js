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

	LogicECM.module.FormsEditor.DataGrid = function (containerId) {
		return LogicECM.module.FormsEditor.DataGrid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.FormsEditor.DataGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.DataGrid.prototype, {
		formTemplates: null,
		formTypes: null,

		draw: function()  {
			this.loadConfigs();
			this.datagridMeta = this.options.datagridMeta;
			this.deferredListPopulation.fulfil("onGridTypeChanged");
			this.onReady();
		},

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
					if (oColumn.field == "prop_lecm-forms-editor_form-template" && grid.formTemplates) {
						for (var i = 0; i < grid.formTemplates.length; i++) {
							if (oData.value == grid.formTemplates[i].template) {
								html += grid.formTemplates[i].localName;
							}
						}
					}
				} else if (oColumn.field == "prop_lecm-forms-editor_form-type" && grid.formTypes) {
					var evaluator = oRecord.getData("itemData")["prop_lecm-forms-editor_form-evaluator"];
					var propId = oRecord.getData("itemData")["prop_lecm-forms-editor_id"];
					if (evaluator && propId) {
						for (i = 0; i < grid.formTypes.length; i++) {
							var id = grid.formTypes[i].id ? grid.formTypes[i].id : "";
							if (evaluator.value == grid.formTypes[i].evaluatorType && propId.value == id) {
								html += grid.formTypes[i].localName;
							}
						}
					}
				}
			}
			return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		},

		loadConfigs: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getFormLayouts",
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						if (oResults) {
							this.formTemplates = oResults;
						}
					},
					scope: this
				}
			});

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getFormTypes",
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults) {
							this.formTypes = oResults;
						}
					},
					scope: this
				}
			});
		}
	}, true);
})();
