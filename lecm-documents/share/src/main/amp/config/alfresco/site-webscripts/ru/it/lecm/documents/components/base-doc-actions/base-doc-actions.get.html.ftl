<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<@script type="text/javascript" src="${url.context}/res/scripts/statemachine/form.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<!-- Document Details Actions -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-final-actions.css" />
<@script type="text/javascript" src="${url.context}/res/components/document-details/document-actions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
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
    <div id="${el}-short-view" class="widget-panel-grey widget-bordered-panel short-view">
        <div class="right-block-content">
            <span class="yui-button yui-push-button base-doc-action-icon">
               <span class="first-child">
                  <button id="${el}-actions-button" type="button" title="${msg('label.title')}"></button>
               </span>
            </span>
        </div>
    </div>
    <script type="text/javascript">//<![CDATA[

    YAHOO.util.Event.onDOMReady(function () {
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

        var shortView = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${el}-wide-view", "hidden");
        } else {
            Dom.addClass("${el}-short-view", "hidden");
        }

        var actionsContainer = Dom.get("${el}-formContainer");
        var shortContainer = Dom.get("${el}-short-view");
        var wideContainer = Dom.get("${el}-wide-view");
        var actionsShown = false;

        YAHOO.Bubbling.on("showRightPartShortChanged", function () {
            var shortView = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
            if (shortView) {
                Dom.addClass("${el}-wide-view", "hidden");
                Dom.removeClass("${el}-short-view", "hidden");
            } else {
                Dom.addClass("${el}-short-view", "hidden");
                Dom.removeClass("${el}-wide-view", "hidden");
            }
            wideContainer.appendChild(actionsContainer);
            actionsShown = false;
        });

        var actionsButton = Dom.get("${el}-actions-button");

        YAHOO.util.Event.addListener(actionsButton, 'click', function () {
            if (actionsShown) return;
            shortContainer.appendChild(actionsContainer);
            actionsShown = true;
            setTimeout(function () {
                YAHOO.util.Event.addListener('Share', 'click', onActionButtonClicked);
            }, 0);

        });

        function onActionButtonClicked(e) {
            if (actionsContainer != e.target && !actionsContainer.contains(e.target)){
                wideContainer.appendChild(actionsContainer);
                actionsShown = false;
                YAHOO.util.Event.removeListener('Share', 'click', onActionButtonClicked);
            }
        }
    });
    //]]>
    </script>
</div>
</#if>
