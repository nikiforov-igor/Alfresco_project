if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.Years = LogicECM.module.WCalendar.Calendar.Years || {};

(function() {

	var attributeForShow = "cm:name";

	LogicECM.module.WCalendar.Calendar.Years.DataGrid = function(containerId) {
		return LogicECM.module.WCalendar.Calendar.Years.DataGrid.superclass.constructor.call(this, containerId);
	};


	YAHOO.lang.extend(LogicECM.module.WCalendar.Calendar.Years.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.WCalendar.Calendar.Years.DataGrid.prototype, {
		getCellFormatter: function DataGrid_getCellFormatter() {
			var scope = this;

			return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
				var html = "";
				var content;

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
						var datalistColumn = scope.datagridColumns[oColumn.key];
						if (datalistColumn) {
							oData = YAHOO.lang.isArray(oData) ? oData : [oData];
							var plane = true;

							for (var i = 0, ii = oData.length, data; i < ii; i++) {
								data = oData[i];

								switch (datalistColumn.dataType.toLowerCase()) {
									case "datetime":
										content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "yyyy");
										html += content;
										break;

									case "date":
										content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "yyyy");
										html += content;
										break;

									case "text":
										content = Alfresco.util.encodeHTML(data.displayValue);
										html += Alfresco.util.activateLinks(content);

										break;

									default:
										if (datalistColumn.type == "association") {
											html += '<a><img src="'
													+ Alfresco.constants.URL_RESCONTEXT
													+ 'components/images/filetypes/'
													+ Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16)
													+ '" width="16" alt="'
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '" title="'
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '" /> '
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '</a>';
										} else {
											html += Alfresco.util.activateLinks(Alfresco.util.encodeHTML(data.displayValue));
										}
										break;
								}
								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
				elCell.innerHTML = html;
			};
		},
		onEventSelectRow: function DataGrid_onEventSelectRow(oArgs) {
			// Проверка а из той ли песочницы (два dataGrida) мы вызвали метод. Переопределяz метод мы
			// переопределяем его для всех песочниц на странице.
			if (this._hasEventInterest(LogicECM.module.WCalendar.Calendar.YEARS_LABEL)) {
				// Выделяем строку в DataGrid
				this.widgets.dataTable.onEventSelectRow(oArgs);
				// Перерисовываем таблицу, здесь
				var me = this;
				// Номер строки в таблице
				var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
				// Выбранный элемент
				var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
				// Отрисовка датагрида для рабочих дней
				YAHOO.Bubbling.fire("activeGridChanged",
						{
							datagridMeta: {
								itemType: "lecm-cal:working-days",
								nodeRef: selectItem.getData().nodeRef,
								actionsConfig: {
									fullDelete: false
								}
							},
							bubblingLabel: LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL
						});
				// Отрисовка датагрида для выходных дней
				YAHOO.Bubbling.fire("activeGridChanged",
						{
							datagridMeta: {
								itemType: "lecm-cal:non-working-days",
								nodeRef: selectItem.getData().nodeRef,
								actionsConfig: {
									fullDelete: false
								}
							},
							bubblingLabel: LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL
						});
				// Активируем кнопку "Новый элемент" в правом TollBar-е
				YAHOO.Bubbling.fire("enableAddButton", {
					bubblingLabel: null,
					disable: false
				});
			}
		},
        setupDataTable: function DataGrid_setupDataTable() {
            // YUI DataTable colum
            var columnDefinitions = this.getDataTableColumnDefinitions();
            // DataTable definition
            var me = this;
            if (!this.widgets.dataTable) {
                this._setupPaginatior();
                this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
                this.widgets.dataTable.subscribe("beforeRenderEvent", function () {
                        me.beforeRenderFunction();
                    },
                    me.widgets.dataTable, true);
                if (this._hasEventInterest(LogicECM.module.WCalendar.Calendar.YEARS_LABEL)) {
                    this.widgets.dataTable.subscribe("rowClickEvent", this.onEventSelectRow, this, true);
                }
            }
            this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
                showExtendSearchBlock: this.options.showExtendSearchBlock
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
                itemType:this.datagridMeta.itemType,
                searchConfig: searchConfig,
                searchShowInactive: searchShowInactive,
                sort: sort
            });
        }
	}, true);

})();
