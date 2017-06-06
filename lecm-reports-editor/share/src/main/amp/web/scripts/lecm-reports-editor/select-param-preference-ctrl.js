/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    LogicECM.module.ReportsEditor.SelectParamPreference = function (id) {
        LogicECM.module.ReportsEditor.SelectParamPreference.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.SelectParamPreference", id);
        this.id = id;
        this.controlId = id + "-cntrl";
        this.formId = id.substring(0, id.indexOf("_savedPreferences"));
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectParamPreference, Alfresco.component.Base,
        {
            options: {
                reportCode: null,
                fieldId: null,
                notSelectedOptionShow: false,
                notSelectedText: ""
            },

            controlId: null,
            saveNewLink: null,
            deleteLink: null,

            id: null,

            select: null,

            preferencesInSelect: [],
            actualPreferences: [],

            onReady: function () {
                this.select = Dom.get(this.id);
                if (this.select) {
                    this.populateSelect();
                }
                Event.on(this.select, "change", this.onSelectChange, this, true);

                this.saveNewLink = Dom.get(this.controlId + "-create-new");
                if (this.saveNewLink) {
                    Event.on(this.controlId + "-create-new", "click", this.onSave, null, this);
                }

                this.deleteLink = Dom.get(this.controlId + "-delete");
                if (this.deleteLink) {
                    Event.on(this.controlId + "-delete", "click", this.onDelete, null, this);
                    Dom.addClass(this.deleteLink, "hidden");
                }
            },

            populateSelect: function () {
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
                                this.preferencesInSelect = oResponse.json.value;
                                this.actualPreferences = new Array(oResponse.json.value);
                            }

                            var select = this.select;
                            while (select.firstChild) {
                                select.removeChild(select.firstChild);
                            }

                            var option;
                            if (this.options.notSelectedOptionShow) {
                                option = document.createElement("option");
                                option.value = '~CREATE-NEW~';
                                option.innerHTML = this.options.notSelectedText;
                                option.selected = !this.preferencesInSelect.length;
                                select.appendChild(option);
                            }

                            if (this.options.needSort) {
                                this.preferencesInSelect.sort(function (left, right) {
                                    if (left['created'] > right['created']) {
                                        return -1;
                                    }
                                    if (left['created'] < right['created']) {
                                        return 1;
                                    }
                                    return 0;
                                });
                            }

                            for (var i = 0; i < this.preferencesInSelect.length; i++) {
                                var preference = this.preferencesInSelect[i];

                                option = document.createElement("option");
                                option.value = i;
                                option.innerHTML = preference.name;
                                if (i == (this.preferencesInSelect.length - 1)) {
                                    option.selected = true;
                                }
                                select.appendChild(option);
                            }

                            this.onSelectChange();
                        }
                    },
                    failureCallback: {
                        scope: this,
                        fn: function (oResponse) {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },

            onSelectChange: function () {
                if (this.select.value != "~CREATE-NEW~") {
                    var argumentsObj = this.preferencesInSelect[this.select.value].args;

                    var components = Alfresco.util.ComponentManager.list();

                    var formId = this.formId;
                    var controls = components.filter(function (item) {
                        return item.id.indexOf(formId + "_") == 0
                    });

                    for (var i = 0; i < controls.length; i++) {
                        var fieldName = controls[i].options.fieldId;
                        if (argumentsObj && argumentsObj.hasOwnProperty(fieldName)) {
                            LogicECM.module.Base.Util.reInitializeControl(formId, fieldName, {
                                currentValue: argumentsObj[fieldName],
                                defaultValue: argumentsObj[fieldName],
                                resetValue: true
                            });
                        }
                    }
                    Dom.removeClass(this.deleteLink, "hidden");
                } else {
                    Dom.addClass(this.deleteLink, "hidden");
                }
            },

            onSave: function () {
                var newParam = {
                    name: "lastParams-" + (this.actualPreferences.length + 1),
                    created: Alfresco.util.toISO8601(new Date()),
                    args: {}
                };

                var fr = Alfresco.util.ComponentManager.find({
                    id: this.formId + "-form",
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

                this.actualPreferences.push(newParam);
                this.preferencesInSelect.push(newParam);

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/user-settings/save",
                    dataObj: {
                        value: YAHOO.lang.JSON.stringify(this.actualPreferences),
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
                                    var lastIndex = this.preferencesInSelect.length - 1;
                                    option.value = lastIndex;
                                    option.innerHTML = this.preferencesInSelect[lastIndex].name;
                                    option.selected = true;
                                    select.appendChild(option);
                                }
                            }
                        }
                    },
                    failureCallback: {
                        fn: function (oResponse) {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },

            onDelete: function () {
                if (this.select && this.select.value != "~CREATE-NEW~") {
                    this.preferencesInSelect.splice(this.select.value, 1, {});
                    this.actualPreferences.splice(this.select.value, 1);
                }

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/user-settings/save",
                    dataObj: {
                        value: YAHOO.lang.JSON.stringify(this.actualPreferences),
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
                    failureCallback: {
                        fn: function (oResponse) {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            }
        });
})();