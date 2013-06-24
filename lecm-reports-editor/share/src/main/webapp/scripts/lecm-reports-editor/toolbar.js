(function () {
    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.ReportsEditor.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.ReportsEditor.Toolbar = function (htmlId) {
        return LogicECM.module.ReportsEditor.Toolbar.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.Toolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.Toolbar.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {
                bubblingLabel: null,
                newRowDialogTitle: 'label.create-row.title',
                searchButtonsType: 'defaultActive',
                newRowButtonType: 'defaultActive'
            },

            _initButtons: function() {
                this.toolbarButtons[this.options.newRowButtonType].newDocumentButton = Alfresco.util.createYUIButton(this, 'newElementButton', this.onNewRow,
                    {
                        disabled: this.options.newRowButtonType != 'defaultActive',
                        value: 'create'
                    });
            },

            onNewRow: function (e, p_obj) {
                var meta = this.modules.dataGrid.datagridMeta;
                if (meta != null && meta.nodeRef.indexOf(":") > 0) {
                    var destination = meta.nodeRef;
                    var itemType = meta.itemType;
                    this.showCreateDialog({itemType: itemType, nodeRef: destination});
                }
            },

            showCreateDialog: function (meta, successMessage) {
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
                        width: "70em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess:{
                            fn: function DataGrid_onActionCreate_success(response) {
                                YAHOO.Bubbling.fire("nodeCreated",
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg(successMessage ? successMessage : "message.save.success")
                                    });
                            },
                            scope:this
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