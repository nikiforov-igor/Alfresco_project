/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM GroupActionsmodule namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.GroupActions
 */
LogicECM.module.GroupActions = LogicECM.module.GroupActions || {};

/**
 * Data Lists: Toolbar component.
 *
 * Displays a list of Toolbar
 *
 * @namespace Alfresco
 * @class LogicECM.module.GroupActions.Toolbar
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        UA = YAHOO.util.UserAction,
        Connect = YAHOO.util.Connect;

    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Dictionary.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.GroupActions.Toolbar = function(htmlId)
    {
	    return LogicECM.module.GroupActions.Toolbar.superclass.constructor.call(this, "LogicECM.module.GroupActions.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.GroupActions.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.GroupActions.Toolbar.prototype,
        {
	        options: {
		        dictionaryName: null,
		        searchButtonsType: 'defaultActive',
		        newRowButtonType: 'defaultActive'
	        },

            /**
             * FileUpload module instance.
             *
             * @property fileUpload
             * @type Alfresco.FileUpload
             */
            fileUpload: null,
            panelCsv:null,
            panelXml: null,

            _initButtons: function () {
                this.toolbarButtons[this.options.searchButtonsType].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
	            this.toolbarButtons[this.options.searchButtonsType].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
            },
            
            // по нажатию на кнопку Поиск
            onSearchClick: function BaseToolbar_onSearch(e, obj) {
                var searchTerm = Dom.get(this.id + "-full-text-search").value;

                var maySearch = this.options.minSTermLength == null || this.options.minSTermLength <= 0 || searchTerm.length == 0;
                if (!maySearch) {// проверяем длину терма
                    maySearch = (searchTerm.length >= this.options.minSTermLength);
                }
                if (maySearch){
                    var dataGrid = this.getGroupActionsDataGrid();
                    var datagridMeta = dataGrid.datagridMeta;
                    
                    if (searchTerm.length > 0) {
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
                        datagridMeta.sort = datagridMeta.sort ? datagridMeta.sort : "cm:modified|false";
                        if (datagridMeta.searchConfig.formData) {
                            if (typeof datagridMeta.searchConfig.formData == "string") {
                                datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);

                            }
                            datagridMeta.searchConfig.formData.datatype = datagridMeta.itemType;
                        } else {
                            datagridMeta.searchConfig.formData = {
                                datatype: datagridMeta.itemType
                            };
                        }
                        dataGrid.search.performSearch({
                            searchConfig: datagridMeta.searchConfig,
                            searchNodes: datagridMeta.searchNodes,
                            searchShowInactive: dataGrid.options.searchShowInactive,
                            sort: datagridMeta.sort,
                            useOnlyInSameOrg: datagridMeta.useOnlyInSameOrg,
                            useFilterByOrg: datagridMeta.useFilterByOrg
                        });
                        YAHOO.Bubbling.fire("showFilteredLabel");
                        YAHOO.Bubbling.fire("showFullTextSearchLabel");
                    } else {
                        this.onClearSearch();
                    }

                    if (obj && obj[1]) {
                        obj[1].preventDefault();
                    }
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            displayTime: 3,
                            text: this.msg("label.need_more_symbols_for_search")
                        });
                }
            },
            
            /**
             * Очистка поиска
             * @constructor
             */
            onClearSearch: function Toolbar_onSearch() {
                Dom.get(this.id + "-full-text-search").value = "";
                var dataGrid = this.getGroupActionsDataGrid();
                if (dataGrid) {
                    var datagridMeta = dataGrid.datagridMeta;

                    //сбрасываем на значение по умолчанию
                    if (datagridMeta.searchConfig) {
                        // сбрасываем терм поиска
                        if (datagridMeta.searchConfig.fullTextSearch) {
                            if (typeof datagridMeta.searchConfig.fullTextSearch == "string") {
                                datagridMeta.searchConfig.fullTextSearch = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.fullTextSearch);
                            }
                            datagridMeta.searchConfig.fullTextSearch.searchTerm = null;

                            //проверяем, заполнена ли форма (атрибутивный поиск)
                            if (datagridMeta.searchConfig.formData) {
                                if (typeof datagridMeta.searchConfig.formData == "string") {
                                    datagridMeta.searchConfig.formData = YAHOO.lang.JSON.parse(datagridMeta.searchConfig.formData);
                                }
                                var nProps = 0;
                                for (var key in datagridMeta.searchConfig.formData) {
                                    nProps++;
                                }
                                if (nProps <= 1) {
                                    datagridMeta.searchConfig.fullTextSearch = null;
                                }
                            } else {
                                datagridMeta.searchConfig.fullTextSearch = null;
                            }
                        }
                    }
                    dataGrid.search.performSearch({
                        parent: datagridMeta.nodeRef,
                        itemType: datagridMeta.itemType,
                        searchConfig: datagridMeta.searchConfig,
                        searchShowInactive: dataGrid.options.searchShowInactive,
	                    sort: datagridMeta.sort,
                        useOnlyInSameOrg: datagridMeta.useOnlyInSameOrg,
                        useFilterByOrg: datagridMeta.useFilterByOrg
                    });
                    if (!datagridMeta.searchConfig || !datagridMeta.searchConfig.fullTextSearch) {
                        YAHOO.Bubbling.fire("hideFilteredLabel");
                    }
                    YAHOO.Bubbling.fire("hideFullTextSearchLabel");
                    this.checkShowClearSearch();
                }
            },
            
            getGroupActionsDataGrid: function getGroupActionsDataGrid() {
            	var result = null;
            	var components = Alfresco.util.ComponentManager.list();
            	for (var i=0; i<components.length; ++i) {
            		if (components[i].name == 'LogicECM.module.Base.DataGrid_group-actions-container') {
            			result = components[i];
            		}
            	}
            	
            	return result;
            }
            
        }, true);
})();