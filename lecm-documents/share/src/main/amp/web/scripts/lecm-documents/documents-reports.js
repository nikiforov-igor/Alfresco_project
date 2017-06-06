if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Documents = LogicECM.module.Documents|| {};
LogicECM.module.Documents.Reports = LogicECM.module.Documents.Reports || {};

(function() {
    var doubleClickLock = false;

    LogicECM.module.Documents.Reports.openReport = function (actionUrl, params) {
        var newWindow = window.open(Alfresco.constants.URL_PAGECONTEXT + "lecm/arm/blank", "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no,resizable=yes");
        var loadReport = function () {
            var newDocument = newWindow.document;
            if (newDocument.URL !== "" && newDocument && newDocument.body) {
                var body = newDocument.body;
                var newform = newDocument.createElement("form");
                newform.action = actionUrl;
                newform.method = "POST";
                newform.enctype = "multipart/form-data";
                body.appendChild(newform);
                var paramsField = newDocument.createElement("input");
                paramsField.type = "hidden";
                paramsField.name = "json_form_parameters";
                paramsField.value = params != null ? encodeURIComponent(JSON.stringify(params)) : "";
                newform.appendChild(paramsField);
                newform.submit();
            } else {
                setTimeout(loadReport, 1000);
            }
        };
        loadReport();
    };

    LogicECM.module.Documents.Reports.reportLinkClicked = function (element, param) {
        if (doubleClickLock) {
            return;
        }
        doubleClickLock = true;

        var reportCode = param.reportCode;
        var documentRef = param.nodeRef;

        var actionUrl = null;
        var doBeforeDialogShow = function (p_form, p_dialog) {
            try {
                var defaultMsg = Alfresco.component.Base.prototype.msg("documents.report." + reportCode + ".title");
                if (defaultMsg == "documents.report." + reportCode + ".title"){
                    defaultMsg = Alfresco.component.Base.prototype.msg("document.report.default.title");
                }
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", defaultMsg ]
                );

                var formElement = Dom.get(p_form.formId);
                if (formElement) {
                    actionUrl = formElement.action;
                    if (actionUrl && actionUrl.indexOf("autoSubmit") > 0) {
                        var data = {};
                        if (documentRef) { // добавляем к параметрам ID - если задано
                            data["ID"] = documentRef;
                        }
                        p_dialog.dialog.subscribe('show', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                        LogicECM.module.Documents.Reports.openReport(actionUrl, data);
                    } else {
                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    }
                }
            } catch (e) {
                Alfresco.logger.error("Error on load report form: ", e);
            } finally {
                doubleClickLock = false;
            }
        };

        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/components/form/report";
        var templateRequestParams = {
            itemKind: "type",
            itemId: reportCode,
            formId: "printReportForm",
            mode: "create",
            submitType: "json",
            showCancelButton: true
        };

        var printReportForm = new Alfresco.module.SimpleDialog(reportCode + "-reportForm");

        printReportForm.setOptions({
            width: "60em",
            templateUrl: templateUrl,
            templateRequestParams: templateRequestParams,
            destroyOnHide: true,
            doBeforeDialogShow: {
                fn: doBeforeDialogShow,
                scope: this
            },
            ajaxSubmitMethod: "GET",
            doBeforeAjaxRequest: {
                fn: function (form) {
                    var renameProperty = function (dataObj, name) {
                        if (name.indexOf("_removed") > 0 ||
                            name.indexOf("-selectedItems") > 0 ||
                            name.indexOf("-autocomplete-input") > 0) { //не может быть удаленных значений, да и они не нужны
                            delete dataObj[name];
                        } else if (name.indexOf("_added") > 0) { // что-то выбрано для ассоциаций
                            var newName = name.replace("_added", "");
                            dataObj[newName] = dataObj[name];
                            delete dataObj[name];
                        }
                    };
                    for (var property in form.dataObj) {
                        if (form.dataObj.hasOwnProperty(property)) {
                            renameProperty(form.dataObj, property);
                        }
                    }
                    if (documentRef) { // добавляем к параметрам ID - если задано
                        form.dataObj["ID"] = documentRef;
                    }
                    form.method = "GET";
                    LogicECM.module.Documents.Reports.openReport(actionUrl, form.dataObj);
                    printReportForm.hide();
                    return false;
                }
            }
        });
        printReportForm.show();
    };
})();
