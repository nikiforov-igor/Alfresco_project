/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
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

	/**
	 * StartWorkflow constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {Alfresco.component.StartWorkflow} The new StartWorkflow instance
	 * @constructor
	 */
	LogicECM.module.StartWorkflow = function StartWorkflow_constructor(htmlId) {
		var module = LogicECM.module.StartWorkflow.superclass.constructor.call(this, 'LogicECM.module.StartWorkflow', htmlId, ['button']);
		YAHOO.Bubbling.on('objectFinderReady', module.onObjectFinderReady, module);
		YAHOO.Bubbling.on('stampControlReady', module.onStampControlReady, module);
		YAHOO.Bubbling.on('formContentReady', module.onStartWorkflowFormContentReady, module);
		YAHOO.Bubbling.on('redrawDocumentActions', module.draw, module);
		return module;
	};

	YAHOO.extend(LogicECM.module.StartWorkflow, Alfresco.component.Base, {
		taskId: null,
		doubleClickLock: false,
		options: {
			nodeRef: null
		},
		draw: function draw_function() {
			var template = '{proxyUri}lecm/statemachine/actions?documentNodeRef={documentNodeRef}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				documentNodeRef: this.options.nodeRef
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var oResults = serverResponse.json;
					var container, actionsContainer, index, action, actionDiv, actionChiefDiv;
                    var showBlock = false;
					if (oResults.actions && oResults.actions.length) {
						this.taskId = oResults.taskId;

						container = document.getElementById(this.id + '-formContainer');
						actionsContainer = document.getElementById(this.id + '-formContainer-actions');
						if (actionsContainer) {
							actionsContainer.innerHTML = '';
						} else {
							actionsContainer = document.createElement('div');
							actionsContainer.id = this.id + '-formContainer-actions';
						}

						for (index in oResults.actions) {
							action = oResults.actions[index];
                            if (!action.hideAction) {
                                actionDiv = document.createElement('div');
                                actionDiv.innerHTML = action.label;
								if (action.type != 'chief_task') {
									actionDiv.onclick = this.show.bind(this, action);
									actionDiv.className = 'widget-button-grey text-cropped';
								}
								else {
									actionDiv.className = 'widget-button-grey-locked text-cropped';
								}
                                if (action.dueDate) {
                                    actionDiv.title = Alfresco.util.message('label.due_date', this.name, {0: action.dueDate});
                                }
								if (action.chiefLogin) {
									actionDiv.className += ' chief-task-marker';
								}
								else if (action.type == 'task') {
									actionDiv.className += ' task-marker';
								}
                                actionsContainer.appendChild(actionDiv);

								if (action.chiefLogin) {
									actionChiefDiv = document.createElement('div');
									actionChiefDiv.className = 'widget-subbutton-grey text-cropped';
									if (action.type == 'chief_task') {
										actionChiefDiv.innerHTML = Alfresco.util.message('label.take_chief_task', this.name, {0: action.chiefShortName});
										actionChiefDiv.onclick = this.takeChiefTask.bind(this, action);
									}
									else {
										actionChiefDiv.innerHTML = Alfresco.util.message('label.return_chief_task', this.name, {0: action.chiefShortName});
										actionChiefDiv.onclick = this.returnChiefTask.bind(this, action);
									}
									actionsContainer.appendChild(actionChiefDiv);
								}
								showBlock = true;
                            }
						}
						container.insertBefore(actionsContainer, container.firstChild);
					}
                    if (document.getElementById('final-actions-actionSet').innerHTML) {
                        showBlock = true;
					}
                    if (showBlock) {
                        Dom.setStyle(this.id, 'display', 'block');
                    } else {
                        Dom.setStyle(this.id, 'display', 'none');
                    }
				}
			};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},

		onObjectFinderReady: function StartWorkflow_onObjectFinderReady(layer, args) {
			var objectFinder = args[1].eventGroup;
			if (objectFinder.options.field == 'assoc_packageItems') {
				objectFinder.selectItems(this.options.nodeRef);
			}
		},
		onStampControlReady: function StartWorkflow_onStampControlReady(layer, args) {
			var stampControl = args[1].eventGroup;
			stampControl.options.itemId = this.options.nodeRef;
		},
		show: function showWorkflowForm(action) {
			if (this.doubleClickLock) {
				return;
			}
			this.doubleClickLock = true;
			this._showSplash();

			var template = '{proxyUri}lecm/statemachine/actions?documentNodeRef={documentNodeRef}&actionId={actionId}&taskId={taskId}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				documentNodeRef: encodeURIComponent(this.options.nodeRef),
				actionId: encodeURIComponent(action.actionId),
				taskId: encodeURIComponent(this.taskId == null ? "" : this.taskId)
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					this._hideSplash();
					var oResults = serverResponse.json;
					if (oResults.errors && oResults.errors.length && !oResults.doesNotBlock) {
						this.doubleClickLock = false;
						var viewDialog = new LogicECM.module.EditFieldsConfirm('confirm-edit-fields');
						viewDialog.show(this.options.nodeRef, action.label, oResults.errors, oResults.fields);
						return;
					}
					if (action.type == 'task') {
						this.showTask(action.actionId, action.label);
					} else if (action.type == 'group') {
						this.onGroupActions(action);
					} else if ((action.workflowId && action.workflowId != 'null') || action.isForm) {
						this.showForm(action, oResults.errors);
					} else {
						this.showPromt(action, oResults.errors);
					}
				}
			};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},
		showForm: function showForm_function(action, errors) {
			var i, message = '';
			if (errors && errors.length) {
				for (i in errors) {
					message += errors[i] + '<br>';
				}
				Alfresco.util.PopupManager.displayPrompt({
					title: Alfresco.util.message('title.execute_action'),
					text: message + Alfresco.util.message('msg.action_document_confirm', this.name, action.label ),
					noEscape: true,
					buttons: [{
						text: Alfresco.util.message('button.ok'),
						handler: {
							fn: function (event, obj) {
								this.destroy();
								obj.parent._showForm(obj.action);
							},
							obj: {
								parent: this,
								action: action
							}
						}
					},{
						text: Alfresco.util.message('button.cancel'),
						handler: function(event, obj) {
							this.destroy();
						},
						isDefault: true
					}]
				});
				this.doubleClickLock = false;
			} else {
				this._showForm(action);
			}
		},
		_showForm: function showForm_action(action) {
			var templateUrl;
			var templateRequestParams;
			if (action.isForm) {
				this._createDocument(action);
			} else {
				templateUrl = Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form';
				templateRequestParams = {
					itemKind: 'workflow',
					itemId: action.workflowId,
					mode: 'create',
					submitType: 'json',
					formId: 'workflow-form',
					args: JSON.stringify(action.variables),
					nodeRef: this.options.nodeRef,
					showCancelButton: true,
					showCaption: false

				};
				if (action.variables != null) {
					if (action.variables.formId != null && action.variables.formId.length > 0) {
						templateRequestParams.formId = action.variables.formId;
					}
				}
				var dialog = new Alfresco.module.SimpleDialog('workflow-form').setOptions({
					width: '65em',
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						scope: this,
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg('logicecm.workflow.runAction.label', action.label));
							var contId = p_dialog.id + '-form-container';
							Dom.addClass(contId, 'metadata-form-edit');
							if (action.documentType) {
								Dom.addClass(contId, action.documentType.replace(':', '_'));
							} else {
								Dom.addClass(contId, 'no-form-type');
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
		showPromt: function showPromt_action(action, errors) {
			var i, message = '';
			for (i in errors) {
				message += errors[i] + '<br>';
			}
			if (!action.doNotAskForConfirmation || errors.length) {
				Alfresco.util.PopupManager.displayPrompt({
					title: Alfresco.util.message('title.execute_action'),
					text: message + Alfresco.util.message('msg.action_document_confirm', this.name, action.label),
					noEscape: true,
					buttons: [{
						text: Alfresco.util.message('button.ok'),
						handler: {
							fn: function (event, obj) {
								this.destroy();
								obj.parent._chooseState('trans', obj.parent.taskId, null, obj.action.actionId);
							},
							obj: {
								parent: this,
								action: action
							}
						}
					}, {
						text: Alfresco.util.message('button.cancel'),
						handler: function (event, obj) {
							this.destroy();
						},
						isDefault: true
					}]
				});
			} else {
				this._chooseState('trans', this.taskId, null, action.actionId, action);
			}
			this.doubleClickLock = false;
		},
		onGroupActions: function onGroupActionsFunction(action) {
			if (action.isForm) {
				this._createScriptForm(action);
			} else {
				if (action.subtype == 'document') {
					var me = this;
					me.doubleClickLock = true;
					Alfresco.util.PopupManager.displayPrompt({
						title: Alfresco.util.message('title.execute_action'),
						text: Alfresco.util.message('msg.action_confirm', this.name, action.label ),
						buttons: [{
							text: Alfresco.util.message('button.ok'),
							handler: {
								fn: function(event, obj) {
									this.destroy();
									me._createDocument(action);
								},
								obj: this
							}
						},{
							text: Alfresco.util.message('button.cancel'),
							handler: function(event, obj) {
								this.destroy();
								me.doubleClickLock = false;
							},
							isDefault: true
						}]
					});
				} else if (action.subtype == 'workflow') {
					var templateRequestParams = {
						itemKind: 'workflow',
						itemId: action.workflowType,
						mode: 'create',
						submitType: 'json',
						formId: 'workflow-form',
						destination: action.formFolder,
						showCancelButton: true,
						args: JSON.stringify(action.variables),
						showCaption: false
					};

					var dialogWidth = '84em';
					if (action && action.workflowType == 'activiti$regnumReservationExecution') {
						dialogWidth = '30em';
					}

					var dialog = new Alfresco.module.SimpleDialog('workflow-form').setOptions({
						width: dialogWidth,
						templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
						templateRequestParams: templateRequestParams,
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function(p_form, p_dialog) {
								p_dialog.dialog.setHeader(this.msg('logicecm.workflow.runAction.label', action.label));
								var contId = p_dialog.id + '-form-container';
								Dom.addClass(contId, 'metadata-form-edit');
								if (action.documentType) {
									Dom.addClass(contId, action.documentType.replace(':', '_'));
								} else {
									Dom.addClass(contId, 'no-form-type');
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
								this._chooseState("signal", null, response.json.persistedObject, null);
							}
						}
					});
					LogicECM.module.Base.Util.registerDialog(dialog);
					dialog.show();
				} else {
					Alfresco.util.PopupManager.displayPrompt({
						title: Alfresco.util.message('title.execute_action'),
						text: Alfresco.util.message('msg.action_confirm', this.name, action.label ),
						buttons: [{
							text: Alfresco.util.message('button.ok'),
							handler: {
								fn: function(event, obj) {
									this.destroy();
									var items = [obj.options.nodeRef];
									Alfresco.util.Ajax.jsonPost({
										url: Alfresco.constants.PROXY_URI + 'lecm/groupActions/exec',
										dataObj: {
											items: items,
											actionId: action.actionId
										},
										successCallback: {
											scope: obj,
											fn: function (oResponse) {
												var json = oResponse.json;
												var item = json.forCollection ? json : json.items[0];
												var message;
												//1. есть ошибки
												//показываем сообщение об ошибке, и подробности если есть
												if (item.withErrors) {
													var title = Alfresco.util.message('title.action_error') + ' ' + action.label;
													this._displayErrorMessageWithDetails(title, item.message);
												} else
												//2. есть отметка - показывать сообщение
												if (item.showModalWindow==true) {
													message = '<div class="noerror-item">' + item.message + '</div>';
													if (item.redirect) {
														this._openMessageWindow(action.label, message, true, Alfresco.constants.URL_PAGECONTEXT + item.redirect);
													} else {
														this._openMessageWindow(action.label, message, true);
													}
												} else
												//3. редирект
												if (item.redirect) {
                                                    if (item.postRedirect) {
                                                        LogicECM.module.Base.Util.setPostLocation(Alfresco.constants.URL_PAGECONTEXT + item.redirect);
                                                    } else {
                                                        window.location.href = Alfresco.constants.URL_PAGECONTEXT + item.redirect;
                                                    }
												} else
												//4. open window
												if (item.openWindow) {
													window.open(Alfresco.constants.URL_PAGECONTEXT + item.openWindow, '', 'toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no');
												} else {
												//5. по дефолту  - просто обновить.
													window.location.reload(true);
												}
											}
										},
										failureMessage: Alfresco.util.message('message.failure'),
										execScripts: true
									});
								},
								obj: this
							}
						},{
							text: Alfresco.util.message('button.cancel'),
							handler: function(event, obj) {
								this.destroy();
							},
							isDefault: true
						}]
					});
					this.doubleClickLock = false;
				}
			}
		},
		showTask: function(taskId, taskName) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + 'components/form';
			var templateRequestParams = {
				itemKind: 'task',
				itemId: taskId,
				mode: 'edit',
				formUI: true,
				submitType: 'json',
				showCancelButton: true,
				reassignReload: true,
				showCaption: false
			};

			var dialogWidth = '55em';
			if (taskName == 'Резервирование') {
				dialogWidth = '30em';
			}

			// Using Forms Service, so always create new instance
			var taskDetails = new Alfresco.module.SimpleDialog(this.id + '-taskDetails');
			taskDetails.setOptions({
				width: dialogWidth,
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
		takeChiefTask: function(action) {
			var template = '{proxyUri}lecm/statemachine/takeChiefTask?documentNodeRef={documentNodeRef}&actionId={actionId}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				documentNodeRef: encodeURIComponent(this.options.nodeRef),
				actionId: encodeURIComponent(action.actionId)
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var oResults = serverResponse.json;
					if (oResults.errors && oResults.errors.length && !oResults.doesNotBlock) {
						this.doubleClickLock = false;
						var viewDialog = new LogicECM.module.EditFieldsConfirm('confirm-edit-fields');
						viewDialog.show(this.options.nodeRef, action.label, oResults.errors, oResults.fields);
						return;
					}
					window.location.reload(true);
				}
			};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},
		returnChiefTask: function(action) {
			var template = '{proxyUri}lecm/statemachine/returnChiefTask?documentNodeRef={documentNodeRef}&actionId={actionId}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				documentNodeRef: encodeURIComponent(this.options.nodeRef),
				actionId: encodeURIComponent(action.actionId)
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var oResults = serverResponse.json;
					if (oResults.errors && oResults.errors.length && !oResults.doesNotBlock) {
						this.doubleClickLock = false;
						var viewDialog = new LogicECM.module.EditFieldsConfirm('confirm-edit-fields');
						viewDialog.show(this.options.nodeRef, action.label, oResults.errors, oResults.fields);
						return;
					}
					window.location.reload(true);
				}
			};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},
		_createScriptForm: function _createScriptFormFunction(action) {
			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form/script';
			var items = [this.options.nodeRef];
			var templateRequestParams = {
				itemKind: 'type',
				itemId: action.actionId,
				formId: 'scriptForm',
				mode: 'create',
				submitType: 'json',
				items: JSON.stringify(items)
			};
			// Using Forms Service, so always create new instance
			var scriptForm = new Alfresco.module.SimpleDialog(this.id + '-scriptForm');
			scriptForm.setOptions({
				width: '55em',
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(action.label);

						var contId = p_dialog.id + '-form-container';
						Dom.addClass(contId, 'metadata-form-edit');
						this.doubleClickLock = false;

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				onSuccess: {
					scope: this,
					fn: function DataGrid_onActionCreate_success(response) {
						var json = eval('(' + response.serverResponse.responseText + ')');
						var item = (json.forCollection) ? json : json.items[0];
						var message;
						if (item.redirect) {
							LogicECM.module.Base.Util.setPostLocation(Alfresco.constants.URL_PAGECONTEXT + item.redirect);
						} else if (item.openWindow) {
							window.open(Alfresco.constants.URL_PAGECONTEXT + item.openWindow, '', 'toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no');
						} else if (!item.withErrors){
							window.location.reload(true);
						} else {
							message = '<div class="' + (item.withErrors ? 'error-item' : 'noerror-item') + '">' + item.message + '</div>';
							this._openMessageWindow(action.label, message, true);
						}
					}
				},
				onFailure: {
					scope: this,
					fn: function DataGrid_onActionCreate_failure(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.save.failure')
						});
						this.doubleClickLock = false;
					}
				}
			});
			LogicECM.module.Base.Util.registerDialog(scriptForm);
			scriptForm.show();
		},
		_createDocument: function _createDocumentFunction(action) {
			if (action.autoFill) {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + 'lecm/documents/additionalParameters',
					dataObj: {
						nodeRef: this.options.nodeRef,
						qName: action.documentType
					},
					successCallback: {
						scope: this,
						fn: function (oResponse) {
							this._redirectToCreateDocument(action, oResponse.json);
						}
					},
					failureMessage: this.msg('message.failure'),
					execScripts: true
				});
			} else {
				this._redirectToCreateDocument(action, null);
			}
		},
		_redirectToCreateDocument: function _redirectToCreateDocument_function(action, additionalParams) {
			var url =  Alfresco.constants.URL_PAGECONTEXT + action.createUrl + '?documentType=' + action.documentType;

			var params = 'documentType=' + action.documentType;
			params += '&formId=' + 'workflow-form';
			params += '&connectionType=' + action.connectionType;
			params += '&connectionIsSystem=' + action.connectionIsSystem;
			params += '&connectionIsReverse=' + action.connectionIsReverse;
			params += '&parentDocumentNodeRef=' + this.options.nodeRef;
			params += '&actionType=' + action.type;
			params += '&taskId=' + this.taskId;
			params += '&actionId=' + action.actionId;
			//this._chooseState(action.type, this.taskId, response.json.persistedObject, action.actionId);
			//me._chooseState('trans', me.taskId, null, action.actionId);
			var prop;

			if (action.variables) {
				for (prop in action.variables) {
					if (action.variables.hasOwnProperty(prop)) {
						params += '&' + prop + '=' + action.variables[prop];
					}
				}
			}

			if (additionalParams) {
				for (prop in additionalParams) {
					if (additionalParams.hasOwnProperty(prop)) {
						params += '&' + prop + '=' + additionalParams[prop];
					}
				}
			}
			this.doubleClickLock = false;
			LogicECM.module.Base.Util.setPostLocation(url + '&' + LogicECM.module.Base.Util.encodeUrlParams(params));
		},
		_chooseState: function(type, taskId, formResponse, actionId) {
			var template = '{proxyUri}lecm/statemachine/choosestate?actionType={actionType}&taskId={taskId}&formResponse={formResponse}&actionId={actionId}';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				actionType: encodeURIComponent(type),
				taskId: encodeURIComponent(taskId),
				formResponse: encodeURIComponent(formResponse),
				actionId: actionId ? encodeURIComponent(actionId) : ''
			});
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var oResults = serverResponse.json;
					this._hideSplash();
					if (oResults.error) {
						Alfresco.util.PopupManager.displayPrompt({
							title: Alfresco.util.message('title.action_error'),
							text: Alfresco.util.message('msg.action_error'),
							buttons: [{
								text: Alfresco.util.message('button.ok'),
								handler: function dlA_onAction_action() {
									this.destroy();
								}
							}]
						});
					} else {
						if (oResults.redirect && oResults.redirect != 'null') {
							window.location.href = Alfresco.constants.URL_PAGECONTEXT + oResults.redirect;
							//LogicECM.module.Base.Util.setPostLocation(Alfresco.constants.URL_PAGECONTEXT + oResults.redirect);
						} else {
							// document.location.href = document.location.href;
							// ALF-2803
							window.location.reload(true);
						}
					}
				}
			};
			this._showSplash();
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},
		/**
		 * Called when a workflow form has been loaded.
		 * Will insert the form in the Dom.
		 *
		 * @method onWorkflowFormLoaded
		 * @param response {Object}
		 */
		onWorkflowFormLoaded: function StartWorkflow_onWorkflowFormLoaded(response) {
			var formEl = Dom.get(this.id + '-workflowFormContainer');
			Dom.addClass(formEl, 'hidden');
			formEl.innerHTML = response.serverResponse.responseText;
		},
		/**
		 * Event handler called when the 'formContentReady' event is received
		 * @method onStartWorkflowFormContentReady
		 * @param layer
		 * @param args
		 */
		onStartWorkflowFormContentReady: function FormManager_onStartWorkflowFormContentReady(layer, args) {
			var formEl = Dom.get(this.id + '-workflowFormContainer');
			Dom.removeClass(formEl, 'hidden');
		},
		_showSplash: function() {
			this.splashScreen = Alfresco.util.PopupManager.displayMessage({
				text: Alfresco.util.message('label.loading'),
				spanClass: 'wait',
				displayTime: 0
			});
		},
		_hideSplash: function() {
			YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroyWithAnimationsStop);
		},

		_displayErrorMessageWithDetails: function(title, msgDetails) {
			
			var customMsg = msgDetails.match("\\[\\[.+\\]\\]");
			if (customMsg != null) {
				msgDetails = customMsg[0].replace("[[", "").replace("]]", "");
			}
			var errorDialogBody = '<div class="error-item">' + title + '</div>';
			errorDialogBody += '<a href="javascript:void(0);" id="' + this.id + '-error-message-show-details-link">' + this.msg("logicecm.base.error.show.details") + '</a></div>';
			errorDialogBody += '<div id="' + this.id + '-error-message-show-details" style="display:none" class="error-dialog-details">' + msgDetails + '</div>';

			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message('title.action_error'),
				text: errorDialogBody,
				noEscape: true,
				buttons: [{
					text: Alfresco.util.message('button.ok'),
					handler: function dlA_onAction_action() {
						this.destroy();
						// ALF-2803
						window.location.reload(true);
					}
				}]
			});

			YAHOO.util.Event.on(this.id + "-error-message-show-details-link", "click", this._errorMessageShowDetails, null, this);
		},

		_errorMessageShowDetails: function() {
			Dom.setStyle(this.id + "-error-message-show-details", "display", "block");
		},

		_openMessageWindow: function openMessageWindowFunction(title, message, reload, newHref) {
			Alfresco.util.PopupManager.displayPrompt({
				title: Alfresco.util.message('title.action_result',this.name,{0:title }),
				text: message,
				noEscape: true,
				buttons: [{
					text: Alfresco.util.message('button.ok'),
					handler: function dlA_onAction_action() {
						this.destroy();
						if (reload) {
							if ((newHref === undefined)) {
								// ALF-2803
								window.location.reload(true);
							} else {
								document.location.href = newHref;
							}
						}
					}
				}]
			});
		}
	});
})();

