/* global YAHOO, Alfresco */

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
    LogicECM = {};
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
        LogicECM.module.Base.DataGrid.superclass.constructor.call(this, "LogicECM.module.Base.DataGrid_" + htmlId, htmlId, ["button", "container", "datasource", "datatable", "paginator", "animation", "history"]);

        // Initialise prototype properties
        this.datagridMeta = {};
        this.datagridColumns = {};
        this.dataRequestFields = [];
        this.dataResponseFields = [];
        this.totalRecords = 0;
        this.showingMoreActions = false;
        this.selectedItems = {};
        this.afterDataGridUpdate = [];
        this.search = null;
        this.initialSearchConfig = null;
        this.currentFilters = [];

        /**
         * Decoupled event listeners
         */
        Bubbling.on("activeGridChanged", this.onGridTypeChanged, this);
        Bubbling.on("dataItemCreated", this.onDataItemCreated, this);
        Bubbling.on("dataItemUpdated", this.onDataItemUpdated, this);
        Bubbling.on("dataItemsDeleted", this.onDataItemsDeleted, this);
        Bubbling.on("datagridRefresh", this.onDataGridRefresh, this);
        Bubbling.on("archiveCheckBoxClicked", this.onArchiveCheckBoxClicked, this);
        Bubbling.on("reCreateDatagrid", this.onReCreateDatagrid, this);

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
                unlimited: false,
                useExtPaginationMode: true,

                disableDynamicPagination: false,

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
				allowCreate: false,

				/**
				 * Отображать или скрывать столбец с checkbox-ами
				 * по-умолчанию - отображать
				 */
				showCheckboxColumn: true,

                /**
                 * Отображать или скрывать столбец с action-ами
                 * по-умолчанию - отображать
                 */
                showActionColumn: true,

				/**
				 * Отображать или скрывать те результаты для которых lecm-dic:active == false
				 * По-умолчанию скрывать
				 */
				searchShowInactive:false,

	            /**
	             * Атрибут для ссылки на форму view
	             */
	            attributeForShow: null,

                /**
                 * Отображать или скрывать столбец с action-ами
                 * по-умолчанию - отображать
                 */
                dataSource:"lecm/search",
                /**
                 * Максимальное число возвращаемых запросом элементов
                 */
                maxResults: 1000,

                /**
                 * Число элементов для подгрузки при скроллинге
                 * Используется при отключенном paging при создании объекта AdvancedSearch
                 */
                loopSize: 50,

				/**
				 * идентификатор формы редактирования из share-config-custom
				 * по-умолчанию он не задан и при редактировании записи таблицы будет использоваться форма по-умолчанию
				 */
				editForm: null,

	            /**
	             * идентификатор формы расширенного поиска
	             */
	            advSearchFormId: "searchBlock-forms",

                /**
                 * Альтернативный адрес для конфигурирования таблицы
                 */
                configURL: null,

                /**
                 * Запрос Datasource с сервера
                 */
                repoDatasource: true,

                /**
                 * Форсировать ли подписку для новых datagrid'ов
                 * Должно быть true, если за один жизненный цикл страницы на неё могут быть добавлены/удалены несколько
                 * datagrid'ов.
                 *
                 * @property forceSubscribing
                 * @type boolean
                 * @default false
                 */
                forceSubscribing: true,

                /**
                 * Переопределить сортировку с указанным значением
                 * Если не равно null, то все столбцы таблицы получат значение этого свойства в атрибут сортировки
                 *
                 * @property overrideSortingWith
                 * @type boolean
                 * @default null
                 */
                overrideSortingWith: null,

                /**
                 * Колонки, которые не следует показывать
                 */
                excludeColumns: [],

                /**
                 * Колонки, содержимое которых не следует разбивать на строки
                 */
                nowrapColumns: [],

	            useCookieForSort: true,

	            editFormWidth: "60em",

                refreshAfterCreate: false,

	            editFormTitleMsg: "label.edit-row.title",

	            createFormTitleMsg: "label.create-row.title",

	            viewFormTitleMsg: "logicecm.view",

                    createItemBtnMsg: null,

	            expandable: false,

	            expandDataSource: "components/form",

				expandDataObj: {},

                /**
                 * Имя атрибута для открытия документа
                 */
                attributeForOpen: null
            },

            showActionsCount: 3,
            splitActionsAtStore: 3,

            currentFilters: [],

            /**
             * Total number of records (documents + folders) in the currentPath.
             *
             * @property totalRecords
             * @type int
             * @default 0
             */
            totalRecords: 0,

            /**
             * Object literal of selected states for visible items (indexed by nodeRef).
             *
             * @property selectedItems
             * @type object
             */
            selectedItems: null,

            /**
             * Фиксировать ли заголовок таблицы
             */
            fixedHeader: false,

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
             * Fields name substitute strings
             *
             * @param dataRequestFields
             * @type Object
             */
            dataRequestNameSubstituteStrings: null,

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

            search: null, //Объект, отвечающий за заполнение датагрида

            initialSearchConfig: null,

            errorMessageDialog: null,

	        doubleClickLock: false,

            onArchiveCheckBoxClicked: function (layer, args) {
                var cbShowArchive = YAHOO.util.Dom.get(this.id + "-cbShowArchive");
                if (cbShowArchive) {
                    var obj = {
                        datagridMeta: this.datagridMeta
                    };
                    if (cbShowArchive.checked) {
                        this.options.searchShowInactive = true;
                    } else {
                        this.options.searchShowInactive = false;
                    }
                    YAHOO.Bubbling.fire("activeGridChanged", obj);
                }
            },
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

	        fnRenderCellExpand: function () {
		        var scope = this;

		        return function (elCell, oRecord, oColumn, oData)
		        {
			        Dom.setStyle(elCell, "width", oColumn.width + "px");
			        Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

			        elCell.innerHTML = '<span id="expand-' + oRecord.getId() + '" class="expand-table-icon">+</span>';

			        YAHOO.util.Event.onAvailable("expand-" + oRecord.getId(), function () {
                        //Проверка нужна, т.к. при перемещении строк есть шанс добавить ещё один листенер.
                        if (YAHOO.util.Event.getListeners ("expand-" + oRecord.getId(), 'click')==null) {
                            YAHOO.util.Event.on("expand-" + oRecord.getId(), 'click', scope.onExpandClick, oRecord, this);
                        }
			        }, null, scope);
		        };
	        },

			onExpandClick: function(e, record) {
				var row = this.widgets.dataTable.getRow(record);
				if (Dom.hasClass(row, "expanded")) {
					Dom.get("expand-" + record.getId()).innerHTML = "+";
					Dom.removeClass(row, "expanded");
					this.onCollapse(record);
				} else {
					Dom.addClass(row, "expanded");
					Dom.get("expand-" + record.getId()).innerHTML = "-";
					var rowId = this.getExpandedRecordId(record);
					if (Dom.get(rowId) != null) {
						Dom.setStyle(rowId, "display", "table-row");
					} else {
						this.prepareExpandedRow(record);
						this.onExpand(record);
					}
				}
			},

	        onCollapse: function (record) {
		        //Можно переопределять для полного удаления схлопываемой строки
//		        var expandedRow = Dom.get(this.getExpandedRecordId(record));
//		        LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
//		        expandedRow.parentNode.removeChild(expandedRow);

		        //Строка не удаляется, а скрывается
		        Dom.setStyle(this.getExpandedRecordId(record), "display", "none");
	        },

            collapseAll: function collapseAll_function() {
                var records = this.widgets.dataTable.getRecordSet().getRecords();
                for (var index in records) {
                    var record = records[index];
                    var row = this.widgets.dataTable.getRow(record);
                    if (Dom.hasClass(row, "expanded")) {
                        Dom.get("expand-" + record.getId()).innerHTML = "+";
                        Dom.removeClass(row, "expanded");
                        this.onCollapse(record);
                    }
                }
            },

			onExpand: function(record) {
				if (this.doubleClickLock) return;
				this.doubleClickLock = true;

				var nodeRef = record.getData("nodeRef");
				if (nodeRef) {
					var me = this;
					var dataObj = YAHOO.lang.merge({
						htmlid: this.getExpandedFormId(record),
						itemKind: "node",
						itemId: nodeRef,
						mode: "view"
					}, this.options.expandDataObj);
					Alfresco.util.Ajax.request({
						url: Alfresco.constants.URL_SERVICECONTEXT + this.options.expandDataSource,
						dataObj: dataObj,
						successCallback: {
							scope: this,
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

            prepareExpandedRow: function(record) {
                var row = this.widgets.dataTable.getRow(record);

                var newRow = document.createElement('tr');
                newRow.className = "expand-row";
                newRow.id = this.getExpandedRecordId(record);
                newRow.style.display="none";
                Dom.insertAfter(newRow, row);
            },

	        addExpandedRow: function(record, text) {
		        var colSpan = this.datagridColumns.length;
		        if (this.options.showCheckboxColumn) {
			        colSpan++;
		        }
		        if (this.options.expandable) {
			        colSpan++;
		        }
		        if (this.options.showActionColumn) {
			        colSpan++;
		        }

		        var newRow = Dom.get(this.getExpandedRecordId(record));

                var newColumn = document.createElement('td');
                newColumn.colSpan = colSpan;
                newColumn.innerHTML = text;
                newRow.appendChild(newColumn);
                newRow.style.display="";
	        },

	        getExpandedRecordId: function(record) {
		        return record.getId() + "-expanded";
	        },

	        getExpandedFormId: function(record) {
		        return this.id + record.getData("nodeRef");
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
				this.deferredListPopulation.fulfil("onGridTypeChanged");
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
                 * Data Type formatter
                 *
                 * @method renderCellDataType
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
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
                                            case "checkboxtable":
                                                columnContent += "<div class='centered'><input type='checkbox'" + (data.displayValue == "true" ? " checked='checked'" : "") + " onClick='changeFieldState(this, \"" + data.value + "\")' /></div>"; //data.displayValue;
                                                break;
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
                                                columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                                break;

                                            case "date":
                                                columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                                break;

                                            case "text":
												var hexColorPattern = /^#[0-9a-f]{6}$/i;
                                                if (data.displayValue.indexOf("!html ") == 0) {
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
                                                    columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
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
                                                            columnContent += '<div class="centered">';
                                                            columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                            columnContent += '</div>';
                                                        }
                                                    }
                                                }
                                                break;
                                        }

                                        if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
                                            html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'"+ oRecord.getData("nodeRef") + "\', title:\'" + scope.options.viewFormTitleMsg+"\'})\">" + columnContent + "</a>";
                                        } else if (scope.options.attributeForOpen != null && datalistColumn.name == scope.options.attributeForOpen) {
                                            html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
                                        } else {
                                            html += columnContent;
                                        }

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

                    if (oRecord && oRecord.getData("itemData")){
                        if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
                            elCell.className += " archive-record";
                        }
                    }
                    elCell.innerHTML = html;
                };
            },

	        getRowFormater: function () {
		        var scope = this;

		        return function (elTr, oRecord) {
			        return true;
		        }
	        },

	/**
             * Настраиваемый formatter. Следует при необходимости переопределять именно этот метод, а не getCellFormatter в дочерних гридах
             *
             * @method renderCellDataType
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            getCustomCellFormatter: function DataGrid_customRenderCellDataType(grid,elCell,oRecord,oColumn,oData){
                return null;
            },

	        addFooter: function DataGrid_getCustomAddFooter(){
		        return null;
	        },

	        getEmployeeViewByLink: function DataGrid_getEmployeeViewByLink(employeeNodeRef, displayValue) {
		        if (displayValue.length == 0) {
			        return "";
		        }
		        return "<span class='person'><a href='javascript:void(0);'"+" onclick=\"LogicECM.module.Base.Util.showEmployeeViewByLink(\'" + employeeNodeRef + "\', \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
	        },

	        getEmployeeView: function DataGrid_getEmployeeView(employeeNodeRef, displayValue) {
		        if (displayValue.length == 0) {
			        return "";
		        }
                return "<span class='person'><a href='javascript:void(0);'"+" onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'"+ employeeNodeRef + "\', title: \'logicecm.employee.view\'})\">" + displayValue + "</a></span>";
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
                if (this.options.actions != null && this.options.actions.length > this.showActionsCount) {
                    this.showActionsCount = this.options.splitActionsAt;
                }
                this.splitActionsAtStore = this.options.splitActionsAt;


                if (this.options.showActionColumn){
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

	                                var confirmFunction = null;
	                                if (me.options.actions != null) {
		                                for (var i = 0; i < me.options.actions.length; i++) {
			                            	if (me.options.actions[i].id == owner.className && me.options.actions[i].confirmFunction != null) {
					                            confirmFunction = me.options.actions[i].confirmFunction;
				                            }
		                                }
	                                }

                                    me[owner.className].call(me, asset, owner, me.datagridMeta.actionsConfig, confirmFunction);
                                }
                            }
                        }
                        return true;
                    };
                    Bubbling.addDefaultAction("datagrid-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
                    Bubbling.addDefaultAction("show-more" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler, me.options.forceSubscribing);
                }

                // Actions module
                this.modules.actions = new LogicECM.module.Base.Actions();

                // Reference to Data Grid component (required by actions module)
                this.modules.dataGrid = this;

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
                // DataSource set-up and event registration
                this.setupDataSource();
                // DataTable set-up and event registration
                this.setupDataTable();
				// DataTable actions setup
				this.setupActions();

				if (this.options.allowCreate) {
					var btn = Alfresco.util.createYUIButton(this, "newRowButton", this.onActionCreate.bind(this));
                                        if(this.options.createItemBtnMsg) {
                                            btn.set('label', this.msg(this.options.createItemBtnMsg));
                                        }
					Dom.setStyle(this.id + "-toolbar", "display", "block");
				}

				// Show grid
				Dom.setStyle(this.id + "-body", "visibility", "visible");

                Bubbling.fire("datagridVisible", this);
            },

            _setupPaginatior: function DataGrid_setupPaginatior() {
                if (this.options.usePagination) {
                    var handlePagination = function DataGrid_handlePagination(state, me) {
                        me.widgets.paginator.setState(state);
                    };
                    var prefix = this.options.useExtPaginationMode ? "lecm." : "";
                    this.widgets.paginator = new YAHOO.widget.Paginator(
                        {
                            containers: [this.id + "-paginatorBottom"],
                            rowsPerPage: this.options.pageSize,
                            initialPage: this.options.initialPage,
                            template: this.msg(prefix + "pagination.template"),
                            pageReportTemplate: this.msg(prefix + "pagination.template.page-report"),
                            previousPageLinkLabel: this.msg(prefix + "pagination.previousPageLinkLabel"),
                            nextPageLinkLabel: this.msg(prefix + "pagination.nextPageLinkLabel"),
                            firstPageLinkLabel: this.msg(prefix + "pagination.firstPageLinkLabel"),
                            lastPageLinkLabel: this.msg(prefix + "pagination.lastPageLinkLabel"),
                            lastPageLinkTitle: this.msg(prefix + "pagination.lastPageLinkLabel.title"),
                            firstPageLinkTitle: this.msg(prefix + "pagination.firstPageLinkLabel.title")
                        });

                    this.widgets.paginator.subscribe("changeRequest" + this.id, handlePagination, this);

                    // Display the bottom paginator bar
                    Dom.setStyle(this.id + "-datagridBarBottom", "display", "none");
                }
            },
            /**
             * Поиск
             * @return {YAHOO.util.DataSource}
             * @private
             */
            _setupDataSource:function () {
                var ds = this.options.dataSource ? this.options.dataSource : "lecm/search";
                if (this.options.repoDatasource) {
                    var uriSearchResults = Alfresco.constants.PROXY_URI + ds;
                } else {
                    var uriSearchResults = Alfresco.constants.URL_SERVICECONTEXT + ds;
                }

                var dSource = new YAHOO.util.DataSource(uriSearchResults,
                    {
                        connMethodPost:true,
                        responseType:YAHOO.util.DataSource.TYPE_JSON,
                        connXhrMode:"queueRequests",
                        responseSchema:{
                            resultsList:"items",
                            metaFields:{
                                startIndex:"startIndex",
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
                this.dataRequestNameSubstituteStrings = [];
                this.dataResponseFields = [];

                for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                    var column = this.datagridColumns[i],
                        columnName = column.name.replace(":", "_"),
                        fieldLookup = ((column.type == "property" || column.formsName.indexOf("prop_") == 0) ? "prop" : "assoc") + "_" + columnName;

                    this.dataRequestFields.push(columnName);
                    this.dataRequestNameSubstituteStrings.push(column.nameSubstituteString);
                    this.dataResponseFields.push(fieldLookup);
                    this.datagridColumns[fieldLookup] = column;
                }

                // DataSource definition if not alfready defined
                if (!this.widgets.dataSource) {
                    this.widgets.dataSource = this._setupDataSource();
                }
            },
            /**
             * Получение колонок dataGrid
             * @return {Array} список колонок
             * @constructor
             */
            getDataTableColumnDefinitions:function DataGrid_getDataTableColumnDefinitions() {
                // YUI DataTable column definitions
				var columnDefinitions = [];
	            if (this.options.expandable) {
		            columnDefinitions.push({
			            key: "expand",
			            label: "",
			            sortable: false,
			            formatter: this.fnRenderCellExpand (),
			            width: 16
		            });
	            }
				if (this.options.showCheckboxColumn) {
                    columnDefinitions.push({
						key: "nodeRef",
						label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
						sortable: false,
						formatter: this.fnRenderCellSelected (),
						width: 16
					});
				}

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

                        columnDefinitions.push({
                            key:this.dataResponseFields[i],
                            label:column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_")),
                            sortable:sortable,
                            resizeable: column.resizeable === undefined ? false : column.resizeable,
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
            beforeRenderFunction:function () {
                var me = this;
                var dataGrid = me.modules.dataGrid;
                var datagridMeta = dataGrid.datagridMeta;

                if (me.currentSort) {
                    if (me.elTh == null) {
                        me.elTh = me.currentSort.oColumn.getThEl();
                    }
                    if (me.elTh == me.currentSort.oColumn.getThEl()) {
                        if (me.currentSort.sSortDir == YAHOO.widget.DataTable.CLASS_DESC) {
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
                    datagridMeta.sort = "";
                    var sortField;
                    if (me.currentSort.oColumn.field.indexOf("assoc_") != 0) {
                        sortField = me.currentSort.oColumn.field.replace("prop_", "").replace("_", ":");
                    } else {
                        sortField = me.currentSort.oColumn.field.replace("assoc_", "").replace("_", ":") + "-text-content";
                    }
                    if (me.desc) {
                        datagridMeta.sort = sortField + "|true";
                        me.desc = false;
                        me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_ASC;
                    } else {
                        datagridMeta.sort = sortField + "|false";
                        me.desc = true;
                        me.currentSort.sSortDir = YAHOO.widget.DataTable.CLASS_DESC;
                    }
                    if (me.sort) {
                        // Обнуляем сортировку иначе зациклится.
                        me.sort = null;
	                    if (me.options.useCookieForSort) {
		                    me.setCookie(this.getSortCookieName(), datagridMeta.sort);
	                    }
                        if (!me.options.usePagination) {
                            this.search.performSearch({
                                searchConfig: datagridMeta.searchConfig,
                                searchShowInactive: me.options.searchShowInactive,
                                parent: datagridMeta.nodeRef,
	                            searchNodes: this.datagridMeta.searchNodes,
                                itemType: datagridMeta.itemType,
                                sort: datagridMeta.sort,
                                useChildQuery:datagridMeta.useChildQuery,
                                useOnlyInSameOrg: datagridMeta.useOnlyInSameOrg,
                                useFilterByOrg: datagridMeta.useFilterByOrg
                            });
                        }
                    }
                }
            },

            exportData: function exportData_function(isAll) {
                this.search.exportData(isAll);
            },

            _filterInArray: function (filterCode, filtersArray) {
                for (var i = 0; i < filtersArray.length; i++) {
                    var filter = filtersArray[i];
                    if (filter.code == filterCode) {
                        return i;
                    }
                }
                return -1;
            },

            _generatePaginatorRequest: function (oState, oSelf) {
                this.widgets.dataSource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON)

                var sort = this.datagridMeta.sort,
                    sortField;
                if (this.currentSort) {
                    if (this.currentSort.oColumn.field.indexOf("assoc_") != 0) {
                        sortField = this.currentSort.oColumn.field.replace("prop_", "").replace("_", ":");
                    } else {
                        sortField = this.currentSort.oColumn.field.replace("assoc_", "").replace("_", ":") + "-text-content";
                    }
                    sort = sortField + "|" + (this.currentSort.sSortDir == YAHOO.widget.DataTable.CLASS_ASC ? "true" : "false");
                }

                // дополнительный фильтр из адресной строки (или параметров)
                if (!this.currentFilters || this.currentFilters.length == 0) {
                    var bookmarkedFilter = null;
                    if (YAHOO.util.History) {
                        bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
                    }
                    if (bookmarkedFilter) {
                        try {
                            while (bookmarkedFilter !== (bookmarkedFilter = decodeURIComponent(bookmarkedFilter))) {
                            }
                        }
                        catch (e) {
                            // Catch "malformed URI sequence" exception
                        }
                        var fnDecodeBookmarkedFilter = function DL_fnDecodeBookmarkedFilter(strFilter) {
                            var filters = strFilter.split("|");
                            return {
                                code: window.unescape(filters[0] || ""),
                                curValue: window.unescape(filters[1] || ""),
                                fromUrl:true
                            };
                        };

                        var filterFromUrl = fnDecodeBookmarkedFilter(bookmarkedFilter);
                        var index = this.dataGrid._filterInArray(filterFromUrl.code, successFilters);
                        if (index >= 0) {
                            this.currentFilters.splice(index, 1);
                        }
                        this.currentFilters.push(filterFromUrl);
                    }
                }

                var selectAllChbx = Dom.get(this.id + '-select-all-records');
                if (selectAllChbx != null) {
                    selectAllChbx.checked = false;
                }

                var searchParams = {
                    parent: this.datagridMeta.nodeRef,
                    searchNodes: null,
                    itemType: this.datagridMeta.itemType,
                    sort: sort,
                    searchConfig: this.datagridMeta.searchConfig,
                    searchFields: this.dataRequestFields.join(","),
                    dataRequestNameSubstituteStrings: this.dataRequestNameSubstituteStrings.join(","),
                    searchShowInactive: this.options.searchShowInactive,
                    offset: oState.pagination.recordOffset,
                    additionalFilters: this.currentFilters,
                    useChildQuery: this.datagridMeta.useChildQuery,
                    useFilterByOrg: this.datagridMeta.useFilterByOrg,
                    useOnlyInSameOrg: this.datagridMeta.useOnlyInSameOrg
                };

                return YAHOO.lang.JSON.stringify(this.search.prepareSearchParams(searchParams));
            },

	        /**
             * Прорисовка таблицы, установка свойств, сортировка.
             * @param columnDefinitions колонки
             * @param me {object} this
             * @return {YAHOO.widget.DataTable} таблица
             * @private
             */
            _setupDataTable: function (columnDefinitions, me) {
	            var dTable = new YAHOO.widget.DataTable(this.id + "-grid", columnDefinitions, this.widgets.dataSource,
                    {
                        generateRequest: this._generatePaginatorRequest.bind(this),
                        initialLoad: false,
                        dynamicData: this.options.usePagination && !this.options.disableDynamicPagination,
                        "MSG_EMPTY": this.msg("message.empty"),
                        "MSG_ERROR": this.msg("message.error"),
                        "MSG_LOADING" : this.msg("message.loading"),
                        MSG_SORTASC: this.msg("message.sortasc"),
                        MSG_SORTDESC: this.msg("message.sortdesc"),
                        paginator: this.widgets.paginator,
	                    sortedBy: this.getDatableSortBy(columnDefinitions),
	                    formatRow: this.getRowFormater()
                    });

                // Обновляем значения totalRecords данными из ответа сервера
                dTable.handleDataReturnPayload = function DataGrid_handleDataReturnPayload(oRequest, oResponse, oPayload) {
	                if (me.options.usePagination) {
		                // Display the bottom paginator bar
		                Dom.setStyle(me.id + "-datagridBarBottom", "display", "block");
	                }

	                me.totalRecords = oResponse.meta.totalRecords;
                    if (oPayload) {
                        oPayload.totalRecords = oResponse.meta.totalRecords;
                        oPayload.pagination.recordOffset = oResponse.meta.startIndex;
                        return oPayload
                    } else {
                        oResponse.meta.pagination =
                        {
                            rowsPerPage: me.options.pageSize,
                            recordOffset: oResponse.meta.startIndex
                        };
                        return oResponse.meta;
                    }
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
                    if (oResponse.results && oResponse.results.length === 0) {
                        this.fireEvent("renderEvent",
                            {
                                type: "renderEvent"
                            });
                    }

                    // Must return true to have the "Loading..." message replaced by the error message
                    return true;
                };

                // Override default function so the "Loading..." message is suppressed
                dTable.doBeforeSortColumn = function DataGrid_doBeforeSortColumn(oColumn, sSortDir) {
                    me.currentSort =
                    {
                        oColumn: oColumn,
                        sSortDir: sSortDir

                    };
                    me.sort = {
                        enable: true
                    }
                    return true;
                };

                if (this.options.showCheckboxColumn) {
                    // Событие когда выбранны все элементы
                    YAHOO.util.Event.onAvailable(this.id + "-select-all-records", function () {
                        YAHOO.util.Event.on(this.id + "-select-all-records", 'click', this.selectAllClick, this, true);
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
                        Dom.get(this.id + '-select-all-records').checked = allChecked;

                        Bubbling.fire("selectedItemsChanged", this.options.bubblingLabel);
                    }, this, true);
                }
                // Сортировка. Событие при нажатии на название столбца.
                dTable.subscribe("beforeRenderEvent", this.beforeRenderFunction.bind(this), dTable, true);

                // Rendering complete event handler
                dTable.subscribe("renderEvent", this.onRenderEvent.bind(this), this, true);

                // Enable row highlighting
                dTable.subscribe("rowMouseoverEvent", dTable.onEventHighlightRow, null, dTable);
                dTable.subscribe("rowMouseoutEvent", dTable.onEventUnhighlightRow, null, dTable);
                dTable.subscribe("rowHighlightEvent", this.onEventHighlightRow, this, true);
                dTable.subscribe("rowUnhighlightEvent", this.onEventUnhighlightRow, this, true);

                if (this.options.height != null) {
                    YAHOO.util.Dom.setStyle(this.id + "-grid", "height", this.options.height + "px");
                }

                if (!this.options.usePagination && this.options.unlimited) {
                    YAHOO.util.Event.addListener(this.id + "-grid", "scroll", this.onContainerScroll, this);
                }

                return dTable;
            },

            onContainerScroll: function (event, scope) {
                var container = event.currentTarget;
                if (container.scrollTop + container.clientHeight == container.scrollHeight) {
                    if (!scope.loadComplete) {
                        // Update the DataSource
                        var offset = scope.widgets.dataTable.getRecordSet().getRecords().length;

                        scope.search.performSearch({
                            searchConfig: scope.datagridMeta.searchConfig,
                            searchShowInactive: scope.options.searchShowInactive,
                            parent: scope.datagridMeta.nodeRef,
                            searchNodes: scope.datagridMeta.searchNodes,
                            itemType: scope.datagridMeta.itemType,
                            sort: scope.datagridMeta.sort,
                            offset: offset,
                            notReplaceRS: true,
                            useChildQuery:scope.datagridMeta.useChildQuery,
                            filter: null,
                            useOnlyInSameOrg: scope.datagridMeta.useOnlyInSameOrg,
                            useFilterByOrg: scope.datagridMeta.useFilterByOrg
                        });
                    }
                }
            },

            onRenderEvent: function () {
                Alfresco.logger.debug("DataTable renderEvent");
                Bubbling.fire("GridRendered");
                for (var i = 0, j = this.afterDataGridUpdate.length; i < j; i++) {
                    this.afterDataGridUpdate[i].call(this);
                }
                this.afterDataGridUpdate = [];
                this.fixHeader();
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
	            this.restoreSortFromCookie();
                // DataTable definition
                if (!this.widgets.dataTable || this.datagridMeta.recreate) {
	                if (this.widgets.dataTable) {
		                this.destroyDatatable();
	                }
                    this._setupPaginatior();
                    this.widgets.dataTable = this._setupDataTable(columnDefinitions, this);
                    this.customTableSetup();
                    if (!this.search) {
                    // initialize Search
                        this.search = new LogicECM.AdvancedSearch(this.id, this).setOptions({
                            showExtendSearchBlock:this.options.showExtendSearchBlock,
                            maxSearchResults: this.options.maxResults,
                            loopSize: this.options.loopSize,
                            unlimited: !this.options.usePagination && this.options.unlimited,
	                        searchFormId: this.options.advSearchFormId
                        });

                    } else {
                        this.search.clear(this);
                    }
                    this.datagridMeta.recreate = false; // сброс флага
                }

                var searchConfig = this.datagridMeta.searchConfig;
                var sort = this.datagridMeta.sort;
				var searchShowInactive;

				//если в datagridMeta существует searchShowInactive
				if (this.datagridMeta.hasOwnProperty ("searchShowInactive")) {
					searchShowInactive = this.datagridMeta.searchShowInactive;
				} else {
					searchShowInactive = this.options.searchShowInactive;
				}

                if (!searchConfig) {
                    searchConfig = {};
                }

                // фиксируем тип
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

                //при первом поиске сохраняем настройки
                if (this.initialSearchConfig == null) {
                    this.initialSearchConfig = {fullTextSearch: null};
                    this.initialSearchConfig = YAHOO.lang.merge(searchConfig, this.initialSearchConfig);
                }

                this.search.performSearch({
                    parent: this.datagridMeta.nodeRef,
                    searchNodes: this.datagridMeta.searchNodes,
                    searchConfig:searchConfig,
                    itemType: this.datagridMeta.itemType,
                    searchShowInactive: searchShowInactive,
                    useChildQuery:this.datagridMeta.useChildQuery,
                    useOnlyInSameOrg: this.datagridMeta.useOnlyInSameOrg,
                    useFilterByOrg: this.datagridMeta.useFilterByOrg,
                    sort:sort
                });
            },

            customTableSetup: function () {
                //override in childs
            },

	        getDatableSortBy: function(columnDefinitions) {
		        if (this.datagridMeta.sort != null) {
			        var columnName = this.datagridMeta.sort.substr(0, this.datagridMeta.sort.indexOf("|"));
			        var dir = this.datagridMeta.sort.substr(this.datagridMeta.sort.indexOf("|") + 1, this.datagridMeta.sort.length);
			        if (columnName != null && columnName.length > 0 && dir != null && dir.length > 0) {
				        columnName = columnName.replace(":", "_");
				        var sotrColumnExist = false;
				        for (var i = 0; i < columnDefinitions.length; i++) {
					        var columnDef;
					        if (columnDefinitions[i].key.indexOf("assoc_") != 0) {
						        columnDef = columnDefinitions[i].key.replace("prop_", "");
					        } else {
						        columnDef = columnDefinitions[i].key.replace("assoc_", "") + "-text-content";
					        }
					        if (columnDef == columnName) {
						        columnName = columnDefinitions[i].key;
						        sotrColumnExist = true;
						        break;
					        }
				        }
				        if (sotrColumnExist) {
					        return {
						        key: columnName,
						        dir: dir == "true" ? "asc" : "desc"
					        }
				        }
			        }
		        }
		        return null;
	        },

	        restoreSortFromCookie: function() {
		        if (this.options.useCookieForSort) {
			        var cookieSort = this.getCookie(this.getSortCookieName());
			        if (cookieSort != null && cookieSort.length > 0) {
				        this.datagridMeta.sort = cookieSort;

				        this.desc = this.datagridMeta.sort.substr(this.datagridMeta.sort.indexOf("|") + 1, this.datagridMeta.sort.length) == "false";
			        }
		        }
	        },

	        getSortCookieName: function() {
		        var cookieName = "datagrid-sort";
		        if (this.datagridMeta.datagridFormId != null && this.datagridMeta.datagridFormId != undefined) {
			        cookieName += "-" + this.datagridMeta.datagridFormId;
		        }
		        cookieName += "-" + this.datagridMeta.itemType;
		        return cookieName;
	        },

	        getCookie: function(name) {
		        var matches = document.cookie.match(new RegExp("(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"));
		        return matches ? decodeURIComponent(matches[1]) : undefined;
	        },

	        setCookie: function(name, value, options) {
				options = options || {};
				var expires = options.expires;
				if (typeof expires == "number" && expires) {
					var d = new Date();
					d.setTime(d.getTime() + expires*1000);
					expires = options.expires = d;
				}
				if (expires && expires.toUTCString) {
					options.expires = expires.toUTCString();
				}
				value = encodeURIComponent(value);
				var updatedCookie = name + "=" + value;
				for(var propName in options) {
					updatedCookie += "; " + propName;
					var propValue = options[propName];
					if (propValue !== true) {
						updatedCookie += "=" + propValue;
					}
				}
				document.cookie = updatedCookie;
			},


			/**
			 * Добавляет меню для колонок
			 */
			setupActions: function() {

                var moreActionsDiv = document.getElementById(this.id + '-moreActions');
                var actionMoreDiv, actionMoreA;
                if (moreActionsDiv) {
                    actionMoreDiv = moreActionsDiv.children[0];
                    if (actionMoreDiv) {
                        actionMoreA = actionMoreDiv.children[0];
                        if (actionMoreA) {
                            actionMoreA.className = 'show-more show-more' + (this.options.bubblingLabel ? "-"+ this.options.bubblingLabel : "");
                        }
                    }
                }

				if (this.options.actions != null) {
					var actionsDiv = document.getElementById(this.id + "-actionSet");
                    if (actionsDiv && actionsDiv.children.length == 0) {
                        for (var i = 0; i < this.options.actions.length; i++) {
                            var action = this.options.actions[i];

                            var actionDiv = document.createElement("div");
                            actionDiv.className = action.id;

                            var actionA = document.createElement("a");
                            actionA.rel = action.permission;
                            actionA.className = "datagrid-action-link " + action.type;
                            actionA.title = action.label;

                            var actionSpan = document.createElement("span");
                            actionSpan.innerHTML = action.label;

                            actionA.appendChild(actionSpan);
                            actionDiv.appendChild(actionA);
                            actionsDiv.appendChild(actionDiv);
                        }
                    }
				}
			},

            /**
             * обновляем набор действий для строки
             */
            updateActions: function(actionsEls, recId, oData) {
                if (this.options.actions != null) {
                    var getActionDivByClass = function(className){
                        var actionsDivs = YAHOO.util.Selector.query("div", actionsEls);
                        var actionDiv = null;
                        for (var j=0; j < actionsDivs.length; j++) {
                            var testDiv = actionsDivs[j];
                            if (Dom.hasClass(testDiv, className)){
                                actionDiv = testDiv;
                                break;
                            }
                        }
                        return actionDiv;
                    }.bind(this);

                    for (var i = 0; i < this.options.actions.length; i++){
                        var showAction = true; // по умолчанию - показывать
                        var action = this.options.actions[i];
                        var evaluator = action.evaluator;
                        if (evaluator != null && typeof evaluator == "function") {
                            //var result = ;
                            //if (result != undefined){
                            showAction = evaluator.call(this, oData);
                            //}
                        }
                        var actionDiv = getActionDivByClass(action.id);
                        if (actionDiv != null && actionDiv != undefined) {
                            Dom.setStyle(actionDiv.id, "display", showAction ? "block" : "none");
                        }

                    }
                    // удаляем блоки Показать еще
                    var showMoreBlock = getActionDivByClass("onActionShowMore");
                    if (showMoreBlock) {
                        actionsEls.removeChild(showMoreBlock);
                    }
                    var showMore = getActionDivByClass("more-actions");
                    if (showMore){
                        var childs = Dom.getChildren(showMore);
                        for (j = 0 ; j < childs.length; j++){
                            actionsEls.appendChild(childs[j]);
                        }
                        actionsEls.removeChild(showMore);
                    }
                }
            },
            /**
             * Выбор всех значений
             * @constructor
             */
            selectAllClick: function DataGrid_selectAllClick() {
                var selectAllElement = Dom.get(this.id + "-select-all-records");
                if (selectAllElement.checked) {
                    this.selectItems("selectAll");
                } else {
                    this.selectItems("selectNone");
                }
            },

            _showVersionLabel:function (oData, id) {
                if (this.versionable) {
                    var versionValue = oData.prop_cm_versionLabel != null ? oData.prop_cm_versionLabel.value : "1.0";
                    // Получаем список ячеек tr
                    var childTrElement = Dom.getChildren(id);
                    // Количество элементов tr
                    var colTr = Dom.getChildren(Dom.get(id)).length;
                    for (i = 0; i < colTr; i++) {
                        Dom.setAttribute(childTrElement[i], "title", this.msg("message.version") + " " + versionValue);
                    }
                }
            }, /**
             * Custom event handler to highlight row.
             *
             * @method onEventHighlightRow
             * @param oArgs.el {HTMLElement} The highlighted TR element.
             * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
             */
            onEventHighlightRow:function DataGrid_onEventHighlightRow(oArgs) {
                // elActions is the element id of the active table cell where we'll inject the actions
                var elActions = Dom.get(this.id + "-actions-" + oArgs.el.id);

                // Выбранный элемент
                //var numSelectItem = this.widgets.dataTable.getTrIndex(oArgs.target);
                // if (this.widgets.paginator) {
                //     numSelectItem = numSelectItem + ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                // }

                // var selectItem = this.widgets.dataTable.getRecordSet().getRecord(numSelectItem);
                var selectItem = oArgs.record;
                if (selectItem){
                    var oData = selectItem.getData();
                    this._showVersionLabel(oData.itemData, oArgs.el.id);
                }

                // Inject the correct action elements into the actionsId element
                if (elActions && !this.showingMoreActions) {
                    // Call through to get the row highlighted by YUI
                    //this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

                    // Clone the actionSet template node from the DOM
                    var record = this.widgets.dataTable.getRecord(oArgs.el.id),
                        clone = null;
                    if (elActions.firstChild === null){
                        clone = Dom.get(this.id + "-actionSet").cloneNode(true);
                        // Token replacement
                        clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record));

                        // Generate an id
                        clone.id = elActions.id + "_a";

                        var actionsDivs = YAHOO.util.Selector.query("div", clone);
                        for (index = 0; index < actionsDivs.length; index++) {
                            var actionDiv = actionsDivs[index];
                            Dom.generateId(actionDiv, actionDiv.className + "-" + oArgs.el.id);
                        }

                        // Simple view by default
                        Dom.addClass(clone, "simple");

                        // фильтруем по правам
                        var userAccess = record.getData("permissions").userAccess;

                        // Remove any actions the user doesn't have permission for
                        var actions = YAHOO.util.Selector.query("div", clone),
                            action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;

                        if (actions.length > this.splitActionsAtStore) {
                            this.options.splitActionsAt = this.splitActionsAtStore - 1;
                        }
                        for (i = 0, ii = actions.length; i < ii; i++) {
                            action = actions[i];
                            aTag = action.firstChild;
                            spanTag = aTag.firstChild;

                            if (aTag.rel !== "") {
                                actionPermissions = aTag.rel.split(",");
                                for (j = 0, jj = actionPermissions.length; j < jj; j++) {
                                    aP = actionPermissions[j];
                                    // Support "negative" permissions
                                    if ((aP.charAt(0) == "~") ? !!userAccess[aP.substring(1)] : !userAccess[aP]) {
                                        clone.removeChild(action);
                                        break;
                                    }
                                    if (!this.versionable && (action.attributes[0].nodeValue == "onActionVersion")) {
                                        clone.removeChild(action);
                                    }
                                }
                            }
                        }
                        elActions.appendChild(clone);
                    }

                    var actionsBlock = elActions.firstChild;

                    this.updateActions(actionsBlock, oArgs.el.id, oData);

                    // Проверяем сколько у нас осталось действий и нужно ли рисовать "More >" контейнер?
                    var splitAt = this.options.splitActionsAt;

                    var getVisibleActions = function(actionsBlock){
                        var actionsDivs = YAHOO.util.Selector.query("div", actionsBlock);
                        var visible = [];
                        for (var j=0; j < actionsDivs.length; j++) {
                            var testDiv = actionsDivs[j];
                            if (testDiv.getAttribute("style") != null){
                                var style = testDiv.getAttribute("style");
                                var attrs = style.split(";");
                                for (var k=0; k <  attrs.length; k++){
                                    if (attrs[k].indexOf("display") >= 0){
                                        var attrDisplay = attrs[k];
                                        var displayValue = attrDisplay.split(":")[1].trim();
                                        if (displayValue != "none"){
                                            visible.push(testDiv);
                                        }
                                    }
                                }
                            } else {
                                visible.push(testDiv);
                            }
                        }
                        return visible;
                    }.bind(this);

                    var visibleActions = getVisibleActions(actionsBlock);
                    var showMoreDiv = null;
                    for (var k=0; k < visibleActions.length; k++) {
                        var testDiv = visibleActions[k];
                        if (Dom.hasClass(testDiv, "onActionShowMore")){
                            showMoreDiv = testDiv;
                            break;
                        }
                    }
                   // actions = YAHOO.util.Selector.query("div", actionsBlock);
                    if (!showMoreDiv && visibleActions.length > 3) {
                        var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true);
                        var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
                        // Insert the two necessary DIVs before the splitAt action item
                        Dom.insertBefore(containerDivs[0], visibleActions[splitAt]);
                        Dom.insertBefore(containerDivs[1], visibleActions[splitAt]);
                        // Now make action items after the split, children of the 2nd DIV
                        var index, moreActions = visibleActions.slice(splitAt);
                        for (index in moreActions) {
                            if (moreActions.hasOwnProperty(index)) {
                                containerDivs[1].appendChild(moreActions[index]);
                            }
                        }
                    }
                }

                if (this.showingMoreActions) {
                    this.deferredActionsMenu = elActions;
                }
                else {
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
             * @param oArgs.el {HTMLElement} The highlighted TR element.
             * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
             */
            onEventUnhighlightRow: function DataGrid_onEventUnhighlightRow(oArgs)
            {
                // Call through to get the row unhighlighted by YUI
                // this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

                var elActions = Dom.get(this.id + "-actions-" + (oArgs.el.id));

                // Just hide the action links, rather than removing them from the DOM
                Dom.addClass(elActions, "hidden");
                this.deferredActionsMenu = null;

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
		            aPageRecords,
		            startRecord,
		            endRecord,
		            record;
	            if (this.widgets.paginator && this.widgets.paginator.getPageRecords()) {
		            aPageRecords = this.widgets.paginator.getPageRecords();
		            startRecord = aPageRecords[0];
		            endRecord = aPageRecords[1];
	            } else {
		            startRecord = 0;
		            endRecord = this.totalRecords;
	            }
	            for (var i = startRecord; i <= endRecord; i++)
	            {
		            record = recordSet.getRecord(i);
		            if (record && this.selectedItems[record.getData("nodeRef")])
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
				if (this.options.showCheckboxColumn) {
					var recordSet = this.widgets.dataTable.getRecordSet(),
						checks = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
						aPageRecords,
						startRecord,
						len = checks.length,
						record, i, fnCheck;
                    if (this.widgets.paginator && this.widgets.paginator.getPageRecords()) {
                        aPageRecords = this.widgets.paginator.getPageRecords();
                        startRecord = aPageRecords[0];
                    } else {
                        startRecord = 0;
                    }
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

					Bubbling.fire("selectedItemsChanged", this.options.bubblingLabel);
				}
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
                            filters: obj.filter ? obj.filter : null
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
                    if (!this.options.refreshAfterCreate) {
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
                                        this.collapseAll();
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
                    } else {
                        this.onDataGridRefresh(layer, args);
                    }
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
								this.search.updatePage();
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
            _updateDataGrid: function DataGrid__updateDataGrid(p_obj) {
                var successFilters = p_obj.filters ? p_obj.filters : null;

                // Reset the custom error messages
                this._setDefaultDataTableErrors(this.widgets.dataTable);

                // More Actions menu no longer relevant
                this.showingMoreActions = false;

                var searchConfig = this.datagridMeta.searchConfig;
                if (!searchConfig) {
                    searchConfig = {};
                }
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

                // Update the DataSource
                var offset = 0;
                if (this.widgets.paginator) {
                    offset = ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                }

                this.search.performSearch({
                    searchConfig: this.datagridMeta.searchConfig,
                    searchShowInactive: this.options.searchShowInactive,
                    parent: this.datagridMeta.nodeRef,
                    searchNodes: this.datagridMeta.searchNodes,
                    itemType: this.datagridMeta.itemType,
                    sort: this.datagridMeta.sort,
                    offset: offset,
                    useChildQuery:this.datagridMeta.useChildQuery,
                    useOnlyInSameOrg: this.datagridMeta.useOnlyInSameOrg,
                    useFilterByOrg: this.datagridMeta.useFilterByOrg,
                    filter: successFilters
                });
            },

            /**
             * Build URI parameter string for doclist JSON data webscript
             *
             * @method _buildDataGridParams
             * @param p_obj.filter {string} [Optional] Current filter
             * @return {Object} Request parameters. Can be given directly to Alfresco.util.Ajax, but must be JSON.stringified elsewhere.
             */
            _buildDataGridParams: function DataGrid__buildDataGridParams(p_obj) {
                var reqFields = [];
                var reqNameSubstituteStrings = [];
                for (var i = 0, ii = this.datagridColumns.length; i < ii; i++) {
                    var column = this.datagridColumns[i],
                        columnName = column.name.replace(":", "_");
                    reqFields.push(columnName);
                    reqNameSubstituteStrings.push(column.nameSubstituteString);
                }
                var nameSubstituteStrings = reqNameSubstituteStrings.join(",");
                return {
                    fields: this.dataRequestFields,
                    substituteStrings: nameSubstituteStrings
                };
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
                var index = 0;
                if (this.widgets.paginator && this.widgets.paginator.getCurrentPage()) {
                    index = ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                }
                for (var i = index, j = recordSet.getLength(); i < j; i++)
                {
                    if (recordSet.getRecord(i).getData(p_parameter) == p_value)
                    {
                        return recordSet.getRecord(i);
                    }
                }
                return null;
            },

            /**
             * @return {boolean}
             */
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
                this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
            },

            /**
                Удаление элемента. onActionDelete дергает этот метод.
                Вынесено в отдельный метод, чтобы в конкретных датагридах не копировать
                код и иметь возможность навешивать доп проверки
             */
            onDelete: function DataGridActions_onDelete(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt){
                var me = this,
                    items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

	            var itemNames = [];
	            var propToShow = "prop_cm_name";
	            for (var j = 0, jj = this.datagridColumns.length; j < jj; j++) {
		            var column = this.datagridColumns[j];
		            if (me.options.attributeForShow != null && column.name == me.options.attributeForShow) {
			            propToShow = column.formsName;
			            break;
		            }
	            }
	            for (var k = 0; k < items.length; k++) {
		            if (items[k] && items[k].itemData && items[k].itemData[propToShow]) {
		                itemNames.push("'" + items[k].itemData[propToShow].displayValue + "'");
	                }
	            }

	            var itemsString = itemNames.join(", ");
                var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items) {
                    var nodeRefs = [];
                    for (var i = 0, ii = items.length; i < ii; i++) {
                        nodeRefs.push(items[i].nodeRef);

                        var record = this._findRecordByParameter(items[i].nodeRef, "nodeRef");
                        if (record != null) {
                            var row = this.widgets.dataTable.getRow(record);
                            if (Dom.hasClass(row, "expanded")) {
                                Dom.get("expand-" + record.getId()).innerHTML = "+";
                                Dom.removeClass(row, "expanded");
                                this.onCollapse(record);
                            }
                        }
                    }
                    var query = "";
                    if (actionsConfig) {
                        var fullDelete = actionsConfig.fullDelete;
                        if (fullDelete != null) {
                            query = query + "full=" + fullDelete;
                        }
                        var trash = actionsConfig.trash;
                        if (fullDelete != null && trash != null) {
                            query = query + "&trash=" + trash;
                        }
                    }
                    this.modules.actions.genericAction(
                        {
                            success:{
                                callback:{
                                    fn: function (response) {
                                        if(fnDeleteComplete){
                                            fnDeleteComplete.call(me);
                                        }
                                        if (response.json.overallSuccess){
                                            if(!this.options.searchShowInactive){
                                                Bubbling.fire("dataItemsDeleted",
                                                    {
                                                        items:items,
                                                        bubblingLabel:me.options.bubblingLabel
                                                    });
                                                Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text:this.msg((actionsConfig && actionsConfig.successMessage)? actionsConfig.successMessage : "message.delete.success", items.length)
                                                    }, YAHOO.util.Dom.get(me.id));
                                            } else {
                                                for (var i = 0, ii = response.json.results.length; i < ii; i++) {
                                                    // Reload the node's metadata
                                                    Alfresco.util.Ajax.jsonPost(
                                                        {
                                                            url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(response.json.results[i].nodeRef).uri,
                                                            dataObj: this._buildDataGridParams(),
                                                            successCallback: {
                                                                fn: function DataGrid_onActionEdit_refreshSuccess(response) {
                                                                    // Fire "itemUpdated" event
                                                                    YAHOO.Bubbling.fire("dataItemUpdated",
                                                                        {
                                                                            item: response.json.item,
                                                                            bubblingLabel: me.options.bubblingLabel
                                                                        });
                                                                },
                                                                scope: this
                                                            }
                                                        });
                                                }
                                            }
                                        } else {
                                            Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text:this.msg("message.delete.failure")
                                                }, YAHOO.util.Dom.get(me.id));
                                        }
                                    },
                                    scope: this
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

                if (!fnPrompt){
                    fnPrompt = function(){me.onDelete_Prompt(fnActionDeleteConfirm,me,items,itemsString)};
                }
                fnPrompt.call(this, fnActionDeleteConfirm);
            },
            onDelete_Prompt: function(fnAfterPrompt,me,items,itemsString){
            Alfresco.util.PopupManager.displayPrompt(
                {
                    title:this.msg("message.confirm.delete.title", items.length),
                    text: (items.length > 1) ? this.msg("message.confirm.delete.group.description", items.length) : this.msg("message.confirm.delete.description", itemsString),
                    buttons:[
                        {
                            text:this.msg("button.delete"),
                            handler:function DataGridActions__onActionDelete_delete() {
                                this.destroy();
                                me.selectItems("selectNone");
                                fnAfterPrompt.call(me, items);
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
                                name:"datagridRefresh",
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
                        nodeRef:this.latestVersion.nodeRef,
                        versionCache: this.versionCache
                    });
            },

            /**
             * Edit Data Item pop-up
             *
             * @method onActionEdit
             * @param item {object} Object literal representing one data item
             */
            onActionEdit:function DataGrid_onActionEdit(item) {
                // Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
	            if (this.editDialogOpening) {
                    return;
                }
                this.editDialogOpening = true;
                var me = this;

	            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams = {
		            itemKind: "node",
		            itemId: item.nodeRef,
		            mode: "edit",
		            submitType: "json",
		            showCancelButton: true
	            };
	            if (this.options.editForm) {
		            templateRequestParams.formId = this.options.editForm;
	            }

                // Using Forms Service, so always create new instance
                var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
                editDetails.setOptions(
                    {
                        width: this.options.editFormWidth,
                        templateUrl:templateUrl,
	                    templateRequestParams:templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn: function(p_form, p_dialog) {
                                var contId = p_dialog.id + "-form-container";
                                if (item.type && item.type != "") {
                                    Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
                                }
								p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
								this.editDialogOpening = false;

	                            p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                            },
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionEdit_success(response) {
                                // Reload the node's metadata
	                            Bubbling.fire("datagridRefresh",
		                            {
			                            bubblingLabel:me.options.bubblingLabel
		                            });
								Alfresco.util.PopupManager.displayMessage({
									text:this.msg("message.details.success")
								});
	                            this.editDialogOpening = false;
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataGrid_onActionEdit_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.details.failure")
                                    });
	                            this.editDialogOpening = false;
                            },
                            scope:this
                        }
                    }).show();
            },

            getTextFields: function () {
                var columns = this.datagridColumns;
                var fields = "";
                for (var i = 0; i < columns.length; i++) {
                    if (columns[i].dataType == "text" || columns[i].dataType == "mltext") {
                        fields += columns[i].name + ",";
                    } else if (columns[i].type == "association") {
                        fields += columns[i].name + "-text-content" + ",";
                    }
                }
                if (fields.length > 1) {
                    fields = fields.substring(0, fields.length - 1);
                }
                return fields;
            },

            showCreateDialog:function (meta, callback, successMessage) {
                if (this.editDialogOpening) return;
	            this.editDialogOpening = true;
	            var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                    var addMsg = meta.addMessage;
                    var contId = p_dialog.id + "-form-container";
                    Alfresco.util.populateHTML(
                        [contId + "_h", addMsg ? addMsg : this.msg(this.options.createFormTitleMsg) ]
                    );
                    if (meta.itemType && meta.itemType != "") {
                        Dom.addClass(contId, meta.itemType.replace(":", "_") + "_edit");
                    }
                    me.editDialogOpening = false;
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams =  {
		            itemKind:"type",
		            itemId:meta.itemType,
		            destination:meta.nodeRef,
		            mode:"create",
		            formId: meta.createFormId != null ? meta.createFormId : "",
		            submitType:"json",
		            showCancelButton: true
	            };

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
	                    templateRequestParams:templateRequestParams,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionCreate_success(response) {
                                if (callback) {// вызов дополнительного события
                                    callback.call(this, response.json.persistedObject);
                                } else { // вызов события по умолчанию
                                    YAHOO.Bubbling.fire("nodeCreated",
                                        {
                                            nodeRef: response.json.persistedObject,
                                            bubblingLabel: this.options.bubblingLabel
                                        });
                                    YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                        {
                                            nodeRef: response.json.persistedObject,
                                            bubblingLabel: this.options.bubblingLabel
                                        });
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: this.msg(successMessage ? successMessage : "message.save.success")
                                        });
                                }
                                this.editDialogOpening = false;
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataGrid_onActionCreate_failure(response) {
                                LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
	                            me.editDialogOpening = false;
	                            this.widgets.cancelButton.set("disabled", false);
                            },
	                        scope: createDetails
                        }
                    }).show();
            },



            /**
             * Create Data Item pop-up
             *
             * @method onActionCreate
             */
            onActionCreate:function DataGrid_onActionCreate() {
                this.showCreateDialog(this.datagridMeta, null, null, null);
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
            },

	        isActiveItem: function DataGrid_isActiveItem(itemData) {
		        return itemData["prop_lecm-dic_active"] == undefined || itemData["prop_lecm-dic_active"].value == true;
	        },

	        /**
	         * Восстановление элемента.
	         *
	         * @method onActionRestore
	         * @param p_items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
	         * @param owner {Object} не используется Dom-объект
	         * @param actionsConfig {Object} Объект с настройками для экшена
	         * @param fnDeleteComplete {Object} CallBack, который вызовется после завершения удаления
	         */
	        onActionRestore:function DataGridActions_onActionRestore(p_items, owner, actionsConfig, fnComplete) {
		        this.onRestore(p_items, owner, actionsConfig, fnComplete, null);
	        },

	        /**
	         Восстановление элемента. onActionRestore дергает этот метод.
	         Вынесено в отдельный метод, чтобы в конкретных датагридах не копировать
	         код и иметь возможность навешивать доп проверки
	         */
	        onRestore: function DataGridActions_onRestore(p_items, owner, actionsConfig, fnComplete, fnPrompt){
		        var me = this,
			        items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

		        var itemNames = [];
		        var propToShow = "prop_cm_name";
		        for (var j = 0, jj = this.datagridColumns.length; j < jj; j++) {
			        var column = this.datagridColumns[j];
			        if (me.options.attributeForShow != null && column.name == me.options.attributeForShow) {
				        propToShow = column.formsName;
				        break;
			        }
		        }
		        for (var k = 0; k < items.length; k++) {
			        if (items[k] && items[k].itemData && items[k].itemData[propToShow]) {
				        itemNames.push("'" + items[k].itemData[propToShow].displayValue + "'");
			        }
		        }

		        var itemsString = itemNames.join(", ");
		        var fnActionRestoreConfirm = function DataGridActions__onActionRestore_confirm(items) {
			        var nodeRefs = [];
			        for (var i = 0, ii = items.length; i < ii; i++) {
				        nodeRefs.push(items[i].nodeRef);
			        }
			        this.modules.actions.genericAction(
				        {
					        success:{
						        event:{
							        name:"datagridRefresh",
							        obj:{
								        items:items,
								        bubblingLabel:me.options.bubblingLabel
							        }
						        },
						        message:this.msg((actionsConfig && actionsConfig.successMessage)? actionsConfig.successMessage : "message.restore.success", items.length),
						        callback:{
							        fn:fnComplete
						        }
					        },
					        failure:{
						        message:this.msg("message.restore.failure")
					        },
					        webscript:{
						        method: Alfresco.util.Ajax.POST,
						        name: "restore"
					        },
					        config:{
						        requestContentType:Alfresco.util.Ajax.JSON,
						        dataObj:{
							        nodeRefs:nodeRefs
						        }
					        }
				        });
		        };

		        if (!fnPrompt){
			        fnPrompt = function onRestore_Prompt(fnAfterPrompt){
				        Alfresco.util.PopupManager.displayPrompt(
					        {
						        title:this.msg("message.confirm.restore.title", items.length),
						        text: (items.length > 1) ? this.msg("message.confirm.restore.group.description", items.length) : this.msg("message.confirm.restore.description", itemsString),
						        buttons:[
							        {
								        text:this.msg("button.restore"),
								        handler:function DataGridActions__onActionRestore_restore() {
									        this.destroy();
									        me.selectItems("selectNone");
									        fnAfterPrompt.call(me, items);
								        }
							        },
							        {
								        text:this.msg("button.cancel"),
								        handler:function DataGridActions__onActionRestore_cancel() {
									        this.destroy();
								        },
								        isDefault:true
							        }
						        ]
					        });
			        }
		        }
		        fnPrompt.call(this, fnActionRestoreConfirm);
	        },

            fixHeader: function() {
                if (this.options.fixedHeader) {
                    var grid = Dom.get(this.id + "-grid");
                    var table = Dom.getChildrenBy(grid, function(el) {
                        return (el.tagName == "TABLE");
                    })[0];
                    var ths = Selector.query("thead > tr > th", table);
                    var tds = Selector.query("tbody > tr > td", table);
                    var fixCellsWidth = function(cells) {
                        for (var i = 0; i < cells.length; i++) {
                            var cell = cells[i];
                            Dom.setStyle(cell, "width", parseInt(Dom.getStyle(cell, "width")) + "px");
                        }
                    };

                    Dom.removeClass(table, "fixedHeader");
                    fixCellsWidth(ths);
                    fixCellsWidth(tds);
                    Dom.addClass(table, "fixedHeader");
                }
            },

	        onExportCsv: function DataGrid__onExportCsv(fileName)
	        {
		        var selectedItems = this.getSelectedItems();

		        var form = document.createElement("form");
		        form.enctype = "multipart/form-data";
		        form.action = Alfresco.constants.PROXY_URI + "lecm/base/action/export-csv";
		        form.method = "POST";

		        var inputFileName = document.createElement("input");
		        inputFileName.type = "hidden";
		        inputFileName.name = "fileName";
		        inputFileName.value = encodeURIComponent(fileName);
		        form.appendChild(inputFileName);

		        var inputTimeZone = document.createElement("input");
		        inputTimeZone.type = "hidden";
		        inputTimeZone.name = "timeZoneOffset";
		        inputTimeZone.value = new Date().getTimezoneOffset();
		        form.appendChild(inputTimeZone);

		        for (var i = 0; i < selectedItems.length;i++) {
			        var inputNodeRef = document.createElement("input");
			        inputNodeRef.type = "hidden";
			        inputNodeRef.name = "nodeRef";
			        inputNodeRef.value = selectedItems[i].nodeRef;
			        form.appendChild(inputNodeRef);
		        }

		        for (i = 0; i < this.datagridColumns.length; i++) {
			        var column = this.datagridColumns[i];
			        var label = column.label.length > 0 ? column.label : this.msg(column.name.replace(":", "_"));
			        var property = column.name;
			        if (column.nameSubstituteString != null) {
				        property = column.nameSubstituteString
			        } else if (column.type == "association") {
				        property = "{" + property + "/cm:name}";
			        } else {
				        property = "{" + property + "}";
			        }
			        var inputField = document.createElement("input");
			        inputField.type = "hidden";
			        inputField.name = "field";
			        inputField.value = property;
			        form.appendChild(inputField);

			        var inputFieldLabel = document.createElement("input");
			        inputFieldLabel.type = "hidden";
			        inputFieldLabel.name = "fieldLabel";
			        inputFieldLabel.value = label;
			        form.appendChild(inputFieldLabel);
		        }

		        document.body.appendChild(form);

		        form.submit();
	        },

            onReCreateDatagrid: function DataGrid_onChangeDatagrid(layer, args) {
                var obj = args[1];
                if (!obj || this._hasEventInterest(obj.bubblingLabel)) {
                    var newMeta = obj.datagridMeta;
                    this.datagridMeta = YAHOO.lang.merge(this.datagridMeta, newMeta);
                    this.datagridMeta.recreate = true;
                    this.populateDataGrid();
                }
            },

	        destroyDatatable: function () {
		        var dTable = this.widgets.dataTable;
		        if (dTable != null) {
			        if (this.options.showCheckboxColumn) {
				        YAHOO.util.Event.removeListener(this.id + "-select-all-records", 'click');

				        var records = dTable.getRecordSet().getRecords();
                        for (var i = 0; i < records.length; i++) {
                            if(records.hasOwnProperty(i)) {
                                YAHOO.util.Event.removeListener("expand-" + records[i].getId(), 'click');
                            }
                        }
			        }

			        dTable.destroy();
		        }
	        },

	        destroy: function ()
	        {
		        try
		        {
			        Bubbling.unsubscribe("activeGridChanged", this.onGridTypeChanged, this);
			        Bubbling.unsubscribe("dataItemCreated", this.onDataItemCreated, this);
			        Bubbling.unsubscribe("dataItemUpdated", this.onDataItemUpdated, this);
			        Bubbling.unsubscribe("dataItemsDeleted", this.onDataItemsDeleted, this);
			        Bubbling.unsubscribe("datagridRefresh", this.onDataGridRefresh, this);
			        Bubbling.unsubscribe("archiveCheckBoxClicked", this.onArchiveCheckBoxClicked, this);
			        Bubbling.unsubscribe("reCreateDatagrid", this.onReCreateDatagrid, this);

			        this.destroyDatatable();
		        }
		        catch (e)
		        {
			        // Ignore
		        }
		        LogicECM.module.Base.DataGrid.superclass.destroy.call(this);
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
                        obj.popup.destroyWithAnimationsStop();
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
