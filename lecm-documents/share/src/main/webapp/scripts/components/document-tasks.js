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
            htmlid: null,
            tasksState: "active",
            myErrandsState: "active",
            errandsIssuedByMeState: "active",

            onReady: function DocumentTasks_onReady() {
                var linkEl = Dom.get(this.id + "-action-expand");
                linkEl.onclick = this.onExpand.bind(this);
            },

            setTasksState: function(value) {
                this.tasksState = value;
            },

            setMyErrandsState: function(value) {
                this.myErrandsState = value;
            },

            setErrandsIssuedByMeState: function(value) {
                this.errandsIssuedByMeState = value;
            },

            onExpand: function () {
                this.htmlid = this.id + Alfresco.util.generateDomId();

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks-errands",
                    dataObj: {
                        htmlid: this.htmlid
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            this.expandView(text);

                            this.loadTasks();

                            this.loadMyErrands();

                            this.loadErrandsIssuedByMe();
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadTasks: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.htmlid,
                        tasksState: this.tasksState
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            var listContainer = Dom.get(this.htmlid + "_tasksList");
                            listContainer.innerHTML = text;
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadMyErrands: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/my-errands",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        errandsUrl: "/lecm/errands/api/documentMyErrands",
                        createButton: false,
                        htmlid: this.htmlid,
                        label: "my.errands"
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            var listContainer = Dom.get(this.htmlid + "_myErrandsList");
                            listContainer.innerHTML = text;
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadErrandsIssuedByMe: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/my-errands",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        errandsUrl: "/lecm/errands/api/documentErrandsIssuedByMe",
                        createButton: true,
                        htmlid: this.htmlid+"issued-by-me",
                        label: "issued.by.me"
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            var listContainer = Dom.get(this.htmlid + "_errandsIssuedByMeList");
                            listContainer.innerHTML = text;
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
