<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = true/>

<script type="text/javascript">//<![CDATA[

	var attributeForShow = "cm:name";
	var $html = Alfresco.util.encodeHTML;
	var $links = Alfresco.util.activateLinks;
	var $userProfile = Alfresco.util.userProfileLink;

	/*
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
	};
	*/

	LogicECM.module.Base.DataGrid.prototype.onActionEdit = function (item) {
		var scope = this;
		var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
		window.location.href = url + "procuracy";
	};

	var datagrid = new LogicECM.module.Base.DataGrid('${id}');
	datagrid.setOptions({
		usePagination:true,
		showExtendSearchBlock:true
	});
	datagrid.setMessages(${messages});

	YAHOO.util.Event.onContentReady ('${id}', function () {
	    createDialog(); // call method from lecm-datagrid.ftl#macro viewFor"
		YAHOO.Bubbling.fire ("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.itemType,
				nodeRef: LogicECM.module.Delegation.DELEGATION_OPTIONS_CONTAINER.nodeRef,
				searchConfig: {
					filter: "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
				}
			}
		});
	});

	function onShowOnlyConfiguredChanged () {
		var cbShowOnlyConfigured = YAHOO.util.Dom.get("cbShowOnlyConfigured");
		var obj = {
			datagridMeta: datagrid.datagridMeta
		};
		if (cbShowOnlyConfigured.checked) {
			obj.datagridMeta.searchConfig.filter = "ISNOTNULL:\"sys:node-uuid\" AND NOT (@lecm\\-d8n:delegation\\-opts\\-status:\"NOT_SET\")"
		} else {
			obj.datagridMeta.searchConfig.filter = ""
		}
		YAHOO.Bubbling.fire ("activeGridChanged", obj);
	};
//]]>
</script>

<div align="right" style="padding-top: 0.5em;">
	<input type="checkbox" class="formsCheckBox" id="cbShowOnlyConfigured" onChange="onShowOnlyConfiguredChanged()" checked>
	<label class="checkbox" for="cbShowOnlyConfigured">Отображать только настроенные</label>
</div>
<@grid.datagrid id showViewForm/>
