<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<@grid.datagrid id>
<script type="text/javascript">//<![CDATA[
var $html = Alfresco.util.encodeHTML,
		$links = Alfresco.util.activateLinks,
		$userProfile = Alfresco.util.userProfileLink;

LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function () {
	var scope = this;

	/**
	 * Data Type custom formatter
	 *
	 * @method renderCellDataType
	 * @param elCell {object}
	 * @param oRecord {object}
	 * @param oColumn {object}
	 * @param oData {object|string}
	 */
	return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData)
	{
		var html = "";

		// Populate potentially missing parameters
		if (!oRecord)
		{
			oRecord = this.getRecord(elCell);
		}
		if (!oColumn)
		{
			oColumn = this.getColumn(elCell.parentNode.cellIndex);
		}

		if (oRecord && oColumn)
		{
			if (!oData)
			{
				oData = oRecord.getData("itemData")[oColumn.field];
			}

			if (oData)
			{
				var datalistColumn = scope.datagridColumns[oColumn.key];
				if (datalistColumn)
				{
					oData = YAHOO.lang.isArray(oData) ? oData : [oData];
					for (var i = 0, ii = oData.length, data; i < ii; i++)
					{
						data = oData[i];

						if (datalistColumn.name == "lecm-dic:plane")  {
							html += data.displayValue ? "${msg('logicecm.dictionary.plane')}" : "${msg('logicecm.dictionary.hierarchical')}";
						} else if (datalistColumn.name == "cm:name") {
							var plane = oRecord.getData("itemData")["prop_lecm-dic_plane"].value;
							html += "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "dictionary?dic=" + encodeURIComponent(data.displayValue) + "&plane=" + plane + "'>" + data.displayValue + "</a>";
						} else {
							switch (datalistColumn.dataType.toLowerCase())
							{
								case "cm:person":
									html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
									break;

								case "datetime":
									html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
									break;

								case "date":
									html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
									break;

								case "text":
									html += $links($html(data.displayValue));
									break;

								default:
									if (datalistColumn.type == "association")
									{
										html += '<a href="' + Alfresco.util.siteURL((data.metadata == "container" ? 'folder' : 'document') + '-details?nodeRef=' + data.value) + '">';
										html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16) + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
										html += ' ' + $html(data.displayValue) + '</a>'
									}
									else
									{
										html += $links($html(data.displayValue));
									}
									break;
							}
						}

						if (i < ii - 1)
						{
							html += "<br />";
						}
					}
				}
			}
		}

		elCell.innerHTML = html;
	};
};

LogicECM.module.Base.DataGrid.prototype.getDataTableColumnDefinitions = function () {
	// YUI DataTable column definitions
	var columnDefinitions =
			[
				{ key: "nodeRef", label: "<input type='checkbox' id='" + this.id + "-select-all-records'>",
					sortable: false, formatter: this.fnRenderCellSelected(), width: 16 }
			];

	var column;
	for (var i = 0, ii = this.datagridColumns.length; i < ii; i++)
	{
		column = this.datagridColumns[i];
		var label = column.label;
		if (column.name == "lecm-dic:plane") {
			label = "${msg('logicecm.dictionary.type')}";
		}
		columnDefinitions.push(
				{
					key: this.dataResponseFields[i],
					label: label,
					sortable: true,
					sortOptions:
					{
						field: column.formsName,
						sortFunction: this.getSortFunction()
					},
					formatter: this.getCellFormatter(column.dataType)
				});
	}

	// Add actions as last column
	columnDefinitions.push(
			{ key: "actions", label: this.msg("label.column.actions"), sortable: false, formatter: this.fnRenderCellActions(), width: 80 }
	);
	return columnDefinitions;
};

/* Экспорт в XML.
		*
* @method onActionExportXML
* @param items {Object} Object literal representing the Data Item to be actioned
*/
LogicECM.module.Base.DataGrid.prototype.onActionExportXML = function (item) {
	var fields = "";
	var dUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(item.itemData.prop_cm_name.value);

	Alfresco.util.Ajax.jsonGet(
			{
				url:dUrl,
				successCallback:{
					fn:function (response) {
						var oResults = eval("(" + response.serverResponse.responseText + ")");
						var itemType = oResults["itemType"];
						var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/dictionary/columns?itemType=" + encodeURIComponent(itemType);
						Alfresco.util.Ajax.jsonGet(
								{
									url:sUrl,
									successCallback:{
										fn:function (response) {
											var oResults = eval("(" + response.serverResponse.responseText + ")");
											for (var nodeIndex in oResults) {
												fields += "field=" + oResults[nodeIndex].fild + "&";
											}
											document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export"
													+ "?" + fields
													+ "nodeRef=" + item.nodeRef;
										},
										scope:this
									},
									failureCallback:{
										fn:function () {
											alert("Failed to load webscript export.")
										},
										scope:this
									}
								});
					},
					scope:this
				},
				failureCallback:{
					fn:function () {
						alert("Failed to load webscript export.")
					},
					scope:this
				}
			});
}
new LogicECM.module.Base.DataGrid('${id}').setOptions(
		{
			usePagination: true,
			showExtendSearchBlock:false
		}).setMessages(${messages});
//]]></script>
</@grid.datagrid>
