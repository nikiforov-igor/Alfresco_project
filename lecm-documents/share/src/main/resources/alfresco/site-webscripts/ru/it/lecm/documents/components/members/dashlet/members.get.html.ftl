<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
<#assign id = args.htmlid>

<div class="dashlet document bordered members">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMembersComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
         </span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
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
                </div>
            </#list>
        </#if>
    </div>
</div>