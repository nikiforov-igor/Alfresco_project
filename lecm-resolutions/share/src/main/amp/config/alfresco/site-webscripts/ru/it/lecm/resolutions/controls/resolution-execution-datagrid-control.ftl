<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<div class="control with-grid resolution-execution-datagrid-control" id="${fieldHtmlId}">
<@grid.datagrid containerId false>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-resolution/resolution-execution-datagrid-control.js'
            ], [
                'css/lecm-resolution/resolution-execution-datagrid-control.css'
            ], createControl);
        }

        YAHOO.util.Event.onDOMReady(init);
        function createControl() {
            new LogicECM.module.Resolutions.ExecutionTable("${fieldHtmlId}").setOptions({
                <#if field.control.params.expandFormId??>
                    expandFormId: "${field.control.params.expandFormId}",
                </#if>
                <#if field.control.params.datagridFormId??>
                    datagridFormId: "${field.control.params.datagridFormId}",
                </#if>
                <#if field.control.params.attributeForOpen??>
                    attributeForOpen: "${field.control.params.attributeForOpen}",
                </#if>
                datagridContainerId: "${containerId}",
                documentNodeRef: "${form.arguments.itemId}",
                jsonValue: '${field.value}'
            }).setMessages(${messages});
        }
    })();
    //]]></script>
</@grid.datagrid>
</div>
<div class="clear"></div>
