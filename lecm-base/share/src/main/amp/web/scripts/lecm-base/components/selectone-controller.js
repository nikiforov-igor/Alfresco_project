if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.SelectOneController = function (htmlId) {
        LogicECM.module.SelectOneController.superclass.constructor.call(this, "LogicECM.module.SelectOneController", htmlId);

        YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
        YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
        YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
        YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);
        YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
        YAHOO.Bubbling.on("showControl", this.onShowControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.SelectOneController, Alfresco.component.Base, {

        options: {
            formId: null,
            fieldId: null,
            disabled: false,
            fireChangeEventName: null,
            selectCurrentValue: null
        },

        onReady: function () {
            this.widgets.inputEl = Dom.get(this.id);
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            YAHOO.util.Event.addListener(this.id, "change", this.onValueChanged, null, this);
        },

        onValueChanged: function () {
            if (this.options.fireChangeEventName && this.widgets.inputEl) {
                YAHOO.Bubbling.fire(this.options.fireChangeEventName, {
                    element: this.widgets.inputEl,
                    value: this.widgets.inputEl.value,
                    selectone: this
                });
            }
        },

		onReadonlyControl: function (layer, args) {
			var fn;
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
				if (this.widgets.inputEl) {
					fn = args[1].readonly ? this.widgets.inputEl.setAttribute : this.widgets.inputEl.removeAttribute;
					fn.call(this.widgets.inputEl, "readonly", "");
				}
			}
		},

        onDisableControl: function (layer, args) {
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                if (this.widgets.inputEl) {
                    this.widgets.inputEl.setAttribute("disabled", "true");
                }

            }
        },

        onEnableControl: function (layer, args) {
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                if (!this.options.disabled) {
                    if (this.widgets.inputEl) {
                        this.widgets.inputEl.removeAttribute("disabled");
                    }
                }
            }
        },

        onReInitializeControl: function (layer, args) {
            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                var options = args[1].options;
                if (options) {
                    this.setOptions(options);
                }

                if (this.options.selectCurrentValue) {
                    if (this.widgets.inputEl) {
                        this.widgets.inputEl.value = this.options.selectCurrentValue;
                    }
                }
            }
        },

        onHideControl: function (layer, args) {
            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                YAHOO.util.Dom.addClass(this.id + '-parent', 'hidden1');
            }
        },

        onShowControl: function (layer, args) {
            if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                YAHOO.util.Dom.removeClass(this.id + '-parent', 'hidden1');
            }
        }
    });
})();
