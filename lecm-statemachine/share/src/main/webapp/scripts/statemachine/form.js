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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	/**
	 * StartWorkflow constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {Alfresco.component.StartWorkflow} The new StartWorkflow instance
	 * @constructor
	 */
	LogicECM.module.StartWorkflow = function StartWorkflow_constructor(htmlId) {
		var module = LogicECM.module.StartWorkflow.superclass.constructor.call(this, "LogicECM.module.StartWorkflow", htmlId, ["button"]);
		YAHOO.Bubbling.on("objectFinderReady", module.onObjectFinderReady, module);
		YAHOO.Bubbling.on("hiddenAssociationFormReady", module.onHiddenAssociationFormReady, module);
		YAHOO.Bubbling.on("formContentReady", module.onStartWorkflowFormContentReady, module);
		return module;
	};

	YAHOO.extend(LogicECM.module.StartWorkflow, Alfresco.component.Base, {
		taskId: null,
        doubleClickLock: false,
        options: {
            nodeRef: null
        },

        draw: function draw_function() {
            var url = Alfresco.constants.PROXY_URI + "/lecm/statemachine/actions?documentNodeRef=" + this.options.nodeRef;
            callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    var parent = oResponse.argument.parent

                    if (oResults.actions != null && oResults.actions.length > 0) {
                        parent.taskId = oResults.taskId;
                        var container = document.getElementById(parent.id + "-formContainer");
                        oResults.actions.forEach(function(action) {
                            var div = document.createElement("div");
                            div.className = "widget-button-grey text-cropped";
                            div.innerHTML = action.label;
                            div.onclick = function() {
                                parent.show(action);
                            }
                            container.appendChild(div);
                        });
                    } else {
                        var container = document.getElementById(parent.id);
                        container.outerHTML = "";
                    }
                },
                argument:{
                    parent:this
                },
                timeout: 60000
            };
            YAHOO.util.Connect.asyncRequest('GET', url, callback);
        },

		onObjectFinderReady:function StartWorkflow_onObjectFinderReady(layer, args) {
			var objectFinder = args[1].eventGroup;
			if (objectFinder.options.field == "assoc_packageItems") {
				objectFinder.selectItems(this.options.nodeRef);
			}
		},

		onHiddenAssociationFormReady:function StartWorkflow_onObjectFinderReady(layer, args) {
			if (args[1].fieldName == "assoc_packageItems") {
				Dom.get(args[1].fieldId + "-added").value = this.options.nodeRef;
                YAHOO.Bubbling.fire("afterSetItems",
                    {
                        items: this.options.nodeRef
                    });
			}
		},

		show: function showWorkflowForm(action) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
            this._showSplash();
            var url = Alfresco.constants.PROXY_URI + "/lecm/statemachine/actions?documentNodeRef=" + this.options.nodeRef + "&actionId=" + action.actionId + "&taskId=" + this.taskId;
            callback = {
                success:function (oResponse) {
                    oResponse.argument.parent._hideSplash();
                    var oResults = eval("(" + oResponse.responseText + ")");
                    var parent = oResponse.argument.parent
                    if (oResults.errors != null && oResults.errors.length > 0) {
                        parent.doubleClickLock = false;
                        viewDialog = new LogicECM.module.EditFieldsConfirm("confirm-edit-fields");
                        viewDialog.show(parent.options.nodeRef, action.label, oResults.errors, oResults.fields);
                        return;
                    }
                    if (action.type == "task") {
                        parent.showTask(action.actionId, action.label);
                    } else if (action.type == "group") {
                        parent.onGroupActions(action);
                    } else if ((action.workflowId != null && action.workflowId != 'null') || action.isForm) {
                        parent.showForm(action);
                    } else {
                        parent.showPromt(action);
                    }
                },
                argument:{
                    parent:this
                },
                timeout: 60000
            };
            YAHOO.util.Connect.asyncRequest('GET', url, callback);
		},

        showForm: function showForm_action(action) {
            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true&args={args}&nodeRef={nodeRef}";
            if (action.isForm) {
                templateUrl = YAHOO.lang.substitute(templateUrl, {
                    itemKind: "type",
                    itemId: action.formType,
                    destination: action.formFolder,
                    mode:"create",
                    submitType:"json",
                    formId:"workflow-form",
                    args: JSON.stringify(action.variables)
                });
            } else {
                templateUrl = YAHOO.lang.substitute(templateUrl, {
                    nodeRef: this.options.nodeRef,
                    itemKind: "workflow",
                    itemId:action.workflowId,
                    mode:"create",
                    submitType:"json",
                    formId:"workflow-form",
                    args: JSON.stringify(action.variables)
                });
            }

            var me = this;
            LogicECM.CurrentModules = {};
            LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
                width:"55em",
                templateUrl:templateUrl,
                actionUrl:null,
                destroyOnHide:true,
                doBeforeDialogShow:{
                    fn:function (p_form, p_dialog) {
                        var contId = p_dialog.id + "-form-container";
                        var dialogName = this.msg("logicecm.workflow.runAction.label", action.label);
                        Alfresco.util.populateHTML(
                            [contId + "_h", dialogName]
                        );

                        Dom.addClass(contId, "metadata-form-edit");
                        if (action.formType && action.formType != "") {
                            Dom.addClass(contId, action.formType.replace(":", "_"));
                        } else {
                            Dom.addClass(contId, "no-form-type");
                        }
                        me.doubleClickLock = false;
                    }
                },
                doBeforeFormSubmit: {
                    fn: function () {
                        this._showSplash();
                    },
                    scope: this
                },
                onSuccess:{
                    fn:function (response) {
                        this._chooseState(action.type, me.taskId, response.json.persistedObject, action.actionId);
                    },
                    scope:this
                }
            }).show();
        },

        showPromt: function showPromt_action(action) {
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
                {
                    title: "Выполнение действия",
                    text: "Подтвердите выполнение для этого документа действия \"" + action.label + "\"",
                    buttons: [
                        {
                            text: "Ок",
                            handler: function dlA_onAction_action()
                            {
                                this.destroy();
                                me._chooseState("trans", me.taskId, null, action.actionId);
                            }
                        },
                        {
                            text: "Отмена",
                            handler: function dlA_onActionDelete_cancel()
                            {
                                this.destroy();
                            },
                            isDefault: true
                        }]
                });
            this.doubleClickLock = false;
        },

        onGroupActions: function onGroupActionsFunction(action) {
            if (action.isForm) {
                this._createScriptForm(action);
            } else {
                var me = this;
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: "Выполнение действия",
                        text: "Подтвердите выполнение действия \"" + action.actionId + "\"",
                        buttons: [
                            {
                                text: "Ок",
                                handler: function dlA_onAction_action()
                                {
                                    this.destroy();
                                    var items = [];
                                    items.push(me.options.nodeRef);
                                    Alfresco.util.Ajax.jsonRequest({
                                        method: "POST",
                                        url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
                                        dataObj: {
                                            items: items,
                                            actionId: action.actionId
                                        },
                                        successCallback: {
                                            fn: function (oResponse) {
                                                document.location.href = document.location.href;
                                            }
                                        },
                                        failureCallback: {
                                            fn: function () {
                                            }
                                        },
                                        scope: me,
                                        execScripts: true
                                    });

                                }
                            },
                            {
                                text: "Отмена",
                                handler: function dlA_onActionDelete_cancel()
                                {
                                    this.destroy();
                                },
                                isDefault: true
                            }]
                    });
                    this.doubleClickLock = false;
            }
        },

        showTask: function(taskId, taskName) {
            var doBeforeDialogShow = function (p_form, p_dialog) {
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", taskName]
                );
                this.doubleClickLock = false;
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&formUI={formUI}&submitType={submitType}&showCancelButton=true",
                {
                    itemKind: "task",
                    itemId: taskId,
                    mode: "edit",
                    formUI: "true",
                    submitType: "json",
                    showCancelButton: "true"
                });

            // Using Forms Service, so always create new instance
            var taskDetails = new Alfresco.module.SimpleDialog(this.id + "-taskDetails");
            taskDetails.setOptions(
                {
                    width:"55em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn:doBeforeDialogShow,
                        scope:this
                    },
                    onSuccess: {
                        fn: function DataGrid_onActionCreate_success(response) {
                            document.location.href = document.location.href;
                        },
                        scope: this
                    }
                }).show();
        },

        _createScriptForm: function _createScriptFormFunction(action) {
            var me = this;
            var doBeforeDialogShow = function (p_form, p_dialog) {
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", action.actionId ]
                );

                Dom.addClass(contId, "metadata-form-edit");
                this.doubleClickLock = false;
            };

            var url = "/lecm/components/form/script" +
                "?itemKind={itemKind}" +
                "&itemId={itemId}" +
                "&formId={formId}" +
                "&mode={mode}" +
                "&submitType={submitType}" +
                "&items={items}";
            var items = [];
            items.push(this.options.nodeRef)
            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url,
                {
                    itemKind: "type",
                    itemId: action.actionId,
                    formId: "scriptForm",
                    mode: "create",
                    submitType: "json",
                    items: JSON.stringify(items)
                });

            // Using Forms Service, so always create new instance
            var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
            scriptForm.setOptions(
                {
                    width: "55em",
                    templateUrl: templateUrl,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: doBeforeDialogShow,
                        scope: this
                    },
                    onSuccess: {
                        fn: function DataGrid_onActionCreate_success(response) {
                            document.location.href = document.location.href;
                        },
                        scope: this
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
        },


        _chooseState:function (type, taskId, formResponse, actionId) {
			var url = Alfresco.constants.PROXY_URI + "lecm/statemachine/choosestate?actionType={actionType}&taskId={taskId}&formResponse={formResponse}&actionId={actionId}";
			url = YAHOO.lang.substitute(url, {
				actionType: type,
				taskId:taskId,
				formResponse: encodeURIComponent(formResponse),
				actionId: actionId ? actionId : ""
			});
            this._showSplash()
			callback = {
				success:function (oResponse) {
                    oResponse.argument.contractsObject._hideSplash();
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults.error == "") {
                        if (oResults.redirect != null && oResults.redirect != "null") {
                            document.location.href = Alfresco.constants.URL_PAGECONTEXT + oResults.redirect;
                        } else {
                            document.location.href = document.location.href;
                        }
                    } else {
                        Alfresco.util.PopupManager.displayPrompt(
                            {
                                title: "Ошибка выполнения действия",
                                text: "При выполнении действия произошла ошибка. Попробуйте обновить страницу и выполнить действие еще раз",
                                buttons: [
                                    {
                                        text: "Ок",
                                        handler: function dlA_onAction_action()
                                        {
                                            this.destroy();
                                        }
                                    }]
                            });
                    }
				},
				argument:{
					contractsObject:this
				},
				timeout: 60000
			};
			YAHOO.util.Connect.asyncRequest('GET', url, callback);
		},

		/**
		 * Called when a workflow form has been loaded.
		 * Will insert the form in the Dom.
		 *
		 * @method onWorkflowFormLoaded
		 * @param response {Object}
		 */
		onWorkflowFormLoaded:function StartWorkflow_onWorkflowFormLoaded(response) {
			var formEl = Dom.get(this.id + "-workflowFormContainer");
			Dom.addClass(formEl, "hidden");
			formEl.innerHTML = response.serverResponse.responseText;
		},

		/**
		 * Event handler called when the "formContentReady" event is received
		 */
		onStartWorkflowFormContentReady:function FormManager_onStartWorkflowFormContentReady(layer, args) {
			var formEl = Dom.get(this.id + "-workflowFormContainer");
			Dom.removeClass(formEl, "hidden");
		},

        _showSplash: function() {
            this.splashScreen = Alfresco.util.PopupManager.displayMessage(
                {
                    text: Alfresco.util.message("label.loading"),
                    spanClass: "wait",
                    displayTime: 0
                });
        },

        _hideSplash: function() {
            YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroy);
        }

	});
})();

