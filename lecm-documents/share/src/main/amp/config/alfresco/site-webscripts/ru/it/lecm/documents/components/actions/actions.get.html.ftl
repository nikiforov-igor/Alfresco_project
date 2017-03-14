<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<#if hasPermission >
    <#assign el=args.htmlid/>

<!-- Markup -->
<div id="${el}">
    <div id="${el}-wide-view" class="widget-panel-grey widget-bordered-panel">
        <h2 id="${el}-heading" class="dark">
        ${msg("label.title")}
            <span id="${el}-show-right-part-short-container" class="alfresco-twister-actions show-right-part-short">
                   <a id="${el}-action-show-right-part-short" href="javascript:void(0);"
                      title="${msg("label.show-right-part-short")}">&nbsp</a>
               </span>
        </h2>
        <div id="${el}-formContainer" class="actions-list">
            <div id="final-actions">
                <div id="final-actions-body" class="document-final-actions document-details-panel">
                    <div class="doclist">
                        <div id="final-actions-actionSet" class="action-set"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="${el}-short-view" class="widget-panel-grey widget-bordered-panel short-view hidden">
        <span class="alfresco-twister-actions show-right-part-wide">
            <a href="javascript:void(0);"
               title="${msg("label.show-right-part-wide")}">&nbsp</a>
        </span>
        <div class="right-block-content">
            <span class="yui-button yui-push-button action-icon">
               <span class="first-child">
                  <button id="${el}-actions-button" type="button" title="${msg('label.title')}"></button>
               </span>
            </span>
        </div>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {

        function init() {
            LogicECM.module.Base.Util.loadResources([
                        'scripts/statemachine/form.js',
                        'components/document-details/document-actions.js',
                        'scripts/components/document-actions.js'
                    ],
                    [
                        'css/components/document-metadata-form-edit.css',
                        'css/components/document-final-actions.css',
                        'css/components/document-metadata-form-edit.css'
                    ], createControl);
        }

        function createControl() {
            new LogicECM.DocumentActions("${el}");
            <#if isAdmin && documentDetailsJSON??>
                new Alfresco.DocumentActions("final-actions").setOptions(
                        {
                            nodeRef: "${nodeRef?js_string}",
                        siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
                            containerId: "${container?js_string}",
                            rootNode: "${rootNode}",
                            replicationUrlMapping: {},
                            documentDetails: ${documentDetailsJSON},
                            repositoryBrowsing: ${(rootNode??)?string}
                        }).setMessages(
                ${messages}
                );
            </#if>
            var workflowForm = new LogicECM.module.StartWorkflow("${el}").setOptions({
                nodeRef: "${nodeRef}"
            });
            workflowForm.draw();
            Alfresco.util.createTwister("${el}-heading", "DocumentActions");
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
</#if>