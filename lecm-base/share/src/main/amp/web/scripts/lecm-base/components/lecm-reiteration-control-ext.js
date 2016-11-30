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
LogicECM.module.Base = LogicECM.module.Base || {};


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Base.ReiterationExt = function (htmlId) {
        LogicECM.module.Base.ReiterationExt.superclass.constructor.call(this, "LogicECM.module.Base.ReiterationExt", htmlId, ["button", "container"]);

        this.switchTypePrefix = this.id +'-swith-type-button-';
        this.typeContainerPrefix = this.id +'-type-container-';

        YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
        YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
        YAHOO.Bubbling.on("showControl", this.onShowControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.Base.ReiterationExt, Alfresco.component.Base,
        {
            panel: null,

            switchTypePrefix: "",
            typeContainerPrefix: "",
            currentType: null,
			readonly: false,
            days: Alfresco.util.message('days.short').split(','),
            daysFull: Alfresco.util.message('days.long').split(','),

            onReady: function () {
                var first = this.days.shift();
                this.days.push(first);
                first = this.daysFull.shift();
                this.daysFull.push(first);

                var message = this.getSummary(this.getControlValue());
                var el = Dom.get(this.id + '-displayValue');
                el.innerHTML = message;
                if (el.tagName === "A") {
                    Event.addListener(el, 'click', function () {
						if (!this.readonly) {
							this.openDialog();
						}
                    }.bind(this));
                }

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },

            getControlValue: function getControlValue_function() {
                try {
                    return JSON.parse(Dom.get(this.id).value);
                } catch (e) {
                    return null;
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
            },

            getValue: function getValue_function() {
                var node = YAHOO.util.Selector.query('.active', this.id + '-switch-type-container', true);
                var type = node.id.replace(this.switchTypePrefix, '');
                var nodes = YAHOO.util.Selector.query('.item.checked', this.typeContainerPrefix + type);
                var data = [];
                for (var i in nodes) {
                    var node = nodes[i];
                    var id = node.id.replace(this.typeContainerPrefix + type, '');
                    data.push(id);
                }
                if (data.length != 0) {
                    return {
                        type: type,
                        data: data
                    }
                } else {
                    return null;
                }
            },

            setValue: function setValue_function(value) {
                var node = YAHOO.util.Selector.query('.active', this.id + '-switch-type-container', true);
                var type = node.id.replace(this.switchTypePrefix, '');
                if (value === null) {
                    this._switchType(type, "week-days");
                    return;
                }
                this._switchType(type, value.type);
                for (var i in value.data) {
                    var node = value.data[i];
                    Dom.addClass(this.typeContainerPrefix + value.type + node, 'checked');
                }
            },

            getSummary: function getSummary_function(value) {
                if (value === null) {
                    return "(" + Alfresco.util.message("label.reiteration.not-specified") + ")";
                }
                var summary = ""
                if (value.type === "week-days") {
                    summary += Alfresco.util.message("label.schedule.form.week-days-type") + ", ";
                    for (var i in value.data) {
                        var index = parseInt(value.data[i] - 1);
                        summary += this.daysFull[index] + ', ';
                    }
                } else if (value.type === "month-days") {
                    summary += Alfresco.util.message("label.schedule.form.month-days-type") + ", " + Alfresco.util.message("date-unit.plural.day") +": ";
                    for (var i in value.data) {
                        summary += value.data[i] + ', ';
                    }
                }
                summary = summary.substring(0, summary.length - 2);
                return summary;
            },

            updateSummary: function updateSummary_function() {
                var value = this.getValue();
                var summary = this.getSummary(value);
                Dom.get(this.id + '-summary').innerHTML = summary;

            },

            openDialog: function openDialog_function() {
                if (this.panel == null) {this.id + '-dialog-panel'
                    this.panel = new YAHOO.widget.SimpleDialog("simpledialog1", {
                        width: "150px",
                        fixedcenter: true,
                        visible: false,
                        draggable: true,
                        close: true,
                        model: true,
                        constraintoviewport: true,
                        buttons: [
                            { text: Alfresco.util.message("form.button.submit.label"), handler: this.onOk.bind(this), isDefault:true },
                            { text: Alfresco.util.message("button.cancel"),  handler: this.onCancel.bind(this)}
                        ]
                    });

                    this.panel.setHeader(Alfresco.util.message("label.reiteration.repeat"));

                    var html  = '<div id="' + this.id + '-dialog-panel-container" class="reiteration">'
                    html += '<div id="' + this.id + '-switch-type-container" style="text-align: center"><span id="' + this.switchTypePrefix +'week-days" class="button active">' + Alfresco.util.message("label.schedule.form.week-days-type") + '</span><span id="' + this.switchTypePrefix +'month-days" class="button">' + Alfresco.util.message("label.schedule.form.month-days-type") + '</span></div>';
                    html += '<div class="delim">&nbsp;</div>';
                    html += '<div id="' + this.typeContainerPrefix +'week-days" class="container hidden1">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 7; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix +'week-days' + i + '">' + this.days[i - 1] + '</div>';
                    }
                    html += '</div>';
                    html += '</div>';
                    html += '<div id="' + this.typeContainerPrefix +'month-days" class="container hidden1">';
                    html += '<div class="container-aligment">';
                    for (var i = 1; i <= 31; i++) {
                        html += '<div class="item" id="' + this.typeContainerPrefix +'month-days' + i + '">' + i + '</div>';
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

                    var items = YAHOO.util.Selector.query('.item', this.id + '-dialog-panel-container')
                    Event.addListener(items, 'click', this.onItemClick, {}, this);

                    this._switchType(null, 'month-days');
                    this._switchType('month-days', 'week-days')
                }
                this.setValue(this.getControlValue());
                this.updateSummary();
                this.panel.show();
            },

            onOk: function onOk_function() {
                var value = this.getValue();
                this.updateValue(value);
                this.panel.hide();
            },

            onCancel: function onCancel() {
                this.panel.hide();
            },

            onChangeType: function onChangeType_function(ev, args) {
                var from = this.currentType;
                var to = args.type;
                this._switchType(from, to);
            },

            _switchType: function _switchType(from, to) {
                if (from != null) {
                    Event.addListener(this.switchTypePrefix + from, 'click', this.onChangeType, {type: from}, this);
                    Dom.removeClass(this.switchTypePrefix + from, 'active');
                    Dom.addClass(this.typeContainerPrefix + from, 'hidden1');
                    var nodes = YAHOO.util.Selector.query('.item.checked', this.typeContainerPrefix + from);
                    Dom.removeClass(nodes, 'checked');
                }
                if (to != null) {
                    this.currentType = to;
                    Event.removeListener(this.switchTypePrefix + to);
                    Dom.addClass(this.switchTypePrefix + to, 'active');
                    Dom.removeClass(this.typeContainerPrefix + to, 'hidden1');
                }
                this.updateSummary();
            },

            onItemClick: function onItemClick_function(ev){
                if (Dom.hasClass(ev.target, 'checked')) {
                    Dom.removeClass(ev.target, 'checked');
                } else {
                    Dom.addClass(ev.target, 'checked');
                }
                this.updateSummary();
            },

			onReadonlyControl: function (layer, args) {
				var ahref, input, fn;
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					ahref = Dom.get(this.id + '-displayValue');
					input = Dom.get(this.id);
					if (ahref) {
						fn = args[1].readonly ? Dom.addClass : Dom.removeClass;
						fn.call(Dom, ahref, "disabled");
					}
					if (input) {
						fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
						fn.call(input, "readonly", "");
					}
				}
			},

            onHideControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    Dom.setStyle(this.id + "-parent", "display", "none");
                }
            },

            onShowControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    Dom.setStyle(this.id + "-parent", "display", "block");
                }
            }
        });
})();
