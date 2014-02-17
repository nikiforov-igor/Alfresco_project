var $siteURL = Alfresco.util.siteURL;

(function () {

    LogicECM.module.ARM.DataGrid = function (containerId) {
        return LogicECM.module.ARM.DataGrid.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.ARM.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DataGrid.prototype, {
        doubleClickLock: false

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
