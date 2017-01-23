if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.SearchQueries = LogicECM.module.SearchQueries || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;
    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.SearchQueries.DataGrid = function (containerId) {
        LogicECM.module.SearchQueries.DataGrid.superclass.constructor.call(this, containerId);

        YAHOO.Bubbling.on("resetDataGrid", this.onResetGrid, this);
        YAHOO.Bubbling.on("populateDataGrid", this.onPopulateGrid, this);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.SearchQueries.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.SearchQueries.DataGrid.prototype, {
        queryConfig: null,

        PREFERENCE_KEY: "ru.it.lecm.search-editor.state.",

        preferenceColumns:[],

        MAX_CONTENT_SIZE: 200,

        tooltipPosition: null,

        setQueryConfig: function (config) {
            this.queryConfig = config;
        },

        onGridTypeChanged:function DataGrid_onActiveDataListChanged(layer, args) {
            var obj = args[1];
            if ((obj !== null) && (obj.datagridMeta !== null)) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    this.datagridMeta = obj.datagridMeta;
                    this.datagridMeta.recreate = true;

                    if (obj.config) {
                        this.queryConfig = obj.config;
                    }
                    if (!this.deferredListPopulation.fulfil("onGridTypeChanged")) {
                        this.populateDataGrid();
                    }
                }
            }
        },

        onPopulateGrid: function (layer, args) {
            var obj = args[1];
            if (obj !== null) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    this.datagridMeta.recreate = true;
                    this.populateDataGrid();
                }
            }
        },

        onResetGrid: function (layer, args) {
            var obj = args[1];
            var label = obj.bubblingLabel;
            if(this._hasEventInterest(label)){
                if (this.widgets.dataTable && this.widgets.dataTable.getRecordSet()) {
                    this.widgets.dataTable.getRecordSet().reset();
                    this.widgets.dataTable.render();
                }

                Dom.setStyle(this.id + "-body", "visibility", "hidden");
            }
        },

        populateDataGrid: function DataGrid_populateDataGrid() {
            if (!YAHOO.lang.isObject(this.datagridMeta) || this.datagridMeta.itemType == null) {
                return;
            }

            this.renderDataGridMeta();

            var cookieKey = this._buildColumnsKey();
            if (cookieKey) {
                var prefs = LogicECM.module.Base.Util.getCookie(cookieKey);
                if (prefs != null) {
                    try {
                        this.preferenceColumns = JSON.parse(prefs);
                    } catch (e) {
                    }
                }
            }
            // Query the visible columns for this list's item type
            var configURL = "";
            if (this.options.configURL != null) {
                configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, this.options.configURL + "?nodeRef=" + encodeURIComponent(this.options.datagridMeta.nodeRef));
            } else {
                configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT,
                    "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(this.datagridMeta.itemType) +
                    (this.options.datagridFormId ? ("&formId=" + encodeURIComponent(this.options.datagridFormId)) : ""));
            }

            Alfresco.util.Ajax.jsonGet(
                {
                    url: configURL,
                    successCallback: {
                        fn: this.onDataGridColumns,
                        scope: this
                    },
                    failureCallback: {
                        fn: this._onDataGridFailure,
                        obj: {
                            title: this.msg("message.error.columns.title"),
                            text: this.msg("message.error.columns.description")
                        },
                        scope: this
                    }
                });
        },

        getDataTableColumnDefinitions:function () {
            var columnDefinitions = [];
            var inArray = function(value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            };

            if (this.options.showCheckboxColumn) {
                columnDefinitions.push({
                    key: "nodeRef",
                    label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
                    sortable: false,
                    formatter: this.fnRenderCellSelected (),
                    width: 16
                });
            }

            var column, sortable;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                column = this.datagridColumns[i];

                if (this.options.overrideSortingWith === null) {
                    sortable = column.sortable;
                } else {
                    sortable = this.options.overrideSortingWith;
                }

                if (this.preferenceColumns.length == 0 || inArray(column.name, this.preferenceColumns)) {
                    var className = "";
                    if (column.dataType == "lecm-orgstr:employee" || (this.options.nowrapColumns.length > 0 && inArray(column.name, this.options.nowrapColumns))) {
                        className = "nowrap "
                    }

                    columnDefinitions.push({
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
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width: Math.round(26.7 * this.showActionsCount) }
                );
            }
            return columnDefinitions;
        },

        onCellMouseover: function(oArgs) {
            var td = oArgs.target;
            if (td) {
                var tooltip = Selector.query(".yui-dt-liner .tt", td, true);
                if (tooltip) {
                    var windowHeight = window.innerHeight;
                    var windowWidth = window.innerWidth;
                    var eventY = oArgs.event.clientY;
                    var d = 30; // отступ
                    if (eventY > windowHeight / 2) {
                        var ttHeight = parseInt(Dom.getStyle(tooltip, "height"));
                        Dom.setStyle(tooltip, "margin-top", (-ttHeight + d) + "px");
                    } else {
                        Dom.setStyle(tooltip, "margin-top", d + "px");
                    }

                    var xy = YAHOO.util.Dom.getXY(td);
                    this.tooltipPosition = YAHOO.util.Dom.getXY(tooltip);
                    xy[1] = xy[1] + 10;
                    if (windowWidth - (xy[0] + 10) < tooltip.offsetWidth) {
                        xy[0] = windowWidth - tooltip.offsetWidth;
                    } else {
                        xy[0] = xy[0] + 10;
                    }
                    YAHOO.util.Dom.setXY(tooltip, xy);
                }
            }
        },
        onCellMouseout: function(oArgs) {
            var td = oArgs.target;
            if (td) {
                var tooltip = Selector.query(".yui-dt-liner .tt", td, true);
                if (tooltip) {
                    if (this.tooltipPosition != null) {
                        YAHOO.util.Dom.setXY(tooltip, this.tooltipPosition);
                        this.tooltipPosition = null;
                    }
                }
            }
        },

        getCellFormatter: function DataGrid_getCellFormatter() {
            var scope = this;
            return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
                var $html = Alfresco.util.encodeHTML,
                    $links = Alfresco.util.activateLinks,
                    $userProfile = Alfresco.util.userProfileLink;
                var html = "";
                var htmlValue = scope.getCustomCellFormatter.call(this, scope, elCell, oRecord, oColumn, oData);
                if (htmlValue == null) { // используем стандартный форматтер
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
                            var datalistColumn = scope.datagridColumns[oColumn.key];
                            if (datalistColumn) {
                                oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                                for (var i = 0, ii = oData.length, data; i < ii; i++) {
                                    data = oData[i];

                                    var columnContent = "";
                                    switch (datalistColumn.dataType.toLowerCase()) {
                                        case "lecm-orgstr:employee":
                                            columnContent += scope.getEmployeeView(data.value, data.displayValue);
                                            break;

                                        case "lecm-orgstr:employee-link":
                                            columnContent += scope.getEmployeeViewByLink(data.value, data.displayValue);
                                            break;

                                        case "cm:person":
                                            columnContent += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                            break;

                                        case "datetime":
                                            columnContent += '<span class="datagrid-datetime">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.default")) + '</span>';
                                            break;

                                        case "date":
                                            columnContent += '<span class="datagrid-date">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly")) + '</span>';
                                            break;

                                        case "text":
                                            var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                            if (hexColorPattern.test(data.displayValue)) {
                                                columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
                                            } else {
                                                columnContent += $links(data.displayValue);
                                            }
                                            break;

                                        case "boolean":
                                            if (data.value) {
                                                columnContent += '<div class="centered">';
                                                columnContent += '<span class="boolean-true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
                                                columnContent += '</div>';
                                            }
                                            break;

                                        default:
                                            if (datalistColumn.type == "association") {
                                                columnContent += $html(data.displayValue);
                                            } else {
                                                if (data.displayValue != "false" && data.displayValue != "true") {
                                                    columnContent += $html(data.displayValue);
                                                } else {
                                                    if (data.displayValue == "true") {
                                                        columnContent += '<span class="boolean-true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
                                                    }
                                                }
                                            }
                                            break;
                                    }

                                    if (datalistColumn.name.toLowerCase().indexOf("cm:nowrap") == 0) {
                                        columnContent = "<div style='white-space: nowrap'>" + data.displayValue + "</div>";
                                    }

                                    html += columnContent;

                                    if (i < ii - 1) {
                                        html += "<br />";
                                    }
                                }

                                var firstColumnIndex = scope.options.showCheckboxColumn ? 1 : 0;

                                var stripedTooltip = html.replace(/<\/?[^>]+>/g,'');
                                if (stripedTooltip.length > scope.MAX_CONTENT_SIZE) {
                                    var content = stripedTooltip.substring(0, scope.MAX_CONTENT_SIZE) + "...";

                                    if (oColumn.getKeyIndex() == firstColumnIndex) {
                                        content = "<a target='_blank' href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + content + "</a>";
                                    }

                                    html = '<div class="tt">' + html + '</div>' + content;
                                } else {
                                    if (oColumn.getKeyIndex() == firstColumnIndex) {
                                        html = "<a target='_blank' href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + html + "</a>";
                                    }
                                }
                            }
                        }
                    }
                } else {
                    html = htmlValue;
                }

                if (oRecord && oRecord.getData("itemData")) {
                    if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
                        elCell.className += " archive-record";
                    }
                }
                elCell.innerHTML = html;
            };
        },

        _setupPaginatior: function () {
            LogicECM.module.SearchQueries.DataGrid.superclass._setupPaginatior.call(this);
            if (this.widgets.paginator) {
                var handlePagination = function (state, me) {
                    me.pageLoading = true;
                    me._showMessageOnPaginationLoading.call(me);
                    me.widgets.paginator.setState(state);
                };
                this.widgets.paginator.subscribe("changeRequest", handlePagination, this);

                this.paginatorSetPage = this.widgets.paginator.setPage;
                this.widgets.paginator.setPage = this.setPage.bind(this);
            }
        },

        setPage: function (page, silent) {
            if (this.pageLoading) {
                return;
            } else {
                this.paginatorSetPage.call(this.widgets.paginator, page, silent);
            }
        },

        sendRequestToUpdateGrid: function () {
            //обновить данные в гриде! перестраивать саму таблицу не нужно
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            if (!this.datagridMeta.searchConfig) {
                this.datagridMeta.searchConfig = {};
                this.datagridMeta.searchConfig.filter = "";
            }
            var searchConfig = this.datagridMeta.searchConfig;

            if (searchConfig.formData) {
                if (typeof searchConfig.formData == "string") {
                    searchConfig.formData = YAHOO.lang.JSON.parse(searchConfig.formData);
                }
                searchConfig.formData.datatype = this.datagridMeta.itemType;
            } else {
                searchConfig.formData = {
                    datatype: this.datagridMeta.itemType
                };
            }
            if (this.datagridMeta.postFilter != null) {
                var lastDocumentsString = localStorage.getItem("ru.it.lecm.documents.last." + Alfresco.constants.USERNAME);
                var lastDocuments = [];
                if (lastDocumentsString != null) {
                    lastDocuments = JSON.parse(lastDocumentsString);
                }
                searchConfig.postFilter = {
                    lastDocuments: lastDocuments,
                    query: this.datagridMeta.postFilter
                };
            }

            var offset = 0;
            if (this.widgets.paginator) {
                offset = ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
            }

            //при первом поиске сохраняем настройки
            if (this.initialSearchConfig == null) {
                this.initialSearchConfig = {fullTextSearch: null};
                this.initialSearchConfig = YAHOO.lang.merge(searchConfig, this.initialSearchConfig);
            }

            this.search.performSearch({
                searchConfig: searchConfig,
                searchShowInactive: this.options.searchShowInactive,
                parent: this.datagridMeta.nodeRef,
                searchNodes: this.datagridMeta.searchNodes,
                itemType: this.datagridMeta.itemType,
                sort: this.datagridMeta.sort,
                offset: offset,
                filter: this.currentFilters,
                useOnlyInSameOrg: this.datagridMeta.useOnlyInSameOrg,
                useFilterByOrg: this.datagridMeta.useFilterByOrg
            });
        },

        setupDataTable: function DataGrid__setupDataTable() {
            var columnDefinitions = this.getDataTableColumnDefinitions();
            this.restoreSortFromCookie();
            // DataTable definition
            var me = this;
            if (!this.widgets.dataTable || this.datagridMeta.recreate) {
                if (this.widgets.dataTable) {
                    this.destroyDatatable();
                }

                this._setupPaginatior();
                this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
                this.datatableHideTableMessage = this.widgets.dataTable.hideTableMessage;
                this.widgets.dataTable.hideTableMessage = this._hideMessageOnPaginationLoading.bind(this);
                this.widgets.dataTable.subscribe("cellMouseoverEvent", this.onCellMouseover, this, true);
                this.widgets.dataTable.subscribe("cellMouseoutEvent", this.onCellMouseout, this, true);
                if (!this.search) {
                    // initialize Search
                    this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
                        showExtendSearchBlock: this.options.showExtendSearchBlock,
                        maxSearchResults: this.options.maxResults,
                        searchFormId: this.options.advSearchFormId
                    });

                } else {
                    this.search.clear(this);
                }
                this.datagridMeta.recreate = false; // сброс флага
            }

            this.search.currentQueryTimeStep = new Date().getTime();
            this.sendRequestToUpdateGrid();
        },

        getAllSelectedItems: function DataGrid_getSelectedItems() {
            var items = [];
            for (var item in this.selectedItems) {
                if (this.selectedItems.hasOwnProperty(item) && this.selectedItems[item]) {
                    items.push(item);
                }
            }
            return items;
        },

        _showMessageOnPaginationLoading: function() {
            this.widgets.dataTable.showTableMessage.call(this.widgets.dataTable);
            this.loadingPopup = Alfresco.util.PopupManager.displayMessage({
                displayTime: 0,
                text: this.msg("label.loading"),
                spanClass: "wait",
                noEscape: true
            });
        },

        _hideMessageOnPaginationLoading: function() {
            this.pageLoading = false;
            this.datatableHideTableMessage.call(this.widgets.dataTable);
            if (this.loadingPopup) {
                this.loadingPopup.hide();
            }
        },

        _buildColumnsKey: function () {
            if (this.queryConfig && this.queryConfig.queryNodeRef) {
                var id = new Alfresco.util.NodeRef(this.queryConfig.queryNodeRef).id;
                return this.PREFERENCE_KEY + "columnPref." + Alfresco.constants.USERNAME + "." + id;
            }
            return null;
        },

        onActionEdit: function (item){
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + item.page + "?nodeRef=" + item.nodeRef;
        },

        onActionViewDocument: function (item){
            this.onActionEdit(item);
        }
    }, true);
})();
