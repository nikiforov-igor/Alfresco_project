if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		this.documentNodeRef = documentNodeRef;

		//YAHOO.util.Event.onContentReady(containerId, this.renewDatagrid, this, true);
		YAHOO.Bubbling.on("activeTabChange", this.renewDatagrid, this);

		return LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalListDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalListDataGridControl.prototype, {
		approvalListType: null,
		approvalContainer: null,
		approvalItemType: null,
		approvalContainerPath: null,
		doubleClickLock: false,
		renewDatagrid: function(event, args) {
			function isDescendant(parent, child) {
				var node = child.parentNode;
				while (node !== null) {
					if (node === parent) {
						return true;
					}
					node = node.parentNode;
				}
				return false;
			}

			var currentTabDiv;
			if (event && event === "activeTabChange" && args) {
				currentTabDiv = args[1].newValue.get('contentEl');
				if (!isDescendant(currentTabDiv, document.getElementById(this.id))) {
					return;
				}
			}
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/approval/GetApprovalListDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.approvalListType = response.json.approvalListType;
							this.approvalContainer = response.json.approvalContainer;
							this.approvalItemType = response.json.approvalItemType;
							this.approvalContainerPath = response.json.approvalContainerPath;
							YAHOO.Bubbling.fire("activeGridChanged", {
								datagridMeta: {
									useFilterByOrg: false,
									itemType: this.approvalListType,
									nodeRef: this.approvalContainer,
									datagridFormId: this.options.approvalListDatagridId, //"approvalListDataGridControl",
									sort: 'lecm-workflow-result:workflow-result-list-complete-date|true',
									searchConfig: {
										filter: '+PATH:"' + this.approvalContainerPath + '//*"'
									}
								},
								bubblingLabel: "ApprovalListDataGridControl"
							});
						}
					}
				},
				failureMessage: "message.failure",
				execScripts: true,
				scope: this
			});
		},
		onActionPrint: function(item) {
			LogicECM.module.Base.Util.printReport(item.nodeRef, 'approval-list');
		},
		getCustomCellFormatter: function(grid, elCell, oRecord, oColumn, oData) {
			var html = "", i, ii, columnContent, datalistColumn, data,
				clickHandledStringTemplate = '<a href="javascript:void(0);" onclick="{clickHandler}({itemId:\'{nodeRef}\'})">{content}</a>';

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

							if (datalistColumn.dataType === 'date' || datalistColumn.dataType === 'datetime') {
								columnContent = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "dd.mm.yyyy HH:MM");
							}

							if (columnContent) {
								if (grid.options.attributeForShow !== null && datalistColumn.name === grid.options.attributeForShow) {
									html += YAHOO.lang.substitute(clickHandledStringTemplate, {
										nodeRef: oRecord.getData("nodeRef"),
										content: columnContent,
										clickHandler: "LogicECM.module.Base.Util.viewAttributes"
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
		},
		onExpand: function(record) {
			if (this.doubleClickLock)
				return;
			this.doubleClickLock = true;

			var me = this, nodeRef = record.getData("nodeRef");
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/workflow/approval/deprecated/approvalListItemsDatagrid",
				dataObj: {
					nodeRef: nodeRef,
					datagridFormId: me.options.approvalItemsDatagridId,
					approvalItemType: this.approvalItemType
				},
				successCallback: {
					fn: function(response) {
						if (response.serverResponse) {
							me.addExpandedRow(record, response.serverResponse.responseText);
						}
						me.doubleClickLock = false;
					}
				},
				failureMessage: "message.failure",
				execScripts: true,
				scope: this
			});
		},
		getExpandedFormId: function(record) {
			var nodeRef = record.getData("nodeRef");
			return nodeRef.replace(/:|\//g, '_') + "-dtgrd";
		}
	}, true);

	LogicECM.module.Approval.ApprovalItemsDataGridControl = function(containerId, approvalNodeRef, approvalItemType) {
		this.approvalListNodeRef = approvalNodeRef;
		this.approvalItemType = approvalItemType;

		YAHOO.util.Event.onContentReady(containerId, function() {
			YAHOO.Bubbling.fire("activeGridChanged", {
				datagridMeta: {
					useFilterByOrg: false,
					itemType: this.approvalItemType,
					nodeRef: this.approvalListNodeRef,
					useChildQuery: true,
					datagridFormId: this.options.datagridFormId,
					sort: 'lecm-workflow:assignee-order|true'
				},
				bubblingLabel: containerId
			});
		}, this, true);

		return LogicECM.module.Approval.ApprovalItemsDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalItemsDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalItemsDataGridControl.prototype, {
		getCustomCellFormatter: function(grid, elCell, oRecord, oColumn, oData) {
			var html = "", i, ii, columnContent, datalistColumn, data,
				resultItemCommentTemplate = '<a href="{proto}//{host}{pageContext}document-attachment?nodeRef={nodeRef}">' +
				'<img src="{resContext}/components/images/generic-file-16.png" width="16"  alt="{displayValue}" title="{displayValue}"/></a>',
				clickHandledStringTemplate = '<a href="javascript:void(0);" onclick="{clickHandler}({itemId:\'{nodeRef}\'})">{content}</a>';

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

							if (datalistColumn.dataType === 'date' || datalistColumn.dataType === 'datetime') {
								columnContent = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "dd.mm.yyyy HH:MM");
							} else {
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
							}
							if (columnContent) {
								if (grid.options.attributeForShow !== null && datalistColumn.name === grid.options.attributeForShow) {
									html += YAHOO.lang.substitute(clickHandledStringTemplate, {
										nodeRef: oRecord.getData("nodeRef"),
										content: columnContent,
										clickHandler: "LogicECM.module.Base.Util.viewAttributes"
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
