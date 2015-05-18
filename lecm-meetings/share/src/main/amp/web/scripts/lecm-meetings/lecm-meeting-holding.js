if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Meetengs = LogicECM.module.Meetengs || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Meetengs.Holding = function(htmlId) {
		LogicECM.module.Meetengs.Holding.superclass.constructor.call(this, htmlId);
		YAHOO.Bubbling.on("meetingHoldingRemoveItem", this.onRemoveItem, this);
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

			Alfresco.util.createYUIButton(this, "create-new-item-button", this.onCreateItem);

			var me = this;
			setInterval(function() {
				me.saveForm();
			}, 15000);
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
		},

		saveForm: function() {
			for (var i = 0; i < this.submitElements.length; i++) {
				this.submitElements[i].submitForm();
			}
		},

		onSubmit: function() {
			for (var i = 0; i < this.submitElements.length; i++) {
				var form = this.submitElements[i].getForm();
				if (form != null) {
					var propFinished = form["prop_lecm-meetings_finished"];
					if (propFinished != null) {
						propFinished.value = true;
					}
				}
			}

			this.saveForm();
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
									me.loadItem(item.nodeRef);
								}
							}
						}
					},
					scope: this
				}
			});
		},

		loadItem: function(nodeRef) {
			var me = this;
			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
					dataObj: {
						htmlid: me.id + nodeRef.replace(/\//g,"_"),
						itemKind: "node",
						itemId: nodeRef,
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
		},

		onCreateItem: function() {
			var me = this;
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/meeting/createNewItem?meetingRef=" + this.options.nodeRef,
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null && result.nodeRef != null) {
							me.loadItem(result.nodeRef);
						}
					},
					scope: this
				}
			});
		},

		onRemoveItem: function(layer, args) {
			var nodeRef = args[1].nodeRef;
			if (nodeRef != null) {
				var me = this;
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/meeting/removeItem?nodeRef=" + nodeRef,
					successCallback: {
						fn: function (response) {
							var itemBlock = Dom.get(me.id + nodeRef.replace(/\//g,"_") + "-form-container");
							if (itemBlock != null) {
								itemBlock.parentNode.removeChild(itemBlock);
							}
						},
						scope: this
					}
				});


			}
		}
	}, true);
})();