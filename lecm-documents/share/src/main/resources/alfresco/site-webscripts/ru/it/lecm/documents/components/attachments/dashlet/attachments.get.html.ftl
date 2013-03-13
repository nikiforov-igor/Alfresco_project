<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<div class="dashlet document bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	    <span class="lecm-dashlet-actions">
	        <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentAttachmentsComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
	    </span>
	    <span class="lecm-dashlet-actions-right">
	        <select id="${id}-attachment-categories" class="attachment-categories-select">
				<#if categories??>
					<#list categories as category>
						<option value="${category.nodeRef}">${category.name}</option>
					</#list>
				</#if>
		    </select>
	    </span>
	</div>
    <div class="body scrollableList" id="${id}_results">
	    <@grid.datagrid containerId false>
		    <script type="text/javascript">//<![CDATA[
		    (function () {
			    var $html = Alfresco.util.encodeHTML,
					    $links = Alfresco.util.activateLinks,
					    $userProfile = Alfresco.util.userProfileLink;

			    var datagrid = null;
			    var select = null;

			    function confirmDeleteAttachments() {
				    YAHOO.Bubbling.fire("fileDeleted", {});
			    }

			    LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function ()
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
											    case "checkboxtable":
												    columnContent += "<div style='text-align: center'><input type='checkbox' " + (data.displayValue == "true" ? "checked='checked'" : "") + " onClick='changeFieldState(this, \"" + data.value + "\")' /></div>"; //data.displayValue;
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

										    if (oColumn.field == "prop_cm_name") {
											    html += "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + oRecord.getData("nodeRef") + "'>" + columnContent + "</a>";
										    } else if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
											    html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + columnContent + "</a>";
										    } else {
											    html += columnContent;
										    }

										    if (i < ii - 1) {
											    html += "<br />";
										    }
									    }
								    }
							    } else if (oColumn.field == "prop_cm_versionLabel") {
								    html += "1.0";
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
			    YAHOO.util.Event.onDOMReady(function (){
				    select = Dom.get("${id}-attachment-categories");
					var selectValue = "";
					if (select != null && select.value != null) {
						selectValue = select.value;
					}
				    YAHOO.util.Event.on("${id}-attachment-categories", "change", onCategoriesSelectChange, this, true);

				    datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
					    usePagination: false,
					    showExtendSearchBlock: false,
					    actions: [
						    {
							    type: "datagrid-action-link-${containerId}",
							    id: "onActionDelete",
							    permission: "delete",
							    label: "${msg("actions.delete-row")}",
							    confirmFunction: confirmDeleteAttachments
						    }
					    ],
					    datagridMeta: {
						    itemType: "cm:content",
						    datagridFormId: "attachments-dashlet-table",
						    createFormId: "",
					        nodeRef: selectValue,
						    actionsConfig: {
							    fullDelete: true
						    }
					    },
					    dataSource:"lecm/search",
					    bubblingLabel: "${containerId}",

					    allowCreate: false,
					    showActionColumn: true,
					    showCheckboxColumn: false
				    }).setMessages(${messages});

				    datagrid.draw();
			    });

			    function onCategoriesSelectChange() {
				    var selectValue = "";
				    if (select != null && select.value != null) {
					    selectValue = select.value;
				    }
				    var meta = datagrid.datagridMeta;
				    meta.nodeRef = selectValue;

				    YAHOO.Bubbling.fire("activeGridChanged",
						    {
							    datagridMeta: meta,
							    bubblingLabel:datagrid.bubblingLabel
						    });
			    }

		    })();
		    //]]></script>
	    </@grid.datagrid>
    </div>
</div>