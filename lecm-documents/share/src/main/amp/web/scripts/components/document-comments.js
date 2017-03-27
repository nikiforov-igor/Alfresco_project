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
 * DocumentComments
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentComments
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentComments constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentComments} The new DocumentComments instance
     * @constructor
     */
    LogicECM.DocumentComments = function DocumentComments_constructor(htmlId) {
        LogicECM.DocumentComments.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentComments, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentComments.prototype,
        {

            options: {
                nodeRef: null,
                site: null,
                activityType: null,
                language: null,
                hasViewCommentPerm: null
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentComments_onReady() {
                var expandButton = Dom.getElementsByClassName('comments-expand');
                Event.addListener(expandButton, 'click', this.onExpand, this, true);

                var lastCustomPanelViewTitle = this.getLastCustomPanelView();
                if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
                    this.onExpand();
                }
            },

            onExpand: function DocumentComments_onExpand() {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/comments/list",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            activityType: this.options.activityType,
                            htmlid: this.id + Alfresco.util.generateDomId()
                        },
                        successCallback: {
                            fn: function (response) {
                                var text = response.serverResponse.responseText;
                                this.expandView(text);
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });
            },
        }, true);
})();
