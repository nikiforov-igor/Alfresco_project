<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<@grid.datagrid id=id showViewForm=true>
<script type="text/javascript">//<![CDATA[
function createDatagrid(attributeForShow) {
	var $html = Alfresco.util.encodeHTML,
			$links = Alfresco.util.activateLinks,
			$userProfile = Alfresco.util.userProfileLink;

	LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function () {
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
										html += $html(data.displayValue);
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
	};

	new LogicECM.module.Base.DataGrid('${id}').setOptions(
			{
				usePagination:true,
				showExtendSearchBlock:false
			}).setMessages(${messages});
}

function loadDictionary() {
	var me = this;
	var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${page.url.args.dic!''}");

	var callback = {
		success:function (oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			if (oResults != null) {
				createDatagrid(oResults.attributeForShow);
			}
		},
		failure:function (oResponse) {
			alert("Справочник не был загружен. Попробуйте обновить страницу.");
		},
		argument:{
		}
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function init() {
	loadDictionary();
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
</@grid.datagrid>
