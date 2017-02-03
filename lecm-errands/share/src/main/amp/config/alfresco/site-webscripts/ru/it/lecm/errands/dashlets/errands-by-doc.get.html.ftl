<#assign id = args.htmlid?js_string>

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>
            <div class="dashlet-title-text">${msg("label.title")}</div>
            <div class="total-tasks-count">

            <#if hasPermission && hasStatemachine && isErrandsStarter && isRegistered>
                <span class="lecm-dashlet-actions">
                    <a id="${id}-action-add" href="javascript:void(0);" onclick="errandsComponent.createChildErrand()" class="add"
                       title="${msg("dashlet.add.errand.tooltip")}">${msg("dashlet.add.errand")}</a>
                </span>
            </#if>

            </div>
        </span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentErrandsComponent.onExpand()"
               class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
        </span>
    </div>

    <div class="body scrollableList dashlet-body" id="${id}-results"></div>
    <script type="text/javascript">
        (function () {
            function init() {
                new Alfresco.widget.DashletResizer("${id}", "document.errands.dashlet");

                new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                        {
                            contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/dashlets/errands-by-doc/content",
                            requestParams: {
                                nodeRef: "${nodeRef}",
                                hasStatemachine: ${hasStatemachine?string},
                                hasPermission: ${hasPermission?string},
                                isErrandsStarter: ${isErrandsStarter?string},
                                htmlid: "${id}-content"
                            },
                            containerId: "${id}-results"
                        }).setMessages(${messages});
            }

            YAHOO.util.Event.onContentReady("${id}-results", init);
        })();
        //]]>
    </script>
</div>
