
<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<#assign docRef= nodeRef/>
<div id="${el}" class="members-list">
    <hr>
    <div class="members-table body scrollableList">
		<#if expertsList??>
			<#assign keys = expertsList?keys>
			<#list keys?sort?reverse as key>
				<#list expertsList[key] as mapAttrs>
					<div class="detail-list-item">
						<div class="avatar">
							<img src="${url.context}/proxy/alfresco/lecm/profile/employee-photo?nodeRef=${mapAttrs["expertRef"]}"alt="Avatar" />
						</div>
						<#assign fio = mapAttrs["lastName"] +" "+ mapAttrs["firstName"] +" "+ mapAttrs["middleName"]>
						<div class="person">
							<h3><a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${mapAttrs["expertRef"]}', title: 'logicecm.employee.view'})">${fio}</a></h3>
							<#if mapAttrs["staf"] != "">
								<div>${mapAttrs["staf"]}</div>
							</#if>
							<div class="member-ref" style="display: none">${mapAttrs["expertRef"]}</div>
						</div>
						<hr>
					</div>
				</#list>
			</#list>
		</#if>
    </div>
</div>