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

	LogicECM.module.Documents.Edit = function(htmlId)
	{
		LogicECM.module.Documents.Edit.superclass.constructor.call(this, "LogicECM.module.Documents.Edit", htmlId, ["container", "json"]);

		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
		YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.Edit, Alfresco.component.Base,
		{
			options: {
				nodeRef: null,
				formId: null,
				args: {}
			},

			rootFolder: null,

			onReady: function () {
				this.loadForm();
			},

			loadForm: function() {
				if (this.options.nodeRef != null) {
					var me = this;
					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
							dataObj: {
								htmlid: this.id,
								itemKind: "node",
								itemId: this.options.nodeRef,
								mode: "edit",
								submitType: "json",
								formId: this.options.formId,
								showSubmitButton: true,
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
				}
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
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
			},

			onFormSubmitFailure: function(response) {
				var message = Alfresco.util.message("message.failure");
				var regnumberDuplicateRegex = /REGNUMBER_DUPLICATE_EXCEPTION/;

				if (regnumberDuplicateRegex.test(response.serverResponse.responseText)) {
					message = "Документ с указанным регистрационным номером уже существует в системе! Сохранение невозможно.";
				}

				Alfresco.util.PopupManager.displayPrompt(
					{
						text: message
					});
			},

			onCancelButtonClick: function() {
				document.location.href = document.referrer;
			}
		});
})();