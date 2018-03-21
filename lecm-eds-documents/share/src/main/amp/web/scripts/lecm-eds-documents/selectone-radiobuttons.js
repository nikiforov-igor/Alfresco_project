if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.SelectOneRadioButtonsController = function (htmlId) {
        LogicECM.module.SelectOneRadioButtonsController.superclass.constructor.call(this, "LogicECM.module.SelectOneRadioButtonsController", htmlId);

        YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
        YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
        YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);

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
            var value;
            if (layer.target && layer.target.value) {
                value = layer.target.value;

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
            var input;
            var radios;
            if ((this.options.formId === args[1].formId) && (this.options.fieldId === args[1].fieldId)) {
                input = Dom.get(this.id);
                if (input) {
                    input.setAttribute("disabled", "true");
                    input.value = "";
                }
				radios = this._getRadios();
				if (radios && radios.length) {
					radios.forEach(function(el) {
						el.setAttribute("disabled", "true");
					});
				}
            }
        },

        onEnableControl: function (layer, args) {
            var input;
            var radios;
            if ((this.options.formId === args[1].formId) && (this.options.fieldId === args[1].fieldId)) {
                if (!this.options.disabled) {
                    input = Dom.get(this.id);
                    if (input) {
                        input.removeAttribute("disabled");
                    }
                    radios = this._getRadios();
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
            var radios = this._getRadios();
            var me = this;
            if (radios && radios.length) {
                radios.forEach(function(radio) {
                    YAHOO.util.Event.addListener(radio, "click", me.onValueChanged, null, me);
                    if (radio.checked) {
                        me.onValueChanged({
                            target: radio
                        });
                    }
                });
            }
        },

        onReInitializeControl: function (layer, args) {
            var radios;
            var radiosCount;
            var radio;
            var currentValue;
            if ((this.options.formId == args[1].formId) && (this.options.fieldId == args[1].fieldId)) {
                var options = args[1].options;
                if (options != null) {
                    this.setOptions(options);
                }
                radios = this._getRadios();
                if (radios && radios.length) {
                    currentValue = this.options.currentValue;
                    if (currentValue) {
                        radiosCount = radios.length;
                        for (var i = 0; i < radiosCount; i++) {
                            if (radios[i].value == currentValue) {
                                radio = radios[i];
                                break;
                            }
                        }
                    }
                    if (!radio) {
                        radio = radios[0];
                    }
                    radio.checked = true;
                    this.onValueChanged({
                        target: radio
                    });
                }
            }
        },

        _getRadios: function() {
            var radios = null;
            if (this.options.fieldName) {
                radios = YAHOO.util.Selector.query('input[name="' + this.options.fieldName + '"]', this.id + "-parent");
            }
            return radios;
        }

    });
})();
