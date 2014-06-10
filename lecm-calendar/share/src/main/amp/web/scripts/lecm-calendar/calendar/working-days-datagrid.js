if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.WorkingDays = LogicECM.module.WCalendar.Calendar.WorkingDays || {};

(function() {

	var attributeForShow = "cm:name";

	LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid = function(containerId) {
		return LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.WCalendar.Calendar.WorkingDays.DataGrid.prototype, {
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
									case "datetime":
										content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.longDateNoYear"));
										html += content;
										break;

									case "date":
										content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.longDateNoYear"));
										html += content;
										break;

									case "text":
										if (datalistColumn.name == "lecm-cal:day") {
											var dateInt = parseInt(data.value);
											var day = dateInt % 100;
											var month = (dateInt - dateInt % 100) / 100;
											var date = new Date(2013, month - 1, day);
											content = Alfresco.util.formatDate(date, scope.msg("date-format.longDateNoYear"));
											html += content;
										} else {
											content = Alfresco.util.encodeHTML(data.displayValue);
											html += Alfresco.util.activateLinks(content);
										}

										break;

									default:
										if (datalistColumn.type == "association") {
											html += '<a><img src="'
													+ Alfresco.constants.URL_RESCONTEXT
													+ 'components/images/filetypes/'
													+ Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16)
													+ '" width="16" alt="'
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '" title="'
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '" /> '
													+ Alfresco.util.encodeHTML(data.displayValue)
													+ '</a>';
										} else {
											html += Alfresco.util.activateLinks(Alfresco.util.encodeHTML(data.displayValue));
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
