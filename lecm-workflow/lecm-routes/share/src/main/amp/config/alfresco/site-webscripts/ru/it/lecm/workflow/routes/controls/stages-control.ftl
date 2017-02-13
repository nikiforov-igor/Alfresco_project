<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign params = field.control.params/>
<#assign editable = form.mode != "view"/>

<#assign createStageFormId="createStageForm"/>
<#if field.control.params.contextProperty??>
	<#if context.properties[field.control.params.contextProperty]??>
		<#assign createStageFormId = context.properties[field.control.params.contextProperty]>
	<#elseif args[field.control.params.contextProperty]??>
		<#assign createStageFormId = args[field.control.params.contextProperty]>
	</#if>
<#elseif context.properties[field.name]??>
	<#assign createStageFormId = context.properties[field.name]>
</#if>

<#assign disableAddButton = false>
<#if field.control.params.disableIfOrganizationEmpty?? && field.control.params.disableIfOrganizationEmpty == "true">
	<#assign disableAddButton = true>
</#if>

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
		[
            'css/lecm-workflow/stages-control.css'
        ], createDatagrid);
	}

	function createDatagrid() {
		var controlId = '${controlId}';
		LogicECM.CurrentModules[controlId] = new LogicECM.module.Routes.StagesControlDatagrid(controlId);
		LogicECM.CurrentModules[controlId].setMessages(${messages});
		LogicECM.CurrentModules[controlId].setOptions({
            createStageFormId: "${createStageFormId}",
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			bubblingLabel: controlId,
			overrideSortingWith: false,
			expandable: true,
			forceSubscribing: true,
			excludeColumns: ["lecmApproveAspects:approvalState", "lecmApproveAspects:hasComment"],
			expandDataSource: "ru/it/lecm/workflow/routes/stages/stageExpanded",
			expandDataObj: {
				editable: ${editable?string},
				isApproval: false,
                mainFormId: "${args.htmlid}",
                routeRef: '${form.arguments.itemId!""}'
			},
			<#if editable>
			actions: [{
				type:"datagrid-action-link-" + controlId,
				id:"onActionAddEmployee",
				permission:"edit",
				label:"${msg('actions.add.employee')}",
				evaluator: function (rowData) {
                    return !this.options.disableAddButton || this.routeOrganization != null;
				}
			}, {
				type:"datagrid-action-link-" + controlId,
				id:"onActionAddMacros",
				permission:"edit",
				label:"${msg('actions.add.macros')}"
			}, {
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
			allowCreate: ${editable?string},
            disableAddButton: ${disableAddButton?string},
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
		});

        LogicECM.CurrentModules[controlId].setRoute('${form.arguments.itemId!""}');

		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				useFilterByOrg: false,
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageType,
				nodeRef: '${itemId}',
				sort: 'cm:created|true',
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

        LogicECM.CurrentModules[controlId]._fireStageControlUpdated();
	}

	YAHOO.util.Event.onContentReady('${controlId}', init);
})();
</script>

<div class='form-field stages-control'>
	<div id='${controlId}'>
		<@grid.datagrid controlId false />
	</div>
</div>
<div class="clear"></div>
