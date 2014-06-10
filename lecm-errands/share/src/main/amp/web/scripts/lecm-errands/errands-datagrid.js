
(function () {

    var Dom = YAHOO.util.Dom;

    var $html = Alfresco.util.encodeHTML;
    var $links = Alfresco.util.activateLinks;

    LogicECM.module.Errands.DataGrid = function (containerId) {
        LogicECM.module.Errands.DataGrid.superclass.constructor.call(this, containerId);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Errands.DataGrid, LogicECM.module.Documents.DataGrid);

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
                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/exclamation_16.png' + '" width="16" alt="Важное" title="Важное" />';
                                    }
                                    break;
                                case "lecm-errands:baseDocString":
                                    if (data.value && (("" + data.value) != "false")) {
                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/base_doc_16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                    }
                                    break;
                                case "lecm-errands:number":
                                    var value = data.displayValue;
                                    if (value == "Не присвоено") {
                                        value = " - ";
                                        columnContent += '<div style="text-align: center;">'
                                    } else {
                                        columnContent += '<div>'
                                    }
                                    columnContent += $links($html(value));
                                    columnContent += '</div>'
                                    break;
                                default:
                                    break;
                            }

                            if (i < ii - 1) {
                                html += "<br />";
                            }

                            if (datalistColumn.name == "lecm-errands:number" && data.displayValue != "Не присвоено") {
                                html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
                            } else {
                                html += columnContent;
                            }

                            if (oRecord && oRecord.getData("itemData")){
                                if (oRecord.getData("itemData")["prop_lecm-errands_is-expired"] && ("" + oRecord.getData("itemData")["prop_lecm-errands_is-expired"].value) == "true") {
                                    elCell.className += " archive-record";
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
            };

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];

                if (this.options.overrideSortingWith === null) {
                    sortable = column.sortable;
                } else {
                    sortable = this.options.overrideSortingWith;
                }

                if (!(this.options.excludeColumns.length > 0 && inArray(column.name, this.options.excludeColumns))) {
                    var className = "";
                    if (column.dataType == "lecm-orgstr:employee" || (this.options.nowrapColumns.length > 0 && inArray(column.name, this.options.nowrapColumns))) {
                         className = "nowrap "
                    }

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
                            className: className + ((column.dataType == 'boolean') ? 'centered' : '')
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
        }
    }, true);
})();
