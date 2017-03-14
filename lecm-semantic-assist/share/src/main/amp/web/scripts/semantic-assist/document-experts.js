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
 * @class LogicECM.DocumentExperts
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
     * @return {LogicECM.DocumentExperts} The new DocumentHistory instance
     * @constructor
     */
    LogicECM.DocumentExperts = function DocumentExperts_constructor(htmlId) {
        LogicECM.DocumentExperts.superclass.constructor.call(this, htmlId);

        return this;
    };

    YAHOO.extend(LogicECM.DocumentExperts, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentExperts.prototype,
        {
            newId: null,

            onReady: function DocumentHistory_onReady() {
                var id = this.newId ? this.newId : this.id;

                var expandEl = Dom.get("experts-by-document-ref");
                if (expandEl != null) {
                    expandEl.onclick = this.onExpand.bind(this);
                }
            },

            onExpand: function () {
                // Обновляем форму и раскрываем в "большой области"
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/br5/semantic/experts/lecm-experts-by-document",
                        dataObj: {
                            htmlid: this.id + Alfresco.util.generateDomId(),
                            nodeRef: this.options.nodeRef
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
