if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};


(function() {
	YAHOO.util.Event.on(window, "load", function() {
		Alfresco.util.Ajax.request({
			method: "GET",
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/isCurrentEmployeeAbscentToday",
			requestContentType: "application/json",
			responseContentType: "application/json",
			successCallback: {
				fn: function (response) {
					var result = response.json;
					if (result != null) {
						LogicECM.module.WCalendar.Absence.isAbscent = result.isAbscent;
						Absence_ShowControlElements();
					}
				},
				scope: this
			}
		});
	});
	
	function Absence_ShowControlElements() {
		Absence_DrawInstantElement();
		Absence_DrawCancelElement();

		if (LogicECM.module.WCalendar.Absence.isAbscent) {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/AbsenceCancelShowDialog",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null && result.showCancelAbsenceDialog) {
							LogicECM.module.WCalendar.Absence.cancelAbsence();
						}
					},
					scope: this
				}
			});
		}

	}

	function Absence_DrawInstantElement() {
		var htmlNode;
		if (!LogicECM.module.WCalendar.Absence.instantButtonID) {
			var menuElements = [];
			menuElements = YAHOO.util.Dom.getElementsByClassName('yuimenuitemlabel', 'a');
			for (var i = 0; i < menuElements.length; i++) {
				var element = menuElements[i];
				if (element.getAttribute("templateuri") && element.getAttribute("templateuri") == "/instant-absence") {
					LogicECM.module.WCalendar.Absence.instantButtonID = YAHOO.util.Dom.generateId(element);
					element.removeAttribute("templateuri");
					element.removeAttribute("href");
					element.setAttribute("onclick", "LogicECM.module.WCalendar.Absence.newInstantAbsence(this)");
					break;
				}
			}
		}

		htmlNode = YAHOO.util.Dom.get(LogicECM.module.WCalendar.Absence.instantButtonID);

		if (!Alfresco.module.SimpleDialog || LogicECM.module.WCalendar.Absence.isAbscent) {
			parentID = htmlNode.parentNode.id;
			parentNode = new YAHOO.util.Element(parentID);
			parentNode.setStyle("display", "none");
		} else if (Alfresco.module.SimpleDialog && !LogicECM.module.WCalendar.Absence.isAbscent) {
			parentID = htmlNode.parentNode.id;
			parentNode = new YAHOO.util.Element(parentID);
			parentNode.setStyle("display", "block");
		}
	}

	function Absence_DrawCancelElement() {
		var htmlNode;
		if (!LogicECM.module.WCalendar.Absence.cancelButtonID) {
			var menuElements = [];
			menuElements = YAHOO.util.Dom.getElementsByClassName('yuimenuitemlabel', 'a');
			for (var i = 0; i < menuElements.length; i++) {
				var element = menuElements[i];
				if (element.getAttribute("templateuri") && element.getAttribute("templateuri") == "/cancel-absence") {
					LogicECM.module.WCalendar.Absence.cancelButtonID = YAHOO.util.Dom.generateId(element);
					element.removeAttribute("templateuri");
					element.removeAttribute("href");
					element.setAttribute("onclick", "LogicECM.module.WCalendar.Absence.cancelAbsence()");
					break;
				}
			}
		}

		htmlNode = YAHOO.util.Dom.get(LogicECM.module.WCalendar.Absence.cancelButtonID);

		if (!LogicECM.module.WCalendar.Absence.isAbscent) {
			parentID = htmlNode.parentNode.id;
			parentNode = new YAHOO.util.Element(parentID);
			parentNode.setStyle("display", "none");
		} else {
			parentID = htmlNode.parentNode.id;
			parentNode = new YAHOO.util.Element(parentID);
			parentNode.setStyle("display", "block");
		}
	}


	
	LogicECM.module.WCalendar.Absence.cancelAbsence = function Absence_cancelAbsence() {
		Alfresco.util.PopupManager.displayPrompt({
			title: Alfresco.component.Base.prototype.msg("title.absence.cancel-absence"),
			text: Alfresco.component.Base.prototype.msg("message.absence.cancel-absence.confirmation"),
			close: false,
			modal: true,
			buttons: [{
				text: Alfresco.component.Base.prototype.msg("button.yes"),
				handler: function Absence_cancelAbsence_acceptCancel() {
					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/set/endCurrentEmployeeActiveAbsence",
						requestContentType: "application/json",
						responseContentType: "application/json",
						successCallback: {
							fn: function (response) {
								LogicECM.module.WCalendar.Absence.isAbscent = false;
								Absence_DrawInstantElement();
								Absence_DrawCancelElement();
								Alfresco.util.PopupManager.displayMessage({
									text: Alfresco.component.Base.prototype.msg("message.absence.cancel-absence.success")
								});
							},
							scope: this
						}
					});
					this.destroy();
				}
			},
			{
				text: Alfresco.component.Base.prototype.msg("button.no"),
				handler: function Absence_cancelAbsence_denyCancel() {
					this.destroy();
				},
				isDefault: true
			}]
		});
	}

	LogicECM.module.WCalendar.Absence.newInstantAbsence = function Absence_onClickInstantAbsence(node) {

		if (LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER) {
			Absence_showDialogNewInstantAbsence(node);
		} else {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/container",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null) {
							LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = result;
							Absence_showDialogNewInstantAbsence(node);
						}
					},
					scope: this
				}
			});
		}

		function Absence_showDialogNewInstantAbsence(node) {
			var scope = node;

			var destination = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef;
			var itemType = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType;

			var doBeforeDialogShow = function Absence_doBeforeDialogShow(p_form, p_dialog) {
				Alfresco.util.populateHTML(
					[ p_dialog.id + "-form-container_h", Alfresco.component.Base.prototype.msg("label.absence.create-instant-absence.title") ]
					);
			};

			var url = "components/form"
			+ "?itemKind={itemKind}"
			+ "&itemId={itemId}"
			+ "&formId={formId}"
			+ "&destination={destination}"
			+ "&mode={mode}"
			+ "&submitType={submitType}"
			+ "&showCancelButton=true";
			var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
				itemKind: "type",
				itemId: itemType,
				formId: "createNewInstantAbsenceForm",
				destination: destination,
				mode: "create",
				submitType: "json"
			});

			// Using Forms Service, so always create new instance
			var instantAbsenceForm = new Alfresco.module.SimpleDialog(scope.id + "-createNewInstantAbsenceForm");

			instantAbsenceForm.setOptions({
				width: "50em",
				templateUrl: templateUrl,
				destroyOnHide: true,
				doBeforeDialogShow:{
					fn: doBeforeDialogShow,
					scope: this
				},
				doBeforeFormSubmit: {
					fn: function() {
						var htmlNodeEnd = YAHOO.util.Dom.get(scope.id + "-createNewInstantAbsenceForm_prop_lecm-absence_end");
						var htmlNodeUnlimited = YAHOO.util.Dom.get(scope.id + "-createNewInstantAbsenceForm_prop_lecm-absence_unlimited");
						var endDate;
						var htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0]
						htmlNodeBegin.value =  Alfresco.util.toISO8601(new Date());
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
				},
				onSuccess: {
					fn: function InstantAbsence_onSuccess(response) {
						LogicECM.module.WCalendar.Absence.isAbscent = true;
						Absence_DrawInstantElement();
						Absence_DrawCancelElement();
						Alfresco.util.PopupManager.displayMessage({
							text:  Alfresco.component.Base.prototype.msg("message.absence.new-instant.success")
						});
					},
					scope: scope
				},
				onFailure: {
					fn: function InstantAbsence_onFailure(response) {
						Alfresco.util.PopupManager.displayMessage({
							text:  Alfresco.component.Base.prototype.msg("message.absence.new-instant.failure")
						});
					},
					scope: scope
				}
			});
			instantAbsenceForm.show();
		}
	};

	LogicECM.module.WCalendar.Absence.dateIsNotBeforeToday = function Absence_dateIsNotBeforeToday(field, args,  event, form, silent, message) {
		var valid = false;
		var showMessage = false;

		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var htmlNode = YAHOO.util.Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		if (field.value && field.value.length > 10) {
			var dateInField = new Date(Alfresco.util.fromISO8601(field.value));
			dateInField.setHours(23, 59, 59, 0);
			var today = new Date();
			today.setHours(0, 0, 0, 0);

			if (dateInField > today) {
				valid = true;
			} else {
				valid = false;
				showMessage = true;
			}
		}

		//Ругнуться, что дата неправильная
		if (!valid && showMessage && form) {
			form.addError(message, field);
		}

		return valid;
	};
})();
