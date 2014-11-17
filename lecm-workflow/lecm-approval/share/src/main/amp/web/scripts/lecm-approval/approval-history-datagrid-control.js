if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function () {

	LogicECM.module.Approval.ApprovalHistoryDataGridControl = function (containerId) {

		YAHOO.util.Event.delegate('Share', 'click', function () {
			LogicECM.module.Base.Util.printReport(this.options.documentNodeRef, this.options.reportId);
		}, '#printApprovalHistoryReport', this, true);

		LogicECM.module.Approval.ApprovalHistoryDataGridControl.superclass.constructor.call(this, containerId);

		this.name = 'LogicECM.module.Approval.ApprovalHistoryDataGridControl';

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalHistoryDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalHistoryDataGridControl.prototype, {
		onCollapse: function (record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		}
	}, true);

})();