(function() {
	LogicECM.module.EditFieldsConfirm = function EditFieldsConfirm_constructor(htmlId) {
		var module = LogicECM.module.EditFieldsConfirm.superclass.constructor.call(this, 'LogicECM.module.EditFieldsConfirm', htmlId, ['button']);
		return module;
	};

	YAHOO.extend(LogicECM.module.EditFieldsConfirm, Alfresco.component.Base, {
		show: function showEditFieldsConfirm(nodeRef, label, errors, fields) {
			Alfresco.util.Ajax.jsonGet(
				{
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/security/api/getPermission?nodeRef=" + encodeURIComponent(nodeRef) + "&permission=" + encodeURIComponent("_lecmPerm_AttrEdit"),
					successCallback: {
						fn: function (response) {
							var containerDiv = document.createElement('div');
							var form = '<div id="confirm-edit-fields-form-container" class="yui-panel">' +
								'<div id="confirm-edit-fields-head" class="hd" title="'+Alfresco.util.message('title.action_failed')+' &quot;' + label + '&quot;">'+Alfresco.util.message('title.action_failed')+' "' + label + '"</div>' +
								'<div id="confirm-edit-fields-body" class="bd">' +
								'<div id="confirm-edit-fields-content" class="form-container"><div class="form-fields">'+Alfresco.util.message('msg.action_failed')+'<br/>';
							for (var i = 0; i < errors.length; i++) {
								form += errors[i];
								form += '<br/>';
							}

							form += '</div></div>' +
								'<div class="bdft">';
							if (fields.length > 0 && response.json == true) {
								form += '<span id="confirm-edit-fields-edit" class="yui-button yui-push-button">' +
									'<span class="first-child">' +
									'<button id="confirm-edit-fields-edit-button" type="button" tabindex="0">'+Alfresco.util.message('btn.edit')+'</button>' +
									'</span>' +
									'</span>';

							}
							form += '<span id="confirm-edit-fields-cancel" class="yui-button yui-push-button">' +
								'<span class="first-child">' +
								'<button id="confirm-edit-fields-cancel-button" type="button" tabindex="0">'+Alfresco.util.message('button.cancel')+'</button>' +
								'</span>' +
								'</span>' +
								'</div>' +
								'</div>' +
								'</div>';
							containerDiv.innerHTML = form;
							var dialog = Alfresco.util.createYUIPanel(Dom.getFirstChild(containerDiv), {
								width: '35em'
							});
							Dom.setStyle('confirm-edit-fields-form-container', 'display', 'block');
							dialog.show();
							var button = document.getElementById('confirm-edit-fields-cancel-button');
							if (button) {
								button.onclick = dialog.hide.bind(dialog);
							}
							button = document.getElementById('confirm-edit-fields-edit-button');
							if (button) {
								button.onclick = function(dlg, ref, flds) {
                                    Alfresco.util.Ajax.jsonGet(
                                        {
                                            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/url/edit",
                                            dataObj: {
                                                nodeRef: ref
                                            },
                                            successCallback: {
                                                fn:function(response){
                                                    dlg.hide();
                                                    var url =  Alfresco.constants.URL_PAGECONTEXT + response.json.url + '?nodeRef=' + ref;
                                                    var params = 'highlightedFields=' + JSON.stringify(flds);
                                                    LogicECM.module.Base.Util.setPostLocation(url + '&' + LogicECM.module.Base.Util.encodeUrlParams(params));
                                                },
                                                scope: this
                                            },
                                            failureCallback: {
                                                scope: this,
                                                fn: function (response) {
                                                    dlg.hide();
                                                    Alfresco.util.PopupManager.displayMessage({
                                                        text: this.msg("message.failure")
                                                    });
                                                }
                                            }
                                        });
								}.bind(button, dialog, nodeRef, fields);
							}
						}
					},
					failureMessage: {
						fn: function (response) {
							alert(response.responseText);
						}
					}
				});
		}
	});
})();
