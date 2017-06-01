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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

/**
 * Data Lists: Toolbar component.
 *
 * Displays a list of Toolbar
 *
 * @namespace Alfresco
 * @class LogicECM.module.Dictionary.Toolbar
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
    LogicECM.module.Dictionary.Toolbar = function(htmlId)
    {
	    return LogicECM.module.Dictionary.Toolbar.superclass.constructor.call(this, "LogicECM.module.Dictionary.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Dictionary.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Dictionary.Toolbar.prototype,
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
                this.toolbarButtons[this.options.newRowButtonType].createButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow);

                this.toolbarButtons[this.options.searchButtonsType].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
	            this.toolbarButtons[this.options.searchButtonsType].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
                this.toolbarButtons["defaultActive"].importCsvButton = Alfresco.util.createYUIButton(this, "importCsvButton", function () {
                    });

                this.groupActions.exportCsvButton = Alfresco.util.createYUIButton(this, "exportCsvButton", this.onExportCSV,
                    {
                        disabled: true
                    });
                this.groupActions.deleteButton = Alfresco.util.createYUIButton(this, "deleteButton", this.onDeleteRow,
                    {
                        disabled: true
                    });
            },

            /**
             * Экспорт CSV
             */
            onExportCSV: function(){
	            var dataGrid = this.modules.dataGrid;
	            if (dataGrid) {
		            dataGrid.onExportCsv(this.options.dictionaryName);
	            }
            },

            /**
             * Удаление выбранного значения в dataGrid.
             * Появляется диалоговое окно с потверждением на удаление
             */
            onDeleteRow: function Toolbar_onDeleteRow() {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    // Get the function related to the clicked item
                    var fn = "onActionDelete";
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, dataGrid.getSelectedItems(), null, {fullDelete: false});
                    }
                }
            }
        }, true);
})();