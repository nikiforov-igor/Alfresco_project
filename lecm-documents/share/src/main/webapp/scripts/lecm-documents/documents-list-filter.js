(function () {
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;

    var PREFERENCES_CONTRACTS = "ru.it.lecm.documents",
        PREF_FILTER = ".documents-list-assign-filter";

    LogicECM.module.Documents.Filter = function Contracts_constructor(htmlId) {
        LogicECM.module.Documents.Filter.superclass.constructor.call(this, "LogicECM.module.Documents.Filter", htmlId, ["button", "container"]);

        // Preferences service
        this.services.preferences = new Alfresco.service.Preferences();
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);

        this.deferredListPopulation = new Alfresco.util.Deferred(["onReady", "initDataGrid"],
            {
                fn: this.populateDataGrid,
                scope: this
            });

        return this;
    };

    YAHOO.extend(LogicECM.module.Documents.Filter, Alfresco.component.Base,
        {
            options: {
                docType: "lecm-document:base",
                gridBubblingLabel: "documents",
                filterId: "assign"
            },

            documentList: null,

            onReady: function () {
                this.widgets.assign = Alfresco.util.createYUIButton(this, "assign", this.onAssignFilterChanged,
                    {
                        type: "menu",
                        menu: "assign-menu",
                        lazyloadmenu: false
                    });

                // Load preferences to override default filter and range
                this.widgets.assign.set("label", this.msg("filter.all"));
                this.widgets.assign.value = "all";

                this.services.preferences.request(this.buildPreferences(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var assignPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.buildPreferences(PREF_FILTER), "all");
                                if (assignPreference !== null) {
                                    this.widgets.assign.value = assignPreference;
                                    // set the correct menu label
                                    var menuItems = this.widgets.assign.getMenu().getItems();
                                    for (index in menuItems) {
                                        if (menuItems.hasOwnProperty(index)) {
                                            if (menuItems[index].value === assignPreference) {
                                                this.widgets.assign.set("label", menuItems[index].cfg.getProperty("text"));
                                                break;
                                            }
                                        }
                                    }
                                }
                                this.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function () {
                                this.deferredListPopulation.fulfil("onReady");
                            },
                            scope: this
                        }
                    });
            },

            populateDataGrid: function () {
                //location.hash = '#filter=' + this.options.filterId + "|" + this.widgets.assign.value;
                var currentFilter = {
                    filterId: this.options.filterId,
                    filterData:this.widgets.assign.value
                };
                this.documentList.currentFilter = currentFilter;
            },
            /**
             * @return {string}
             */
            buildPreferences: function (suffix) {
                var opt = this.options;
                return PREFERENCES_CONTRACTS + "." + opt.docType.split(":").join("_") + (suffix ? suffix : "");
            },

            onAssignFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.assign.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.assign.value = menuItem.value;
                    this.services.preferences.set(this.buildPreferences(PREF_FILTER), this.widgets.assign.value);
                }
            },

            onApplyButtonClick: function () {
                var currentFilter = {
                    filterId: this.options.filterId,
                    filterData:this.widgets.assign.value
                };
                location.hash = '#filter=' + this.options.filterId + "|" + this.widgets.assign.value;

                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        filter: currentFilter,
                        bubblingLabel: this.options.gridBubblingLabel
                    });
            },

            // инициализация грида
            onInitDataGrid: function BaseToolbar_onInitDataGrid(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.gridBubblingLabel || !datagrid.options.bubblingLabel) || this.options.gridBubblingLabel == datagrid.options.bubblingLabel) {
                    this.documentList = datagrid;
                    this.deferredListPopulation.fulfil("initDataGrid");
                }
            }
        });
})();