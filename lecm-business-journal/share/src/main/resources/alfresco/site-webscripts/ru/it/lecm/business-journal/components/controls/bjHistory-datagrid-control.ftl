<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<div class="form-field with-grid" id="bjHistory-${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
            (function () {
                YAHOO.util.Event.onDOMReady(function (){
                    var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                        usePagination: true,
                        disableDynamicPagination: true,
                        pageSize: 10,
                        showExtendSearchBlock: true,
                        datagridMeta: {
                                itemType: "lecm-busjournal:bjRecord",
                                datagridFormId: "bjHistory",
                                createFormId: "",
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
                    }).setMessages(${messages});

                    datagrid.draw();
                });

            })();
        //]]></script>
    </@grid.datagrid>
</div>
