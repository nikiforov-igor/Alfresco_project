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

(function() {

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.SelectOne = function(fieldHtmlId) {
        LogicECM.module.SelectOne.superclass.constructor.call(this, "LogicECM.module.SelectOne", fieldHtmlId, [ "container", "datasource"]);

        Event.on(this.id, "change", this.onSelectChange, this, true);

        Bubbling.on("disableControl", this.onDisableControl, this);
        Bubbling.on("enableControl", this.onEnableControl, this);
        Bubbling.on("reInitializeControl", this.onReInitializeControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOne, Alfresco.component.Base, {
        options: {
            controlId: null,
            selectedValue: null,
            webscriptType: null,
            webscript: null,
            withEmpty: null,
            mandatory: false,
            currentNodeRef: null,
            destination: null,
            updateOnAction: null,
            changeItemFireAction: null,
            fieldId: null,
            formId: null,
            needSort: null
        },

        onReady: function() {
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            this._init();
        },

        _init: function() {
            var url, symbol;

            if (this.options.webscript) {
                if ("server" == this.options.webscriptType) {
                    url = Alfresco.constants.PROXY_URI;
                } else {
                    url = Alfresco.constants.URL_SERVICECONTEXT;
                }
                url += this.options.webscript;
                symbol = url.indexOf("?") > 0 ? "&" : "?";
                if (this.options.destination && this.options.destination != "{destination}") {
                    url += symbol + "nodeRef=" + this.options.destination + "&type=create";
                } else if (this.options.currentNodeRef) {
                    url += symbol + "nodeRef=" + this.options.currentNodeRef + "&type=edit";
                }
                Alfresco.util.Ajax.jsonGet({
                    url: url,
                    successCallback: {
                        scope: this,
                        fn: function (response) {
                            var i, select, option, prop,
                                oResults = response.json;
                            if (oResults) {
                                select = document.getElementById(this.id);
                                while (select.firstChild) {
                                    select.removeChild(select.firstChild);
                                }

                                if (this.options.withEmpty) {
                                    option = document.createElement("option");
                                    option.value = '';
                                    select.appendChild(option);
                                }

                                if (this.options.needSort) {
                                    oResults.data.sort(function(left, right) {
                                        if (left.name < right.name) {
                                            return -1;
                                        }
                                        if (left.name > right.name) {
                                            return 1;
                                        }
                                        return 0;
                                    });
                                }

                                for (i in oResults.data) {
                                    option = document.createElement("option");
                                    option.value = oResults.data[i].value;
                                    option.innerHTML = oResults.data[i].name;
                                    if (oResults.data[i].value == this.options.selectedValue) {
                                        option.selected = true;
                                    }
                                    delete oResults.data[i].value;
                                    delete oResults.data[i].name;
                                    for(prop in oResults.data[i]) {
                                        option['data-' + prop] = oResults.data[i][prop];
                                    }
                                    select.appendChild(option);
                                }
                                if (this.options.mandatory) {
                                    Bubbling.fire("mandatoryControlValueUpdated", this);
                                }
                                if (this.options.changeItemFireAction) {
                                    Bubbling.fire(this.options.changeItemFireAction, {
                                        selectedItem: select.value,
                                        formId: this.options.formId,
                                        fieldId: this.options.fieldId
                                    });
                                }
                            }
                        }
                    },
                    failureMessage: this.msg('message.failure')
                });
            }

            if (this.options.updateOnAction && this.options.updateOnAction.length) {
                var select = document.getElementById(this.id);
                if (select) {
                    select.setAttribute("disabled", "true");
                }
                Bubbling.unsubscribe(this.options.updateOnAction, this.onUpdateSelect, this);
                Bubbling.on(this.options.updateOnAction, this.onUpdateSelect, this);
            }
        },

        onUpdateSelect: function(layer, args) {
            var selectedItems = args[1].selectedItems;
            var control = Dom.get(this.id);
            if (control) {
                if (selectedItems && selectedItems.length > 0) {
                    control.removeAttribute("disabled");
                } else {
                    /*control.options[control.selectedIndex].selected = false;
                     document.getElementById(this.options.controlId + "-removed").value = this.options.selectedValue;
                     document.getElementById(this.options.controlId + "-added").value = "";*/
                    control.setAttribute("disabled", "true");
                }
            }
        },

        onSelectChange: function() {
            var select = document.getElementById(this.id);
            if (this.options.mandatory) {
                Bubbling.fire("mandatoryControlValueUpdated", this);
            }
            if (this.options.changeItemFireAction) {
                Bubbling.fire(this.options.changeItemFireAction, {
                    selectedItem: select.value,
                    formId: this.options.formId,
                    fieldId: this.options.fieldId
                });
            }
        },

        onDisableControl: function(layer, args) {
            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                var control = Dom.get(this.id);
                if (control) {
                    control.setAttribute("disabled", "true");
                    control.value = "";
                }

                this.tempDisabled = true;
            }
        },

        onEnableControl: function(layer, args) {
            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                if (!this.options.disabled) {
                    var control = Dom.get(this.id);
                    if (control) {
                        control.removeAttribute("disabled");
                    }

                    this.tempDisabled = true;
                }
                this.tempDisabled = false;
            }
        },

        onReInitializeControl: function(layer, args) {
            var formId = args[1].formId;
            var fieldId = args[1].fieldId;
            var options = args[1].options;
            if (this.options.formId == formId && this.options.fieldId == fieldId) {
                var o = YAHOO.lang.merge(this.options, options);
                this.options = o;
                this._init();
            }
        }
    });
})();
