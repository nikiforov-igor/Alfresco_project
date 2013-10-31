<#assign id=args.htmlid/>

<#if hasPermission>
    <script type="text/javascript">
        //<![CDATA[
        (function () {
            function init() {
                new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                    {
                        contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/document/document-tasks/content",
                        requestParams: {
                            nodeRef: "${args.nodeRef}",
                            state: "active",
                            myTasksLimit: 5,
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
        <div class="document-metadata-header document-components-panel" id="${id}-results">
            <h2 id="${id}-heading" class="dark">
                ${msg("heading")}
                <span class="alfresco-twister-actions">
                    <a id="${id}-action-expand" href="javascript:void(0);" class="expand" title="${msg("label.expand")}">&nbsp;</a>
                </span>
            </h2>

            <div id="${id}-formContainer">
                <div class="lecm-document-content-ajax-loading"></div>
            </div>

        <script type="text/javascript">
            //variable is used for expanding dashlet. refactor it?
            var documentTasksComponent = null;
        </script>
        <script type="text/javascript">//<![CDATA[
        (function () {
            Alfresco.util.createTwister("${id}-heading", "DocumentTasks", {
                panel: "${id}-formContainer"
            });

            function init() {
                documentTasksComponent = new LogicECM.DocumentTasks("${id}").setOptions(
                        {
                            nodeRef: "${args.nodeRef}",
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