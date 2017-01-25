/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
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
				mayAdd: null,
				mayView: null,
				hasStatemachine: null,
				args: {},
                higlightedFields: []
			},

			rootFolder: null,
            runtimeForm: null,

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
								mayView: this.options.mayView,
								mayAdd: this.options.mayAdd,
								hasStatemachine: this.options.hasStatemachine,
								showSubmitButton: true,
								showCancelButton: true,
								showCaption: false,
								args: JSON.stringify(this.options.args),
                                fields: JSON.stringify(this.options.higlightedFields)
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
                this.runtimeForm = args[1].runtime;
                var submitFunction = this.runtimeForm.submitElements[0].submitForm;
                var me = this;
                this.runtimeForm.submitElements[0].submitForm = function() {
                    if (me.runtimeForm.validate()) {
                        me._showSplash();
                    }
                    submitFunction.call(me.runtimeForm.submitElements[0]);
                };

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
				YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
			},

			onFormSubmitSuccess: function (response) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
			},

			onFormSubmitFailure: function(response) {
                for (var index in this.runtimeForm.submitElements) {
                    var button = this.runtimeForm.submitElements[index];
                    button.set("disabled", false);
                }
                this._hideSplash();
                var message = Alfresco.util.message("message.failure");
				var regnumberDuplicateRegex = /REGNUMBER_DUPLICATE_EXCEPTION/;

				if (regnumberDuplicateRegex.test(response.serverResponse.responseText)) {
					message = this.msg("msg.regnum_exist");
				}

				Alfresco.util.PopupManager.displayPrompt(
					{
						text: message
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
                YAHOO.lang.later(2000, this.splashScreen, this.splashScreen.destroyWithAnimationsStop);
            }
		});
})();