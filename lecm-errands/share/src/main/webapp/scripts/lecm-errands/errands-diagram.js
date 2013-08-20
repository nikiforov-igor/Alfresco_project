/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands|| {};


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
     /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Errands.Diagram = function ErrandsDiagram_constructor(htmlId) {
        LogicECM.module.Errands.Diagram.superclass.constructor.call(this, "LogicECM.module.Errands.Diagram", htmlId, ["button", "container","paginator"]);
        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.Errands.Diagram, Alfresco.component.Base,
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
                maxItems: 20,
                initialPage: 1

            },

            errandsList: null,
            skipCount: 0,
            dataTable: null,
            maxDate: null,
            minDate: null,

            calendarData: {},
            columnDefs:[],

            /**
             * Fired by YUI when parent element is available for scripting
             * @method onReady
             */
            onReady: function ()
            {
                // The activity list container
                this.errandsList = Dom.get(this.id + "-errands-diagram");
                this.populateErrandsList();
//                YAHOO.util.Event.addListener(this.id + "-paginator", "scroll", this.onContainerScroll, this);
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
                        url: Alfresco.constants.PROXY_URI  + "lecm/errands/api/activeErrands",
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

                if(response.json.data.length > 0) {
//                    this.skipCount = this.skipCount + response.json.paging.totalItems;
//                    this.options.maxItems = this.skipCount ;
                    this.maxDate =  new Date(response.json.paging.maxDate);
                    this.minDate = (response.json.paging.minDate == "") ? new Date() : new Date(response.json.paging.minDate);
                }

                if (this.dataTable == null) {

                }
                    this.columnDefs = [
                        { key: "number", label: this.msg("datagrid.column.number"), sortable: false, formatter: this.bind(this.renderCellIcons)},
                        { key: "taskName", label: this.msg("datagrid.column.taskName"), sortable: false, formatter: this.bind(this.renderCellIcons)},
                        { key: "executor", label: this.msg("datagrid.column.executor"), sortable: false, formatter: this.bind(this.renderCellIcons)},
                        { key: "startDate", label: this.msg("datagrid.column.startDate"), sortable: false, formatter: this.bind(this.renderCellIcons)},
                        { key: "endDate", label: this.msg("datagrid.column.endDate"), sortable: false, formatter: this.bind(this.renderCellIcons)},
                        { key: "duration", label: this.msg("datagrid.column.duration"), sortable: false, formatter: this.bind(this.renderCellIcons)}
                    ];
                    this.buildCalendar(response);
                    var initialSource = new YAHOO.util.DataSource(response.json.data);
                    initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                    this.errandsList.innerHTML = "";
                    this.dataTable = new YAHOO.widget.DataTable(this.id + "-errands-diagram", this.columnDefs, initialSource, {
//                        initialLoad: false,
                        dynamicData: true,
                        scope: this,
                        generateRequest: this.generateRequest,
                        paginator : new YAHOO.widget.Paginator({
                            rowsPerPage: this.options.maxItems,
                            totalRecords:response.json.paging.maxRecord,
                            containers: [this.id + "-errands-paginator"],
                            template: this.msg("pagination.template"),
                            initialPage: this.options.initialPage
                        })
                    });

//                    // Set width as a string value
                    new YAHOO.widget.ScrollingDataTable(this.id + "-errands-diagram", this.columnDefs,
                        initialSource, {width:"100%"});
//                } else {
//                    console.log("sdfsdf");
//                    this.dataTable.doBeforeLoadData = this.doBeforeLoadData();
//                }
            },

