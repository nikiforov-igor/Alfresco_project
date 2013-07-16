(function() {
    LogicECM.module.Documents.Reports = LogicECM.module.Documents.Reports || {};

    LogicECM.module.Documents.Reports.reportLinkClicked = function(element, param) {
        var reportCode = param.reportCode;
        var doBeforeDialogShow = function (p_form, p_dialog) {
            Alfresco.util.populateHTML(
                [p_dialog.id + "-form-container_h", Alfresco.component.Base.prototype.msg("contracts.report." + reportCode + ".title")]
            );
        };

        var url = "/lecm/components/form/report" +
            "?itemKind={itemKind}" +
            "&itemId={itemId}" +
            "&formId={formId}" +
            "&mode={mode}" +
            "&submitType={submitType}" +
            "&showCancelButton=true" +
            "&showResetButton=false" +
            "&showSubmitButton=true";
        var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
            itemKind: "type",
            itemId: reportCode,
            formId: "printReportForm",
            mode: "create",
            submitType: "json"
        });

        var printReportForm = new Alfresco.module.SimpleDialog(reportCode + "-reportForm");

        printReportForm.setOptions({
            actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/" + reportCode,
            width: "50em",
            templateUrl: templateUrl,
            destroyOnHide: true,
            doBeforeDialogShow: {
                fn: doBeforeDialogShow,
                scope: this
            },
            ajaxSubmitMethod: "GET",
            doBeforeAjaxRequest: {
                fn: function (form) {
                    form.method = "GET";
                    return true;
                }
            },
            onSuccess: {
                fn: function ContractReports_onSuccess(response) {
                    window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText, "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                    Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.component.Base.prototype.msg("documents.report.success")
                    });
                },
                scope: this
            },
            onFailure: {
                fn: function ContractReports_onFailure(response) {
                    Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.component.Base.prototype.msg("documents.report.failure")
                    });
                },
                scope: this
            }
        });
        printReportForm.show();

    };
})();
