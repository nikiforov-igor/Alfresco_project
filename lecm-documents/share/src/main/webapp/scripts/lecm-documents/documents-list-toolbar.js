(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

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
                createDialogWidth: "70em"
            },

            _initButtons: function() {
                this.toolbarButtons[this.options.newRowButtonType].newDocumentButton = Alfresco.util.createYUIButton(this, "newDocumentButton", this.onNewRow,
                    {
                        value: "create"
                    });

                this.toolbarButtons[this.options.searchButtonsType].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);
                this.toolbarButtons[this.options.searchButtonsType].exSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick);
            },

            onNewRow: function (e, p_obj) {
                var destination = this.options.destination,
                    itemType = this.options.itemType;
                this.showCreateDialog({itemType: itemType, nodeRef: destination});
            },

            showCreateDialog: function (meta) {
                // Intercept before dialog show
                var me = this;
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    var addMsg = meta.addMessage;
                    var defaultMsg = this.msg("label.create-row.title");
                    var testMsg = this.msg(me.options.newRowDialogTitle);
                    if (testMsg != me.options.newRowDialogTitle){
                        defaultMsg = testMsg;
                    }
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", addMsg ? addMsg : defaultMsg ]
                    );

                    Dom.addClass(p_dialog.id + "-form", "metadata-form-edit");
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                    {
                        itemKind: "type",
                        itemId: meta.itemType,
                        destination: meta.nodeRef,
                        mode: "create",
                        formId: meta.createFormId != null ? meta.createFormId : "",
                        submitType: "json"
                    });

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width: this.options.createDialogWidth,
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function DataGrid_onActionCreate_success(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                window.location.href = window.location.protocol + "//" + window.location.host +
                                    Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
            }
        }, true);
})();