<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<#if hasPermission && baseDocRef??>
    <#assign el=args.htmlid/>
<!-- Markup -->
<div id="${el}">
    <div id="${el}-wide-view" class="widget-panel-grey widget-bordered-panel">
        <h2 id="${el}-heading" class="dark">
        ${msg("label.title")}
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
        <div class="right-block-content">
            <span class="yui-button yui-push-button base-doc-action-icon">
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
                    ], [
                        'css/components/document-metadata-form-edit.css',
                        'css/components/document-final-actions.css'
                    ], createControl);
        }

        function createControl() {
            new LogicECM.DocumentActions("${el}").setOptions({
                isBaseDocActions: true
            });
            <#if isAdmin && documentDetailsJSON??>
                new Alfresco.DocumentActions("final-actions").setOptions(
                        {
                            nodeRef: "${baseDocRef?js_string}",
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
                nodeRef: "${baseDocRef}"
            });
            workflowForm.draw();
            Alfresco.util.createTwister("${el}-heading", "DocumentActions");
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</div>
</#if>
