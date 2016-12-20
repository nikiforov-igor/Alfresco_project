<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-header.css" />

<#-- TODO такого файла не существует, нудо убедиться, чего не хватает
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-transfer-right.js"></@script>
-->

<#if isAdmin>
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-admin-tools-right.js"></@script>
</#if>

<@script type="text/javascript" src="${url.context}/res/scripts/components/document-favorite.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-copy.js"></@script>

<#assign id = args.htmlid?html>
<#assign controlId = id + "-cntrl">
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<#if item??>
    <div class="document-header">
        <div class="document-info">
            <h1 class="thin dark">
                <a id="document-title" class="title" href="${siteURL(viewUrl + "?nodeRef=" + nodeRef)}" title="${documentName}">${documentName}</a>
                <span id="document-title-span" class="title" title="${documentName}">${documentName}</span> <#-- без ссылки -->
                <span id="document-title-breadcrumb"></span>
            </h1>
        </div>

        <div class="document-action">
            <#if subscribed>
                <script type="text/javascript">//<![CDATA[
                (function()
                {

                    function init() {
                        LogicECM.module.Base.Util.loadScripts([
                            'scripts/lecm-subscriptions/controls/lecm-subscribe.js'
                        ], createControl);
                    }

                    function createControl () {
                        var control = new LogicECM.module.Subscriptions.SubscribeControl("${id}").setMessages(${messages});
                        control.setOptions({
                            objectNodeRef: "${nodeRef}"
                        });
                    }

                    YAHOO.util.Event.onDOMReady(init);
                })();
                //]]></script>

                <div class="subscribe-header">
                    <span id="${controlId}-subscribe-button" class="yui-button yui-push-button hidden1">
                       <span class="first-child">
                          <button type="button" title="${msg("button.subscribe")}"></button>
                       </span>
                    </span>
                </div>
                <div class="unsubscribe-header">
                    <span id="${controlId}-unsubscribe-button" class="yui-button yui-push-button hidden1">
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

                <div class="admin-tools-right">
                    <span id="${controlId}-admin-tools-right-button" class="yui-button yui-push-button">
                       <span class="first-child">
                          <button type="button" title="${msg("button.admin.tools.right")}"></button>
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
            <#if isDocumentStarter>
                <script type="text/javascript">//<![CDATA[
                (function()
                {
                    var copyControl = new LogicECM.module.DocumentCopy("${id}").setMessages(${messages});
                    copyControl.setOptions({
                        documentRef: "${nodeRef}",
                        canCopy: ${canCopy?string}
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
            </#if>
        </div>

        <div class="clear"></div>
    </div>
<#else>
    <div class="document-header">
        <div class="status-banner">
            ${msg(accessMsg)}
        </div>
    </div>
</#if>