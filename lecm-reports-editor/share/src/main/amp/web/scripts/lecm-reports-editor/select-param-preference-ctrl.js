/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.ReportsEditor.SelectParamPreference = function (id) {
        LogicECM.module.ReportsEditor.SelectParamPreference.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.SelectParamPreference", id);
        this.id = id;
        this.controlId = id + "-cntrl";
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectParamPreference, Alfresco.component.Base,
        {
            options: {
                reportCode: null,
                fieldId: null,
                formId: null,
                notSelectedOptionShow: false,
                notSelectedText: "",
                currentValue: null,
                preferencesValue: null,
                needSort: true
            },

            controlId: null,
            saveNewLink: null,
            deleteLink: null,
            controls: null,

            id: null,

            select: null,
            firstSelectOption: null,

            preferences: [],

            onReady: function () {
                this.saveNewLink = Dom.get(this.controlId + "-create-new");
                if (this.saveNewLink) {
                    Event.on(this.controlId + "-create-new", "click", this.onSaveClick, null, this);
                }

                this.deleteLink = Dom.get(this.controlId + "-delete");
                if (this.deleteLink) {
                    Event.on(this.controlId + "-delete", "click", this.onDeleteClick, null, this);
                    Dom.addClass(this.deleteLink, "hidden");
                }

                this.select = Dom.get(this.id);
                if (this.select) {
                    this.populateSelect();
                    Event.on(this.select, "change", this.onSelectChange, this, true);
                }
            },

            populateSelect: function () {
                if (!this.options.preferencesValue) {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "GET",
                        url: Alfresco.constants.PROXY_URI + "lecm/user-settings/get",
                        dataObj: {
                            category: "reports",
                            key: this.options.reportCode + ".saved-preferences"
                        },
                        successCallback: {
                            scope: this,
                            fn: function (oResponse) {
                                if (oResponse.json && oResponse.json.value) {
                                    this._fillSelect(oResponse.json.value, true);
                                }

                            }
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });
                } else {
                    var preferences = [];
                    try {
                        preferences = YAHOO.lang.JSON.parse(this.options.preferencesValue);
                    } catch (e) {
                    }
                    this._fillSelect(preferences, false);
                }
            },

            _fillSelect: function (preferences, fireChangeEvent) {
                var select = this.select;
                while (select.firstChild) {
                    select.removeChild(select.firstChild);
                }

                var option;
                if (this.options.notSelectedOptionShow) {
                    option = document.createElement("option");
                    option.value = '~CREATE-NEW~';
                    option.innerHTML = this.options.notSelectedText;
                    option.selected = !preferences || !preferences.length;
                    select.appendChild(option);
                }

                if (this.options.needSort) {
                    preferences.sort(function (left, right) {
                        if (left['created'] > right['created']) {
                            return -1;
                        }
                        if (left['created'] < right['created']) {
                            return 1;
                        }
                        return 0;
                    });
                }

                for (var i = 0; i < preferences.length; i++) {
                    var preference = preferences[i];

                    option = document.createElement("option");
                    option.value = preference.name;
                    option.innerHTML = preference.name;
                    option.selected = (i == 0) || (this.options.currentValue && this.options.currentValue == preference.name);
                    select.appendChild(option);
                }
                if (select.options.length > 1) {
                    this.firstSelectOption = select.options[1];
                }
                if (fireChangeEvent) {
                    this.onSelectChange();
                }
                if (this.select.value !== "~CREATE-NEW~") {
                    Dom.removeClass(this.deleteLink, "hidden");
                }
                this.preferences = preferences;
            },

            onSelectChange: function () {
                if (this.select.value === "~CREATE-NEW~") {
                    this._resetValues();
                } else {
                    this._initControls();
                    var argumentsObj = this._getPreferenceByName(this.select.value);
                    var arguments = argumentsObj ? argumentsObj.args : {};
                    var formId = this.options.formId;
                    var fieldId = this.options.fieldId;
                    for (var i = 0; i < this.controls.length; i++) {
                        var controlFieldId = this.controls[i].options.fieldId;
                        if (controlFieldId && arguments.hasOwnProperty(controlFieldId) && (controlFieldId != fieldId)) {
                            var newValue = arguments[controlFieldId];
                            LogicECM.module.Base.Util.reInitializeControl(formId, controlFieldId, {
                                currentValue: newValue,
                                defaultValue: newValue,
                                fieldValues: newValue ? newValue.split(",") : [],
                                resetValue: !newValue,
                                selectCurrentValue: newValue
                            });
                        }
                    }
                    Dom.removeClass(this.deleteLink, "hidden");
                }
            },

            onSaveClick: function () {
                var context = this;
                Alfresco.util.PopupManager.getUserInput(
                    {
                        title: this.msg("report.param.prereference.save.title"),
                        text: this.msg("report.param.preference.save.label"),
                        input: "text",
                        modal: true,
                        close: true,
                        buttons: [
                            {
                                text: Alfresco.util.message("button.ok"),
                                handler: {
                                    fn: function (event, obj) {
                                        var input = obj.body.children[2]; /*label,br,input*/
                                        if (input) {
                                            var inputValue = input.value;
                                            var indexInCurrentValues = context._getIndexByName(inputValue.trim());
                                            if (indexInCurrentValues >= 0) {
                                                Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text: Alfresco.util.message('report.param.prereference.save.not_unique')
                                                    }, Dom.get(obj.id));
                                                return;
                                            } else {
                                                context.onSave(inputValue);
                                                this.destroy();
                                            }
                                        }
                                    }
                                },
                                isDefault: true
                            },
                            {
                                text: Alfresco.util.message("button.cancel"),
                                handler: function () {
                                    this.destroy();
                                }
                            }
                        ]
                    });
            },

            onSave: function (name) {
                var newParam = {
                    name: name ? name : "lastParams-" + (this.preferences.length + 1),
                    created: Alfresco.util.toISO8601(new Date()),
                    args: {}
                };

                var fr = Alfresco.util.ComponentManager.find({
                    id: this.options.formId + "-form",
                    name: "Alfresco.FormUI"
                });

                if (fr && fr.length) {
                    var renameProperty = function (dataObj, name) {
                        if (name.indexOf("_removed") > 0 ||
                            name.indexOf("_added") > 0 ||
                            name.indexOf("-selectedItems") > 0 ||
                            name.indexOf("-autocomplete-input") > 0) {
                            delete dataObj[name];
                        }
                    };
                    var formData = fr[0].formsRuntime.getFormData();
                    for (var property in formData) {
                        if (formData.hasOwnProperty(property)) {
                            renameProperty(formData, property);
                        }
                    }

                    newParam.args = formData;
                }

                this.preferences.unshift(newParam);

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/user-settings/save",
                    dataObj: {
                        value: YAHOO.lang.JSON.stringify(this.preferences),
                        category: "reports",
                        key: this.options.reportCode + ".saved-preferences"
                    },
                    successCallback: {
                        scope: this,
                        fn: function (oResponse) {
                            if (oResponse.json) {
                                var select = this.select;
                                if (select) {
                                    var option = document.createElement("option");
                                    option.value = this.preferences[0].name;
                                    option.innerHTML = this.preferences[0].name;
                                    option.selected = true;

                                    if (this.firstSelectOption) {
                                        Dom.insertBefore(option, this.firstSelectOption);
                                    } else {
                                        select.appendChild(option);
                                    }
                                    this.firstSelectOption = option;
                                    Dom.removeClass(this.deleteLink, "hidden");
                                }
                            }
                        }
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });

            },

            onDeleteClick: function () {
                if (this.select && this.select.value !== "~CREATE-NEW~") {
                    this.preferences.splice(this._getIndexByName(this.select.value), 1);
                }

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/user-settings/save",
                    dataObj: {
                        value: YAHOO.lang.JSON.stringify(this.preferences),
                        category: "reports",
                        key: this.options.reportCode + ".saved-preferences"
                    },
                    successCallback: {
                        scope: this,
                        fn: function (oResponse) {
                            if (oResponse.json) {
                                var select = this.select;
                                if (select) {
                                    // сохраним удаляемое значение
                                    var removingValue = select.value;
                                    select.remove(select.selectedIndex);
                                    if (select.value === "~CREATE-NEW~") {
                                        this._resetValues();
                                    }
                                    // если удаляемое значение являлось первым option'ом в select'е, то меняем firstSelectOption
                                    if (removingValue == this.firstSelectOption.value) {
                                        this.firstSelectOption = (select.options.length == 1) ? null : select.options[1];
                                    }
                                }
                            }
                        }
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
            },

            _initControls: function() {
                if (!this.controls) {
                    this.controls = [];
                    var components = Alfresco.util.ComponentManager.list();
                    var form = Alfresco.util.ComponentManager.get(this.options.formId + "-form");
                    var formIndex = components.indexOf(form);
                    while (formIndex > 0) {
                        formIndex++;
                        var component = components[formIndex];
                        if (component && component.name != "Alfresco.FormUI") {
                            this.controls.push(component);
                        } else {
                            formIndex = -1;
                        }
                    }
                }
            },

            _resetValues: function() {
                this._initControls();
                Dom.addClass(this.deleteLink, "hidden");
                var formId = this.options.formId;
                var fieldId = this.options.fieldId;
                for (var i = 0; i < this.controls.length; i++) {
                    var control = this.controls[i];
                    var controlFieldId = control.options.fieldId;
                    if (controlFieldId && (controlFieldId != fieldId)) {
                        LogicECM.module.Base.Util.reInitializeControl(formId, controlFieldId, {
                            currentValue: "",
                            defaultValue: "",
                            fieldValues: "".split(","),
                            resetValue: true,
                            selectCurrentValue: ""
                        });
                    }
                }
            },

            _getPreferenceByName: function (name) {
                var index = this._getIndexByName(name);
                return index >= 0 ? this.preferences[index] : null;
            },

            _getIndexByName: function (name) {
                if (this.preferences) {
                    for (var i = 0; i < this.preferences.length; i++) {
                        if (this.preferences[i].name == name) {
                            return i;
                        }
                    }
                }
                return -1;
            }
        });
})();