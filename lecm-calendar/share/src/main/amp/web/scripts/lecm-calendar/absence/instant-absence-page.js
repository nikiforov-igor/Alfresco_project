if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Absence = LogicECM.module.WCalendar.Absence;

	Absence.InstantAbsencePage = function (containerId) {
		Absence.InstantAbsencePage.superclass.constructor.call(
			this, "LogicECM.module.WCalendar.Absence.InstantAbsencePage",
			containerId, ["button", "container", "connection", "json", "selector"]);

		YAHOO.Bubbling.on("currentEmployeeAbsenceChanged", this.onCurrentEmployeeAbsenceChanged, this);

		return this;
	};


	YAHOO.lang.extend(Absence.InstantAbsencePage, Alfresco.component.Base);

	YAHOO.lang.augmentObject(Absence.InstantAbsencePage.prototype, {
		isAbsent: null,

		onReady: function () {
			this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancelButton", this.showCancelAbsenceDialog.bind(this));

			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/isCurrentEmployeeAbsentToday",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null) {
							this.isAbsent = result.isAbsent;
							this.draw();
						}
					},
					scope: this
				}
			});
		},

		onInstantAbsenceForm: function (result) {
			var contentNode = Dom.get(this.id + "-content"),
				formNode, form;

			contentNode.innerHTML = result.serverResponse.responseText;
			formNode = Dom.get(this.id + "-form");

			if (formNode) {
				form = new Alfresco.forms.Form(this.id + "-form");
				form.setAJAXSubmit(true, {
					successCallback: {
						fn: function (response) {
							YAHOO.Bubbling.fire("currentEmployeeAbsenceChanged", {
								isAbsent: true
							});
							Alfresco.util.Ajax.request({
								method: "GET",
								url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/AbsenceCancelShowDialog",
								requestContentType: "application/json",
								responseContentType: "application/json"
							});

							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.absence.new-instant.success")
							});
						},
						scope: this
					},
					failureCallback: {
						fn: function (response) {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.absence.new-instant.failure")
							});
						},
						scope: this
					}
				});
				form.doBeforeFormSubmit = {
					fn: function () {
						var htmlNodeEnd = Dom.get(this.id + "_prop_lecm-absence_end"),
							htmlNodeUnlimited = Dom.get(this.id + "_prop_lecm-absence_unlimited"),
							htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0],
							beginDate = LogicECM.module.Base.Util.dateToUTC0(new Date()),
							endDate = htmlNodeUnlimited.checked ? beginDate : LogicECM.module.Base.Util.dateToUTC0(Alfresco.util.fromISO8601(htmlNodeEnd.value));

						htmlNodeBegin.value = Alfresco.util.toISO8601(beginDate);
						htmlNodeEnd.value = Alfresco.util.toISO8601(endDate);
					},
					scope: this
				};
				form.setSubmitAsJSON(true);
				form.setShowSubmitStateDynamically(true, false);
				form.init();
			}
		},
		draw: function () {
			if (this.isAbsent) {
				Dom.setStyle(this.id + "-content", "display", "none");
				Dom.setStyle(this.id + "-content-cancel", "display", "block");
			} else {
				Dom.get(this.id + "-content").innerHTML = "";
				Dom.setStyle(this.id + "-content", "display", "block");
				Dom.setStyle(this.id + "-content-cancel", "display", "none");

				var destination = Absence.ABSENCE_CONTAINER.nodeRef,
					itemType = Absence.ABSENCE_CONTAINER.itemType,
					argsObj = {
						htmlid: this.id,
						formId: "createNewInstantAbsenceForm",
						itemKind: "type",
						itemId: itemType,
						mode: "create",
						submitType: "json",
						destination: destination,
						showCancelButton: false,
						showResetButton: false,
						showSubmitButton: true,
						showCaption: false
					};

				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
					dataObj: argsObj,
					successCallback: {
						fn: function (formResult) {
							if (!Absence.defaultReasonNodeRef) {
								Alfresco.util.Ajax.request({
									method: "GET",
									url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/absenceReasonDefault",
									requestContentType: "application/json",
									responseContentType: "application/json",
									successCallback: {
										fn: function (response) {
											var result = response.json;
											if (result != null) {
												Absence.defaultReasonNodeRef = result.nodeRef;
												this.onInstantAbsenceForm(formResult);
											}
										},
										scope: this
									}
								});
							} else {
								this.onInstantAbsenceForm(formResult);
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function (response) {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.absence.new-instant.failure")
							});
						},
						scope: this
					},
					execScripts: true
				});

				Dom.setStyle(this.id + "-body", "visibility", "visible");
			}

		},

		showCancelAbsenceDialog: function () {
			var me = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: this.msg("title.absence.cancel-absence"),
				text: this.msg("message.absence.cancel-absence.confirmation"),
				close: false,
				modal: true,
				buttons: [
					{
						text: this.msg("button.yes"),
						handler: function () {
							me.acceptCancel();
							this.destroy();
						}
					},
					{
						text: this.msg("button.no"),
						handler: function () {
							this.destroy();
						},
						isDefault: true
					}
				]
			});
		},

		acceptCancel: function () {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/set/endCurrentEmployeeActiveAbsence",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function (response) {
						Absence.isAbsent = false;
						YAHOO.Bubbling.fire("currentEmployeeAbsenceChanged", {
							isAbsent: false
						});
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.absence.cancel-absence.success")
						});
					},
					scope: this
				}
			});
		},

		onCurrentEmployeeAbsenceChanged: function (layer, args) {
			this.isAbsent = args[1].isAbsent;
			this.draw();
		}
	}, true);

})();
