if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.StateStatisticsControl = function (htmlId) {
        LogicECM.module.StateStatisticsControl.superclass.constructor.call(this, "LogicECM.module.StateStatisticsControl", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.StateStatisticsControl, Alfresco.component.Base,
        {
            statisticsLoaded: false,

            options: {
                documentNodeRef: null,
                formId: null,
                fieldId: null,
                value: null,
                expandable: true,
                showEmptyStatuses: false,
                statusesOrder: null,
                statisticsField: ""
            },

            onReady: function () {
                if (this.options.value) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                        dataObj: {
                            nodeRef: this.options.documentNodeRef,
                            substituteString: "{" + this.options.fieldId + "}"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response && response.json.formatString) {
                                    Dom.get(this.id + "-displayValue").innerHTML = response.json.formatString;
                                    if (this.options.expandable) {
                                        Event.addListener(this.id + "-displayValue", "click", function () {
                                            var statisticsBlock = Dom.get(this.id + "-statistics");
                                            var iconSpan = Dom.get(this.id + "-displayValue").parentElement;
                                            if (statisticsBlock.classList.contains("hidden1")) {
                                                if (!this.statisticsLoaded && this.options.statisticsField) {
                                                    this.loadState();
                                                }
                                                Dom.removeClass(statisticsBlock, "hidden1");
                                                Dom.removeClass(iconSpan, "collapsed");
                                                Dom.addClass(iconSpan, "expanded");
                                            } else {
                                                Dom.addClass(statisticsBlock, "hidden1");
                                                Dom.removeClass(iconSpan, "expanded");
                                                Dom.addClass(iconSpan, "collapsed");
                                            }
                                        }, this, true);
                                    }
                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
                }
            },

            loadState: function () {
                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: this.options.documentNodeRef,
                        substituteString: "{" + this.options.statisticsField + "}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response && response.json.formatString) {
                                var statistics = JSON.parse(response.json.formatString);
                                if (statistics) {
                                    this.statisticsLoaded = true;
                                    this.drawState(statistics);
                                }
                            }
                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });
            },

            drawState: function (statistics) {
                var ul = document.createElement("ul");
                var order = this.options.statusesWithOrder;

                var i, status;
                if (order && order instanceof Array && order.length) {
                    for (i = 0; i < order.length; i++) {
                        for (var j = 0; j < statistics.length; j++) {
                            status = statistics[j].state;
                            if (order[i] == status) {
                                if (statistics[j].count || this.options.showEmptyStatuses) {
                                    ul.appendChild(this.getStatisticsItemView(statistics[j]));
                                }
                                break;
                            }
                        }
                    }
                } else {
                    for (i = 0; i < statistics.length; i++) {
                        if (statistics[i].count || this.options.showEmptyStatuses) {
                            ul.appendChild(this.getStatisticsItemView(statistics[i]));
                        }
                    }
                }
                var statisticsBlock = Dom.get(this.id + "-statistics");
                statisticsBlock.appendChild(ul);
            },

            getStatisticsItemView: function (statistic) {
                var li = document.createElement("li");
                li.innerHTML = statistic.state + ": " + statistic.count;
                return li;
            }
        });
})();

