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
            dataTable: null,

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
                // локализуем дату
                YAHOO.util.DateLocale[this.msg("locale")] = {
                    b: [this.msg("label.date.month0"),
                        this.msg("label.date.month1"),
                        this.msg("label.date.month2"),
                        this.msg("label.date.month3"),
                        this.msg("label.date.month4"),
                        this.msg("label.date.month5"),
                        this.msg("label.date.month6"),
                        this.msg("label.date.month7"),
                        this.msg("label.date.month8"),
                        this.msg("label.date.month9"),
                        this.msg("label.date.month10"),
                        this.msg("label.date.month11")]
                };
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
                            fn: this.showDataTable,
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

            showDataTable: function showDataTableDashlet(response, obj){

                if (response.json.data.length > 0) {
                    this.skipCount = this.skipCount + response.json.paging.totalItems;
                }

                if (this.dataTable == null) {
                    var columnDefs = [
                        { key: "isImportant", label: "", sortable: false, formatter: this.bind(this.renderCellIcons), width: "16", className: "image"},
                        { key: "baseDocString", label: "", sortable: false, formatter: this.bind(this.renderCellIcons), width: "16", className: "image"},
                        { key: "linkString", label: "", sortable: false, formatter: this.bind(this.renderCellIcons)}
                    ];
                    var initialSource = new YAHOO.util.DataSource(response.json.data);
                    initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                    initialSource.responseSchema = {isImportant: "isImportant", baseDocString: "baseDocString", linkString: "linkString"};

                    this.dataTable = new YAHOO.widget.DataTable(this.id + "-errands", columnDefs, initialSource, {});
                    this.dataTable.getTheadEl().hidden = true;
                    this.dataTable.getTableEl().className += "my-errands";

                } else {
                    this.dataTable.addRows(response.json.data);
                }
            },

            /**
             * Priority & pooled icons custom datacell formatter
             */
            renderCellIcons: function (elCell, oRecord, oColumn, oData)
            {
                var data = oRecord.getData(),
                    desc = "";

                if (oColumn.key =="isImportant" && data.isImportant == "true") {
                    elCell.className += " " + oColumn.className;
                    oColumn.getThEl().className += "" + oColumn.className;
                    desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/exclamation_16.png'
                        + '" width="16" alt="' + this.msg("label.important") + '" title="' + this.msg("label.important")
                        + '" />';
                }

                if (oColumn.key =="baseDocString" && data.baseDocString != undefined) {
                    elCell.className += " " + oColumn.className;
                    oColumn.getThEl().className += "" + oColumn.className;
                    desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/base_doc_16.png'
                        + '" width="16" alt="' + data.baseDocString + '" title="' + data.baseDocString + '" />';
                }

                if (oColumn.key =="linkString") {
                    desc = "<a href='" + window.location.protocol + "//" + window.location.host +Alfresco.constants.URL_PAGECONTEXT
                        + "document?nodeRef="+ data.nodeRef + "'>"+data.number +": \""+ data.title + "\"</a> ";
                    desc += "<div class='info'>" + this.msg("label.from") + " <a href='" + window.location.protocol + "//" + window.location.host +
                        Alfresco.constants.URL_PAGECONTEXT + "view-metadata?nodeRef="+ data.initiator + "'>"
                        + data.initiator_name + ",</a> ";
                    if (data.date) {
                        desc += this.msg("label.up.to") + " " + YAHOO.util.Date.format(new Date(data.date), {format: "%d %b %Y"}, this.msg("locale"));
                    } else {
                        desc += "без срока";
                    }
                    desc += "</div> ";
                    if (data.isExpired == "true") {
                        desc  += "<div class='expired'>" + this.msg("label.is-expired") + "</div>";
                    }
                }

                elCell.innerHTML = desc;
            },

            /**
             * List load failed
             * @method onListLoadFailed
             */
            onListLoadFailed: function ()
            {
                this.errandsList.innerHTML = '<div class="detail-list-item first-item last-item">' + this.msg("label.load-failed") + '</div>';
            }

        });
})();
