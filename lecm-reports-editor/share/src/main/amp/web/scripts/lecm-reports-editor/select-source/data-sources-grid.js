/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.SourcesGrid
 */
(function () {
	LogicECM.module.ReportsEditor.SourcesGrid = function(containerId) {
		LogicECM.module.ReportsEditor.SourcesGrid.superclass.constructor.call(this, containerId);
		return this;
	};
	
	YAHOO.lang.extend(LogicECM.module.ReportsEditor.SourcesGrid, LogicECM.module.Base.DataGrid);
	
	YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.SourcesGrid.prototype, {
		mainDataGridLabel: null,
		onActionSelectSource: function(item) {
			//выбираем набор данных - копируем его в отчет
			YAHOO.Bubbling.fire("copySourceToReport", {
				dataSourceId: item.nodeRef,
				bubblingLabel: this.mainDataGridLabel
			});
		},
		onRenderEvent: function() {
			YAHOO.Bubbling.fire("GridRendered");
			if (this._hasEventInterest(this.options.bubblingLabel)) {
				for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
					this.afterDataGridUpdate[i].call(this);
				}
				this.afterDataGridUpdate = [];
				
				var nodeRef = "NOT_LOAD";
				var selectItem = this.widgets.dataTable.getSelectedTrEls()[0];
				if (selectItem != undefined) {
					var numItems = this.widgets.dataTable.getTrIndex(selectItem);
					nodeRef = this.widgets.dataTable.getRecordSet().getRecord(numItems).getData().nodeRef;
				}
				
				YAHOO.Bubbling.fire("activeGridChanged",
						{
							datagridMeta: {
								itemType: "lecm-rpeditor:reportDataColumn",
                                useChildQuery:true,
								nodeRef: nodeRef,
								sort: "lecm-rpeditor:dataColumnCode|true"
							},
							bubblingLabel: this.options.bubblingLabel.replace("sourcesList", "sourceColumns")
						});
			}
		},
		setupDataTable: function() {
			var columnDefinitions = this.getDataTableColumnDefinitions();
			var me = this;
			if (!this.widgets.dataTable) {
				this._setupPaginatior();
				this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
				this.widgets.dataTable.subscribe("beforeRenderEvent", function() {
					me.beforeRenderFunction();
				},
						me.widgets.dataTable, true);
				
				this.widgets.dataTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
				this.widgets.dataTable.subscribe("unselectAllRowsEvent", this.onEventUnselectAllRows, this, true);
			}
			this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
				showExtendSearchBlock: false
			});
			
			var searchConfig = this.datagridMeta.searchConfig;
			var sort = this.datagridMeta.sort;
			var searchShowInactive = false;
			
			if (!searchConfig) {
				searchConfig = {};
			}
			if (searchConfig.formData) {
				searchConfig.formData.datatype = this.datagridMeta.itemType;
			} else {
				searchConfig.formData = {
					datatype: this.datagridMeta.itemType
				};
			}
			//при первом поиске сохраняем настройки
			if (this.initialSearchConfig == null) {
				this.initialSearchConfig = {fullTextSearch: null};
				this.initialSearchConfig = YAHOO.lang.merge(searchConfig, this.initialSearchConfig);
			}
			
			this.search.performSearch({
				parent: this.datagridMeta.nodeRef,
				itemType: this.datagridMeta.itemType,
				searchConfig: searchConfig,
				searchShowInactive: searchShowInactive,
				sort: sort
			});
		},
		onEventSelectRow: function DataGrid_onEventSelectRow(oArgs) {
			this.widgets.dataTable.onEventSelectRow(oArgs);
			// Номер строки в таблице
			var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
			// Выбранный элемент
			var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
			// Отрисовка датагрида для Столбцов Набора данных
			YAHOO.Bubbling.fire("activeGridChanged",
					{
						datagridMeta: {
							itemType: "lecm-rpeditor:reportDataColumn",
							nodeRef: selectItem.getData().nodeRef,
							sort: "lecm-rpeditor:dataColumnCode|true"
						},
						bubblingLabel: this.options.bubblingLabel.replace("sourcesList", "sourceColumns")
					});
		},
		onEventUnselectAllRows: function DataGrid_onEventSelectRow(oArgs) {
			var recordSet = this.widgets.dataTable.getRecordSet();
			for (var i = 0, j = recordSet.getLength(); i < j; i++) {
				if (recordSet.getRecord(i)) {
					var record = recordSet.getRecord(i);
					record.getData().itemData.selected = false;
				}
			}
		},
		onDataItemCreated: function(layer, args) {
			var obj = args[1];
			if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.nodeRef !== null)) {
				var nodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
				// Reload the node's metadata
				Alfresco.util.Ajax.jsonPost(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
							dataObj: this._buildDataGridParams(),
							successCallback: {
								fn: function DataGrid_onDataItemCreated_refreshSuccess(response) {
									var item = response.json.item;
									var fnAfterUpdate = function DataGrid_onDataItemCreated_refreshSuccess_fnAfterUpdate() {
										var recordFound = this._findRecordByParameter(item.nodeRef, "nodeRef");
										if (recordFound !== null) {
											if (obj.oldNodeRef) {
												// очищаем выделение
												this.widgets.dataTable.unselectAllRows();
												
												//выделяем новую строку
												this.widgets.dataTable.selectRow(recordFound);
												
												// удаляем из таблицы предыдущее значение (оно более никогда не будет использовано)
												var oldRecord = this._findRecordByParameter(obj.oldNodeRef, "nodeRef");
												if (oldRecord !== null) {
													this.widgets.dataTable.deleteRow(oldRecord);
												}
											}
											
											recordFound.getData().itemData.selected = true;
											
											// помечаем запись, которую скопировали как выбранную (чтобы скрыть кнопку выбора)
											if (obj.copiedRef) {
												var copiedRecord = this._findRecordByParameter(obj.copiedRef, "nodeRef");
												if (copiedRecord !== null) {
													copiedRecord.getData().itemData.selected = true;
												}
											}
											
											//фиксируем набор данных в Редакторе
											YAHOO.Bubbling.fire("selectDataSource", {
												dataSourceId: item.nodeRef
											});
											
										}
									};
									this.afterDataGridUpdate.push(fnAfterUpdate);
									if (obj.copiedRef) {
										var record = this._findRecordByParameter(obj.copiedRef, "nodeRef");
										if (record) {
											this.widgets.dataTable.addRow(item, record.getCount() + 1);
										}
									} else {
										this.widgets.dataTable.addRow(item);
									}
								},
								scope: this
							},
							failureCallback: {
								fn: function DataGrid_onDataItemCreated_refreshFailure(response) {
									alert(response.json.message);
									Alfresco.util.PopupManager.displayMessage(
											{
												text: this.msg("message.create.refresh.failure")
											});
								},
								scope: this
							}
						});
			}
		}
	}, true);
})();