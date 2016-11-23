/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */

LogicECM.module.Errands = LogicECM.module.Errands || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Errands.ReiterationExt = function (htmlId) {
        LogicECM.module.Base.ReiterationExt.superclass.constructor.call(this, "LogicECM.module.Base.ReiterationExt", htmlId, ["button", "container"]);

        this.typeContainerPrefix = this.id + '-type-container-';

        YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
        YAHOO.Bubbling.on("showControl", this.onShowControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.ReiterationExt, LogicECM.module.Base.ReiterationExt,
        {
            panel: null,
            isPanelShown: false,
            currentType: "DAILY",
            currentPickerType: null,
            pickerTypes: ['week-days', 'month-days'],
            days: Alfresco.util.message('days.short').split(','),
            daysFull: Alfresco.util.message('days.long').split(','),

            onReady: function () {
                var first = this.days.shift();
                this.days.push(first);
                first = this.daysFull.shift();
                this.daysFull.push(first);

                var select = Dom.get(this.id + '-type');
                Event.addListener(select, 'click', this.onChangeType, this, true);
                var value = this.getControlValue();
                var message = this.getSummary(value);
                if (value) {
                    this.currentType = value.type;
                    select.value = this.getControlValue().type;
                    //select.selectedOptions[0].innerHTML = message;
                }

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },
            getValue: function getValue_function() {
                if (this.currentType != "DAILY") {
                    var nodes = YAHOO.util.Selector.query('.item.checked', this.typeContainerPrefix + this.currentPickerType);
                    var data = [];
                    for (var i in nodes) {
                        var node = nodes[i];
                        var id = node.id.replace(this.typeContainerPrefix + this.currentPickerType, '');
                        data.push(id);
                    }
                    if (data.length) {
                        return {
                            type: this.currentType,
                            data: data
                        }
                    } else {
                        return null;
                    }
                } else {
                    return {
                        type: this.currentType,
                        data: []
                    }
                }
            },

            setValue: function setValue_function(value) {
                var type = this.currentType;
                if (!value) {
                    this._switchType(type, "WEEKLY");
                    return;
                }
                this._switchType(type, value.type);
                for (var i in value.data) {
                    var node = value.data[i];
                    Dom.addClass(this.typeContainerPrefix + this.currentPickerType + node, 'checked');
                }
            },

            getSummary: function getSummary_function(value) {
                if (!value) {
                    return "(" + Alfresco.util.message("label.reiteration.not-specified") + ")";
                }
                var summary = "";
                if (value.type === "DAILY") {
                    summary += this.msg("label.reiteration-control.options." + value.type.toLowerCase());
                }
                else if (value.type === "WEEKLY") {
                    summary += this.msg("label.reiteration-control.options." + value.type.toLowerCase()) + ", ";
                    for (var i in value.data) {
                        var index = parseInt(value.data[i] - 1);
                        if (i != 0) {
                            summary += ', ';
                        }
                        summary += this.daysFull[index];
                    }
                } else {
                    summary += this.msg("label.reiteration-control.options." + value.type.toLowerCase()) + ", " + Alfresco.util.message("date-unit.plural.day") + ": ";
                    for (var i in value.data) {
                        if (i != 0) {
                            summary += ', ';
                        }
                        summary += value.data[i];
                    }
                }
                return summary;
            },

            openDialog: function openDialog_function() {
                if (!this.panel) {
                    this.id + '-dialog-panel'
                    this.panel = new YAHOO.widget.SimpleDialog("simpledialog1", {
                        width: "100px",
                        fixedcenter: true,
                        visible: false,
                        draggable: true,
                        close: true,
                        model: true,
                        constraintoviewport: true,
                        buttons: [
                            {
                                text: Alfresco.util.message("form.button.submit.label"),
                                handler: this.onOk.bind(this),
                                isDefault: true
                            },
                            {text: Alfresco.util.message("button.cancel"), handler: this.onCancel.bind(this)}
                        ]
                    });

                    this.panel.setHeader(Alfresco.util.message("label.reiteration.repeat"));

                    var html = '<div id="' + this.id + '-dialog-panel-container" class="reiteration">'
                    html += '<div id="' + this.id + '-label-type" style="text-align: center"><span class="label"></span></div>';
                    html += '<div class="delim">&nbsp;</div>';
                    html += '<div id="' + this.typeContainerPrefix + 'week-days" class="container hidden1">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 7; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix + 'week-days' + i + '">' + this.days[i - 1] + '</div>';
                    }
                    html += '</div>';
                    html += '</div>';
                    html += '<div id="' + this.typeContainerPrefix + 'month-days" class="container hidden1">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 31; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix + 'month-days' + i + '">' + i + '</div>';
                        if (i % 7 == 0) {
                            html += "<br/>";
                        }
                    }
                    html += '</div>';
                    html += '</div>';
                    html += '<div class="summary"><label for="' + this.id + '-summary">Сводка: </label><span id="' + this.id + '-summary"></span></div>';
                    html += '</div>'

                    this.panel.setBody(html);
                    this.panel.render(document.body);
                    //this._switchType(null,"WEEKLY");
                    var items = YAHOO.util.Selector.query('.item', this.id + '-dialog-panel-container');
                    Event.addListener(items, 'click', this.onItemClick, {}, this);
                }
                this.setValue(this.getControlValue());
                this.updateSummary();
                this.panel.show();

            },


            onChangeType: function onChangeType_function(ev, args) {
                if (ev.detail == 0) {
                    var to = ev.target.value;
                    if (to && to != "DAILY") {
                        if (!this.isPanelShown) {
                            this.openDialog();
                        }
                        if (this.currentType != to) {
                            this._switchType(this.currentType, to);
                        }
                    } else if (to == "DAILY") {
                        this.currentType = to;
                        this.updateValue(this.getValue());
                    }
                }
            },

            _switchType: function _switchType(from, to) {
                var toPickerType = this.getPickerTypeByRepeatType(to);

                if (from) {
                    if (this.currentPickerType != toPickerType) {
                        Dom.addClass(this.typeContainerPrefix + this.currentPickerType, 'hidden1');
                    }
                    var nodes = YAHOO.util.Selector.query('.item.checked', this.typeContainerPrefix + this.currentPickerType);
                    Dom.removeClass(nodes, 'checked');
                }
                if (to) {
                    YAHOO.util.Selector.query('span', this.id + '-label-type', true).innerHTML = this.msg('label.reiteration-control.options.' + to.toLowerCase());
                    this.currentType = to;

                    if (this.currentPickerType != toPickerType) {
                        Dom.removeClass(this.typeContainerPrefix + toPickerType, 'hidden1');
                        this.currentPickerType = toPickerType;
                    }
                }

                this.updateSummary();
            },
            getPickerTypeByRepeatType: function (repeatType) {
                var pickerType = null;
                if (repeatType) {
                    if (repeatType === "WEEKLY") {
                        pickerType = this.pickerTypes[0];
                    } else {
                        pickerType = this.pickerTypes[1];
                    }
                }
                return pickerType;
            },
            onHideControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    Dom.setStyle(this.id + "-parent", "display", "none");
                    this.isPanelShown = false;

                }
            },

            onShowControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    Dom.setStyle(this.id + "-parent", "display", "block");
                    this.isPanelShown = true;
                }
            },
            updateValue: function updateValue_function(value) {
                if (value === null) {
                    Dom.get(this.id).value = "";
                } else {
                    Dom.get(this.id).value = JSON.stringify(value);
                }
                var summary = this.getSummary(value)
                var el = Dom.get(this.id + '-displayValue');
                el.innerHTML = summary;
                //var select = Dom.get(this.id + '-type');
                //select.selectedOptions[0].innerHTML = summary;
            }


        });
})();
