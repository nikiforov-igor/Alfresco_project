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
 * @class LogicECM.DocumentMembers
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
     * @return {LogicECM.DocumentMembers} The new DocumentHistory instance
     * @constructor
     */
    LogicECM.DocumentMembers = function DocumentMembers_constructor(htmlId) {
        LogicECM.DocumentMembers.superclass.constructor.call(this, htmlId);

        YAHOO.Bubbling.on("memberCreated", this.onRefresh, this);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentMembers, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentMembers.prototype,
        {
            newId: null,

            onReady: function DocumentHistory_onReady() {
                var id = this.newId ? this.newId : this.id;

                var expandButton = Dom.getElementsByClassName('members-expand');
                Event.addListener(expandButton, 'click', this.onExpand, this, true);

                Alfresco.util.createTwister(id + "-heading", "DocumentMembers");

                LogicECM.services = LogicECM.services || {};
                if(LogicECM.services.DocumentViewPreferences) {
                    var lastCustomPanelViewTitle = this.getLastCustomPanelView();
                    if (lastCustomPanelViewTitle == this.getTitle() && this.isSplitPanel()) {
                        this.onExpand();
                    }
                }
            },

            onExpand: function () {
                // Обновляем форму и раскрываем в "большой области"
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/members-list",
                        dataObj: {
                            htmlid: this.id + Alfresco.util.generateDomId(),
                            nodeRef: this.options.nodeRef
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
            onRefresh: function (layer, args) {
                var newId = Alfresco.util.generateDomId();
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/document-members",
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
                        failureMessage: "message.failure",
                        scope: this,
                        execScripts: true
                    });
            }
        }, true);
})();
