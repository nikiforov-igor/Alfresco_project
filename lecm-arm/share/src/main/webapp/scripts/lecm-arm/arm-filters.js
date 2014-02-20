if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.Filters = function (htmlId) {
        LogicECM.module.ARM.Filters.superclass.constructor.call(this, "LogicECM.module.ARM.Filters", htmlId);

        this.avaiableFilters = [];
        this.currentFilters = [];
        this.currentQuery = null;
        this.bubblingLabel = null;
        this.currentNode = null;

        YAHOO.Bubbling.on("updateArmFilters", this.onUpdateAvaiableFilters, this);
        YAHOO.Bubbling.on("updateCurrentFilters", this.onUpdateCurrentFilters, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.Filters, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.Filters.prototype,
        {
            avaiableFilters: [],
            currentFilters: [],

            currentQuery: null,

            currentNode: null,

            bubblingLabel: null,

            onReady: function () {
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "inherit");
                YAHOO.util.Event.on(this.id + "-delete-all-link", 'click', this.deleteAllFilters, null, this);
                this.updateCurrentFiltersForm(false);
            },

            onUpdateAvaiableFilters: function (layer, args) {
                //сработает при выборе узла в дереве
                var currentNode = args[1].currentNode;
                if (currentNode != null) {
                    this.currentNode = currentNode;
                    var filters = currentNode.data.filters;
                    var hasFilters = filters != null && filters.length > 0;
                    if (hasFilters) {
                        this.avaiableFilters = [];
                        for (var i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            this.avaiableFilters.push(filter);
                        }

                    } else {
                        this.avaiableFilters = [];
                    }
                }
                //this.updateCurrentFiltersForm(true);
            },

            onUpdateCurrentFilters: function (layer, args) {
                var filters = args[1].filtersData;
                if (filters != null) {
                    //обновим сброшенные - оставим те пришли(будут обновлены) и те, которых нет в списке доступных для выбора
                    var newCurrentFilters = [];
                    for (var i = 0; i < this.currentFilters.length; i++) {
                        var filterCode = this.currentFilters[i].code;
                        if (filters[filterCode] != null || (this._filterInArray(filterCode, this.avaiableFilters) < 0)) {
                            newCurrentFilters.push(this.currentFilters[i]);
                        }
                    }
                    this.currentFilters = newCurrentFilters;

                    //обновим/добавим пришедшие
                    for (var key in filters) {
                        var filterValue = filters[key];
                        var indexInCurrent = this._filterInArray(key, this.currentFilters);
                        if (indexInCurrent < 0) { // еще нет в текущих - добавим
                            var index = this._filterInArray(key, this.avaiableFilters);
                            var currentFilter = this.avaiableFilters[index];
                            currentFilter.curValue = filterValue;
                            this.currentFilters.push(currentFilter);
                        } else { // есть в текущих - обновим значение фильтра
                            this.currentFilters[indexInCurrent].curValue = filterValue;
                        }
                    }
                }

                this.updateCurrentFiltersForm(true)
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

            updateCurrentFiltersForm: function (sendRequestToGrid) {
                var context = this;
                if (sendRequestToGrid) {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/arm/query-by-filters",
                        dataObj: {
                            armNode: YAHOO.lang.JSON.stringify(this.currentNode.data),
                            filters: YAHOO.lang.JSON.stringify(this.currentFilters)
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                if (oResponse) {
                                    YAHOO.Bubbling.fire("activeFiltersChanged", {
                                        bubblingLabel: context.bubblingLabel,
                                        filtersMeta: {
                                            query: oResponse.json.query
                                        }
                                    });
                                    context.currentQuery = oResponse.json.query;
                                }
                            }
                        },
                        failureCallback: {
                            fn: function () {
                                YAHOO.Bubbling.fire("activeFiltersChanged", {
                                    bubblingLabel: "documents-arm",
                                    filterMeta: {
                                        query: ""
                                    }
                                });
                            }
                        },
                        scope: this,
                        execScripts: true
                    });
                }
                var filtersExist = this.currentFilters.length > 0;
                Dom.setStyle(this.id + "-body", "display", filtersExist ? "" : "none");

                if (filtersExist) {
                    var currentFiltersConteiner = Dom.get(this.id + "-current-filters");
                    if (currentFiltersConteiner != null) {
                        var filtersHTML = "";
                        for (var i = 0; i < this.currentFilters.length; i++) {
                            filtersHTML += "<span class='arm-filter-item'>";
                            filtersHTML += this.currentFilters[i].name;
                            filtersHTML += this.getRemoveFilterButton(this.currentFilters[i]);
                            filtersHTML += "</span>";
                        }
                        currentFiltersConteiner.innerHTML = filtersHTML;
                    }
                }
            },

            getRemoveFilterButton: function (filter) {
                var id = Dom.generateId();
                var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
                YAHOO.util.Event.onAvailable(id, function (filter) {
                    YAHOO.util.Event.on(id, 'click', this.deleteFilter, filter, this);
                }, filter, this);
                return result;
            },

            deleteFilter: function (e, filter) {
                this.currentFilters.splice(this.currentFilters.indexOf(filter), 1);
                this.updateCurrentFiltersForm(true);
            },

            deleteAllFilters: function () {
                this.currentFilters = [];
                this.updateCurrentFiltersForm(true);
            }
        }, true);
})();