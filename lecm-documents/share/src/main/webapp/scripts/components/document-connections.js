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
    LogicECM.DocumentConnections = function DocumentConnections_constructor(htmlId) {
        LogicECM.DocumentConnections.superclass.constructor.call(this, "LogicECM.DocumentConnections", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentConnections, Alfresco.component.Base,
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
            onReady: function DocumentConnections_onReady() {
                var linkEl = Dom.get(this.id + "-link");
                linkEl.onclick = this.onLinkClick.bind(this);
            },

            onLinkClick: function DocumentConnections_onLinkClick() {
                Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid:"document-connections-" + this.options.nodeRef,
                            itemKind:"node",
                            itemId:this.options.nodeRef,
                            formId:"connections",
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
                                var container = Dom.get("custom-region");
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;

                                    Dom.setStyle("main-content-region", "display", "none");
                                    Dom.setStyle("custom-region", "display", "block");
                                }
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true
                    });
            }
        });
})();
