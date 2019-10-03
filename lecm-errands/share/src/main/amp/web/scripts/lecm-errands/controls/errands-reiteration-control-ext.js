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
            currentType: "DAILY",
            currentPickerType: null,
            pickerTypes: ['none', 'week-days', 'month-days'],
            days: Alfresco.util.message('days.short').split(','),
            daysFull: Alfresco.util.message('days.long').split(','),
            options: {
                defaultType: null,
                defaultDays: []
            },
            widgets: {
                select: null
            },

            onReady: function () {
                var first = this.days.shift();
                this.days.push(first);
                first = this.daysFull.shift();
                this.daysFull.push(first);
                if (!this.getControlValue()) {
                    this.loadDefaultValue();
                } else {
                    this.updateValue(this.getControlValue());
                }
                var el = Dom.get(this.id + '-displayValue');
                //если поле не disabled - отображается как ссылка,
                // иначе просто span
                if (el.tagName === "A") {
                    Event.addListener(el, 'click', function (e) {
                        this.openDialog(e);
                    }.bind(this));
                }
                this.widgets.select = Dom.get(this.id + "-type");

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },
            getValue: function getValue_function() {
                if (this.currentType !== "DAILY") {
                    return LogicECM.module.Errands.ReiterationExt.superclass.getValue.call(this);
                } else {
                    return {
                        type: this.currentType,
                        data: []
                    }
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
                        if (i > 0) {
                            summary += ', ';
                        }
                        summary += this.daysFull[index];
                    }
                } else {
                    summary += this.msg("label.reiteration-control.options." + value.type.toLowerCase()) + ", " + Alfresco.util.message("date-unit.plural.day") + ": ";
                    for (var i in value.data) {
                        if (i > 0) {
                            summary += ', ';
                        }
                        summary += value.data[i];
                    }
                }
                return summary;
            },

            openDialog: function openDialog_function(e) {
                if (!this.panel) {
                    this.panel = new YAHOO.widget.SimpleDialog(this.id + '-dialog-panel', {
                        width: "256px",
                        visible: false,
                        draggable: true,
                        modal: true,
                        close: true,
                        constraintoviewport: true,
                        buttons: [
                            {
                                text: Alfresco.util.message("form.button.submit.label"),
                                handler: this.onOk.bind(this),
                                isDefault: true
                            },
                            {
                                text: Alfresco.util.message("button.cancel"),
                                handler: this.onCancel.bind(this)
                            }
                        ]
                    });

                    this.panel.setHeader(Alfresco.util.message("label.reiteration.repeat"));

                    var html = '<div id="' + this.id + '-dialog-panel-container" class="reiteration">';
                    html += '<div id="' + this.id + '-switch-type-container" style="text-align: center"></div>';
                    html += '<div id="' + this.typeContainerPrefix + 'week-days" class="container hidden1 week-days">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 7; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix + 'week-days' + i + '">' + this.days[i - 1] + '</div>';
                    }
                    html += '</div>';
                    html += '</div>';
                    html += '<div id="' + this.typeContainerPrefix + 'month-days" class="container hidden1 month-days">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 31; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix + 'month-days' + i + '">' + i + '</div>';
                        if (i % 7 == 0) {
                            html += "<br/>";
                        }
                    }
                    html += '</div>';
                    html += '</div>';
                    html += '<div class="summary"><label for="' + this.id + '-summary">' + this.msg("label.reiteration-control.summary") + ': </label><span id="' + this.id + '-summary"></span></div>';
                    html += '</div>';

                    this.panel.setBody(html);
                    this.panel.render(document.body);
                    var picker = Dom.get(this.id + '-dialog-panel');
                    Dom.setStyle(picker, "min-width", "256px");
                    var select = this.widgets.select;
                    Dom.get(this.id + '-switch-type-container').appendChild(select);
                    Dom.setStyle(select, "display", "block");
                    Dom.setStyle(select, "width", "100%");
                    Event.addListener(select, 'change', this.onChangeType, this, true);
                    var items = YAHOO.util.Selector.query('.item', this.id + '-dialog-panel-container');
                    Event.addListener(items, 'click', this.onItemClick, {}, this);

                    this.balloon = Alfresco.util.createBalloon(select, {
                        effectType: null,
                        effectDuration: 0
                    });
                    this.panel.hideEvent.subscribe(function(){
                        this.balloon.hide();
                    }, null, this);
                }
                this.setValue(this.getControlValue());
                this.widgets.select.value = this.currentType;
                this.updateSummary();
                this.panel.show();

                var clicked = Event.getTarget(e);
                var pickerPanel = Dom.get(this.id + '-dialog-panel').parentNode;

                var x = Dom.getX(clicked);
                if (Dom.getX(pickerPanel) != x) {
                    Dom.setX(pickerPanel, x);
                }
                var y = Dom.getY(clicked) + clicked.offsetHeight;
                if (Dom.getY(pickerPanel) != y) {
                    Dom.setY(pickerPanel, y);
                }
            },

            onItemClick: function onItemClick_function(ev){
                LogicECM.module.Errands.ReiterationExt.superclass.onItemClick.call(this, ev);
            },

            _switchType: function _switchType(from, to) {
                var toPickerType = this.getPickerTypeByRepeatType(to);

                if (from) {
                    if (from != "DAILY") {
                        if (this.currentPickerType != toPickerType) {
                            Dom.addClass(this.typeContainerPrefix + this.currentPickerType, 'hidden1');
                        }
                        var nodes = YAHOO.util.Selector.query('.item.checked', this.typeContainerPrefix + this.currentPickerType);
                        Dom.removeClass(nodes, 'checked');
                    }
                }
                if (to) {
                    this.currentType = to;
                    if (to == "DAILY") {
                        this.currentPickerType = toPickerType;
                    } else {
                        if (this.currentPickerType != toPickerType) {
                            Dom.removeClass(this.typeContainerPrefix + toPickerType, 'hidden1');
                            this.currentPickerType = toPickerType;
                        }
                    }
                }
                this.updateSummary();
            },

            getPickerTypeByRepeatType: function (repeatType) {
                var pickerType = null;
                if (repeatType) {
                    if (repeatType === "DAILY") {
                        pickerType = this.pickerTypes[0];
                    } else if (repeatType === "WEEKLY") {
                        pickerType = this.pickerTypes[1];
                    } else {
                        pickerType = this.pickerTypes[2];
                    }
                }
                return pickerType;
            }
        });
})();
