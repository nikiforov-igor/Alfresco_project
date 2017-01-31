define(["dojo/_base/declare",
        "dijit/MenuItem",
        "alfresco/core/Core",
        "alfresco/menus/_AlfMenuItemMixin"], function(declare, MenuItem, AlfCore, _AlfMenuItemMixin) {

    if (typeof LogicECM == "undefined" || !LogicECM) {
        LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};
    LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
    LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

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

        i18nScope: 'absenceInstantWidget',

        i18nRequirements: [{i18nFile: './properties/AbsenceInstantWidget.properties'}],

        onCurrentEmployeeAbsenceChanged: function(layer, args) {
	        this.isAbsent = args[1].isAbsent;
            if (this.isAbsent) {
                this.textDirNode.innerHTML = this.message('title.cancel.absence');
                this.onClick = this.showCancelAbsenceDialog;
            } else {
                this.textDirNode.innerHTML = this.message('title.out.of.office');
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
                title: me.message('title.cancel.absence'),
                text: me.message('message.cancel.absence.confirmation'),
                close: false,
                modal: true,
                buttons: [{
                    text: me.message('button.yes'),
                    handler: function() {
                        me.acceptCancel();
                        this.destroy();
                    }
                }, {
                    text: me.message('button.no'),
                    handler: function() {
                        this.destroy();
                    },
                    isDefault: true
                }]
            });
            evt.preventDefault();
        },

        acceptCancel: function() {
            var me = this;
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
                            text: me.message("message.absence.has.been.canceled")
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
                        p_dialog.dialog.setHeader(this.message("header.title"));
                    },
                    scope: this
                },
                doBeforeFormSubmit: {
                    fn: function() {
                        var htmlNodeEnd = Dom.get(this.textDirNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_end"),
                            htmlNodeUnlimited = Dom.get(this.textDirNode.id + "-createNewInstantAbsenceForm_prop_lecm-absence_unlimited"),
                            htmlNodeBegin = document.getElementsByName("prop_lecm-absence_begin")[0],
                            beginDate = LogicECM.module.Base.Util.dateToUTC0(new Date()),
                            endDate = htmlNodeUnlimited.checked ? beginDate : LogicECM.module.Base.Util.dateToUTC0(Alfresco.util.fromISO8601(htmlNodeEnd.value));

                        htmlNodeBegin.value = Alfresco.util.toISO8601(beginDate);
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
                            text: this.message('message.absence.has.been.created')
                        });
                    },
                    scope: this
                },
                onFailure: {
                    fn: function(response) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.message('message.error')
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

            if (LogicECM.module.Base) {
                LogicECM.module.Base.Util.loadScripts([
                    'modules/simple-dialog.js'
                ]);
            }
        }

    });

});
