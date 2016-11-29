if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};

(function () {
    LogicECM.module.SubmitFunction = function (htmlId) {
        LogicECM.module.SubmitFunction.superclass.constructor.call(this, "LogicECM.module.SubmitFunction", htmlId);
        return this;
    };

    YAHOO.extend(LogicECM.module.SubmitFunction, Alfresco.component.Base, {
        options: {
            submitFunction: null,
            form: null,
            nodeRef: null,
            mode: null
        },

        submitFormFunction: null,
        submitButton: null,
        formsRuntime: null,

        init: function () {
            if (this.options.form && this.options.form.formsRuntime) {
                this.formsRuntime = this.options.form.formsRuntime;

                if (YAHOO.lang.isFunction(this.options.submitFunction)) {
                    this.submitButton = this.formsRuntime.submitElements[0];
                    this.submitFormFunction = this.submitButton.submitForm;

                    this.submitButton.submitForm = this.options.submitFunction.bind(this, this.submitFormFunction, this.submitButton, this.formsRuntime);
                }
            }
        }
    });
})();