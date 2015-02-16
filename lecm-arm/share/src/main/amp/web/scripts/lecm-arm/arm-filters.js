if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.Filters = function (htmlId) {
        LogicECM.module.ARM.Filters.superclass.constructor.call(this, "LogicECM.module.ARM.Filters", htmlId);

        this.avaiableFilters = [];
        this.currentFilters = [];
        this.currentQuery = null;
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

        YAHOO.Bubbling.on("showSearchByAttributesLabel", this.onShowSearchByAttributes, this);
        YAHOO.Bubbling.on("hideSearchByAttributesLabel", this.onHideSearchByAttributes, this);
        YAHOO.Bubbling.on("showFullTextSearchLabel", this.onShowFullTextSearch, this);
        YAHOO.Bubbling.on("hideFullTextSearchLabel", this.onHideFullTextSearch, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.Filters, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.Filters.prototype,
        {
            PREFERENCE_KEY: "ru.it.lecm.arm.",

            avaiableFilters: [],
            currentFilters: [],
            filtersFromPref: [],

            currentQuery: null,

            currentNode: null,

            options: {},

            currentSelectedItems: null,

            attrSearchApplied: false,
            fullTextSearchApplied: false,

            onReady: function () {
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "inherit");
                YAHOO.util.Dom.setStyle(this.id, "display", "none");

                YAHOO.util.Event.on(this.id + "-delete-all-link", 'click', this.deleteAllFilters, null, this);

                var filters = this;
                this.preferences.request(this._buildPreferencesKey(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var filtersPref = Alfresco.util.findValueByDotNotation(p_oResponse.json, filters._buildPreferencesKey());
                                if (filtersPref != null && filtersPref != "") {
                                    filters.filtersFromPref = YAHOO.lang.JSON.parse(filtersPref);
                                } else {
                                    filters.filtersFromPref = [];
                                }
                                filters.currentFilters = filters.filtersFromPref.slice(0);
                                filters.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function () {
                                filters.filtersFromPref = [];
                                filters.currentFilters = filters.filtersFromPref.slice(0);
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

                        this.currentFilters = this.filtersFromPref.slice(0);
                        for (var j = 0; j < this.filtersFromPref.length; j++) {
                            var indexInAvaiables = this._filterInArray(this.filtersFromPref[j].code, this.avaiableFilters);
                            if (indexInAvaiables < 0) {
                                this.currentFilters.splice(this.currentFilters.indexOf(this.filtersFromPref[j]), 1);
                            }
                        }
                    } else {
                        this.avaiableFilters = [];
                        this.currentFilters = [];
                    }
                }
                if (!this.deferredListPopulation.fulfil("updateArmFilters")) {
                    this.updateCurrentFiltersForm(false);
                }
            },

            onUpdateCurrentFilters: function (layer, args) {
                var filters = args[1].filtersData;
                if (filters != null) {
                    //обновим сброшенные - оставим те пришли(будут обновлены)
                    var newCurrentFilters = [];
                    for (var i = 0; i < this.currentFilters.length; i++) {
                        var filterCode = this.currentFilters[i].code;
                        if (filters[filterCode] != null) {
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

            updateCurrentFormView: function () {
                var filtersExist = this.currentFilters.length > 0 || this.fullTextSearchApplied || this.attrSearchApplied;
                Dom.setStyle(this.id, "display", filtersExist ? "" : "none");

                if (filtersExist) {
                    var currentFiltersConteiner = Dom.get(this.id + "-current-filters");
                    if (currentFiltersConteiner != null) {
                        var filtersHTML = "";
                        for (var i = 0; i < this.currentFilters.length; i++) {
                            var curFilter = this.currentFilters[i];
                            var valuesTitle = "";
                            if (YAHOO.lang.isArray(curFilter.curValue)) {
                                for (var j = 0; j < curFilter.curValue.length; j++) {
                                    var vTitle = this._findFilterValueTitle(curFilter.curValue[j], curFilter.values);
                                    valuesTitle += (vTitle + ", ");
                                }
                                valuesTitle = valuesTitle.substring(0, valuesTitle.length - 2);
                            } else {
                                valuesTitle = this._findFilterValueTitle(curFilter.curValue, curFilter.values);
                            }

                            filtersHTML += "<span class='arm-filter-item' title='" + valuesTitle + "'>";
                            filtersHTML += curFilter.name;
                            filtersHTML += this.getRemoveFilterButton(curFilter);
                            filtersHTML += "</span>";
                        }

                        if (this.attrSearchApplied) {
                            filtersHTML += "<span class='arm-filter-item' title='" + Alfresco.util.message('lecm.arm.lbl.by.attrs') + "'>";
                            filtersHTML += Alfresco.util.message('lecm.arm.lbl.by.attrs');
                            filtersHTML += this.getRemoveAttrFilterButton();
                            filtersHTML += "</span>";
                        }

                        if (this.fullTextSearchApplied) {
                            filtersHTML += "<span class='arm-filter-item' title='" + Alfresco.util.message('lecm.arm.lbl.by.text') + "'>";
                            filtersHTML += Alfresco.util.message('lecm.arm.lbl.by.text');
                            filtersHTML += this.getRemoveFullTextFilterButton();
                            filtersHTML += "</span>";
                        }

                        currentFiltersConteiner.innerHTML = filtersHTML;
                    }
                }
            },

            updateCurrentFiltersForm: function (updatePrefs) {
                var context = this;

                this.updateCurrentFormView();

                YAHOO.Bubbling.fire("activeFiltersChanged", {
                    bubblingLabel: context.options.bubblingLabel,
                    filters: this.currentFilters ? this.currentFilters : []
                });

                if (updatePrefs) {
                    this.preferences.set(this._buildPreferencesKey(), YAHOO.lang.JSON.stringify(this.currentFilters ? this.currentFilters : []));
                    this.filtersFromPref = this.currentFilters.slice(0);
                }
            },

            _findFilterValueTitle: function (code, valuesList) {
                for (var i = 0; i < valuesList.length; i++) {
                    if (valuesList[i].code == code) {
                        return valuesList[i].name;
                    }
                }
                return null;
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
                this.deleteAttributesFilter();
                this.deleteFullTextFilter();
                this.updateCurrentFiltersForm(true);
            },

            onShowSearchByAttributes: function () {
                this._updateAttrSearchState(true)
            },

            onHideSearchByAttributes: function () {
                this._updateAttrSearchState(false);
            },

            onShowFullTextSearch: function () {
                this._updateFullTextSearchState(true);
            },

            onHideFullTextSearch: function () {
                this._updateFullTextSearchState(false);
            },

            _updateAttrSearchState: function (active) {
                this.attrSearchApplied = active;
                this.updateCurrentFormView();
            },

            _updateFullTextSearchState: function (active) {
                this.fullTextSearchApplied = active;
                this.updateCurrentFormView();
            },

            getRemoveAttrFilterButton: function () {
                var id = Dom.generateId();
                var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
                YAHOO.util.Event.onAvailable(id, function () {
                    YAHOO.util.Event.on(id, 'click', this.deleteAttributesFilter, null, this);
                }, null, this);
                return result;
            },

            getRemoveFullTextFilterButton: function () {
                var id = Dom.generateId();
                var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
                YAHOO.util.Event.onAvailable(id, function () {
                    YAHOO.util.Event.on(id, 'click', this.deleteFullTextFilter, null, this);
                }, null, this);
                return result;
            },

            deleteFullTextFilter: function () {
                YAHOO.Bubbling.fire("clearFullTextSearch", {
                    bubblingLabel: this.options.bubblingLabel
                });
            },

            deleteAttributesFilter: function () {
                YAHOO.Bubbling.fire("clearAttributesSearch", {
                    bubblingLabel: this.options.bubblingLabel
                });
            },



            _buildPreferencesKey: function () {
                return this.PREFERENCE_KEY +  LogicECM.module.ARM.SETTINGS.ARM_CODE + ".current-filters";
            }
        }, true);
})();