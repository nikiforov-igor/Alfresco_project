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
        LogicECM.DocumentConnections.superclass.constructor.call(this, htmlId);

	    YAHOO.Bubbling.on("connectionsUpdate", this.onConnectionsUpdate, this);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentConnections, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentConnections.prototype,
        {
            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentConnections_onReady() {
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
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: "document-connections-" + this.options.nodeRef,
                            itemKind: "node",
                            itemId: this.options.nodeRef,
                            formId: "connections",
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
            },

	        onConnectionsUpdate: function (layer, args) {
		        Alfresco.util.Ajax.request(
			        {
				        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/connections",
				        dataObj: {
					        nodeRef: this.options.nodeRef,
					        htmlid: this.id + "-" + Alfresco.util.generateDomId()
				        },
				        successCallback: {
					        fn:function(response){
						        var container = Dom.get(this.id);
						        if (container != null) {
							        container.innerHTML = response.serverResponse.responseText;
						        }
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
