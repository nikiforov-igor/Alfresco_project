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


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.DocumentsJournal
 */
LogicECM.module.DocumentsJournal = LogicECM.module.DocumentsJournal || {};

(function () {

    LogicECM.module.DocumentsJournal.DataGrid = function (containerId) {
        LogicECM.module.DocumentsJournal.DataGrid.superclass.constructor.call(this, containerId);
        return this;
    };

    /**
     * Extend from LogicECM.module.Base.DataGrid
     */
    YAHOO.lang.extend(LogicECM.module.DocumentsJournal.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.DocumentsJournal.DataGrid.prototype, {

        doubleClickLock: false,

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
            return columnDefinitions;
        },

        fnRenderCellImage: function DataGrid_fnRenderCellSelected() {
            var scope = this;

            /**
             * Selector custom datacell formatter
             *
             * @method renderCellSelected
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
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
        },
        showCreateDialog:function (meta) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
            // Intercept before dialog show
            var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                var addMsg = meta.addMessage;
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", addMsg ? addMsg : this.msg("label.create-row.title") ]
                );
                this.doubleClickLock = false;
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                {
                    itemKind:"type",
                    itemId:meta.itemType,
                    destination:meta.nodeRef,
                    mode:"create",
                    formId: meta.createFormId != null ? meta.createFormId : "",
                    submitType:"json"
                });

            // Using Forms Service, so always create new instance
            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
            createDetails.setOptions(
                {
                    width:"50em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn:doBeforeDialogShow,
                        scope:this
                    },
                    onSuccess:{
                        fn:function DataGrid_onActionCreate_success(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text: this.msg("message.save.success")
                                });
                            window.location.href = window.location.protocol + "//" + window.location.host +
                                Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                            this.doubleClickLock = false;
                        },
                        scope:this
                    },
                    onFailure:{
                        fn:function DataGrid_onActionCreate_failure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.save.failure")
                                });
                            this.doubleClickLock = false;
                        },
                        scope:this
                    }
                }).show();
        },

        onDataGridColumns: function DataGrid_onDataGridColumns(response)
        {
            this.datagridColumns = response.json.columns;
            /*this._setupFilter();*/
            // DataSource set-up and event registration
            this.setupDataSource();
            // DataTable set-up and event registration
            this.setupDataTable();
            // DataTable actions setup
            this.setupActions();

            if (this.options.allowCreate) {
                Alfresco.util.createYUIButton(this, "newRowButton", this.onActionCreate.bind(this));
                Dom.setStyle(this.id + "-toolbar", "display", "block");
            }

            // Show grid
            Dom.setStyle(this.id + "-body", "visibility", "visible");
            }
    }, true);
})();
