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
 * DocumentAttachmentsPreview
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentsPreview
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentAttachmentsPreview constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentAttachmentsPreview} The new DocumentAttachmentsPreview instance
     * @constructor
     */
    LogicECM.DocumentAttachmentsPreview = function DocumentAttachmentsPreview_constructor(htmlId) {
        LogicECM.DocumentAttachmentsPreview.superclass.constructor.call(this, "DocumentAttachmentsPreview", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachmentsPreview, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsPreview.prototype,
        {

            options: {
                nodeRef: null,
                baseDocAssocName: null,
                showBaseDocAttachmentsBottom: false
            },
            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsPreview_onReady() {
                var showListButton = Dom.get(this.id + "-show-list");
                Event.addListener(showListButton, 'click', this.onShowListPressed, this, true);
            },

            onShowListPressed: function DocumentAttachmentsPreview_onShowListPressed() {
                var dataObj = {
                    nodeRef: this.options.nodeRef,
                    htmlid: this.id + Alfresco.util.generateDomId(),
                };
                if (this.options.baseDocAssocName) {
                    dataObj.baseDocAssocName = this.options.baseDocAssocName;
                    dataObj.showBaseDocAttachmentsBottom = this.options.showBaseDocAttachmentsBottom;
                }
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/attachments-list",
                        dataObj: dataObj,
                        successCallback: {
                            fn: function (response) {
                                var html = response.serverResponse.responseText;
                                var formEl = Dom.get("custom-region");
                                if (formEl != null) {
                                    formEl.innerHTML = "";
                                    formEl.innerHTML = html;
                                }
                                LogicECM.services = LogicECM.services || {};
                                if (LogicECM.services.documentViewPreferences) {
                                    LogicECM.services.documentViewPreferences.setIsDocAttachmentsInPreview(false);
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