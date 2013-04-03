<#assign id = args.htmlid?html>
<#assign controlId = id + "-cntrl">
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<#if item??>
    <div class="document-header">
        <div class="document-info">
            <h1 class="thin dark">
                <a id="document-title" class="title" href="${siteURL("document?nodeRef=" + nodeRef)}">${documentName}</a><span id="document-title-breadcrumb"></span>
            </h1>
        </div>

        <div class="document-action">
            <#if subscribed>
                <script type="text/javascript">//<![CDATA[
                (function()
                {
                    var control = new window.LogicECM.module.Subscriptions.SubscribeControl("${id}").setMessages(${messages});
                    control.setOptions({
                        objectNodeRef: "${nodeRef}"
                    });
                })();
                //]]></script>

                <div class="subscribe">
                    <span id="${controlId}-subscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.subscribe")}"></button>
                       </span>
                    </span>
                </div>
                <div class="unsubscribe">
                    <span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.unsubscribe")}"></button>
                       </span>
                    </span>
                </div>
            </#if>
        </div>

        <div class="clear"></div>
    </div>
<#else>
    <div class="document-header">
        <div class="status-banner">
            ${msg("banner.not-found")}
        </div>
    </div>
</#if>