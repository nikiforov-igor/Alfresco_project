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

				var expandButton = Dom.getElementsByClassName('agenda-expand');
				Event.addListener(expandButton, 'click', this.onExpand, this, true);

                Alfresco.util.createTwister(id  + "-heading", "MeetingAgenda");

				var lastCustomPanelViewTitle = this.getLastCustomPanelView();
				if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
					this.onExpand();
				}

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
                            htmlid: (this.id + Alfresco.util.generateDomId()).replace(/\//g,"_")
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
		        Alfresco.util.Ajax.jsonGet(
			        {
				        url: Alfresco.constants.PROXY_URI + "/lecm/meetings/getAgendaInfo",
				        dataObj: {
					        nodeRef: this.options.nodeRef
				        },
				        successCallback: {
					        fn:function(response){
						        var container = Dom.get(this.id+"-formContainer");
						        if (container != null) {
							    	container.innerHTML = "";
									var json = response.json;
									if (json && json.size!=null) {
										var sizeDiv =  document.createElement('div');
										var span = document.createElement('span');
										sizeDiv.appendChild(span);
										container.appendChild(sizeDiv);
										span.innerHTML = Alfresco.util.message('title.agenda_size', this.name)+": " + json.size;
										
										var statusDiv =  document.createElement('div');
										span = document.createElement('span');
										statusDiv.appendChild(span);
										container.appendChild(statusDiv);
										span.innerHTML = Alfresco.util.message("agenda_status."+json.status, this.name);
										
									} else {
										var div = document.createElement('div');
										div.className = "block-empty-body";
										var span = document.createElement('span');
										span.className = "block-empty faded";
										div.appendChild(span);
										container.appendChild(div);
										span.innerHTML = Alfresco.util.message("message.block.empty");
									}
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
