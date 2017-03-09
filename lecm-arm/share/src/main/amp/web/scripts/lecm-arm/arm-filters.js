if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.Filters = function (htmlId) {
        LogicECM.module.ARM.Filters.superclass.constructor.call(this, "LogicECM.module.ARM.Filters", htmlId);

        this.availableFilters = [];
        this.currentFilters = [];
        this.filtersFromPref = [];

        // Preferences service
        this.preferences = new Alfresco.service.Preferences();

        this.deferredListPopulation = new Alfresco.util.Deferred(["updateArmFilters", "onReady"],
            {
                fn: this.updateCurrentFiltersForm,
                scope: this
            });

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
            availableFilters: [],
            /*текущие фильтры в формате {code: 'CODE', value: 'индекс_значения,индекс_значения3'}*/
            currentFilters: [],
            /*фильтры, полученные из preference пользователей или URL*/
            filtersFromPref: [],

            options: {
                bubblingLabel: "documents-arm"
            },

            attrSearchApplied: false,
            fullTextSearchApplied: false,

            onReady: function () {
                YAHOO.util.Dom.addClass(this.id, "hidden");
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "inherit");

                YAHOO.util.Event.on(this.id + "-delete-all-link", 'click', this.deleteAllFilters, null, this);

                var filtersFromArgs = Alfresco.util.getQueryStringParameter("filters");
                if (filtersFromArgs) {
                    var filters = filtersFromArgs.split(";");
                    for (var i = 0; i < filters.length; i++) {
                        var filtersObj = filters[i].split("|");
                        this.filtersFromPref.push({
                            code: filtersObj[0],
                            value: filtersObj[1] ? filtersObj[1].split(",") : []
                        });
                    }
                    this.deferredListPopulation.fulfil("onReady");
                } else {
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
                                    this.filtersFromPref = [];
                                    this.deferredListPopulation.fulfil("onReady");
                                },
                                scope: this
                            }
                        });
                }
            },

            renderFilters: function (element, callback, parent) {
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/draw-filters",
                    dataObj: {
                        htmlId: Alfresco.util.generateDomId(),
                        filters: YAHOO.lang.JSON.stringify(this.getFilledFilters())
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            element.innerHTML = oResponse.serverResponse.responseText;
                            if (callback && YAHOO.lang.isFunction(callback)) {
                                callback.call(parent ? parent : this);
                            }
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            /*сработает при выборе узла в дереве*/
            onUpdateAvaiableFilters: function (currentNode) {
                if (currentNode) {
                    this.currentFilters = [];

                    var filters = currentNode.data.filters;
                    if (filters && filters.length) {
                        this.availableFilters = filters;

                        /*Проверим, что сохраненный фильтр присутствует среди фильтров на узле (учитываем, что фильтр мог быть удален из АРМа)*/
                        for (var j = 0; j < this.filtersFromPref.length; j++) {
                            var indexInAvailable = this._getIndexByCode(this.filtersFromPref[j].code, this.availableFilters);
                            if (indexInAvailable >= 0) {
                                this.currentFilters.push(this.filtersFromPref[j]);
                            }
                        }
                    } else {
                        this.availableFilters = [];
                    }
                }
                if (!this.deferredListPopulation.fulfil("updateArmFilters")) {
                    this.updateCurrentFiltersForm(false);
                }
            },

            /*возвращает список availableFilters*/
            getAvailableFilters: function () {
                return this.availableFilters;
            },

            /*возвращает предзаполненным выбранными данными список availableFilters*/
            getFilledFilters: function () {
                var filledFilters = this.availableFilters.slice(0);

                filledFilters.forEach(function (filter) {
                    var current = this._getFilterByCode(filter.code, this.currentFilters);
                    filter.curValue = current ? current.value : [];
                }, this);

                return filledFilters;
            },

            /*сработает при применении фильтров в тулбаре
            * filters - {code1:[values1], code2:[values2]}
            * */
            onUpdateCurrentFilters: function (filters) {
                if (filters) {
                    //актуализируем список фильтров и их значений
                    var newCurrentFilters = [];

                    for (var key in filters) {
                        var availableFilter = this._getFilterByCode(key, this.availableFilters);
                        if (availableFilter) {
                            newCurrentFilters.push ({
                                code: availableFilter.code,
                                value: this._getValuesIndexes(filters[key], availableFilter.values)
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
                        if (filtersArray[i].code == filterCode) {
                            return i;
                        }
                    }
                }
                return -1;
            },

            _getFilterByCode: function (filterCode, filtersArray) {
                var index = this._getIndexByCode(filterCode, filtersArray);
                return index >= 0 ? filtersArray[index] : null;
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
                            var availableFilter = this._getFilterByCode(this.currentFilters[i].code, this.availableFilters);
                            if (availableFilter) {
                                var valuesIndexes = this.currentFilters[i].value;
                                var valuesTitle = "";
                                for (var j = 0; j < valuesIndexes.length; j++) {
                                    var valueIndex = valuesIndexes[j];
                                    if (availableFilter.values[valueIndex]) {
                                        var vTitle =  availableFilter.values[valueIndex].name;
                                        valuesTitle += (vTitle + ", ");
                                    }
                                }
                                valuesTitle = valuesTitle.substring(0, valuesTitle.length - 2);

                                filtersHTML += "<span class='arm-filter-item' title='" + valuesTitle + "'>";
                                filtersHTML += availableFilter.name;
                                filtersHTML += this.getRemoveFilterButton(this.currentFilters[i]);
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
                    var availableFilter = this._getFilterByCode(this.currentFilters[i].code, this.availableFilters);
                    if (availableFilter) {
                        var valuesIndexes = this.currentFilters[i].value;
                        var values = [];
                        for (var j = 0; j < valuesIndexes.length; j++) {
                            var valueIndex = valuesIndexes[j];
                            if (availableFilter.values[valueIndex]){
                                values.push(availableFilter.values[valueIndex].code);
                            }
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
                    this.preferences.set(this._buildPreferencesKey(), YAHOO.lang.JSON.stringify(this.filtersFromPref));
                }
            },

            _getValuesIndexes: function (values, valuesList) {
                var result = [];
                for (var i = 0; i < values.length; i++) {
                    var index = this._getIndexByCode(values[i], valuesList);
                    if (index >= 0) {
                        result.push(index);
                    }
                }
                return result;
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