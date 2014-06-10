<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<@script type="text/javascript" src="${url.context}/res/scripts/statemachine/form.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<!-- Document Details Actions -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-final-actions.css" />
<@script type="text/javascript" src="${url.context}/res/components/document-details/document-actions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />

<#if hasPermission >
    <#assign el=args.htmlid/>

<!-- Markup -->
<div id="${el}">
<div class="widget-panel-grey widget-bordered-panel">
    <h2 id="${el}-heading" class="dark">
        ${msg("label.title")}
    </h2>
    <div id="${el}-formContainer">
        <div id="final-actions">
            <div id="final-actions-body" class="document-final-actions document-details-panel">
                <div class="doclist">
                    <div id="final-actions-actionSet" class="action-set"></div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript">//<![CDATA[
    YAHOO.util.Event.onDOMReady(function (){
            <#if documentDetailsJSON??>
                new Alfresco.DocumentActions("final-actions").setOptions(
                    {
                        nodeRef: "${nodeRef?js_string}",
                        siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
                        containerId: "${container?js_string}",
                        rootNode: "${rootNode}",
                        replicationUrlMapping: ${replicationUrlMappingJSON!"{}"},
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
    });
    //]]>
    </script>
</div>
</div>
</#if>