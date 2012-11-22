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
    LogicECM.AdvancedSearch = function (htmlId) {
        LogicECM.AdvancedSearch.superclass.constructor.call(this, "LogicECM.AdvancedSearch", htmlId, ["button", "container", "datasource", "datatable", "paginator"]);

        YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
        YAHOO.Bubbling.on("doSearch", this.onSearch, this);
    };

    YAHOO.extend(LogicECM.AdvancedSearch, Alfresco.component.Base,
        {
            searchDialog: null, // окно атрибутивного поиска
            dataTable:null,   // DataTable из грида
            dataSource:null,  // DataSource из грида
            dataColumns:{},   // набор колонок из датагрида
            datagridMeta:{}, // метаданные из датагрида

            searchStarted: false, // флаг, что идет поиск

            // сохраненные настройки предыдущего поиска
            currentSearchSort:"",
            currentSearchFilter:"",
            currentSearchQuery:"",
            currentFullTextSearch:{},
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:{
                maxSearchResults:1000,
                showExtendSearchBlock: false  // По умолчанию аттрибутивный поиск скрыт
            },

            /**
             * Currently visible Search Form object
             */
            currentForm:null,

            /**
             * Начальная инициализация поиска (обязательна
             */
            initSearch:function ADVSearch_onReady(metaData) {
                var me = this;
                this.datagridMeta = metaData;
                // DataSource default definition
                if (!this.dataSource) {
                    var uriSearchResults = Alfresco.constants.PROXY_URI_RELATIVE + "lecm/search";
                    this.dataSource = new YAHOO.util.DataSource(uriSearchResults,
                        {
                            connMethodPost: true,
                            responseType:YAHOO.util.DataSource.TYPE_JSON,
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

                }

                if (this.options.showExtendSearchBlock) {//включена опция
                    // создаем диалог
                    this.searchDialog = Alfresco.util.createYUIPanel("searchBlock",
                        {
                            width:"800px"
                        });
                    // создаем кнопки
                    this.widgets.searchButton = Alfresco.util.createYUIButton(this, "searchBlock-search-button", this.onSearchClick, {}, Dom.get("searchBlock-search-button"));
                }
            },

            /**
             * Получение и отрисовка формы
             *
             * @method renderFormTemplate
             * @param form {Object} Form descriptor to render template for
             * @param repopulate {boolean} If true, repopulate form instance based on supplied data
             */
            renderFormTemplate:function ADVSearch_renderFormTemplate(form) {
                // update current form state
                this.currentForm = form;

                var formDiv = Dom.get("searchBlock-forms"); // элемент в который будет отрисовываться форма
                form.htmlid = "searchBlock-forms";

                // load the form component for the appropriate type
                var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
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
            onSearchClick:function ADVSearch_onSearchClick(e, obj) {
                var me = this;
                if (!me.searchStarted) { // если поиск еще не начат (для предотвращения повторного взова метода)
                    me.searchStarted = true; // блокируем остальные запросы
                    // получает данные из формы
                    var formData = me.currentForm.runtime.getFormData();
                    formData.datatype = me.currentForm.type;

                    // формируем запрос
                    var query = YAHOO.lang.JSON.stringify(formData);

                    // включаем поиск во всех вложенных директория относительно родительской
                    var fullTextSearch = {
                        parentNodeRef:me.datagridMeta.nodeRef
                    };

                    this._performSearch(
                        {
                            searchSort:me.currentSearchSort, // сохраняем текущую сортировку
                            searchQuery:query, // поиск по заполненной форме (тип + данные)
                            searchFilter:"", // сбрасываем фильтр
                            fullTextSearch:YAHOO.lang.JSON.stringify(fullTextSearch)// поиск во всех вложенных директориях
                        });

                    this.hideDialog();
                }
            },

            /**
             * Поиск
             * args - Объект с настройками поиска
             */
            _performSearch:function ADVSearch__performSearch(args) {
                var searchSort = args.searchSort,
                    searchQuery = args.searchQuery,
                    searchFilter = args.searchFilter,
                    fullTextSearch = args.fullTextSearch;

                // вернуть следующие поля для элемента(строки)
                var reqFields = [];
                for (var i = 0, ii = this.dataColumns.length; i < ii; i++) {
                    var column = this.dataColumns[i],
                        columnName = column.name.replace(":", "_");
                    reqFields.push(columnName);
                }
                var fields = reqFields.join(",");

                //очистить таблицу и отрисовать
                this.dataTable.deleteRows(0, this.dataTable.getRecordSet().getLength());
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
                    me.currentSearchSort = searchSort;
                    me.currentSearchFilter = searchFilter;
                    me.currentSearchQuery = searchQuery;
                    me.currentFullTextSearch = fullTextSearch;
                    me.dataTable.onDataReturnInitializeTable.call(me.dataTable, sRequest, oResponse, oPayload);
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
                var searchParams = this._buildSearchParams(searchQuery, searchFilter, searchSort, fields, fullTextSearch);
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
                if (!obj.bubblingLabel || obj.bubblingLabel == this.datagridMeta.bubblingLabel){
                    this._performSearch(obj);
                }
            },

            _buildSearchParams:function ADVSearch__buildSearchParams(searchQuery, searchFilter, searchSort, searchFields, fullTextSearch) {
                var request =
                {
                    params:{
                        sort:searchSort != null ? searchSort : "",
                        query:searchQuery != null ? searchQuery : "",
                        filter:searchFilter != null ? searchFilter : "" ,
                        maxResults:this.options.maxSearchResults + 1, // to calculate whether more results were available,
                        fields:searchFields,
                        fullTextSearch: fullTextSearch != null ? fullTextSearch : ""
                    }
                };
                return request;
            },

            /**
             * Очистка input и вызов поиска по умолчанию (метод пока не используется)
             */
            onClearClick:function ADVSearch_onSearchClick(e, obj) {
                var queryInput = Dom.get("full-text-search");
                queryInput.value = "";
                queryInput.focus();

                this._performSearch(
                    {
                        searchSort:this.currentSearchSort,
                        searchQuery:this.currentSearchQuery,
                        searchFilter:this.currentSearchFilter,
                        fullTextSearch:"" //убрать полнотекстовый поиск
                    });
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit:function ADVSearch_onBeforeFormRuntimeInit(layer, args) {
                // extract the current form runtime - so we can reference it later
                if (this.currentForm) {
                    this.currentForm.runtime = args[1].runtime;
                }
            },

            /**
             * метод для скрытия диалога с аттриюбутивным поиском
             */
            hideDialog: function ADVSearch_hideDialog() {
                if (this.searchDialog != null) {
                    this.searchDialog.hide();
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
                    if(!this.currentForm || !this.currentForm.htmlid) { // форма ещё создана или не проинициализирована
                        // создаем форму
                        this.renderFormTemplate(defaultForm);
                    }

                    // Finally show the component body here to prevent UI artifacts on YUI button decoration
                    Dom.setStyle("searchBlock", "display", "block");

                    if (this.searchDialog != null) {
                        this.searchDialog.show();
                    }
                }
            }
        });
})();