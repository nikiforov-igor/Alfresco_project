<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>
<#assign realDelete = false/>
<#if fullDelete??>
	<#assign realDelete = fullDelete/>
</#if>
<div class="yui-t1" id="orgstructure-grid-with-tree">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content">
        <!-- include base datagrid markup-->
		<@grid.datagrid id>
			<script type="text/javascript">//<![CDATA[
			(function () {
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
													html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
													break;

												case "date":
													html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
													break;

												case "text":
													if (data.displayValue != "true" && data.displayValue != "false") {
														var content = Alfresco.util.encodeHTML(data.displayValue);
														if (datalistColumn.name == attributeForShow) {
															html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
														} else {
															html += content;
														}
														break;
													} else {
														if (data.displayValue && data.displayValue == "true") {
															html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
														}
														break;
													}

												case "boolean":
													if (data.displayValue) {
														html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
													}
													break;

												default:
													if (datalistColumn.type == "association") {
														html += $html(data.displayValue);
													} else {
														if (data.displayValue != "false" && data.displayValue != "true") {
															html += $html(data.displayValue);
														} else {
															if (data.displayValue == "true") {
																html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
															}
														}
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

					// Переопределяем метод onActionDelete. Добавляем проверки
					LogicECM.module.Base.DataGrid.prototype.onActionDelete = function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
						var me = this,
								items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
						var deletedUnit = items[0]; // для Оргструктуры одновременно удалить можно ТОЛЬКО ОДНО подразделение
						// Проверим есть ли у подразделения штатные расписания
						var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getUnitStaffPositions?nodeRef=" + deletedUnit.nodeRef;
						var callback = {
							success:function (oResponse) {
								var oResults = eval("(" + oResponse.responseText + ")");
								if (oResults && oResults.length > 0) { // нельзя удалять - есть назначенные должности
									Alfresco.util.PopupManager.displayMessage(
											{
												text:me.msg("message.delete.unit.failure.has.composition")
											});
								} else {
									// проверим нет ли дочерних АКТИВНЫХ подразделений
									var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getUnitChildren?nodeRef=" + deletedUnit.nodeRef + "&onlyActive=false";
									var callback = {
										success:function (oResponse) {
											var oResults = eval("(" + oResponse.responseText + ")");
											if (oResults && oResults.length > 0) { // нельзя удалять - есть дочерние подразделения
												Alfresco.util.PopupManager.displayMessage(
														{
															text:me.msg("message.delete.unit.failure.has.children")
														});
											} else { // удаляем! вызов метода из грида
												me.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
											}
										},
										failure:function (oResponse) {
											Alfresco.util.PopupManager.displayMessage(
													{
														text:me.msg("message.delete.unit.error")
													});
										},
										argument:{
										}
									};
									YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
								}
							},
							failure:function (oResponse) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text:me.msg("message.delete.unit.error")
										});
							}
						};
						YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
					};

					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination:true,
								showExtendSearchBlock:${showSearchBlock?string},
								actions:[
									{
										type:"action-link-${bubblingLabel!"orgstructure"}",
										id:"onActionEdit",
										permission:"edit",
										label:"${msg("actions.edit")}"
									},
									{
										type:"action-link-${bubblingLabel!"orgstructure"}",
										id:"onActionVersion",
										permission:"edit",
										label:"${msg("actions.version")}"
									},
									{
										type:"action-link-${bubblingLabel!"orgstructure"}",
										id:"onActionDelete",
										permission:"delete",
										label:"${msg("actions.delete-row")}"
									}
								],
								bubblingLabel: "${bubblingLabel!"orgstructure"}",
								showCheckboxColumn: false
							}).setMessages(${messages});
				}

				function init() {
					createDatagrid("lecm-orgstr:element-short-name");
				}

				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
	<div id="alf-filters" class="tree">
		<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:organization-unit"
						nodePattern="lecm-orgstr_element-full-name" itemPattern="lecm-orgstr_element-full-name"
						drawEditors=false fullDelete=realDelete>
		</@orgTree.tree>
	</div>
</div>
