if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationList = LogicECM.module.Delegation.DelegationList || {};

(function () {

	var attributeForShow = "cm:name";
	var $html = Alfresco.util.encodeHTML;
	var $links = Alfresco.util.activateLinks;
	var $userProfile = Alfresco.util.userProfileLink;

	LogicECM.module.Delegation.DelegationList.Grid = function (containerId) {
		return LogicECM.module.Delegation.DelegationList.Grid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.DelegationList.Grid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.DelegationList.Grid.prototype, {

		getCellFormatter: function () {
			var scope = this;

			return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
				var html = "";

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
										html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
										break;

									case "datetime":
										var content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										}
										html += content;
										break;

									case "date":
										var content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										}
										html += content;
										break;

									case "text":
										var content = $html(data.displayValue);
										if (datalistColumn.name == attributeForShow) {
											html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										} else {
											html += $links(content);
										}
										break;

									default:
										if (datalistColumn.type == "association") {
											html += '<a href="' + Alfresco.util.siteURL((data.metadata == "container" ? 'folder' : 'document') + '-details?nodeRef=' + data.value) + '">';
											html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16) + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
											html += ' ' + $html(data.displayValue) + '</a>'
										}
										else {
											html += $links($html(data.displayValue));
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
		},

		onActionEdit: function (item) {
			var baseUrl = window.location.protocol + "//" + window.location.host;
			//delegator - доверенное лицо, тот кто создает доверенность
			var template = "delegation-opts?delegator={delegator}";
			var url = YAHOO.lang.substitute (baseUrl + Alfresco.constants.URL_PAGECONTEXT + template, {
				delegator: item.nodeRef // доверенное лицо, тот кто создает доверенность
			});
			window.location.href = url;
		}

	}, true);

})();
