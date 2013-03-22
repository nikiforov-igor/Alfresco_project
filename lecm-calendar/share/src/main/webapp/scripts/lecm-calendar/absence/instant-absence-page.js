if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function() {
	LogicECM.module.WCalendar.Absence.InstantAbsencePage = function(containerId) {
		return LogicECM.module.WCalendar.Absence.InstantAbsencePage.superclass.constructor.call(
				this,
				"LogicECM.module.WCalendar.Absence.InstantAbsencePage",
				containerId,
				["button", "container", "connection", "json", "selector"]);
	};


	YAHOO.lang.extend(LogicECM.module.WCalendar.Absence.InstantAbsencePage, Alfresco.component.Base);

	YAHOO.lang.augmentObject(LogicECM.module.WCalendar.Absence.InstantAbsencePage.prototype, {
		onInstantAbsenceForm: function(result) {
			var contentEl = YAHOO.util.Dom.get(this.id + "-content");
			contentEl.innerHTML = result.serverResponse.responseText;

			var formEl = YAHOO.util.Dom.get(this.id + "-form");
			if (formEl) {
				var form = new Alfresco.forms.Form(this.id + "-form");
				form.setAJAXSubmit(true, {
					successCallback: {
						fn: function InstantAbsence_onSuccess(response) {
							LogicECM.module.WCalendar.Absence.isAbsent = true;
							YAHOO.Bubbling.fire("currentEmployeeAbsenceChanged", {
								isAbsent: true
							});
							Alfresco.util.Ajax.request({
								method: "GET",
								url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/AbsenceCancelShowDialog",
								requestContentType: "application/json",
								responseContentType: "application/json"
							});
							LogicECM.module.WCalendar.Absence.drawInstantElement();
							LogicECM.module.WCalendar.Absence.drawCancelElement();
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.component.Base.prototype.msg("message.absence.new-instant.success")
							});
						},
						scope: this
					},
					failureCallback: {
						fn: function InstantAbsence_onFailure(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.component.Base.prototype.msg("message.absence.new-instant.failure")
							});
						},
						scope: this
					}
				});
				form.doBeforeFormSubmit = {
					fn: function InstantAbsence_doBeforeSubmit() {
						var htmlNodeEnd = YAHOO.util.Dom.get(this.id + "_prop_lecm-absence_end");
						var htmlNodeUnlimited = YAHOO.util.Dom.get(this.id + "_prop_lecm-absence_unlimited");
						var endDate;
						var htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0];
						htmlNodeBegin.value = Alfresco.util.toISO8601(new Date());
						if (htmlNodeUnlimited.checked) {
							var beginDate = Alfresco.util.fromISO8601(htmlNodeBegin.value);
							endDate = new Date(beginDate);
						} else {
							endDate = Alfresco.util.fromISO8601(htmlNodeEnd.value);
						}
						endDate.setHours(23, 59, 59, 0);
						htmlNodeEnd.value = Alfresco.util.toISO8601(endDate);
					},
					scope: this
				};
				form.setSubmitAsJSON(true);
				form.setShowSubmitStateDynamically(true, false);
				form.init();
			}
		},
		onReady: function() {

			Alfresco.logger.info("LogicECM.module.WCalendar.Absence.InstantAbsencePage has been created");

			var destination = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef;
			var itemType = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType;


			var argsObj = {
				htmlid: this.id,
				formId: "createNewInstantAbsenceForm",
				itemKind: "type",
				itemId: itemType,
				mode: "create",
				submitType: "json",
				destination: destination,
				showCancelButton: false,
				showResetButton: false,
				showSubmitButton: true
			};

			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				dataObj: argsObj,
				successCallback: {
					fn: function InstantAbsencePage_onSuccessForm(formResult) {
						if (!LogicECM.module.WCalendar.Absence.defaultReasonNodeRef) {
							Alfresco.util.Ajax.request({
								method: "GET",
								url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/absenceReasonDefault",
								requestContentType: "application/json",
								responseContentType: "application/json",
								successCallback: {
									fn: function(response) {
										var result = response.json;
										if (result != null) {
											LogicECM.module.WCalendar.Absence.defaultReasonNodeRef = result.nodeRef;
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
					fn: function InstantAbsencePage_onFailureForm(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.component.Base.prototype.msg("message.absence.new-instant.failure")
						});
					},
					scope: this
				},
				execScripts: true
			});

			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	}, true);

})();
