<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments-list.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-attachments-list-actions.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-attachments.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-attachments-list.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-attachments-preview.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-category-attachments-list.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-attachments-dashlet-datagrid.js"></@script>
</@>

<@markup id="html">
	<!-- Parameters and libs -->
	<#assign el=args.htmlid/>
	<#if attachments??>
	<!-- Markup -->
	<div class="widget-bordered-panel attachments-panel">
	<div id="${el}-wide-view" class="document-metadata-header document-components-panel">
	    <h2 id="${el}-heading" class="dark">
	        ${msg("heading")}
	        <span class="alfresco-twister-actions">
	            <a id="${el}-action-expand" href="javascript:void(0);" class="expand attachments-expand" title="${msg("label.expand")}">&nbsp</a>
	        </span>
	    </h2>

	    <div id="${el}-formContainer" class="attachments-set right-block-content">
		    <#if attachments?? && attachments.items?? && (attachments.items?size > 0)>
	            <ul id="${el}-attachments-set" class="attachment-category">
	                <#list attachments.items as item>
	                    <li>
	                        <div class="category-title">
	                            <#if item.category??>
									${item.category.name!""}
								</#if>
	                        </div>
	                        <#if item.attachments??>
	                            <ul class="attachment" id="${el}-attach-list">
	                                <#list item.attachments as attachment>
	                                    <li  title="${attachment.name!""}" class="text-cropped">
		                                    <#if hasViewAttachmentPerm>
			                                    <a class="text-cropped"
												   onclick="LogicECM.module.Base.Util.showAttachmentsModalForm('${nodeRef}', '${attachment.nodeRef}', '${baseDocAssocName!""}')"
												   <#if item.category.nodeRef == "">target="_blank"</#if>>
			                                        ${attachment.name!""}
			                                    </a>
		                                    <#else>
		                                        ${attachment.name!""}
		                                    </#if>
	                                    </li>
	                                </#list>
	                            </ul>
	                        </#if>
	                    </li>
	                </#list>
	                <#if attachments.hasNext == "true">
	                <li>
	                    <div class="right-more-link-arrow attachments-expand"></div>
	                    <div class="right-more-link attachments-expand">${msg('label.attachments.more')}</div>
	                    <div class="clear"></div>
	                </li>
	                </#if>
	            </ul>
			<#else>
				<div class="block-empty-body">
				    <span class="block-empty faded">
					    ${msg("message.block.empty")}
				    </span>
				</div>
		    </#if>
	    </div>
	    <script type="text/javascript">//<![CDATA[

		if (typeof LogicECM == "undefined" || !LogicECM) {
		    LogicECM = {};
		}
    	if (typeof LogicECM.DocumentAttachmentsComponent == "undefined" || !LogicECM.DocumentAttachmentsComponent) {
		    LogicECM.DocumentAttachmentsComponent = {};
		}

	    (function () {
	        function init() {
	            LogicECM.DocumentAttachmentsComponent = new LogicECM.DocumentAttachments("${el}").setOptions({
					nodeRef: "${nodeRef}",
					baseDocAssocName: "${baseDocAssocName!""}",
					title: "${msg('heading')}",
					showAfterReady: ${(view?? && view == "attachments")?string}
				}).setMessages(${messages});
	        }

	        YAHOO.util.Event.onDOMReady(init);
	    })();
	    //]]>
	    </script>
	</div>

    <div id="${el}-short-view" class="document-components-panel short-view">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" class="expand attachments-expand" title="${msg("label.expand")}">&nbsp</a>
        </span>
        <div id="${el}-formContainer" class="right-block-content">
            <span class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('heading')}"></button>
               </span>
            </span>
		</div>
    </div>
</div>
<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.services = LogicECM.services || {};
    if (LogicECM.services.documentViewPreferences) {
        var shortView = LogicECM.services.documentViewPreferences.getShowRightPartShort();
        if (shortView) {
            Dom.addClass("${el}-wide-view", "hidden");
        } else {
			Dom.addClass("${el}-short-view", "hidden");
		}
    } else {
        Dom.addClass("${el}-short-view", "hidden");
	}
}) ();
//]]></script>
	</#if>
</@>
