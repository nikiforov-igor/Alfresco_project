<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign editable = form.mode != "view"/>

<#assign itemId = args.itemId/>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function init() {
		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/lecm-datagrid.js',
			'scripts/lecm-workflow/routes/stages-control.js'
		],
		[], createDatagrid);
	}

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Routes.StagesControlDatagrid(controlId);
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: controlId,
			expandable: true,
			forceSubscribing: true,
			excludeColumns: ["lecmApproveAspects:approvalState"],
			expandDataSource: "ru/it/lecm/workflow/routes/stages/stageExpanded",
			<#if editable>
			actions: [{
				type:"datagrid-action-link-" + controlId,
				id:"onActionEdit",
				permission:"edit",
				label:"${msg('actions.edit')}"
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionDelete",
				permission:"delete",
				label:"${msg('actions.delete-row')}"
			}],
			</#if>
			showActionColumn: ${editable?string},
			allowCreate: ${editable?string}
		});

		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType,
				nodeRef: '${itemId}',
				searchConfig: {
					filter: '-ASPECT:"sys:temporary" AND -ASPECT:"lecm-workflow:temp"'
				},
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			bubblingLabel: controlId
		});
	}

	YAHOO.util.Event.onContentReady('${controlId}', init);
})();
</script>

<div class='form-field'>
	<div id='${controlId}'>
		<@grid.datagrid controlId false />
	</div>
</div>
<div class="clear"></div>
