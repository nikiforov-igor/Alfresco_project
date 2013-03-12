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
 * @class LogicECM.DocumentMembers
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
     * @return {LogicECM.DocumentMembers} The new DocumentHistory instance
     * @constructor
     */
    LogicECM.DocumentMembers = function DocumentMembers_constructor(htmlId) {
        LogicECM.DocumentMembers.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentMembers, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentMembers.prototype,
        {
            onReady: function DocumentHistory_onReady() {
                var expandEl = Dom.get(this.id + "-action-expand");
                if (expandEl != null) {
                    expandEl.onclick = this.onExpand.bind(this);
                }

                var linkEl = Dom.get(this.id + "-link");
                if (linkEl != null) {
                    linkEl.onclick = this.onExpand.bind(this);
                }
            },

            onExpand: function () {
                // Обновляем форму и раскрываем в "большой области"
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: "document-members-" + this.options.nodeRef,
                            itemKind: "node",
                            itemId: this.options.nodeRef,
                            formId: "members",
                            mode: "view"
                        },
                        successCallback: {
                            fn:function(response){
                                this.expandView(response.serverResponse.responseText);
                            },
                            scope: this
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
            }
        }, true);
})();
