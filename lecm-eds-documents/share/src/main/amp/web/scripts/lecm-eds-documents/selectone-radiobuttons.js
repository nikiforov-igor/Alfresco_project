if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.SelectOneRadioButtonsController = function (htmlId) {
        LogicECM.module.SelectOneRadioButtonsController.superclass.constructor.call(this, "LogicECM.module.SelectOneRadioButtonsController", htmlId);

        YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
        YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOneRadioButtonsController, Alfresco.component.Base, {

        options: {
            formId: null,
            fieldId: null,
            fireChangeEventName: null,
            fieldName: null
        },

        onValueChanged: function (layer) {
            if (layer.target && layer.target.value) {
                var value = layer.target.value;

                YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
                YAHOO.util.Dom.get(this.id).value = value;

                YAHOO.Bubbling.fire(this.options.fireChangeEventName, {
                    value: value,
                    formId: this.options.formId,
                    fieldId: this.options.fieldId
                });
            }
        },

        onDisableControl: function (layer, args) {
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                var input = Dom.get(this.id);
                if (input) {
                    input.setAttribute("disabled", "true");
                    input.value = "";
                }
				var radios = YAHOO.util.Selector.query('input[name="' + this.options.fieldName + '"]', this.id + "-parent");
				if (radios && radios.length) {
					radios.forEach(function(el) {
						el.setAttribute("disabled", "true");
					});
				}
            }
        },

        onEnableControl: function (layer, args) {
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                if (!this.options.disabled) {
                    var input = Dom.get(this.id);
                    if (input) {
                        input.removeAttribute("disabled");
                    }
					var radios = YAHOO.util.Selector.query('input[name="' + this.options.fieldName + '"]', this.id + "-parent");
					if (radios && radios.length) {
						radios.forEach(function(el) {
							el.removeAttribute("disabled");
						});
					}
                }
            }
        },

        onReady: function () {
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            if (this.options.fieldName) {
                var radioButtons = YAHOO.util.Selector.query('input[name="' + this.options.fieldName + '"]', this.id + "-parent");
                if (radioButtons && radioButtons.length) {
                    for (var i = 0; i < radioButtons.length; i++) {
                        YAHOO.util.Event.addListener(radioButtons[i], "click", this.onValueChanged, null, this);
                        if (radioButtons[i].checked) {
                            this.onValueChanged({
                                target: radioButtons[i]
                            });
                        }
                    }
                }
            }
        }
    });
})();
