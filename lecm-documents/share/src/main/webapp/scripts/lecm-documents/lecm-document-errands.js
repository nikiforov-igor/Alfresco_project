if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands|| {};

(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.Errands.Tasks = function ErrandsTasks_constructor(htmlId) {
        LogicECM.module.Errands.Tasks.superclass.constructor.call(this, "LogicECM.module.Errands.Tasks", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.Tasks, Alfresco.component.Base,
        {
            options: {
                itemType: "lecm-errands:document",
                nodeRef: null,
                containerId: "",
                errandsUrl: "",
                anchorId: ""
            },
            listContainer: null,
            selected: null,
            /**
             * html элемент в котрый помещаем результат
             */
            onReady: function () {
                this.listContainer = Dom.get(this.options.containerId);
                this.selected = Dom.get(this.id + "-errands-filter");
                if (this.selected) {
                    this.loadMyErrands();
                }
                Event.on(this.id + "-errands-filter", "change", this.loadMyErrands, this, true);
            },

            loadMyErrands: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.PROXY_URI + this.options.errandsUrl,
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        filter: (this.selected != null) ? this.selected.value : ""
                    },
                    successCallback: {
                        fn: function (response) {
                            this.showErrands(response);
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            showErrands: function(response) {
                this.listContainer.innerHTML = "";
                if (response.json.errands.length > 0) {
                    var results = response.json.errands;
                    for (var i = 0; i < results.length; i++) {
                        var errand = results[i];
                        var status, statusClass;
                        if (errand.isExpired == "true") {
                            status = this.msg("errandslist.label.overdue");
                            statusClass = "errands-overdue";
                        } else {
                            var today = new Date();
                            var endDate = Date.parse(errand.dueDate);
                            var difference = (endDate.getTime()-today.getTime())/1000/60/24;
                            if (difference > 5) {
                                status = this.msg("errandslist.label.new");
                                statusClass = "errands-new";
                            } else {
                                status = this.msg("errandslist.label.after");
                                statusClass = "errands-after";
                            }
                        }
                        var isImportant = (errand.isImportant == "false") ? "WORKFLOWTASKPRIORITY_LOW" : "WORKFLOWTASKPRIORITY_HIGH";
                        var isImportantTitle = (errand.isImportant == "true") ? this.msg("errandslist.label.important") : "";

                        var detail = "<div class=\"workflow-task-item\">";
                        detail += "<div class=\"workflow-task-list-picture " + isImportant + "\" title=\"" + isImportantTitle + "\">&nbsp;</div>";
                        detail += "<div style=\"float: left;\">";
                        detail += "<div>";
                        detail += "<div class=\"workflow-task-title workflow-task-list-left-column\" style=\"font-size: 16px;\">";
                        detail += "<a href=\""+window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT+"document?nodeRef="+ errand.nodeRef +"\">" + errand.title + ":</a>";
                        detail += "</div>";
                        detail += "<span class=\"workflow-task-status "+ statusClass +"\">" + status + "</span>";
                        detail += "</div>";
                        detail += "<div style=\"clear: both;\"></div>";
                        detail += "<div class=\"workflow-task-description\">" + errand.description + "</div>";
                        detail += "<div>";
                        detail += "<div class=\"workflow-task-list-left-column\">";
                        detail += "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.duedate") + ": </span>" + errand.dueDate;
                        detail += "</div>";
                        detail += "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.status") + ": </span>" + errand.statusMessage;
                        detail += "</div>";
                        detail += "</div>";
                        detail += "<div style=\"clear: both;\"></div>";
                        detail += "</div>";
                        this.listContainer.innerHTML += detail;
                    }
                }
                if (this.options.anchorId != "") {
                    Dom.get(this.options.anchorId).scrollIntoView();
                }
            }

        });
})();