<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    var errands;
    (function () {
        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.tasks.dashlet");

            errands = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions(
                {
                    itemType: "lecm-errands:document",
                    destination: LogicECM.module.Documents.ERRANDS_SETTINGS.nodeRef,
                    parentDoc:"${nodeRef}"
                }).setMessages(${messages});

            var tasks = new LogicECM.module.Dashlet.Tasks("${id}-controlCenterDashlet").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        containerId: "${id}_results"
                    }).setMessages(${messages});
            tasks.onReady();
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document bordered" id="${id}-controlCenterDashlet">
    <div class="title dashlet-title">
        <span>
            <div style="float:left; margin-right: 4px;">${msg("label.title")}</div>
            <div class="total-tasks-count">

            <#if hasStatemachine>
                <span class="lecm-dashlet-actions">
                    <a id="${id}-action-add" href="javascript:void(0);" onclick="errands.onAddErrandClick()" class="add"
                       title="${msg("dashlet.add.errand.tooltip")}">${msg("dashlet.add.errand")}</a>
                </span>
            </#if>

            </div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentTasksComponent.onExpand()"
               class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
</div>

<div class="body scrollableList dashlet-body" id="${id}_results"></div>

</div>