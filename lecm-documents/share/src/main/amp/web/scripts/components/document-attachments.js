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

	    YAHOO.Bubbling.on("metadataRefresh", this.onAttachmentsUpdate, this);
	    YAHOO.Bubbling.on("fileRenamed", this.onAttachmentsUpdate, this);
	    YAHOO.Bubbling.on("fileDeleted", this.onAttachmentsUpdate, this);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachments, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachments.prototype,
        {
	        newId: null,
            options: {
				baseDocAssocName: null,
		        showAfterReady: false
	        },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachments_onReady() {
                var id = this.newId ? this.newId : this.id;

                //Делегируем все нажатия на элементы с классом attachments-expand
                YAHOO.util.Event.delegate('Share', 'click', this.onExpand, '.attachments-expand', this, true);

                Alfresco.util.createTwister(id  + "-heading", "DocumentAttachments");

	            if (this.options.showAfterReady) {
		            this.onExpand();
	            }
            },

	        onExpand: function DocumentAttachments_onLinkClick() {
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
            },

	        onAttachmentsUpdate: function DocumentAttachments_onAttachmentsUpdate(layer, args) {
                var newId = Alfresco.util.generateDomId();
		        Alfresco.util.Ajax.request(
			        {
				        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/document-attachments",
				        dataObj: {
					        nodeRef: this.options.nodeRef,
					        htmlid: newId,
							baseDocAssocName: this.options.baseDocAssocName
				        },
				        successCallback: {
					        fn:function(response){
						        var container = Dom.get(this.id);
						        if (container != null) {
							    	container.innerHTML = response.serverResponse.responseText;
                                }
                                this.newId = newId;
                                this.onReady();
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
