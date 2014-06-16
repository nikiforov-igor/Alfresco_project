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
				args: {}
			},

			rootFolder: null,

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
							htmlid: "document-create",
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

								var submitButton = Dom.get("document-create-form-submit");
								submitButton.value = me.msg("label.save");
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			},

			onFormContentReady: function(layer, args) {
					var submitButton = args[1].buttons.submit;
				submitButton.set("label", this.msg("label.save"));

				var cancelButton = args[1].buttons.cancel;
				if (cancelButton)
				{
					cancelButton.addListener("click", this.onCancelButtonClick, null, this);
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
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
			},

			onFormSubmitFailure: function(response) {
				Alfresco.util.PopupManager.displayPrompt(
					{
						text: Alfresco.util.message("message.failure")
					});
			},

			onCancelButtonClick: function() {
				document.location.href = document.referrer;
			}
		});
})();