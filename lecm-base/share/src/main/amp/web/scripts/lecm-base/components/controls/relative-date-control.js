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

        radioValue: null,
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
            days: null,
            mode: "RELATIVE"
        },

        onDateSelected: function (layer, args) {
            this.onValueChanged(layer);
        },

        onRadioValueChanged: function (layer) {
            if (layer.target && layer.target.value) {
                this.controlStateValue.mode = layer.target.value;
                this.updateValue();

                if ("RELATIVE" == this.controlStateValue.mode) {
                    this.daysInput.setAttribute("disabled", false);
                    this.daysModeSelect.setAttribute("disabled", false);

                    if (this.datePicker) {
                        this.datePicker.onDisableControl(null, [null, {
                            formId: this.datePicker.options.formId,
                            fieldId: this.datePicker.options.fieldId
                        }])
                    }
                } else {
                    this.daysInput.value = "";
                    this.daysInput.setAttribute("disabled", true);
                    this.daysModeSelect.value = "";
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

        onValueChanged: function (layer) {
            this.updateValue();
        },

        onReady: function () {
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);

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

            this.daysInput = YAHOO.util.Dom.get(this.id + "-days");
            if (this.daysInput) {
                YAHOO.util.Event.addListener(this.daysInput, "change", this.onValueChanged, null, this);
            }

            this.daysModeSelect = YAHOO.util.Dom.get(this.id + "-daysMode");
            if (this.daysModeSelect) {
                YAHOO.util.Event.addListener(this.daysModeSelect, "change", this.onValueChanged, null, this);
            }

            this.dateInput = YAHOO.util.Dom.get(this.id + "-date-value");
        },

        updateValue: function () {
            this.controlStateValue.mode = this.daysModeSelect.value;
            this.controlStateValue.date = this.dateInput.value;
            this.controlStateValue.days = this.daysInput.value;

            YAHOO.util.Dom.get(this.id).value = YAHOO.lang.JSON.stringify(this.controlStateValue);
        },

        getValue: function () {
            return YAHOO.lang.JSON.stringify(this.controlStateValue);
        }
    });
})();
