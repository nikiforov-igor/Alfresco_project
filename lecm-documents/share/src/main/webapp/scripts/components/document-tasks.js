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

            onExpand: function (anchor) {
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

                            this.loadTasks(anchor);

                            this.loadMyErrands(anchor);

                            this.loadErrandsIssuedByMe(anchor);
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadTasks: function (anchor) {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.htmlid,
                        tasksState: this.tasksState,
                        isAnchor: (anchor !=null && anchor == "tasksList") ? "true" : "false"
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

            loadMyErrands: function (anchor) {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/my-errands",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        errandsUrl: "/lecm/errands/api/documentMyErrands",
                        createButton: false,
                        htmlid: this.htmlid+"_myErrandsList",
                        label: "my-errands",
                        isAnchor: (anchor !=null && anchor == "myErrandsList") ? "true" : "false"


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

            loadErrandsIssuedByMe: function (anchor) {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/my-errands",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        errandsUrl: "/lecm/errands/api/documentErrandsIssuedByMe",
                        createButton: true,
                        htmlid: this.htmlid+"_errandsIssuedByMeList",
                        label: "issued-by-me",
                        isAnchor: (anchor !=null && anchor == "errandsIssuedByMeList") ? "true" : "false"
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
