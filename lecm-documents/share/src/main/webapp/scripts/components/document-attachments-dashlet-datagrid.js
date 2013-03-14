// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachments
 */
LogicECM.DocumentAttachments = LogicECM.DocumentAttachments || {};

var $html = Alfresco.util.encodeHTML,
	$links = Alfresco.util.activateLinks,
	$userProfile = Alfresco.util.userProfileLink;

(function () {

	LogicECM.DocumentAttachments.DataGrid = function (containerId) {
		return LogicECM.DocumentAttachments.DataGrid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.DocumentAttachments.DataGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.DocumentAttachments.DataGrid.prototype, {
		getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {
			var html = "";
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
					var datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (var i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

							var columnContent = "";
							switch (datalistColumn.dataType.toLowerCase()) {
								case "checkboxtable":
									columnContent += "<div style='text-align: center'><input type='checkbox' " + (data.displayValue == "true" ? "checked='checked'" : "") + " onClick='changeFieldState(this, \"" + data.value + "\")' /></div>"; //data.displayValue;
									break;
								case "lecm-orgstr:employee":
									columnContent += grid.getEmployeeView(data.value, data.displayValue);
									break;
								case "lecm-orgstr:employee-link":
									columnContent += grid.getEmployeeViewByLink(data.value, data.displayValue);
									break;

								case "cm:person":
									columnContent += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
									break;

								case "datetime":
									columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.default"));
									break;

								case "date":
									columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("date-format.defaultDateOnly"));
									break;

								case "text":
									var hexColorPattern = /^#[0-9a-f]{6}$/i;
									if (hexColorPattern.test(data.displayValue)) {
										columnContent += $links(data.displayValue + '<div style="background-color: ' + data.displayValue + '; display: inline; padding: 0px 10px; margin-left: 3px;">&nbsp</div>');
									} else {
										columnContent += $links($html(data.displayValue));
									}
									break;

								case "boolean":
									if (data.value) {
										columnContent += '<div style="text-align: center;">'
										columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
										columnContent += '</div>'
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

							if (oColumn.field == "prop_cm_name") {
								html += "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + oRecord.getData("nodeRef") + "'>" + columnContent + "</a>";
							} else if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
								html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + columnContent + "</a>";
							} else {
								html += columnContent;
							}

							if (i < ii - 1) {
								html += "<br />";
							}
						}
					}
				} else if (oColumn.field == "prop_cm_versionLabel") {
					html += "1.0";
				} else if (oColumn.field == "prop_cm_image") {
					var icon = Alfresco.util.getFileIcon(oRecord.getData("itemData")["prop_cm_name"].value, "cm:content", 16);
					html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + icon +"'/>";
				}
			}
			return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		}
	}, true);
})();
