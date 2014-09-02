define(["dojo/_base/declare",
        "dijit/MenuItem",
        "alfresco/core/Core",
        "alfresco/menus/_AlfMenuItemMixin"], function(declare, MenuItem, AlfCore, _AlfMenuItemMixin) {

    if (typeof LogicECM == "undefined" || !LogicECM) {
        window.LogicECM = {};
    }

    window.LogicECM.module = LogicECM.module || {};
    window.LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
    window.LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

    // Валидаторы для форм

    // Валидатор даты окончания отсутствия
    LogicECM.module.WCalendar.Absence.dateIsNotBeforeToday = function(field, args, event, form, silent, message) {
        var valid = false,
            showMessage = false,
            htmlNode, dateInField, today;

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

        return valid;
    };

    var Absence = LogicECM.module.WCalendar.Absence;

    // Валидатор причины отсутствия. Устанавливает причину отсутствия по умолчанию
    LogicECM.module.WCalendar.Absence.instantAbsenceReasonValidation = function(field, args, event, form, silent, message) {
        var result = true,
            htmlNodeReasonSelect;
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

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Deferred = Alfresco.util.Deferred,
        Bubbling = YAHOO.Bubbling,
        Element = YAHOO.util.Element;

    return declare([MenuItem, AlfCore, _AlfMenuItemMixin], {

        instantButtonNode: null,

        cancelButtonNode: null,

        instantAbsencePrerequisites: null,

        showCancellationDialogPrerequisites: null,

        defaultReasonNodeRef: null,

        isAbsent: null,

        onCurrentEmployeeAbsenceChanged: function(layer, args) {
	        this.isAbsent = args[1].isAbsent;
            if (this.isAbsent) {
                this.textDirNode.innerHTML = "Отменить отсутствие";
                this.onClick = this.showCancelAbsenceDialog;
            } else {
                this.textDirNode.innerHTML = "Меня нет в офисе";
                this.onClick = this.newInstantAbsence;
            }
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
                            this.isAbsent = result.isAbsent;
                            Bubbling.fire("currentEmployeeAbsenceChanged", {
                                isAbsent: result.isAbsent
                            });
                            if (this.isAbsent) {
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
            if (this.isAbsent || this.isAbsent === null) {
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

        showCancelAbsenceDialog: function(evt) {
            var me = this;
            Alfresco.util.PopupManager.displayPrompt({
                title: "Отменить отсутствие",
                text: "В системе отмечено Ваше отсутствие. Вы хотите его отменить? Делегирования в этом случае также будут отменены.",
                close: false,
                modal: true,
                buttons: [{
                    text: 'Да',
                    handler: function() {
                        me.acceptCancel();
                        this.destroy();
                    }
                }, {
                    text: 'Нет',
                    handler: function() {
                        this.destroy();
                    },
                    isDefault: true
                }]
            });
            evt.preventDefault();
        },

        acceptCancel: function() {
            Alfresco.util.Ajax.request({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/set/endCurrentEmployeeActiveAbsence",
                requestContentType: "application/json",
                responseContentType: "application/json",
                successCallback: {
                    fn: function(response) {
                        this.isAbsent = false;
                        Bubbling.fire("currentEmployeeAbsenceChanged", {
                            isAbsent: false
                        });
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Отсутствие отменено"
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
            if (!this.defaultReasonNodeRef) {
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

        newInstantAbsence: function(evt) {
            // если отсутствие каких-либо скриптов будет мешать созданию диалога, их можно добавить сюда
            if (!Alfresco.module.SimpleDialog || !Alfresco.FormUI) {
                window.location.href = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "my-profile?path=" + encodeURI("Мое отсутствие/Меня нет в офисе");

            } else {
                this.instantAbsencePrerequisites = new Deferred(["absenceContainer", "defaultReason"], {
                    fn: this.newInstantAbsenceDialog,
                    scope: this
                });
                this.getAbsenceContainer();
                this.getDefaultAbsenceReason();
            }
            evt.preventDefault();
        },

        newInstantAbsenceDialog: function() {
            var destination = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.nodeRef,
                itemType = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER.itemType,
                instantAbsenceForm = new Alfresco.module.SimpleDialog(this.textDirNode.id + "-createNewInstantAbsenceForm"),
                url = "lecm/components/form" +
                    "?itemKind={itemKind}" +
                    "&itemId={itemId}" +
                    "&formId={formId}" +
                    "&destination={destination}" +
                    "&mode={mode}" +
                    "&submitType={submitType}" +
                    "&showCancelButton=true",
                templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
                    itemKind: "type",
                    itemId: itemType,
                    formId: "createNewInstantAbsenceForm",
                    destination: destination,
                    mode: "create",
                    submitType: "json"
                });

            instantAbsenceForm.setOptions({
                width: "50em",
                templateUrl: templateUrl,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    fn: function(p_form, p_dialog) {
                        p_dialog.dialog.setHeader("Отсутствие");
                    },
                    scope: this
                },
                doBeforeFormSubmit: {
                    fn: function() {
                        var htmlNodeEnd = Dom.get(this.textDirNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_end"),
                            htmlNodeUnlimited = Dom.get(this.textDirNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_unlimited"),
                            htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0],
                            beginDate = new Date(),
                            endDate;
                        htmlNodeBegin.value = Alfresco.util.toISO8601(beginDate);
                        if (htmlNodeUnlimited.checked) {
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
                    fn: function(response) {
                        this.isAbsent = true;
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
                            text: "Отсутствие успешно создано"
                        });
                    },
                    scope: this
                },
                onFailure: {
                    fn: function(response) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: "Произошла ошибка"
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
        },

        postCreate: function logic_ecm_absence__postCreate() {
            this.showCancellationDialogPrerequisites = new Deferred(["isAbsent", "showCancellationPropmt"], {
                fn: this.showCancelAbsenceDialog,
                scope: this
            });

            Bubbling.on("currentEmployeeAbsenceChanged", this.onCurrentEmployeeAbsenceChanged, this);
            this.checkEmployeeAbsence();
            this.checkCancellationPropmt();
            this.setupIconNode();
        }

    });

});
