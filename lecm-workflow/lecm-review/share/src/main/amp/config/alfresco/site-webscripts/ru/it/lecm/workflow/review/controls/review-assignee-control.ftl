<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#assign params = field.control.params/>
<#assign label = field.label?html/>
<#assign itemId = args.itemId/>
<#assign controlId = fieldHtmlId + "-cntrl">

<div id='${controlId}'>

	<span id="${controlId}-add-employee" class="yui-button yui-push-button">
		<span class="first-child">
			<button type="button">Добавить участника</button>
		</span>
	</span>

	<input type="hidden" id="${controlId}-list-node-input" name="${field.name}_added" value=""/>

	<@grid.datagrid controlId false />
</div>

<script type='text/javascript'>
(function () {
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};

	function createDatagrid() {
		var controlId = '${controlId}';
		var reviewAssigneeControl = new LogicECM.module.Review.AssigneeControl(controlId);
		reviewAssigneeControl.setMessages(${messages});
		reviewAssigneeControl.setOptions({
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			overrideSortingWith: false,
			forceSubscribing: true,
			bubblingLabel: '${controlId}',
			expandable: false,
			showActionColumn: true,
			actions: [{
				type:"datagrid-action-link-" + controlId,
				id:"onActionDelete",
				permission:"delete",
				label:"${msg('actions.delete-row')}"
			}]
		});
	}

	YAHOO.util.Event.onDOMReady(function() {

		var js = ['scripts/lecm-base/components/lecm-datagrid.js',
				  'scripts/lecm-base/components/advsearch.js',
				  'modules/simple-dialog.js',
				  'scripts/lecm-review/review-assignee-control.js'];
		var css = [];
		LogicECM.module.Base.Util.loadResources(js, css, createDatagrid);
	});
})();
</script>
