/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};
/**
 * Base: DataGrid component.
 *
 * @namespace Alfresco
 * @class LogicECM.module.Base.DataGrid
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink;

    /**
     * DataGrid constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Base.DataGrid} The new DataGrid instance
     * @constructor
     */
    LogicECM.module.Base.DataGrid = function(htmlId)
    {
        LogicECM.module.Base.DataGrid.superclass.constructor.call(this, "LogicECM.module.Base.DataGrid", htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation", "history"]);

        // Initialise prototype properties
        this.datagridMeta = {};
        this.datagridColumns = {};
        this.dataRequestFields = [];
        this.dataResponseFields = [];
        this.currentPage = 1;
        this.totalRecords = 0;
        this.showingMoreActions = false;
        this.selectedItems = {};
        this.afterDataGridUpdate = [];

        /**
         * Decoupled event listeners
         */
        Bubbling.on("activeGridChanged", this.onGridTypeChanged, this);
        Bubbling.on("dataItemCreated", this.onDataItemCreated, this);
        Bubbling.on("dataItemUpdated", this.onDataItemUpdated, this);
        Bubbling.on("dataItemsDeleted", this.onDataItemsDeleted, this);
        Bubbling.on("dataItemsDuplicated", this.onDataGridRefresh, this);

        /* Deferred list population until DOM ready */
        this.deferredListPopulation = new Alfresco.util.Deferred(["onReady", "onGridTypeChanged"],
            {
                fn: this.populateDataGrid,
                scope: this
            });

        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Base.DataGrid, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Base.DataGrid.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:
            {
                /**
                 * Flag indicating whether pagination is available or not.
                 *
                 * @property usePagination
                 * @type boolean
                 * @default false
                 */
                usePagination: false,

                showExtendSearchBlock: true,

                /**
                 * Initial page to show on load (otherwise taken from URL hash).
                 *
                 * @property initialPage
                 * @type int
                 */
                initialPage: 1,

                /**
                 * Number of items per page
                 *
                 * @property pageSize
                 * @type int
                 */
                pageSize: 20,

                /**
                 * Delay time value for "More Actions" popup, in milliseconds
                 *
                 * @property actionsPopupTimeout
                 * @type int
                 * @default 500
                 */
                actionsPopupTimeout: 500,

                /**
                 * Delay before showing "loading" message for slow data requests
                 *
                 * @property loadingMessageDelay
                 * @type int
                 * @default 1000
                 */
                loadingMessageDelay: 1000,

                /**
                 * How many actions to display before the "More..." container
                 *
                 * @property splitActionsAt
                 * @type int
                 * @default 3
                 */
                splitActionsAt: 3,

                /**
                 * Метка для bubbling. Используется для отрисовки датагрида. Следует передать в datagridMeta
                 */
                bubblingLabel: null,

				/**
				 * Набор действий для списка
				 */
				actions: null,

				/**
				 * Data List metadata retrieved from the Repository
				 *
				 * @param datagridMeta
				 * @type Object
				 */
				datagridMeta: null,

				/**
				 * Grid height for forms control
				 */
				height: null,

				/**
				 * Allow create button toolbar
				 */
				allowCreate: false
            },

            /**
             * Current page being browsed.
             *
             * @property currentPage
             * @type int
             * @default 1
             */
            currentPage: null,

            /**
             * Total number of records (documents + folders) in the currentPath.
             *
             * @property totalRecords
             * @type int
             * @default 0
             */
            totalRecords: null,

            /**
             * Object literal of selected states for visible items (indexed by nodeRef).
             *
             * @property selectedItems
             * @type object
             */
            selectedItems: null,

            /**
             * Current actions menu being shown
             *
             * @property currentActionsMenu
             * @type object
             * @default null
             */
            currentActionsMenu: null,

            /**
             * Whether "More Actions" pop-up is currently visible.
             *
             * @property showingMoreActions
             * @type boolean
             * @default false
             */
            showingMoreActions: null,

            /**
             * Deferred actions menu element when showing "More Actions" pop-up.
             *
             * @property deferredActionsMenu
             * @type object
             * @default null
             */
            deferredActionsMenu: null,

            /**
             * Deferred function calls for after a data grid update
             *
             * @property afterDataGridUpdate
             * @type array
             */
            afterDataGridUpdate: null,

            /**
             * Data List metadata retrieved from the Repository
             *
             * @param datagridMeta
             * @type Object
             */
            datagridMeta: null,

            /**
             * Data List columns from Form configuration
             *
             * @param datalistColumns
             * @type Object
             */
            datagridColumns: null,

            /**
             * Fields sent in the data request
             *
             * @param dataRequestFields
             * @type Object
             */
            dataRequestFields: null,

            /**
             * Fields returned from the data request
             *
             * @param dataResponseFields
             * @type Object
             */
            dataResponseFields: null,
            /** The latest version of the document
             *
             * @property latestVersion
             * @type {Object}
             */
            latestVersion: null,

            /**
             * A cached copy of the version history to limit duplicate calls.
             *
             * @property versionCache
             * @type {Object} XHR response object
             */
            versionCache: null,

            versionable: false,
            /**
             * Порядок сортировки
             */
            desc: true,
            elTh: null,
            /**
             * Порядок сортировки. Рисуется стрелочка на отсортированным столбцом
             */
//            currentSort:
//            {
//                oColumn:null,
//                sSortDir:null
//            },

            /**
             * Returns selector custom datacell formatter
             *
             * @method fnRenderCellSelected
             */
            fnRenderCellSelected: function DataGrid_fnRenderCellSelected()
            {
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
                return function DataGrid_renderCellSelected(elCell, oRecord, oColumn, oData)
                {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oData + '"' + (scope.selectedItems[oData] ? ' checked="checked">' : '>');
                };
            },

            /**
             * Returns actions custom datacell formatter
             *
             * @method fnRenderCellActions
             */
            fnRenderCellActions: function DataGrid_fnRenderCellActions()
            {
                var scope = this;

                /**
                 * Actions custom datacell formatter
                 *
                 * @method renderCellActions
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function DataGrid_renderCellActions(elCell, oRecord, oColumn, oData)
                {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
                };
            },


			/**
			 * Draw stanalone grid
			 * @return {Function}
			 * @constructor
			 */

			draw: function() {
				this.datagridMeta = this.options.datagridMeta;
				this.deferredListPopulation.fulfil("onGridTypeChanged")
				this.onReady();
			},

            /**
             * Return data type-specific formatter
             *
             * @method getCellFormatter
             * @return {function} Function to render read-only value
             */
            getCellFormatter: function DataGrid_getCellFormatter()
            {
                var scope = this;

                /**
                 * Data Type custom formatter
                 *
                 * @method renderCellDataType
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData)
                {
                    var html = "";

                    // Populate potentially missing parameters
                    if (!oRecord)
                    {
                        oRecord = this.getRecord(elCell);
                    }
                    if (!oColumn)
                    {
                        oColumn = this.getColumn(elCell.parentNode.cellIndex);
                    }

                    if (oRecord && oColumn)
                    {
                        if (!oData)
                        {
                            oData = oRecord.getData("itemData")[oColumn.field];
                        }

                        if (oData)
                        {
                            var datalistColumn = scope.datagridColumns[oColumn.key];
                            if (datalistColumn)
                            {
                                oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                                for (var i = 0, ii = oData.length, data; i < ii; i++)
                                {
                                    data = oData[i];

                                    switch (datalistColumn.dataType.toLowerCase())
                                    {
                                        case "cm:person":
                                            html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                            break;

                                        case "datetime":
                                            html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
                                            break;

                                        case "date":
                                            html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
                                            break;

                                        case "text":
                                            html += $links($html(data.displayValue));
                                            break;

                                        default:
                                            if (datalistColumn.type == "association")
                                            {
                                                html += '<a>';
                                                html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16) + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                html += ' ' + $html(data.displayValue) + '</a>'
                                            }
                                            else
                                            {
                                                html += $links($html(data.displayValue));
                                            }
                                            break;
                                    }

                                    if (i < ii - 1)
                                    {
                                        html += "<br />";
                                    }
                                }
                            }
                        }
                    }

                    elCell.innerHTML = html;
                };
            },

            /**
             * Return data type-specific sorter
             *
             * @method getSortFunction
             * @return {function} Function to sort column by
             */
            getSortFunction: function DataGrid_getSortFunction()
            {
                /**
                 * Data Type custom sorter
                 *
                 * @method sortFunction
                 * @param a {object} Sort record a
                 * @param b {object} Sort record b
                 * @param desc {boolean} Ascending/descending flag
                 * @param field {String} Field to sort by
                 */
                return function DataGrid_sortFunction(a, b, desc, field)
                {
                    var fieldA = a.getData().itemData[field],
                        fieldB = b.getData().itemData[field];

                    if (YAHOO.lang.isArray(fieldA))
                    {
                        fieldA = fieldA[0];
                    }
                    if (YAHOO.lang.isArray(fieldB))
                    {
                        fieldB = fieldB[0];
                    }

                    // Deal with empty values
                    if (!YAHOO.lang.isValue(fieldA))
                    {
                        return (!YAHOO.lang.isValue(fieldB)) ? 0 : 1;
                    }
                    else if (!YAHOO.lang.isValue(fieldB))
                    {
                        return -1;
                    }

                    var valA = fieldA.value,
                        valB = fieldB.value;

                    if (valA.indexOf && valA.indexOf("workspace://SpacesStore") == 0)
                    {
                        valA = fieldA.displayValue;
                        valB = fieldB.displayValue;
                    }

                    return YAHOO.util.Sort.compare(valA, valB, desc);
                };
            },

            /**
             * Fired by YUI when parent element is available for scripting
             *
             * @method onReady
             */
            onReady: function DataGrid_onReady()
            {
                var me = this;

                // Hook action events
                var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        if (typeof me[owner.className] == "function")
                        {
                            args[1].stop = true;
                            var row = me.widgets.dataTable.getRecord(args[1].target.offsetParent);
                            if (row) {
                                var asset = row.getData();
                                me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, null);
                            }
                        }
                    }
                    return true;
                };
                Bubbling.addDefaultAction("action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler);
                Bubbling.addDefaultAction("show-more", fnActionHandler);

                // Actions module
                this.modules.actions = new LogicECM.module.Base.Actions();

                var context = this;
                // initialize Search
                // draw it after get metaData!
                this.modules.search = new LogicECM.AdvancedSearch(this.id).setOptions({
                    showExtendSearchBlock:context.options.showExtendSearchBlock
                });

				if (this.options.bubblingLabel != null) {
					this.datagridMeta.bubblingLabel = this.options.bubblingLabel;
				}

                // Reference to Data Grid component (required by actions module)
                this.modules.dataGrid = this;

                // Assume no list chosen for now
                Dom.removeClass(this.id + "-selectListMessage", "hidden");

                this.deferredListPopulation.fulfil("onReady");

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");

                Bubbling.fire("initDatagrid",
                    {
                        datagrid:this
                    });
            },

            /**
             * Display an error message pop-up
             *
             * @private
             * @method _onDataListFailure
             * @param response {Object} Server response object from Ajax request wrapper
             * @param message {Object} Object literal of the format:
             *    <pre>
             *       title: Dialog title string
             *       text: Dialog body message
             *    </pre>
             */
            _onDataGridFailure: function DataGrid__onDataGridFailure(p_response, p_message)
            {
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: p_message.title,
                        text: p_message.text,
                        modal: true,
                        buttons: [
                            {
                                text: this.msg("button.ok"),
                                handler: function DataGrid__onDataGridFailure_OK()
                                {
                                    this.destroy();
                                },
                                isDefault: true
                            }]
                    });

            },
            /**
             * Обновление формы поиска
             * @constructor
             */
            renderSearchForm: function DataGrid_renderSearchForm()
            {
                if (!YAHOO.lang.isObject(this.datagridMeta))
                {
                    return;
                }
                // init search
                if (this.modules.search) {
                    this.modules.search.initSearch(this.datagridMeta);
                } else {
                    this.modules.search = new LogicECM.AdvancedSearch(this.id);
                    this.modules.search.initSearch(this.datagridMeta);
                }
            },
            /**
             * Renders Data List metadata, i.e. title and description
             *
             * @method renderDataGridMeta
             */
            renderDataGridMeta: function DataGrid_renderDataGridMeta()
            {
                if (!YAHOO.lang.isObject(this.datagridMeta))
                {
                    return;
                }

                Alfresco.util.populateHTML(
                    [ this.id + "-title", $html(this.datagridMeta.title) ],
                    [ this.id + "-description", $links($html(this.datagridMeta.description, true)) ]
                );
            },

            /**
             * Retrieves the Data List from the Repository
             *
             * @method populateDataGrid
             */
            populateDataGrid: function DataGrid_populateDataGrid()
            {
                if (!YAHOO.lang.isObject(this.datagridMeta))
                {
                    return;
                }

				this.renderDataGridMeta();
                this.renderSearchForm();
                // Query the visible columns for this list's item type
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "components/data-lists/config/columns?itemType=" + encodeURIComponent(this.datagridMeta.itemType)),
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
            },

            /**
             * Data List column definitions returned from the Repository
             *
             * @method onDataGridColumns
             * @param response {Object} Ajax data structure
             */
            onDataGridColumns: function DataGrid_onDataGridColumns(response)
            {
                this.datagridColumns = response.json.columns;
                // Set-up YUI History Managers and Paginator
                this._setupHistoryManagers();
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
				// Hide "no list" message
                Dom.addClass(this.id + "-selectListMessage", "hidden");
            },

            /**
             * History Manager set-up and event registration
             *
             * @method _setupHistoryManagers
             */
            _setupHistoryManagers: function DataGrid__setupHistoryManagers()
            {
                /**
                 * YUI History - filter
                 */
                var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
                bookmarkedFilter = bookmarkedFilter === null ? "all" : (YAHOO.env.ua.gecko > 0) ? bookmarkedFilter : window.escape(bookmarkedFilter);

                try
                {
                    while (bookmarkedFilter != (bookmarkedFilter = decodeURIComponent(bookmarkedFilter))){}
                }
                catch (e1)
                {
                    // Catch "malformed URI sequence" exception
                }

                var fnDecodeBookmarkedFilter = function DataGrid_fnDecodeBookmarkedFilter(strFilter)
                {
                    var filters = strFilter.split("|"),
                        filterObj =
                        {
                            filterId: window.unescape(filters[0] || ""),
                            filterData: window.unescape(filters[1] || "")
                        };

                    filterObj.filterOwner = Alfresco.util.FilterManager.getOwner(filterObj.filterId);
                    return filterObj;
                };

                //this.options.initialFilter = fnDecodeBookmarkedFilter(bookmarkedFilter);

                // Register History Manager filter update callback
                YAHOO.util.History.register("filter", bookmarkedFilter, function DataGrid_onHistoryManagerFilterChanged(newFilter)
                {
                    Alfresco.logger.debug("HistoryManager: filter changed:" + newFilter);
                    // Firefox fix
                    if (YAHOO.env.ua.gecko > 0)
                    {
                        newFilter = window.unescape(newFilter);
                        Alfresco.logger.debug("HistoryManager: filter (after Firefox fix):" + newFilter);
                    }

                    this._updateDataGrid.call(this,
                        {
                            filter: fnDecodeBookmarkedFilter(newFilter)
                        });
                }, null, this);


                /**
                 * YUI History - page
                 */
                var me = this;
                var handlePagination = function DataGrid_handlePagination(state, me)
                {
                    me.widgets.paginator.setState(state);
                    YAHOO.util.History.navigate("page", String(state.page));
                };

                if (this.options.usePagination && !this.widgets.paginator)
                {
                    var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
                    while (bookmarkedPage != (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
                    this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

                    // Register History Manager page update callback
                    YAHOO.util.History.register("page", bookmarkedPage, function DataGrid_onHistoryManagerPageChanged(newPage)
                    {
                        Alfresco.logger.debug("HistoryManager: page changed:" + newPage);
                        me.widgets.paginator.setPage(parseInt(newPage, 10));
                        this.currentPage = parseInt(newPage, 10);
                    }, null, this);

                    // YUI Paginator definition
                    this.widgets.paginator = new YAHOO.widget.Paginator(
                        {
                            containers: [this.id + "-paginatorBottom"],
                            rowsPerPage: this.options.pageSize,
                            initialPage: this.currentPage,
                            template: this.msg("pagination.template"),
                            pageReportTemplate: this.msg("pagination.template.page-report"),
                            previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
                            nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
                        });

                    this.widgets.paginator.subscribe("changeRequest", handlePagination, this);

                    // Display the bottom paginator bar
                    Dom.setStyle(this.id + "-datagridBarBottom", "display", "block");
                }

                // Initialize the browser history management library
                try
                {
                    YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
                }
                catch (e2)
                {
                    /*
                     * The only exception that gets thrown here is when the browser is
                     * not supported (Opera, or not A-grade)
                     */
                    Alfresco.logger.error(this.name + ": Couldn't initialize HistoryManager.", e2);
                }
            },
            /**
             * Поиск
             * @return {YAHOO.util.DataSource}
             * @private
             */
            _setupDataSource:function () {
                var uriSearchResults = Alfresco.constants.PROXY_URI + "lecm/search";
                var dSource = new YAHOO.util.DataSource(uriSearchResults,
                    {
                        connMethodPost:true,
                        responseType:YAHOO.util.DataSource.TYPE_JSON,
                        connXhrMode:"queueRequests",
                        responseSchema:{
                            resultsList:"items",
                            metaFields:{
                                paginationRecordOffset:"startIndex",
                                totalRecords:"totalRecords",
                                isVersionable:"versionable",
                                meta:"metadata"
                            }
                        }
                    });

                dSource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON);

                // Intercept data returned from data webscript to extract custom metadata
                dSource.doBeforeCallback = function DataGrid_doBeforeCallback(oRequest, oFullResponse, oParsedResponse) {
                    this.versionable = oFullResponse.versionable;
                    // Container userAccess event
                    var permissions = oFullResponse.metadata.permissions;
                    if (permissions && permissions.userAccess) {
                        Bubbling.fire("userAccess",
                            {
                                userAccess:permissions.userAccess
                            });
                    }
                    return oParsedResponse;
                }.bind(this);
                return dSource;
            },
            /**
             * DataSource set-up and event registration
             *
             * @method _setupDataSource
             * @protected
             */
            setupDataSource: function DataGrid__setupDataSource()
            {
                this.dataRequestFields = [];
                this.dataResponseFields = [];

                for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                    var column = this.datagridColumns[i],
                        columnName = column.name.replace(":", "_"),
                        fieldLookup = (column.type == "property" ? "prop" : "assoc") + "_" + columnName;

                    this.dataRequestFields.push(columnName);
                    this.dataResponseFields.push(fieldLookup);
                    this.datagridColumns[fieldLookup] = column;
                }

                // DataSource definition if not alfready defined
                if (!this.widgets.dataSource) {
                    this.widgets.dataSource = this._setupDataSource();
                    // link dataSource with search
                    this.modules.search.dataSource = this.widgets.dataSource;
                }
                this.modules.search.dataColumns = this.datagridColumns;
            },
            /**
             * Получение колонок dataGrid
             * @return {Array} список колонок
             * @constructor
             */
            getDataTableColumnDefinitions:function DataGrid_getDataTableColumnDefinitions() {
                // YUI DataTable column definitions
                var columnDefinitions =
                    [
                        { key:"nodeRef", label:"<input type='checkbox' id='select-all-records'>", sortable:false, formatter:this.fnRenderCellSelected(), width:16 }
                    ];

                var column;
                for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                    column = this.datagridColumns[i];
                    columnDefinitions.push(
                        {
                            key:this.dataResponseFields[i],
                            label:column.label,
                            sortable:true,
                            sortOptions:{
                                field:column.formsName,
                                sortFunction:this.getSortFunction()
                            },
                            formatter:this.getCellFormatter(column.dataType)
                        });
                }

                // Add actions as last column
                columnDefinitions.push(
                    { key:"actions", label:this.msg("label.column.actions"), sortable:false, formatter:this.fnRenderCellActions(), width:80 }
                );
                return columnDefinitions;
            },
            /**
             * Прорисовка таблицы, установка свойств, сортировка.
             * @param columnDefinitions колонки
             * @param me {object} this
             * @return {YAHOO.widget.DataTable} таблица
             * @private
             */
            _setupDataTable:function (columnDefinitions, me) {
                var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
                    {
                        renderLoopSize:this.options.usePagination ? 16 : 32,
                        initialLoad:false,
                        dynamicData:false,
                        "MSG_EMPTY":this.msg("message.empty"),
                        "MSG_ERROR":this.msg("message.error"),
                        paginator:this.widgets.paginator
                    });

                // Update totalRecords with value from server
                dTable.handleDataReturnPayload = function DataGrid_handleDataReturnPayload(oRequest, oResponse, oPayload) {
                    me.totalRecords = oResponse.meta.totalRecords;
                    oResponse.meta.pagination =
                    {
                        rowsPerPage:me.options.pageSize,
                        recordOffset:(me.currentPage - 1) * me.options.pageSize
                    };
                    return oResponse.meta;
                };

                // Override abstract function within DataTable to set custom error message
                dTable.doBeforeLoadData = function DataGrid_doBeforeLoadData(sRequest, oResponse, oPayload) {
                    if (oResponse.error) {
                        try {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            me.widgets.dataTable.set("MSG_ERROR", response.message);
                        }
                        catch (e) {
                            me._setDefaultDataTableErrors(me.widgets.dataTable);
                        }
                    }

                    // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
                    if (oResponse.results.length === 0) {
                        this.fireEvent("renderEvent",
                            {
                                type:"renderEvent"
                            });
                    }

                    // Must return true to have the "Loading..." message replaced by the error message
                    return true;
                };

                // Override default function so the "Loading..." message is suppressed
                dTable.doBeforeSortColumn = function DataGrid_doBeforeSortColumn(oColumn, sSortDir) {
                    me.currentSort =
                    {
                        oColumn:oColumn,
                        sSortDir:sSortDir

                    };
                    me.sort = {
                        enable: true
                    }
                    return true;
                };

                // Событие когда выбранны все элементы
                YAHOO.util.Event.onAvailable("select-all-records", function () {
                    YAHOO.util.Event.on("select-all-records", 'click', this.selectAllClick, this, true);
                }, this, true);

                // File checked handler
                dTable.subscribe("checkboxClickEvent", function (e) {
                    var id = e.target.value;
                    this.selectedItems[id] = e.target.checked;

                    var checks = Selector.query('input[type="checkbox"]', dTable.getTbodyEl()),
                        len = checks.length, i;

                    var allChecked = true;
                    for (i = 0; i < len; i++) {
                        if (!checks[i].checked) {
                            allChecked = false;
                            break;
                        }
                    }
                    Dom.get('select-all-records').checked = allChecked;

                    Bubbling.fire("selectedItemsChanged");
                }, this, true);

                // Сортировка. Событие при нажатии на название столбца.
                dTable.subscribe("beforeRenderEvent",function () {
                        var dataGrid = me.modules.dataGrid;
                        var datagridMeta = dataGrid.datagridMeta;

                        if (me.currentSort){
                            if (me.elTh == null) {
                                me.elTh = me.currentSort.oColumn.getThEl();
                            }
                            if (me.elTh == me.currentSort.oColumn.getThEl()) {
                                if (me.currentSort.sSortDir == YAHOO.widget.DataTable.CLASS_DESC){
                                    Dom.addClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_DESC);
                                } else {
                                    Dom.removeClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_DESC);
                                    Dom.addClass(me.currentSort.oColumn.getThEl(), YAHOO.widget.DataTable.CLASS_ASC);
                                }

                            } else {
                                Dom.removeClass(me.elTh, YAHOO.widget.DataTable.CLASS_DESC);
                                Dom.removeClass(me.elTh, YAHOO.widget.DataTable.CLASS_ASC)
                                me.elTh = me.currentSort.oColumn.getThEl();
                            }
                        }
                        if (me.sort) {
                            if (datagridMeta.searchConfig == undefined) {
                                datagridMeta.searchConfig = {};
                            }
                            datagridMeta.searchConfig.sort = "";
                            // Если ассоциация, то не сортируем
                            if (me.currentSort.oColumn.field.indexOf("assoc_") != 0) {
                                if (me.desc) {
                                    datagridMeta.searchConfig.sort = me.currentSort.oColumn.field.replace("prop_","").replace("_",":") +
                                       "|false";
                                    me.desc = false;
                                    me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_DESC;

                                } else {
                                    datagridMeta.searchConfig.sort = me.currentSort.oColumn.field.replace("prop_", "").replace("_", ":") +
                                        "|true";
                                    me.desc = true;
                                    me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_ASC;
                                }
                                //complete initial search
                                var initialData = {
                                    datatype:datagridMeta.itemType
                                };
                                var searchConfig = datagridMeta.searchConfig;
                                var sorting, filter, fullText;
                                    filter = searchConfig.filter;
                                    fullText = searchConfig.fullTextSearch;
                                if (me.sort){
                                // Обнуляем сортировку иначе зациклится.
                                me.sort = null;
                                YAHOO.Bubbling.fire("doSearch",
                                    {
                                        searchSort:datagridMeta.searchConfig.sort,
                                        searchQuery:YAHOO.lang.JSON.stringify(initialData),
                                        searchFilter:filter,
                                        fullTextSearch:fullText,
                                        bubblingLabel:me.options.bubblingLabel
                                    });
                                }
                            }
                        }
                    },
                dTable, true);

                // Rendering complete event handler
                dTable.subscribe("renderEvent", function () {
                    Alfresco.logger.debug("DataTable renderEvent");

                    // Deferred functions specified?
                    for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
                        this.afterDataGridUpdate[i].call(this);
                    }
                    this.afterDataGridUpdate = [];
                }, this, true);

                // Enable row highlighting
                dTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
                dTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);

				if (this.options.height != null) {
					YAHOO.util.Dom.setStyle(this.id + "-grid", "height", this.options.height + "px");
				}

				return dTable;
            },
            /**
             * DataTable set-up and event registration
             *
             * @method setupDataTable
             * @protected
             */
            setupDataTable: function DataGrid__setupDataTable(columns)
            {
                // YUI DataTable colum
                var columnDefinitions = this.getDataTableColumnDefinitions();
                // DataTable definition
                var me = this;
                if (!this.widgets.dataTable) {
                    this.widgets.dataTable = this._setupDataTable(columnDefinitions, me);
                    // link current table with search and do search
                    this.modules.search.dataTable = this.widgets.dataTable;
                }
                //complete initial search
                var initialData = {
                    datatype:this.datagridMeta.itemType
                };

                var searchConfig = this.datagridMeta.searchConfig;
                var sorting, filter, fullText;
                if (searchConfig) {
                    filter = searchConfig.filter;
                    sorting = searchConfig.sort != null && searchConfig.sort.length > 0 ? searchConfig.sort : "cm:name|true"; // по умолчанию поиск по свойству cm:name по убыванию
                    fullText = searchConfig.fullTextSearch;
                }
                // trigger the initial search
                YAHOO.Bubbling.fire("doSearch",
                    {
                        searchSort:sorting,
                        searchQuery:YAHOO.lang.JSON.stringify(initialData),
                        searchFilter:filter,
                        fullTextSearch:fullText,
                        searchShowInactive:false,
                        bubblingLabel:me.options.bubblingLabel
                    });
            },

			/**
			 * Добавляет меню для колонок
			 */
			setupActions: function() {
				if (this.options.actions != null) {
					var actionsDiv = document.getElementById(this.id + "-actionSet");
					for (var i = 0; i < this.options.actions.length; i++) {
						var action = this.options.actions[i];

						var actionDiv = document.createElement("div");
						actionDiv.className = action.id;

						var actionA = document.createElement("a");
						actionA.rel = action.permission;
						actionA.className = "action-link " + action.type;
						actionA.title = action.label;

						var actionSpan = document.createElement("span");
						actionSpan.innerHTML = action.label;

						actionA.appendChild(actionSpan);
						actionDiv.appendChild(actionA);
						actionsDiv.appendChild(actionDiv);
					}
				}
			},

            /**
             * Выбор всех значений
             * @constructor
             */
            selectAllClick: function DataGrid_selectAllClick() {
                var selectAllElement = Dom.get("select-all-records");
                if (selectAllElement.checked) {
                    this.selectItems("selectAll");
                } else {
                    this.selectItems("selectNone");
                }
            },

            /**
             * Custom event handler to highlight row.
             *
             * @method onEventHighlightRow
             * @param oArgs.event {HTMLEvent} Event object.
             * @param oArgs.target {HTMLElement} Target element.
             */
            onEventHighlightRow: function DataGrid_onEventHighlightRow(oArgs)
            {
                // elActions is the element id of the active table cell where we'll inject the actions
                var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

                // Inject the correct action elements into the actionsId element
                if (elActions && elActions.firstChild === null)
                {
                    // Call through to get the row highlighted by YUI
                    this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

                    // Clone the actionSet template node from the DOM
                    var record = this.widgets.dataTable.getRecord(oArgs.target.id),
                        clone = Dom.get(this.id + "-actionSet").cloneNode(true);

                    // Token replacement
                    clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record));

                    // Generate an id
                    clone.id = elActions.id + "_a";

                    // Simple view by default
                    Dom.addClass(clone, "simple");

                    // Trim the items in the clone depending on the user's access
                    var userAccess = record.getData("permissions").userAccess,
                        actionLabels = record.getData("actionLabels") || {};

                    // Remove any actions the user doesn't have permission for
                    var actions = YAHOO.util.Selector.query("div", clone),
                        action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;
                    if (actions.length > 3) {
                        this.options.splitActionsAt = 2;
                    } else {
                        this.options.splitActionsAt = 3;
                    }
                    for (i = 0, ii = actions.length; i < ii; i++)
                    {
                        action = actions[i];
                        aTag = action.firstChild;
                        spanTag = aTag.firstChild;
                        if (spanTag && actionLabels[action.className])
                        {
                            spanTag.innerHTML = $html(actionLabels[action.className]);
                        }

                        if (aTag.rel !== "")
                        {
                            actionPermissions = aTag.rel.split(",");
                            for (j = 0, jj = actionPermissions.length; j < jj; j++)
                            {
                                aP = actionPermissions[j];
                                // Support "negative" permissions
                                if ((aP.charAt(0) == "~") ? !!userAccess[aP.substring(1)] : !userAccess[aP])
                                {
                                    clone.removeChild(action);
                                    break;
                                }
                                if (!this.versionable && (action.attributes[0].nodeValue == "onActionVersion")){
                                    clone.removeChild(action);
                                }
                            }
                        }
                    }

                    // Need the "More >" container?
                    var splitAt = this.options.splitActionsAt;
                    actions = YAHOO.util.Selector.query("div", clone);
                    if (actions.length > 3)
                    {
                        var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true);
                        var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
                        // Insert the two necessary DIVs before the splitAt action item
                        Dom.insertBefore(containerDivs[0], actions[splitAt]);
                        Dom.insertBefore(containerDivs[1], actions[splitAt]);
                        // Now make action items after the split, children of the 2nd DIV
                        var index, moreActions = actions.slice(splitAt);
                        for (index in moreActions)
                        {
                            if (moreActions.hasOwnProperty(index))
                            {
                                containerDivs[1].appendChild(moreActions[index]);
                            }
                        }
                    }

                    elActions.appendChild(clone);
                }

                if (this.showingMoreActions)
                {
                    this.deferredActionsMenu = elActions;
                }
                else
                {
                    this.currentActionsMenu = elActions;
                    // Show the actions
                    Dom.removeClass(elActions, "hidden");
                    this.deferredActionsMenu = null;
                }
            },

            /**
             * Custom event handler to unhighlight row.
             *
             * @method onEventUnhighlightRow
             * @param oArgs.event {HTMLEvent} Event object.
             * @param oArgs.target {HTMLElement} Target element.
             */
            onEventUnhighlightRow: function DataGrid_onEventUnhighlightRow(oArgs)
            {
                // Call through to get the row unhighlighted by YUI
                this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

                var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

                // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
                if (!this.showingMoreActions || Dom.hasClass(document.body, "masked"))
                {
                    // Just hide the action links, rather than removing them from the DOM
                    Dom.addClass(elActions, "hidden");
                    this.deferredActionsMenu = null;
                }
            },

            /**
             * The urls to be used when creating links in the action cell
             *
             * @method getActionUrls
             * @param record {YAHOO.widget.Record | Object} A data record, or object literal describing the item in the list
             * @return {object} Object literal containing URLs to be substituted in action placeholders
             */
            getActionUrls: function DataGrid_getActionUrls(record)
            {
                var recordData = YAHOO.lang.isFunction(record.getData) ? record.getData() : record,
                    nodeRef = recordData.nodeRef;

                return (
                {
                    editMetadataUrl: "edit-dataitem?nodeRef=" + nodeRef
                });
            },

            /**
             * Public function to get array of selected items
             *
             * @method getSelectedItems
             * @return {Array} Currently selected items
             */
            getSelectedItems: function DataGrid_getSelectedItems()
            {
                var items = [],
                    recordSet = this.widgets.dataTable.getRecordSet(),
                    aPageRecords = this.widgets.paginator.getPageRecords(),
                    startRecord = aPageRecords[0],
                    endRecord = aPageRecords[1],
                    record;

                for (var i = startRecord; i <= endRecord; i++)
                {
                    record = recordSet.getRecord(i);
                    if (this.selectedItems[record.getData("nodeRef")])
                    {
                        items.push(record.getData());
                    }
                }

                return items;
            },

            /**
             * Public function to select items by specified groups
             *
             * @method selectItems
             * @param p_selectType {string} Can be one of the following:
             * <pre>
             * selectAll - all items
             * selectNone - deselect all
             * selectInvert - invert selection
             * </pre>
             */
            selectItems: function DataGrid_selectItems(p_selectType)
            {
                var recordSet = this.widgets.dataTable.getRecordSet(),
                    checks = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
                    aPageRecords = this.widgets.paginator.getPageRecords(),
                    startRecord = aPageRecords[0],
                    len = checks.length,
                    record, i, fnCheck;

                switch (p_selectType)
                {
                    case "selectAll":
                        fnCheck = function(assetType, isChecked)
                        {
                            return true;
                        };
                        break;

                    case "selectNone":
                        fnCheck = function(assetType, isChecked)
                        {
                            return false;
                        };
                        break;

                    case "selectInvert":
                        fnCheck = function(assetType, isChecked)
                        {
                            return !isChecked;
                        };
                        break;

                    default:
                        fnCheck = function(assetType, isChecked)
                        {
                            return isChecked;
                        };
                }

                for (i = 0; i < len; i++)
                {
                    record = recordSet.getRecord(i + startRecord);
                    this.selectedItems[record.getData("nodeRef")] = checks[i].checked = fnCheck(record.getData("type"), checks[i].checked);
                }

                Bubbling.fire("selectedItemsChanged");
            },

            /**
             * Current DataList changed event handler
             *
             * @method onGridTypeChanged
             * @param layer {object} Event fired (unused)
             * @param args {array} Event parameters (unused)
             */
            onGridTypeChanged:function DataGrid_onActiveDataListChanged(layer, args) {
                var obj = args[1];
                if ((obj !== null) && (obj.datagridMeta !== null)) {
                    // Если метка не задана, или метки совпадают - дергаем метод
                    var label = obj.bubblingLabel;
                    if(this._hasEventInterest(label)){
                        this.datagridMeta = obj.datagridMeta;
                        this.datagridMeta.bubblingLabel = obj.bubblingLabel;
                        // Could happen more than once, so check return value of fulfil()
                        if (!this.deferredListPopulation.fulfil("onGridTypeChanged")) {
                            this.populateDataGrid();
                        }
                    }
                }
            },

            /**
             * DataGrid Refresh Required event handler
             *
             * @method onDataGridRefresh
             * @param layer {object} Event fired (unused)
             * @param args {array} Event parameters (unused)
             */
            onDataGridRefresh: function DataGrid_onDataGridRefresh(layer, args)
            {
                var obj = args[1];
                if (!obj || this._hasEventInterest(obj.bubblingLabel)){
                    this._updateDataGrid.call(this,
                        {
                            page: this.currentPage
                        });
                    Bubbling.fire("itemsListChanged");
                }
            },

            /**
             * Data Item created event handler
             *
             * @method onDataItemCreated
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onDataItemCreated:function DataGrid_onDataItemCreated(layer, args) {
                var obj = args[1];
                if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.nodeRef !== null)) {
                    var nodeRef = new Alfresco.util.NodeRef(obj.nodeRef);
                    // Reload the node's metadata
                    Alfresco.util.Ajax.jsonPost(
                        {
                            url:Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + nodeRef.uri,
                            dataObj:this._buildDataGridParams(),
                            successCallback:{
                                fn:function DataGrid_onDataItemCreated_refreshSuccess(response) {
                                    this.versionable = response.json.versionable;
                                    var item = response.json.item;
                                    var fnAfterUpdate = function DataGrid_onDataItemCreated_refreshSuccess_fnAfterUpdate() {
                                        var recordFound = this._findRecordByParameter(item.nodeRef, "nodeRef");
                                        if (recordFound !== null) {
                                            var el = this.widgets.dataTable.getTrEl(recordFound);
                                            Alfresco.util.Anim.pulse(el);
                                        }
                                    };
                                    this.afterDataGridUpdate.push(fnAfterUpdate);
                                    this.widgets.dataTable.addRow(item);
                                },
                                scope:this
                            },
                            failureCallback:{
                                fn:function DataGrid_onDataItemCreated_refreshFailure(response) {
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:this.msg("message.create.refresh.failure")
                                        });
                                },
                                scope:this
                            }
                        });
                }
            },

            /**
             * Data Item updated event handler
             *
             * @method onDataItemUpdated
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onDataItemUpdated: function DataGrid_onDataItemUpdated(layer, args)
            {
                var obj = args[1];
                if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.item !== null))
                {
                    var recordFound = this._findRecordByParameter(obj.item.nodeRef, "nodeRef");
                    if (recordFound !== null)
                    {
                        this.widgets.dataTable.updateRow(recordFound, obj.item);
                        var el = this.widgets.dataTable.getTrEl(recordFound);
                        Alfresco.util.Anim.pulse(el);
                    }
                }
            },

            /**
             * Data Items deleted event handler
             *
             * @method onDataItemsDeleted
             * @param layer {object} Event fired
             * @param args {array} Event parameters (depends on event type)
             */
            onDataItemsDeleted: function DataGrid_onDataItemsDeleted(layer, args)
            {
                var obj = args[1];
                if (obj && this._hasEventInterest(obj.bubblingLabel) && (obj.items !== null))
                {
                    var recordFound, el,
                        fnCallback = function(record)
                        {
                            return function DataGrid_onDataItemsDeleted_anim()
                            {
                                this.widgets.dataTable.deleteRow(record);
                            };
                        };

                    for (var i = 0, ii = obj.items.length; i < ii; i++)
                    {
                        recordFound = this._findRecordByParameter(obj.items[i].nodeRef, "nodeRef");
                        if (recordFound !== null)
                        {
                            el = this.widgets.dataTable.getTrEl(recordFound);
                            Alfresco.util.Anim.fadeOut(el,
                                {
                                    callback: fnCallback(recordFound),
                                    scope: this
                                });
                        }
                    }
                }
            },

            /**
             * Resets the YUI DataTable errors to our custom messages
             * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
             *
             * @method _setDefaultDataTableErrors
             * @private
             * @param dataTable {object} Instance of the DataTable
             */
            _setDefaultDataTableErrors: function DataGrid__setDefaultDataTableErrors(dataTable)
            {
                var msg = Alfresco.util.message;
                dataTable.set("MSG_EMPTY", msg("message.empty", "LogicECM.module.Base.DataGrid"));
                dataTable.set("MSG_ERROR", msg("message.error", "LogicECM.module.Base.DataGrid"));
            },

            /**
             * Updates all Data Grid data by calling repository webscript with current list details
             *
             * @method _updateDataGrid
             * @private
             * @param p_obj.filter {object} Optional filter to navigate with
             */
            _updateDataGrid: function DataGrid__updateDataGrid(p_obj)
            {
                p_obj = p_obj || {};
                Alfresco.logger.debug("DataGrid__updateDataGrid: ", p_obj.filter);
                var successFilter = YAHOO.lang.merge({}, p_obj.filter !== undefined ? p_obj.filter : {}),
                    loadingMessage = null,
                    timerShowLoadingMessage = null,
                    me = this,
                    params =
                    {
                        filter: successFilter
                    };

                // Clear the current document list if the data webscript is taking too long
                var fnShowLoadingMessage = function DataGrid_fnShowLoadingMessage()
                {
                    Alfresco.logger.debug("DataGrid__uDG_fnShowLoadingMessage: slow data webscript detected.");
                    // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
                    if (timerShowLoadingMessage)
                    {
                        loadingMessage = Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime: 0,
                                text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                                noEscape: true
                            });

                        if (YAHOO.env.ua.ie > 0)
                        {
                            this.loadingMessageShowing = true;
                        }
                        else
                        {
                            loadingMessage.showEvent.subscribe(function()
                            {
                                this.loadingMessageShowing = true;
                            }, this, true);
                        }
                    }
                };

                // Reset the custom error messages
                this._setDefaultDataTableErrors(this.widgets.dataTable);

                // More Actions menu no longer relevant
                this.showingMoreActions = false;

                // Slow data webscript message
                this.loadingMessageShowing = false;
                timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);

                var destroyLoaderMessage = function DataGrid__uDG_destroyLoaderMessage()
                {
                    if (timerShowLoadingMessage)
                    {
                        // Stop the "slow loading" timed function
                        timerShowLoadingMessage.cancel();
                        timerShowLoadingMessage = null;
                    }

                    if (loadingMessage)
                    {
                        if (this.loadingMessageShowing)
                        {
                            // Safe to destroy
                            loadingMessage.destroy();
                            loadingMessage = null;
                        }
                        else
                        {
                            // Wait and try again later. Scope doesn't get set correctly with "this"
                            YAHOO.lang.later(100, me, destroyLoaderMessage);
                        }
                    }
                };

                var successHandler = function DataGrid__uDG_successHandler(sRequest, oResponse, oPayload)
                {
                    destroyLoaderMessage();
                    // Updating the DotaGrid may change the item selection
                    var fnAfterUpdate = function DataGrid__uDG_sH_fnAfterUpdate()
                    {
                        Bubbling.fire("selectedFilesChanged");
                    };
                    this.afterDataGridUpdate.push(fnAfterUpdate);
                    this.currentPage = p_obj.page || 1;
                    Bubbling.fire("filterChanged", successFilter);
                    this.widgets.dataTable.onDataReturnReplaceRows.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                };

                var failureHandler = function DataGrid__uDG_failureHandler(sRequest, oResponse)
                {
                    destroyLoaderMessage();
                    // Clear out deferred functions
                    this.afterDataGridUpdate = [];

                    if (oResponse.status == 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload(true);
                    }
                    else
                    {
                        try
                        {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            this.widgets.dataTable.set("MSG_ERROR", response.message);
                            this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                            if (oResponse.status == 404)
                            {
                                // Site or container not found - deactivate controls
                                Bubbling.fire("deactivateAllControls");
                            }
                        }
                        catch(e)
                        {
                            this._setDefaultDataTableErrors(this.widgets.dataTable);
                        }
                    }
                };

                //complete initial search
                var initialData = {
                    datatype:this.datagridMeta.itemType
                };

                var searchConfig = this.datagridMeta.searchConfig;
                var sorting, filter, fullText;
                if (searchConfig) {
                    filter = searchConfig.filter;
                    sorting = searchConfig.sort != null ? searchConfig.sort : "cm:name|true"; // по умолчанию поиск по свойству cm:name по убыванию
                    fullText = searchConfig.fullTextSearch;
                }
                // Update the DataSource
                var requestParams = this.modules.search._buildSearchParams(YAHOO.lang.JSON.stringify(initialData), filter, sorting, this.dataRequestFields.join(","), fullText);
                this.widgets.dataSource.sendRequest(YAHOO.lang.JSON.stringify(requestParams),
                    {
                        success:successHandler,
                        failure:failureHandler,
                        scope:this
                    });
            },

            /**
             * Build URI parameter string for doclist JSON data webscript
             *
             * @method _buildDataGridParams
             * @param p_obj.filter {string} [Optional] Current filter
             * @return {Object} Request parameters. Can be given directly to Alfresco.util.Ajax, but must be JSON.stringified elsewhere.
             */
            _buildDataGridParams: function DataGrid__buildDataGridParams(p_obj)
            {
                var request =
                {
                    fields: this.dataRequestFields
                };

                if (p_obj && p_obj.filter)
                {
                    request.filter =
                    {
                        filterId: p_obj.filter.filterId,
                        filterData: p_obj.filter.filterData
                    };
                }

                return request;
            },

            /**
             * Searches the current recordSet for a record with the given parameter value
             *
             * @method _findRecordByParameter
             * @private
             * @param p_value {string} Value to find
             * @param p_parameter {string} Parameter to look for the value in
             */
            _findRecordByParameter: function DataGrid__findRecordByParameter(p_value, p_parameter)
            {
                var recordSet = this.widgets.dataTable.getRecordSet();
                Bubbling.fire("itemsListChanged");
                for (var i = 0, j = recordSet.getLength(); i < j; i++)
                {
                    if (recordSet.getRecord(i).getData(p_parameter) == p_value)
                    {
                        return recordSet.getRecord(i);
                    }
                }
                return null;
            },

            _hasEventInterest: function DataGrid_hasEventInterest(bubbleLabel){
                if (!this.options.bubblingLabel || !bubbleLabel) {
                    return true;
                } else {
                    return this.options.bubblingLabel == bubbleLabel;
                }
            },
            //Действия по умолчанию. В конкретных реализациях ДатаГрида эти методы при необходимости следует переопределять
            /**
             * Delete item(s).
             *
             * @method onActionDelete
             * @param p_items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
             * @param owner {Object} не используется Dom-объект
             * @param actionsConfig {Object} Объект с настройками для экшена
             * @param fnDeleteComplete {Object} CallBack, который вызовется после завершения удаления
             */
            onActionDelete:function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
                this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete);
            },

            /**
                Удаление элемента. onActionDelete дергает этот метод.
                Вынесено в отдельный метод, чтобы в конкретных датагридах не копировать
                код и иметь возможность навешивать доп проверки
             */
            onDelete: function DataGridActions_onDelete(p_items, owner, actionsConfig, fnDeleteComplete){
                var me = this,
                    items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

                var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items) {
                    var nodeRefs = [];
                    for (var i = 0, ii = items.length; i < ii; i++) {
                        nodeRefs.push(items[i].nodeRef);
                    }
                    var query = "";
                    if (actionsConfig) {
                        var fullDelete = actionsConfig.fullDelete;
                        if (fullDelete != null) {
                            query = query + "full=" + fullDelete;
                        }
                    }
                    this.modules.actions.genericAction(
                        {
                            success:{
                                event:{
                                    name:"dataItemsDeleted",
                                    obj:{
                                        items:items,
                                        bubblingLabel:me.options.bubblingLabel
                                    }
                                },
                                message:this.msg("message.delete.success", items.length),
                                callback:{
                                    fn:fnDeleteComplete
                                }
                            },
                            failure:{
                                message:this.msg("message.delete.failure")
                            },
                            webscript:{
                                method:Alfresco.util.Ajax.DELETE,
                                name:"delete",
                                queryString:query
                            },
                            config:{
                                requestContentType:Alfresco.util.Ajax.JSON,
                                dataObj:{
                                    nodeRefs:nodeRefs
                                }
                            }
                        });
                };

                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title:this.msg("message.confirm.delete.title", items.length),
                        text:this.msg("message.confirm.delete.description", items.length),
                        buttons:[
                            {
                                text:this.msg("button.delete"),
                                handler:function DataGridActions__onActionDelete_delete() {
                                    this.destroy();
                                    fnActionDeleteConfirm.call(me, items);
                                }
                            },
                            {
                                text:this.msg("button.cancel"),
                                handler:function DataGridActions__onActionDelete_cancel() {
                                    this.destroy();
                                },
                                isDefault:true
                            }
                        ]
                    });
            },
            /**
             * Продублировать item(s).
             *
             * @method onActionDuplicate
             * @param p_items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
             */
            onActionDuplicate:function DataListActions_onActionDuplicate(p_items) {
                var me = this;
                var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items],
                    destinationNodeRef = new Alfresco.util.NodeRef(this.modules.dataGrid.datagridMeta.nodeRef),
                    nodeRefs = [];

                for (var i = 0, ii = items.length; i < ii; i++) {
                    nodeRefs.push(items[i].nodeRef);
                }

                this.modules.actions.genericAction(
                    {
                        success:{
                            event:{
                                name:"dataItemsDuplicated",
                                obj:{
                                    items:items,
                                    bubblingLabel:me.options.bubblingLabel
                                }
                            },
                            message:this.msg("message.duplicate.success", items.length)
                        },
                        failure:{
                            message:this.msg("message.duplicate.failure")
                        },
                        webscript:{
                            method:Alfresco.util.Ajax.POST,
                            name:"duplicate/node/" + destinationNodeRef.uri
                        },
                        config:{
                            requestContentType:Alfresco.util.Ajax.JSON,
                            dataObj:{
                                nodeRefs:nodeRefs
                            }
                        }
                    });
            },

            /**
             * Получение списка версий и вывод диалогового окна для просмотра
             * @param item {object} выбранный элемент
             */
            doShowVersions:function (item) {
                var sUrl = Alfresco.constants.PROXY_URI + "api/version?nodeRef=" + item.nodeRef;

                var callback = {
                    success:function (oRequest) {
                        var oResults = eval("(" + oRequest.responseText + ")");
                        this.versionCache = oResults;
                        this.latestVersion = oResults.splice(0, 1)[0];
                        this.onViewHistoricPropertiesClick(item.nodeRef);
                    }.bind(this),
                    failure:function (oRequest) {
                        alert("Failed to load version data. " + "[" + oRequest.statusText + "]");
                    },
                    argument:{
                    }
                };

                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
            },
            /**
             * Просмотр версий
             * @param item {object} выбранный элемент
             */
            onActionVersion:function (item) {
                this.doShowVersions(item);
            },

            /**
             * Установка параметров диалогового окна просмотра версий
             * @param nodeRef {string} ссылка на узел
             * @constructor
             */
            onViewHistoricPropertiesClick:function DocumentVersions_onViewHistoricPropertiesClick(nodeRef) {
                // Call the Hictoric Properties Viewer Module
                Alfresco.module.getHistoricPropertiesViewerInstance().show(
                    {
                        filename:this.latestVersion.name,
                        currentNodeRef:nodeRef,
                        latestVersion:this.latestVersion,
                        nodeRef:this.latestVersion.nodeRef
                    });
            },

            /**
             * Edit Data Item pop-up
             *
             * @method onActionEdit
             * @param item {object} Object literal representing one data item
             */
            onActionEdit:function DataGrid_onActionEdit(item) {
                var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-dialogTitle", this.msg("label.edit-row.title") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
                    {
                        itemKind:"node",
                        itemId:item.nodeRef,
                        mode:"edit",
                        submitType:"json"
                    });

                // Using Forms Service, so always create new instance
                var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
                editDetails.setOptions(
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
                            fn:function DataGrid_onActionEdit_success(response) {
                                // Reload the node's metadata
                                Alfresco.util.Ajax.jsonPost(
                                    {
                                        url:Alfresco.constants.PROXY_URI + "slingshot/datalists/item/node/" + new Alfresco.util.NodeRef(item.nodeRef).uri,
                                        dataObj:this._buildDataGridParams(),
                                        successCallback:{
                                            fn:function DataGrid_onActionEdit_refreshSuccess(response) {
                                                // Fire "itemUpdated" event
                                                Bubbling.fire("dataItemUpdated",
                                                    {
                                                        item:response.json.item,
                                                        bubblingLabel:me.options.bubblingLabel
                                                    });
                                                // Display success message
                                                Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text:this.msg("message.details.success")
                                                    });
                                            },
                                            scope:this
                                        },
                                        failureCallback:{
                                            fn:function DataGrid_onActionEdit_refreshFailure(response) {
                                                Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text:this.msg("message.details.failure")
                                                    });
                                            },
                                            scope:this
                                        }
                                    });
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataGrid_onActionEdit_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.details.failure")
                                    });
                            },
                            scope:this
                        }
                    }).show();
            },

			/**
			 * Create Data Item pop-up
			 *
			 * @method onActionCreate
			 */
			onActionCreate:function DataGrid_onActionCreate() {
				var me = this;
				// Intercept before dialog show
				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-dialogTitle", this.msg("label.edit-row.title") ]
					);
				};

				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
					{
						itemKind: "type",
						itemId: this.datagridMeta.itemType,
						destination: this.datagridMeta.nodeRef,
						mode:"create",
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
									YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
										{
											nodeRef:response.json.persistedObject,
											bubblingLabel: this.options.bubblingLabel
										});
									Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.details.success")
										});
							},
							scope:this
						},
						onFailure:{
							fn:function DataGrid_onActionCreate_failure(response) {
								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.details.failure")
									});
							},
							scope:this
						}
					}).show();
			},

            /**
             * Show more actions pop-up.
             *
             * @method onActionShowMore
             * @param record {object} Object literal representing file or folder to be actioned
             * @param elMore {element} DOM Element of "More Actions" link
             */
            onActionShowMore:function DL_onActionShowMore(record, elMore) {
                var me = this;

                // Fix "More Actions" hover style
                Dom.addClass(elMore.firstChild, "highlighted");

                // Get the pop-up div, sibling of the "More Actions" link
                var elMoreActions = Dom.getNextSibling(elMore);
                Dom.removeClass(elMoreActions, "hidden");
                me.showingMoreActions = true;

                // Hide pop-up timer function
                var fnHidePopup = function DL_oASM_fnHidePopup() {
                    // Need to rely on the "elMoreActions" enclosed variable, as MSIE doesn't support
                    // parameter passing for timer functions.
                    Event.removeListener(elMoreActions, "mouseover");
                    Event.removeListener(elMoreActions, "mouseout");
                    Dom.removeClass(elMore.firstChild, "highlighted");
                    Dom.addClass(elMoreActions, "hidden");
                    me.showingMoreActions = false;
                    if (me.deferredActionsMenu !== null) {
                        Dom.addClass(me.currentActionsMenu, "hidden");
                        me.currentActionsMenu = me.deferredActionsMenu;
                        me.deferredActionsMenu = null;
                        Dom.removeClass(me.currentActionsMenu, "hidden");
                    }
                };

                // Initial after-click hide timer - 4x the mouseOut timer delay
                if (elMoreActions.hideTimerId) {
                    window.clearTimeout(elMoreActions.hideTimerId);
                }
                elMoreActions.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout * 4);

                // Mouse over handler
                var onMouseOver = function DLSM_onMouseOver(e, obj) {
                    // Clear any existing hide timer
                    if (obj.hideTimerId) {
                        window.clearTimeout(obj.hideTimerId);
                        obj.hideTimerId = null;
                    }
                };

                // Mouse out handler
                var onMouseOut = function DLSM_onMouseOut(e, obj) {
                    var elTarget = Event.getTarget(e);
                    var related = elTarget.relatedTarget;

                    // In some cases we should ignore this mouseout event
                    if ((related !== obj) && (!Dom.isAncestor(obj, related))) {
                        if (obj.hideTimerId) {
                            window.clearTimeout(obj.hideTimerId);
                        }
                        obj.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout);
                    }
                };

                Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
                Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
            }
        }, true);
})();

