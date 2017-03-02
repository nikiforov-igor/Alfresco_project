<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-connections-list.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-connections.css" />
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
        <div id="${el}" class="widget-bordered-panel connections-panel">
            <div id="${el}-wide-view" class="document-metadata-header document-components-panel">
                <h2 id="${el}-heading" class="dark">
                ${msg("heading")}
                    <span class="alfresco-twister-actions">
                        <a id="${el}-action-expand" href="javascript:void(0);" class="expand connections-expand" title="${msg("label.expand")}">&nbsp</a>
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

            <div id="${el}-short-view" class="document-components-panel short-view">
                <span class="alfresco-twister-actions">
                    <a href="javascript:void(0);" class="expand connections-expand" title="${msg("label.expand")}">&nbsp</a>
                </span>
                <div id="${el}-formContainer" class="right-block-content">
                    <span class="yui-button yui-push-button">
                       <span class="first-child">
                          <button type="button" title="${msg('heading')}"></button>
                       </span>
                    </span>
                </div>
            </div>
            <script type="text/javascript">//<![CDATA[
                (function () {
                    function init() {
                        LogicECM.services = LogicECM.services || {};
                        if (LogicECM.services.DocumentViewPreferences) {
                            var shortView = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
                            if (shortView) {
                                Dom.addClass("${el}-wide-view", "hidden");
                            } else {
                                Dom.addClass("${el}-short-view", "hidden");
                            }
                        } else {
                            Dom.addClass("${el}-short-view", "hidden");
                        }
                    }
                    YAHOO.util.Event.onContentReady("${el}", init, true);
                })();
            //]]></script>
        </div>

	</#if>
</@>
