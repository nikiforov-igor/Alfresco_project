<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="subscriptions-type-grid">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
            <!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true>
            <script type="text/javascript">//<![CDATA[
            function createDatagrid() {
	            var $html = Alfresco.util.encodeHTML,
			            $links = Alfresco.util.activateLinks,
			            $userProfile = Alfresco.util.userProfileLink;

	            LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function DataGrid_getCellFormatter()
	            {
		            var scope = this;

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
										            columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
										            break;

									            case "date":
										            columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
										            break;

									            case "text":
										            var hexColorPattern = /^#[0-9a-f]{6}$/i;
										            if (hexColorPattern.test(data.displayValue)) {
											            columnContent += $links(data.displayValue + '<div style="background-color: ' + data.displayValue + '; display: inline; padding: 0px 10px; margin-left: 3px;">&nbsp</div>');
										            } else {
											            columnContent += $links($html(data.displayValue));
										            }
										            break;

									            case "boolean":
										            if (data.value) {
											            columnContent += '<div style="text-align: center;">'
											            columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
											            columnContent += '</div>'
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
													            columnContent += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
												            }
											            }
										            }
										            break;
								            }

								            if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
									            html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + columnContent + "</a>";
								            } else {
									            html += columnContent;
								            }

								            if (i < ii - 1) {
									            html += "<br />";
								            }
							            }
						            }
					            } else if (oColumn.field == "assoc_lecm-subscr_object-type-assoc" ||
							            oColumn.field == "assoc_lecm-subscr_event-category-assoc"){
						            html += "${msg('subscriptions.all')}";
					            }
				            }
			            } else {
				            html = htmlValue;
			            }

			            if (oRecord && oRecord.getData("itemData")){
				            if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
					            elCell.className += " archive-record";
				            }
			            }
			            elCell.innerHTML = html;
		            };
	            };

                new window.LogicECM.module.Base.DataGrid('${id}').setOptions(
                        {
                            usePagination: true,
                            actions: [
                                {
                                    type: "datagrid-action-link-${bubblingLabel!''}",
                                    id: "onActionEdit",
                                    permission: "edit",
                                    label: "${msg("actions.edit")}"
                                },
                                {
                                    type: "datagrid-action-link-${bubblingLabel!''}",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}"
                                }
                            ],
	                        bubblingLabel: "${bubblingLabel!''}",
                            showCheckboxColumn: true,
	                        attributeForShow:"cm:name",
	                        advSearchFormId: "${advSearchFormId!''}"
                        }).setMessages(${messages});
            }

            function init() {
                createDatagrid();
            }

            YAHOO.util.Event.onDOMReady(init);
            //]]></script>
		</@grid.datagrid>
        </div>
    </div>
</div>
