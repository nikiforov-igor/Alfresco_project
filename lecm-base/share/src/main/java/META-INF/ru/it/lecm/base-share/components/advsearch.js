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
            dataTable:null,
            dataSource:null,
            dataColumns:{},

            currentSearchTerm: "",
            currentSearchSort:"",
            currentSearchFilter:"",
            currentSearchQuery:"",

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
                searchRepo:true,

                minSearchTermLength:3,

                maxSearchResults:3000
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

                var defaultForm = new Object();
                defaultForm.id = "search";
                defaultForm.type = metaData.itemType;

                // search YUI button and menus
                this.widgets.searchButton1 = Alfresco.util.createYUIButton(this, "search-button-1", this.onSearchClick);
                this.widgets.searchButton2 = Alfresco.util.createYUIButton(this, "search-button-2", this.onSearchClick);
                this.widgets.clearButton = Alfresco.util.createYUIButton(this, "clear-button", this.onClearClick);

                // render initial form template
                this.renderFormTemplate(defaultForm, true);

                // register the "enter" event on the search text field
                var queryInput = Dom.get(this.id + "-search-text");

                this.widgets.enterListener = new YAHOO.util.KeyListener(queryInput,
                    {
                        keys:YAHOO.util.KeyListener.KEY.ENTER
                    },
                    {
                        fn:me._searchEnterHandler,
                        scope:this,
                        correctScope:true
                    }, "keydown").enable();

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
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
                this.currentForm.repopulate = repopulate;

                var containerDiv = Dom.get(this.id + "-forms");

                var visibleFormFn = function () {
                    // hide visible form if any
                    for (var i = 0, c = containerDiv.children; i < c.length; i++) {
                        if (!Dom.hasClass(c[i], "hidden")) {
                            Dom.addClass(c[i], "hidden");
                            break;
                        }
                    }
                    // display cached form element
                    Dom.removeClass(form.htmlid, "hidden");
                    // reset focus to search input textbox
                    Dom.get(this.id + "-search-text").focus();
                };

                if (!form.htmlid) {
                    // generate child container div for this form
                    var htmlid = this.id + "_" + containerDiv.children.length;
                    var formDiv = document.createElement("div");
                    formDiv.id = htmlid;
                    Dom.addClass(formDiv, "hidden");
                    Dom.addClass(formDiv, "share-form");

                    // cache htmlid so we know the form is present on the form
                    form.htmlid = htmlid;

                    // load the form component for the appropriate type
                    var formUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind=type&itemId={itemId}&formId={formId}&mode=edit&showSubmitButton=false&showCancelButton=false",
                        {
                            itemId:form.type,
                            formId:form.id
                        });
                    var formData =
                    {
                        htmlid:htmlid
                    };
                    Alfresco.util.Ajax.request(
                        {
                            url:formUrl,
                            dataObj:formData,
                            successCallback:{
                                fn:function ADVSearch_onFormTemplateLoaded(response) {
                                    // Inject the template from the XHR request into the child container div
                                    formDiv.innerHTML = response.serverResponse.responseText;
                                    containerDiv.appendChild(formDiv);
                                    visibleFormFn.call(this);
                                },
                                scope:this
                            },
                            failureMessage:"Could not load form component '" + formUrl + "'.",
                            scope:this,
                            execScripts:true
                        });
                } else {
                    visibleFormFn.call(this);
                }
            },

            /**
             * Repopulate currently displayed Form fields based on saved query data
             *
             * @method repopulateCurrentForm
             */
            repopulateCurrentForm:function ADVSearch_repopulateCurrentForm() {
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

            /**
             * Event handler that gets fired when user clicks the Search button.
             *
             * @method onSearchClick
             * @param e {object} DomEvent
             * @param obj {object} Object passed back from addListener method
             */
            onSearchClick:function ADVSearch_onSearchClick(e, obj) {
                var me = this;

                // retrieve form data structure directly from the runtime
                var formData = me.currentForm.runtime.getFormData();
                // add DD type to form data structure
                formData.datatype = me.currentForm.type;

                var termsValues = Dom.get(this.id + "-search-text").value;

                var terms = termsValues.split(",");

                var termsString = "";
                var columns = this.dataColumns;

                for (var i = 0; i < columns.length; i++) {
                    if (columns[i].dataType == "text") {
                        for (var j = 0; j < terms.length; j++) {
                            var t = terms[j];
                            if (t.length > 0) {
                                termsString += columns[i].name + ":" + YAHOO.lang.trim(t) + "#";
                            }
                        }
                    }
                }

                if (termsString.length > 0) {
                    termsString = termsString.substring(0, (termsString.length) - 1); // delete last #
                }

                var query = YAHOO.lang.JSON.stringify(formData);

                this._performSearch(
                    {
                        searchTerm:termsString,
                        searchSort:"",
                        searchQuery:query,
                        searchFilter:""
                    });
            },

            _performSearch:function Search__performSearch(args) {
                var searchTerm = YAHOO.lang.trim(args.searchTerm),
                    searchSort = args.searchSort,
                    searchQuery = args.searchQuery,
                    searchFilter = args.searchFilter,
	                fullTextSearch = args.fullTextSearch;

                var reqFields = [];
                for (var i = 0, ii = this.dataColumns.length; i < ii; i++) {
                    var column = this.dataColumns[i],
                        columnName = column.name.replace(":", "_");
                    reqFields.push(columnName);
                }
                var fields = reqFields.join(",");
                if (searchQuery.length === 0 &&
                    searchTerm.replace(/\*/g, "").length < this.options.minSearchTermLength && searchFilter.length === 0) {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("message.minimum-length", this.options.minSearchTermLength)
                        });
                    return;
                }

                // empty results table
                this.dataTable.deleteRows(0, this.dataTable.getRecordSet().getLength());

                // update the ui to show that a search is on-going
                this.dataTable.set("MSG_EMPTY", "");
                this.dataTable.render();

                var me = this;
                // Success handler
                function successHandler(sRequest, oResponse, oPayload) {
                    // update current state on success
                    this.currentSearchTerm = searchTerm;
                    this.currentSearchSort = searchSort;
                    this.currentSearchFilter = searchFilter;
                    this.currentSearchQuery = searchQuery;
                    me.dataTable.onDataReturnInitializeTable.call(me.dataTable, sRequest, oResponse, oPayload);
                }

                // Failure handler
                function failureHandler(sRequest, oResponse) {
                    if (oResponse.status == 401) {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else {
                        try {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            me.dataTable.set("MSG_ERROR", response.message);
                            me.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                        }
                        catch (e) {
                            me.dataTable.render();
                        }
                    }
                }

                var searchParams = this._buildSearchParams(searchTerm, searchQuery, searchFilter, searchSort, fields, fullTextSearch);
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

            _buildSearchParams:function Search__buildSearchParams(searchTerm, searchQuery, searchFilter, searchSort, searchFields, fullTextSearch) {
                var request =
                {
                    params:{
                        term:searchTerm,
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
            },

            /**
             * Event handler called when the "beforeFormRuntimeInit" event is received
             */
            onBeforeFormRuntimeInit:function ADVSearch_onBeforeFormRuntimeInit(layer, args) {
                // extract the current form runtime - so we can reference it later
                this.currentForm.runtime = args[1].runtime;

                // Repopulate current form from url query data?
                if (this.currentForm.repopulate) {
                    this.currentForm.repopulate = false;
                    this.repopulateCurrentForm();
                }
            },

            /**
             * Search text box ENTER key event handler
             *
             * @method _searchEnterHandler
             */
            _searchEnterHandler:function ADVSearch__searchEnterHandler(e, args) {
                this.onSearchClick(e, args);
            }
        });
})();
