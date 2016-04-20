if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Counters = LogicECM.module.Counters || {};

(function() {

    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink;

    LogicECM.module.Counters.DataGrid = function(containerId) {
        LogicECM.module.Counters.DataGrid.superclass.constructor.call(this, containerId);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Counters.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Counters.DataGrid.prototype, {

        /**
         * Retrieves the Data List from the Repository
         *
         * @method populateDataGrid
         */
        populateDataGrid: function DataGrid_populateDataGrid()
        {
            if (!YAHOO.lang.isObject(this.datagridMeta))
            {
                return;
            }

            this.renderDataGridMeta();

            var itemType = "";

            // Fix for multy types with the same fields set:
            if (this.datagridMeta.itemType.indexOf(",") == -1) {
                itemType = this.datagridMeta.itemType;
            }
            else {
                itemType = this.datagridMeta.itemType.split(",")[0];
            }

            // Query the visible columns for this list's item type
            var configURL = "";
            if (this.options.configURL != null) {
                configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, this.options.configURL + "?nodeRef=" + encodeURIComponent(this.options.datagridMeta.nodeRef));
            } else {
                configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(itemType) + ((this.datagridMeta.datagridFormId != null && this.datagridMeta.datagridFormId != undefined) ? "&formId=" + encodeURIComponent(this.datagridMeta.datagridFormId) : ""));
            }

            Alfresco.util.Ajax.jsonGet(
                {
                    url: configURL,
                    successCallback:
                    {
                        fn: this.onDataGridColumns,
                        scope: this
                    },
                    failureCallback:
                    {
                        fn: this._onDataGridFailure,
                        obj:
                        {
                            title: this.msg("message.error.columns.title"),
                            text: this.msg("message.error.columns.description")
                        },
                        scope: this
                    }
                });
        }

    }, true);
})();
