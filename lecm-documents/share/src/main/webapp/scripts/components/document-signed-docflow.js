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
    LogicECM.DocumentSignedDocflow = function DocumentSignedDocflow_constructor(htmlId) {
        LogicECM.DocumentMembers.superclass.constructor.call(this, htmlId);

        return this;
    };

    YAHOO.extend(LogicECM.DocumentSignedDocflow, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentSignedDocflow.prototype,
        {
            newId: null,

            onReady: function DocumentSignedDocflow_onReady() {
                var id = this.newId ? this.newId : this.id;

                var refreshEl = Dom.get(id + "-action-refresh");
                if (refreshEl != null) {
                    refreshEl.onclick = this.onRefresh.bind(this);
                }

                Alfresco.util.createTwister(id + "-heading", "DocumentSignedDocflow");
            },
            onRefresh: function (layer, args) {
				alert("SUDDENLY, REFRESH!");
            },
			onSignDocuments:  function (layer, args) {
				alert("onSignDocuments");
			},
			onSendDocuments: function (layer, args) {
				alert("onSendDocuments");
			},
			onViewSignatures: function (layer, args) {
				alert("onViewSignatures");
			}
        }, true);
})();
