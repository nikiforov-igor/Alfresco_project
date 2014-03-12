if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		var me = this;

		Alfresco.util.Ajax.request({
			method: "GET",
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/approval/GetApprovalListDataForDocument',
			dataObj: {
				documentNodeRef: documentNodeRef
			},
			successCallback: {
				fn: function(response) {
					if (response) {
						me.approvalListType = response.json.approvalListType;
						me.approvalContainer = response.json.approvalContainer;
						me.approvalItemType = response.json.approvalItemType;
						me.approvalContainerPath = response.json.approvalContainerPath;
						YAHOO.util.Event.onContentReady(containerId, function() {
							YAHOO.Bubbling.fire("activeGridChanged", {
								datagridMeta: {
									itemType: me.approvalListType,
									nodeRef: me.approvalContainer,
									datagridFormId: "approvalListDataGridControl",
									sort: 'lecm-workflow-result:workflow-result-list-complete-date|true',
									searchConfig: {
										filter: '+PATH:"' + me.approvalContainerPath + '//*"'
									}
								},
								bubblingLabel: "ApprovalListDataGridControl"
							});
						});
					}
				}
			},
			failureMessage: "message.failure",
			execScripts: true,
			scope: this
		});

		return LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalListDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalListDataGridControl.prototype, {
		onActionPrint: function(item) {
			LogicECM.module.Base.Util.printReport(item.nodeRef, 'approval-list');
		},
		onExpand: function(record) {
			var me = this, nodeRef = record.getData("nodeRef");
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/workflow/approval/approvalListItemsDatagrid",
				dataObj: {
					nodeRef: nodeRef,
					approvalItemType: this.approvalItemType
				},
				successCallback: {
					fn: function(response) {
						if (response.serverResponse) {
							me.addExpandedRow(record, response.serverResponse.responseText);
						}
					}
				},
				failureMessage: "message.failure",
				execScripts: true,
				scope: this
			});
		}
	}, true);



	LogicECM.module.Approval.ApprovalItemsDataGridControl = function(containerId, approvalNodeRef, approvalItemType) {
		var me = this;
		this.approvalListNodeRef = approvalNodeRef;
		this.approvalItemType = approvalItemType;

		YAHOO.util.Event.onContentReady(containerId, function() {
			YAHOO.Bubbling.fire("activeGridChanged", {
				datagridMeta: {
					itemType: me.approvalItemType,
					nodeRef: me.approvalListNodeRef,
					datagridFormId: "approvalItemsDataGridControl",
					sort: 'lecm-workflow:assignee-order|true'
				},
				bubblingLabel: containerId
			});
		});

		return LogicECM.module.Approval.ApprovalItemsDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalItemsDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalItemsDataGridControl.prototype, {
		getCustomCellFormatter: function(grid, elCell, oRecord, oColumn, oData) {
			var html = "", i, ii, columnContent, datalistColumn, data,
					resultItemCommentTemplate = '<a href="{proto}//{host}{pageContext}document-attachment?nodeRef={nodeRef}">' +
					'<img src="{resContext}/components/images/generic-file-16.png" width="16"  alt="{displayValue}" title="{displayValue}"/></a>',
					clickHandledStringTemplate = '<a href="javascript:void(0);" onclick="{clickHandler}(\'{nodeRef}\')">{content}</a>';

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
					datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

							switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
								case "lecmApprovalResult:approvalResultItemCommentAssoc":
									columnContent = YAHOO.lang.substitute(resultItemCommentTemplate, {
										proto: window.location.protocol,
										host: window.location.host,
										pageContext: Alfresco.constants.URL_PAGECONTEXT,
										nodeRef: data.value,
										resContext: Alfresco.constants.URL_RESCONTEXT,
										displayValue: data.displayValue
									});
									break;
								default:
									break;
							}
							if (columnContent) {
								if (grid.options.attributeForShow !== null && datalistColumn.name === grid.options.attributeForShow) {
									html += YAHOO.lang.substitute(clickHandledStringTemplate, {
										nodeRef: oRecord.getData("nodeRef"),
										content: columnContent,
										clickHandler: "viewAttributes"
									});
								} else {
									html += columnContent;
								}

								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
			}
			return html ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		}
	}, true);
})();
