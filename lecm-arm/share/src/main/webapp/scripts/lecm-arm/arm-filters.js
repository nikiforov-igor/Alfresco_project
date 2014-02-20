(function () {
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
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            onUpdateAvaiableFilters: function (layer, args) {
                var currentNode = args[1].currentNode;
                if (currentNode !== null) {
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
            },

            onUpdateCurrentFilters: function (layer, args) {
                var filters = args[1].filtersData;
                if (filters != null) {
                    //обновим пришедшие
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
                    //обновим текущие. если фильтр не пришел - значит он удален
                    //TODO продумать, как удалять фильтры
                    /*var newCurrentFilters = [];
                    for (var i = 0; i < this.currentFilters.length; i++) {
                        var filterKey = this.currentFilters[i].code;
                        if (filters[filterKey] !== null) {
                            newCurrentFilters.push(this.currentFilters[i]);
                        }
                    }*/
                }

                var context = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/query-by-filters",
                    dataObj: {
                        armNode: YAHOO.lang.JSON.stringify(this.currentNode),
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
                })
            },

            _filterInArray: function(filterCode, filtersArray) {
                for (var i = 0; i < filtersArray.length; i++) {
                    var filter = filtersArray[i];
                    if (filter.code == filterCode) {
                        return i;
                    }
                }
                return -1;
            }
        }, true);
})();