if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.RelativeDateController = function (htmlId) {
        LogicECM.module.RelativeDateController.superclass.constructor.call(this, "LogicECM.module.RelativeDateController", htmlId);

        YAHOO.Bubbling.on("dateSelected", this.onDateSelected, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.RelativeDateController, Alfresco.component.Base, {
        datePicker: null,
        dateInput: null,
        daysInput: null,
        daysModeSelect: null,

        options: {
            formId: null,
            fieldId: null,
            fireChangeEventName: null,
            fieldName: null
        },

        controlStateValue: {
            type: "RELATIVE_DATE",
            daysMode: "WORK",
            date: null,
            days: 0,
            mode: "RELATIVE"
        },

        onDateSelected: function () {
            this.onValueChanged();
        },

        onRadioValueChanged: function (layer) {
            if (layer.target && layer.target.value) {
                this.controlStateValue.mode = layer.target.value;

                this.updateValue();

                if ("RELATIVE" == this.controlStateValue.mode) {
                    if (this.daysModeSelect){
                        this.daysModeSelect.removeAttribute("disabled");
                    }

                    if (this.daysInput) {
                        this.daysInput.removeAttribute("disabled");
                        YAHOO.Bubbling.fire("registerValidationHandler",
                            {
                                fieldId: this.daysInput.id,
                                handler: LogicECM.constraints.isNumber,
                                when: "change"
                            });
                    }

                    if (this.datePicker) {
                        this.datePicker.onDisableControl(null, [null, {
                            formId: this.datePicker.options.formId,
                            fieldId: this.datePicker.options.fieldId
                        }])
                    }
                } else {
                    if (this.daysInput) {
                        YAHOO.Bubbling.fire("registerValidationHandler",
                            {
                                fieldId: this.daysInput.id,
                                handler: LogicECM.constraints.notMandatory,
                                when: "change"
                            });
                    }

                    this.daysInput.setAttribute("disabled", true);
                    this.daysModeSelect.setAttribute("disabled", true);

                    if (this.datePicker) {
                        this.datePicker.onEnableControl(null, [null, {
                            formId: this.datePicker.options.formId,
                            fieldId: this.datePicker.options.fieldId
                        }])
                    }
                }
            }
        },

        onValueChanged: function () {
            this.updateValue();
        },

        onReady: function () {
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);

            this.daysInput = YAHOO.util.Dom.get(this.id + "-days");
            if (this.daysInput) {
                YAHOO.util.Event.addListener(this.daysInput, "change", this.onValueChanged, null, this);
            }

            this.daysModeSelect = YAHOO.util.Dom.get(this.id + "-daysMode");
            if (this.daysModeSelect) {
                YAHOO.util.Event.addListener(this.daysModeSelect, "change", this.onValueChanged, null, this);
            }

            this.dateInput = YAHOO.util.Dom.get(this.id + "-date-value");

            var radioButtons = YAHOO.util.Selector.query('input[name="' + this.options.fieldName + '-radio"]', this.id + "-radio-parent");
            if (radioButtons && radioButtons.length) {
                for (var i = 0; i < radioButtons.length; i++) {
                    YAHOO.util.Event.addListener(radioButtons[i], "click", this.onRadioValueChanged, null, this);
                    if (radioButtons[i].checked) {
                        this.onRadioValueChanged({
                            target: radioButtons[i]
                        });
                    }
                }
            }
        },

        updateValue: function () {
            this.controlStateValue.daysMode = this.daysModeSelect.value;
            this.controlStateValue.date = this.dateInput.value;
            this.controlStateValue.days = this.daysInput.value;

            YAHOO.util.Dom.get(this.id).value = this.getValue();
        },

        getValue: function () {
            return YAHOO.lang.JSON.stringify(this.controlStateValue);
        },

        setValue: function (config) {
            this.controlStateValue = config;
        }
    });
})();
