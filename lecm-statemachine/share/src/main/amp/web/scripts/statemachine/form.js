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

(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom;
	var Event = YAHOO.util.Event;

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
		YAHOO.Bubbling.on("redrawDocumentActions", module.draw, module);
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
			var callback = {
				success: function(oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					var parent = oResponse.argument.parent;

					if (oResults.actions != null && oResults.actions.length > 0) {
						Dom.setStyle(parent.id, "display", "block");

						parent.taskId = oResults.taskId;

						var container = document.getElementById(parent.id + "-formContainer");
						var actionsContainer = document.getElementById(parent.id + "-formContainer-actions");
						if (actionsContainer != null) {
							actionsContainer.innerHTML = "";
						} else {
							actionsContainer = document.createElement("div");
							actionsContainer.id = parent.id + "-formContainer-actions";
						}

						oResults.actions.forEach(function(action) {
							var div = document.createElement("div");
                            if (action.dueDate != null) {
                                div.title = "Срок исполнения: " + action.dueDate;
                            }
							div.className = "widget-button-grey text-cropped";
                            if (action.type == "task") {
                                div.className += " task-marker";
                            }
							div.innerHTML = action.label;
							div.onclick = function() {
								parent.show(action);
							};
                            actionsContainer.appendChild(div);
						});
                        container.insertBefore(actionsContainer, container.firstChild);
					} else {
                        if (document.getElementById("final-actions-actionSet").innerHTML == "") {
	                        Dom.setStyle(parent.id, "display", "none");
                        }
					}
				},
				argument: {
					parent: this
				},
				timeout: 60000
			};
			YAHOO.util.Connect.asyncRequest('GET', url, callback);
		},
		onObjectFinderReady: function StartWorkflow_onObjectFinderReady(layer, args) {
			var objectFinder = args[1].eventGroup;
			if (objectFinder.options.field == "assoc_packageItems") {
				objectFinder.selectItems(this.options.nodeRef);
			}
		},
		onHiddenAssociationFormReady: function StartWorkflow_onObjectFinderReady(layer, args) {
			if (args[1].fieldName == "assoc_packageItems") {
				Dom.get(args[1].fieldId + "-added").value = this.options.nodeRef;
				YAHOO.Bubbling.fire("afterSetItems",
						{
							items: this.options.nodeRef
						});
			}
		},
		show: function showWorkflowForm(action) {
			if (this.doubleClickLock)
				return;
			this.doubleClickLock = true;
			this._showSplash();

			var template = "{proxyUri}lecm/statemachine/actions?documentNodeRef={documentNodeRef}&actionId={actionId}&taskId={taskId}";
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				documentNodeRef: encodeURIComponent(this.options.nodeRef),
				actionId: encodeURIComponent(action.actionId),
				taskId: encodeURIComponent(this.taskId)
			});
			var callback = {
				success: function(oResponse) {
					oResponse.argument.parent._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					var parent = oResponse.argument.parent
					if (oResults.errors != null && oResults.errors.length > 0) {
						parent.doubleClickLock = false;
						var viewDialog = new LogicECM.module.EditFieldsConfirm("confirm-edit-fields");
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
				argument: {
					parent: this
				},
				timeout: 60000
			};
			YAHOO.util.Connect.asyncRequest('GET', url, callback);
		},
		showForm: function showForm_action(action) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT;
			var templateRequestParams;
			var formWidth = "65em";
			if (action.isForm) {
                var me = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "GET",
                    url: Alfresco.constants.PROXY_URI + "lecm/documents/additionalParameters",
                    dataObj: {
                        nodeRef: this.options.nodeRef,
                        qName: action.formType
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            var json = eval("(" + oResponse.serverResponse.responseText + ")");
                            var url =  Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + action.formType;

                            var params = "documentType=" + action.formType;
                            params += "&formId=" + "workflow-form";
                            params += "&connectionType=" + action.connectionType;
                            params += "&connectionIsSystem=" + action.connectionIsSystem;
                            params += "&connectionIsReverse=" + action.connectionIsReverse;
                            params += "&parentDocumentNodeRef=" + me.options.nodeRef;

                            if (action.variables != null) {
                                for (var prop in action.variables) {
                                    if (action.variables.hasOwnProperty(prop)) {
                                        params += "&" + prop + "=" + action.variables[prop];
                                    }
                                }
                            }

                            if (json != null) {
                                for (var prop in json) {
                                    if (json.hasOwnProperty(prop)) {
                                        params += "&" + prop + "=" + json[prop];
                                    }
                                }
                            }

                            window.location.href = url + "&" + LogicECM.module.Base.Util.encodeUrlParams(params);
                        }
                    },
                    failureCallback: {
                        fn: function () {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
			} else {
				templateUrl += "lecm/components/form";
				templateRequestParams = {
					itemKind: "workflow",
					itemId: action.workflowId,
					mode: "create",
					submitType: "json",
					formId: "workflow-form",
					args: JSON.stringify(action.variables),
					nodeRef: this.options.nodeRef,
					showCancelButton: true

				};
				LogicECM.CurrentModules = {};
				var dialog = LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
					width: formWidth,
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						scope: this,
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg("logicecm.workflow.runAction.label", action.label));
							var contId = p_dialog.id + "-form-container";
							Dom.addClass(contId, "metadata-form-edit");
							if (action.formType && action.formType != "") {
								Dom.addClass(contId, action.formType.replace(":", "_"));
							} else {
								Dom.addClass(contId, "no-form-type");
							}

							this.doubleClickLock = false;

							p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
						}
					},
					doBeforeFormSubmit: {
						scope: this,
						fn: function() {
							this._showSplash();
						}
					},
					onSuccess: {
						scope: this,
						fn: function(response) {
							this._chooseState(action.type, this.taskId, response.json.persistedObject, action.actionId);
						}
					}
				});
                LogicECM.module.Base.Util.registerDialog(dialog);
                dialog.show();
			}
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
                if (action.subtype == "document" || action.subtype == "workflow") {
                    if (action.subtype == "document") {
                        var me = this;
                        Alfresco.util.Ajax.jsonRequest({
                            method: "GET",
                            url: Alfresco.constants.PROXY_URI + "lecm/documents/additionalParameters",
                            dataObj: {
                                nodeRef: this.options.nodeRef,
                                qName: action.formType
                            },
                            successCallback: {
                                fn: function (oResponse) {
                                    var json = eval("(" + oResponse.serverResponse.responseText + ")");
                                    var url =  Alfresco.constants.URL_PAGECONTEXT + "document-create?documentType=" + action.documentType;

                                    var params = "documentType=" + action.documentType;
                                    params += "&formId=" + "workflow-form";
                                    params += "&connectionType=" + action.connectionType;
                                    params += "&connectionIsSystem=" + action.connectionIsSystem;
                                    params += "&parentDocumentNodeRef=" + me.options.nodeRef;

                                    if (json != null) {
                                        for (var prop in json) {
                                            if (json.hasOwnProperty(prop)) {
                                                params += "&" + prop + "=" + json[prop];
                                            }
                                        }
                                    }
                                    window.location.href = url + "&" + LogicECM.module.Base.Util.encodeUrlParams(params);
                                }
                            },
                            failureCallback: {
                                fn: function () {
                                }
                            },
                            scope: this,
                            execScripts: true
                        });
                    } else {
                        var templateRequestParams = {
                            itemKind: "workflow",
                            itemId: action.workflowType,
                            mode: "create",
                            submitType: "json",
                            formId: "workflow-form",
                            destination: action.formFolder,
                            showCancelButton: true
                        };
                        var responseHandler = function(response) {
                            // document.location.href = document.location.href;
                            // ALF-2803
                            window.location.reload(true);
                        }

	                    var me = this;
	                    LogicECM.CurrentModules = {};
	                    var dialog = LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
		                    width: "84em",
		                    templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
		                    templateRequestParams: templateRequestParams,
		                    actionUrl: null,
		                    destroyOnHide: true,
		                    doBeforeDialogShow: {
			                    scope: this,
			                    fn: function(p_form, p_dialog) {
				                    p_dialog.dialog.setHeader(this.msg("logicecm.workflow.runAction.label", action.label));
				                    var contId = p_dialog.id + "-form-container";
				                    Dom.addClass(contId, "metadata-form-edit");
				                    if (action.formType && action.formType != "") {
					                    Dom.addClass(contId, action.formType.replace(":", "_"));
				                    } else {
					                    Dom.addClass(contId, "no-form-type");
				                    }

				                    this.doubleClickLock = false;

				                    p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
			                    }
		                    },
		                    doBeforeFormSubmit: {
			                    scope: this,
			                    fn: function() {
				                    this._showSplash();
			                    }
		                    },
		                    onSuccess: {
			                    scope: this,
			                    fn: responseHandler
		                    }
	                    });
                        LogicECM.module.Base.Util.registerDialog(dialog);
                        dialog.show();
                    }
                } else {
                    var me = this;
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: "Выполнение действия",
                            text: "Подтвердите выполнение действия \"" + action.actionId + "\"",
                            buttons: [
                                {
                                    text: "Ок",
                                    handler: function dlA_onAction_action() {
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
                                                    var json = eval("(" + oResponse.serverResponse.responseText + ")");
                                                    var item =  null;
                                                    if (json.forCollection) {
                                                        item = json;
                                                    } else {
                                                        item = json.items[0];
                                                    }
                                                    var message = "";
                                                    if (item.redirect != "") {
                                                        document.location.href = Alfresco.constants.URL_PAGECONTEXT + item.redirect;
                                                    } else if (item.openWindow) {
                                                        window.open(Alfresco.constants.URL_PAGECONTEXT + item.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                                                    } else if (!item.withErrors){
                                                        window.location.reload(true);
                                                    } else {
                                                        message += "<div class=\"" + (item.withErrors ? "error-item" : "noerror-item") + "\">" + item.message + "</div>";
                                                    }
                                                    if (message != "") {
                                                        this._openMessageWindow(actionId, message, true);
                                                    }
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
                                    handler: function () {
                                        this.destroy();
                                    },
                                    isDefault: true
                                }
                            ]
                        });
                    this.doubleClickLock = false;
                }
			}
		},
		showTask: function(taskId, taskName) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form";
			var templateRequestParams = {
				itemKind: "task",
				itemId: taskId,
				mode: "edit",
				formUI: true,
				submitType: "json",
				showCancelButton: true,
				reassignReload: true
			};
			// Using Forms Service, so always create new instance
			var taskDetails = new Alfresco.module.SimpleDialog(this.id + "-taskDetails");
			taskDetails.setOptions({
				width: "55em",
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(taskName);
						this.doubleClickLock = false;

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				onSuccess: {
					scope: this,
					fn: function(response) {
						// document.location.href = document.location.href;
						// ALF-2803
						window.location.reload(true);
					}
				}
			});
            LogicECM.module.Base.Util.registerDialog(taskDetails);
            taskDetails.show();
		},
		_createScriptForm: function _createScriptFormFunction(action) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form/script";
			var items = [this.options.nodeRef];
			var templateRequestParams = {
				itemKind: "type",
				itemId: action.actionId,
				formId: "scriptForm",
				mode: "create",
				submitType: "json",
				items: JSON.stringify(items)
			};
			// Using Forms Service, so always create new instance
			var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
			scriptForm.setOptions({
				width: "55em",
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(action.actionId);

						var contId = p_dialog.id + "-form-container";
						Dom.addClass(contId, "metadata-form-edit");
						this.doubleClickLock = false;

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				onSuccess: {
					scope: this,
					fn: function DataGrid_onActionCreate_success(response) {
						// document.location.href = document.location.href;
						// ALF-2803
						window.location.reload(true);
					}
				},
				onFailure: {
					scope: this,
					fn: function DataGrid_onActionCreate_failure(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.save.failure")
						});
						this.doubleClickLock = false;
					}
				}
			});
            LogicECM.module.Base.Util.registerDialog(scriptForm);
            scriptForm.show();
		},
		_chooseState: function(type, taskId, formResponse, actionId) {
			var template = "{proxyUri}lecm/statemachine/choosestate?actionType={actionType}&taskId={taskId}&formResponse={formResponse}&actionId={actionId}";
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				actionType: encodeURIComponent(type),
				taskId: encodeURIComponent(taskId),
				formResponse: encodeURIComponent(formResponse),
				actionId: actionId ? encodeURIComponent(actionId) : ""
			});
			this._showSplash();
			var callback = {
				success: function(oResponse) {
					oResponse.argument.contractsObject._hideSplash();
					var oResults = eval("(" + oResponse.responseText + ")");
					if (oResults.error == "") {
						if (oResults.redirect != null && oResults.redirect != "null") {
							document.location.href = Alfresco.constants.URL_PAGECONTEXT + oResults.redirect;
						} else {
							// document.location.href = document.location.href;
							// ALF-2803
							window.location.reload(true);
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
				argument: {
					contractsObject: this
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
		onWorkflowFormLoaded: function StartWorkflow_onWorkflowFormLoaded(response) {
			var formEl = Dom.get(this.id + "-workflowFormContainer");
			Dom.addClass(formEl, "hidden");
			formEl.innerHTML = response.serverResponse.responseText;
		},
		/**
		 * Event handler called when the "formContentReady" event is received
		 */
		onStartWorkflowFormContentReady: function FormManager_onStartWorkflowFormContentReady(layer, args) {
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

(function() {
	LogicECM.module.EditFieldsConfirm = function EditFieldsConfirm_constructor(htmlId) {
		var module = LogicECM.module.EditFieldsConfirm.superclass.constructor.call(this, "LogicECM.module.EditFieldsConfirm", htmlId, ["button"]);
		return module;
	};

	YAHOO.extend(LogicECM.module.EditFieldsConfirm, Alfresco.component.Base, {
		show: function showEditFieldsConfirm(nodeRef, label, errors, fields) {
			var containerDiv = document.createElement("div");
			var form = '<div id="confirm-edit-fields-form-container" class="yui-panel">' +
					'<div id="confirm-edit-fields-head" class="hd" title="Ошибка действия &quot;' + label + '&quot;">Ошибка действия "' + label + '"</div>' +
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
				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
				var templateRequestParams = {
					itemKind: "node",
					itemId: nodeRef,
					mode: "edit",
					submitType: "json",
					formId: "",
					fields: JSON.stringify(fields),
					showCancelButton: true
				};
				var dialog = new Alfresco.module.SimpleDialog("action-edit-form").setOptions({
					width: "70em",
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						scope: this,
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg("document.main.form.edit"));
							Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");

							p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
						}
					},
					onSuccess: {
						scope: this,
						fn: function(response) {
							// document.location.href = document.location.href;
							// ALF-2803
							window.location.reload(true);
						}
					}
				});
                LogicECM.module.Base.Util.registerDialog(dialog);
                dialog.show();
            }.bind(this);
		}

	});

})();
