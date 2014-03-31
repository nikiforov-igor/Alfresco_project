(function() {
    LogicECM.module.Documents.Reports = LogicECM.module.Documents.Reports || {};

    LogicECM.module.Documents.Reports.reportLinkClicked = function(element, param) {
        var reportCode = param.reportCode;
        var documentRef = param.nodeRef;
        var doBeforeDialogShow = function (p_form, p_dialog) {
            var defaultMsg = Alfresco.component.Base.prototype.msg("documents.report." + reportCode + ".title");
            if (defaultMsg == "documents.report." + reportCode + ".title"){
                defaultMsg = Alfresco.component.Base.prototype.msg("document.report.default.title");
            }
            Alfresco.util.populateHTML(
                [ p_dialog.id + "-form-container_h", defaultMsg ]
            );

            var formElement = Dom.get(p_form.formId);
            if (formElement) {
                var actionUrl = formElement.action;
                if (actionUrl && actionUrl.indexOf("autoSubmit") > 0) {
                    var data = {};
                    if (documentRef) { // добавляем к параметрам ID - если задано
                        data["ID"] = documentRef;
                    }
	                YAHOO.util.Dom.setStyle(p_dialog.dialog.id, "display", "none");
                    Alfresco.util.Ajax.request({
                        method: "GET",
                        url: actionUrl,
                        dataObj: data,
                        responseContentType: "text/html",
                        successCallback: {
                            fn: function _onSuccess(response) {
                                p_dialog.dialog.hide();
                                window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText, "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                                Alfresco.util.PopupManager.displayMessage({
                                    text: Alfresco.component.Base.prototype.msg("documents.report.success")
                                });
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function _onFailure(response) {
                                p_dialog.dialog.hide();
                                Alfresco.util.PopupManager.displayMessage({
                                    text: Alfresco.component.Base.prototype.msg("documents.report.failure")
                                });
                            }
                        }
                    });
                }
            }
	        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
            //YAHOO.util.Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
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
            //actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/" + reportCode,
            width: "50em",
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
                    return true;
                }
            },
            onSuccess: {
                fn: function _onSuccess(response) {
                    window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText, "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                    Alfresco.util.PopupManager.displayMessage({
                        text: Alfresco.component.Base.prototype.msg("documents.report.success")
                    });
                },
                scope: this
            },
            onFailure: {
                fn: function _onFailure(response) {
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
