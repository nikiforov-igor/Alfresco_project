<!-- Parameters and libs -->
<#assign el=args.htmlid/>

<!-- Markup -->
<div class="document-metadata-header document-details-panel">
    <h2 id="${el}-heading" class="thin dark">
        ${msg("heading")}
        <span class="alfresco-twister-actions">
            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
        </span>
    </h2>

    <div id="${el}-formContainer">
        <div id="${el}-form" style="display:none"></div>
        <table id="${el}-connection-set" style="width:  100%">
            <#if connections?? && connections.items??>
                <#list connections.items as item>
                    <tr>
                        <td>
                            <a href="${url.context}/page/document?nodeRef=${item.connectedDocument.nodeRef}">
                                <#if item.connectedDocument.presentString?? && (item.connectedDocument.presentString?length > 0)>
                                    ${item.connectedDocument.presentString}
                                <#else>
                                    ${item.connectedDocument.name}
                                </#if>
                            </a>
                        </td>
                        <td style="text-align: right">
                            ${item.type.name}
                        </td>
                    </tr>
                </#list>
                <#if connections.hasNext == "true">
                    <tr>
                        <td></td>
                        <td style="text-align: right">
                            <a id="${el}-link" href="javascript:void(0);" onclick="" class="edit" title="${msg("label.connections.more")}">${msg("label.connections.more")}</a>
                        </td>
                    </tr>
                </#if>
            </#if>
        </table>
    </div>

    <script type="text/javascript">//<![CDATA[
        Alfresco.util.createTwister("${el}-heading", "DocumentConnections");

        new LogicECM.DocumentConnections("${el}").setOptions(
                {
                    nodeRef: "${nodeRef}",
                    title:"${msg('heading')}"
            }).setMessages(${messages});
    //]]></script>
</div>