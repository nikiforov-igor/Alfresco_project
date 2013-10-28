if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Dashlet = LogicECM.module.Dashlet|| {};

(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.Dashlet.Tasks = function DashletTasks_constructor(htmlId) {
        LogicECM.module.Dashlet.Tasks.superclass.constructor.call(this, "LogicECM.module.Dashlet.Tasks", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Dashlet.Tasks, Alfresco.component.Base,
        {
            options: {
                nodeRef: null,
                containerId: ""
            },

            dashletBodyContainer: null,

            onReady: function () {
                this.dashletBodyContainer = Dom.get(this.options.containerId);

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/dashlets/document-my-tasks/content",
                    dataObj: {
                        nodeRef: this.options.nodeRef
                    },
                    successCallback: {
                        fn: function (response) {
                            this.dashletBodyContainer.innerHTML = response.serverResponse.responseText;
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            }

        });
})();