(function () {
    LogicECM.module.Documents.AuthorFilter = function AuthorFilter_constructor(htmlId) {
        LogicECM.module.Documents.AuthorFilter.superclass.constructor.call(this, "LogicECM.module.Documents.AuthorFilter", htmlId, ["button", "container"]);
        
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);

        this.deferredListPopulation = new Alfresco.util.Deferred(["onReady", "initDataGrid"],
            {
                fn: this.populateDataGrid,
                scope: this
            });

        this.manager = LogicECM.module.Documents.filtersManager ? LogicECM.module.Documents.filtersManager : new LogicECM.module.Documents.FiltersManager() ; 
        
        return this;
    };

    YAHOO.extend(LogicECM.module.Documents.AuthorFilter, Alfresco.component.Base,
        {
            manager: null,
            
            PREF_FILTER_ID: ".documents-list-docAuthor-filter",
            options: {
                docType: "lecm-document:base",
                gridBubblingLabel: "documents",
                filterId: "docAuthor"
            },

            documentList: null,

            deferredListPopulation: {},

            onReady: function () {
                this.manager.setOptions({docType: this.options.docType});
                
                this.widgets.author = Alfresco.util.createYUIButton(this, "author", this.onAuthorFilterChanged,
                    {
                        type: "menu",
                        menu: "author-menu",
                        lazyloadmenu: false
                    });

                // Load preferences to override default filter and range
                this.widgets.author.set("label", this.msg("filter.all"));
                this.widgets.author.value = "all";

                this.manager.preferences.request(this.manager._buildPreferencesKey(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var authorPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.manager._buildPreferencesKey(this.PREF_FILTER_ID), "all");
                                if (authorPreference !== null) {
                                    this.widgets.author.value = authorPreference;
                                    // set the correct menu label
                                    var menuItems = this.widgets.author.getMenu().getItems();
                                    for (index in menuItems) {
                                        if (menuItems.hasOwnProperty(index)) {
                                            if (menuItems[index].value === authorPreference) {
                                                this.widgets.author.set("label", menuItems[index].cfg.getProperty("text"));
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
                //location.hash = '#filter=' + this.options.filterId + "|" + this.widgets.author.value;
                var currentFilter = {
                    filterId: this.options.filterId,
                    filterData:this.widgets.author.value
                };
                this.documentList.currentFilter = currentFilter;

                /*YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        filter: currentFilter,
                        bubblingLabel: this.options.gridBubblingLabel
                    });*/
            },

            onAuthorFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.author.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.author.value = menuItem.value;
                }
            },

            onApplyButtonClick: function () {
/*                var currentFilter = {
                    filterId: this.options.filterId,
                    filterData:this.widgets.author.value
                };
                */
                var success = {
                    fn: function () {
                        window.location.reload(true);
                    }
                } ;
                this.manager.preferences.set(this.manager._buildPreferencesKey(this.PREF_FILTER_ID), this.widgets.author.value, {successCallback: success});


                /*location.hash = '#filter=' + this.options.filterId + "|" + this.widgets.author.value;

                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        filter: currentFilter,
                        bubblingLabel: this.options.gridBubblingLabel
                    });*/
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