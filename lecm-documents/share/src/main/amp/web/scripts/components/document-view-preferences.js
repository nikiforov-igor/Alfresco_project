/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */

if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.services = LogicECM.services || {};

(function () {
    LogicECM.module.DocumentViewPreferences = function (nodeRef, docType, defaultExpandComponent) {
        LogicECM.module.DocumentViewPreferences.superclass.constructor.call(this);
        this.options.nodeRef = nodeRef;
        this.options.docType = docType;
        this.options.defaultExpandComponent = defaultExpandComponent;

        this.preferencesService = new Alfresco.service.Preferences();
        return this;
    };

    YAHOO.extend(LogicECM.module.DocumentViewPreferences, Alfresco.service.BaseService,
        {
            DOCUMENT_VIEW_PREFERENCES: {
                /* Двухпанельный вид */
                SPLIT_PANEL: "SHOW_CUSTOM_PANEL_SPLITED",
                /* Последняя раскрытая панель */
                LAST_CUSTOM_PANEL: "LAST_CUSTOM_PANEL_VIEW",
                /* Сворачивание правой части */
                SHOW_RIGHT_PART_SHORT: "SHOW_RIGHT_PART_SHORT",
                /* Показывать вложения в превьювере */
                IS_DOCUMENT_ATTACHMENTS_IN_PREVIEW: "IS_DOCUMENT_ATTACHMENTS_IN_PREVIEW"
            },

            preferences: {
                splitPanel: null,
                lastCustomPanel: null,
                showRightPartShort: null,
                isDocumentAttachmentsInPreview: null
            },

            options: {
                nodeRef: null,
                docType: null,
                defaultExpandComponent: null
            },

            preferencesService: null,

            getPanelSplited: function () {
                if (this.preferences.splitPanel == null) {
                    this.preferences.splitPanel = Alfresco.util.findValueByDotNotation(this.preferencesService.get(), this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.SPLIT_PANEL), false);
                }

                return this.preferences.splitPanel;
            },

            setPanelSplited: function (isPanelSplit) {
                this.preferencesService.set(this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.SPLIT_PANEL), isPanelSplit);
                this.preferences.splitPanel = isPanelSplit;
                YAHOO.Bubbling.fire("panelSplitedChanged", {
                    panelSplit: this.preferences.splitPanel
                });
            },

            getLastCustomPanelView: function () {
                if (this.preferences.lastCustomPanel == null) {
                    this.preferences.lastCustomPanel = Alfresco.util.findValueByDotNotation(this.preferencesService.get(), this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.LAST_CUSTOM_PANEL), "");
                    if (this.preferences.lastCustomPanel == null) {
                        this.preferences.lastCustomPanel = this.options.defaultExpandComponent;
                    }
                }
                return this.preferences.lastCustomPanel;
            },

            setLastCustomPanelView: function (panelName) {
                this.preferencesService.set(this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.LAST_CUSTOM_PANEL), panelName);
                this.preferences.lastCustomPanel = panelName;
            },

            getShowRightPartShort: function () {
                if (this.preferences.showRightPartShort == null) {
                    this.preferences.showRightPartShort = Alfresco.util.findValueByDotNotation(this.preferencesService.get(), this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.SHOW_RIGHT_PART_SHORT), false);
                }

                return this.preferences.showRightPartShort;
            },

            setShowRightPartShort: function (showRightPartShort) {
                this.preferencesService.set(this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.SHOW_RIGHT_PART_SHORT), showRightPartShort);
                this.preferences.showRightPartShort = showRightPartShort;
                YAHOO.Bubbling.fire("showRightPartShortChanged", {
                    showRightPartShort: this.preferences.showRightPartShort
                });
            },

            getIsDocAttachmentsInPreview: function () {
                if (this.preferences.isDocumentAttachmentsInPreview == null) {
                    this.preferences.isDocumentAttachmentsInPreview = Alfresco.util.findValueByDotNotation(this.preferencesService.get(), this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.IS_DOCUMENT_ATTACHMENTS_IN_PREVIEW), false);
                }

                return this.preferences.isDocumentAttachmentsInPreview;
            },

            setIsDocAttachmentsInPreview: function (isDocAttachmentsInPreview) {
                this.preferencesService.set(this._getPreferenceKey(this.DOCUMENT_VIEW_PREFERENCES.IS_DOCUMENT_ATTACHMENTS_IN_PREVIEW), isDocAttachmentsInPreview);
                this.preferences.isDocumentAttachmentsInPreview = isDocAttachmentsInPreview;
                YAHOO.Bubbling.fire("isDocAttachmentsInPreviewChanged", {
                    isDocumentAttachmentsInPreview: this.isDocumentAttachmentsInPreview
                });
            },

            _getDocType: function() {
                return this.options.docType.replace(':', '_');
            },

            _getPreferenceKey: function(key) {
                return "Alfresco.service.Preferences." + this._getDocType() + "." + key;
            }
        }
    );
})();