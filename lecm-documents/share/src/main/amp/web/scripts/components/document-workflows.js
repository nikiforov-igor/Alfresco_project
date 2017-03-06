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
 * @class LogicECM.DocumentWorkflows
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
     * @return {LogicECM.DocumentWorkflows} The new DocumentWorkflows instance
     * @constructor
     */
    LogicECM.DocumentWorkflows = function DocumentWorkflows_constructor(htmlId) {
        LogicECM.DocumentWorkflows.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentWorkflows, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentWorkflows.prototype,
        {
            onReady: function DocumentWorkflows_onReady() {
                var expandButton = Dom.getElementsByClassName('workflows-expand');
                Event.addListener(expandButton, 'click', this.onExpand, this, true);

                LogicECM.services = LogicECM.services || {};
                if(LogicECM.services.documentViewPreferences) {
                    var lastCustomPanelViewTitle = this.getLastCustomPanelView();
                    if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
                        this.onExpand();
                    }
                }
            },

            onExpand: function () {
                Alfresco.util.Ajax.request({
                    url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/workflows",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        htmlid: this.id + Alfresco.util.generateDomId()
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
