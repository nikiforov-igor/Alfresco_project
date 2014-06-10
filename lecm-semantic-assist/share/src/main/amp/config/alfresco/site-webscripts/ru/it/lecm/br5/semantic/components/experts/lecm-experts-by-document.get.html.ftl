<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>

<#assign aDateTime = .now>
<#assign el=args.htmlid + aDateTime?iso_utc/>
<#assign docRef= nodeRef/>
<div id="${el}" class="members-list">
    <hr>
    <@view.viewForm formId="${el}-view-node-form"/>
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
							<h3>${view.showViewLink(fio, mapAttrs["expertRef"], "logicecm.employee.view")}</h3>
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