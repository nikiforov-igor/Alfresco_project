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

        // Preferences service
        this.preferences = new Alfresco.service.Preferences();

        this.deferredListPopulation = new Alfresco.util.Deferred(["updateArmFilters", "onReady"],
            {
                fn: this.updateCurrentFiltersForm,
                scope: this
            });

        YAHOO.Bubbling.on("updateArmFilters", this.onUpdateAvaiableFilters, this);
        YAHOO.Bubbling.on("updateCurrentFilters", this.onUpdateCurrentFilters, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.Filters, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.Filters.prototype,
        {
            PREFERENCE_KEY: "ru.it.lecm.arm.current-filters",

            avaiableFilters: [],
            currentFilters: [],

            currentQuery: null,

            currentNode: null,

            bubblingLabel: null,

            onReady: function () {
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "inherit");
                YAHOO.util.Dom.setStyle(this.id + "-body", "display", "none");

                YAHOO.util.Event.on(this.id + "-delete-all-link", 'click', this.deleteAllFilters, null, this);

                var filters = this;
                this.preferences.request(this.PREFERENCE_KEY,
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var filtersPref = Alfresco.util.findValueByDotNotation(p_oResponse.json, filters.PREFERENCE_KEY);
                                if (filtersPref != null && filtersPref != "") {
                                    filters.currentFilters = YAHOO.lang.JSON.parse(filtersPref);
                                } else {
                                    filters.currentFilters = [];
                                }
                                filters.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (p_oResponse) {
                                filters.currentFilters = [];
                                filters.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        }
                    });
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
                this.deferredListPopulation.fulfil("updateArmFilters");
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

            updateCurrentFiltersForm: function (updatePrefs) {
                var context = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/query-by-filters",
                    dataObj: {
                        armNode: YAHOO.lang.JSON.stringify((this.currentNode && this.currentNode.data) ? this.currentNode.data : {}),
                        filters: context._buildFiltersJSON()
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
                                bubblingLabel: context.bubblingLabel,
                                filterMeta: {
                                    query: ""
                                }
                            });
                        }
                    },
                    scope: this,
                    execScripts: true
                });

                var filtersExist = this.currentFilters.length > 0;
                Dom.setStyle(this.id + "-body", "display", filtersExist ? "" : "none");

                if (filtersExist) {
                    var currentFiltersConteiner = Dom.get(this.id + "-current-filters");
                    if (currentFiltersConteiner != null) {
                        var filtersHTML = "";
                        for (var i = 0; i < this.currentFilters.length; i++) {
                            var curFilter =  this.currentFilters[i];
                            if (YAHOO.lang.isArray(curFilter.curValue)) {
                                for (var j = 0; j < curFilter.curValue.length; j++) {
                                    var valueTitle = this._findFilterValueTitle(curFilter.curValue[j], curFilter.values);
                                    if (valueTitle != null) {
                                        filtersHTML += "<span class='arm-filter-item'>";
                                        filtersHTML += valueTitle;
                                        filtersHTML += this.getRemoveFilterButton(curFilter, curFilter.curValue[j]);
                                        filtersHTML += "</span>";
                                    }
                                }
                            } else {
                                filtersHTML += "<span class='arm-filter-item'>";
                                filtersHTML += this._findFilterValueTitle(curFilter.curValue, curFilter.values);
                                filtersHTML += this.getRemoveFilterButton(curFilter, curFilter.curValue);
                                filtersHTML += "</span>";
                            }
                        }
                        currentFiltersConteiner.innerHTML = filtersHTML;
                    }
                }
                if (updatePrefs) {
                    this.preferences.set(this.PREFERENCE_KEY, this._buildFiltersJSON());
                }
            },

            _buildFiltersJSON: function() {
                return YAHOO.lang.JSON.stringify(this.currentFilters ? this.currentFilters : [])
            },

            _findFilterValueTitle: function(code, valuesList) {
                for (var i = 0; i < valuesList.length; i++) {
                    if (valuesList[i].code == code) {
                        return valuesList[i].name;
                    }
                }
                return null;
            },

            getRemoveFilterButton: function (filter, valueToDelete) {
                var id = Dom.generateId();
                var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
                YAHOO.util.Event.onAvailable(id, function (filter) {
                    YAHOO.util.Event.on(id, 'click', this.deleteFilter, {
                        filter: filter,
                        value:valueToDelete
                    }, this);
                }, filter, this);
                return result;
            },

            deleteFilter: function (e, filterObj) {
                if (YAHOO.lang.isArray(filterObj.filter.curValue)) {
                    var filterIndex = this.currentFilters.indexOf(filterObj.filter);
                    this.currentFilters[filterIndex].curValue.splice(this.currentFilters[filterIndex].curValue.indexOf(filterObj.value), 1)
                } else {
                    this.currentFilters.splice(this.currentFilters.indexOf(filterObj.filter), 1);
                }
                this.updateCurrentFiltersForm(true);
            },

            deleteAllFilters: function () {
                this.currentFilters = [];
                this.updateCurrentFiltersForm(true);
            }
        }, true);
})();