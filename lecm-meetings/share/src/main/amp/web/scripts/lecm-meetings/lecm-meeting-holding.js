if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Meetengs = LogicECM.module.Meetengs || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Meetengs.Holding = function(htmlId) {
		LogicECM.module.Meetengs.Holding.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Meetengs.Holding, LogicECM.module.Documents.Edit);

	YAHOO.lang.augmentObject(LogicECM.module.Meetengs.Holding.prototype, {
		submitElements: [],

		onReady: function () {
			var actionSubmit = Dom.get(this.id + "-event-action-save");
			if (actionSubmit != null) {
				YAHOO.util.Event.addListener(actionSubmit, "click", this.onSubmit, null, this);
			}

			var actionCancel = Dom.get(this.id + "-event-action-cancel");
			if (actionCancel != null) {
				YAHOO.util.Event.addListener(actionCancel, "click", this.onCancelButtonClick, null, this);
			}

			this.loadForm();
			this.loadItems();
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
							args: JSON.stringify(this.options.args),
							fields: JSON.stringify(this.options.higlightedFields)
						},
						successCallback: {
							fn: function (response) {
								var container = Dom.get(me.id + "-body");
								Dom.setStyle(me.id + "-form-buttons", "display", "none");
								container.innerHTML = response.serverResponse.responseText;
							}
						},
						failureMessage: "message.failure",
						execScripts: true
					});
			}
		},

		onBeforeFormRuntimeInit: function(layer, args) {
			//this.runtimeForm = args[1].runtime;
			this.submitElements.push(args[1].runtime.submitElements[0]);

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
			//YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
		},

		onSubmit: function() {
			for (var i = 0; i < this.submitElements.length; i++) {
		   	    this.submitElements[i].submitForm();
			}
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + this.options.nodeRef;
		},

		onFormSubmitSuccess: function (response) {

		},

		loadItems: function() {
			var me = this;
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/meeting/getHoldingItems?meetingRef=" + this.options.nodeRef,
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null) {
							if (result.items != null && result.items.length > 0) {
								for (var i = 0; i < result.items.length; i++) {
									var item = result.items[i];

									Alfresco.util.Ajax.request(
										{
											url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
											dataObj: {
												htmlid: me.id + item.nodeRef.replace(/\//g,"_"),
												itemKind: "node",
												itemId: item.nodeRef,
												mode: "edit",
												submitType: "json",
												formId: "holding",
												showSubmitButton: true,
												showCancelButton: true
											},
											successCallback: {
												fn: function (response) {
													var container = Dom.get(me.id + "-items");
													container.innerHTML += response.serverResponse.responseText;
												}
											},
											failureMessage: "message.failure",
											execScripts: true
										});
								}
							}
						}
					},
					scope: this
				}
			});
		}
	}, true);
})();