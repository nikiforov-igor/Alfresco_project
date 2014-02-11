<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<#assign allowCreate = false/>
<#assign showActions = false/>
<#assign usePagination = false/>

<div class="form-field with-grid" id="${controlId}">
    <label for="${controlId}" style="white-space: nowrap; overflow: visible;">${field.label?html}:</label>
<@grid.datagrid containerId true "app-list-item-employee-view">
    <script type="text/javascript">//<![CDATA[
    (function () {
	    LogicECM.module.Base.DataGrid.prototype.getCustomCellFormatter = function (grid, elCell, oRecord, oColumn, oData) {
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
				    var datalistColumn = grid.datagridColumns[oColumn.key];
				    if (datalistColumn) {
					    oData = YAHOO.lang.isArray(oData) ? oData : [oData];
					    for (var i = 0, ii = oData.length, data; i < ii; i++) {
						    data = oData[i];

						    var columnContent = "";
						    switch (datalistColumn.name.toLowerCase()) { //  меняем отрисовку для конкретных колонок
							    case "lecm-al:approval-item-comment-assoc":
								    columnContent = "<a href=\'" +window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT+'document-attachment?nodeRef='+ data.value +"\'\"><img src=\"${url.context}/res/components/images/generic-file-16.png\" width=\"16\"  alt=\"" + data.displayValue + "\" title=\"" + data.displayValue + "\"/></a>";
								    break;
							    default:
								    break;
						    }
						    if (columnContent != "") {
							    if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
								    html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + columnContent + "</a>";
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
		    }
		    return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
	    };

        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: ${usePagination?string},
                showExtendSearchBlock: false,
                actions: [],
                datagridMeta: {
                    itemType: "lecm-al:approval-item",
                    datagridFormId: "datagrid",
                    nodeRef: "${form.arguments.itemId}"
                },
                bubblingLabel: "${containerId}",
                allowCreate: ${allowCreate?string},
                showActionColumn: ${showActions?string},
                showCheckboxColumn: false
            }).setMessages(${messages});

            datagrid.draw();
        });

    })();
    //]]></script>
</@grid.datagrid>
</div>
