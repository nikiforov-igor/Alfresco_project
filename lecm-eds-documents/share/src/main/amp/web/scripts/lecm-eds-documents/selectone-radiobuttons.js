if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.SelectOneRadioButtonsController = function (htmlId) {
        LogicECM.module.SelectOneRadioButtonsController.superclass.constructor.call(this, "LogicECM.module.SelectOneRadioButtonsController", htmlId);

        Bubbling.on("disableControl", this.onDisableControl, this);
        Bubbling.on("enableControl", this.onEnableControl, this);
        Bubbling.on("reInitializeControl", this.onReInitializeControl, this);
        Bubbling.on("resetControl", this.onResetControl, this);
        Bubbling.on("readonlyControl", this.onReadonlyControl, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOneRadioButtonsController, Alfresco.component.Base, {
        originalOptions: null,
        options: {
            formId: null,
            fieldId: null,
            fireChangeEventName: null,
            fieldName: null,
            readonly: false,
            currentValue: null
        },

        onReadonlyControl: function (layer, args) {
            var input, fn;
            var radios;
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                this.readonly = args[1].readonly;
                input = Dom.get(this.controlId);
                if (input) {
                    fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
                    fn.call(input, "readonly", "");
                }
                radios = this._getRadios();
                if (radios && radios.length) {
                    if(args[1].readonly){
                        radios.forEach(function(el) {
                            if(!el.checked){//радиобаттоны не могут быть в состоянии ридонли, дизейблим все не выбранные
                                el.setAttribute("disabled", "true");
                            }
                        });
                    } else {
                        radios.forEach(function(el) {
                            el.removeAttribute("disabled");
                        });
                    }
                }
            }
        },

        onValueChanged: function (layer) {
            var value;
            if (layer.target && layer.target.value) {
                value = layer.target.value;

                Bubbling.fire('mandatoryControlValueUpdated', this);
                YAHOO.util.Dom.get(this.id).value = value;

                Bubbling.fire(this.options.fireChangeEventName, {
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
                        me.options.currentValue = radio.value;
                    }
                });
            }
            this.originalOptions = Alfresco.util.deepCopy(this.options);
            this.init();
        },

        init: function() {
            var radio;
            var radios = this._getRadios();
            var radiosCount = radios.length;
            var currentValue = this.options.currentValue;
            if (radios && radiosCount) {
                for (var i = 0; i < radiosCount; i++) {
                    radio = radios[i];
                    radio.checked = false;
                    if (radio.value == currentValue) {
                        radio.checked = true;
                        this.onValueChanged({
                            target: radio
                        });
                    }
                }
            }
        },

        onReInitializeControl: function (layer, args) {
            if ((this.options.formId == args[1].formId) && (this.options.fieldId == args[1].fieldId)) {
                var options = args[1].options;
                if (options != null) {
                    this.setOptions(options);
                }
                this.init();
            }
        },

        onResetControl: function (layer, args) {
            if ((this.options.formId == args[1].formId) && (this.options.fieldId == args[1].fieldId)) {
                args[1].action = "reInitializeControl";
                args[1].options = Alfresco.util.deepCopy(this.originalOptions);
                this.onReInitializeControl("reInitializeControl", args);
                Bubbling.fire('mandatoryControlValueUpdated', this);
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
