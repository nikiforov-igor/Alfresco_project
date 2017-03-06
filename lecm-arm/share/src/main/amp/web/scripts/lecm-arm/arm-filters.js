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
        this.filtersFromPref = [];

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

            /*фильтры, полученные из настроек АРМа - содержат полную информацию о фильтре*/
            avaiableFilters: [],
            /*текущие фильтры в формате {code: 'CODE', value: 'индекс_значения,индекс_значения3'}*/
            currentFilters: [],
            /*фильтры, полученные из preference пользователей*/
            filtersFromPref: [],
            currentNode: null,

            options: {
                bubblingLabel: "documents-arm"
            },

            attrSearchApplied: false,
            fullTextSearchApplied: false,

            onReady: function () {
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "inherit");
                YAHOO.util.Dom.addClass(this.id, "hidden");

                YAHOO.util.Event.on(this.id + "-delete-all-link", 'click', this.deleteAllFilters, null, this);

                this.preferences.request(this._buildPreferencesKey(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var filtersPref = Alfresco.util.findValueByDotNotation(p_oResponse.json, this._buildPreferencesKey());
                                if (filtersPref) {
                                    this.filtersFromPref = YAHOO.lang.JSON.parse(filtersPref);
                                } else {
                                    this.filtersFromPref = [];
                                }
                                this.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function () {
                                this.currentFilters = [];
                                this.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        }
                    });
            },

            /*сработает при выборе узла в дереве*/
            onUpdateAvaiableFilters: function (layer, args) {
                var currentNode = args[1].currentNode;
                this.currentNode = currentNode;
                if (this.currentNode) {
                    this.currentNode = currentNode;
                    var filters = currentNode.data.filters;
                    var hasFilters = filters && filters.length;
                    if (hasFilters) {
                        this.avaiableFilters = [];
                        for (var i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            this.avaiableFilters.push(filter);
                        }

                        /*Проверим, что сохраненный фильтр присутствует среди фильтров на узле (учитываем, что фильтр мог быть удален из АРМа)*/
                        this.currentFilters = [];
                        for (var j = 0; j < this.filtersFromPref.length; j++) {
                            var indexInAvailable = this._getIndexByCode(this.filtersFromPref[j].code, this.avaiableFilters);
                            if (indexInAvailable >= 0) {
                                this.currentFilters.push(this.filtersFromPref[j]);
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

            /*сработает при применении фильтров в тулбаре*/
            onUpdateCurrentFilters: function (layer, args) {
                var filters = args[1].filtersData;
                if (filters) {
                    //актуализируем список фильтров и их значений
                    var newCurrentFilters = [];

                    for (var key in filters) {
                        var index = this._getIndexByCode(key, this.avaiableFilters);
                        if (index >= 0) {
                            var availableFilter = this.avaiableFilters[index];

                            var filterValues = YAHOO.lang.isArray(filters[key]) ? filters[key] : [filters[key]]
                            newCurrentFilters.push ({
                                code: availableFilter.code,
                                value: this._getValuesIndexes(filterValues, availableFilter.values)
                            });
                        }
                    }

                    this.currentFilters = newCurrentFilters;
                }

                this.updateCurrentFiltersForm(true)
            },

            _getIndexByCode: function (filterCode, filtersArray) {
                if (filterCode && filtersArray) {
                    for (var i = 0; i < filtersArray.length; i++) {
                        var filter = filtersArray[i];
                        if (filter.code == filterCode) {
                            return i;
                        }
                    }
                }
                return -1;
            },

            /*перерисовка примененных фильтров*/
            updateCurrentFormView: function () {
                var filtersExist = this.currentFilters.length || this.fullTextSearchApplied || this.attrSearchApplied;
                if (filtersExist) {
                    YAHOO.util.Dom.removeClass(this.id, "hidden");
                } else {
                    YAHOO.util.Dom.addClass(this.id, "hidden");
                }

                if (filtersExist) {
                    var currentFiltersConteiner = Dom.get(this.id + "-current-filters");
                    if (currentFiltersConteiner) {
                        var filtersHTML = "";
                        for (var i = 0; i < this.currentFilters.length; i++) {
                            var curFilter = this.currentFilters[i];
                            var curValuesInd = this.currentFilters[i].value ? this.currentFilters[i].value.split(",") : [];
                            var availableFilterInd = this._getIndexByCode(curFilter.code, this.avaiableFilters);

                            if (availableFilterInd >= 0) {
                                var availableFilter = this.avaiableFilters[availableFilterInd];

                                var valuesTitle = "";
                                for (var j = 0; j < curValuesInd.length; j++) {
                                    var vTitle =  availableFilter.values[curValuesInd[j]].name;
                                    valuesTitle += (vTitle + ", ");
                                }
                                valuesTitle = valuesTitle.substring(0, valuesTitle.length - 2);

                                filtersHTML += "<span class='arm-filter-item' title='" + valuesTitle + "'>";
                                filtersHTML += availableFilter.name;
                                filtersHTML += this.getRemoveFilterButton(curFilter);
                                filtersHTML += "</span>";
                            }
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

            /*Обновление состояния фильтров*/
            updateCurrentFiltersForm: function (updatePrefs) {
                /*формы*/
                this.updateCurrentFormView();

                /*состояния поиска*/
                var appliedFilters = [];
                for (var i = 0; i < this.currentFilters.length; i++) {
                    var curFilter = this.currentFilters[i];
                    var curValuesInd = this.currentFilters[i].value ? this.currentFilters[i].value.split(",") : [];

                    var availableFilterInd = this._getIndexByCode(curFilter.code, this.avaiableFilters);
                    if (availableFilterInd >= 0) {
                        var availableFilter = this.avaiableFilters[availableFilterInd];

                        var values = [];
                        for (var j = 0; j < curValuesInd.length; j++) {
                            values.push(availableFilter.values[curValuesInd[j]].code);
                        }
                        appliedFilters.push({
                            'curValue': values,
                            'class': availableFilter.class,
                            'query': availableFilter.query
                        });
                    }
                }
                YAHOO.Bubbling.fire("activeFiltersChanged", {
                    bubblingLabel: this.options.bubblingLabel,
                    filters: appliedFilters
                });

                /*user preferences*/
                if (updatePrefs) {
                    this.filtersFromPref = this.currentFilters.slice(0);
                    this.preferences.set(this._buildPreferencesKey(), YAHOO.lang.JSON.stringify(this._buildFiltersPrefObj()));
                }
            },

            _buildFiltersPrefObj: function () {
                var filtersForPref = [];
                for (var i = 0; i < this.currentFilters.length; i++) {
                    filtersForPref.push({
                        code: this.currentFilters[i].code,
                        value: this.currentFilters[i].value
                    });
                }
                return filtersForPref;
            },

            _getValuesIndexes: function (values, valuesList) {
                var result = [];
                for (var i = 0; i < values.length; i++) {
                    var index = this._getIndexByCode(values[i], valuesList);
                    result.push(index);
                }
                return result.join(",");
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