
<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<#assign docRef= nodeRef/>
<#assign hasAddPermission = (hasStatemachine && mayAdd?? && mayAdd)/>

<script type="text/javascript">
    function hideButton() {
        if(location.hash != "#expanded") {
            YAHOO.util.Dom.setStyle(this, 'display', 'none');
        }
    }
    YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
</script>

<div class="panel-header">
    <div class="panel-title">${msg("label.title")}</div>
        <div class="lecm-dashlet-actions">
            <a id="${el}-action-collapse" class="collapse" title="${msg('msg.collapse')}"></a>
        </div>
</div>

<div id="${el}" class="members-list">
    <table class="members-title">
        <tr>
            <td class="members-name">
            ${msg("label.document.members")}
            </td>
            <#if hasAddPermission>
                <td class="members-add">
                    <div class="member-add">
                   <span id="${el}-addMember-button" class="yui-button yui-push-button">
                      <span class="first-child">
                         <button type="button" title="${msg("button.member.add")}">${msg("button.add.member")}</button>
                      </span>
                   </span>
                    </div>
                </td>
            </#if>
        </tr>
    </table>
    <hr>
    <div class=" members-scrollable">
        <div class="members-table">
        <#if members?? && members.items??>
            <#list members.items as member>
                <div class="detail-list-item <#if member_has_next>border-bottom</#if>">
                    <div class="avatar">
                        <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${member.employeeRef}" alt="Avatar"/>
                    </div>
                    <div class="member">
                        <h3><a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${member.employeeRef}', title: 'logicecm.employee.view'})">${member.employeeName}</a></h3>

                        <div>${member.employeePosition}</div>
                        <div class="member-ref hidden1">${member.employeeRef}</div>
                    </div>
                    <div class="member-actions">
                        <div class="list-action-set">
                            <#if mayDelete>
                                <div class="onActionDelete" data-noderef="${member.nodeRef!""}"
                                     data-name="${member.employeeName!""}">
                                    <a title="${msg("action.delete-member.title")}" class="list-action-link" href="#">
                                        <span>
                                        ${msg("action.delete-member.title")}
                                        </span>
                                    </a>
                                </div>
                            </#if>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </#list>
        </#if>
        </div>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {

        function loadDeps() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/documents/members/lecm-document-members.js'
            ], init);
        }

        function init() {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/document/api/getMembersFolder",
                dataObj: {
                    nodeRef: "${docRef}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response && response.nodeRef) {
                            draw(response.nodeRef);
                        }
                    },
                    scope: this
                },
                failureMessage: "${msg('message.failure')}"
            });
        }

        function draw(folderRef) {
            var control = new LogicECM.module.Members.DocumentMembers("${el}").setMessages(${messages});
            control.setOptions({
                documentNodeRef: "${docRef}",
                documentMembersFolderRef: folderRef,
                datagridBublingLabel: "document-members-list"
            });
        }

        YAHOO.util.Event.onDOMReady(loadDeps);
    })();
    //]]></script>
</div>