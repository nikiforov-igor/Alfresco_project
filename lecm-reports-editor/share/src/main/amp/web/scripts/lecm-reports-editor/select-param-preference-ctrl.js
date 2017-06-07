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

            id: null,

            select: null,

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
                }
                Event.on(this.select, "change", this.onSelectChange, this, true);
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
                if (fireChangeEvent) {
                    this.onSelectChange();
                }
                if (this.select.value !== "~CREATE-NEW~") {
                    Dom.removeClass(this.deleteLink, "hidden");
                }
                this.preferences = preferences;
            },

            onSelectChange: function () {
                if (this.select.value !== "~CREATE-NEW~") {
                    var argumentsObj = this._getPreferenceByName(this.select.value);
                    var arguments = {};
                    if (argumentsObj) {
                        arguments = argumentsObj.args;
                    }

                    var components = Alfresco.util.ComponentManager.list();

                    var controls = components.filter(function (item) {
                        return item.id.indexOf(this.options.formId + "_") == 0
                    }, this);

                    for (var i = 0; i < controls.length; i++) {
                        var fieldName = controls[i].options.fieldId; //TODO Убедиться, что во всех контролах тут есть данные
                        if (arguments.hasOwnProperty(fieldName) && !(fieldName == this.options.fieldId)) {
                            LogicECM.module.Base.Util.reInitializeControl(this.options.formId, fieldName, {
                                currentValue: arguments[fieldName],
                                defaultValue: arguments[fieldName],
                                resetValue: true
                            });
                        }
                    }
                    Dom.removeClass(this.deleteLink, "hidden");
                } else {
                    Dom.addClass(this.deleteLink, "hidden");
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
                        constraintoviewport: false,
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
                            name.indexOf("-selectedItems") > 0 ||
                            name.indexOf("-autocomplete-input") > 0) {
                            delete dataObj[name];
                        } else if (name.indexOf("_added") > 0) {
                            var newName = name.replace("_added", "");
                            dataObj[newName] = dataObj[name];
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
                                    select.appendChild(option);
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
                                    select.remove(select.selectedIndex);
                                }
                            }
                        }
                    },
                    failureMessage: this.msg("message.failure"),
                    scope: this,
                    execScripts: true
                });
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