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
 * MeetingAgenda
 *
 * @namespace LogicECM
 * @class LogicECM.MeetingAgenda
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * MeetingAgenda constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.MeetingAgenda} The new MeetingAgenda instance
     * @constructor
     */
    LogicECM.MeetingAgenda = function MeetingAgenda_constructor(htmlId) {
        LogicECM.MeetingAgenda.superclass.constructor.call(this, htmlId);

	    YAHOO.Bubbling.on("formValueChanged", this.onUpdate, this);
        return this;
    };

    YAHOO.extend(LogicECM.MeetingAgenda, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.MeetingAgenda.prototype,
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
            onReady: function MeetingAgenda_onReady() {
                var id = this.newId ? this.newId : this.id;

                //Делегируем все нажатия на элементы с классом attachments-expand
                YAHOO.util.Event.delegate('Share', 'click', this.onExpand, '.agenda-expand', this, true);

                Alfresco.util.createTwister(id  + "-heading", "MeetingAgenda");

	            if (this.options.showAfterReady) {
		            this.onExpand();
	            }
            },

	        onExpand: function MeetingAgenda_onLinkClick() {
                // Load the form
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/meetings/agenda-list",
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

	        onUpdate: function MeetingAgenda_onUpdate(layer, args) {
                var newId = Alfresco.util.generateDomId();
		        Alfresco.util.Ajax.request(
			        {
				        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/meetings/meeting-agenda",
				        dataObj: {
					        nodeRef: this.options.nodeRef,
					        htmlid: newId
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
