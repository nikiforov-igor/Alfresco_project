if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
	var $combine = Alfresco.util.combinePaths;

    var css = ['css/lecm-arm/arm-documents-datagrid.css'];
    LogicECM.module.Base.Util.loadResources([], css);

    LogicECM.module.ARM.DataGrid = function (containerId) {
        LogicECM.module.ARM.DataGrid.superclass.constructor.call(this, containerId)

        YAHOO.Bubbling.on("activeFiltersChanged", this.onActiveFiltersChanged, this);
        YAHOO.Bubbling.on("armNodeSelected", this.onArmNodeSelected, this);

        this.deferredListPopulation = new Alfresco.util.Deferred(["activeFiltersChanged", "onGridTypeChanged", "armNodeSelected"],
            {
                fn: this.populateDataGrid,
                scope: this
            });

        var date = new Date;
        date.setDate(date.getDate() + 30);
        this.expiresDate = date;

        return this;
    };

    YAHOO.lang.extend(LogicECM.module.ARM.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DataGrid.prototype, {
        doubleClickLock: false,

        PREFERENCE_KEY: "ru.it.lecm.arm.",

        armMenuState: {},

        expiresDate: new Date(),

        notSingleQueryPattern: /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i,

        onActionEdit: function (item){
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + item.page + "?nodeRef=" + item.nodeRef;
        },

        onActionViewDocument: function (item){
            this.onActionEdit(item);
        },

        onArmNodeSelected: function (layer, args) {
            if (args[1].armNode) {
                var datagridMeta = this.datagridMeta;
                var node = args[1].armNode;

                var searchQuery = this.getSearchQuery(node);
                if (searchQuery != null) {
                    datagridMeta.searchConfig = {
                        filter: searchQuery
                    }
                }
                if (node.data.columns != null && node.data.columns.length > 0) {
                    datagridMeta.columns = node.data.columns;
                } else {
                    datagridMeta.columns = [
                        {
                            dataType: "text",
                            formsName: "prop_lecm-document_present-string",
                            name: "lecm-document:present-string",
                            label: Alfresco.util.message('lecm.arm.lbl.name'),
                            sortable: true,
                            type: "property"
                        }
                    ];
                }

                if (node.data.types != null && node.data.types.length > 0) {
                    datagridMeta.itemType = node.data.types;
                } else {
                    datagridMeta.itemType = "lecm-document:base";
                }
                datagridMeta.recreate = true;
            }
            if (args[1].menuState) {
                this.armMenuState = args[1].menuState;

                if (this.armMenuState.pageNum) {
                    this.options.initialPage = this.armMenuState.pageNum;
                }
            }
            this.selectedItems = {};
            var selectAll = Dom.get(this.id + '-select-all-records');
            if (selectAll != null) {
                selectAll.checked = false;
            }
            YAHOO.Bubbling.fire("updateArmFilters", {
                currentNode: args[1].armNode,
	            isNotGridNode: args[1].isNotGridNode
            });

        },

        onActiveFiltersChanged: function (layer, args) {
            var obj = args[1];
            if (obj !== null) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if (this._hasEventInterest(label)) {
                    this.currentFilters = obj.filters;
                    this.populateDataGrid();
                }
            }
        },

        _setupPaginatior: function () {
            if (this.options.usePagination) {
                var handlePagination = function DataGrid_handlePagination(state, me) {
                    me.widgets.paginator.setState(state);
                };

                this.widgets.paginator = new YAHOO.widget.Paginator(
                    {
                        containers: [this.id + "-paginatorBottom"],
                        rowsPerPage: this.options.pageSize,
                        initialPage: this.options.initialPage,
                        totalRecords:  YAHOO.widget.Paginator.VALUE_UNLIMITED, // temporary to allow initialPage config.  Will be overwritten by DataTable
                        template: this.msg("lecm.pagination.template"),
                        pageReportTemplate: this.msg("lecm.pagination.template.page-report"),
                        previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
                        nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel"),
                        firstPageLinkLabel: this.msg("lecm.pagination.firstPageLinkLabel"),
                        lastPageLinkLabel: this.msg("lecm.pagination.lastPageLinkLabel"),
                        lastPageLinkTitle: this.msg("lecm.pagination.lastPageLinkLabel.title"),
                        firstPageLinkTitle: this.msg("lecm.pagination.firstPageLinkLabel.title")
                    });

                this.widgets.paginator.subscribe("changeRequest" + this.id, handlePagination, this);

                // Display the bottom paginator bar
                Dom.setStyle(this.id + "-datagridBarBottom", "display", "none");
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
                searchShowInactive: true,
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

	    populateDataGrid: function DataGrid_populateDataGrid() {
		    if (!YAHOO.lang.isObject(this.datagridMeta) || this.datagridMeta.itemType == null) {
			    return;
		    }

		    this.renderDataGridMeta();

		    if (this.datagridMeta.columns != null) {
				var columnParam = {
					json: {
						columns: this.datagridMeta.columns
					}
				};
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
	    },

        _generatePaginatorRequest: function (oState, oSelf) {
            var request = LogicECM.module.ARM.DataGrid.superclass._generatePaginatorRequest.call(this, oState, oSelf);
            this.armMenuState.pageNum = oState.pagination.page;

            LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(), YAHOO.lang.JSON.stringify(this.armMenuState), {expires:this.expiresDate});

            return request;
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
            this.sendRequestToUpdateGrid();
        },

        onExpand: function(record) {
	        if (this.doubleClickLock) return;
	        this.doubleClickLock = true;

	        var nodeRef = record.getData("nodeRef");
	        if (nodeRef != null) {
		        var me = this;
		        Alfresco.util.Ajax.request(
			        {
				        url: Alfresco.constants.PROXY_URI + "lecm/document/connections/api/armPresentation",
				        dataObj: {
					        nodeRef: nodeRef
				        },
				        successCallback: {
					        fn: function(response) {
						        if (response.serverResponse != null) {
							        me.addExpandedRow(record, response.serverResponse.responseText);
						        }
						        me.doubleClickLock = false;
					        }
				        },
				        failureMessage: "message.failure",
				        execScripts: true,
				        scope: this
			        });
	        }
	    },

        getSearchQuery: function (node, buffer, parentId) {
            if (node) {
                var query = node.data.searchQuery;
                if (query && query.length > 0) {
                    var include = !node.data.isAggregate;
                    var baseNodeId = node.data ? node.data.baseNodeId : node.baseNodeId;
                    if (!buffer) {
                        buffer = [];
                        include = true;
                    }
                    if (!parentId) {
                        parentId = baseNodeId;
                    }
                    if ((parentId == baseNodeId
                        || (parentId + '-' + (node.data.runAs ? new Alfresco.util.NodeRef(node.data.runAs).id : "")) == baseNodeId) && include) {
                        buffer.push(query);
                    }
                }
                return this.getSearchQuery(node.parent, buffer, node.data.armNodeId);
            } else {
                var resultedQuery = "";

                if (buffer) {
                    buffer = buffer.reverse();

                    for (var i = 0; i < buffer.length; i++) {
                        var q = buffer[i];
                        var isSingleNotQuery = q.indexOf("NOT") == 0 && !this.notSingleQueryPattern.test(q.trim());
                        resultedQuery += ((!isSingleNotQuery && q.length > 0 ? "(" : "") + q + (!isSingleNotQuery && q.length > 0 ? ")" : "" ) + " AND ");
                    }
                }

                return resultedQuery.length > 4 ?
                    resultedQuery.substring(0, resultedQuery.length - 4) : resultedQuery;
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
                                            columnContent += '<span class="datagrid-datetime">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly")) + '</span>';
                                            break;

                                        case "date":
                                            columnContent += '<span class="datagrid-date">' + Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly")) + '</span>';
                                            break;

                                        case "text":
                                            var hexColorPattern = /^#[0-9a-f]{6}$/i;
											if (data.displayValue.indexOf("<ignoreHtml>") == 0) {
												columnContent += data.displayValue.replace(/<(?:.|\n)*?>/gm, '');
											} else if (data.displayValue.indexOf("!html ") == 0) {
												columnContent += data.displayValue.substring(6);
											} else if (hexColorPattern.test(data.displayValue)) {
                                                columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
                                            } else {
                                                columnContent += $links($html(data.displayValue));
                                            }
                                            break;

                                        case "boolean":
                                            if (data.value) {
                                                columnContent += '<div class="centered">';
//                                                columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
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
//                                                        columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                        columnContent += '<span class="boolean-true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                    var tooltip = null;
                                    switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
                                        case "lecm-events:title":
                                        case "lecm-document:title":
                                            if (columnContent.length > 150) {
                                                tooltip = columnContent;
                                                columnContent = columnContent.substring(0, 150) + "...";
                                            }
                                            break;

                                        case "lecm-outgoing:contractor-assoc":
                                            if (columnContent.length > 200) {
                                                tooltip = columnContent;
                                                columnContent = columnContent.substring(0, 200) + "...";
                                            }
                                            break;
                                        default:
                                            if (datalistColumn.name.toLowerCase().indexOf("cm:nowrap") == 0) {
                                                columnContent = "<div style='white-space: nowrap'>" + data.displayValue + "</div>";
                                            }
                                            break;
                                    }

                                    var firstColumnIndex = scope.options.showCheckboxColumn ? 2 : 1;
                                    if (oColumn.getKeyIndex() == firstColumnIndex) {
                                        columnContent = "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + oRecord.getData("page") + '?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
                                    }

                                    if (tooltip) {
                                        columnContent = '<div class="tt">' + tooltip + '</div>' + columnContent;
                                    }

                                    html += columnContent;

                                    if (i < ii - 1) {
                                        html += "<br />";
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

        getAllSelectedItems: function DataGrid_getSelectedItems() {
            var items = [];
            for (var item in this.selectedItems) {
                if (this.selectedItems.hasOwnProperty(item) && this.selectedItems[item]) {
                    items.push(item);
                }
            }
            return items;
        },

        _buildPreferencesKey: function () {
            return this.PREFERENCE_KEY +  LogicECM.module.ARM.SETTINGS.ARM_CODE + ".menu-state." + LogicECM.currentUser;
        }
    }, true);
})();
