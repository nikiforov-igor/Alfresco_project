<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-all.js"/>

	<!-- Advanced Search -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
	<!-- Historic Properties Viewer -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
</@>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-all-datagrid">

<@grid.datagrid id>
<script type="text/javascript">//<![CDATA[
(function(){

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
											html += $html(data.displayValue);
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
		var columnDefinitions =	[];
	
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
	                        var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(itemType) + "&formId=export-fields";
							Alfresco.util.Ajax.jsonGet(
									{
										url:sUrl,
										successCallback:{
											fn:function (response) {
	                                            var datagridColumns = response.json.columns;
												for (var nodeIndex in datagridColumns) {
													fields += "field=" + datagridColumns[nodeIndex].name + "&";
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
	};
	
	new LogicECM.module.Base.DataGrid('${id}').setOptions(
			{
	            bubblingLabel:"${bubblingLabel}",
				usePagination: true,
				showExtendSearchBlock:false,
	            showCheckboxColumn:false,
	            actions: [
	                {
	                    type:"datagrid-action-link-${bubblingLabel}",
	                    id:"onActionEdit",
	                    permission:"edit",
	                    label:"${msg("actions.edit")}"
	                },
	                {
	                    type:"datagrid-action-link-${bubblingLabel}",
	                    id:"onActionExportXML",
	                    permission:"edit",
	                    label:"${msg("actions.export-xml")}"
	                }
	            ]
			}).setMessages(${messages});

})();
//]]></script>
</@grid.datagrid>
