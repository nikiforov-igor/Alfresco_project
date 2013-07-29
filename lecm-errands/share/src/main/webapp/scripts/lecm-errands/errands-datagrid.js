
(function () {

    var Dom = YAHOO.util.Dom;

    var $html = Alfresco.util.encodeHTML;

    LogicECM.module.Errands.DataGrid = function (containerId) {
        LogicECM.module.Errands.DataGrid.superclass.constructor.call(this, containerId);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Errands.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Errands.DataGrid.prototype, {
        getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
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
                            switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
                                case "lecm-errands:is-important":
                                    if (data.value && (("" + data.value) != "false")) {
                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/exclamation_16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                    }
                                    break;
                                case "lecm-errands:baseDocString":
                                    if (data.value && (("" + data.value) != "false")) {
                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/base_doc_16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                    }
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

        onEventHighlightRow:function DataGrid_onEventHighlightRow(oArgs) {

            LogicECM.module.Errands.DataGrid.superclass.onEventHighlightRow.apply( this, [oArgs] );

            // Выбранный элемент
            var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
            if (this.widgets.paginator) {
                numSelectItem = numSelectItem + ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
            }

            var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
            if (selectItem){
                var oData = selectItem.getData();
                this._showBaseDocumentTitle(oData.itemData, oArgs.target.id);
            }
        },

        _showBaseDocumentTitle:function (oData, id) {
            var baseDocPresentString = oData["prop_lecm-errands_baseDocString"] ? oData["prop_lecm-errands_baseDocString"] .value : null;
            if (baseDocPresentString && baseDocPresentString != "") {
                // Получаем список ячеек tr
                var childTrElement = Dom.getChildren(id);
                // Количество элементов tr
                var colTr = Dom.getChildren(Dom.get(id)).length;
                for (i = 0; i < colTr; i++) {
                    Dom.setAttribute(childTrElement[i], "title", baseDocPresentString);
                }
            }
        },

        getDataTableColumnDefinitions: function DataGrid_getDataTableColumnDefinitions() {
            // YUI DataTable column definitions
            var columnDefinitions = [];
            if (this.options.showCheckboxColumn) {
                columnDefinitions.push({
                    key: "nodeRef",
                    label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
                    sortable: false,
                    formatter: this.fnRenderCellSelected (),
                    width: 16
                });
            }

            // важность
            columnDefinitions.push(
                {
                    key: "prop_lecm-errands_is-important",
                    label: "",
                    sortable: false,
                    formatter: this.getCellFormatter("d:boolean"),
                    className: 'centered',
                    width: 16
                });

            // документ-основание
            columnDefinitions.push(
                {
                    key: "prop_lecm-errands_baseDocString",
                    label: "",
                    sortable: false,
                    formatter: this.getCellFormatter("d:boolean"),
                    className: 'centered',
                    width: 16
                });


            var inArray = function(value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            }

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];

                if (this.options.overrideSortingWith === null) {
                    sortable = column.sortable;
                } else {
                    sortable = this.options.overrideSortingWith;
                }

                if (!(this.options.excludeColumns.length > 0 && inArray(column.name, this.options.excludeColumns))) {
                    columnDefinitions.push(
                        {
                            key:this.dataResponseFields[i],
                            label:column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                            sortable:sortable,
                            sortOptions:{
                                field:column.formsName,
                                sortFunction:this.getSortFunction()
                            },
                            formatter:this.getCellFormatter(column.dataType),
                            className: (column.dataType == 'boolean') ? 'centered' : ''
                        });
                }
            }
            if (this.options.showActionColumn){
                // Add actions as last column
                columnDefinitions.push(
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width:80 }
                );
            }
            return columnDefinitions;
        },
        onActionEdit: function DataGrid_onActionEdit(item) {
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + item.nodeRef;
        }
    }, true);
})();
