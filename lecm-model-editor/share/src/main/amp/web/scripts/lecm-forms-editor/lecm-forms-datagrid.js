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
					if (oColumn.field == "prop_lecm-forms-editor_form-template" && grid.formTemplates != null) {
						for (var i = 0; i < grid.formTemplates.length; i++) {
							if (oData.value == grid.formTemplates[i].template) {
								html += grid.formTemplates[i].localName;
							}
						}
					}
				} else if (oColumn.field == "prop_lecm-forms-editor_form-type" && grid.formTypes != null) {
					var evaluator = oRecord.getData("itemData")["prop_lecm-forms-editor_form-evaluator"];
					var propFormId = oRecord.getData("itemData")["prop_lecm-forms-editor_form-id"];
					if (evaluator != null && propFormId != null) {
						for (i = 0; i < grid.formTypes.length; i++) {
							var formId = grid.formTypes[i].id != null ?grid.formTypes[i].id : "";
							if (evaluator.value == grid.formTypes[i].evaluatorType && propFormId.value == formId) {
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
						if (oResults != null) {
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
						if (oResults != null) {
							this.formTypes = oResults;
						}
					},
					scope: this
				}
			});
		}
	}, true);
})();
