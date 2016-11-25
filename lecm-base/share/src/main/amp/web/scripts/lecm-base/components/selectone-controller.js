if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.SelectOneController = function (htmlId) {
        LogicECM.module.SelectOneController.superclass.constructor.call(this, "LogicECM.module.SelectOneController", htmlId);

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
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            YAHOO.util.Event.addListener(this.id, "change", this.onValueChanged, null, this);
        },

        onValueChanged: function () {
            if (this.options.fireChangeEventName) {
                var input = Dom.get(this.id);
                YAHOO.Bubbling.fire(this.options.fireChangeEventName, {
                    element: input,
                    value: input.value,
                    selectone: this
                });
            }
        },

        onDisableControl: function (layer, args) {
            if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                var input = Dom.get(this.id);
                if (input) {
                    input.setAttribute("disabled", "true");
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
                    var input = Dom.get(this.id);
                    if (input) {
                        input.value = this.options.selectCurrentValue;
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
