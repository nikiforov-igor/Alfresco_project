<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<script type="text/javascript">//<![CDATA[
(function () {

    function init() {
        LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-documents/graph-tree-control.js',
                    'scripts/lecm-eds-documents/execution-tree-control.js'
                ], [
                    'css/lecm-documents/graph-view-control.css',
                    'css/lecm-eds-documents/execution-tree-control.css'
                ],
                createControl);
    }

    function createControl() {
        new LogicECM.module.ExecutionTreeControl("${fieldHtmlId}").setOptions({
            documentNodeRef: "${form.arguments.itemId}",
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
        }).setMessages(${messages});
    }

    YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);
})();
//]]></script>

<div class="control execution-tree-control viewmode">
    <span id="${fieldHtmlId}"></span>
    <div class="connections-list">
        <div id="${fieldHtmlId}-execution-graph-tree" class="graph-tree">
            <div class ="yui-skin-sam">
                <div id="${fieldHtmlId}-expandable-table"> </div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>