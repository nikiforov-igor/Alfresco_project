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

	LogicECM.module.Approval.StagesCellFormatter = function (grid, elCell, oRecord, oColumn, oData) {
		function formatState(nodeRef, decisionData, hasComment) {
			if (!hasComment) {
				return null;
			}

			return "<a href=\"javascript:void(0);\" onclick=\"LogicECM.module.Base.Util.viewAttributes(" +
				"{itemId:\'" + nodeRef + "\'," +
				"title: \'label.view.approval.details\', " +
				"formId: \'viewApprovalResult\' })>" + decisionData.displayValue + "</a>";
		}
		var html = null;

		if (!oRecord) {
			oRecord = this.getRecord(elCell);
		}
		if (!oColumn) {
			oColumn = this.getColumn(elCell.parentNode.cellIndex);
		}

		if (oRecord && oColumn) {
			if (!oData) {
				oData = oRecord.getData("itemData")[oColumn.field];
			}

			if (oData) {
				var datalistColumn = grid.datagridColumns[oColumn.key];
				if (datalistColumn) {
					oData = YAHOO.lang.isArray(oData) ? oData : [oData];
					for (var i = 0; i < oData.length; i++) {
						switch (datalistColumn.name) {
							case 'lecmApproveAspects:approvalState':
								var state = oRecord.getData('itemData')['prop_lecmApproveAspects_approvalState'];
								var hasComment = !!(oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'] && oRecord.getData('itemData')['prop_lecmApproveAspects_hasComment'].value);
								var nodeRef = oRecord.getData("nodeRef");
								html = formatState(nodeRef, state, hasComment);
								break;
							case 'lecmWorkflowRoutes:stageExpression':
								if (oData[i].displayValue && oData[i].displayValue.length) {
									html = '<div class="centered"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png" width="16" alt="true" title="true" id="yui-gen538"></div>';
								} else {
									html = '';
								}
								break;
							default:
								break;
						}
					}
				}
			}
		}
		return html;
	}
})();
