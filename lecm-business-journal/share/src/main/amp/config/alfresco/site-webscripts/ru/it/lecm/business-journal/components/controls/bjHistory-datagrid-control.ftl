<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<div class="control bj-datagrid with-grid" id="bjHistory-${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
            (function () {
            	function init() {
                    LogicECM.module.Base.Util.loadScripts([
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
						'/scripts/lecm-business-journal/business-journal-datagrid.js'
                    ], createDatagrid);
				}
            	function createDatagrid() {
                    var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                        usePagination: true,
                        disableDynamicPagination: true,
                        pageSize: 10,
                        showExtendSearchBlock: true,
                        datagridMeta: {
                                itemType: "lecm-busjournal:bjRecord",
                                datagridFormId: "bjHistory",
                                createFormId: "",
                                sort:"lecm-busjournal:bjRecord-date|false",
                                nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
                                actionsConfig: {
                                    fullDelete: "${field.control.params.fullDelete!"false"}"
                                }
                            },
                        dataSource:"lecm/business-journal/ds/history",
                        <#if field.control.params.height??>
                            height: ${field.control.params.height},
                        </#if>
                        allowCreate: false,
                        showActionColumn: false,
                        showCheckboxColumn: false,
                        bubblingLabel: "${bubblingLabel!"bj-history-records"}",
                        attributeForShow:"lecm-busjournal:bjRecord-date"
                        <#if field.control.params.fixedHeader??>
                            ,fixedHeader: ${field.control.params.fixedHeader}
                        </#if>
                    }).setMessages(${messages});

                    datagrid.draw();
                }
            	YAHOO.util.Event.onDOMReady(init);
            })();
        //]]></script>
    </@grid.datagrid>
</div>
