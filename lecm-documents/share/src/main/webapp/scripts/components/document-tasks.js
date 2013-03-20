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
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentTasks
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentHistory constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentTasks} The new DocumentTasks instance
     * @constructor
     */
    LogicECM.DocumentTasks = function DocumentTasks_constructor(htmlId) {
        LogicECM.DocumentTasks.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentTasks, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentTasks.prototype,
        {
            tasksType: "active",

            onReady: function DocumentTasks_onReady() {
                var linkEl = Dom.get(this.id + "-action-expand");
                linkEl.onclick = this.onExpand.bind(this);
            },

            setTasksType: function(value) {
                this.tasksType = value;
            },

            onExpand: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.id + Alfresco.util.generateDomId(),
                        tasksType: this.tasksType
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            this.expandView(text);
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            }
        }, true);
})();
