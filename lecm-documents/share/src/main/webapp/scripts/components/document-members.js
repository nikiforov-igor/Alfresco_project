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
        YAHOO.Bubbling.on("dataItemsDeleted", this.onRefresh, this);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentMembers, LogicECM.DocumentComponentBase);

    YAHOO.lang.augmentObject(LogicECM.DocumentMembers.prototype,
        {
            onReady: function DocumentHistory_onReady() {
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
                // Обновляем форму и раскрываем в "большой области"
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/members-list",
                        dataObj: {
                            htmlid: this.id + Alfresco.util.generateDomId(),
                            /*itemKind: "node",*/
                            nodeRef: this.options.nodeRef
                            /*formId: "members",
                            mode: "view"*/
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
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/document-members",
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
                        failureMessage: "message.failure",
                        scope: this,
                        execScripts: true
                    });
            }
        }, true);
})();
