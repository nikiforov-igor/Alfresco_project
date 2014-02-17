(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.DocumentsToolbar = function (htmlId) {
        LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);

        this.filtersDialog = null;
        this.splashScreen = null;

        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
        {
            filtersDialog: null,
            splashScreen: null,
            gridBubblingLabel: "documents-arm",

            _renderFilters: function () {
                var filtersDiv = Dom.get("filtersBlock-forms");
                /*Alfresco.util.Ajax.request(
                    {
                        url: '',
                        dataObj: {},
                        successCallback: {
                            fn: function (response) {
                                filtersDiv.innerHTML = response.serverResponse.responseText;
                                if (this.filtersDialog != null) {
                                    this.filtersDialog.show();
                                }
                            },
                            scope: this
                        },
                        failureMessage: "Could not load form component",
                        scope: this,
                        execScripts: true
                    });*/
            },

            onFiltersClick: function () {
                //отрисовка фильтров в окне
                this._drawFiltersPanel();
                this._renderFilters();
            },

            onApplyFilterClick: function () {
                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        bubblingLabel: this.gridBubblingLabel
                    });

            },

            _initButtons: function () {
                this.toolbarButtons["defaultActive"].filtersButton = Alfresco.util.createYUIButton(this, "filtersButton", this.onFiltersClick);

                this._drawFiltersPanel();
            },

            _drawFiltersPanel: function () {
                if (this.filtersDialog == null) {
                    // создаем диалог
                    this.filtersDialog = Alfresco.util.createYUIPanel("filtersBlock",
                        {
                            width: "800px"
                        });
                    // создаем кнопки
                    this.widgets.searchButton = Alfresco.util.createYUIButton(this, "filtersBlock-apply-button", this.onApplyFilterClick, {}, Dom.get("filtersBlock-apply-button"));
                }
            }
        }, true);
})();