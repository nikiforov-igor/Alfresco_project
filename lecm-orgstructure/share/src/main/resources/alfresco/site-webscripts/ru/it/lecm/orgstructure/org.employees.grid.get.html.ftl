<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
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

                                            case "boolean":
                                                if (data.displayValue) {
                                                    html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
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

				LogicECM.module.Base.DataGrid.prototype.deleteEvaluator = function DataGridActions_deleteEvaluator(rowData) {
					var itemData = rowData.itemData;
					return itemData["assoc_lecm-orgstr_employee-main-position"] == undefined || itemData["assoc_lecm-orgstr_employee-main-position"].value.length == 0;
				};

				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							showExtendSearchBlock:true,
							actions: [
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionEdit",
									permission:"edit",
									label:"${msg("actions.edit")}"
								},
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionVersion",
									permission:"edit",
									label:"${msg("actions.version")}"
								},
								{
									type:"action-link-${bubblingLabel!"employee"}",
									id:"onActionDelete",
									permission:"delete",
									label:"${msg("actions.delete-row")}",
									evaluator:"deleteEvaluator"
								}
							],
							bubblingLabel: "${bubblingLabel!"employee"}",
							showCheckboxColumn: false
						}).setMessages(${messages});
			}

			function init() {
				createDatagrid("lecm-orgstr:employee-last-name");
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
