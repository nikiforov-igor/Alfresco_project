if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function() {

	LogicECM.module.Calendar.Edit = function(htmlId) {
		LogicECM.module.Calendar.Edit.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.Edit, LogicECM.module.Documents.Edit);

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.Edit.prototype, {
		onFormSubmitSuccess: function (response) {
			var nodeRef = response.json.persistedObject;

			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/afterUpdate",
					dataObj: {
						eventNodeRef: nodeRef
					},
					//filter out non relevant events for current view
					successCallback:
					{
						fn: function (o) {
							window.location.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + nodeRef;
						},
						scope: this
					},
					failureMessage: this.msg("load.fail")
				});

		},

		onBeforeFormRuntimeInit: function(layer, args) {
			this.runtimeForm = args[1].runtime;
			var submitElement = this.runtimeForm.submitElements[0];
			var originalSubmitFunction = submitElement.submitForm;

			submitElement.submitForm = this.onSubmit.bind(this, originalSubmitFunction, submitElement);

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

		onSubmit: function(fn, scope) {
			if (this.runtimeForm.validate()) {
				this._showSplash();

				var fromDate = Dom.get(this.runtimeForm.formId)["prop_lecm-events_from-date"];
				var toDate = Dom.get(this.runtimeForm.formId)["prop_lecm-events_to-date"];
				var allDay = Dom.get(this.runtimeForm.formId)["prop_lecm-events_all-day"];
				var location = Dom.get(this.runtimeForm.formId)["assoc_lecm-events_location-assoc"];

				Alfresco.util.Ajax.jsonPost({
					url: Alfresco.constants.PROXY_URI + "lecm/events/event/checkAvailable",
					dataObj: {
						"fromDate": fromDate.value,
						"toDate": toDate.value,
						"allDay": allDay.value,
						"location": location.value,
						"event": this.options.nodeRef
					},
					successCallback: {
						fn: function refreshSuccess(response) {
							var json = response.json;

							if (json.locationAvailable) {
								var notAvailableEmployees = "";
								if (json.members != null) {
									for (var i = 0; i < json.members.length; i++) {
										if (!json.members[i].available) {
											if (notAvailableEmployees.length > 0) {
												notAvailableEmployees += ", "
											}
											notAvailableEmployees += json.members[i].name;
										}
									}
								}

								if (notAvailableEmployees.length > 0)  {
									this._hideSplash();

									Alfresco.util.PopupManager.displayPrompt(
										{
											title: this.msg("title.confirm.event.employees.notAvailable"),
											text: this.msg("message.event.employees.notAvailable", notAvailableEmployees),
											buttons:[
												{
													text:this.msg("button.ok"),
													handler:function () {
														this.destroy();
														if (YAHOO.lang.isFunction(fn) && scope) {
															fn.call(scope);
														}
													}
												},
												{
													text:this.msg("button.cancel"),
													handler:function () {
														this.destroy();
													},
													isDefault:true
												}
											]
										});

								} else {
									if (YAHOO.lang.isFunction(fn) && scope) {
										fn.call(scope);
									}
								}
							} else {
								this._hideSplash();
								Alfresco.util.PopupManager.displayMessage(
									{
										text: this.msg("message.event.location.notAvailable")
									});
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function refreshFailure(response) {
							console.log(response);
						},
						scope: this
					}
				});
			} else {
				if (YAHOO.lang.isFunction(fn) && scope) {
					fn.call(scope);
				}
			}
		}
	}, true);
})();