// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};

(function () {

    LogicECM.module.TextField = function (htmlId) {
        LogicECM.module.TextField.superclass.constructor.call(this, "LogicECM.module.TextField", htmlId);
        this.controlId = htmlId;

        return this;
    };

    YAHOO.extend(LogicECM.module.TextField, Alfresco.component.Base,
        {
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