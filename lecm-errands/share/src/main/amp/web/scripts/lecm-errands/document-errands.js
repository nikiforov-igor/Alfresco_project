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
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentErrands
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    /**
     * DocumentHistory constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentErrands} The new DocumentTasks instance
     * @constructor
     */
    LogicECM.DocumentErrands = function DocumentErrands_constructor(htmlId) {
        LogicECM.DocumentErrands.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentErrands, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentErrands.prototype,
        {
            htmlid: null,
            tasksState: "active",
            myErrandsState: "active",
            errandsIssuedByMeState: "active",

            onReady: function DocumentTasks_onReady() {
                YAHOO.util.Event.delegate('Share', 'click', this.onExpand, '.errands-expand', this, true);
            },

            onExpand: function (anchor) {
                this.htmlid = this.id + Alfresco.util.generateDomId();

                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/tasks-errands",
                    dataObj: {
                        htmlid: this.htmlid,
                        nodeRef: this.options.nodeRef
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            this.expandView(text);

                            this.loadErrandsList(anchor);

                            YAHOO.util.Event.on(this.htmlid + "-errands-filter", "change", this.loadErrandsList, this, true);
                        },
                        scope: this
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            loadErrandsList: function (anchor) {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/errandsList",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.htmlid+"_errandsList",
                        isAnchor: (anchor !=null && anchor == "errandsList") ? "true" : "false",
                        filter: Dom.get(this.htmlid + "-errands-filter").value
                    },
                    successCallback: {
                        fn: function(response) {
                            var text = response.serverResponse.responseText;
                            var listContainer = Dom.get(this.htmlid + "_errandsList");
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
