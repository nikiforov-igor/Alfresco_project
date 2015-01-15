if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM || {};

LogicECM.module.ARM.dashlet = LogicECM.module.ARM.dashlet || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;

    var PREFERENCES_ARM = "ru.it.lecm.arm",
        PREF_FILTER = ".dashlet-arm-documents-filter";

    LogicECM.module.ARM.dashlet.ARMDocuments = function (htmlId) {
        LogicECM.module.ARM.dashlet.ARMDocuments.superclass.constructor.call(this, "LogicECM.module.ARM.dashlet.ARMDocuments", htmlId, ["button", "container"]);

        this.services.preferences = new Alfresco.service.Preferences();
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.dashlet.ARMDocuments, Alfresco.component.Base,
        {
            options: {
                baseQuery: "",
                isExist: false,
                regionId: ""
            },

            dataTable: null,
            documentsList: null,

            loadItemsCount: 15,
            skipItemsCount: 0,

            isInitialized: false,

            onReady: function () {
                // Create dropdown filter widgets
                this.widgets.filters = Alfresco.util.createYUIButton(this, "filters", this.onFilterChanged,
                    {
                        type: "menu",
                        menu: "filters-menu",
                        lazyloadmenu: false
                    });

                // The activity list container
                this.documentsList = Dom.get(this.id + "-documents");

                this.createDataTable();

                // Значение по умолчанию - первая строка
                var menuItems = this.widgets.filters.getMenu().getItems();
                for (var index in menuItems) {
                    if (menuItems.hasOwnProperty(index)) {
                        this.widgets.filters.set("label", menuItems[index].cfg.getProperty("text"));
                        this.widgets.filters.value = menuItems[index].value;
                        break;
                    }
                }

                // пробует вытащить сохраненное значение
                this.services.preferences.request(this.buildPreferences(),
                    {
                        successCallback: {
                            fn: function (p_oResponse) {
                                var filtersPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.buildPreferences(PREF_FILTER), "");
                                if (filtersPreference !== null) {
                                    // set the correct menu label
                                    var menuItems = this.widgets.filters.getMenu().getItems();
                                    for (var index in menuItems) {
                                        if (menuItems.hasOwnProperty(index)) {
                                            if (menuItems[index].cfg.getProperty("text") === filtersPreference) {
                                                this.widgets.filters.value = menuItems[index].value;
                                                this.widgets.filters.set("label", filtersPreference);
                                                break;
                                            }
                                        }
                                    }
                                }
                                // Display the toolbar now that we have selected the filter
                                Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
                                this.loadDocuments();
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function () {
                                // Display the toolbar now that we have selected the filter
                                Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
                                this.loadDocuments();
                            },
                            scope: this
                        }
                    });


            },

            /**
             * @return {string}
             */
            buildPreferences: function (suffix) {
                var opt = this.options;
                return PREFERENCES_ARM + "." + opt.regionId + (suffix ? suffix : "");
            },

            onFilterChanged: function (p_sType, p_aArgs) {
                var menuItem = p_aArgs[1];
                if (menuItem) {
                    this.isInitialized = false;

                    this.widgets.filters.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.filters.value = menuItem.value;
                    this.skipItemsCount = 0;

                    this.dataTable.getRecordSet().reset();
                    this.dataTable.render();
                    this.dataTable.showTableMessage("Загрузка данных...");
                    this.loadDocuments();
                    this.services.preferences.set(this.buildPreferences(PREF_FILTER), this.widgets.filters.get("label"));
                }
            },

            loadDocuments: function () {
                var me = this;
                if (this.options.isExist) {
                    var query = me.options.baseQuery;
                    if (me.options.baseQuery.length > 0 && me.widgets.filters.value != null && me.widgets.filters.value.length > 0) {
                        query += " AND (" + me.widgets.filters.value + ")"
                    }

                    Alfresco.util.Ajax.jsonPost(
                        {
                            url: Alfresco.constants.PROXY_URI + "lecm/document/getDocumentsByQuery",
                            dataObj: {
                                skipCount: me.skipItemsCount,
                                loadCount: me.loadItemsCount,
                                query: query
                            },
                            successCallback: {
                                fn: function (response) {
                                    var items = response.json;

                                    if (items.length > 0) {
                                        this.skipItemsCount = this.skipItemsCount + items.length;
                                    }
                                    this.dataTable.hideTableMessage();
                                    this.dataTable.addRows(response.json);
                                    this.isInitialized = true;
                                    Dom.setStyle(this.id + "-documents-loading", "visibility", "hidden");
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function () {
                                    this.dataTable.showTableMessage(this.msg("message.documents.load.failure"));
                                    Dom.setStyle(this.id + "-documents-loading", "visibility", "hidden");
                                },
                                scope: this
                            }
                        });
                } else {
                    this.dataTable.set("MSG_EMPTY", "Этот дашлет не настроен");
                    this.dataTable.render();
                }
            },

            onContainerScroll: function (event, scope) {
                var container = event.currentTarget;
                if (container.scrollTop + container.clientHeight == container.scrollHeight) {
                    Dom.setStyle(scope.id + "-documents-loading", "visibility", "visible");
                    if (scope.isInitialized) {
                        scope.loadDocuments();
                    }
                }
            },

            renderCell: function (elCell, oRecord, oColumn, oData) {
                var data = oRecord.getData();

                elCell.innerHTML = "<a href='" + window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT
                    + "document?nodeRef=" + data.nodeRef + "'>" + data.extPresentString + "</a> ";

            },
            createDataTable: function () {
                if (this.dataTable == null) {
                    var columnDefs = [
                        { key: "extPresentString", label: "", sortable: false, formatter: this.bind(this.renderCell)}
                    ];
                    var initialSource = new YAHOO.util.DataSource([]);
                    initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                    initialSource.responseSchema = {extPresentString: "extPresentString", nodeRef: "nodeRef", presentString: "presentString"};

                    this.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefs, initialSource, {});
                    this.dataTable.getTheadEl().hidden = true;
                    this.dataTable.getTableEl().className += "eds-documents";

                    this.dataTable.set("MSG_EMPTY", "Нет документов");
                    this.dataTable.set("MSG_ERROR", "Ошибка при выполнении запроса");

                    YAHOO.util.Event.addListener(this.id + "-main", "scroll", this.onContainerScroll, this);

                    this.dataTable.render();
                }
            }
        });
})();