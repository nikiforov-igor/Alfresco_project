(function () {
    LogicECM.module.ARM.Filters = function (htmlId) {
        LogicECM.module.ARM.Filters.superclass.constructor.call(this, "LogicECM.module.ARM.Filters", htmlId);

        this.avaiableFilters = [];
        this.currentFilters = [];
        this.currentQuery = null;
        this.bubblingLabel = null;

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

            bubblingLabel: null,

            onReady: function () {
                YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            onUpdateAvaiableFilters: function (layer, args) {
                var filters = args[1].filters;
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
            },

            onUpdateCurrentFilters: function (layer, args) {
                var filters = args[1].filters;
                var hasFilters = filters != null && filters.length > 0;
                if (hasFilters) {
                    this.currentFilters = [];
                    for (var i = 0; i < filters.length; i++) {
                        var filter = filters[i];
                        this.currentFilters.push(filter);
                    }

                } else {
                    this.currentFilters = [];
                }

                var context = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/query-by-filters",
                    dataObj: {
                        filters: YAHOO.lang.JSON.stringify(this.currentFilters)
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            if (oResponse) {
                                YAHOO.Bubbling.fire("activeFiltersChanged", {
                                    bubblingLabel: context.bubblingLabel,
                                    filterMeta: {
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
            }
        }, true);
})();