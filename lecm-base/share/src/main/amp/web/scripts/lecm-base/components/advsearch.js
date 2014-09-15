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
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;
    var $html = Alfresco.util.encodeHTML;
    /**
     * Advanced Search constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.AdvancedSearch} The new AdvancedSearch instance
     * @constructor
     */
    LogicECM.AdvancedSearch = function (htmlId, grid) {
        LogicECM.AdvancedSearch.superclass.constructor.call(this, "LogicECM.AdvancedSearch", htmlId, ["button", "container", "datasource", "datatable", "paginator"]);
        // Initialise prototype properties
        this.dataTable = grid.widgets.dataTable;
        this.dataSource = grid.widgets.dataSource;
        this.dataColumns = grid.datagridColumns;
        this.bubblingLabel = grid.options.bubblingLabel;

        this.searchDialog = null;
        this.searchStarted = false;
        this.currentSearchConfig = null;
        this.currentForm = null;
        this.dataGrid = grid;
        YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
        YAHOO.Bubbling.on("doSearch", this.onSearch, this);
        YAHOO.Bubbling.on("clearAttributesSearch", this.onClearSearchClick, this);
    };

    YAHOO.extend(LogicECM.AdvancedSearch, Alfresco.component.Base,
        {
            searchDialog: null, // окно атрибутивного поиска
            dataTable:null,   // DataTable из грида
            dataSource:null,  // DataSource из грида
            dataColumns:{},   // набор колонок из датагрида

            searchStarted: false, // флаг, что идет поиск

            bubblingLabel: null, // метка грида для которого будет выполнен поиск

            // сохраненные настройки предыдущего поиска
            currentSearchConfig:null,

            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:{
                maxSearchResults:1000,
                loopSize:50,
                unlimited:false, // флаг, что грид не ограничен в вовзвращаемых записях (выключен paging)
                showExtendSearchBlock: false,  // По умолчанию аттрибутивный поиск скрыт
                searchFormId: "searchBlock-forms"
            },

            /**
             * Currently visible Search Form object
             */
            currentForm:null,

            dataGrid: null, // Обратная ссылка на грид

            currentSearchArgs: null,

            /**
             * Получение и отрисовка формы
             *
             * @method renderFormTemplate
             * @param form {Object} Form descriptor to render template for
             * @param repopulate {boolean} If true, repopulate form instance based on supplied data
             */
            renderFormTemplate: function ADVSearch_renderFormTemplate(form, isClearSearch, e, obj) {
                if (isClearSearch == undefined) {
                    isClearSearch = false;
                }
                // update current form state
                this.currentForm = form;

                if (this.currentForm != null) {
                    var formDiv = Dom.get("searchBlock-forms"); // элемент в который будет отрисовываться форма
                    form.htmlid = this.options.searchFormId + "-" + form.type.split(":").join("_");

                    // load the form component for the appropriate type
                    var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "/components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
                        {
                            itemId: form.type,
                            formId: form.id
                        });
                    var formData =
                    {
                        htmlid: form.htmlid
                    };
                    Alfresco.util.Ajax.request(
                        {
                            url: formUrl,
                            dataObj: formData,
                            successCallback: {
                                fn: function ADVSearch_onFormTemplateLoaded(response) {
                                    formDiv.innerHTML = response.serverResponse.responseText;
                                    if (this.searchDialog != null) {
                                        if (isClearSearch) {
                                            //сбрасываем на значение по умолчанию
                                            if (this.dataGrid.datagridMeta.searchConfig) {
                                                // сбрасываем данные формы
                                                this.dataGrid.datagridMeta.searchConfig.formData = {
                                                    datatype: this.dataGrid.datagridMeta.itemType
                                                };

                                                // если у нас не задан терм поиска, значит делаем полный сброс полнотекстового поиска
                                                if (this.dataGrid.datagridMeta.searchConfig.fullTextSearch) {
                                                    if (typeof this.dataGrid.datagridMeta.searchConfig.fullTextSearch == "string") {
                                                        this.dataGrid.datagridMeta.searchConfig.fullTextSearch =
                                                            YAHOO.lang.JSON.parse(this.dataGrid.datagridMeta.searchConfig.fullTextSearch);
                                                    }
                                                    if (!this.dataGrid.datagridMeta.searchConfig.fullTextSearch.searchTerm ||
                                                        this.dataGrid.datagridMeta.searchConfig.fullTextSearch.searchTerm.length <= 0) {
                                                        this.dataGrid.datagridMeta.searchConfig.fullTextSearch = null;
                                                    }
                                                }

                                                this.performSearch({
                                                    parent: this.dataGrid.datagridMeta.nodeRef,
                                                    searchNodes: this.dataGrid.datagridMeta.searchNodes,
                                                    sort: this.dataGrid.datagridMeta.sort,
                                                    itemType: this.dataGrid.datagridMeta.itemType,
                                                    searchConfig: this.dataGrid.datagridMeta.searchConfig,
                                                    useChildQuery:this.dataGrid.datagridMeta.useChildQuery,
                                                    searchShowInactive: this.dataGrid.options.searchShowInactive
                                                });

                                            }
                                            if (!this.dataGrid.datagridMeta.searchConfig || !this.dataGrid.datagridMeta.searchConfig.fullTextSearch) {
                                                YAHOO.Bubbling.fire("hideFilteredLabel");
                                            }
                                            YAHOO.Bubbling.fire("hideSearchByAttributesLabel");
                                        } else {
                                            this.searchDialog.show();
                                        }
                                    }
                                },
                                scope: this
                            },
                            failureMessage: "Could not load form component '" + formUrl + "'.",
                            scope: this,
                            execScripts: true
                        });
                }
            },

            /**
             * Обработчик для кнопки Найти для аттрибутивного поиска
             *
             * @method onSearchClick
             * @param e {object} DomEvent
             * @param obj {object} Object passed back from addListener method
             */
            onSearchClick:function ADVSearch_onSearchClick(e, obj, showFilteredLabel) {
                if (showFilteredLabel == undefined) {
                    showFilteredLabel = true;
                }
                var me = this;
                if (!me.searchStarted) { // если поиск еще не начат (для предотвращения повторного взова метода)
                    me.searchStarted = true; // блокируем остальные запросы
                    // получает данные из формы
                    var formData = me.currentForm.runtime.getFormData();
                    formData.datatype = me.currentForm.type;

                    // формируем запрос
                    //var query = YAHOO.lang.JSON.stringify(formData);

                    // включаем поиск во всех вложенных директория относительно родительской
                    var fullTextSearch = {
                        parentNodeRef:me.dataGrid.datagridMeta.nodeRef
                    };
                    var sConfig = me.currentSearchConfig;
                    if (!sConfig) {
                        sConfig = {};
                    }
                    sConfig.formData = formData; // запрос
                    if (sConfig.fullTextSearch) {
                        if (typeof sConfig.fullTextSearch == "string"){
                            sConfig.fullTextSearch = YAHOO.lang.JSON.parse(sConfig.fullTextSearch);
                        }
                    } else {
                        sConfig.fullTextSearch = {};
                    }
                    sConfig.fullTextSearch = YAHOO.lang.merge(sConfig.fullTextSearch, fullTextSearch);

                    me.dataGrid.datagridMeta.searchConfig = sConfig;

                    this.performSearch(
                        {
                            searchConfig:sConfig,
	                        searchNodes: me.dataGrid.datagridMeta.searchNodes,
                            useChildQuery: false, //ищем через solr
                            searchShowInactive: me.dataGrid.options.searchShowInactive,
                            sort:me.dataGrid.datagridMeta.sort
                        });

                    if (showFilteredLabel) {
                        YAHOO.Bubbling.fire("showFilteredLabel");
                        YAHOO.Bubbling.fire("showSearchByAttributesLabel");
                    }

                    this.searchDialog.hide();
                }
            },

            /**
             * Обработчик для кнопки "Очитста" для аттрибутивного поиска
             *
             * @method onClearSearchClick
             * @param e {object} DomEvent
             * @param obj {object} Object passed back from addListener method
             */
            onClearSearchClick:function ADVSearch_onSearchClick(e, obj) {
                this.renderFormTemplate(this.currentForm, true, e, obj);
                YAHOO.Bubbling.fire("hideFilteredLabel");
                YAHOO.Bubbling.fire("hideSearchByAttributesLabel");
            },

            /**
             * Поиск
             * args - Объект с настройками поиска
             */
            performSearch: function ADVSearch__performSearch(args) {
                var searchConfig = args.searchConfig;

                this.currentSearchArgs = args;

                var notReplaceRS = args.notReplaceRS ;

                if (!notReplaceRS) {
                    this.dataTable.getRecordSet().reset();
                    this.dataTable.render();
                }

                var me = this;

                var loadingMessage = Alfresco.util.PopupManager.displayMessage({
                    displayTime: 0,
                    text: $html(this.msg("label.loading")),
                    spanClass: "wait",
                    noEscape: true
                });

                //Обработчик на успех
                function successHandler(sRequest, oResponse, oPayload) {
                    loadingMessage.hide();

                    me.searchStarted = false;
                    // update current state on success
                    me.currentSearchConfig = searchConfig;

                    if (oResponse.meta.startIndex > oResponse.meta.totalRecords) {
                        oResponse.meta.startIndex = 0;
                    }
                    oResponse.meta.pagination =
                    {
                        rowsPerPage: me.dataGrid.options.pageSize,
                        recordOffset: oResponse.meta.startIndex
                    };

                    var sotredBy = me.dataTable.get("sortedBy");

                    if (!notReplaceRS) {//по старому пути
                        me.dataTable.onDataReturnInitializeTable.call(me.dataTable, sRequest, oResponse, oResponse.meta);
                    } else {
                        if (args.offset != null && args.offset < oResponse.meta.totalRecords) { // проверка на случай возможных ошибок в гриде - исключаем их
                            me.dataTable.addRows(oResponse.results, me.dataTable.getRecordSet().getRecords().length);
                        }
                    }

                    me.dataTable.set("sortedBy", sotredBy);
                    YAHOO.Bubbling.fire("onSearchSuccess", {
                        bubblingLabel: this.bubblingLabel
                    });

                    //выводим предупреждающее сообщение, если достигли лимита и у нас не безграничный грид
                    if (!me.options.unlimited && (oResponse.results && oResponse.results.length >= me.options.maxSearchResults)) {
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime: 3,
                                text: this.msg("label.limit_reached")
                            });
                    }

                    me.dataGrid.addFooter();

                    if (me.options.unlimited) {
                        var ROW_HEIGHT = 25;
                        var ROW_HEIGHT_WITH_BORDER = 26;
                        var firstRowId = me.dataTable.getRecordSet().getRecords().length > 0 ? me.dataTable.getRecordSet().getRecords()[0].getId() : null;
                        var rowHeight = 0;
                        if (firstRowId != null) {
                            var rowStyle = Dom.getStyle(firstRowId, "height");
                            rowHeight = rowStyle ? rowStyle.replace("px", "") : ROW_HEIGHT_WITH_BORDER;
                        } else {
                            rowHeight = ROW_HEIGHT_WITH_BORDER; //(высота 25 + граница 1) - хедер + пустая строка
                        }


                        var newHeight = (me.dataTable.getRecordSet().getRecords().length) * (rowHeight);

                        // проверим, достигли ли лимита
                        if (me.dataTable.getRecordSet().getRecords().length >= oResponse.meta.totalRecords) {
                            me.dataGrid.loadComplete = true; // грид полностью загружен
                            newHeight = (oResponse.meta.totalRecords > 0 ? oResponse.meta.totalRecords : 1)*rowHeight + ROW_HEIGHT_WITH_BORDER;
                        } else {
                            me.dataGrid.loadComplete = false;
                        }
                        YAHOO.util.Dom.setStyle(this.id + "-grid", "height", newHeight + "px"); // фиксируем новую высоту
                        if (!me.dataGrid.loadComplete) { // свдвигаем скролл вверх - для удобства работы с ним
                            var gridContainer = YAHOO.util.Dom.get(this.id + "-grid");
                            if (gridContainer) {
                                gridContainer.scrollTop = YAHOO.util.Dom.get(this.id + "-grid").scrollTop - ROW_HEIGHT;
                            }
                        }

                    }
                }

                // Обработчик на неудачу
                function failureHandler(sRequest, oResponse) {
                    loadingMessage.hide();

                    me.searchStarted = false;
                    me.dataGrid.loadComplete = false;
                    if (oResponse.status == 401) {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    } else {
	                    Alfresco.util.PopupManager.displayMessage(
		                    {
			                    text:me.msg("message.datagrid.load-data.failure")
		                    });
                    }
                }

                this.dataSource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON); // для предотвращения ошибок
                var searchParams = this.prepareSearchParams(args);
                this.dataSource.sendRequest(YAHOO.lang.JSON.stringify(searchParams),
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            /**
             * Обработчки события  "doSearch"
             */
            onSearch:function AdvSearch_onSearch(layer, args) {
                var obj = args[1];
                if (!obj.bubblingLabel || !this.bubblingLabel || obj.bubblingLabel == this.bubblingLabel){
                    this.performSearch(obj);
                }
            },

            exportData: function exportAllData_function(isAll) {
                if (this.currentSearchArgs == null) return;
                var parameters = this.prepareSearchParams(this.currentSearchArgs);
                parameters.columns = this.dataColumns;
                if (!isAll) {
                    var items = this.dataGrid.getSelectedItems();
                    var currentSelectedItems = [];
                    for (var i = 0; i < items.length; i++) {
                        currentSelectedItems.push(items[i].nodeRef);
                    }
                    parameters.params.searchNodes = currentSelectedItems.join(",");
                }
                var form = document.createElement("form");
                form.enctype = "multipart/form-data";
                form.action = Alfresco.constants.PROXY_URI + "lecm/search/export";
                form.method = "POST";

                var inputFileName = document.createElement("input");
                inputFileName.type = "hidden";
                inputFileName.name = "parameters";
                inputFileName.value = JSON.stringify(parameters);
                form.appendChild(inputFileName);

                var timeZoneOffset = document.createElement("input");
                timeZoneOffset.type = "hidden";
                timeZoneOffset.name = "timeZoneOffset";
                timeZoneOffset.value = encodeURIComponent(new Date().getTimezoneOffset());
                form.appendChild(timeZoneOffset);

                document.body.appendChild(form);

                form.submit();
            },

            prepareSearchParams: function prepareSearchParams_function(args) {
                var searchConfig = args.searchConfig,
                    searchShowInactive = args.searchShowInactive,
                    parent = args.parent,
                    searchNodes = args.searchNodes,
                    itemType = args.itemType,
                    sort = args.sort,
                    useChildQuery = args.useChildQuery ? args.useChildQuery : false,
                    offset = args.offset ? args.offset : -1;

                // вернуть следующие поля для элемента(строки)
                var reqFields = [];
                var reqNameSubstituteStrings = [];
                for (var i = 0, ii = this.dataColumns.length; i < ii; i++) {
                    var column = this.dataColumns[i],
                        columnName = column.name.replace(":", "_");
                    reqFields.push(columnName);
                    reqNameSubstituteStrings.push(column.nameSubstituteString);
                }
                var fields = reqFields.join(",");
                var nameSubstituteStrings = reqNameSubstituteStrings.join(",");

                // дополнительный фильтр из адресной строки (или параметров)
                var successFilters = args.filter;
                if (!successFilters) {
                    successFilters = this.dataGrid.currentFilters;
                }
                var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
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
                        successFilters.splice(index, 1);
                    }
                    successFilters.push(filterFromUrl);
                }

                return this.buildSearchParams(parent, searchNodes, itemType, sort != null ? sort : "cm:name|true", searchConfig, fields, nameSubstituteStrings, searchShowInactive, offset, successFilters, useChildQuery);
            },

            buildSearchParams:function ADVSearch__buildSearchParams(parent, searchNodes, itemType, sort, searchConfig, searchFields, dataRequestNameSubstituteStrings, searchShowInactive, offset, additionalFilters, useChildQuery) {
                // ВСЕГДА должно существовать значение по умолчанию. Для объектов и строк - это должна быть пустая строка
                if (searchConfig && searchConfig.formData && typeof searchConfig.formData == "object") {
                    searchConfig.formData = YAHOO.lang.JSON.stringify(searchConfig.formData);
                }
                if (searchConfig && searchConfig.fullTextSearch && typeof searchConfig.fullTextSearch == "object") {
                    searchConfig.fullTextSearch = YAHOO.lang.JSON.stringify(searchConfig.fullTextSearch);
                }
                var startIndex = 0;
                if (offset >= 0 && ((this.dataGrid.options.usePagination && !this.dataGrid.options.disableDynamicPagination) || this.dataGrid.options.unlimited)) {
                    startIndex = offset;
                }

                var filters = [];
                if (additionalFilters && additionalFilters.length > 0) {
                    filters = YAHOO.lang.JSON.stringify(additionalFilters);
                }
                return {
                    params: {
                        parent: parent != null ? parent : "",
	                    searchNodes: searchNodes != null ? searchNodes.toString() : "",
                        itemType: itemType != null ? itemType : "",
                        searchConfig: searchConfig != null ? YAHOO.lang.JSON.stringify(searchConfig) : "",
                        maxResults: (this.dataGrid.options.usePagination && !this.dataGrid.options.disableDynamicPagination) ?
                            this.dataGrid.options.pageSize : (this.options.unlimited && this.dataTable.getRecordSet().getRecords().length > 0 ? this.options.loopSize : this.dataGrid.options.maxResults),
                        fields: searchFields != null ? searchFields : "",
                        nameSubstituteStrings: dataRequestNameSubstituteStrings,
                        showInactive: searchShowInactive != null ? searchShowInactive : "false",
                        startIndex: startIndex,
                        sort: sort != null ? sort : "",
                        filter: filters ? filters : "",
                        useChildQuery: useChildQuery != null ?  useChildQuery : false
                    }
                };
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit:function ADVSearch_onBeforeFormRuntimeInit(layer, args) {
                // extract the current form runtime - so we can reference it later
                if (this.currentForm && args[1].runtime.formId == (this.options.searchFormId + "-" + this.currentForm.type.split(":").join("_") + "-form")) {
                    this.currentForm.runtime = args[1].runtime;
                }
            },

            /**
             * метод для вывода диалога с аттриюбутивным поиском
             */
            showDialog: function ADVSearch_showDialog(metaData) {
                var defaultForm = new Object();
                defaultForm.id = "search";
                defaultForm.type = metaData.itemType;

                if (this.options.showExtendSearchBlock) { // если заданы соответствующая опция
                    if (this.searchDialog == null){
                        // Если SearchBlock уже есть в разметке в body (остался с предыдущей "страницы")
                        // удаляем его
                        // Это актуально для раздела "Администрирование"
                        var searchBlockInBody = Selector.query("body > div > #searchBlock", null, true);
                        if (searchBlockInBody) {
                            searchBlockInBody.parentNode.removeChild(searchBlockInBody);
                        }
                        // создаем диалог
                        this.searchDialog = Alfresco.util.createYUIPanel("searchBlock",
                            {
                                width:"800px"
                            });
                        // создаем кнопки
                        this.widgets.searchButton = Alfresco.util.createYUIButton(this, "searchBlock-search-button", this.onSearchClick, {}, Dom.get("searchBlock-search-button"));
                        this.widgets.clearSearchButton = Alfresco.util.createYUIButton(this, "searchBlock-clearSearch-button", this.onClearSearchClick, {}, Dom.get("searchBlock-clearSearch-button"));
                    }

                    if(!this.currentForm || !this.currentForm.htmlid) { // форма ещё создана или не проинициализирована
                        // создаем форму
                        this.renderFormTemplate(defaultForm);
                    } else {
                        if (this.searchDialog != null) {
                            this.searchDialog.show();
                        }
                    }
                }
            },

            clear: function ADVSearch_clear(grid) {
                this.dataTable = grid.widgets.dataTable;
                this.dataSource = grid.widgets.dataSource;
                this.dataColumns = grid.datagridColumns;
                this.bubblingLabel = grid.options.bubblingLabel;
                this.dataGrid = grid;
                if (this.currentForm) {
                    this.renderFormTemplate(this.currentForm, true);
                }
                YAHOO.Bubbling.fire("hideFilteredLabel");
                YAHOO.Bubbling.fire("hideSearchByAttributesLabel");
            }
        });
})();