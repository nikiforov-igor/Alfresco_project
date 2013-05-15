(function() {
	var formToReportProperties = {
		"assoc_lecm-contract-reports_contracts-list-contract-subject": "contractSubject",
		"assoc_lecm-contract-reports_contracts-list-contract-type": "contractType",
		"assoc_lecm-contract-reports_contracts-list-contractor": "contractContractor",
		"prop_lecm-contract-reports_contracts-list-actual-only": "contractActualOnly",
		"prop_lecm-contract-reports_contracts-list-end": "end",
		"prop_lecm-contract-reports_contracts-list-start": "start",
		"prop_lecm-contract-reports_contracts-list-sum": "contractSum",
		"prop_lecm-contract-reports_approval-discipline-end": "periodEnd",
		"prop_lecm-contract-reports_approval-discipline-start": "periodStart",
		"assoc_lecm-contract-reports_contracts-list-contract-subject_added": "",
		"assoc_lecm-contract-reports_contracts-list-contract-subject_removed": "",
		"assoc_lecm-contract-reports_contracts-list-contract-type_added": "",
		"assoc_lecm-contract-reports_contracts-list-contract-type_removed": "",
		"assoc_lecm-contract-reports_contracts-list-contractor_added": "",
		"assoc_lecm-contract-reports_contracts-list-contractor_removed": "",
		"contracts-list-reports-link-printReportForm_assoc_lecm-contract-reports_contracts-list-contractor-cntrl-selectedItems": ""
	};

	LogicECM.module.Contracts.reports.reportLinkClicked = function(element, param) {
		var reportType = param.reportType;

		var doBeforeDialogShow = function ContractReports_doBeforeDialogShow(p_form, p_dialog) {
			// заголовки окон: contracts.report.contracts-list.title, contracts.report.approval-discipline.title
			Alfresco.util.populateHTML(
					[p_dialog.id + "-form-container_h", Alfresco.component.Base.prototype.msg("contracts.report." + reportType + ".title")]
					);
		};

		var url = "lecm/components/form" +
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
			itemId: "lecm-contract-reports:" + reportType + "-report",
			formId: "printReportForm",
			mode: "create",
			submitType: "json"
		});

		var printReportForm = new Alfresco.module.SimpleDialog(this.id + "-printReportForm");

		printReportForm.setOptions({
			actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/" + reportType,
			width: "50em",
			templateUrl: templateUrl,
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: doBeforeDialogShow,
				scope: this
			},
			ajaxSubmitMethod: "GET",
			doBeforeAjaxRequest: {
				fn: function ContractsReports_doBeforeAjaxRequest(form, formToReportProperties) {
					Object.prototype.renameProperty = function(oldName, newName) {
						// Check for the old property name to avoid a ReferenceError in strict mode.
						if (this.hasOwnProperty(oldName)) {
							if (newName.length > 0) {
								this[newName] = this[oldName];
							}
							delete this[oldName];
						}
						return this;
					};
					for (var property in formToReportProperties) {
						form.dataObj.renameProperty(property, formToReportProperties[property]);
					}
					form.method = "GET";
					return true;
				},
				obj: formToReportProperties

			},
			onSuccess: {
				fn: function ContractReports_onSuccess(response) {
					window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText, "report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("contracts.report.success")
					});
				},
				scope: this
			},
			onFailure: {
				fn: function ContractReports_onFailure(response) {
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("contracts.report.failure")
					});
				},
				scope: this
			}
		});
		printReportForm.show();

	};
})();
