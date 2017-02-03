// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
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
		fileUpload: null,

		options: {
			baseDocAssocName: null
		},

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
									columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("lecm.date-format.defaultDateOnly"));
									break;

								case "date":
									columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("lecm.date-format.defaultDateOnly"));
									break;

								case "text":
									var hexColorPattern = /^#[0-9a-f]{6}$/i;
									if (hexColorPattern.test(data.displayValue)) {
										columnContent += $links(data.displayValue + '<div class="color-block" style="background-color: ' + data.displayValue + ';">&nbsp</div>');
									} else {
										columnContent += $links($html(data.displayValue));
									}
									break;

								case "boolean":
									if (data.value) {
										columnContent += '<div class="centered">';
										columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
										columnContent += '</div>';
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
                                                columnContent += '<div class="centered">';
                                                columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
                                                columnContent += '</div>';
                                            }
										}
									}
									break;
							}

							if (oColumn.field == "prop_cm_name") {
								var href = '<a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.showAttachmentsModalForm(\'{documentRef}\', \'{attachmentRef}\', \'{baseDocAssocName}\')">{columnContent}</a>';
								html += YAHOO.lang.substitute(href, {
									documentRef: grid.options.documentRef,
									attachmentRef: oRecord.getData("nodeRef"),
									baseDocAssocName: grid.options.baseDocAssocName,
									columnContent: columnContent
								});
								oColumn.maxAutoWidth = oColumn.getColEl().offsetWidth;
							} else if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
								html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oRecord.getData("nodeRef") + "\'})\">" + columnContent + "</a>";
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
					var src;
					if (oRecord.getData("isInnerAttachment") == "false") {
						src = "images/lecm-documents/link_attachment.png";
						html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + src +"' title='" + grid.msg("title.attachment.isLink") + "'/>";
					} else {
					var icon = Alfresco.util.getFileIcon(oRecord.getData("itemData")["prop_cm_name"].value, "cm:content", 16);
						src = "components/images/filetypes/" + icon;
						html += "<img src='" + Alfresco.constants.URL_RESCONTEXT + src +"'/>";
					}
				}
			}
			return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
		},

		onActionViewContent: function (p_item, owner, actionsConfig, fnCallback) {
			var viewUrl = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + p_item.nodeRef.replace(":/", "") + "/" + p_item.itemData.prop_cm_name.value;

			window.open(viewUrl);
		},

		onActionUploadNewVersion: function (p_item, owner, actionsConfig, fnCallback) {
			var displayName = p_item.itemData.prop_cm_name.value,
				nodeRef = p_item.nodeRef;
			var version = "1.0";
			if (p_item.itemData.prop_cm_versionLabel != null) {
				version = p_item.itemData.prop_cm_versionLabel.value;
			}

			if (!this.fileUpload)
			{
				this.fileUpload = Alfresco.getFileUploadInstance();
			}

			// Show uploader for multiple files
			var description = this.msg("label.filter-description", displayName),
				extensions = "*";

			if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
			{
				// Only add a filtering extension if filename contains a name and a suffix
				extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
			}

			var singleUpdateConfig =
			{
				updateNodeRef: nodeRef,
				updateFilename: displayName,
				updateVersion: version,
				overwrite: true,
				filter: [
					{
						description: description,
						extensions: extensions
					}],
				mode: this.fileUpload.MODE_SINGLE_UPDATE,
				onFileUploadComplete:
				{
					fn: this.onNewVersionUploadComplete,
					scope: this
				},
                suppressRefreshEvent: true
			};

			this.fileUpload.show(singleUpdateConfig);
		},

		onNewVersionUploadComplete: function (complete)
		{
			YAHOO.Bubbling.fire("datagridRefresh",
				{
					bubblingLabel:this.options.bubblingLabel
				});
		}
	}, true);
})();
