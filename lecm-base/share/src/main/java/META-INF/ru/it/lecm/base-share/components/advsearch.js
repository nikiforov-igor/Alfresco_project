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
        LogicECM.AdvancedSearch.superclass.constructor.call(this, "LogicECM.AdvancedSearch", htmlId, ["button", "container"]);

        YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
        YAHOO.Bubbling.on("doSearch", this.onSearch, this);
    };

    YAHOO.extend(LogicECM.AdvancedSearch, Alfresco.component.Base,
        {
            searchDialog: null,
            dataTable:null,
            dataSource:null,
            dataColumns:{},
            datagridMeta:{},

            searchStarted: false,

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
                /**
                 * Previously saved query, if any
                 *
                 * @property savedQuery
                 * @type string
                 */
                savedQuery:"",

                /**
                 * It is possible to disable searching entire repo via config
                 *
                 * @property searchRepo
                 * @type boolean
                 */
                //searchRepo:true,

                //minSearchTermLength:3,

                maxSearchResults:3000,
                // default hide search block
                showExtendSearchBlock: false
            },

            /**
             * Currently visible Search Form object
             */
            currentForm:null,

            /**
             * Fired by YUI when parent element is available for scripting.
             * Component initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
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

                } else {
                    this.dataSource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON);
                }

                // YUI Paginator definition
                var handlePagination = function Search_handlePagination(state, me) {
                    me.currentPage = state.page;
                    me.widgets.paginator.setState(state);
                };
                this.widgets.paginator = new YAHOO.widget.Paginator(
                    {
                        containers:[this.id + "-paginator-top", this.id + "-paginator-bottom"],
                        rowsPerPage:this.options.pageSize,
                        initialPage:1,
                        template:this.msg("pagination.template"),
                        pageReportTemplate:this.msg("pagination.template.page-report"),
                        previousPageLinkLabel:this.msg("pagination.previousPageLinkLabel"),
                        nextPageLinkLabel:this.msg("pagination.nextPageLinkLabel")
                    });
                this.widgets.paginator.subscribe("changeRequest", handlePagination, this);

                if (this.options.showExtendSearchBlock) {
                // создаем диалог
                this.searchDialog = Alfresco.util.createYUIPanel("searchBlock",
                    {
                        width:"800px"
                    });
                }
            },

            /**
             * Loads or retrieves from cache the Form template for a given content type
             *
             * @method renderFormTemplate
             * @param form {Object} Form descriptor to render template for
             * @param repopulate {boolean} If true, repopulate form instance based on supplied data
             */
            renderFormTemplate:function ADVSearch_renderFormTemplate(form, repopulate) {
                // update current form state
                this.currentForm = form;

                var formDiv = Dom.get(this.id + "-forms");
                form.htmlid = this.id + "-forms";

                Dom.addClass(formDiv, "hidden");
                //Dom.addClass(formDiv, "share-form");

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
                                // Inject the template from the XHR request into the child container div
                                formDiv.innerHTML = response.serverResponse.responseText;
                                // display cached form element
                                Dom.removeClass(form.htmlid, "hidden");
                                if (this.searchDialog) {
                                    this.searchDialog.show();
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
             * Repopulate currently displayed Form fields based on saved query data
             *
             * @method repopulateCurrentForm
             */
            /*repopulateCurrentForm:function ADVSearch_repopulateCurrentForm() {
                if (this.options.savedQuery.length !== 0) {
                    var savedQuery = YAHOO.lang.JSON.parse(this.options.savedQuery);
                    var elForm = Dom.get(this.currentForm.runtime.formId);

                    for (var i = 0, j = elForm.elements.length; i < j; i++) {
                        var element = elForm.elements[i];
                        var name = element.name;
                        if (name != undefined && name !== "-") {
                            var savedValue = savedQuery[name];
                            if (savedValue !== undefined) {
                                if (element.type === "checkbox" || element.type === "radio") {
                                    element.checked = (savedValue === "true");
                                }
                                else {
                                    element.value = savedValue;
                                }
                            }
                        }
                    }
                }
            },
*/
            /**
             * Event handler that gets fired when user clicks the Search button.
             *
             * @method onSearchClick
             * @param e {object} DomEvent
             * @param obj {object} Object passed back from addListener method
             */
            onSearchClick:function ADVSearch_onSearchClick(e, obj) {
                var me = this;
                if (!me.searchStarted) {
                    me.searchStarted = true;
                    // retrieve form data structure directly from the runtime
                    var formData = me.currentForm.runtime.getFormData();
                    // add DD type to form data structure
                    formData.datatype = me.currentForm.type;

                    var query = YAHOO.lang.JSON.stringify(formData);

                    var fullTextSearch = {
                        parentNodeRef:me.datagridMeta.nodeRef
                    };

                    this._performSearch(
                        {
                            searchSort:me.currentSearchSort,
                            searchQuery:query, // поиск по заполненной форме (тип + данные)
                            searchFilter:"", // сбросить фильтр
                            fullTextSearch:YAHOO.lang.JSON.stringify(fullTextSearch)// поиск во всех вложенных директориях
                        });

                    this.hideDialog();
                }
            },

            _performSearch:function Search__performSearch(args) {
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

            onSearch:function AdvSearch_onSearch(layer, args) {
                var obj = args[1];
                this._performSearch(obj);
            },

            _buildSearchParams:function Search__buildSearchParams(searchQuery, searchFilter, searchSort, searchFields, fullTextSearch) {
                var request =
                {
                    params:{
                        sort:searchSort,
                        query:searchQuery,
                        filter:searchFilter,
                        maxResults:this.options.maxSearchResults + 1, // to calculate whether more results were available,
                        fields:searchFields,
	                    fullTextSearch: fullTextSearch != null ? fullTextSearch : ""
                    }
                };
                return request;
            },

            onClearClick:function ADVSearch_onSearchClick(e, obj) {
                var queryInput = Dom.get(this.id + "-search-text");
                queryInput.value = "";
                queryInput.focus();

                this.datagridMeta.filter = this.currentSearchFilter;
                this.datagridMeta.fullTextSearch = ""; // убрать полнотекстовый поиск
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit:function ADVSearch_onBeforeFormRuntimeInit(layer, args) {
                // extract the current form runtime - so we can reference it later
                if (this.currentForm) {
                    this.currentForm.runtime = args[1].runtime;

                    // Repopulate current form from url query data?
                    if (this.currentForm.repopulate) {
                        this.currentForm.repopulate = false;
                    }
                }
            },

            hideDialog: function ADVSearch_hideDialog() {
                if (this.searchDialog != null) {
                        this.searchDialog.hide();
                }
            },

            showDialog: function ADVSearch_showDialog(metaData) {
                var defaultForm = new Object();
                defaultForm.id = "search";
                defaultForm.type = metaData.itemType;

                if (this.options.showExtendSearchBlock) {
                    // создаем кнопки
                    this.widgets.searchButton1 = Alfresco.util.createYUIButton(this, "search-button-1", this.onSearchClick);
                    this.widgets.searchButton2 = Alfresco.util.createYUIButton(this, "search-button-2", this.onSearchClick);

                    // показываем форму
                    this.renderFormTemplate(defaultForm, true);

                    // Finally show the component body here to prevent UI artifacts on YUI button decoration
                    Dom.setStyle("searchBlock", "display", "block");

                    if (this.searchDialog != null) {
                        this.searchDialog.show();
                    }
                }
            }
        });
})();
