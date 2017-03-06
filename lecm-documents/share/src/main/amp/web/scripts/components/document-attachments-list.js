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
 * DocumentAttachmentsList
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentsList
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * DocumentAttachmentsList constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentAttachmentsList} The new DocumentAttachmentsList instance
     * @constructor
     */
    LogicECM.DocumentAttachmentsList = function DocumentAttachmentsList_constructor(htmlId) {
        LogicECM.DocumentAttachmentsList.superclass.constructor.call(this, "DocumentAttachmentsList", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachmentsList,  Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsList.prototype,
        {

            options: {
                nodeRef: null,
                inclBaseDoc: false
            },
            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsList_onReady() {
                var showInPreviewButton = Dom.get(this.id + "-action-show-previewer");
                Event.addListener(showInPreviewButton, 'click', this.onShowAsListPressed, this, true);
            },

            onShowInPreviewPressed: function DocumentAttachmentsList_onShowInPreviewPressed() {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/attachments/preview",
                        dataObj: {
                            nodeRef: this.options.nodeRef,
                            htmlid: this.id + Alfresco.util.generateDomId(),
                            inclBaseDoc: this.options.inclBaseDoc
                        },
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
                                    LogicECM.services.documentViewPreferences.setIsDocAttachmentsInPreview(true);
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
