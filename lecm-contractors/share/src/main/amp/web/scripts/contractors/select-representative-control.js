/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.SelectRepresentativeForContractor = function LogicECM_module_SelectRepresentativeForContractor(controlId, contractorSelectEvent) {
        LogicECM.module.SelectRepresentativeForContractor.superclass.constructor.call(this, "LogicECM.module.SelectRepresentativeForContractor", controlId, []);

        YAHOO.Bubbling.on(contractorSelectEvent, this.onUpdateRepresentatives, this);

        this.previousSelected = null;
        this._firstSelected = null;
        this.globCurrentContractor = null;
        this.addRepresentativeButton = null;

        return this;
    };

    YAHOO.extend(LogicECM.module.SelectRepresentativeForContractor, Alfresco.component.Base, {
        previousSelected: null,

        _firstSelected: null,

        addRepresentativeButton: null,

        globCurrentContractor: null, //переменная для сохранения выбранного контрагента

        options: {
            representativeSelectId: null,
            createNewMessage: null,
            emptyMessage: null,
            disabled: false,
            defaultValue: null
        },

        onReady: function SelectRepresentativeForContractor_onReady() {
            if (!this.options.disabled) {
                this.addRepresentativeButton = new YAHOO.widget.Button(this.id + "-add-new-representative-button",
                    {
                        onclick: {
                            fn: this._showAddRepresentativeForm, scope: this
                        },
                        disabled: true
                    }
                );

                // Собираем Input-элементы.
                var currentInputEl = Dom.get(this.id),
                    addedInputEl = Dom.get(this.id + "-added"),
                    removedInputEl = Dom.get(this.id + "-removed");

                var selectElement = Dom.get(this.options.representativeSelectId);
                if (selectElement != null) {
                    if (selectElement.options.length === 0) {
                        selectElement.options.length = 1; // FUTURE: JSHint-friendly.
                        selectElement.options[0] = new Option(this.options.emptyMessage, "", true);
                        selectElement.disabled = true;
                    }
                }

                // Необходимо для распознования "подёргивания".
                this._firstSelected = currentInputEl.value;

                var control = this;
                Event.on(this.options.representativeSelectId, "change", function (/*event, that*/) {
                    if (control._firstSelected === control.value) {
                        addedInputEl.value = "";
                        removedInputEl.value = "";
                    } else {
                        addedInputEl.value = this.value;
                        removedInputEl.value = control._firstSelected;
                    }

                    currentInputEl.value = control.value;
                });
            }
        },

        // после закрытия диалога вернуть фокус в исходный контрол
        backToControl: function() {
            var controlBtn = this.addRepresentativeButton;
            if (controlBtn) {
                controlBtn.focus();
            }
        },

        _showAddRepresentativeDialog: function (response) {
            var isPrimaryCheckboxChecked,
                templateRequestParams = {
                    itemKind: "type",
                    itemId: "lecm-contractor:link-representative-and-contractor",
                    destination: this.globCurrentContractor,
                    mode: "create",
                    submitType: "json",
                    ignoreNodes: response.json.join(),
                    showCancelButton: "true",
					showCaption: false
                },
                // Создание формы добавления адресанта.
                addRepresentativeForm = new Alfresco.module.SimpleDialog(this.id + "-add-representative-form"),
                me = this;

            addRepresentativeForm.setOptions({
                width: "50em",
                templateUrl: "components/form",
                templateRequestParams: templateRequestParams,
                destroyOnHide: true,
                doBeforeFormSubmit: {
                    fn: function () {
                        isPrimaryCheckboxChecked = Dom.get(me.id + "-add-representative-form_prop_lecm-contractor_link-to-representative-association-is-primary-entry").checked;
                    },
                    scope: this
                },
                doBeforeDialogShow: {
                    fn: function (p_form, p_dialog) {
                        var message;
                        if (this.options.createNewMessage) {
                            message = this.options.createNewMessage;
                        } else {
                            message = this.msg("dialog.createNew.title");
                        }
                        p_dialog.dialog.setHeader(message);
                    },
                    scope: this
                },
                doAfterDialogHide: {
                    // после закрытия диалога вернуть фокус в исходный контрол
                    fn: function (p_form, p_dialog) {
                        var controlBtn = this.addRepresentativeButton;
                        if (controlBtn) {
                            controlBtn.focus();
                        }
                    },
                    scope: this
                },

                onSuccess: {
                    fn: function (response) {
                        var addedLinkRef = response.json.persistedObject, // persistedObject это [link-representative-and-contractor], НЕ [representative-type]
                            fakeObject = {};

                        fakeObject[me.globCurrentContractor] = null;
                        Dom.get(me.id).value = "";

                        if (isPrimaryCheckboxChecked) {
                            Alfresco.util.Ajax.request({
                                method: "POST",
                                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
                                dataObj: {
                                    "representativeToAssignAsPrimary": addedLinkRef
                                },
                                requestContentType: "application/json",
                                responseContentType: "application/json",
                                successCallback: {
                                    fn: function () {
                                        me.onUpdateRepresentativesList(fakeObject, /* force */ true, addedLinkRef);
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function () {
                                        Alfresco.util.PopupManager.displayMessage({
                                            text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
                                        });
                                    }
                                }
                            });
                        } else {
                            me.onUpdateRepresentativesList(fakeObject, /* force */ true, addedLinkRef);
                        }

                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.component.Base.prototype.msg("message.add-representative.success")
                        });
                    },
                    scope: this
                },
                onFailure: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.component.Base.prototype.msg("message.add-representative.failure")
                        });
                    },
                    scope: this
                }
            });

            addRepresentativeForm.show();
        },

        _showAddRepresentativeForm: function () {
            if (this.globCurrentContractor === null) {
                window.alert(Alfresco.util.message("msg.has_to_choose_contractor"));
                return false;
            }

            // Спасаем "тонущие" всплывающие сообщения.
            Alfresco.util.PopupManager.zIndex = 9000;

            //дергаем сервис который получает список адресантов, которые связаны с контрагентом
            Alfresco.util.Ajax.request({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/busy",
                responseContentType: "application/json",
                dataObj: {
                    nodeRef: this.globCurrentContractor
                },
                successCallback: {
                    fn: this._showAddRepresentativeDialog,
                    scope: this
                },
                failureCallback: {
                    fn: function () {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.util.message("msg.get_addressee_list_failure")
                        });
                    }
                }
            });
            return true;
        },

        checkAddChildPermission: function (nodeRef) {
            var control = this;
            if (nodeRef != null) {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/security/api/getPermission?nodeRef=" + encodeURIComponent(nodeRef) + "&permission=" + encodeURIComponent("AddChildren"),
                        successCallback: {
                            fn: function (response) {
                                var oResults = response.json;
                                if (oResults != null) {
                                    if (control.addRepresentativeButton != null) {
                                        control.addRepresentativeButton.set("disabled", !oResults);
                                    }
                                }
                            },
                            scope: this
                        },
                        failureMessage: "message.failure"
                    });
            }
        },

        onUpdateRepresentatives: function (layer, args) {
            var selectedContractors = Object.keys(args[1].selectedItems); // IE 9+
            this.onUpdateRepresentativesList(selectedContractors, false, null);
        },

        onUpdateRepresentativesList: function (selectedContractors, force, representativeToSelect) {
            if (!this.options.disabled) {
                var selectElement = Dom.get(this.options.representativeSelectId),
                    currentInputEl = Dom.get(this.id),
                    addedInputEl = Dom.get(this.id + "-added"),
                    removedInputEl = Dom.get(this.id + "-removed"),
                    selectedContractor;

                if (selectedContractors.length === 0) {
                    selectElement.options.length = 1; // FUTURE: JSHint-friendly.
                    selectElement.options[0] = new Option(this.options.emptyMessage, "", true);
                    selectElement.disabled = true;

                    this.previousSelected = null;

                    // Помечаем к удалению то, что было выбрано. Если у нас уже есть что-то, помеченное к удалению, значит
                    // мы когда-то сменили контрагента и удалить адресанта необходимо только для того, который был
                    // выбран в самом начале. Проще говоря, кого бы мы не выбрали, удалить нам необходимо только
                    // предыдущего.
                    removedInputEl.value = removedInputEl.value || currentInputEl.value;
                    addedInputEl.value = ""; // Кроме того, если мы что-то добавляли, то теперь отменяем добавление.
                    currentInputEl.value = "";
                    this.globCurrentContractor = null;

                    return;
                }

                selectElement.disabled = false;
                selectedContractor = this.globCurrentContractor = selectedContractors[0];

                this.checkAddChildPermission(this.globCurrentContractor);

                // Событие на которое мы подписываем этот обработчик вызывается 3+ раз за один "выбор" контрагента, а
                // заполнять список адресантов необходимо только один раз.
                //
                // FUTURE: Если YAHOO.util.Dom.get не умеет кэшировать, кэшировать самому (Input-элементы).
                if (this.previousSelected === selectedContractor) {
                    if (!force) {
                        return;
                    }
                } else {
                    this.previousSelected = selectedContractor;
                }

                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/getrepresentatives",
                    dataObj: {
                        targetContractor: selectedContractor
                    },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: function (response) {
                            var i,
                                shortName,
                                mustBeSelected,
                                wasNotSelected = true,
                                lg = response.json.representatives.length;

                            // Очищаем список.
                            selectElement.options.length = 1; // FUTURE: JSHint-friendly...
                            selectElement.options[ 0 ] = new Option(this.options.emptyMessage, "", true, true);

                            // Если представители отсутствуют.
                            if (lg == 0) {
                                addedInputEl.value = "";
                                removedInputEl.value = removedInputEl.value || currentInputEl.value;
                            }

                            for (i = 0; i < lg; ++i) {
                                // Выбираем основного адресанта.
                                mustBeSelected = false;

                                if (representativeToSelect) {
                                    // representativeToSelect это [link-representative-and-contractor], НЕ [representative-type]
                                    mustBeSelected = response.json.representatives[ i ].linkRef === representativeToSelect;
                                } else if (this.options.defaultValue) { // нам было передано значение по умолчанию
                                    mustBeSelected = this.options.defaultValue === response.json.representatives[ i ].nodeRef;
                                } else if (currentInputEl.value === "") { // Если c сервера ничего не пришло или мы выбрали "Без адресанта".
                                    // То выбираем основного адресанта (согласно требованиям).
                                    mustBeSelected = response.json.representatives[ i ].isPrimary;
                                } else { // Если c сервера что-то пришло, то мы в Edit-режиме, тогда...
                                    if (currentInputEl.value === response.json.representatives[ i ].nodeRef) {
                                        // Выбираем того, кто "пришёл с сервера".
                                        mustBeSelected = true;
                                    }
                                }

                                // Если у нас есть Адресант с ( mustBeSelected === true ), то снимаем выделение с
                                // первого элемента "Без адресанта...".
                                if (mustBeSelected) {
                                    wasNotSelected = false;

                                    selectElement.options[ 0 ].selected = false;
                                    selectElement.options[ 0 ].defaultSelected = false;

                                    // Обновление Input-элементов из обработчика события 'change' для выпадающего списка.
                                    // FUTURE: Вынести в отдельный метод.
                                    if (this._firstSelected === response.json.representatives[ i ].nodeRef) {
                                        addedInputEl.value = "";
                                        removedInputEl.value = "";
                                    } else {
                                        addedInputEl.value = response.json.representatives[ i ].nodeRef;
                                        removedInputEl.value = this._firstSelected;
                                    }

                                    currentInputEl.value = response.json.representatives[ i ].nodeRef;
                                }

                                shortName = response.json.representatives[ i ].shortName;
                                selectElement.options[ selectElement.options.length ] = new Option(
                                    response.json.representatives[ i ].isPrimary ? "[" + shortName + "]" : shortName,
                                    response.json.representatives[ i ].nodeRef,
                                    mustBeSelected,
                                    mustBeSelected
                                );
                            }

                            // Если никто из Адресантов не был выбран автоматически, значит - отсутствует Основной контакт,
                            // в этом случае, очищаем всё, что было до этого.
                            if (wasNotSelected) {
                                addedInputEl.value = "";
                                removedInputEl.value = removedInputEl.value || currentInputEl.value;
                            }
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: Alfresco.util.message("err.get_addressee_list_failure") // Alfresco.component.Base.prototype.msg("")
                            });
                        }
                    }
                });
            }
        }
    });
})();
