if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}



LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationList = LogicECM.module.Delegation.DelegationList || {};

(function () {
	"use strict";
	var $html = Alfresco.util.encodeHTML;
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

		onActionEdit: function (item) {
			var baseUrl = window.location.protocol + "//" + window.location.host;
			//delegator - доверенное лицо, тот кто создает доверенность
			var template = "delegation-opts?delegator={delegator}&bubbling=" + this.options.bubblingLabel;
			var url = YAHOO.lang.substitute (baseUrl + Alfresco.constants.URL_PAGECONTEXT + template, {
				delegator: item.nodeRef // доверенное лицо, тот кто создает доверенность
			});
            var me = this;
            Alfresco.util.Ajax.request(
                {
                    url: url,
                    successCallback:{
                        fn:function(response){
                            var formEl = Dom.get(me.id + "-delegation-settings");
                            formEl.innerHTML = response.serverResponse.responseText;
                        }
                    },
                    failureMessage: this.msg("message.failure"),
                    execScripts:true
                });
		},

		/**
		 * Return data type-specific formatter
		 *
		 * @method getCellFormatter
		 * @return {function} Function to render read-only value
		 */
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
										case "checkboxtable":
											columnContent += "<div class='centered'><input type='checkbox'" + (data.displayValue == "true" ? " checked='checked'" : "") + " onClick='changeFieldState(this, \"" + data.value + "\")' /></div>"; //data.displayValue;
											break;
										case "lecm-orgstr:employee":
											columnContent += scope.getEmployeeView(data.value, data.displayValue);
											break;
										case "lecm-orgstr:employee-link":
											columnContent += scope.getEmployeeViewByLink(data.value, data.displayValue);
											break;

										case "cm:person":
											columnContent += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
											break;

										case "datetime":
											columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
											break;

										case "date":
											columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
											break;

										case "text":
											var hexColorPattern = /^#[0-9a-f]{6}$/i;
											if (data.displayValue.indexOf("!html ") == 0) {
												columnContent += data.displayValue.substring(6);
											} else if (hexColorPattern.test(data.displayValue)) {
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

									if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
										html += "<a href='javascript:void(0);' onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oRecord.getData("nodeRef") + "\', title: \'" + scope.options.viewFormTitleMsg + "\'})\">" + columnContent + "</a>";
									} else if (scope.options.attributeForOpen != null && datalistColumn.name == scope.options.attributeForOpen) {
										html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'\">" + columnContent + "</a>";
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
				elCell.innerHTML = html;
			};
		},
	}, true);
})();
