if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;

    /**
     * MeetingAgenda constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.MeetingAgenda} The new MeetingAgenda instance
     * @constructor
     */
    LogicECM.MeetingAgenda = function MeetingAgenda_constructor(htmlId) {
        LogicECM.MeetingAgenda.superclass.constructor.call(this, htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.MeetingAgenda, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.MeetingAgenda.prototype,
        {
            options: {
		        nodeRef: null,
                componentHtmlId: null
	        },

            onReady: function () {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/meetings/agenda-list",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            htmlid: this.id + "-" + Alfresco.util.generateDomId()
                        },
                        successCallback: {
                            fn:function(response){
                                var html = response.serverResponse.responseText;
                                var formEl = Dom.get(this.id);
                                if (formEl) {
                                    formEl.innerHTML = "";
                                    formEl.innerHTML = html;
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
