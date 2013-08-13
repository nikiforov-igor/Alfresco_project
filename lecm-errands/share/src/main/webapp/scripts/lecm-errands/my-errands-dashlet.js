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
    LogicECM.dashlet.MyErrands = function (htmlId)
    {
        LogicECM.dashlet.MyErrands.superclass.constructor.call(this, "LogicECM.dashlet.MyErrands", htmlId, ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]);

        // Services
        this.services.preferences = new Alfresco.service.Preferences();

        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.dashlet.MyErrands, Alfresco.component.Base);
    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.dashlet.MyErrands.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:
            {
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
            skipCount: 0,

            /**
             * Fired by YUI when parent element is available for scripting
             * @method onReady
             */
            onReady: function ()
            {
                // The activity list container
                this.errandsList = Dom.get(this.id + "-errands");
                this.populateErrandsList();
                YAHOO.util.Event.addListener(this.id + "-paginator", "scroll", this.onContainerScroll, this);
            },
            onContainerScroll: function (event, scope) {
                var container = event.currentTarget;
                if (container.scrollTop + container.clientHeight == container.scrollHeight) {
                    Dom.setStyle(scope.id + "-loading", "visibility", "visible");
                    scope.populateErrandsList();
                }
            },
            /**
             * Populate the activity list via Ajax request
             * @method populateContractsList
             */
            populateErrandsList: function Contracts_populateContractsList()
            {
                // Load the activity list
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI  + "lecm/errands/getErrandsFilter",
                        dataObj:{
                            skipCount: this.skipCount,
                            maxItems: this.options.maxItems
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
                if (p_response.json.data.length > 0) {
                    this.skipCount = this.skipCount + p_response.json.paging.totalItems;
                    var results = p_response.json.data;
                    for (var i = 0; i < results.length; i++) { // [].forEach() не работает в IE
                        var item = results[i];
                        var div = this.createRow();
                        var detail = document.createElement('span');
                        if (item.isImportant == "true") {
                            detail.innerHTML = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/exclamation_16.png' + '" width="16" alt="' + this.msg("label.important") + '" title="' + this.msg("label.important") + '" />';
                        }
                        if (item.baseDocString != undefined) {
                            detail.innerHTML += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/base_doc_16.png' + '" width="16" alt="' + item.baseDocString + '" title="' + item.baseDocString + '" />';
                        }
                        var str = "<a href='" + window.location.protocol + "//" + window.location.host +
                            Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef="+ item.nodeRef + "'>"+
                            item.record + "</a> ";
                        detail.innerHTML += item.record.replace(item.record, str);
                        if (item.isExpired == "true") {
                            detail.innerHTML += "<div class='expired'>" + this.msg("label.is-expired") + "</div>" ;
                        }
                        div.appendChild(detail);
                        this.errandsList.appendChild(div);
                    }
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
