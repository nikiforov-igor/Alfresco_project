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
 * DocumentTags
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentTags
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentTags constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentTags} The new DocumentTags instance
     * @constructor
     */
    LogicECM.DocumentTags = function DocumentTags_constructor(htmlId) {
        LogicECM.DocumentTags.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentTags, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentTags.prototype,
        {
	        newId: null,
            options: {
		        showAfterReady: false
	        },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentTags_onReady() {


				var semanticPanelEl = Dom.get("cloud-term-ref");
				if (semanticPanelEl != null) {
					semanticPanelEl.onclick = this.onExpand.bind(this);
	            }

	            if (this.options.showAfterReady) {
		            this.onExpand();
	            }
            },

	        onExpand: function DocumentTags_onLinkClick() {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/br5/semantic/cloud/lecm-document-tags-cloud",
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
