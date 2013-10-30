/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function()
{
	var Dom = YAHOO.util.Dom;


	LogicECM.module.DocumentTable = function (fieldHtmlId)
	{
		LogicECM.module.DocumentTable.superclass.constructor.call(this, "LogicECM.module.DocumentTable", fieldHtmlId, [ "container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.DocumentTable, Alfresco.component.Base,
		{
			options: {
				currentValue: null,
				bubblingLabel: "custom",
				toolbarId: null,
				containerId: null,
				datagridFormId: "datagrid",
				attributeForShow: "",
				disabled: null,
				messages: null,
				mode: null,
				datagridHeight: null,
				repeating: null
			},

			tableData: null,

			onReady: function(){
				this.loadTableData();
			},

			loadTableData: function() {
				if (this.options.currentValue != null) {
					var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getData?nodeRef=" + encodeURIComponent(this.options.currentValue);
					Alfresco.util.Ajax.jsonGet(
						{
							url: sUrl,
							successCallback: {
								fn: function (response) {
									this.tableData = response.json;

									this.createToolbar();
									this.createDataGrid();
								},
								scope: this
							},
							failureCallback: {
								fn: function (oResponse) {
//									var response = YAHOO.lang.JSON.parse(oResponse.responseText);
//									this.widgets.dataTable.set("MSG_ERROR", response.message);
//									this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
								},
								scope: this
							}
						});
				}
			},

			createToolbar: function() {
				if (this.tableData != null && this.tableData.rowType != null) {
					new LogicECM.module.Base.Toolbar(null, this.options.toolbarId).setMessages(this.options.messages).setOptions({
						bubblingLabel: this.options.bubblingLabel,
						itemType: this.tableData.rowType,
						destination: this.tableData.nodeRef,
						newRowButtonType: this.options.disabled ? "inActive" : "defaultActive"
					});
				}
			},

			createDataGrid: function() {
				if (this.tableData != null && this.tableData.rowType != null) {
					var actions = [];
					var actionType = "datagrid-action-link-" + this.options.bubblingLabel;
					if (!this.options.disabled) {
						actions.push({
							type: actionType,
							id: "onActionEdit",
							permission: "edit",
							label: this.msg("actions.edit")
						});
						actions.push({
							type: actionType,
							id: "onActionDelete",
							permission: "delete",
							label: this.msg("actions.delete-row")
						});
					}

					var datagrid = new LogicECM.module.DocumentTableDataGrid(this.options.containerId).setOptions({
						usePagination: true,
						showExtendSearchBlock: false,
						formMode: this.options.mode,
						actions: actions,
						otherActions: [
                            {
	                            type: actionType,
								id: "onMoveTableRowUp",
								permission: "edit",
								label: this.msg("actions.tableRowUp")
							},
			                {
				                type: actionType,
								id: "onMoveTableRowDown",
								permission: "edit",
								label: this.msg("action.tableRowDown")
							},
			                {
				                type: actionType,
								id: "onAddRow",
								permission: "edit",
								label: this.msg("action.addRow")
							}
						],
						datagridMeta: {
							itemType: this.tableData.rowType,
							datagridFormId: this.options.datagridFormId,
							createFormId: "",
							nodeRef: this.tableData.nodeRef,
							actionsConfig: {
								fullDelete: true
					        },
							sort: "",
							searchConfig: null
						},
						bubblingLabel: this.options.bubblingLabel,
						height: this.options.datagridHeight,
						showActionColumn: true,
						showOtherActionColumn: true,
						showCheckboxColumn: false,
						attributeForShow: this.options.attributeForShow,
						repeating: this.options.repeating
					}).setMessages(this.options.messages);
				}

				datagrid.draw();
			}
		});
})();

LogicECM.module.DocumentTableDataGrid= LogicECM.module.DocumentTableDataGrid  || {};

(function () {

	LogicECM.module.DocumentTableDataGrid = function (htmlId) {
		return LogicECM.module.DocumentTableDataGrid.superclass.constructor.call(this, htmlId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.DocumentTableDataGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.DocumentTableDataGrid.prototype, {
		addFooter: function() {
//			if (this.documentRef != null && this.itemType != null && this.assocType != null) {
//				var sUrl = sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/tables/api/getTotalRows" +
//					"?documentNodeRef=" + encodeURIComponent(this.documentRef) +
//					"&tableDataType=" + encodeURIComponent(this.itemType) +
//					"&tableDataAssocType=" + encodeURIComponent(this.assocType);
//				Alfresco.util.Ajax.jsonGet(
//					{
//						url: sUrl,
//						successCallback: {
//							fn: function (response) {
//								var oResults = response.json;
//								if (oResults != null && oResults.length > 0) {
//									var row = oResults[0];
//									var item = {
//										itemData: {},
//										nodeRef: row.nodeRef,
//										type: "total"
//									};
//
//									var datagridColumns = this.datagridColumns;
//									if (datagridColumns != null) {
//										for (var i = 0; i < datagridColumns.length; i++) {
//											var field = datagridColumns[i].name;
//											var totalFields = Object.keys(row.itemData);
//											for (var j = 0; j < totalFields.length; j++) {
//												var totalField = totalFields[j];
//												if (totalField.indexOf(field) == 0) {
//													var value = row.itemData[totalField];
//													item.itemData[datagridColumns[i].formsName] = {
//														value: value,
//														displayValue: value
//													};
//												}
//											}
//										}
//									}
//
//									this.widgets.dataTable.addRow(item);
//								}
//							},
//							scope: this
//						},
//						failureCallback: {
//							fn: function (oResponse) {
//
//							},
//							scope: this
//						}
//					});
//			}
		},

		getRowFormater: function(elTr, oRecord) {
			if (oRecord.getData("type") == "total") {
				YAHOO.util.Dom.addClass(elTr, 'total-row');
			}
			return true;
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
					var datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (var i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

							var columnContent = "";
							switch (datalistColumn.dataType.toLowerCase()) { //  меняем отрисовку для конкретных колонок
								case "cm:content":
									var fileIcon = Alfresco.util.getFileIcon(data.displayValue, "cm:content", 16);
									var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"'/>";

									columnContent = "<a href=\'" + Alfresco.constants.URL_PAGECONTEXT+'document-attachment?nodeRef='+ data.value +"\'\">" + fileIconHtml + data.displayValue + "</a>";
									break;
								default:
									break;
							}
							if (columnContent != "") {
								html += columnContent;

								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
			}
			return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		}
	}, true)

})();


