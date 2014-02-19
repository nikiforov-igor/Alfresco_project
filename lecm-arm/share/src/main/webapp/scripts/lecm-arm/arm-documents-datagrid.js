var $siteURL = Alfresco.util.siteURL,
	$combine = Alfresco.util.combinePaths;

(function () {

    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.ARM.DataGrid = function (containerId) {
        LogicECM.module.ARM.DataGrid.superclass.constructor.call(this, containerId)

        this.filtersMeta = null;
        YAHOO.Bubbling.on("activeFiltersChanged", this.onActiveFiltersChanged, this);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.ARM.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DataGrid.prototype, {
        doubleClickLock: false,

        filtersMeta: null,

        onActiveFiltersChanged: function (layer, args) {
            var obj = args[1];
            if (obj !== null) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    this.filtersMeta = obj.filtersMeta;
                    //обновить данные в гриде! перестраивать саму таблицу не нужно
                    this._setDefaultDataTableErrors(this.widgets.dataTable);

                    if ((this.filtersMeta !== null) && (this.filtersMeta.query !== null) && this.filtersMeta.query.length > 0) {
                        var searchConfig = this.datagridMeta.searchConfig;
                        if (searchConfig == null) {
                            this.datagridMeta.searchConfig = {};
                            this.datagridMeta.searchConfig.filter = "";
                        }

                        var updatedSearchConfig = YAHOO.lang.merge(searchConfig, {});
                        updatedSearchConfig.filter = updatedSearchConfig.filter + " AND (" + this.filtersMeta.query + ")";

                        var offset = 0;
                        if (this.widgets.paginator){
                            offset = ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                        }

                        this.search.performSearch({
                            searchConfig: updatedSearchConfig,
                            searchShowInactive: this.options.searchShowInactive,
                            parent: this.datagridMeta.nodeRef,
                            searchNodes: this.datagridMeta.searchNodes,
                            itemType: this.datagridMeta.itemType,
                            sort:this.datagridMeta.sort,
                            offset:offset,
                            filter: null
                        });
                    }
                }
            }
        },

	    populateDataGrid: function DataGrid_populateDataGrid() {
		    if (!YAHOO.lang.isObject(this.datagridMeta)) {
			    return;
		    }

		    this.renderDataGridMeta();

		    if (this.datagridMeta.columns != null) {
				var columnParam = {
					json: {
						columns: this.datagridMeta.columns
					}
				}
			    this.onDataGridColumns(columnParam);
		    } else {
			    // Query the visible columns for this list's item type
			    var configURL = "";
			    if (this.options.configURL != null) {
				    configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, this.options.configURL + "?nodeRef=" + encodeURIComponent(this.options.datagridMeta.nodeRef));
			    } else {
				    configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(this.datagridMeta.itemType) + ((this.datagridMeta.datagridFormId != null && this.datagridMeta.datagridFormId != undefined) ? "&formId=" + encodeURIComponent(this.datagridMeta.datagridFormId) : ""));
			    }

			    Alfresco.util.Ajax.jsonGet(
				    {
					    url: configURL,
					    successCallback:
					    {
						    fn: this.onDataGridColumns,
						    scope: this
					    },
					    failureCallback:
					    {
						    fn: this._onDataGridFailure,
						    obj:
						    {
							    title: this.msg("message.error.columns.title"),
							    text: this.msg("message.error.columns.description")
						    },
						    scope: this
					    }
				    });
		    }
	    }

        /*getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
            var html = "";
            // Populate potentially missing parameters
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
                            switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                case "lecm-document:ext-present-string":
                                    columnContent += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + data.displayValue + "</a>";
                                    columnContent += '<br />';
                                    columnContent += oRecord.getData("itemData")["prop_lecm-document_list-present-string"].value;
                                    break;
                                default:
                                    break;
                            }

                            if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
                            } else {
                                html += columnContent;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }

                            if (grid.options.contractsWithMyActiveTasks != null) {
                                var bold = false;
                                var nodeRef = oRecord.getData("nodeRef");
                                for (var j = 0; j < grid.options.contractsWithMyActiveTasks.length; j++) {
                                    if (grid.options.contractsWithMyActiveTasks[j] == nodeRef) {
                                        bold = true;
                                        break;
                                    }
                                }
                                if (bold) {
                                    html = "<b>" + html + "</b>";
                                }
                            }
                        }
                    }
                }
            }
            return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
        },

        getDataTableColumnDefinitions: function DataGrid_getDataTableColumnDefinitions() {
            // YUI DataTable column definitions
            var columnDefinitions = [];
            var column, sortable;

            var inArray = function(value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            };

            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];
                sortable = column.sortable;
                if (column.name != "cm:name" && column.name != "lecm-document:list-present-string") {
                    if (column.name == "cm:image") {
                        columnDefinitions.push(
                            {
                                key: this.dataResponseFields[i],
                                label: column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                                sortable: sortable,
                                sortOptions: {
                                    field: column.formsName,
                                    sortFunction: this.getSortFunction()
                                },
                                formatter: this.fnRenderCellImage(),
                                className: (column.dataType == 'boolean') ? 'centered' : '',
                                width: 72
                            });
                    } else {
                        if (!(this.options.excludeColumns.length > 0 && inArray(column.name, this.options.excludeColumns))) {
                            columnDefinitions.push(
                                {
                                    key: this.dataResponseFields[i],
                                    label: column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                                    sortable: false,
                                    sortOptions: {
                                        field: column.formsName,
                                        sortFunction: this.getSortFunction()
                                    },
                                    formatter: this.getCellFormatter(column.dataType),
                                    className: (column.dataType == 'boolean') ? 'centered' : ''
                                });
                        }
                    }
                }
            }
            return columnDefinitions;
        },

        fnRenderCellImage: function DataGrid_fnRenderCellSelected() {
            var scope = this;

            *//**
             * Selector custom datacell formatter
             *
             * @method renderCellSelected
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             *//*
            return function DataGrid_renderCellSelected(elCell, oRecord, oColumn, oData) {
                Dom.setStyle(elCell, "width", oColumn.width + "px");
                Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
                var html = "";
                if (oRecord.getData("type")) {
                    var icon = oRecord.getData("type").replace(":", "_") + ".png";
                    html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + "/images/lecm-documents/type-icons/" + icon + "'/>";
                } else {
                    html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + "/images/lecm-documents/type-icons/default_document.png'/>";
                }
                elCell.innerHTML = html;
            };
        }*/
    }, true);
})();
