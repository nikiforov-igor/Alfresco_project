/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
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
                this.toolbarButtons[this.options.newRowButtonType] = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow);

                this.toolbarButtons[this.options.searchButtonsType].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearch);
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
                var datagridMeta = this.modules.dataGrid.datagridMeta;
                var selectItems = this.modules.dataGrid.selectedItems;
                var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(datagridMeta.itemType) + "&formId=export-fields";
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: sUrl,
                        successCallback:
                        {
                            fn: function(response){
                                var datagridColumns = response.json.columns;
                                var fields = "";
                                var items = "";
                                var columns = "";
                                for (var nodeIndex in datagridColumns) {
                                    fields += (fields.length > 0 ? "," : "") + encodeURIComponent(datagridColumns[nodeIndex].name);
                                    columns += (columns.length > 0 ? "," : "") + encodeURIComponent(datagridColumns[nodeIndex].label);
                                }
                                for (var item in selectItems) {
                                    if (selectItems[item]) {
                                        items += (items.length > 0 ? "," : "") + encodeURIComponent(item);
                                    }
                                }
                                document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export-csv"
                                    + "?fields=" + fields
                                    + "&datagridColumns=" + columns
                                    + "&selectedItems=" + items
                                    + "&fileName=dictionary";
                            },
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: function() {alert("Failed to load webscript export CSV.")},
                            scope: this
                        }
                    });
            }
        }, true);
})();