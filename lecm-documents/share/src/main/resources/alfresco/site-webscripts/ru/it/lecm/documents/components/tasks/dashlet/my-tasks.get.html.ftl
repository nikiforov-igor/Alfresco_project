<#assign id = args.htmlid?js_string>

<#if hasPermission>
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

                new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                    {
                        contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/dashlets/document-my-tasks/content",
                        requestParams: {
                            nodeRef: "${nodeRef}"
                        },
                        containerId: "${id}-results"
                    }).setMessages(${messages});
            }

            YAHOO.util.Event.onContentReady("${id}-results", init);
        })();
        //]]>
    </script>

    <div class="dashlet document bordered">
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

        <div class="body scrollableList dashlet-body" id="${id}-results"></div>
    </div>
</#if>