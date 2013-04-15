(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Contracts.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Contracts.Toolbar = function (htmlId) {
        LogicECM.module.Contracts.Toolbar.superclass.constructor.call(this, "LogicECM.module.Contracts.Toolbar", htmlId, ["button", "container"]);
        this.toolbarButtons = {};
        // Decoupled event listeners
        YAHOO.Bubbling.on("initDatagrid", this.onInitDataGrid, this);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Contracts.Toolbar, Alfresco.component.Base);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Contracts.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                bubblingLabel: null,
                destination: null,
                itemType:null
            },

            /**
             * Кнопки Tollbara, активируются при выборе элемента в дереве
             * @constructor
             */
            toolbarButtons: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             *
             * @method onReady
             */
            onReady: function () {
                this.toolbarButtons.newContractButton = Alfresco.util.createYUIButton(this, "newContractButton", this.onNewRow,
                    {
                        disabled: false,
                        value: "create"
                    });

                this.toolbarButtons.searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                    {
                        disabled: true
                    });

                this.toolbarButtons.exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                    {
                        disabled: true
                    });

                var me = this;


                // Search
                this.checkShowClearSearch();
                Event.on(this.id + "-clearSearchInput", "click", this.onClearSearch, null, this);
                Event.on(this.id + "-full-text-search", "keyup", this.checkShowClearSearch, null, this);

                var searchInput = Dom.get(this.id + "-full-text-search");
                new YAHOO.util.KeyListener(searchInput,
                    {
                        keys: 13
                    },
                    {
                        fn: me.onSearchClick,
                        scope: this,
                        correctScope: true
                    }, "keydown").enable();

                // Finally show the component body here to prevent UI artifacts on YUI button decoration
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            /**
             * New Row button click handler
             */
            onNewRow: function (e, p_obj) {
                var destination = this.options.destination,
                    itemType = this.options.itemType;
                this.showCreateDialog({itemType: itemType, nodeRef: destination}, null, null);
            },

            // инициализация грида
            onInitDataGrid: function (layer, args) {
                var datagrid = args[1].datagrid;
                if ((!this.options.bubblingLabel || !datagrid.options.bubblingLabel) || this.options.bubblingLabel == datagrid.options.bubblingLabel) {
                    this.modules.dataGrid = datagrid;
                }
            },

            // по нажатию на кнопку Поиск
            onSearchClick: function () {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var dataGrid = this.modules.dataGrid;
                var datagridMeta = dataGrid.datagridMeta;

                var me = this;
                if (searchTerm.length > 0) {
                    var columns = dataGrid.datagridColumns;

                    var fields = dataGrid.getTextFields();
                    var fullTextSearch = {
                        parentNodeRef: datagridMeta.nodeRef,
                        fields: fields,
                        searchTerm: searchTerm
                    };
                    if (!datagridMeta.searchConfig) {
                        datagridMeta.searchConfig = {};
                    }
                    datagridMeta.searchConfig.fullTextSearch = fullTextSearch;
                    datagridMeta.searchConfig.formData = {
                        datatype: datagridMeta.itemType
                    };
                    this.modules.dataGrid.search.performSearch({
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: false,
                        sort: datagridMeta.sort
                    });
                    YAHOO.Bubbling.fire("showFilteredLabel");
                } else {
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }
                    this.modules.dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: false
                    });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                }
            },

            // клик на Атрибутивном Поиске
            onExSearchClick:function () {
                var grid = this.modules.dataGrid;
                var advSearch = grid.search;

                advSearch.showDialog(grid.datagridMeta);
            },

            _hasEventInterest: function DataGrid_hasEventInterest(bubbleLabel) {
                if (!this.options.bubblingLabel || !bubbleLabel) {
                    return true;
                } else {
                    return this.options.bubblingLabel == bubbleLabel;
                }
            },
            /**
             * Скрывает кнопку поиска, если строка ввода пустая
             * @constructor
             */
            checkShowClearSearch: function Toolbar_checkShowClearSearch() {
                if (Dom.get(this.id + "-full-text-search").value.length > 0) {
                    Dom.setStyle(this.id + "-clearSearchInput", "visibility", "visible");
                } else {
                    Dom.setStyle(this.id + "-clearSearchInput", "visibility", "hidden");
                }
            },
            /**
             * Очистка поиска
             * @constructor
             */
            onClearSearch: function Toolbar_onSearch() {
                Dom.get(this.id + "-full-text-search").value = "";
                if (this.modules.dataGrid) {
                    var dataGrid = this.modules.dataGrid;
                    var datagridMeta = dataGrid.datagridMeta;
                    datagridMeta.searchConfig = dataGrid.initialSearchConfig;
                    if (datagridMeta.searchConfig.fullTextSearch) {
                        datagridMeta.searchConfig.fullTextSearch = null;
                    }
                    YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta: datagridMeta
                        });
                    YAHOO.Bubbling.fire("hideFilteredLabel");
                    this.checkShowClearSearch();
                }
            },

            showCreateDialog:function (meta, callback, successMessage) {
                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    var addMsg = meta.addMessage;
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", addMsg ? addMsg : this.msg("label.create-row.title") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                    {
                        itemKind:"type",
                        itemId:meta.itemType,
                        destination:meta.nodeRef,
                        mode:"create",
                        formId: meta.createFormId != null ? meta.createFormId : "",
                        submitType:"json"
                    });

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width:"50em",
                        templateUrl:templateUrl,
                        actionUrl:null,
                        destroyOnHide:true,
                        doBeforeDialogShow:{
                            fn:doBeforeDialogShow,
                            scope:this
                        },
                        onSuccess:{
                            fn:function DataGrid_onActionCreate_success(response) {
                                if (callback) {// вызов дополнительного события
                                    callback.call(this, response.json.persistedObject);
                                } else { // вызов события по умолчанию
                                    YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                        {
                                            nodeRef:response.json.persistedObject,
                                            bubblingLabel:this.options.bubblingLabel
                                        });
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:this.msg(successMessage ? successMessage : "message.save.success")
                                        });
                                }
                            },
                            scope:this
                        },
                        onFailure:{
                            fn:function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text:this.msg("message.save.failure")
                                    });
                            },
                            scope:this
                        }
                    }).show();
            }
        }, true);
})();