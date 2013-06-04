var $siteURL = Alfresco.util.siteURL;

(function () {

    LogicECM.module.Contracts.DataGrid = function (containerId) {
        return LogicECM.module.Contracts.DataGrid.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.Contracts.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Contracts.DataGrid.prototype, {
        getCellFormatter: function DataGrid_getCellFormatter() {
            var scope = this;

            return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData) {
                var $html = Alfresco.util.encodeHTML,
                    $links = Alfresco.util.activateLinks,
                    $userProfile = Alfresco.util.userProfileLink;
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

                                    if (datalistColumn.name == "lecm-document:creator") {
                                        columnContent = "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'view-metadata?nodeRef=' + oRecord.getData("itemData")["prop_lecm-document_creator-ref"].value + "\'>" + columnContent + "</a>";
                                    }

                                    if (datalistColumn.name == "lecm-contract:totalAmount") {
                                        var currency = oRecord.getData("itemData")["assoc_lecm-contract_currency-assoc"];

                                        columnContent = '<div style="text-align: right; margin-right: 10px">' + columnContent + ' ' + (currency ? currency.displayValue : '') + '</div>';
                                    }

                                    if (scope.options.attributeForShow != null && datalistColumn.name == scope.options.attributeForShow) {
                                        html += "<a href=\'" + window.location.protocol + '//' + window.location.host + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + oRecord.getData("nodeRef") + "\'>" + columnContent + "</a>";
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

                if (oRecord && oRecord.getData("itemData")) {
                    if (oRecord.getData("itemData")["prop_lecm-dic_active"] && oRecord.getData("itemData")["prop_lecm-dic_active"].value == false) {
                        elCell.className += " archive-record";
                    }
                }
                elCell.innerHTML = html;
            };
        },
        onActionEdit: function DataGrid_onActionEdit(item) {
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + item.nodeRef;
        },
	    onActionDuplicate:function DataGrid_onActionDuplicate(p_items) {
		    var me = this;
		    var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items],
			    destinationNodeRef = new Alfresco.util.NodeRef(this.modules.dataGrid.datagridMeta.nodeRef),
			    nodeRefs = [];

		    for (var i = 0, ii = items.length; i < ii; i++) {
			    nodeRefs.push(items[i].nodeRef);
		    }

		    this.modules.actions.genericAction(
			    {
				    success:{
					    callback:
					    {
						    fn: function (data)
						    {
							    if (data != null && data.json != null && data.json.results != null && data.json.results.length == 1) {
								    window.location = $siteURL("document?nodeRef=" + data.json.results[0].nodeRef);
							    }
						    }
					    }
				    },
				    failure:{
					    message:this.msg("message.duplicate.failure")
				    },
				    webscript:{
					    method: Alfresco.util.Ajax.POST,
					    stem: Alfresco.constants.PROXY_URI + "lecm/contracts/",
					    name:"duplicate/node/" + destinationNodeRef.uri
				    },
				    config:{
					    requestContentType:Alfresco.util.Ajax.JSON,
					    dataObj:{
						    nodeRefs:nodeRefs
					    }
				    }
			    });
	    }
    }, true);
})();
