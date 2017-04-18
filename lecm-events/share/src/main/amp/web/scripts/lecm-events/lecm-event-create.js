if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function() {

	LogicECM.module.Calendar.Create = function(htmlId) {
		LogicECM.module.Calendar.Create.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.Create, LogicECM.module.Documents.Create);

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.Create.prototype, {
		onFormSubmitSuccessRedirect: function(nodeRef) {
			var reloadCheckbox = Dom.get('document-form-close-and-create-new');
			if (reloadCheckbox && reloadCheckbox.checked) {
				window.location.reload();
			} else if (this.options.backUrl != null) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + decodeURIComponent(this.options.backUrl);
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'event?nodeRef=' + nodeRef;
			}
		},

		onBeforeFormRuntimeInit: function(layer, args) {
			this.runtimeForm = args[1].runtime;
			var submitElement = this.runtimeForm.submitElements[0];
			var originalSubmitFunction = submitElement.submitForm;

			submitElement.submitForm = this.onSubmit.bind(this, originalSubmitFunction, submitElement);

			var actionSubmit = Dom.get(this.id + "-event-action-save");
			if (actionSubmit != null) {
				YAHOO.util.Event.addListener(actionSubmit, "click", this.onSubmit.bind(this, originalSubmitFunction, submitElement));
			}

			var actionCancel = Dom.get(this.id + "-event-action-cancel");
			if (actionCancel != null) {
				YAHOO.util.Event.addListener(actionCancel, "click", this.onCancelButtonClick, null, this);
			}

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
			YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
		},

		onSubmit: function(fn, scope) {
			if (this.runtimeForm.validate()) {
				this._showSplash();

				var form = Dom.get(this.runtimeForm.formId);
				var fromDate = form["prop_lecm-events_from-date"];
				var toDate = form["prop_lecm-events_to-date"];
				var allDay = form["prop_lecm-events_all-day"];
				var location = form["assoc_lecm-events_location-assoc"];
				var members = [];
				var timezoneOffset = new Date().getTimezoneOffset();

				var busyTimeMembersFieldsFromService = [];
				Alfresco.util.Ajax.jsonRequest(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/events/getPropsForFilterShowInCalendar",
						method: "GET",
						successCallback: {
							scope: this,
							fn: function (response) {
								if (response.json && response.json.propsString) {
									busyTimeMembersFieldsFromService = response.json.propsString.split(",");
									busyTimeMembersFieldsFromService = busyTimeMembersFieldsFromService.map(function (field) {
										return 'assoc_' + field.replace(':', '_');
									}, this);
								} else {
									busyTimeMembersFieldsFromService.push("assoc_lecm-events_temp-members-assoc");
								}

								members = [];
								busyTimeMembersFieldsFromService.forEach(function (fieldId) {
									if (form[fieldId] && form[fieldId].value) {
										var membersFromField = form[fieldId].value.split(",");
										members = members.concat(membersFromField);
									}
								}, this);

								Alfresco.util.Ajax.jsonPost({
									url: Alfresco.constants.PROXY_URI + "lecm/events/event/checkAvailable",
									dataObj: {
										"fromDate": fromDate.value,
										"toDate": toDate.value,
										"allDay": allDay.value,
										"clientTimezoneOffset": timezoneOffset,
										"location": location.value,
										"members": members
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

												if (notAvailableEmployees.length > 0) {
													this._hideSplash();

													Alfresco.util.PopupManager.displayPrompt(
														{
															title: this.msg("title.confirm.event.employees.notAvailable"),
															text: this.msg("message.event.employees.notAvailable", notAvailableEmployees),
															buttons: [
																{
																	text: this.msg("button.ok"),
																	handler: function () {
																		this.destroy();
																		if (YAHOO.lang.isFunction(fn) && scope) {
																			fn.call(scope);
																		}
																	}
																},
																{
																	text: this.msg("button.cancel"),
																	handler: function () {
																		this.destroy();
																	},
																	isDefault: true
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
							}
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
