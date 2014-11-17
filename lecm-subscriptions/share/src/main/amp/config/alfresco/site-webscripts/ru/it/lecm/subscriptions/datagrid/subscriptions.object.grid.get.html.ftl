<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="subscriptions-to-object-grid">
	<div id="yui-main-2">
		<div class="yui-b datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
            (function()  {
                function createDatagrid() {
                    var sUrl = Alfresco.constants.PROXY_URI + "lecm/subscriptions/roots";
                    var callback = {
                        success:function (oResponse) {
                            var oResults = eval("(" + oResponse.responseText + ")");
                            if (oResults != null) {
                                for (var nodeIndex in oResults) {
                                    if (oResults[nodeIndex].page == "subscriptions-to-object") {
                                        var root = {
                                            nodeRef:oResults[nodeIndex].nodeRef,
                                            itemType:oResults[nodeIndex].itemType,
                                            page:oResults[nodeIndex].page,
                                            fullDelete:oResults[nodeIndex].fullDelete
                                        };
                                        var namespace = "lecm-subscr";
                                        var cType = root.itemType;
                                        root.itemType = namespace + ":" + cType;
                                        root.bubblingLabel = cType;

                                        LogicECM.module.Base.DataGrid.prototype.getCustomCellFormatter = function(grid, elCell, oRecord, oColumn, oData) {
                                                var html = "";
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
                                                        var datalistColumn = grid.datagridColumns[oColumn.key];
                                                        if (datalistColumn) {
                                                            oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                                                            for (var i = 0, ii = oData.length, data; i < ii; i++) {
                                                                data = oData[i];

                                                                var columnContent = "";
                                                                switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
                                                                    case "lecm-subscr:subscription-object-assoc":
                                                                        columnContent += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'" + data.value + "\')\">" + data.displayValue + "</a>";
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }

                                                                if (columnContent) {
                                                                    html += columnContent;

                                                                    if (i < ii - 1) {
                                                                        html += "<br />";
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                return html.length > 0 ? html : null;  // возвращаем NULL чтобы вызывался основной метод отрисовки
                                            };

                                        var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
                                                {
                                                    usePagination:true,
                                                    showExtendSearchBlock:true,
                                                    actions: [
                                                        {
                                                            type:"datagrid-action-link-${bubblingLabel!''}",
                                                            id:"onActionEdit",
                                                            permission:"edit",
                                                            label:"${msg("actions.edit")}"
                                                        },
                                                        {
                                                            type:"datagrid-action-link-${bubblingLabel!''}",
                                                            id:"onActionDelete",
                                                            permission:"delete",
                                                            label:"${msg("actions.delete-row")}"
                                                        }
                                                    ],
                                                    bubblingLabel: "${bubblingLabel!''}",
                                                    showCheckboxColumn: true,
                                                    attributeForShow:"cm:name",
                                                    advSearchFormId: "${advSearchFormId!''}",
                                                    datagridMeta:{
                                                        itemType: root.itemType,
                                                        nodeRef: root.nodeRef,
                                                        actionsConfig:{
                                                            fullDelete:true
                                                        }
                                                    }
                                                }).setMessages(${messages});
                                        datagrid.draw();
                                    }

                                }
                            }
                        },
                        failure:function (oResponse) {
                            YAHOO.log("Failed to process XHR transaction.", "info", "example");
                        },
                        argument:{
                            context:this
                        },
                        timeout:10000
                    };
                    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                }

                function init() {
                    LogicECM.module.Base.Util.loadResources([
                        'modules/simple-dialog.js',
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
                        'components/form/date-range.js',
                        'components/form/number-range.js',
                        'scripts/lecm-base/components/versions.js',
                        'components/form/form.js'
                    ], [
                        'modules/document-details/historic-properties-viewer.css',
                        'components/search/search.css',
                        'yui/treeview/assets/skins/sam/treeview.css'
                    ], createDatagrid);
                }

                YAHOO.util.Event.onDOMReady(init);
            })();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
