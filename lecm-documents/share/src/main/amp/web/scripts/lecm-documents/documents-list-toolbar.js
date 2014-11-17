if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents|| {};

(function () {
    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.Documents.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.Documents.Toolbar = function (htmlId) {
        return LogicECM.module.Documents.Toolbar.superclass.constructor.call(this, "LogicECM.module.Documents.Toolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.Documents.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.Documents.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                bubblingLabel: null,
                destination: null,
                itemType: null,
                newRowDialogTitle: "label.create-row.title",
                searchButtonsType: 'defaultActive',
                newRowButtonType: 'defaultActive',
                createDialogWidth: "70em",
                createDialogClass: ""
            },

            doubleClickLock: false,

            _initButtons: function() {
                this.toolbarButtons[this.options.newRowButtonType].newDocumentButton = Alfresco.util.createYUIButton(this, "newDocumentButton", this.onNewRow,
                    {
                        value: "create"
                    });

                this.toolbarButtons[this.options.searchButtonsType].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
                this.toolbarButtons[this.options.searchButtonsType].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
            },

            onNewRow: function () {
	            window.location.href =
		            Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + this.options.itemType + "&" + LogicECM.module.Base.Util.encodeUrlParams("documentType=" + this.options.itemType);
            }
        }, true);
})();