(function () {
    LogicECM.module.EditFieldsConfirm = function EditFieldsConfirm_constructor(htmlId) {
        var module = LogicECM.module.EditFieldsConfirm.superclass.constructor.call(this, "LogicECM.module.EditFieldsConfirm", htmlId, ["button"]);
        return module;
    };

    YAHOO.extend(LogicECM.module.EditFieldsConfirm, Alfresco.component.Base, {
        show: function showEditFieldsConfirm(nodeRef, label, errors, fields) {
            var containerDiv = document.createElement("div");
            var form = '<div id="confirm-edit-fields-form-container" class="yui-panel">' +
                '<div id="confirm-edit-fields-head" class="hd">Ошибка действия "' + label + '"</div>' +
                '<div id="confirm-edit-fields-body" class="bd">' +
                '<div id="confirm-edit-fields-content" class="form-container"><div class="form-fields" style="padding: 1em">Выполнение действия невозможно.<br/>';
                for (var i = 0; i < errors.length; i++) {
                    form += errors[i];
                    form += "<br/>";
                }

                form += '</div></div>' +
                '<div class="bdft">'
                if (fields.length > 0) {
                    form += '<span id="confirm-edit-fields-edit" class="yui-button yui-push-button">' +
                            '<span class="first-child">' +
                            '<button id="confirm-edit-fields-edit-button" type="button" tabindex="0">Редактировать</button>' +
                            '</span>' +
                            '</span>';

                }
                form += '<span id="confirm-edit-fields-cancel" class="yui-button yui-push-button">' +
                        '<span class="first-child">' +
                        '<button id="confirm-edit-fields-cancel-button" type="button" tabindex="0">Отмена</button>' +
                        '</span>' +
                        '</span>' +
                        '</div>' +
                        '</div>' +
                        '</div>';
            containerDiv.innerHTML = form;
            this.dialog = Alfresco.util.createYUIPanel(Dom.getFirstChild(containerDiv),
                {
                    width: "35em"
                });
            Dom.setStyle("confirm-edit-fields-form-container", "display", "block");
            this.dialog.show();
            var button = document.getElementById("confirm-edit-fields-cancel-button");
            button.onclick = function() {
                this.dialog.hide();
            }.bind(this);
            button = document.getElementById("confirm-edit-fields-edit-button");
            button.onclick = function() {
                this.dialog.hide();
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true&fields={fields}";
                templateUrl = YAHOO.lang.substitute(templateUrl, {
                    itemKind:"node",
                    itemId: nodeRef,
                    mode:"edit",
                    submitType:"json",
                    formId: "",
                    fields: JSON.stringify(fields)
                });
                new Alfresco.module.SimpleDialog("action-edit-form").setOptions({
                    width:"70em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn: function(p_form, p_dialog) {
                            var fileSpan = '<span class="light">' + this.msg("document.main.form.edit") + '</span>';
                            Alfresco.util.populateHTML(
                                [ p_dialog.id + "-form-container_h", fileSpan]
                            );
                            Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                        },
                        scope: this
                    },
                    onSuccess:{
                        fn:function (response) {
                            document.location.href = document.location.href;
                        },
                        scope:this
                    }
                }).show();
            }.bind(this);
        }

    });

})();
