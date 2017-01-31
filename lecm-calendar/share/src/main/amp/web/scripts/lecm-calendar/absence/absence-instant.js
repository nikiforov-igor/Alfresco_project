if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};


(function() {

	var Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event,
			Deferred = Alfresco.util.Deferred,
			Bubbling = YAHOO.Bubbling,
			Element = YAHOO.util.Element,
			Absence = LogicECM.module.WCalendar.Absence, instantAbsenceComponent;

	Absence.Instant = function(containerId) {
		return  Absence.Instant.superclass.constructor.call(
				this, "LogicECM.module.WCalendar.Absence.Instant", containerId, ["connection", "json", "selector"]);
	};

	YAHOO.lang.extend(Absence.Instant, Alfresco.component.Base, {
		instantButtonNode: null,
		cancelButtonNode: null,
		instantAbsencePrerequisites: null,
		showCancellationDialogPrerequisites: null,
		onReady: function() {
			this.showCancellationDialogPrerequisites = new Deferred(["isAbsent", "showCancellationPropmt"],
					{
						fn: this.showCancelAbsenceDialog,
						scope: this
					});

			Bubbling.on("currentEmployeeAbsenceChanged", this.onCurrentEmployeeAbsenceChanged, this);
			this.checkEmployeeAbsence();
			this.checkCancellationPropmt();
		},
		onCurrentEmployeeAbsenceChanged: function(layer, args) {
			this.drawInstantElement();
			this.drawCancelElement();
		},
		checkEmployeeAbsence: function() {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/isCurrentEmployeeAbsentToday",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function(response) {
						var result = response.json;
						if (result != null) {
							Absence.isAbsent = result.isAbsent;
							Bubbling.fire("currentEmployeeAbsenceChanged", {
								isAbsent: result.isAbsent
							});
							if (Absence.isAbsent) {
								this.fulfilShowCancellationDialogPrerequisites("isAbsent");
							} else {
								this.showCancellationDialogPrerequisites.expire();
							}
						}
					},
					scope: this
				}
			});
		},
		checkCancellationPropmt: function() {
			if (Absence.isAbsent || Absence.isAbsent === null) {
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/AbsenceCancelShowDialog",
					requestContentType: "application/json",
					responseContentType: "application/json",
					successCallback: {
						fn: function(response) {
							var result = response.json;
							if (result != null && result.showCancelAbsenceDialog) {
								this.fulfilShowCancellationDialogPrerequisites("showCancellationPropmt");
							} else {
								this.showCancellationDialogPrerequisites.expire();
							}
						},
						scope: this
					}
				});
			}

		},
		drawInstantElement: function() {
			var menuElements, parentID, parentNode;
			if (!this.instantButtonNode) {
				menuElements = Dom.getElementsByClassName('yuimenuitemlabel', 'a');
				for (var i = 0; i < menuElements.length; i++) {
					var element = menuElements[i];
					if (element.getAttribute("templateuri") && element.getAttribute("templateuri") == "/instant-absence") {
						Dom.generateId(element);
						element.removeAttribute("templateuri");
						element.removeAttribute("href");
						Event.on(element, "click", this.newInstantAbsence, this, true);
						this.instantButtonNode = element;
						break;
					}
				}
			}

			if (Absence.isAbsent) {
				parentID = this.instantButtonNode.parentNode.id;
				parentNode = new Element(parentID);
				parentNode.setStyle("display", "none");
			} else {
				parentID = this.instantButtonNode.parentNode.id;
				parentNode = new Element(parentID);
				parentNode.setStyle("display", "block");
			}
		},
		drawCancelElement: function() {
			var menuElements, parentID, parentNode;
			if (!this.cancelButtonNode) {
				menuElements = Dom.getElementsByClassName('yuimenuitemlabel', 'a');
				for (var i = 0; i < menuElements.length; i++) {
					var element = menuElements[i];
					if (element.getAttribute("templateuri") && element.getAttribute("templateuri") == "/cancel-absence") {
						Dom.generateId(element);
						element.removeAttribute("templateuri");
						element.removeAttribute("href");
						Event.on(element, "click", this.showCancelAbsenceDialog, this, true);
						this.cancelButtonNode = element;
						break;
					}
				}
			}

			if (!Absence.isAbsent) {
				parentID = this.cancelButtonNode.parentNode.id;
				parentNode = new Element(parentID);
				parentNode.setStyle("display", "none");
			} else {
				parentID = this.cancelButtonNode.parentNode.id;
				parentNode = new Element(parentID);
				parentNode.setStyle("display", "block");
			}
		},
		showCancelAbsenceDialog: function() {
			var me = this;
			Alfresco.util.PopupManager.displayPrompt({
				title: this.msg("title.absence.cancel-absence"),
				text: this.msg("message.absence.cancel-absence.confirmation"),
				close: false,
				modal: true,
				buttons: [
					{
						text: this.msg("button.yes"),
						handler: function() {
							me.acceptCancel();
							this.destroy();
						}
					},
					{
						text: this.msg("button.no"),
						handler: function() {
							this.destroy();
						},
						isDefault: true
					}]
			});
		},
		acceptCancel: function() {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/set/endCurrentEmployeeActiveAbsence",
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function(response) {
						Absence.isAbsent = false;
						Bubbling.fire("currentEmployeeAbsenceChanged", {
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
		getAbsenceContainer: function() {
			if (!LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER) {
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/container",
					requestContentType: "application/json",
					responseContentType: "application/json",
					successCallback: {
						fn: function(response) {
							var result = response.json;
							if (result != null) {
								LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = result;
								this.fulfilInstantAbsencePrerequisites("absenceContainer");
							}
						},
						scope: this
					}
				});
			} else {
				this.fulfilInstantAbsencePrerequisites("absenceContainer");
			}
		},
		getDefaultAbsenceReason: function() {
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
								this.fulfilInstantAbsencePrerequisites("defaultReason");
							}
						},
						scope: this
					}
				});
			} else {
				this.fulfilInstantAbsencePrerequisites("defaultReason");
			}
		},
		newInstantAbsence: function() {
			// если отсутствие каких-либо скриптов будет мешать созданию диалога, их можно добавить сюда
			if (!Alfresco.module.SimpleDialog || !Alfresco.FormUI) {
				window.location.href = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "instant-absence";

			} else {
				this.instantAbsencePrerequisites = new Deferred(["absenceContainer", "defaultReason"],
						{
							fn: this.newInstantAbsenceDialog,
							scope: this
						});
				this.getAbsenceContainer();
				this.getDefaultAbsenceReason();
			}
		},
		newInstantAbsenceDialog: function() {
			var destination = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef,
					itemType = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType,
					instantAbsenceForm = new Alfresco.module.SimpleDialog(this.instantButtonNode.id + "-createNewInstantAbsenceForm"),
					url = "lecm/components/form" +
					"?itemKind={itemKind}" +
					"&itemId={itemId}" +
					"&formId={formId}" +
					"&destination={destination}" +
					"&mode={mode}" +
					"&submitType={submitType}" +
					"&showCancelButton=true" +
					"&showCaption=false",
					templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
						itemKind: "type",
						itemId: itemType,
						formId: "createNewInstantAbsenceForm",
						destination: destination,
						mode: "create",
						submitType: "json"
					});

			instantAbsenceForm.setOptions({
				width: "60em",
				templateUrl: templateUrl,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(this.msg("label.absence.create-instant-absence.title"));
					},
					scope: this
				},
				doBeforeFormSubmit: {
					fn: function() {
						var htmlNodeEnd = Dom.get(this.instantButtonNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_end"),
								htmlNodeUnlimited = Dom.get(this.instantButtonNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_unlimited"),
								htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0],
								beginDate = new Date(), endDate;
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
				},
				onSuccess: {
					fn: function(response) {
						Absence.isAbsent = true;
						Bubbling.fire("currentEmployeeAbsenceChanged", {
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
				onFailure: {
					fn: function(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.absence.new-instant.failure")
						});
					},
					scope: this
				}
			});
			instantAbsenceForm.show();
		},
		fulfilInstantAbsencePrerequisites: function(prerequisite) {
			if (this.instantAbsencePrerequisites) {
				this.instantAbsencePrerequisites.fulfil(prerequisite);
			}
		},
		fulfilShowCancellationDialogPrerequisites: function(prerequisite) {
			if (this.showCancellationDialogPrerequisites) {
				this.showCancellationDialogPrerequisites.fulfil(prerequisite);
			}
		}

	});

	// Валидаторы для форм

	// Валидатор даты окончания отсутствия
	Absence.dateIsNotBeforeToday = function(field, args, event, form, silent, message) {
		var valid = false,
				showMessage = false,
				htmlNode, dateInField, today;

		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		htmlNode = Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		if (field.value && field.value.length > 10) {
			dateInField = new Date(Alfresco.util.fromISO8601(field.value));
			dateInField.setHours(23, 59, 59, 0);
			today = new Date();
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

	// Валидатор причины отсутствия. Устанавливает причину отсутствия по умолчанию
	Absence.instantAbsenceReasonValidation = function(field, args, event, form, silent, message) {
		var result = true, htmlNodeReasonSelect;
		if (!field.value) {
			if (Absence.defaultReasonNodeRef) {
				htmlNodeReasonSelect = Dom.get(field.id + "-added");
				htmlNodeReasonSelect.value = Absence.defaultReasonNodeRef;
				field.value = Absence.defaultReasonNodeRef;
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	};

	instantAbsenceComponent = new LogicECM.module.WCalendar.Absence.Instant(null);
	Event.onDOMReady(instantAbsenceComponent.onReady, instantAbsenceComponent, true);

})();
