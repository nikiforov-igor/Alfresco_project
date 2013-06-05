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
    var Dom = YAHOO.util.Dom;
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
                showExtendSearchBlock: false,  // По умолчанию аттрибутивный поиск скрыт
                searchFormId: "searchBlock-forms"
            },

            /**
             * Currently visible Search Form object
             */
            currentForm:null,

            dataGrid: null, // Обратная ссылка на грид

            /**
             * Получение и отрисовка формы
             *
             * @method renderFormTemplate
             * @param form {Object} Form descriptor to render template for
             * @param repopulate {boolean} If true, repopulate form instance based on supplied data
             */
            renderFormTemplate:function ADVSearch_renderFormTemplate(form, isClearSearch, e, obj) {
	            if (isClearSearch == undefined) {
		            isClearSearch = false;
	            }
                // update current form state
                this.currentForm = form;

                var formDiv = Dom.get("searchBlock-forms"); // элемент в который будет отрисовываться форма
                form.htmlid = this.options.searchFormId;

                // load the form component for the appropriate type
                var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "/components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
                    {
                        itemId:form.type,
                        formId:form.id
                    });
                var formData =
                {
                    htmlid:form.htmlid
                };
                Alfresco.util.Ajax.request(
                    {
                        url:formUrl,
                        dataObj:formData,
                        successCallback:{
                            fn:function ADVSearch_onFormTemplateLoaded(response) {
                                formDiv.innerHTML = response.serverResponse.responseText;
                                if (this.searchDialog != null) {
	                                if (isClearSearch) {
                                        //сбрасываем на значение по умолчанию
                                        if (this.dataGrid.initialSearchConfig != null) {
                                            this.dataGrid.datagridMeta.searchConfig = YAHOO.lang.merge({}, this.dataGrid.initialSearchConfig);
                                        } else {
                                            this.dataGrid.datagridMeta.searchConfig = null;
                                        }
                                        //this.searchDialog.hide();
                                        this.performSearch({
                                            parent:this.dataGrid.datagridMeta.nodeRef,
                                            itemType:this.dataGrid.datagridMeta.itemType,
                                            searchConfig:this.dataGrid.datagridMeta.searchConfig,
                                            searchShowInactive:this.dataGrid.options.searchShowInactive
                                        });
	                                } else {
		                                this.searchDialog.show();
	                                }
                                }
                            },
                            scope:this
                        },
                        failureMessage:"Could not load form component '" + formUrl + "'.",
                        scope:this,
                        execScripts:true
                    });
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
                    sConfig.fullTextSearch = fullTextSearch;
                    this.performSearch(
                        {
                            searchConfig:sConfig,
                            searchShowInactive: me.dataGrid.options.searchShowInactive,
                            sort:me.dataGrid.datagridMeta.sort
                        });

	                if (showFilteredLabel) {
	                    YAHOO.Bubbling.fire("showFilteredLabel");
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
	        },

            /**
             * Поиск
             * args - Объект с настройками поиска
             */
            performSearch:function ADVSearch__performSearch(args) {
                var searchConfig = args.searchConfig,
                    searchShowInactive = args.searchShowInactive,
                    parent = args.parent,
                    itemType = args.itemType,
                    sort = args.sort;
                // дополнительный фильтр из адресной строки (или параметров)
                var successFilter = args.filter;
                if (!successFilter) {
                    var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
                    bookmarkedFilter = bookmarkedFilter || "none|/";
                    try {
                        while (bookmarkedFilter !== (bookmarkedFilter = decodeURIComponent(bookmarkedFilter))) {
                        }
                    }
                    catch (e) {
                        // Catch "malformed URI sequence" exception
                    }

                    var fnDecodeBookmarkedFilter = function DL_fnDecodeBookmarkedFilter(strFilter) {
                        var filters = strFilter.split("|"),
                            filterObj =
                            {
                                filterId: window.unescape(filters[0] || ""),
                                filterData: window.unescape(filters[1] || "")
                            };
                        return filterObj;
                    };

                    successFilter = fnDecodeBookmarkedFilter(bookmarkedFilter);
                }

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

                this.dataTable.getRecordSet().reset();
                this.dataTable.render();

                var me = this;

                // Запуск сообщения о загрузке
                var loadingMessage = null,
                    timerShowLoadingMessage = null;

                var fnShowLoadingMessage = function DataGrid_fnShowLoadingMessage() {
                    if (timerShowLoadingMessage) {
                        loadingMessage = Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime:0,
                                text:'<span class="wait">' + $html(this.msg("label.loading")) + '</span>',
                                noEscape:true
                            });

                        if (YAHOO.env.ua.ie > 0) {
                            this.loadingMessageShowing = true;
                        }
                        else {
                            loadingMessage.showEvent.subscribe(function () {
                                this.loadingMessageShowing = true;
                            }, this, true);
                        }
                    }
                };

                this.loadingMessageShowing = false;
                timerShowLoadingMessage = YAHOO.lang.later(500, this, fnShowLoadingMessage);

                var destroyLoaderMessage = function DataGrid__uDG_destroyLoaderMessage() {
                    if (timerShowLoadingMessage) {
                        // Stop the "slow loading" timed function
                        timerShowLoadingMessage.cancel();
                        timerShowLoadingMessage = null;
                    }
                    if (loadingMessage) {
                        if (this.loadingMessageShowing) {
                            // Safe to destroy
                            loadingMessage.destroy();
                            loadingMessage = null;
                        }
                        else {
                            // Wait and try again later. Scope doesn't get set correctly with "this"
                            YAHOO.lang.later(100, me, destroyLoaderMessage);
                        }
                    }
                };

                //Обработчик на успех
                function successHandler(sRequest, oResponse, oPayload) {
                    destroyLoaderMessage();
                    me.searchStarted = false;
                    // update current state on success
                    me.currentSearchConfig = searchConfig;

                    if (oResponse.meta.startIndex > oResponse.meta.totalRecords){
                        oResponse.meta.startIndex = 0;
                    }
                    oResponse.meta.pagination =
                    {
                        rowsPerPage: me.dataGrid.options.pageSize,
                        recordOffset: oResponse.meta.startIndex
                    };

                    YAHOO.Bubbling.fire("filterChanged", successFilter);
                    me.dataTable.onDataReturnInitializeTable.call(me.dataTable, sRequest, oResponse, oResponse.meta);
                    YAHOO.Bubbling.fire("onSearchSuccess", successFilter);

                    //выводим предупреждающее сообщение, если достигли лимита
                    if (oResponse.results && oResponse.results.length  >= me.options.maxSearchResults) {
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime:3,
                                text: this.msg("label.limit_reached")
                            });
                    }
                }

                // Обработчик на неудачу
                function failureHandler(sRequest, oResponse) {
                    destroyLoaderMessage();
                    me.searchStarted = false;
                    if (oResponse.status == 401) {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else {
                        try {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            me.dataTable.set("MSG_ERROR", response.message);
                            me.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                            if (oResponse.status == 404) {
                                // Site or container not found - deactivate controls
                                YAHOO.Bubbling.fire("deactivateAllControls");
                            }
                        }
                        catch (e) {
                            me.dataTable.render();
                        }
                    }
                }

                this.dataSource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON); // для предотвращения ошибок
                var searchParams = this.buildSearchParams(parent, itemType, sort != null ? sort : "cm:name|true", searchConfig, fields, nameSubstituteStrings, searchShowInactive, -1, successFilter);
                this.dataSource.sendRequest(YAHOO.lang.JSON.stringify(searchParams),
                    {
                        success:successHandler,
                        failure:failureHandler,
                        scope:this
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

            buildSearchParams:function ADVSearch__buildSearchParams(parent, itemType, sort, searchConfig, searchFields, dataRequestNameSubstituteStrings, searchShowInactive, offset, additionalFilter) {
                // ВСЕГДА должно существовать значение по умолчанию. Для объектов и строк - это должна быть пустая строка
                if (searchConfig && searchConfig.formData && typeof searchConfig.formData == "object") {
                    searchConfig.formData = YAHOO.lang.JSON.stringify(searchConfig.formData);
                }
                if (searchConfig && searchConfig.fullTextSearch && typeof searchConfig.fullTextSearch == "object") {
                    searchConfig.fullTextSearch = YAHOO.lang.JSON.stringify(searchConfig.fullTextSearch);
                }
                var startIndex = 0;
                if (offset >= 0 && this.dataGrid.options.useDynamicPagination) {
                    startIndex = offset;
                }

                var filter = null;
                if (additionalFilter && additionalFilter.filterId) {
                    filter = YAHOO.lang.JSON.stringify(additionalFilter);
                }
                return {
                    params: {
                        parent: parent != null ? parent : "",
                        itemType: itemType != null ? itemType : "",
                        searchConfig: searchConfig != null ? YAHOO.lang.JSON.stringify(searchConfig) : "",
                        maxResults: this.dataGrid.options.useDynamicPagination ? this.dataGrid.options.pageSize : this.dataGrid.options.maxResults,
                        fields: searchFields != null ? searchFields : "",
                        nameSubstituteStrings: dataRequestNameSubstituteStrings,
                        showInactive: searchShowInactive != null ? searchShowInactive : "false",
                        startIndex: startIndex,
                        sort: sort != null ? sort : "",
                        filter: filter ? filter : ""
                    }
                };
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit:function ADVSearch_onBeforeFormRuntimeInit(layer, args) {
                // extract the current form runtime - so we can reference it later
                if (this.currentForm && args[1].runtime.formId == (this.options.searchFormId + "-form")) {
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
                    if(this.searchDialog == null){
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
            }
        });
})();