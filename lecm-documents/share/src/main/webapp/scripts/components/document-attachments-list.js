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
                nodeRef: null,

                categories: null
            },

            fileUpload: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsList_onReady() {
                if (this.options.categories != null) {
                    for (var i = 0; i < this.options.categories.length; i++) {
                        var category = this.options.categories[i];
                        var button = Alfresco.util.createYUIButton(this, category + "-fileUpload-button", this.onFileUpload,
                            {
                                disabled: false,
                                value: "CreateChildren"
                            });
                        button.categoryNodeRef = category;
                    }
                }
            },

            onFileUpload: function DocumentAttachmentsList_onFileUpload(e, obj)
            {
                if (this.fileUpload == null)
                {
                    this.fileUpload = Alfresco.getFileUploadInstance();
                }

                var multiUploadConfig =
                {
                    destination: obj.categoryNodeRef,
                    filter: [],
                    mode: this.fileUpload.MODE_MULTI_UPLOAD,
                    thumbnails: "doclib",
                    onFileUploadComplete:
                    {
                        fn: this.onFileUploadComplete,
                        scope: this
                    }
                };
                this.fileUpload.show(multiUploadConfig);
                Event.preventDefault(e);
            },

            onFileUploadComplete: function DocumentAttachmentsList_onFileUploadComplete(complete) {
            }
        }, true);
})();
