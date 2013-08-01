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
            
            PREF_FILTER_ID: "docAuthor",
            options: {
                docType: "lecm-document:base",
                gridBubblingLabel: "documents"
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

                this.widgets.author.set("label", this.msg("filter.all"));
                this.widgets.author.value = "all";

                this.widgets.applyButton = Alfresco.util.createYUIButton(this, "applyButton", this.onApplyButtonClick);

                this.manager.preferences.request(this.manager._buildPreferencesKey(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var authorPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.manager._buildPreferencesKey(this.PREF_FILTER_ID), this.options.docType +"/all");
                                if (authorPreference !== null) {
                                    this.widgets.author.value = authorPreference;
                                    var menuItems = this.widgets.author.getMenu().getItems();
                                    var authorTypeKey = authorPreference.replace(this.options.docType + "/", "");
                                    for (index in menuItems) {
                                        if (menuItems.hasOwnProperty(index)) {
                                            if (menuItems[index].value === authorTypeKey) {
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
                var currentFilter = {
                    filterId: this.PREF_FILTER_ID,
                    filterData: this.widgets.author.value
                };
                this.documentList.currentFilter = currentFilter;
                location.hash = '#filter=' + this.PREF_FILTER_ID + "|" + this.widgets.author.value;
            },

            onAuthorFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.author.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.author.value = menuItem.value;
                }
            },

            onApplyButtonClick: function () {
                var context = this;

                this._showSplash();


                var success = {
                    fn: function () {
                        location.hash = '#filter=' + context.PREF_FILTER_ID + "|" + context.options.docType + "/" + context.widgets.author.value;
                        window.location.reload(true);
                    }
                } ;

                var failure = {
                    fn: function () {
                        context._hideSplash();
                    }
                } ;

                this.manager.preferences.set(this.manager._buildPreferencesKey(this.PREF_FILTER_ID), context.options.docType + "/" + context.widgets.author.value, {successCallback: success, failureCallback: failure});
            },

            onInitDataGrid: function BaseToolbar_onInitDataGrid(layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.gridBubblingLabel || !datagrid.options.bubblingLabel) || this.options.gridBubblingLabel == datagrid.options.bubblingLabel) {
                    this.documentList = datagrid;
                    this.deferredListPopulation.fulfil("initDataGrid");
                }
            },

            _showSplash: function () {
                this.splashScreen = Alfresco.util.PopupManager.displayMessage(
                    {
                        text: Alfresco.util.message("label.loading"),
                        spanClass: "wait",
                        displayTime: 0
                    });
            } ,

            _hideSplash: function () {
                YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroy);
            }
        });
})();