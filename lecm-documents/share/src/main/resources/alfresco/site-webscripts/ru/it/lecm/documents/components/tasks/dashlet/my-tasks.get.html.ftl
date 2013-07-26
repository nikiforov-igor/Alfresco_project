<#assign id = args.htmlid?js_string>
<#if data??>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.tasks.dashlet");
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>

<script type="text/javascript">
    LogicECM.module.Errands.SETTINGS =
        <#if errandsDashletSettings?? >
        ${errandsDashletSettings}
        <#else>
        {}
        </#if>;

    var errands = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions(
        {
            itemType:"lecm-errands:document",
            destination: LogicECM.module.Errands.SETTINGS.nodeRef
        }).setMessages(${messages});
</script>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>
            <div style="float:left; margin-right: 4px;">${msg("label.title")}</div>
            <div class="total-tasks-count">

            <span class="lecm-dashlet-actions">
                <a id="${id}-action-add" href="javascript:void(0);" onclick="errands.onAddErrandClick()" class="add" title="${msg("dashlet.add.errand.tooltip")}">${msg("dashlet.add.errand")}</a>
            </span>
        </span>
        </div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentTasksComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
        <div style="padding: 10px;">
            <div>
                <div style="float:left;">${msg("dashlet.my.tasks.assigned.count", data.myTasksTotalCount)}</div>

                <#if data.myLatestTask??>
                    <div style="float:right;">
                        <a href="${url.context}/page/task-edit?taskId=${data.myLatestTask.id}" style="padding-right: 30px;">${msg("dashlet.label.last.task")}</a>${data.myLatestTask.startDate}
                    </div>
                </#if>
            </div>

            <div style="clear: both; padding-top: 10px;">
               <div style="float:left;">${msg("dashlet.my.errands.assigned.count", myErrandsData.myErrandsCount)}</div>

                <#if myErrandsData.latestErrandNoderef??>
                    <div style="float:right;">
                        <a href="${url.context}/page/document?nodeRef=${myErrandsData.latestErrandNoderef}" style="padding-right: 30px;">${msg("dashlet.label.last.errand")}</a>${myErrandsData.latestErrandStartDate}
                    </div>
                </#if>
            </div>

            <div style="clear: both; padding-top: 10px;">${msg("dashlet.my.errands.assigned.by.me.count", errandsIssuedByMeData.errandsIssuedByMeCount)}</div>

        </div>
    </div>
</div>
</#if>