/**
 * Модуль для обработки экшенов в датагриде. ПОдключается непосредственно в датагрид
 */
(function () {
    LogicECM.module.Base.Actions = function () {
        this.name = "LogicECM.module.Base.Actions";

        /* Load YUI Components */
        Alfresco.util.YUILoaderHelper.require(["json"], this.onComponentsLoaded, this);

        return this;
    };

    LogicECM.module.Base.Actions.prototype =
    {
        /**
         * Flag indicating whether module is ready to be used.
         * Flag is set when all YUI component dependencies have loaded.
         *
         * @property isReady
         * @type boolean
         */
        isReady:false,

        /**
         * Object literal for default AJAX request configuration
         *
         * @property defaultConfig
         * @type object
         */
        defaultConfig:{
            method:"POST",
            urlStem:Alfresco.constants.PROXY_URI + "lecm/base/action/",
            dataObj:null,
            successCallback:null,
            successMessage:null,
            failureCallback:null,
            failureMessage:null,
            object:null
        },

        /**
         * Fired by YUILoaderHelper when required component script files have
         * been loaded into the browser.
         *
         * @method onComponentsLoaded
         */
        onComponentsLoaded:function DLA_onComponentsLoaded() {
            this.isReady = true;
        },

        /**
         * Make AJAX request to data webscript
         *
         * @method _runAction
         * @private
         * @return {boolean} false: module not ready for use
         */
        _runAction:function DLA__runAction(config, obj) {
            // Check components loaded
            if (!this.isReady) {
                return false;
            }

            // Merge-in any supplied object
            if (typeof obj == "object") {
                config = YAHOO.lang.merge(config, obj);
            }

            if (config.method == Alfresco.util.Ajax.DELETE) {
                if (config.dataObj !== null) {
                    // Change this request into a POST with the alf_method override
                    config.method = Alfresco.util.Ajax.POST;
                    if (config.url.indexOf("alf_method") < 1) {
                        config.url += (config.url.indexOf("?") < 0 ? "?" : "&") + "alf_method=delete";
                    }
                    Alfresco.util.Ajax.jsonRequest(config);
                }
                else {
                    Alfresco.util.Ajax.request(config);
                }
            }
            else {
                Alfresco.util.Ajax.jsonRequest(config);
            }
        },


        /**
         * ACTION: Generic action.
         * Generic DataList action based on passed-in parameters
         *
         * @method genericAction
         * @param action.success.event.name {string} Bubbling event to fire on success
         * @param action.success.event.obj {object} Bubbling event success parameter object
         * @param action.success.message {string} Timed message to display on success
         * @param action.success.callback.fn {object} Callback function to call on success.
         * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
         * @param action.success.callback.scope {object} Success callback function scope
         * @param action.success.callback.obj {object} Success callback function object passed to callback
         * @param action.failure.event.name {string} Bubbling event to fire on failure
         * @param action.failure.event.obj {object} Bubbling event failure parameter object
         * @param action.failure.message {string} Timed message to display on failure
         * @param action.failure.callback.fn {object} Callback function to call on failure.
         * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
         * @param action.failure.callback.scope {object} Failure callback function scope
         * @param action.failure.callback.obj {object} Failure callback function object passed to callback
         * @param action.webscript.stem {string} optional webscript URL stem
         * <pre>default: Alfresco.constants.PROXY_URI + "slingshot/datalists/action/"</pre>
         * @param action.webscript.name {string} data webscript URL name
         * @param action.webscript.method {string} HTTP method to call the data webscript on
         * @param action.webscript.queryString {string} Optional queryString to append to the webscript URL
         * @param action.webscript.params.nodeRef {string} nodeRef of target item
         * @param action.wait.message {string} if set, show a Please wait-style message during the operation
         * @param action.config {object} optional additional request configuration overrides
         * @return {boolean} false: module not ready
         */
        genericAction:function DataGridActions_genericAction(action) {
            var success = action.success,
                failure = action.failure,
                webscript = action.webscript,
                params = action.params ? action.params : action.webscript.params,
                overrideConfig = action.config,
                wait = action.wait,
                configObj = null;

            var fnCallback = function DataGridActions_genericAction_callback(data, obj) {
                // Check for notification event
                if (obj) {
                    // Event(s) specified?
                    if (obj.event && obj.event.name) {
                        YAHOO.Bubbling.fire(obj.event.name, obj.event.obj);
                    }
                    if (YAHOO.lang.isArray(obj.events)) {
                        for (var i = 0, ii = obj.events.length; i < ii; i++) {
                            YAHOO.Bubbling.fire(obj.events[i].name, obj.events[i].obj);
                        }
                    }

                    // Please wait pop-up active?
                    if (obj.popup) {
                        obj.popup.destroy();
                    }
                    // Message?
                    if (obj.message) {
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                text:obj.message
                            });
                    }
                    // Callback function specified?
                    if (obj.callback && obj.callback.fn) {
                        obj.callback.fn.call((typeof obj.callback.scope == "object" ? obj.callback.scope : this),
                            {
                                config:data.config,
                                json:data.json,
                                serverResponse:data.serverResponse
                            }, obj.callback.obj);
                    }
                }
            };

            // Please Wait... message pop-up?
            if (wait && wait.message) {
                if (typeof success != "object") {
                    success = {};
                }
                if (typeof failure != "object") {
                    failure = {};
                }

                success.popup = Alfresco.util.PopupManager.displayMessage(
                    {
                        modal:true,
                        displayTime:0,
                        text:wait.message,
                        effect:null
                    });
                failure.popup = success.popup;
            }

            var url;
            if (webscript.stem) {
                url = webscript.stem + webscript.name;
            }
            else {
                url = this.defaultConfig.urlStem + webscript.name;
            }

            if (params) {
                url = YAHOO.lang.substitute(url, params);
                configObj = params;
            }
            if (webscript.queryString && webscript.queryString != "") {
                url += "?" + webscript.queryString;
            }

            var config = YAHOO.lang.merge(this.defaultConfig,
                {
                    successCallback:{
                        fn:fnCallback,
                        scope:this,
                        obj:success
                    },
                    successMessage:null,
                    failureCallback:{
                        fn:fnCallback,
                        scope:this,
                        obj:failure
                    },
                    failureMessage:null,
                    url:url,
                    method:webscript.method,
                    responseContentType:Alfresco.util.Ajax.JSON,
                    object:configObj
                });

            return this._runAction(config, overrideConfig);
        }
    };

    /* Dummy instance to load optional YUI components early */
    var dummyInstance = new LogicECM.module.Base.Actions();
})();
