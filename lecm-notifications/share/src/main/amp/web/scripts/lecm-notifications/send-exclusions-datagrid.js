if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Base = LogicECM.module.Base || {};
LogicECM.module.Notifications = LogicECM.module.Notifications || {};

(function () {

    LogicECM.module.Notifications.sendExclusionsDataGrid = function (htmlId) {
        LogicECM.module.Notifications.sendExclusionsDataGrid.superclass.constructor.call(this, htmlId);
        return this
    };

    YAHOO.lang.extend(LogicECM.module.Notifications.sendExclusionsDataGrid, LogicECM.module.Base.DataGrid, {
        argumentsItemId: null,

        showCreateDialog: function (meta, callback, successMessage) {
            if (this.createDialogOpening) return;
            this.createDialogOpening = true;
            var me = this;
            // Intercept before dialog show
            var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                var addMsg = meta.addMessage;
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", addMsg ? addMsg : this.msg("label.create.new.exclusion")]
                );
                if (meta.itemType && meta.itemType.length) {
                    Dom.addClass(contId, meta.itemType.replace(":", "_") + "_create");
                }
                me.createDialogOpening = false;

                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
            };

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "type",
                itemId: meta.itemType,
                destination: meta.nodeRef,
                mode: "create",
                formId: "create-exclusions",
                submitType: "json",
                showCancelButton: true,
                showCaption: false
            };

            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
            createDetails.setOptions({
                width: "50em",
                templateUrl: templateUrl,
                templateRequestParams: templateRequestParams,
                actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/template/createNewExclusion',
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: doBeforeDialogShow,
                    scope: this
                },
                doBeforeAjaxRequest: {
                    scope: this,
                    fn: function (form) {
                        var exclusionEmployees = form.dataObj["assoc_lecm-notification-template_exclusion-employee"];
                        for (var property in form.dataObj) {
                            delete form.dataObj[property];
                        }
                        form.dataObj["forEmployees"] = exclusionEmployees;
                        form.dataObj["template"] = this.argumentsItemId;
                        return true;
                    }
                },
                onSuccess: {
                    fn: function () {
                        YAHOO.Bubbling.fire("datagridRefresh",
                            {
                                bubblingLabel: this.options.bubblingLabel
                            });
                        this.createDialogOpening = false;
                    },
                    scope: this
                },
                onFailure: {
                    fn: function (response) {
                        LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
                        me.createDialogOpening = false;
                        this.widgets.cancelButton.set("disabled", false);
                    },
                    scope: createDetails
                }
            }).show();
        },

        onDelete: function DataGridActions_onDelete(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {
            var me = this,
                items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

            var fnActionDeleteConfirm = function (items) {
                var nodeRefs = [];
                for (var i = 0, ii = items.length; i < ii; i++) {
                    nodeRefs.push(items[i].nodeRef);
                }
                var deleteURL = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "lecm/notifications/template/deleteExclusion?template={template}&forEmployees={forEmployees}",
                    {
                        template: me.argumentsItemId,
                        forEmployees: nodeRefs.join(",")
                    });
                Alfresco.util.Ajax.jsonDelete({
                    url: deleteURL,
                    successCallback: {
                        fn: function (response) {
                            YAHOO.Bubbling.fire("datagridRefresh",
                                {
                                    bubblingLabel: me.options.bubblingLabel
                                });
                        },
                        scope: me
                    },
                    failureMessage: me.msg("message.delete.failure")
                });
            };

            if (!fnPrompt) {
                fnPrompt = function () {
                    me.onDelete_Prompt(fnActionDeleteConfirm, me, items)
                };
            }
            fnPrompt.call(this, fnActionDeleteConfirm);
        },

        onDataGridColumns: function DataGrid_onDataGridColumns(response) {
            this.datagridColumns = response.json.columns;
            // DataSource set-up and event registration
            this.setupDataSource();
            // DataTable set-up and event registration
            this.setupDataTable();
            // DataTable actions setup
            this.setupActions();

            if (this.options.allowCreate) {
                var btn = Alfresco.util.createYUIButton(this, "newRowButton", this.onActionCreate.bind(this));
                if (this.options.createItemBtnMsg) {
                    btn.set('label', this.msg(this.options.createItemBtnMsg));
                }
            }
            var toolbar = Dom.get(this.id + "-toolbar");
            if (toolbar) {
                var del = document.createElement("a");
                del.className = "notification_clear_all";
                del.innerHTML = this.msg("title.delete-all-exclusions");
                toolbar.appendChild(del);

                YAHOO.util.Event.addListener(del, "click", function(event) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/notifications/template/clearAllExclusions",
                        dataObj: {
                            template: this.argumentsItemId
                        },
                        successCallback: {
                            fn: function (response) {
                                YAHOO.Bubbling.fire("datagridRefresh",
                                    {
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure")
                    });
                }.bind(this));
            }

            YAHOO.util.Dom.setStyle(this.id + "-toolbar", "display", "block");
            // Show grid
            YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");

            YAHOO.Bubbling.fire("datagridVisible", this);
        }
    }, true);

})();