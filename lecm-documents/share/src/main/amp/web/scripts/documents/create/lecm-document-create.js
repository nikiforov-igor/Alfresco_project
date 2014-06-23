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

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Documents.Create = function(htmlId)
	{
		LogicECM.module.Documents.Create.superclass.constructor.call(this, "LogicECM.module.Documents.Create", htmlId, ["container", "json"]);

		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
		YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.Create, Alfresco.component.Base,
		{
			options: {
				documentType: null,
				formId: null,

				//Параметры для создания связи
				connectionType: null,
				connectionIsSystem: null,
				parentDocumentNodeRef: null,

				args: {}
			},

			rootFolder: null,
			splashScreen: null,

			onReady: function () {
				this.loadDraftRoot();
			},

			loadDraftRoot: function() {
				if (this.options.documentType != null) {
					var me = this;
					var url = Alfresco.constants.PROXY_URI + "lecm/document-type/settings?docType=" + this.options.documentType;
					var callback = {
						success: function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
							me.rootFolder = oResults.nodeRef;
							me.loadForm();
						},
						timeout: 60000
					};
					YAHOO.util.Connect.asyncRequest('GET', url, callback);
				}
			},

			loadForm: function() {
				var me = this;
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						dataObj: {
							htmlid: this.id,
							itemKind:"type",
							itemId: this.options.documentType,
							destination: this.rootFolder,
							mode: "create",
							submitType:"json",
							formId: this.options.formId,
							showSubmitButton:true,
							showCancelButton: true,
							args: JSON.stringify(this.options.args)
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-body");
								container.innerHTML = response.serverResponse.responseText;
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			},

			onFormContentReady: function(layer, args) {
				if (args[1].parentId == this.id) {
					var submitButton = args[1].buttons.submit;
					submitButton.set("label", this.msg("label.save"));

					var cancelButton = args[1].buttons.cancel;
					if (cancelButton) {
						cancelButton.addListener("click", this.onCancelButtonClick, null, this);
					}

					var previewHeight = Dom.getRegion(this.id + "-form-fields").height - 26;
					Dom.setStyle(this.id + "-preview", "height", previewHeight.toString() + "px");
				}
			},

			onBeforeFormRuntimeInit: function(layer, args) {
				args[1].runtime.setAJAXSubmit(true,
					{
						successCallback:
						{
							fn: this.onFormSubmitSuccess,
							scope: this
						},
						failureCallback:
						{
							fn: this.onFormSubmitFailure,
							scope: this
						}
					});
			},

			onFormSubmitSuccess: function (response) {
				var createdDocument = response.json.persistedObject;
				if (this.options.connectionType != null && this.options.connectionIsSystem != null && this.options.parentDocumentNodeRef != null) {
                    var template = "{proxyUri}lecm/documents/connection?connectionType={connectionType}&connectionIsSystem={connectionIsSystem}&fromNodeRef={fromNodeRef}&toNodeRef={toNodeRef}";
                    var url = YAHOO.lang.substitute(template, {
                        proxyUri: Alfresco.constants.PROXY_URI,
                        connectionType: encodeURIComponent(this.options.connectionType),
                        connectionIsSystem: encodeURIComponent(this.options.connectionIsSystem),
                        fromNodeRef: this.options.parentDocumentNodeRef,
                        toNodeRef: createdDocument
                    });
                    this._showSplash();
                    var callback = {
                        success: function(oResponse) {
                            document.location.href = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + createdDocument;
                        },
                        timeout: 60000
                    };
                    YAHOO.util.Connect.asyncRequest('GET', url, callback);
				} else {
					window.location.href = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + createdDocument;
				}
			},

			onFormSubmitFailure: function(response) {
				Alfresco.util.PopupManager.displayPrompt(
					{
						text: Alfresco.util.message("message.failure")
					});
			},

			onCancelButtonClick: function() {
				document.location.href = document.referrer;
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