<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#assign bubblingLabel = 'organizations-list'/>
<#assign datagridId = args.htmlid + '-' + bubblingLabel/>

<div id='${datagridId}'>
<@grid.datagrid id=datagridId showViewForm=true showArchiveCheckBox=true>
	<script type='text/javascript'>//<![CDATA[
		(function () {
			function initOrganizationsListDatagrid() {
				new LogicECM.module.OrgStructure.OrganizationsList('${datagridId}', {
					usePagination: true,
					disableDynamicPagination: false,
					showExtendSearchBlock: true,
					overrideSortingWith: false,
					showCheckboxColumn: false,
					showActionColumn: true,
					expandable: false,
					bubblingLabel: '${bubblingLabel}',
					actions: [{
						type:'datagrid-action-link-' + '${bubblingLabel}',
						id:'onActionEdit',
						permission:'edit',
						label: '${msg("actions.edit")}'
					}, {
						type:'datagrid-action-link-' + '${bubblingLabel}',
						id:'onActionDelete',
						permission:'delete',
						label: '${msg("actions.delete-row")}'
					}]
				}, {
					datagridFormId: 'organizationsListDatagrid',
					createFormId: 'createOrganization',
					itemType: '${itemType}',
					nodeRef: '${nodeRef}',
					useChildQuery: false,
					useFilterByOrg: false,
					//				sort: 'cm:created|true',
					searchConfig: {
						filter: '+ASPECT:"lecm-orgstr-aspects:is-organization-aspect"'
					},
					//				actionsConfig: {
					//					fullDelete: true,
					//					trash: false
					//				}
				}, ${messages});
			}

			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-base/components/advsearch.js',
				'scripts/lecm-base/components/lecm-datagrid.js',
				'scripts/lecm-orgstructure/organizations-list.js'
			], [
			], initOrganizationsListDatagrid);
		})();
	//]]></script>
</@>
</div>
