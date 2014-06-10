(function () {
    LogicECM.module.Documents.AuthorFilter = function AuthorFilter_constructor(htmlId) {
        LogicECM.module.Documents.AuthorFilter.superclass.constructor.call(this, "LogicECM.module.Documents.AuthorFilter", htmlId, ["button", "container"]);

        this.manager = LogicECM.module.Documents.filtersManager ? LogicECM.module.Documents.filtersManager : new LogicECM.module.Documents.FiltersManager() ; 
        
        return this;
    };

    YAHOO.extend(LogicECM.module.Documents.AuthorFilter, Alfresco.component.Base,
        {
            manager: null,
            
            PREF_FILTER_ID: "docAuthor",
            options: {
                docType: "lecm-document:base",
                gridBubblingLabel: "documents",
                filterOver: false
            },

            expiresDate: new Date(),

            onReady: function () {
                var date = new Date;
                date.setDate(date.getDate() + 30);
                this.expiresDate = date;

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

                if (!this.options.filterOver || this.options.filterOver == "false") {
                    var authorPreference = LogicECM.module.Base.Util.getCookie(this.manager._buildPreferencesKey(this.PREF_FILTER_ID));
                    if (authorPreference == null) {
                        authorPreference = this.options.docType + "/all";
                    }
                    this._updateWidgets(authorPreference);
                } else {
                    var filter = location.hash;
                    if (filter && filter != "") {
                        var re = /#(\w+)=(\w+)\|/;
                        filter = filter.replace(re, "");
                        this._updateWidgets(filter);
                    }
                }
            },

            onAuthorFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.widgets.author.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.author.value = menuItem.value;
                }
            },

            onApplyButtonClick: function () {
                LogicECM.module.Base.Util.setCookie(this.manager._buildPreferencesKey(this.PREF_FILTER_ID), this.options.docType + "/" + this.widgets.author.value, {expires:this.expiresDate});
                location.hash = '#filter=' + this.PREF_FILTER_ID + "|" + this.options.docType + "/" + this.widgets.author.value;
                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        bubblingLabel: this.options.gridBubblingLabel
                    });
            },

            _updateWidgets: function (authorPreference) {
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
        });
})();