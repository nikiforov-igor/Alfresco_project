<@markup id="js">
    <@script type="text/javascript" src="${url.context}/res/scripts/components/document-members.js"></@script>
</@>
<@markup id="css">
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-members-list.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-members.css" />
</@>

<@markup id="html">
    <#if members??>
        <#assign aDateTime = .now>
        <#assign el=args.htmlid + aDateTime?iso_utc/>
        <#assign skipCount=5/>

    <script type="text/javascript">
        //TODO:Переписать
        var documentMembersComponent = null;
    </script>

    <div id="${el}" class="widget-bordered-panel members-panel">
        <div id="${el}-wide-view" class="document-components-panel">
            <h2 id="${el}-heading" class="dark">
            ${msg("heading")}
                <span class="alfresco-twister-actions">
	            <a id="${el}-action-expand" href="javascript:void(0);" onclick=""
                   class="expand members-expand"
                   title="${msg("label.expand")}">&nbsp</a>
	         </span>
            </h2>

            <div id="${el}-formContainer">
                <#if members?? && members.items?? && (members.items?size > 0)>
                    <ul id="document-members-set" class="document-members-set document-right-set">
                        <#assign i=0/>
                        <#list members.items as item>
                            <#if i < skipCount>
                                <li class="text-broken">
                                    <a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${item.employeeRef}', title: 'logicecm.employee.view'})">${item.employeeName}</a><br/>
                                ${item.employeePosition}<br/>
                                    <#assign i = i+1/>
                                </li>
                            </#if>
                        </#list>
                        <#if members.hasNext == "true">
                            <li>
                                <div class="right-more-link-arrow" onclick="documentMembersComponent.onExpand();"></div>
                                <div class="right-more-link"
                                     onclick="documentMembersComponent.onExpand();">${msg('label.members.more')}</div>
                                <div class="clear"></div>
                            </li>
                        </#if>
                    </ul>
                <#else>
                    <div class="block-empty-body right-block-content">
				    <span class="block-empty faded">
                    ${msg("message.block.empty")}
                    </span>
                    </div>
                </#if>
            </div>

            <script type="text/javascript">//<![CDATA[
            (function () {
                function init() {
                    if (documentMembersComponent == null) {
                        documentMembersComponent = new LogicECM.DocumentMembers("${el}").setOptions(
                                {
                                    nodeRef: "${nodeRef}",
                                    title: "${msg('heading')}"
                                }).setMessages(${messages});
                    }
                }

                YAHOO.util.Event.onContentReady("${el}", init, true);
            })();
            //]]>
            </script>
        </div>
        <div id="${el}-short-view" class="document-components-panel short-view">
        <span class="alfresco-twister-actions">
            <a href="javascript:void(0);" onclick="" class="expand members-expand" title="${msg("label.expand")}">&nbsp</a>
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
    LogicECM.services = LogicECM.services || {};
    var shortView = LogicECM.services.DocumentViewPreferences.getShowRightPartShort();
    if (shortView) {
        Dom.addClass("${el}-wide-view", "hidden");
    } else {
        Dom.addClass("${el}-short-view", "hidden");
    }
    //]]></script>
    </div>
    </#if>
</@>