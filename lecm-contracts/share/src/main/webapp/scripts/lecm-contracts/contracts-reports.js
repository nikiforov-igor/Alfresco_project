(function() {
	YAHOO.util.Event.onDOMReady(function() {
		YAHOO.util.Event.addListener("contracts-list-reports-link", "click", LogicECM.module.Contracts.reports.reportLinkClicked, {"reportType": "contracts-list"});
		YAHOO.util.Event.addListener("approval-discipline-reports-link", "click", LogicECM.module.Contracts.reports.reportLinkClicked, {"reportType": "approval-discipline"});
	});

	LogicECM.module.Contracts.reports.reportLinkClicked = function(event, param) {
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
			actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contracts/reports",
			width: "50em",
			templateUrl: templateUrl,
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: doBeforeDialogShow,
				scope: this
			},
//			doBeforeFormSubmit: {
//				fn: function ContractsReports_doBeforeSubmit() {
//
//				},
//				scope: this
//			},
			onSuccess: {
				fn: function ContractReports_onSuccess(response) {
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
