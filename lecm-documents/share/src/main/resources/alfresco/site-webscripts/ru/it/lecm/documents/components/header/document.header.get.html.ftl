<#assign id = args.htmlid?html>
<#assign controlId = id + "-cntrl">

<#if item??>
    <div class="document-header">
        <div class="document-info">
            <h1 class="thin dark">
                ${documentName}
            </h1>
        </div>

        <div class="document-action">
            <#if subscribed>
                <script type="text/javascript">//<![CDATA[
                (function()
                {
                    var control = new LogicECM.module.Subscriptions.SubscribeControl("${id}").setMessages(${messages});
                    control.setOptions({
                        objectNodeRef: "${nodeRef}"
                    });
                })();
                //]]></script>

                <div class="subscribe">
                    <span id="${controlId}-subscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.subscribe")}"/>
                       </span>
                    </span>
                </div>
                <div class="unsubscribe">
                    <span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.unsubscribe")}"/>
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