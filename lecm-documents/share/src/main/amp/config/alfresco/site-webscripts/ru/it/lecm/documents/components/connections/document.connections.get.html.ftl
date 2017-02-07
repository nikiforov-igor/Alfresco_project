<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-connections-list.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-connections-list.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/components/document-connections.js"></@script>
</@>


<@markup id="html">
	<!-- Parameters and libs -->
    <#assign aDateTime = .now>
    <#assign el=args.htmlid + aDateTime?iso_utc/>
	<#if connections??>
	<!-- Markup -->
	<script type="text/javascript">
		//TODO:Переписать
		var documentConnectionsComponent = null;
	</script>
        <div id="${el}" class="widget-bordered-panel">
            <div class="document-metadata-header document-components-panel">
                <h2 id="${el}-heading" class="dark">
                ${msg("heading")}
                    <span class="alfresco-twister-actions">
	            <a id="${el}-action-expand" href="javascript:void(0);" onclick="" class="expand" title="${msg("label.expand")}">&nbsp</a>
	        </span>
                </h2>

                <div id="${el}-formContainer">
                    <div id="${el}-form" class="hidden1"></div>
                    <#if connections?? && connections.items?? && (connections.items?size > 0)>
                        <ul id="${el}-connection-set" class="document-connection-set document-right-set">
                            <#list connections.items as item>
                                <li class="text-broken <#if item.connectedDocument?? && !item.connectedDocument.hasAccess>dont-have-access</#if>">
                                ${item.type.name}
                                    <br/>
                                    <#if item.connectedDocument.extPresentString?? && (item.connectedDocument.extPresentString?length > 0)>
                                        <#assign docname="${item.connectedDocument.extPresentString}"/>
                                    <#else>
                                        <#assign docname="${item.connectedDocument.name}"/>
                                    </#if>
                                    <a href="${url.context}/page/${item.connectedDocument.viewUrl}?nodeRef=${item.connectedDocument.nodeRef}" class="text-cropped" title="${docname}">
                                    ${docname}
                                    </a>
                                </li>
                            </#list>
                        </ul>
                    <#else>
                        <div class="block-empty-body right-block-content">
                            <span class="block-empty faded">
                            ${msg("message.block.empty")}
                            </span>
                        </div>
                    </#if>
                    <#if connections.hasNext == "true">
                        <li>
                            <div class="right-more-link-arrow" onclick="documentConnectionsComponent.onExpand();"></div>
                            <div class="right-more-link" onclick="documentConnectionsComponent.onExpand();">${msg('label.connections.more')}</div>
                            <div class="clear"></div>
                        </li>
                    </#if>
                </div>

                <script type="text/javascript">//<![CDATA[
                (function(){
                    function init() {
                        Alfresco.util.createTwister("${el}-heading", "DocumentConnections");

                        if (documentConnectionsComponent == null) {
                            documentConnectionsComponent = new LogicECM.DocumentConnections("${el}").setOptions({
                                nodeRef: "${nodeRef}",
                                title: "${msg('heading')}",
								excludeType: "${excludeType!""}"
                            }).setMessages(${messages});
                        }
                    }

                    YAHOO.util.Event.onContentReady("${el}", init, true);
                })();
                //]]></script>
            </div>
        </div>
	</#if>
</@>
