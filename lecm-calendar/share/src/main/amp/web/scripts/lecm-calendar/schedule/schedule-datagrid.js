if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};

(function() {

	var attributeForShow = "";

	LogicECM.module.WCalendar.Schedule.DataGrid = function(containerId) {
		return LogicECM.module.WCalendar.Schedule.DataGrid.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Schedule.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.WCalendar.Schedule.DataGrid.prototype, {
		getCellFormatter: function DataGrid_getCellFormatter() {
			var scope = this;

			return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
				var html = "";
				var content;

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
						var datalistColumn = scope.datagridColumns[oColumn.key];
						if (datalistColumn) {
							oData = YAHOO.lang.isArray(oData) ? oData : [oData];
							var plane = true;

							for (var i = 0, ii = oData.length, data; i < ii; i++) {
								data = oData[i];

								switch (datalistColumn.dataType.toLowerCase()) {
									case "cm:person":
										html += '<span class="person">'
												+ Alfresco.util.userProfileLink(data.metadata, data.displayValue)
												+ '</span>';
										break;

									case "datetime":
										content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.shortTime24FTL"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes("
													+"{itemId:\'"
													+ oRecord.getData("nodeRef")
													+ "\'})\">"
													+ content
													+ "</a>";
										}
										html += content;
										break;

									default:
										content = Alfresco.util.encodeHTML(data.displayValue);
										if (datalistColumn.name == attributeForShow) {
											html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes("
													+"{itemId:\'"
													+ oRecord.getData("nodeRef")
													+ "\'})\">"
													+ content
													+ "</a>";
										} else {
											html += Alfresco.util.activateLinks(content);
										}
										break;
								}
								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
				elCell.innerHTML = html;
			};
		}
	}, true);

})();
