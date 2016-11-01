<#assign id = args.htmlid>

<div class="dashlet document bordered members">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMembersComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
			<a id="${id}-action-experts-doc" href="${url.context}/page/experts-by-document?nodeRef=${nodeRef}" class="semantic-list"  target="_blank" title="${msg('dashlet.semantic.experts.tooltip')}">&nbsp</a>
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
                        <h3><a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${member.employeeRef}', title: 'logicecm.employee.view'})">${member.employeeName}</a></h3>
                        <div>${member.employeePosition}</div>
                        <div class="member-ref hidden1">${member.employeeRef}</div>
                    </div>
                </div>
            </#list>
        </#if>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
	(function(){
		function init(){
			var semanticEl = YAHOO.util.Dom.get("semantic-mudule-active-htmlid");
			if (!semanticEl){
				var dashletAction = YAHOO.util.Dom.get("${id}-action-experts-doc");
				if (dashletAction){
					YAHOO.util.Dom.setStyle(dashletAction, 'display', 'none');
				}
			}
		}
		YAHOO.util.Event.onContentReady("${id}-action-experts-doc", init, true);
	})();
//]]></script>