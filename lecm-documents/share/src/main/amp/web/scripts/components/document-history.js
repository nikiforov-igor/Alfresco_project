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
 * @class LogicECM.DocumentHistory
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
     * @return {LogicECM.DocumentHistory} The new DocumentHistory instance
     * @constructor
     */
    LogicECM.DocumentHistory = function DocumentHistory_constructor(htmlId) {
        LogicECM.DocumentHistory.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentHistory, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentHistory.prototype,
        {
            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentHistory_onReady() {
                var expandButton = Dom.getElementsByClassName('history-expand');
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
                // Load the datagrid
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/history-datagrid",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            htmlid: this.id + Alfresco.util.generateDomId(),
                            showSecondaryCheckBox: true,
                            hideCollapseButton: false,
                            dataSource: "lecm/business-journal/ds/history"
                        },
                        successCallback: {
                            fn:function(response){
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
