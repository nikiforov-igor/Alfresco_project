<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<div class="control with-grid resolution-execution-datagrid-control" id="${controlId}">
<@grid.datagrid containerId false>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js'
            ], [
                'css/lecm-resolution/resolution-execution-datagrid-control.css'
            ], createDatagrid);
        }
        YAHOO.util.Event.onDOMReady(init);
        function createDatagrid() {
            var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                usePagination: false,
                showExtendSearchBlock: false,
                actions: [],
                expandable: true,
                <#if field.control.params.expandFormId??>
                    expandDataObj: {
                        formId: "${field.control.params.expandFormId}"
                    },
                </#if>
                datagridMeta: {
                    itemType: "lecm-errands:document",
                    useChildQuery: false,
                    useFilterByOrg: false,
                    datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
                    searchConfig: {
                        filter: "@lecm\\-errands\\:additional\\-document\\-assoc\\-ref:'${form.arguments.itemId}'"
                    }
                },
                bubblingLabel: "${containerId}",
                allowCreate: false,
                showActionColumn: false,
                showCheckboxColumn: false
            }).setMessages(${messages});
            datagrid.draw();
        }
    })();
    //]]></script>
</@grid.datagrid>
</div>
<div class="clear"></div>
