if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function() {
	var Dom = YAHOO.util.Dom,
			Absence = LogicECM.module.WCalendar.Absence;

	Absence.InstantAbsencePage = function(containerId) {
		return Absence.InstantAbsencePage.superclass.constructor.call(
				this, "LogicECM.module.WCalendar.Absence.InstantAbsencePage",
				containerId, ["button", "container", "connection", "json", "selector"]);
	};


	YAHOO.lang.extend(Absence.InstantAbsencePage, Alfresco.component.Base);

	YAHOO.lang.augmentObject(Absence.InstantAbsencePage.prototype, {
		onInstantAbsenceForm: function(result) {
			var contentNode = Dom.get(this.id + "-content"),
					formNode, form;

			contentNode.innerHTML = result.serverResponse.responseText;
			formNode = Dom.get(this.id + "-form");

			if (formNode) {
				form = new Alfresco.forms.Form(this.id + "-form");
				form.setAJAXSubmit(true, {
					successCallback: {
						fn: function(response) {
							Absence.isAbsent = true;
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
						fn: function(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.absence.new-instant.failure")
							});
						},
						scope: this
					}
				});
				form.doBeforeFormSubmit = {
					fn: function() {
						var htmlNodeEnd = Dom.get(this.id + "_prop_lecm-absence_end"),
								htmlNodeUnlimited = Dom.get(this.id + "_prop_lecm-absence_unlimited"),
								htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0],
								endDate, beginDate = new Date();

						htmlNodeBegin.value = Alfresco.util.toISO8601(beginDate);
						if (htmlNodeUnlimited.checked) {
							endDate = new Date(beginDate);
						} else {
							endDate = Alfresco.util.fromISO8601(htmlNodeEnd.value);
						}
						//endDate.setHours(23, 59, 59, 0);
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
						showSubmitButton: true
					};

			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				dataObj: argsObj,
				successCallback: {
					fn: function(formResult) {
						if (!Absence.defaultReasonNodeRef) {
							Alfresco.util.Ajax.request({
								method: "GET",
								url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/absenceReasonDefault",
								requestContentType: "application/json",
								responseContentType: "application/json",
								successCallback: {
									fn: function(response) {
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
					fn: function(response) {
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
	}, true);

})();
