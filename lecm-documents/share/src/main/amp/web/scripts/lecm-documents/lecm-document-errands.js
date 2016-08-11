if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands|| {};

(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Errands.Lists = function ErrandsTasks_constructor(htmlId) {
        LogicECM.module.Errands.Lists.superclass.constructor.call(this, "LogicECM.module.Errands.Lists", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.Lists, Alfresco.component.Base,
            {
                options: {
                    itemType: "lecm-errands:document",
                    nodeRef: null,
                    anchorId: "",
                    filter: ""
                },
                listContainer: null,
                selected: null,
                /**
                 * html элемент в котрый помещаем результат
                 */
                onReady: function () {
                    var me = this;

                    Dom.addClass(me.id + "-meErrands-parent", "hidden1");
                    Dom.addClass(me.id + "-issuedByMeErrands-parent", "hidden1");
                    Dom.addClass(me.id + "-controlledMeErrands-parent", "hidden1");
                    Dom.addClass(me.id + "-otherErrands-parent", "hidden1");

                    Dom.addClass(me.id + "-meErrands-label", "hidden1");
                    Dom.addClass(me.id + "-issuedByMeErrands-label", "hidden1");
                    Dom.addClass(me.id + "-controlledMeErrands-label", "hidden1");
                    Dom.addClass(me.id + "-otherErrands-label", "hidden1");

                    Alfresco.util.Ajax.request({
                        url: Alfresco.constants.PROXY_URI + "/lecm/errands/api/documentErrandsFilteredList",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            filter: this.options.filter
                        },
                        successCallback: {
                            fn: function (response) {

                                var k = 0;
                                if (response.json.meErrands.length > 0) {
                                    k++;
                                }
                                if (response.json.issuedMeErrands.length > 0) {
                                    k++;
                                }
                                if (response.json.controlledMeErrands.length > 0) {
                                    k++;
                                }
                                if (response.json.otherErrands.length > 0) {
                                    k++;
                                }
                                if (k > 1) {
                                    Dom.removeClass(me.id + "-meErrands-label", "hidden1");
                                    Dom.removeClass(me.id + "-issuedByMeErrands-label", "hidden1");
                                    Dom.removeClass(me.id + "-controlledMeErrands-label", "hidden1");
                                    Dom.removeClass(me.id + "-otherErrands-label", "hidden1");
                                }

                                me.showErrands(response.json.meErrands, Dom.get(me.id + "-meErrands"));
                                me.showErrands(response.json.issuedMeErrands, Dom.get(me.id + "-issuedByMeErrands"));
                                me.showErrands(response.json.controlledMeErrands, Dom.get(me.id + "-controlledMeErrands"));
                                me.showErrands(response.json.otherErrands, Dom.get(me.id + "-otherErrands"));
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });
                },

                showErrands: function(errands, container) {
                    container.innerHTML = "";
                    var detail = "";
                    if (errands.length > 0) {
                        Dom.removeClass(container.id + "-parent", "hidden1");

                        var template = "view-metadata?nodeRef={nodeRef}";
                        for (var i = 0; i < errands.length; i++) {
                            var errand = errands[i];
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
                            // Generate the link
                            var url = "";

                            detail = "<div class=\"workflow-task-item\">";
                        detail +=   "<div class=\"workflow-task-list-picture " + isImportant + "\" title=\"" + isImportantTitle + "\">&nbsp;</div>";
                        detail +=   "<div style=\"float: left;\">";
                        detail +=       "<div>";
                        detail +=           "<div class=\"workflow-task-title workflow-task-list-left-column\" style=\"font-size: 16px;\">";
                        detail +=           "<a href=\""+window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT+"document?nodeRef="+ errand.nodeRef +"\">" + errand.title + ":</a>";
                        detail +=           "</div>";
                        detail +=           "<span class=\"workflow-task-status "+ statusClass +"\">" + status + "</span>";
                        detail +=       "</div>";
                        detail +=       "<div style=\"clear: both;\"></div>";
                        detail +=       "<div class=\"workflow-task-description\">" + errand.description + "</div>";
                        detail +=       "<div>";
                        detail +=           "<div class=\"workflow-task-list-left-column\">";
                        detail +=               "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.duedate") + ": </span>" + errand.dueDate;
                        detail +=           "</div>";
                        detail +=           "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.status") + ": </span>" + errand.statusMessage+"<br/>";
                            url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + template,
                                    {
                                        nodeRef: errand.executor
                                    });
                        detail +=           "<div class=\"workflow-task-list-left-column\">";
                        detail +=               "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.executor") + ": </span>" + '<a href="' + url + '">' + errand.executorName + '</a>';
                        detail +=           "</div>";
                            url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + template,
                                    {
                                        nodeRef: errand.initiator
                                    });
                        detail +=           "<span class=\"workflow-task-list-label\">" + this.msg("errandslist.label.initiator") + ": </span>" +  '<a href="' + url + '">' + errand.initiatorName + '</a>';

                        detail +=       "</div>";
                        detail +=   "</div>";
                        detail +=   "<div style=\"clear: both;\"></div>";
                            detail += "</div>";
                            container.innerHTML += detail;
                        }
                    } else {
                        detail = "<div class=\"workflow-task-line\">" + this.msg("errandslist.label.no-errands") + "</div>"
                        container.innerHTML += detail
                    }
                    if (this.options.anchorId != "") {
                        Dom.get(this.options.anchorId).scrollIntoView();
                    }
                }

            });
})();