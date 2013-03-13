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
 * DocumentAttachments
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachments
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentAttachments constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentAttachments} The new DocumentAttachments instance
     * @constructor
     */
    LogicECM.DocumentAttachments = function DocumentAttachments_constructor(htmlId) {
        LogicECM.DocumentAttachments.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachments, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachments.prototype,
        {
	        options: {
		        showAfterReady: false
	        },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachments_onReady() {
	            var expandEl = Dom.get(this.id + "-action-expand");
	            if (expandEl != null) {
		            expandEl.onclick = this.onLinkClick.bind(this);
	            }

	            var linkEl = Dom.get(this.id + "-link");
	            if (linkEl != null) {
		            linkEl.onclick = this.onLinkClick.bind(this);
	            }
            },

            onLinkClick: function DocumentAttachments_onLinkClick() {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/attachments-list",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            htmlid: this.id + Alfresco.util.generateDomId()
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
