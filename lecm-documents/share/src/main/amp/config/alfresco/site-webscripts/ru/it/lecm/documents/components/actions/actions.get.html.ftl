<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<@script type="text/javascript" src="${url.context}/res/scripts/statemachine/form.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<!-- Document Details Actions -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-final-actions.css" />
<@script type="text/javascript" src="${url.context}/res/components/document-details/document-actions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-actions.js"></@script>

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
    <div id="${el}-short-view" class="widget-panel-grey widget-bordered-panel short-view">
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
    new LogicECM.DocumentActions("${el}");

    YAHOO.util.Event.onDOMReady(function () {
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
    });
    //]]>
    </script>
</div>
</#if>