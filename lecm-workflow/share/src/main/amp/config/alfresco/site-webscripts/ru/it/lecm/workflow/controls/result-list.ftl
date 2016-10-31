<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<#assign allowCreate = false/>
<#assign showActions = false/>
<#assign usePagination = false/>

<#if field.control.params.itemType??>
	<#assign itemType = field.control.params.itemType>
<#else>
	<#assign itemType = "lecm-workflow-result:workflow-result-item">
</#if>

<div class="form-field with-grid" id="${controlId}">
	<label for="${controlId}">${field.label?html}:</label>
<@grid.datagrid containerId true "app-list-item-employee-view">
	<script type="text/javascript">
	(function () {

        function draw() {
            LogicECM.module.Base.DataGrid.prototype.getCustomCellFormatter = function(grid, elCell, oRecord, oColumn, oData) {
                var html = "", i, ii, columnContent, datalistColumn, data,
                        resultItemCommentTemplate = '<a href="{proto}//{host}{pageContext}document-attachment?nodeRef={nodeRef}">' +
                                '<img src="{resContext}/components/images/generic-file-16.png" width="16"  alt="{displayValue}" title="{displayValue}"/></a>',
                        attributeForShowTemplate = '<a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:\'{nodeRef}\'})">{content}</a>';

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
                        datalistColumn = grid.datagridColumns[oColumn.key];
                        if (datalistColumn) {
                            oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                            for (i = 0, ii = oData.length, data; i < ii; i++) {
                                data = oData[i];
                                var columnContent = "";
                                switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
                                    case "lecmApprovalResult:approvalResultItemCommentAssoc":
                                        columnContent = YAHOO.lang.substitute(resultItemCommentTemplate, {
                                            proto: window.location.protocol,
                                            host: window.location.host,
                                            pageContext: Alfresco.constants.URL_PAGECONTEXT,
                                            nodeRef: data.value,
                                            resContext: Alfresco.constants.URL_RESCONTEXT,
                                            displayValue: data.displayValue
                                        });
                                    default:
                                        break;
                                }

                                switch (datalistColumn.dataType.toLowerCase()) {
                                    case "datetime":
                                        columnContent += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), grid.msg("lecm.date-format.defaultDateOnly"));
                                        break;

                                    default:
                                        break;
                                }

                                if (columnContent) {
                                    if (grid.options.attributeForShow != null && datalistColumn.name == grid.options.attributeForShow) {
                                        html += YAHOO.lang.substitute(attributeForShowTemplate, {
                                            nodeRef: oRecord.getData("nodeRef"),
                                            content: columnContent
                                        });
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
                return html ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
            };

            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: ${usePagination?string},
                showExtendSearchBlock: false,
                actions: [],
                datagridMeta: {
                    useFilterByOrg: false,
                    itemType: "${itemType}",
                    useChildQuery: true,
                    datagridFormId: "datagrid",
                    nodeRef: "${form.arguments.itemId}",
                    sort: "lecm-workflow:assignee-order|true"
                },
                bubblingLabel: "${containerId}",
                allowCreate: ${allowCreate?string},
                showActionColumn: ${showActions?string},
                showCheckboxColumn: false
            }).setMessages(${messages});

            datagrid.draw();

        }

        function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js'
            ], draw);
        }

        YAHOO.util.Event.onDOMReady(init);

	})();
</script>
	</@grid.datagrid>
</div>
