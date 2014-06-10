<@script type="text/javascript" src="${url.context}/res/scripts/components/document-workflows.js"></@script>

<#assign id=args.htmlid/>

<#if hasPermission>
    <script type="text/javascript">
        //<![CDATA[
        (function () {
            function init() {
                new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                    {
                        contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/document/document-workflows/content",
                        requestParams: {
                            nodeRef: "${nodeRef}",
                            containerHtmlId: "${id}"
                        },
                        containerId: "${id}-results"
                    }).setMessages(${messages});
            }

            YAHOO.util.Event.onContentReady("${id}-results", init);
        })();
        //]]>
    </script>

    <div class="widget-bordered-panel">
        <div class="document-metadata-header document-components-panel">
            <h2 id="${id}-heading" class="dark">
                ${msg("heading")}
                <span class="alfresco-twister-actions">
                    <div>
                        <a id="${id}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp</a>
                    </div>
                </span>
            </h2>

            <div id="${id}-formContainer">
                <div class="right-workflows-container" id="${id}-results"></div>

                <span id="${id}-right-more-link-container" class="hidden1">
                    <div class="right-more-link-arrow" onclick="documentWorkflowsComponent.onExpand();"></div>
                    <div class="right-more-link" onclick="documentWorkflowsComponent.onExpand();">${msg('right.label.more')}</div>
                    <div class="clear"></div>
                </span>
            </div>

            <script type="text/javascript">
                var documentWorkflowsComponent = null;
            </script>
            <script type="text/javascript">//<![CDATA[
            (function () {
                Alfresco.util.createTwister("${id}-heading", "DocumentWorkflows");

                function init() {
                    documentWorkflowsComponent = new LogicECM.DocumentWorkflows("${id}").setOptions(
                            {
                                nodeRef: "${nodeRef}",
                                title: "${msg('heading')}"
                            }).setMessages(${messages});
                }

                YAHOO.util.Event.onDOMReady(init);
            })();
            //]]>
            </script>

        </div>
    </div>
</#if>