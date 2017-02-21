if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.ExecutionStateControl = function (htmlId) {
        LogicECM.module.ExecutionStateControl.superclass.constructor.call(this, "LogicECM.module.ExecutionStateControl", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.ExecutionStateControl, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                formId: null,
                fieldId: null,
                value: null
            },

            onReady: function () {
                if (this.options.value) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                        dataObj: {
                            nodeRef: this.options.documentNodeRef,
                            substituteString: "{lecm-eds-aspect:execution-state}"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response && response.json.formatString) {
                                    Dom.get(this.id + "-displayValue").innerHTML = response.json.formatString;
                                    this.loadStatistics();
                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
                }
            },
            loadStatistics: function () {
                var executionStatistics = null;
                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: this.options.documentNodeRef,
                        substituteString: "{lecm-eds-aspect:execution-statistics}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response && response.json.formatString) {
                                executionStatistics = JSON.parse(response.json.formatString);
                                if (executionStatistics) {
                                    this.drawStats(executionStatistics);
                                }
                            }
                        },
                        scope: this
                    },
                    failureMessage: Alfresco.util.message("message.details.failure"),
                    scope: this
                });
            },
            drawStats: function (statistics) {
                var ul = document.createElement("ul");
                var temp = {};
                Object.keys(statistics).forEach(function (key) {
                    if (statistics[key] > 0) {
                        var html = key + ": " + statistics[key];
                        if (key == "Ожидает исполнения") {
                            temp["1"] = html;
                        }
                        if (key == "На исполнении") {
                            temp["2"] = html;
                        }
                        if (key == "На доработке") {
                            temp["3"] = html;
                        }
                        if (key == "На контроле") {
                            temp["4"] = html;
                        }
                        if (key == "Исполнено") {
                            temp["5"] = html;
                        }
                        if (key == "Отменено") {
                            temp["6"] = html;
                        }
                    }
                });
                Object.keys(temp).sort(function (a, b) {
                    return parseInt(a) > parseInt(b);
                }).forEach(function (key) {
                    var li = document.createElement("li");
                    li.innerHTML = temp[key];
                    ul.appendChild(li);
                });

                var statisticsBlock = Dom.get(this.id + "-statistics");
                statisticsBlock.append(ul);
                var iconSpan = Dom.get(this.id + "-displayValue").parentElement;
                Event.addListener(this.id + "-displayValue", "click", function () {
                    if (statisticsBlock.classList.contains("hidden1")) {
                        Dom.removeClass(statisticsBlock, "hidden1");
                        Dom.removeClass(iconSpan, "collapsed");
                        Dom.addClass(iconSpan, "expanded");

                    } else {
                        Dom.addClass(statisticsBlock, "hidden1");
                        Dom.removeClass(iconSpan, "expanded");
                        Dom.addClass(iconSpan, "collapsed");
                    }
                });
            }
        });
})();


