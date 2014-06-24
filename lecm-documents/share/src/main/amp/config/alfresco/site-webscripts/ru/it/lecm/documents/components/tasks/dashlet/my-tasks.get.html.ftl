<#assign id = args.htmlid?js_string>

<#if hasPermission>
    <div class="dashlet document bordered">
        <div class="title dashlet-title">
            <span>
                <div class="dashlet-title-text">${msg("label.title")}</div>
                <div class="total-tasks-count">

                <#if hasStatemachine && isErrandsStarter>
                    <span class="lecm-dashlet-actions">
                        <a id="${id}-action-add" href="javascript:void(0);" onclick="errandsComponent.createChildErrand()" class="add"
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
	    <script type="text/javascript">
		    (function () {
			    function init() {
				    new Alfresco.widget.DashletResizer("${id}", "document.tasks.dashlet");

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
    </div>
</#if>