//            doBeforeLoadData: function(oRequest, oResponse, oPayload) {
//                  oPayload.totalRecords = oResponse.meta.totalRecords;
//                  oPayload.pagination.recordOffset = oResponse.meta.startIndex;
//                  return oPayload;
//            },

            generateRequest: function () {
                var me = arguments[1].configs.scope;
                var obj = arguments[0].pagination;

//                me.options.maxItems = ((obj.recordOffset + obj.rowsPerPage)>obj.totalRecords) ? obj.totalRecords : (obj.recordOffset + obj.rowsPerPage);
                me.options.initialPage = obj.page;
                me.skipCount = obj.recordOffset;
                me.populateErrandsList();
            },

                /**
                 * Priority & pooled icons custom datacell formatter
                 */
            renderCellIcons : function (elCell, oRecord, oColumn, oData)
            {
                var data = oRecord.getData(),
                    desc = "";
                var date;

                if (oColumn.key =="number") {
                    desc = "<a href='" + window.location.protocol + "//" + window.location.host +Alfresco.constants.URL_PAGECONTEXT
                        + "document?nodeRef="+ data.nodeRef + "'>"+ data.number + "</a> ";
                }
                if (oColumn.key =="taskName") {
                    desc = "<a href='" + window.location.protocol + "//" + window.location.host +Alfresco.constants.URL_PAGECONTEXT
                        + "document?nodeRef="+ data.nodeRef + "'>"+ data.title + "</a> ";
//                    oColumn.getThEl().width = (desc.length+1)+"px"
                }
                if (oColumn.key =="executor") {
                    desc = "<a href='javascript:void(0);' onclick=\"viewAttributes('"+
                        data.executorRef + "', null, 'logicecm.employee.view')\">"+ data.executor_name + "</a> ";
                }
                if (oColumn.key =="startDate") {
                    if (data.startDate != undefined) {
                        date = new Date(data.startDate);
                        desc = date.getDate() + "/" + date.getMonth() + "/" + date.getFullYear();
                    }
                }
                if (oColumn.key =="endDate") {
                    if (data.endDate != undefined) {
                        date = new Date(data.endDate);
                        desc = date.getDate() + "/" + date.getMonth() + "/" + date.getFullYear();
                    }
                }
                if (oColumn.key =="duration") {
                    if ((data.startDate != undefined) && (data.endDate != undefined)){
                        var startDate = new Date(data.startDate);
                        var endDate = new Date(data.endDate );
                        desc = (endDate.getDate()+1) - startDate.getDate() + this.msg("label.day");
                    } else {
                        desc = this.msg("label.unknown")
                    }
                }

                elCell.innerHTML = desc;
            },

            /**
             * Priority & pooled icons custom datacell formatter
             */
            drawCellIcons: function (elCell, oRecord, oColumn, oData)
            {
                var data = oRecord.getData(),
                    desc = "", startDate, endDate;

                startDate = (data.startDate == undefined) ? this.minDate : new Date(data.startDate);
                endDate = (data.endDate == undefined) ? this.maxDate : new Date(data.endDate);

                var dayColumn = new Date();
                dayColumn.setDate(oColumn.label);
                dayColumn.setMonth(oColumn.getParent().key);
                dayColumn.setFullYear(oColumn.getParent().year);

                endDate.setHours(23);
                endDate.setMinutes(59);
                endDate.setSeconds(59);

                if (startDate <= dayColumn && dayColumn <= endDate) {
                    if (data.isExpired=="true") {
                        elCell.setAttribute('class',elCell.getAttribute('class')+' redline');
                    } else {
                        elCell.setAttribute('class',elCell.getAttribute('class')+' blueline');
                    }
                }


                if (oColumn.key =="taskName") {
                    desc = "<a href='" + window.location.protocol + "//" + window.location.host +Alfresco.constants.URL_PAGECONTEXT
                        + "document?nodeRef="+ data.nodeRef + "'>"+ data.title + "</a> ";
                }
//                var minData = new Date(data)

                elCell.innerHTML = desc;
            },

            buildCalendar: function(response){

                if (response.json.data.length > 0) {
                    var maxDate = this.maxDate;
                    var minDate = this.minDate;

                    var calendar = {
                        years: {}
                    };

                    // формируем календарь
                    for (var i=minDate.getFullYear(); i<maxDate.getFullYear()+1; i++) {
                        calendar.years[i] = {};
                        var day = new Date();
                        if (i < maxDate.getFullYear()) {
                            day.setFullYear(i);
                            for (var month=0; month < 12; month++){
                                day.setMonth(month);
                                calendar.years[i][month] = day.getDaysInMonth();
                            }
                        } else {
                            for (var month=minDate.getMonth(); month<maxDate.getMonth()+1; month++) {
                                day.setMonth(month);
                                calendar.years[i][month] = day.getDaysInMonth();
                            }
                        }
                    }

                    // формируем столбец календаря
                    for (var year in calendar.years) {
//                        var yearObj = { key:year, label:year, sortable: false, children:[] };
                        for (var month in calendar.years[year]){
                            var monthObj = { key:month, label:this.msg("column.month"+month) + " (" +year +") ", year:year, sortable: false, children:[] };

                            var startDay = ((this.minDate.getFullYear() == year) && (this.minDate.getMonth() == month)) ? this.minDate.getDate() : 1;
                            var endDay = ((this.maxDate.getFullYear() == year) && (this.maxDate.getMonth() == month)) ? this.maxDate.getDate()+1 : calendar.years[year][month]+1;


                            for (var day = startDay; day < endDay; day++) {
                                var dayObj = { key:year + month + day, label:day, sortable: false, formatter: this.bind(this.drawCellIcons) };
                                monthObj.children.push(dayObj);
                            }
//                            yearObj.children.push(monthObj);
                            this.columnDefs.push(monthObj);
                        }
//                        this.columnDefs.push(yearObj);
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
