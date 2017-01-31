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

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Documents.Create = function(htmlId) {
		LogicECM.module.Documents.Create.superclass.constructor.call(this, 'LogicECM.module.Documents.Create', htmlId, ['container', 'json']);

		YAHOO.Bubbling.on('beforeFormRuntimeInit', this.onBeforeFormRuntimeInit, this);
		YAHOO.Bubbling.on('formContentReady', this.onFormContentReady, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.Create, Alfresco.component.Base, {
		options: {
			documentType: null,
			formId: null,

			//Параметры для создания связи
			connectionType: null,
			connectionIsSystem: null,
			connectionIsReverse: false,
			parentDocumentNodeRef: null,
			//для завершения процесса
			workflowTask: null,
			//для перевода родительского документа в другой статус
			actionId: null,
			actionType: null,
			taskId: null,
			backUrl: null,
			args: {}
		},

		rootFolder: null,
		splashScreen: null,
		runtimeForm: null,

		onReady: function () {
			this.loadDraftRoot();
		},

		loadDraftRoot: function() {
			var url;
			var template = '{proxyUri}lecm/document-type/settings?docType={docType}';
			var successCallback;
			if (this.options.documentType) {
				url = YAHOO.lang.substitute(template, {
					proxyUri: Alfresco.constants.PROXY_URI,
					docType: encodeURIComponent(this.options.documentType)
				});

				successCallback = {
					scope: this,
					fn: function(serverResponse) {
						this.rootFolder = serverResponse.json.nodeRef;
						this.loadForm();
					}
				};

				Alfresco.util.Ajax.jsonGet({
					url: url,
					successCallback: successCallback,
					failureMessage: this.msg('message.failure')
				});
			}
		},

		loadForm: function() {
			var template = '{serviceContext}lecm/components/form';
			var url = YAHOO.lang.substitute(template, {
				serviceContext: Alfresco.constants.URL_SERVICECONTEXT
			});
			var dataObj = {
				htmlid: this.id,
				itemKind: 'type',
				itemId: this.options.documentType,
				destination: this.rootFolder,
				mode: 'create',
				submitType: 'json',
				formId: this.options.formId,
				showSubmitButton: true,
				showCancelButton: true,
				showCaption: false,
				args: JSON.stringify(this.options.args)
			};
			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					var container = Dom.get(this.id + '-body');
					container.innerHTML = serverResponse.serverResponse.responseText;
				}
			};
			Alfresco.util.Ajax.request({
				method: 'POST',
				url: url,
				dataObj: dataObj,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure'),
				execScripts: true
			});
		},

		onFormContentReady: function(layer, args) {
			if (args[1].parentId == this.id) {
				var submitButton = args[1].buttons.submit;
				submitButton.set('label', this.msg('label.save'));

				var cancelButton = args[1].buttons.cancel;
				if (cancelButton) {
					cancelButton.addListener('click', this.onCancelButtonClick, null, this);
				}

				var previewHeight = Dom.getRegion(this.id + '-form-fields').height - 26;
				Dom.setStyle(this.id + '-preview', 'height', previewHeight.toString() + 'px');
			}
		},

		onBeforeFormRuntimeInit: function(layer, args) {
            YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
			this.runtimeForm = args[1].runtime;
			var submitElement = this.runtimeForm.submitElements[0];
			var originalSubmitFunction = submitElement.submitForm;

			var newSubmitFunction = function(fn, scope) {
				if (this.runtimeForm.validate()) {
					this._showSplash();
				}
				if (YAHOO.lang.isFunction(fn) && scope) {
					fn.call(scope);
				}
			};

			submitElement.submitForm = newSubmitFunction.bind(this, originalSubmitFunction, submitElement);

			this.runtimeForm.setAJAXSubmit(true, {
				successCallback: {
					scope: this,
					fn: this.onFormSubmitSuccess
				},
				failureCallback: {
					scope: this,
					fn: this.onFormSubmitFailure
				}
			});
		},

		onFormSubmitSuccess: function (response) {
			var createdDocument = response.json.persistedObject;
			var index;

			for (index in this.runtimeForm.submitElements) {
				this.runtimeForm.submitElements[index].set('disabled', true);
			}

			if (YAHOO.lang.isFunction(LogicECM.onSuccessCustomFunction)) {
				LogicECM.onSuccessCustomFunction(createdDocument, this);
			}
			var defferedConfig = {
				scope: this,
				fn: this.onFormSubmitSuccessRedirect,
				obj: createdDocument
			};
			var deferredLabels = ['chooseState', 'connectDocuments', 'workflowTask'];
			this.deferredFormSubmitSuccessRedirect = new Alfresco.util.Deferred(deferredLabels, defferedConfig);

			var fn;
			if (this.options.connectionType && this.options.parentDocumentNodeRef) {
				deferredLabels.splice(deferredLabels.indexOf('connectDocuments'), 1);
				fn = function(document) {
					this._connectDocuments('connectDocuments', document);
				};
			} else if (this.options.workflowTask) {
				deferredLabels.splice(deferredLabels.indexOf('workflowTask'), 1);
				fn = function(document) {
					this._workflowTask('workflowTask', document);
				};
			}
			if (this.options.actionId && this.options.actionType && this.options.taskId) {
				deferredLabels.splice(deferredLabels.indexOf('chooseState'), 1);
				this._chooseState('chooseState', this.options.actionType, this.options.taskId, null, this.options.actionId, fn, createdDocument);
			} else if (YAHOO.lang.isFunction(fn)) {
				fn.call(this, createdDocument);
			}

			for (index in deferredLabels) {
				this.deferredFormSubmitSuccessRedirect.fulfil(deferredLabels[index]);
			}
		},

		_chooseState: function(deferredLabel, type, taskId, formResponse, actionId, callback, callbackParams) {
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
					if (serverResponse.json && serverResponse.json.error) {
						Alfresco.util.PopupManager.displayPrompt({
							title: this.msg('title.action_failed'),
							text: this.msg('msg.action_failed_text'),
							buttons: [{
								text: this.msg('button.ok'),
								handler: function dlA_onAction_action(){
									this.destroy();
								}
							}]
						});
					}
					this.deferredFormSubmitSuccessRedirect.fulfil(deferredLabel);
					if (YAHOO.lang.isFunction(callback)) {
						callback.call(this, callbackParams);
					}
				}
			};
			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: successCallback,
				failureCallback: {
					scope: this,
					fn: function () {
						this.deferredFormSubmitSuccessRedirect.fulfil(deferredLabel);
						if (YAHOO.lang.isFunction(callback)) {
							callback.call(this, callbackParams);
						}
					}
				}
			});
		},

		_connectDocuments: function(deferredLabel, createdDocument) {
			var template = '{proxyUri}lecm/documents/connection?connectionType={connectionType}&connectionIsSystem={connectionIsSystem}&fromNodeRef={fromNodeRef}&toNodeRef={toNodeRef}';
			var fromNodeRef = this.options.connectionIsReverse ? createdDocument : this.options.parentDocumentNodeRef;
			var toNodeRef = this.options.connectionIsReverse ? this.options.parentDocumentNodeRef : createdDocument;

			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				connectionType: encodeURIComponent(this.options.connectionType),
				connectionIsSystem: encodeURIComponent(this.options.connectionIsSystem),
				fromNodeRef: fromNodeRef,
				toNodeRef: toNodeRef
			});

			var successCallback = {
				scope: this,
				fn: function(serverResponse) {
					this.deferredFormSubmitSuccessRedirect.fulfil(deferredLabel);
				}
			};

			Alfresco.util.Ajax.request({
				url: url,
				successCallback: successCallback,
				failureMessage: this.msg('message.failure')
			});
		},

		_workflowTask: function(deferredLabel, createdDocument) {
			var template = '{proxyUri}api/task/{workflowTask}/formprocessor';
			var url = YAHOO.lang.substitute(template, {
				proxyUri: Alfresco.constants.PROXY_URI,
				workflowTask: encodeURIComponent(this.options.workflowTask)
			});
			var dataObj = {
				'prop_lecmWorkflowDocument_createdNodeRef': createdDocument,
				'direct-type': 'on',
				'prop_transitions': 'Next'
			};

			Alfresco.util.Ajax.jsonPost({
				url: url,
				dataObj: dataObj,
				successCallback: {
					scope: this,
					fn: function (response) {
						this.deferredFormSubmitSuccessRedirect.fulfil(deferredLabel);
					}
				},
				failureMessage: this.msg('message.failure')
			});
		},

		onFormSubmitSuccessRedirect: function(nodeRef) {
			var reloadCheckbox = Dom.get('document-form-close-and-create-new');
			if (reloadCheckbox && reloadCheckbox.checked) {
				window.location.reload();
			} else if (this.options.backUrl != null) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + decodeURIComponent(this.options.backUrl);
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + nodeRef;
			}
		},

		onFormSubmitFailure: function(response) {
			for (var index in this.runtimeForm.submitElements) {
				this.runtimeForm.submitElements[index].set('disabled', false);
			}
			this._hideSplash();
			Alfresco.util.PopupManager.displayPrompt({
				text: this.msg('message.failure')
			});
		},

		onCancelButtonClick: function() {
			document.location.href = document.referrer;
		},

		_showSplash: function() {
			this.splashScreen = Alfresco.util.PopupManager.displayMessage({
				text: this.msg('label.loading'),
				spanClass: 'wait',
				displayTime: 0
			});
		},

		_hideSplash: function() {
			YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroyWithAnimationsStop);
		}
	});
})();
