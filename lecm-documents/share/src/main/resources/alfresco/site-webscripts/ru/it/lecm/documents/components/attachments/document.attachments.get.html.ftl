<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if attachments??>
<!-- Markup -->
<div class="widget-bordered-panel">
<div class="document-metadata-header document-components-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.edit")}">&nbsp</a>
        </span>
    </h2>

    <div id="${el}-formContainer" class="attachments-set">
        <ul id="${el}-attachments-set" class="attachment-category">
            <#if attachments?? && attachments.items??>
                <#list attachments.items as item>
                    <li>
                        <div class="category-title">
                            <#if item.category??>
								${item.category.name!""}
							</#if>
                        </div>
                        <#if item.attachments??>
                            <ul class="attachment">
                                <#list item.attachments as attachment>
                                    <li>
                                        <a href="${url.context}/page/document-attachment?nodeRef=${attachment.nodeRef}">
                                        ${attachment.name!""}
                                        </a>
                                    </li>
                                </#list>
                            </ul>
                        </#if>
                    </li>
                </#list>
                <#if attachments.hasNext == "true">
                <li>
                    <div class="right-more-link-arrow" onclick="documentAttachmentsComponent.onExpand();"></div>
                    <div class="right-more-link" onclick="documentAttachmentsComponent.onExpand();">${msg('label.attachments.more')}</div>
                    <div style="clear:both;"></div>
                </li>
                </#if>
            </#if>
        </ul>
    </div>
    <script type="text/javascript">
        var documentAttachmentsComponent = null;
    </script>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            documentAttachmentsComponent =new LogicECM.DocumentAttachments("${el}").setOptions(
                    {
                        nodeRef: "${nodeRef}",
                        title: "${msg('heading')}",
                        showAfterReady: ${(view?? && view == "attachments")?string}
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
</div>
</#if>
