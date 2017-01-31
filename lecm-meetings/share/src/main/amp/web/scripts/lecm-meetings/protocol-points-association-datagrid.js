if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Base = LogicECM.module.Base || {};
LogicECM.module.Meetings = LogicECM.module.Meetings || {};

(function () {

    LogicECM.module.Meetings.protocolPointsAssociationDataGrid = function (htmlId) {
        LogicECM.module.Meetings.protocolPointsAssociationDataGrid.superclass.constructor.call(this, htmlId);

        this.options.formId = htmlId.substring(0, htmlId.indexOf("_assoc"));

        return this
    };

    YAHOO.lang.extend(LogicECM.module.Meetings.protocolPointsAssociationDataGrid, LogicECM.module.Base.AssociationDataGrid, {

        fieldsForFilter: [
            '_assoc_lecm-protocol_meeting-chairman-assoc',
            '_assoc_lecm-protocol_secretary-assoc',
            '_assoc_lecm-protocol_attended-assoc',
            '_assoc_lecm-protocol_signers-assoc'
        ],

        filteredFields: ['lecm-protocol-ts:reporter-assoc', 'lecm-protocol-ts:coreporter-assoc'],

        reportersFilter: [],

        notExistsInFilter: function (item) {
            return this.reportersFilter.indexOf(item) == -1 && item.length > 0;
        },

        getAllowedEmployees: function () {
            this.reportersFilter = [];

            this.fieldsForFilter.forEach(function (fieldId) {
                var controls = Alfresco.util.ComponentManager.find({id: this.options.formId + fieldId});

                if (controls && controls.length) {
                    this.reportersFilter = this.reportersFilter.concat(Object.keys(controls[0].selectedItems).filter(this.notExistsInFilter, this));
                } else {
                    var control = YAHOO.util.Dom.get(this.options.formId + fieldId);
                    if (control) {
                        this.reportersFilter = this.reportersFilter.concat((control.value.split(",")).filter(this.notExistsInFilter, this));
                    }
                }
            }, this);

            /* + забытый инициатор*/
            jQuery.ajax({
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/orgstructure/api/getCurrentEmployee",
                type: "GET",
                timeout: 30000,
                async: false,
                dataType: "json",
                contentType: "application/json",
                context:this,
                success: function (response) {
                    if (response && response.nodeRef) {
                        var initiator = [];
                        initiator.push(response.nodeRef);

                        this.reportersFilter = this.reportersFilter.concat(initiator.filter(this.notExistsInFilter, this));
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    Alfresco.util.PopupManager.displayMessage({
                        text: errorThrown
                    });
                }
            });

            return this.reportersFilter.join(",");
        },

        onActionEdit: function DataGrid_onActionEdit(item) {
            if (this.editDialogOpening) {
                return;
            }
            this.editDialogOpening = true;
            var me = this;

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "node",
                itemId: item.nodeRef,
                mode: "edit",
                submitType: "json",
                showCancelButton: true,
				showCaption: false
            };
            if (this.options.editForm) {
                templateRequestParams.formId = this.options.editForm;
            }
            templateRequestParams.args = JSON.stringify({
                allowedForPoint: this.getAllowedEmployees()
            });
            var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
            editDetails.setOptions({
                width: this.options.editFormWidth,
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: function (p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        if (item.type) {
                            Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
                        }
                        p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
                        this.editDialogOpening = false;

                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    },
                    scope: this
                },
                onSuccess: {
                    fn: function (response) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg("message.details.success")
                        });
                        Alfresco.util.Ajax.jsonPost({
                            url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(response.json.persistedObject).uri,
                            dataObj: this._buildDataGridParams(),
                            successCallback: {
                                fn: function DataGrid_onActionEdit_refreshSuccess(response) {
                                    YAHOO.Bubbling.fire("dataItemUpdated", {
                                        item: response.json.item,
                                        bubblingLabel: me.options.bubblingLabel
                                    });
                                },
                                scope: this
                            }
                        });
                        this.editDialogOpening = false;
                    },
                    scope: this
                },
                onFailure: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg("message.details.failure")
                        });
                        this.editDialogOpening = false;
                    },
                    scope: this
                }
            }).show();
        },

        showCreateDialog: function (meta, callback, successMessage) {
            if (this.editDialogOpening) return;
            this.editDialogOpening = true;
            var me = this;
            // Intercept before dialog show
            var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                var addMsg = meta.addMessage;
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", addMsg ? addMsg : this.msg(this.options.createFormTitleMsg)]
                );
                if (meta.itemType && meta.itemType != "") {
                    Dom.addClass(contId, meta.itemType.replace(":", "_") + "_edit");
                }
                me.editDialogOpening = false;

                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
            };

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "type",
                itemId: meta.itemType,
                destination: meta.nodeRef,
                mode: "create",
                formId: meta.createFormId != null ? meta.createFormId : "",
                submitType: "json",
                showCancelButton: true,
				showCaption: false
            };

            templateRequestParams.args = JSON.stringify({
                allowedForPoint: this.getAllowedEmployees()
            });

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
            createDetails.setOptions({
                width: "50em",
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: doBeforeDialogShow,
                    scope: this
                },
                onSuccess: {
                    fn: function (response) {
                        if (callback) {
                            callback.call(this, response.json.persistedObject);
                        } else {
                            YAHOO.Bubbling.fire("nodeCreated", {
                                nodeRef: response.json.persistedObject,
                                bubblingLabel: this.options.bubblingLabel
                            });
                            YAHOO.Bubbling.fire("dataItemCreated", {
                                nodeRef: response.json.persistedObject,
                                bubblingLabel: this.options.bubblingLabel
                            });
                            Alfresco.util.PopupManager.displayMessage({
                                text: this.msg(successMessage ? successMessage : "message.save.success")
                            });
                        }
                        this.editDialogOpening = false;
                    },
                    scope: this
                },
                onFailure: {
                    fn: function (response) {
                        LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
                        me.editDialogOpening = false;
                        this.widgets.cancelButton.set("disabled", false);
                    },
                    scope: createDetails
                }
            }).show();
        }
    }, true);

})();