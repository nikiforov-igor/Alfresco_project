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
    LogicECM.DocumentMain = function DocumentMain_constructor(htmlId) {
        LogicECM.DocumentMain.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentMain, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentMain.prototype,
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
            onReady: function DocumentMain_onReady() {
                var linkEl = Dom.get(this.id + "-link");
                linkEl.onclick = this.onLinkClick.bind(this);
            },

            onLinkClick: function () {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: "documentMain-" + this.options.nodeRef,
                            itemKind: "node",
                            itemId: this.options.nodeRef,
                            formId: "",
                            mode: "view"
                        },
                        successCallback: {
                            fn: this.onExtendView,
                            scope: this
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
            }
        }, true);
})();
