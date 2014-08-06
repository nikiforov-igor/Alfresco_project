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
    var LogicECM = {};
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
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Base.Reiteration = function (htmlId) {
        LogicECM.module.Base.Reiteration.superclass.constructor.call(this, "LLogicECM.module.Base.Reiteration", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Base.Reiteration, Alfresco.component.Base,
        {
            options: {
                value: {}
            },

        onReady: function () {

            var elements = YAHOO.util.Selector.query("input", this.id + "-control-buttons");
            for (var i = 0; i < elements.length; i++) {
                YAHOO.util.Event.addListener(elements[i], "click", this.onRadioClicked.bind(this));
            }

            var elements = YAHOO.util.Selector.query("td", this.id + "-month-days-mode");
            for (var i = 0; i < elements.length; i++) {
                YAHOO.util.Event.addListener(elements[i], "click", this.onDateClicked.bind(this));
            }

            YAHOO.util.Event.addListener("working-days", "change", this.updateValue.bind(this));
            YAHOO.util.Event.addListener("non-working-days", "change", this.updateValue.bind(this));

            var elements = YAHOO.util.Selector.query("input", this.id + "-week-days-mode");
            for (var i = 0; i < elements.length; i++) {
                YAHOO.util.Event.addListener(elements[i], "click", this.updateValue.bind(this));
            }

            var valueObject = this.options.value;
            if (valueObject != null && valueObject.type != null) {
                if (valueObject.type === "week-days") {
                    for (var i in valueObject.data) {
                        var index = valueObject.data[i];
                        YAHOO.util.Dom.get(this.id + "-week-days-mode-" + index).checked = true;
                    }
                } else if (valueObject.type === "month-days") {
                    YAHOO.util.Dom.get(this.id + "-control-month-days").click();
                    var monthDaysInput = YAHOO.util.Dom.get(this.id + "-month-days-input");
                    monthDaysInput.value = valueObject.data.join(",");
                    var elements = YAHOO.util.Selector.query("td", this.id + "-month-days-mode");

                    for (var i = 0; i < elements.length; i++) {
                        if (valueObject.data.indexOf(elements[i].textContent) != -1) {
                            elements[i].setAttribute("class", "selected-date");
                        }
                    }
                }
            }
            this.updateValue.bind(this);
        },

        onRadioClicked: function onRadioClicked_function(event) {
            var monthDays = new YAHOO.util.Element(this.id + '-month-days-mode');
            var weekDays = new YAHOO.util.Element(this.id + '-week-days-mode');
            var shiftWork = new YAHOO.util.Element(this.id + '-shift-work-mode');

            var value = event.toElement.value;

            if (value === "week-days") {
                monthDays.setStyle("display", "none");
                weekDays.setStyle("display", "block");
                shiftWork.setStyle("display", "none");
            } else if (value === "month-days") {
                monthDays.setStyle("display", "block");
                weekDays.setStyle("display", "none");
                shiftWork.setStyle("display", "none");
            } else if (value === "shift-work") {
                monthDays.setStyle("display", "none");
                weekDays.setStyle("display", "none");
                shiftWork.setStyle("display", "block");
            } else {
                alert(value);
            }

            this.updateValue();
        },

        onDateClicked: function onDateClicked_function(event) {
            var monthDaysInput = YAHOO.util.Dom.get(this.id + "-month-days-input");

            var selectedDates = monthDaysInput.value.split(",");
            dateSelected = event.toElement.textContent;
            // Дата, которую только что выбрали, уже есть в списке выбранных
            if (selectedDates.indexOf(dateSelected) > -1) {
                var newMonthDaysInput = "";
                event.toElement.removeAttribute("class", 0);
                for (var j = 0; j < selectedDates.length; j++) {
                    if (selectedDates[j] === dateSelected) {
                        continue;
                    }
                    if (newMonthDaysInput.length > 0) {
                        newMonthDaysInput += ",";
                    }
                    newMonthDaysInput += selectedDates[j];
                }
                monthDaysInput.value = newMonthDaysInput;
            } else {
                if (monthDaysInput.value.length > 0) {
                    monthDaysInput.value += "," + dateSelected;
                } else {
                    monthDaysInput.value += event.toElement.textContent;
                }
                event.toElement.setAttribute("class", "selected-date");
            }

            this.updateValue();
        },

        updateValue: function updateValue_function() {
            var value = this.getValue();
            var hasError = this.checkErrors(value);
            if (!hasError) {
                YAHOO.util.Dom.get(this.id).value = JSON.stringify(value);
            } else {
                YAHOO.util.Dom.get(this.id).value = "";
            }

        },

        getValue: function getValue_function() {
            var reiterationType = YAHOO.util.Dom.getElementsBy(function(el) {
                return (el.name === 'reiteration-type' && el.checked);
            }, 'input', this.id + '-control-buttons', null, null, null, true);

            if (reiterationType.value === "week-days") {
                var daysChecked = YAHOO.util.Dom.getElementsBy(function(el) {
                    return (el.type === 'checkbox' && el.checked);
                }, 'input', this.id + '-week-days-mode');
                var value = {
                    type: "week-days",
                    data: []
                };
                for (var i in daysChecked) {
                    value.data.push(daysChecked[i].value);
                }
                return value;
            } else if (reiterationType.value === "month-days") {
                var monthDays = YAHOO.util.Dom.get(this.id + '-month-days-input');
                var value = {
                    type: "month-days",
                    data: monthDays.value !== "" ? monthDays.value.split(",") : []
                }
                return value;
            }/* else if (reiterationType.value === "shift-work") {
                var workingDays = YAHOO.util.Dom.get("working-days");
                var nonWorkingDays = YAHOO.util.Dom.get("non-working-days");

                var workingDaysNum = Number(workingDays.value);
                var nonWorkingDaysNum = Number(nonWorkingDays.value);
            }*/
        },

        checkErrors: function checkErrors_function(value) {
            var valid = true;
            var errorMessage;

            var errorContainer = YAHOO.util.Dom.get(this.id + '-reiteration-rules-error-container');
            errorContainer.innerHTML = "";

            if (value.type === "week-days") {
                if (value.data.length < 1) {
                    valid = false;
                    errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.week-days");
                }
            } else if (value.type === "month-days") {
                if (value.data.length < 1) {
                    valid = false;
                    errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.month-days");
                }
            } else if (reiterationType.value === "shift-work") {
                var workingDays = YAHOO.util.Dom.get("working-days");
                var nonWorkingDays = YAHOO.util.Dom.get("non-working-days");

                var workingDaysNum = Number(workingDays.value);
                var nonWorkingDaysNum = Number(nonWorkingDays.value);

                if (isNaN(workingDaysNum) || isNaN(nonWorkingDaysNum) || workingDaysNum < 1 || nonWorkingDaysNum < 1) {
                    valid = false;
                    errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.shift-work");
                }
            } else {
                valid = false;
                errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.reiteration-type");
            }

            if (!valid && errorMessage.length) {
                errorContainer.innerHTML = errorMessage;
                return true;
            } else {
                return false;
            }
        }
    });
})();




