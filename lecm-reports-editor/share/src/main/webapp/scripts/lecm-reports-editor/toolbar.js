(function () {
    LogicECM.module.ReportsEditor.Toolbar = function (htmlId) {
        return LogicECM.module.ReportsEditor.Toolbar.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.Toolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.Toolbar.prototype,
        {
            options: {
                bubblingLabel: null,
                createFormId: '',
                newRowDialogTitle: 'label.create-row.title',
                searchButtonsType: 'defaultActive',
                newRowButtonType: 'defaultActive'
            },
            doubleClickLock: false,

            _initButtons: function() {
                this.toolbarButtons[this.options.newRowButtonType].newDocumentButton = Alfresco.util.createYUIButton(this, 'newElementButton', this.onNewRow,
                    {
                        disabled: this.options.newRowButtonType != 'defaultActive',
                        value: 'create'
                    });

	            this.toolbarButtons['defaultActive'].push(
		            Alfresco.util.createYUIButton(this, "importXmlButton", this.showImportDialog)
	            );
	            this.importFromSubmitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML,{
		            disabled: true
	            });
	            Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog,{});
	            YAHOO.util.Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);
	            YAHOO.util.Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
            },

            onNewRow: function () {
                var meta = this.modules.dataGrid.datagridMeta;
                if (meta != null && meta.nodeRef.indexOf(":") > 0) {
                    var destination = meta.nodeRef;
                    var itemType = meta.itemType;
                    this.showCreateDialog({itemType: itemType, nodeRef: destination, createFormId:this.options.createFormId}, null);
                }
            },

            showCreateDialog: function (meta, successMessage) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
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

                    Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                    me.doubleClickLock = false;
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

                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width: "60em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess:{
                            fn: function DataGrid_onActionCreate_success(response) {
                                YAHOO.Bubbling.fire("dataItemCreated",
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                if ("lecm-rpeditor:reportDescriptor" == meta.itemType || "lecm-rpeditor:subReportDescriptor" == meta.itemType){
                                    YAHOO.Bubbling.fire("newReportCreated",
                                        {
                                            reportId: response.json.persistedObject
                                        });
                                }
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg(successMessage ? successMessage : "message.save.success")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope:this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            }
        }, true);
})();