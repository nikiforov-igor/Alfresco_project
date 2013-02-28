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
        LogicECM.DocumentHistory.superclass.constructor.call(this, "LogicECM.DocumentHistory", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentHistory, Alfresco.component.Base,
        {

            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                /**
                 * The nodeRefs to load the form for.
                 *
                 * @property nodeRef
                 * @type string
                 * @required
                 */
                nodeRef: null
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentHistory_onReady() {
                var linkEl = Dom.get(this.id + "-link");
                linkEl.onclick = this.onLinkClick.bind(this);
            },

            onLinkClick: function DocumentHistory_onLinkClick() {

                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/document/history",
                        dataObj: {
                            nodeRef: this.options.nodeRef
                        },
                        successCallback: {
                            fn: this.onHistoryLoaded,
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });

                Dom.setStyle("main-region", "display", "none");
                Dom.setStyle("custom-region", "display", "block");
            },

            onHistoryLoaded: function DocumentHistory_onHistoryLoaded() {
                var formEl = Dom.get("custom-region");
                formEl.innerHTML = "История!!!!!!"; //response.serverResponse.responseText;

                Dom.setStyle("main-content-region", "display", "none");
                Dom.setStyle("custom-region", "display", "block");
            }
        });
})();
