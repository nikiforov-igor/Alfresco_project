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
		this.options.excludeType = null;
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
				var expandButton = Dom.getElementsByClassName('connections-expand');
				Event.addListener(expandButton, 'click', this.onExpand, this, true);

                LogicECM.services = LogicECM.services || {};
				if (LogicECM.services.DocumentViewPreferences) {
					var lastCustomPanelViewTitle = this.getLastCustomPanelView();
					if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
						this.onExpand();
					}
				}
            },

            onExpand: function () {
	            // Load the form
	            Alfresco.util.Ajax.request(
		            {
			            url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/connections-list",
			            dataObj: {
				            nodeRef: this.options.nodeRef,
				            htmlid: this.id + Alfresco.util.generateDomId(),
							excludeType: this.options.excludeType
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

	        onConnectionsUpdate: function (layer, args) {
		        Alfresco.util.Ajax.request(
			        {
				        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/connections",
				        dataObj: {
					        nodeRef: this.options.nodeRef,
					        htmlid: this.id + "-" + Alfresco.util.generateDomId(),
							excludeType: this.options.excludeType
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
