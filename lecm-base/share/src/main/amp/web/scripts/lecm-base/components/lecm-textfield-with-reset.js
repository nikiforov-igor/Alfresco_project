if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.TextFieldWithReset = function (fieldHtmlId) {
        LogicECM.module.TextFieldWithReset.superclass.constructor.call(this, "LogicECM.module.TextFieldWithReset", fieldHtmlId, ["container", "datasource"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.TextFieldWithReset, Alfresco.component.Base, {

        options: {
            defaultValue: '',
            fieldId: null,
            disabled: false,
            controlId: null,
            resetBtnId: null,
            resetButtonTitle: "Восстановить значение по умолчанию",
            resetButtonLabel: "По умолчанию",
            formId: null
        },

        textField: null,

        onReady: function () {
            this.textField = Dom.get(this.options.fieldId);
            if (this.textField) {
                this.options.controlId = this.id + '-cntrl';
                this.options.resetBtnId = this.options.controlId + '-reset-button';

                // Create button if control is enabled
                if (!this.options.disabled) {
                    if (this.widgets.resetButton == null) {
                        var buttonOptions = {
                            onclick: {
                                fn: this.resetValue,
                                obj: null,
                                scope: this
                            },
                            title: this.options.resetButtonTitle
                        };

                        // Create picker button
                        var buttonName = Dom.get(this.options.resetBtnId).name;
                        this.widgets.resetButton = new YAHOO.widget.Button(this.options.resetBtnId, buttonOptions);
                        Dom.get(this.options.resetBtnId).name = buttonName;
                    }
                }
                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            }
        },


        resetValue: function () {
            this.textField.value = this.options.defaultValue;
        }
    });
})();
