if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.TextFieldWithReset = function (fieldHtmlId) {
        LogicECM.module.TextFieldWithReset.superclass.constructor.call(this, "LogicECM.module.TextFieldWithReset", fieldHtmlId, ["container", "datasource"]);

        YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.TextFieldWithReset, Alfresco.component.Base, {

        options: {
            defaultValue: null,
            defaultValueDataSource: null,
            fieldId: null,
            parentId: null,
            disabled: false,
            controlId: null,
            resetBtnId: null,
            resetButtonTitle: Alfresco.util.message("label.control.restore.byDefault"),
            resetButtonLabel: Alfresco.util.message("label.control.byDefault"),
            formId: null
        },

        textField: null,
        defaultValue: null,

        onReInitializeControl: function (layer, args) {
            var formId = args[1].formId;
            var fieldId = args[1].fieldId;
            var options = args[1].options;
            if (this.options.formId == formId && this.options.fieldId == fieldId) {
                this.options = YAHOO.lang.merge(this.options, options);
                this._init();
            }
        },

        onReady: function () {
            this._init();
            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
        },

        _init: function () {
            this.textField = Dom.get(this.id);
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
                    this.loadDefaultValue();
                }
            }
        },

        resetValue: function () {
            this.updateField(true);
        },

        loadDefaultValue: function () {
            if (this.options.defaultValue != null) {
                this.defaultValue = this.options.defaultValue;
                this.updateField(false);
            } else if (this.options.defaultValueDataSource) {
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource
                    + (this.options.defaultValueDataSource.indexOf("?") != -1 ? "&" : "?") + "id=" + encodeURIComponent(this.options.parentId),
                    successCallback: {
                        scope: this,
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults && oResults.value) {
                                this.defaultValue = oResults.value;
                            }
                            this.updateField(false);
                        }
                    },
                    failureMessage: this.msg("message.failure")
                });
            }
        },

        updateField: function (reset) {
            if (this.textField.value == "" || reset) {
                this.textField.value = this.defaultValue;
            }
        }
    });
})();
