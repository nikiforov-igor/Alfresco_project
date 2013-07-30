<#assign id = args.htmlid?html>
<#assign controlId = id + "-cntrl">
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<#if item??>
    <div class="document-header">
        <div class="document-info">
            <h1 class="thin dark">
                <a id="document-title" class="title" href="${siteURL("document?nodeRef=" + nodeRef)}" style="display: none;">${documentName}</a>
                <span id="document-title-span" class="title">${documentName}</span> <#-- без ссылки -->
                <span id="document-title-breadcrumb"></span>
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

                <div class="subscribe-header">
                    <span id="${controlId}-subscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.subscribe")}"></button>
                       </span>
                    </span>
                </div>
                <div class="unsubscribe-header">
                    <span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button" style="display: none">
                       <span class="first-child">
                          <button type="button" title="${msg("button.unsubscribe")}"></button>
                       </span>
                    </span>
                </div>
            </#if>
            <#if isAdmin>
                <script type="text/javascript">//<![CDATA[
                (function()
                {
                    var transfer = new LogicECM.module.Transfer.TransferRight("${id}").setMessages(${messages});
                    transfer.setOptions({
                        documentRef: "${nodeRef}",
                        bublingLabel: "transferRight"
                    });
                })();
                //]]></script>

                <div class="transfer-right">
                    <span id="${controlId}-transfer-right-button" class="yui-button yui-push-button">
                       <span class="first-child">
                          <button type="button" title="${msg("button.transfer.right")}"></button>
                       </span>
                    </span>
                </div>
            </#if>

	        <script type="text/javascript">//<![CDATA[
	        (function()
	        {
		        var favorite = new LogicECM.module.DocumentFavourite("${id}").setMessages(${messages});
		        favorite.setOptions({
			        documentRef: "${nodeRef}",
			        isFavourite: ${(item.isFavourite!false)?string}
		        });
	        })();
	        //]]></script>
	        <div class="favorite" id="${controlId}-favourite">
		        <span id="${controlId}-favourite-button" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button"></button>
                   </span>
                </span>
	        </div>

            <script type="text/javascript">//<![CDATA[
            (function()
            {
                var copyControl = new LogicECM.module.DocumentCopy("${id}").setMessages(${messages});
                copyControl.setOptions({
                    documentRef: "${nodeRef}"
                });
            })();
            //]]></script>
            <div class="copy" id="${controlId}-copy">
		        <span id="${controlId}-copy-button" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button"></button>
                   </span>
                </span>
            </div>
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