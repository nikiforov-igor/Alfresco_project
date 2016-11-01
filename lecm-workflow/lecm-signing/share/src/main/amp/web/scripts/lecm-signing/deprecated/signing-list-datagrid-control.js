if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Signing = LogicECM.module.Signing || {};

(function() {

	LogicECM.module.Signing.SigningListDatagridControl = function(containerId, documentNodeRef) {

		this.documentNodeRef = documentNodeRef;

		YAHOO.util.Event.onContentReady(containerId, this.renewDatagrid, this, true);

		return LogicECM.module.Signing.SigningListDatagridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Signing.SigningListDatagridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Signing.SigningListDatagridControl.prototype, {
		signingItemType: null,
		signingListRef: null,
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
		renewDatagrid: function() {
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/signing/GetSigningListDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.signingItemType = response.json.signingItemType;
							this.signingListRef = response.json.signingListRef;
							if (this.signingListRef) {
								YAHOO.Bubbling.fire("activeGridChanged", {
									datagridMeta: {
										useFilterByOrg: false,
										itemType: this.signingItemType,
										useChildQuery: true,
										nodeRef: this.signingListRef,
										datagridFormId: this.options.datagridFormId,
										sort: 'lecm-workflow:assignee-order|true'
									},
									bubblingLabel: "SigningListDatagridControl"
								});
							}
						}
					}
				},
				failureMessage: "message.failure",
				execScripts: true,
				scope: this
			});
		}
	}, true);
})();
