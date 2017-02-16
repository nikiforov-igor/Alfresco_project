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
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;
    /**
     * Dashboard TasksSubordinates constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.dashlet.TasksSubordinates} The new component instance
     * @constructor
     */
    LogicECM.dashlet.DocErrands = function (htmlId) {
        LogicECM.dashlet.DocErrands.superclass.constructor.call(this, "LogicECM.dashlet.DocErrands", htmlId);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.dashlet.DocErrands, Alfresco.component.Base);
    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.dashlet.DocErrands.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                /**
                 * Maximum number of tasks to display in the dashlet.
                 *
                 * @property maxItems
                 * @type int
                 * @default 50
                 */
                maxItems: 50,
                parentDoc: null,
                errandJSON: {data:[]}
            },

            errandsList: null,

            skipCount: 0,
            dataTable: null,
            errandTable: null,

            /**
             * Fired by YUI when parent element is available for scripting
             * @method onReady
             */
            onReady: function () {
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

                this.errandsList = Dom.get(this.id + "-issued-errands");
                this.showErrandData();
                this.populateErrandsList();

                /*YAHOO.util.Event.addListener(this.id, "scroll", this.onContainerScroll, this);*/
            },

/*            onContainerScroll: function (event, scope) {
                var container = event.currentTarget;
                if (container.scrollTop + container.clientHeight == container.scrollHeight) {
                    *//*Dom.setStyle(scope.id + "-issued-errands-loading", "visibility", "visible");*//*
                    scope.populateErrandsList();
                }
            },*/

            /**
             * Populate the activity list via Ajax request
             * @method populateContractsList
             */
            populateErrandsList: function () {
                // Load the activity list
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/errands/getIssuedToMeErrands",
                        dataObj: {
                            skipCount: this.skipCount,
                            maxItems: this.options.maxItems,
                            nodeRef:this.options.parentDoc,
                            rolesFields:"lecm-errands:initiator-assoc-ref"
                        },
                        successCallback: {
                            fn: this.showDataTable,
                            scope: this
                        },
                        failureCallback: {
                            fn: this.onListLoadFailed,
                            scope: this
                        },
                        scope: this,
                        execScripts: true
                    });
            }
            ,

            showDataTable: function showDataTableDashlet(response, obj) {
                //if (response.json.data.length > 0) {
                    this.skipCount = this.skipCount + response.json.paging.totalItems;
                    if (this.dataTable == null) {
                        var columnDefs = [
                            { key: "icon", label: "", sortable: false, formatter: this.bind(this.renderIcon), width: "24", className: "image"},
                            { key: "record", label: "", sortable: false, formatter: this.bind(this.renderCell)}
                        ];
                        var initialSource = new YAHOO.util.DataSource(response.json.data);
                        initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                        initialSource.responseSchema = {icon: "icon", record: "record"};

                        this.dataTable = new YAHOO.widget.DataTable(this.id + "-issued-errands", columnDefs, initialSource,
                            {"MSG_EMPTY": this.msg("message.errands.empty")
                        });

                        this.dataTable.getTheadEl().hidden = true;
                        if (response.json.data.length > 0) {
                            this.dataTable.getTableEl().className += "my-errands";
                        } else {
                            this.dataTable.getTableEl().className += "my-errands-empty";
                        }
                    } else {
                        this.dataTable.addRows(response.json.data);
                    }
                    /*Dom.setStyle(this.id + "-issued-errands-loading", "visibility", "hidden");*/
                //}
            },

            showErrandData: function showDataTableDashlet() {
                //if (this.options.errandJSON != null && this.options.errandJSON.data.length > 0) {
                    if (this.errandTable == null) {
                        var columnDefs = [
                            { key: "icon", label: "", sortable: false, formatter: this.bind(this.renderIcon), width: "24", className: "image"},
                            { key: "record", label: "", sortable: false, formatter: this.bind(this.renderCellWithAuthor)}
                        ];
                        var initialSource = new YAHOO.util.DataSource(this.options.errandJSON.data);
                        initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                        initialSource.responseSchema = {icon: "icon", record: "record"};

                        this.errandTable = new YAHOO.widget.DataTable(this.id + "-errand", columnDefs, initialSource,
                            {"MSG_EMPTY": this.msg("message.errands.empty")
                            });
                        this.errandTable.getTheadEl().hidden = true;
                        if (this.options.errandJSON != null && this.options.errandJSON.data.length > 0) {
                            this.errandTable.getTableEl().className += "my-errands";
                        } else {
                            this.errandTable.getTableEl().className += "my-errands-empty";
                        }
                    } else {
                        this.errandTable.getRecordSet().reset();
                        this.errandTable.addRows(this.options.errandJSON.data);
                    }
                //}
            },

            /**
             * Priority & pooled icons custom datacell formatter
             */
            renderCell: function (elCell, oRecord, oColumn, oData) {
                var data = oRecord.getData(),
                    desc = "";

                var additionalClassName = "";
                if (data.isExpired == "true" && (data.status == this.msg("label.errand-status-in-work")|| data.status == this.msg("label.errand-status-not-executed"))) {
                    additionalClassName = "errand-expired";
                } else if (data.status == this.msg("label.errand-status-executed")) {
                    additionalClassName = "errand-executed";
                }

                var errandDescription = data.title
                    + ", " + this.msg("label.errand-executor") + ": " + data.executor_name + ", "
                    + data.date;

                desc = "<a href='" + window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT
                    + "document?nodeRef=" + data.nodeRef + "'>" + errandDescription + "</a> ";

                desc = "<div class='" + additionalClassName + "'>" + desc + "</div>";

                elCell.innerHTML = desc;
            },

            renderIcon:  function (elCell, oRecord, oColumn, oData) {
                var data = oRecord.getData(),
                    desc = "";

                elCell.className += " " + oColumn.className;
                oColumn.getThEl().className += "" + oColumn.className;

                if (data.status == this.msg("label.errand-status-in-work") || data.status == this.msg("label.errand-status-not-executed")) {
                    if (data.isExpired == "true") {
                        desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/errands/expired.png'
                            + '" width="24" alt="' + this.msg("label.expired") + '" title="' + this.msg("label.expired")
                            + '" />';
                    } else {
                        if (data.status == this.msg("label.errand-status-not-executed")) {
                            desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/errands/not_executed.png'
                                + '" width="24" alt="' + this.msg("label.not_executed") + '" title="' + this.msg("label.not_executed")
                                + '" />';
                        }
                    }
                } else if (data.status == this.msg("label.errand-status-executed")) {
                    desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/errands/executed.png'
                        + '" width="24" alt="' + this.msg("label.executed") + '" title="' + this.msg("label.executed")
                        + '" />';
                }

                elCell.innerHTML = desc;
            },

            renderCellWithAuthor: function (elCell, oRecord, oColumn, oData) {
                var data = oRecord.getData(),
                    desc = "";

                elCell.className += " " + oColumn.className;
                oColumn.getThEl().className += "" + oColumn.className;

                var additionalClassName = "";
                if (data.isExpired == "true" &&
                    (data.status == this.msg("label.errand-status-in-work") || data.status == this.msg("label.errand-status-not-executed"))) {
                    additionalClassName = "errand-expired";
                } else if (data.status == this.msg("label.errand-status-executed")) {
                    additionalClassName = "errand-executed";
                }

                var errandDescription = data.title + ","
                    + this.msg("label.errand-author") + ": " + data.initiator_name + ", "
                    + data.date;
                desc = "<a href='" + window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT
                    + "document?nodeRef=" + data.nodeRef + "'>" + errandDescription + "</a> ";

                desc = "<div class='" + additionalClassName + "'>" + desc + "</div>";

                elCell.innerHTML = desc;
            },
            /**
             * List load failed
             * @method onListLoadFailed
             */
            onListLoadFailed: function () {
                /*Dom.setStyle(this.id + "-issued-errands-loading", "visibility", "hidden");*/
                if (this.errandsList) {
                    this.errandsList.innerHTML = '<div class="detail-list-item first-item last-item">' + this.msg("label.load-failed") + '</div>';
                }
            }
        });
})();
