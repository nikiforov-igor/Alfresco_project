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
		YAHOO.Bubbling.on("meetingHoldingChangeTechnicalMembers", this.onChangeTechnicalMembers, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Meetengs.Holding, LogicECM.module.Documents.Edit);

	YAHOO.lang.augmentObject(LogicECM.module.Meetengs.Holding.prototype, {
		submitElements: [],
		forms: [],

		HOLDING_MEETING: "holdingMeeting",
		ITEM_FORM_PREFIX: "mhi-",

		onReady: function () {
			var actionSave = Dom.get(this.id + "-event-action-save");
			if (actionSave != null) {
				YAHOO.util.Event.addListener(actionSave, "click", this.saveForm, true, this);
			}

			var actionFinish = Dom.get(this.id + "-event-action-finish");
			if (actionFinish != null) {
				YAHOO.util.Event.addListener(actionFinish, "click", this.onSubmit, null, this);
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
							fields: JSON.stringify(this.options.higlightedFields),
							showCaption: false
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
			var formRuntime = args[1].runtime;
			this.forms.push(new LogicECM.module.Base.FormWrapper(formRuntime));

			var submitElement = formRuntime.submitElements[0];
			this.submitElements.push(submitElement);

			args[1].runtime.setAJAXSubmit(true);
		},

		saveForm: function(immediate, isUserAction) {
			for (var index = 0, len = this.forms.length; index < len; index++) {
				var form = this.forms[index];
                if (form && form.runtime.formId !== this.HOLDING_MEETING + "-form" && form.isDirty()) {
                    form.submit(immediate);
                    if (!isUserAction) {
                        break;
					}
			}}
		},

		onSubmit: function() {
			this.saveDates(true);
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
						htmlid: this.ITEM_FORM_PREFIX + nodeRef.replace('workspace://SpacesStore/', '_'),
						itemKind: "node",
						itemId: nodeRef,
						mode: "edit",
						submitType: "json",
						formId: "holding",
						showSubmitButton: true,
						showCancelButton: true,
						showCaption: false
					},
					successCallback: {
						fn: function (response) {
							var container = Dom.get(me.id + "-items");
							var div = document.createElement('div');
							div.innerHTML = response.serverResponse.responseText;
							container.appendChild(div);
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
			if (nodeRef) {
				var me = this;
				Alfresco.util.PopupManager.displayPrompt(
					{

						title: me.msg("message.confirm.delete.title"),
						text: me.msg("message.delete.confirm.delete.point"),
						buttons:[
							{
								text: me.msg("button.delete"),
								handler:function DataGridActions__onActionDelete_delete() {
									this.destroy();
									me.deleteItem(nodeRef);
								}
							},
							{
								text:me.msg("button.cancel"),
								handler:function DataGridActions__onActionDelete_cancel() {
									this.destroy();
								},
								isDefault:true
							}
						]
					});
			}
		},

		deleteItem: function(nodeRef) {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/meeting/removeItem",
				dataObj: {
					nodeRef: nodeRef
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						var itemBlock = Dom.get(this.ITEM_FORM_PREFIX + nodeRef.replace('workspace://SpacesStore/', '_') + "-form-container");
						if (itemBlock) {
							itemBlock.parentNode.removeChild(itemBlock);
						}
					}
				}
			});
		},

		onChangeTechnicalMembers: function () {
			for (var i = 0; i < this.submitElements.length; i++) {
				var formId = this.submitElements[i].getForm().id.replace("-form", "");
				LogicECM.module.Base.Util.reInitializeControl(formId, "lecm-meetings-ts:holding-reporter-assoc", {});
			}

		},

		saveDates: function (isUserAction) {
			this.saveForm(true, isUserAction);

			var arguments = {};
			for (var i = 0; i < this.submitElements.length; i++) {
				if (this.submitElements[i].getForm()
					&& this.submitElements[i].getForm().id != (this.HOLDING_MEETING + "-form")) {
					var form = this.submitElements[i].getForm();
					if (form.id.indexOf(this.ITEM_FORM_PREFIX) < 0) {
						var propFromDate = form["prop_lecm-events_from-date"];
						if (propFromDate) {
							arguments.from_date = propFromDate.value;
						} else {
							arguments.from_date = Alfresco.util.toISO8601(new Date(), {"milliseconds": false});
						}
						var propToDate = form["prop_lecm-events_to-date"];
						if (propToDate) {
							arguments.to_date = propToDate.value;
						} else {
							arguments.to_date = Alfresco.util.toISO8601(new Date(), {"milliseconds": false});
						}
					}
				}
			}

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form';
			var templateRequestParams = {
				itemKind: 'node',
				itemId: this.options.nodeRef,
				nodeRef: this.options.nodeRef,
				mode: 'edit',
				submitType: 'json',
				formId: 'holding-accept',
				args: JSON.stringify(arguments),
				showCancelButton: true,
				showCaption: false
			};

			var dialog = new Alfresco.module.SimpleDialog(this.HOLDING_MEETING).setOptions({
				width: '65em',
				templateUrl: templateUrl,
				templateRequestParams: templateRequestParams,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function (p_form, p_dialog) {
						p_dialog.dialog.setHeader(this.msg('label.workflow.holdingMeeting'));

						var contId = p_dialog.id + '-form-container';
						Dom.addClass(contId, 'metadata-form-edit');
						Dom.addClass(contId, 'no-form-type');
						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				doBeforeFormSubmit: {
					scope: this,
					fn: function () {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('label.loading'),
							spanClass: 'wait',
							displayTime: 0
						});
					}
				},
				onSuccess: {
					scope: this,
					fn: function () {
						window.location.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + this.options.nodeRef;
					}
				}
			});
			LogicECM.module.Base.Util.registerDialog(dialog);
			dialog.show();
		}
	}, true);

})();