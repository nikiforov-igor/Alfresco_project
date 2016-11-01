if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function() {

	var attributeForShow = "";

	LogicECM.module.WCalendar.Absence.DataGrid = function(containerId) {
		this.timezone = Alfresco.util.toISO8601(new Date()).substr(23);
		return LogicECM.module.WCalendar.Absence.DataGrid.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Absence.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.WCalendar.Absence.DataGrid.prototype, {
		getCellFormatter: function DataGrid_getCellFormatter()
		{
			var scope = this;

			/**
			 * Data Type formatter
			 *
			 * @method renderCellDataType
			 * @param elCell {object}
			 * @param oRecord {object}
			 * @param oColumn {object}
			 * @param oData {object|string}
			 */
			return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
				var $html = Alfresco.util.encodeHTML;
				var $links = Alfresco.util.activateLinks;
				var $userProfile = Alfresco.util.userProfileLink;
				var html = "";
				var htmlValue = scope.getCustomCellFormatter.call(this, scope, elCell, oRecord, oColumn, oData);
				if (htmlValue == null) { // используем стандартный форматтер
					// Populate potentially missing parameters
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
								for (var i = 0, ii = oData.length, data; i < ii; i++) {
									data = oData[i];

									var columnContent = "";
									switch (datalistColumn.dataType.toLowerCase()) {
										case "lecm-orgstr:employee":
											columnContent += scope.getEmployeeView(data.value, data.displayValue);
											break;

										case "lecm-orgstr:employee-link":
											columnContent += scope.getEmployeeViewByLink(data.value, data.displayValue);
											break;

										case "cm:person":
											columnContent += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
											break;

										case "datetime": case "date":
											//Заменяем таймзону на клиентскую
											data.value = data.value.substr(0, 23) + scope.timezone;
											columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
											break;

										case "text":
											columnContent += $links($html(data.displayValue));
											break;

										case "boolean":
											if (data.value) {
												columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
											}
											break;

										default:
											if (datalistColumn.type == "association") {
												columnContent += $html(data.displayValue);
											} else {
												if (data.displayValue != "false" && data.displayValue != "true") {
													columnContent += $html(data.displayValue);
												} else {
													if (data.displayValue == "true") {
														columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
													}
												}
											}
											break;
									}

									if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
										html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oRecord.getData("nodeRef") + "\'})\">" + columnContent + "</a>";
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
				} else {
					html = htmlValue;
				}

				if (oRecord && oRecord.getData("itemData")) {
					if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
						elCell.className += " archive-record";
					}
				}
				elCell.innerHTML = html;
			};
		}
	}, true);

})();
