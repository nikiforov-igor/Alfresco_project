// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};

(function () {

	var Dom = YAHOO.util.Dom;

    LogicECM.module.TextField = function (htmlId) {
        LogicECM.module.TextField.superclass.constructor.call(this, "LogicECM.module.TextField", htmlId);
        this.controlId = htmlId;
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
        YAHOO.Bubbling.on("hideControl", this.onHideControl, this);
        YAHOO.Bubbling.on("showControl", this.onShowControl, this);
        YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
        YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.TextField, Alfresco.component.Base,
        {
			readonly: false,
            runtimeForm: null,
            controlId: null,
            isMandatory: false,

            options: {
                formId: null,
                fieldId: null,
                disabled: false,

                /*данные из формы*/
                objectNodeRef: null, // id объекта
                typeName: null, // тип объекта

                /*параметры валидации*/
                isUniqueValue: false,
                checkInArchive: false,
                validationType: 'keyup',
                validationMessageId: "LogicECM.constraints.isUnique.message",
                validationFn: null
            },

			onReadonlyControl: function (layer, args) {
				var input, fn;
				if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
					this.readonly = args[1].readonly;
					input = Dom.get(this.controlId);
					if (input) {
						fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
						fn.call(input, "readonly", "");
					}
				}
			},

            onHideControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    YAHOO.util.Dom.addClass(this.controlId + '-cntrl', 'hidden1');
                }
            },
            onShowControl: function (layer, args) {
                if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
                    YAHOO.util.Dom.removeClass(this.controlId + '-cntrl', 'hidden1');
                }
            },

            onDisableControl: function (layer, args) {
                if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                    var input = Dom.get(this.controlId);
                    if (input) {
                        input.setAttribute("disabled", "true");
                    }
                }
            },
            onEnableControl: function (layer, args) {
                if (this.options.formId === args[1].formId && this.options.fieldId === args[1].fieldId) {
                    if (!this.options.disabled) {
                        var input = Dom.get(this.controlId);
                        if (input) {
                            input.removeAttribute("disabled");
                        }
                    }
                }
            },

            onReady: function () {
                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);

                this.runtimeForm = Alfresco.util.ComponentManager.get(this.options.formId + "-form");

                if (this.options.isUniqueValue) {
                    if (this.runtimeForm && this.runtimeForm.formsRuntime && !this.options.disabled) {
                        if (this.runtimeForm.formsRuntime.formId === this.options.formId + "-form") {
                            // добавляем валидацию на поле

                            this.runtimeForm.formsRuntime.addValidation(
                                this.id, // fieldId
                                (this.options.validationFn != null && typeof this.options.validationFn == "function") ?
                                    this.options.validationFn : LogicECM.constraints.isUnique, /*validationHandler*/
                                {
                                    nodeRef: this.options.objectNodeRef,
                                    typeName: this.options.typeName,
                                    propertyName: this.options.fieldId,
                                    messageId: this.options.validationMessageId,
                                    checkInArchive: this.options.checkInArchive
                                }, // validationArgs
                                this.options.validationType, // when
                                Alfresco.util.message(this.options.validationMessageId)  // message
                            );
                        }
                    }
                }
            }
        });
})();
