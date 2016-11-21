if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

LogicECM.module.OrgStructure.BusinessRoles = LogicECM.module.OrgStructure.BusinessRoles || {};

(function () {

	var attributeForShow = "cm:name";

	LogicECM.module.OrgStructure.BusinessRoles.DataGrid = function (containerId) {
		return LogicECM.module.OrgStructure.BusinessRoles.DataGrid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.OrgStructure.BusinessRoles.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.OrgStructure.BusinessRoles.DataGrid.prototype, {

		getCellFormatter: function DataGrid_getCellFormatter () {
			var scope = this;

			return function DataGrid_renderCellDataType (elCell, oRecord, oColumn, oData) {
				var html = "";
				var content;

				if (!oRecord) {
					oRecord = this.getRecord (elCell);
				}
				if (!oColumn) {
					oColumn = this.getColumn (elCell.parentNode.cellIndex);
				}

				if (oRecord && oColumn) {
					if (!oData) {
						oData = oRecord.getData ("itemData")[oColumn.field];
					}
					if (oData) {
						var datalistColumn = scope.datagridColumns[oColumn.key];
						if (datalistColumn) {
							oData = YAHOO.lang.isArray (oData) ? oData : [oData];
							var plane = true;

							for (var i = 0, ii = oData.length, data; i < ii; i++) {
								data = oData[i];

								switch (datalistColumn.dataType.toLowerCase ()) {
									case "cm:person":
										html += '<span class="person">'
											 + Alfresco.util.userProfileLink (data.metadata, data.displayValue)
											 + '</span>';
										break;

									case "datetime":
										content = Alfresco.util.formatDate (Alfresco.util.fromISO8601 (data.value), scope.msg ("lecm.date-format.defaultDateOnly"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes("
												+ "{itemId:\'"
												+ oRecord.getData("nodeRef")
												+ "\'})\">"
												+ content
												+ "</a>";
										}
										html += content;
										break;

									case "date":
										content = Alfresco.util.formatDate (Alfresco.util.fromISO8601 (data.value), scope.msg ("lecm.date-format.defaultDateOnly"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes("
												+ "{itemId:\'"
												+ oRecord.getData("nodeRef")
												+ "\'})\">"
												+ content
												+ "</a>";
										}
										html += content;
										break;

									case "text":
										content = Alfresco.util.encodeHTML (data.displayValue);
										if (datalistColumn.name == attributeForShow) {
											html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes("
												+ "{itemId:\'"
												+ oRecord.getData("nodeRef")
												+ "\'})\">"
												+ content
												+ "</a>";
										} else {
											html += Alfresco.util.activateLinks (content);
										}
										break;

									default:
										if (datalistColumn.type == "association") {
											html += '<a><img src="'
												 + Alfresco.constants.URL_RESCONTEXT
												 + 'components/images/filetypes/'
												 + Alfresco.util.getFileIcon (data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16)
												 + '" width="16" alt="'
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '" title="'
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '" /> '
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '</a>'
										} else {
											html += Alfresco.util.activateLinks (Alfresco.util.encodeHTML (data.displayValue));
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
