<!-- Parameters and libs -->
<#assign el=args.htmlid/>
<#if connections??>
<!-- Markup -->
<div class="widget-bordered-panel">
<div class="document-metadata-header document-components-panel">
    <h2 id="${el}-heading" class="thin dark">
    ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
        </span>
    </h2>

    <div id="${el}-formContainer">
        <div id="${el}-form" style="display:none"></div>
        <ul id="${el}-connection-set" style="width:  100%">
            <#if connections?? && connections.items??>
                <hr>
                <#list connections.items as item>
                    <li>
                    ${item.type.name}
                        <br/>
                        <a href="${url.context}/page/document?nodeRef=${item.connectedDocument.nodeRef}">
                            <#if item.connectedDocument.presentString?? && (item.connectedDocument.presentString?length > 0)>
                                    ${item.connectedDocument.presentString}
                                <#else>
                            ${item.connectedDocument.name}
                            </#if>
                        </a>
                        <hr>
                    </li>
                </#list>
                <#if connections.hasNext == "true">
                    <li>
                        <div class="right-more-link-arrow" onclick="documentConnectionsComponent.onExpand();"></div>
                        <div class="right-more-link" onclick="documentConnectionsComponent.onExpand();">${msg('label.connections.more')}</div>
                        <div style="clear:both;"></div>
                    </li>
                </#if>
            </#if>
        </ul>
    </div>

    <script type="text/javascript">//<![CDATA[
    Alfresco.util.createTwister("${el}-heading", "DocumentConnections");

    var documentConnectionsComponent = new window.LogicECM.DocumentConnections("${el}").setOptions(
            {
                nodeRef: "${nodeRef}",
                title:"${msg('heading')}"
            }).setMessages(${messages});
    //]]></script>
</div>
</div>
</#if>
