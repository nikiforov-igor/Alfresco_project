(function () {
    LogicECM.module.Errands.Filter = function ErrandsFilter_constructor(htmlId) {
        LogicECM.module.Errands.Filter.superclass.constructor.call(this, "LogicECM.module.Errands.Filter", htmlId, ["button", "container"]);

        this.manager = LogicECM.module.Documents.filtersManager ? LogicECM.module.Documents.filtersManager : new LogicECM.module.Documents.FiltersManager() ;

        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.Filter, Alfresco.component.Base,
        {
            manager: null,

            PREF_FILTER_ID: "errandsFilter",
            options: {
                docType: "lecm-errands:document",
                gridBubblingLabel: "errands",
                filterOver: false
            },

            splashScreen: null,

            onReady: function () {
                this.manager.setOptions({docType: this.options.docType}); // явно прописываем тип

                this.widgets.assign = Alfresco.util.createYUIButton(this, "assign", this.onAssignFilterChanged,
                    {
                        type: "menu",
                        menu: "assign-menu",
                        lazyloadmenu: false
                    });

                this.widgets.assign.set("label", this.msg("filter.all"));
                this.widgets.assign.value = "all";

                this.widgets.date = Alfresco.util.createYUIButton(this, "date", this.onDateFilterChanged,
                    {
                        type: "menu",
                        menu: "date-menu",
                        lazyloadmenu: false
                    });

                this.widgets.date.set("label", this.msg("filter.all"));
                this.widgets.date.value = "all";

                this.widgets.importantCheckBox = YAHOO.util.Dom.get(this.id + "-importantCheck");
                this.widgets.controlCheckBox = YAHOO.util.Dom.get(this.id + "-controlCheck");

                this.widgets.applyButton = Alfresco.util.createYUIButton(this, "applyButton", this.onApplyButtonClick);

                if (!this.options.filterOver || this.options.filterOver == "false") {
                    this.manager.preferences.request(this.manager._buildPreferencesKey(),
                        {
                            successCallback: {
                                fn: function (p_oResponse) {
                                    var preferences = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.manager._buildPreferencesKey(this.PREF_FILTER_ID), "all/all/false/false");
                                    if (preferences !== null) {
                                        this._updateWidgets(preferences);
                                    }
                                },
                                scope: this
                            }
                        });
                } else {
                    var filter = location.hash;
                    if (filter && filter != ""){
                        var re = /#(\w+)=(\w+)\|/;
                        filter.replace(re, "")
                        this._updateWidgets(filter);
                    }
                }
            },

            onAssignFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.assign.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.assign.value = menuItem.value;
                }
            },

            onDateFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.date.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.date.value = menuItem.value;
                }
            },

            onApplyButtonClick: function () {
                var context = this;
                var newValue = this.widgets.assign.value + "/" + this.widgets.date.value + "/" + this.widgets.importantCheckBox.checked + "/" + this.widgets.controlCheckBox.checked;
                this._showSplash();

                var success = {
                    fn: function () {
                        location.hash = '#filter=' + context.PREF_FILTER_ID + "|" + newValue;
                        window.location.reload(true);
                    }
                } ;

                var failure = {
                    fn: function () {
                        context._hideSplash();
                    }
                } ;
                this.manager.preferences.set(this.manager._buildPreferencesKey(this.PREF_FILTER_ID), newValue, {successCallback: success, failureCallback: failure});
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
            },

            _updateWidgets: function (preferences) {
                var prefs = preferences.split("/");
                if (prefs[0]) {
                    this.widgets.assign.value = prefs[0];
                    var menuItems = this.widgets.assign.getMenu().getItems();
                    for (index in menuItems) {
                        if (menuItems.hasOwnProperty(index)) {
                            if (menuItems[index].value === prefs[0]) {
                                this.widgets.assign.set("label", menuItems[index].cfg.getProperty("text"));
                                break;
                            }
                        }
                    }
                }
                if (prefs[1]){
                    this.widgets.date.value = prefs[1];
                    menuItems = this.widgets.date.getMenu().getItems();
                    for (index in menuItems) {
                        if (menuItems.hasOwnProperty(index)) {
                            if (menuItems[index].value === prefs[1]) {
                                this.widgets.date.set("label", menuItems[index].cfg.getProperty("text"));
                                break;
                            }
                        }
                    }
                }

                this.widgets.importantCheckBox.checked = (prefs[2] == "true");
                this.widgets.controlCheckBox.checked = (prefs[3] == "true");
            }
        });
})();