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
        LogicECM.DocumentAttachmentsList.superclass.constructor.call(this, "LogicECM.DocumentAttachmentsList", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachmentsList, Alfresco.component.Base);

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsList.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                /**
                 * The nodeRefs to load the form for.
                 *
                 * @property nodeRef
                 * @type string
                 * @required
                 */
                nodeRef: null
            },

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsList_onReady() {
                this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
                    {
                        disabled: false,
                        value: "CreateChildren"
                    });
            },

            onFileUpload: function DocumentAttachmentsList_onFileUpload(e, p_obj)
            {
                var fileUpload = Alfresco.getFileUploadInstance();

                var multiUploadConfig =
                {
                    destination: this.options.nodeRef,
                    filter: [],
                    mode: fileUpload.MODE_MULTI_UPLOAD,
                    thumbnails: "doclib",
                    onFileUploadComplete:
                    {
                        fn: this.onFileUploadComplete,
                        scope: this
                    }
                };
                fileUpload.show(multiUploadConfig);
            },

            onFileUploadComplete: function DocumentAttachmentsList_onFileUploadComplete(complete) {
                alert("complete upload");
            }
        }, true);
})();
