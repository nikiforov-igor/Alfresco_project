<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>

<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<#assign docRef= nodeRef/>
<#assign hasAddPermission = (mayAdd?? && mayAdd)/>
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
    <@view.viewForm formId="${el}-view-node-form"/>
    <div class="members-table body scrollableList">
    <#if members?? && members.items??>
        <#list members.items as member>
            <div class="detail-list-item">
                <div class="avatar">
                    <img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${member.employeeRef}"alt="Avatar" />
                </div>
                <div class="person">
                    <h3>${view.showViewLink(member.employeeName, member.employeeRef, "logicecm.employee.view")}</h3>
                    <div>${member.employeePosition}</div>
                    <div class="member-ref" style="display: none">${member.employeeRef}</div>
                </div>
                <hr>
            </div>
        </#list>
    </#if>
    </div>
    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/document/api/getMembersFolder",
                        dataObj: {
                            nodeRef: "${docRef}"
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                var oResults = eval("(" + oResponse.serverResponse.responseText + ")");
                                if (oResults && oResults.nodeRef) {
                                    draw(oResults.nodeRef);
                                }
                            }
                        },
                        failureMessage: "message.failure"
                    });
        }

        function draw(folderRef) {
            var control = new window.LogicECM.module.Members.DocumentMembers("${el}").setMessages(${messages});
            control.setOptions({
                documentNodeRef: "${docRef}",
                documentMembersFolderRef: folderRef,
                datagridBublingLabel: "document-members-list"
            });
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>
</div>