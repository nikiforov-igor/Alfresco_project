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
 * @class LogicECM.dashlet
 */
LogicECM.dashlet = LogicECM.dashlet || {};


/**
 * Dashboard TasksSubordinates component.
 *
 * @namespace LogicECM.dashlet
 * @class LogicECM.dashlet.TasksSubordinates
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $siteURL = Alfresco.util.siteURL;
    /**
     * Preferences
     */
    var PREFERENCES_TASKS_DASHLET_FILTER = "ru.it.lecm.share.errands.dashlet.filter";

    /**
     * Dashboard TasksSubordinates constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.dashlet.TasksSubordinates} The new component instance
     * @constructor
     */
    LogicECM.dashlet.Errands = function (htmlId)
    {
        LogicECM.dashlet.Errands.superclass.constructor.call(this, "LogicECM.dashlet.Errands", htmlId, ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]);

        // Services
        this.services.preferences = new Alfresco.service.Preferences();

        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.dashlet.Errands, Alfresco.component.Base);

    /**
     * Augment prototype with Common Workflow actions to reuse createFilterURLParameters
     */
//    YAHOO.lang.augmentProto(LogicECM.dashlet.Errands, Alfresco.action.WorkflowActions);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.dashlet.Errands.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:
            {
                activeFilter: "important",
                /**
                 * Task types not to display
                 *
                 * @property hiddenTaskTypes
                 * @type object
                 * @default []
                 */
                hiddenTaskTypes: [],

                /**
                 * Maximum number of tasks to display in the dashlet.
                 *
                 * @property maxItems
                 * @type int
                 * @default 50
                 */
                maxItems: 50
            },

            errandsList: null,

            /**
             * Fired by YUI when parent element is available for scripting
             * @method onReady
             */
            onReady: function ()
            {
                // Create filter menu
                this.widgets.filterMenuButton = Alfresco.util.createYUIButton(this, "sorting", this.onFilterSelected,
                    {
                        type: "menu",
                        menu: "sorting-menu",
                        lazyloadmenu: true
                    });

                // The activity list container
                this.errandsList = Dom.get(this.id + "-errands");

                this.widgets.filterMenuButton.set("label", this.msg("sorting.important"));
                this.widgets.filterMenuButton.value = "important";
                // Display the toolbar now that we have selected the filter
                Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
            },

            /**
             * Reloads the list with the new filter and updates the filter menu button's label
             *
             * @param p_sType {string} The event
             * @param p_aArgs {array} Event arguments
             */
            onFilterSelected: function (p_sType, p_aArgs)
            {
                var menuItem = p_aArgs[1];

                if (menuItem)
                {
                    this.widgets.filterMenuButton.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.filterMenuButton.value = menuItem.value;

//                    var parameters = this.substituteParameters(this.options.filters[menuItem.value], {});
//                    this.widgets.alfrescoDataTable.loadDataTable(parameters);
                    this.populateErrandsList(this.widgets.filterMenuButton.value);
                    // Save preferences
                    this.services.preferences.set(PREFERENCES_TASKS_DASHLET_FILTER, menuItem.value);
                }
            },
            /**
             * Populate the activity list via Ajax request
             * @method populateContractsList
             */
            populateErrandsList: function Contracts_populateContractsList(filter)
            {
                var newId = Alfresco.util.generateDomId();
                // Load the activity list
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI  + "lecm/errands/getErrandsFilter",
                        dataObj:
                        {
                            filterId: this.widgets.filterMenuButton.value
                        },
                        successCallback:
                        {
                            fn: this.onListLoaded,
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: this.onListLoadFailed,
                            scope: this
                        },
                        scope: this,
                        execScripts: true
                    });
            },

            createRow: function(innerHtml) {
                var div = document.createElement('div');

                div.setAttribute('class', 'row');
                if (innerHtml) {
                    div.innerHTML = innerHtml;
                }
                return div;
            },
            onListLoaded: function Contracts_onListLoaded(p_response, p_obj)
            {
                this.options.activeFilter = p_obj;
                var html = p_response.serverResponse.responseText;
                if (p_response.json.length > 0) {
                    this.errandsList.innerHTML = "";
                    var results = p_response.json;
                    for (var i = 0; i < results.length; i++) { // [].forEach() не работает в IE
                        var item = results[i];
                        var div = this.createRow();
                        var detail = document.createElement('span');

                        detail.innerHTML = "Поручение: " + "<a href='" + window.location.protocol + "//" + window.location.host +
                            Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef="+ item.nodeRef + "'>" +
                            item.title + " № " + item.number + ",</a>" + " исполнитель " +
                            "<a href='" + window.location.protocol + "//" + window.location.host +
                            Alfresco.constants.URL_PAGECONTEXT + "view-metadata?nodeRef="+ item.executor + "'>" +
                            item.executor_name + ",</a>" + " срок: " + item.limit + "<br/>" ;
                        detail.setAttribute('class', 'detail');
                        div.appendChild(detail);
                        this.errandsList.appendChild(div);
                    }
                } else {
                    this.errandsList.innerHTML = this.msg("label.not-record");
                }
            },

            /**
             * List load failed
             * @method onListLoadFailed
             */
            onListLoadFailed: function Contracts_onListLoadFailed()
            {
                this.errandsList.innerHTML = '<div class="detail-list-item first-item last-item">' + this.msg("label.load-failed") + '</div>';
            }

        });
})();
