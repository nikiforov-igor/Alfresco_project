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
 * DocumentDocuments
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentDocuments
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentDocuments constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentDocuments} The new DocumentDocuments instance
     * @constructor
     */
    LogicECM.DocumentDocuments = function DocumentDocuments_constructor(htmlId) {
        LogicECM.DocumentDocuments.superclass.constructor.call(this, htmlId);

        return this;
    };

    YAHOO.extend(LogicECM.DocumentDocuments, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentDocuments.prototype,
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
            onReady: function DocumentDocuments_onReady() {
                var id = this.newId ? this.newId : this.id;

	            var expandEl = Dom.get("documents-by-document-ref");
	            if (expandEl != null) {
		            expandEl.onclick = this.onExpand.bind(this);
	            }

	            if (this.options.showAfterReady) {
		            this.onExpand();
	            } 
            },

	        onExpand: function DocumentDocuments_onLinkClick() {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/br5/semantic/documents/lecm-documents-by-document",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
							type: "lecm",
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