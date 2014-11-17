/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.Contractors = LogicECM.module.Contractors || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Contractors.CheckDuplicates = function (htmlId) {
        LogicECM.module.Contractors.CheckDuplicates.superclass.constructor.call(this, "LogicECM.module.Contractors.CheckDuplicates", htmlId, ["container", "json"]);

        return this;
    };

    YAHOO.extend(LogicECM.module.Contractors.CheckDuplicates, Alfresco.component.Base,
        {
            options: {
                nodeRef: null,
                submitButton: null,
                rootElement: null
            },

            submitFormFunction: null,
            submitButton: null,

            init: function () {
                this.submitButton = this.options.submitButton;
                this.submitFormFunction = this.submitButton.submitForm;
                this.submitButton.submitForm = this.requestCheckData.bind(this);
            },

            requestCheckData: function requestCheckData_function() {
                var inn = document.getElementById(this.options.rootElement + "_prop_lecm-contractor_INN").value;
                if (inn == null || inn == "") {
                    this._submit();
                } else {
                    var me = this;
                    var url = Alfresco.constants.PROXY_URI + "/lecm/contractors/inn?number=" + encodeURIComponent(inn) + "&nodeRef=" + encodeURIComponent(this.options.nodeRef);
                    var callback = {
                        success: function (oResponse) {
                            var oResults = eval("(" + oResponse.responseText + ")");
                            if (oResults.length > 0) {
                                var message = "В системе найдены Контрагенты с совпадающим ИНН:<br/>";
                                for (var item in oResults) {
                                    message += oResults[item] + "<br/>";
                                }
                                Alfresco.util.PopupManager.displayPrompt(
                                    {
                                        title: "Найдены контрагенты с одинаковым ИНН",
                                        text: message,
                                        noEscape: true,
                                        buttons: [
                                            {
                                                text: "Ок",
                                                handler: function dlA_onAction_action() {
                                                    this.destroy();
                                                    me._submit();
                                                }
                                            },
                                            {
                                                text: "Отмена",
                                                handler: function dlA_onActionDelete_cancel() {
                                                    this.destroy();
                                                },
                                                isDefault: true
                                            }
                                        ]
                                    });
                            } else {
                                me._submit();
                            }
                        },
                        argument: {
                            parent: this
                        },
                        timeout: 60000
                    };
                    YAHOO.util.Connect.asyncRequest('GET', url, callback);
                }
            },

            _submit: function _submit_function() {
                this.submitButton.submitForm = this.submitFormFunction;
                this.submitButton.submitForm();
            }
        });
})();