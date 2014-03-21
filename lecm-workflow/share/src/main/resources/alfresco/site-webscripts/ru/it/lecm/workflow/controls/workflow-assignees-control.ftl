<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#-- Контейнеры -->
<#assign htmlId = args.htmlid>
<#assign formId = htmlId + '-form'>

<#assign namespaceId = htmlId + (field.control.params.namespace ! '')>
<#assign containerId = namespaceId + '-control-container'>
<#assign dimmerId = namespaceId + '-dimmer'>
<#assign buttonsContainerId = namespaceId + '-buttons-container'>
<#assign radioWorkflowTypeId = namespaceId + '-radio-container'>
<#assign menuContainerId = namespaceId + '-menu-container'>

<#-- Контролы -->
<#assign btnsControlId = namespaceId + '-buttons-cntrl'>

<#-- Экземпляры -->
<#assign saveListButtonId = namespaceId + '-save-list-button'>
<#assign deleteListButtonId = namespaceId + '-delete-list-button'>
<#assign datagridId = namespaceId + '-datagrid'>
<#assign daysToCompleteFieldId = namespaceId + '-days-to-complete-field'>

<#-- SCC -->
<#assign workflowTypeShareConfig = field.control.params.workflowType ? lower_case>
<#assign concurrencyShareConfig = ((field.control.params.concurrency) ! 'user') ? lower_case>
<#assign allowedBusinessRoleId = (field.control.params.allowedBusinessRoleId) ! ''>

<#assign showListSelectMenu = (field.control.params.showListSelectMenu!'false') == 'true'>
<#assign showComputeTermsButton = (field.control.params.showComputeTermsButton!'false') == 'true'>
<#assign itemType = field.control.params.itemType!''>

<#assign shouldInitAllowed = allowedBusinessRoleId ? has_content>
<#assign formAddAssigneeTitle = (field.control.params.formAddAssigneeTitle) ! 'Добавить участника'>

<#-- Отправка данных -->
<#assign concurrencyInputId = namespaceId + '-concurrency-input'>
<#assign concurrencyInputName = field.control.params.concurrencyInputName ! ''>
<#assign concurrencyInputInitialValue = (concurrencyShareConfig == 'user') ? string('SEQUENTIAL', concurrencyShareConfig)>
<#assign concurrencyIsNotPredefined = concurrencyShareConfig == 'user'>

<#assign listNodeRefInput = namespaceId + '-workflow-type-input'>

<#assign dueDateId = '${htmlId}_prop_bpm_workflowDueDate'>

<#-- Состояния -->
<#assign routeRef = (form.mode == 'edit') ? string(args.itemId, '')>
<#assign isRoute = routeRef?has_content>

<div id='${containerId}' class="workflow-list-control-container">
	<#-- Элемент-затемнитель контрола, который показывается на время его инициализации -->
	<div id="${dimmerId}" class="workflow-list-control-dimmer">
		<div class="workflow-list-control-dimmer__inner">Подождите, пожалуйста...</div>
	</div>

	<#-- NodeRef на папку -->
	<input type="hidden" id="${listNodeRefInput}" name="${field.name}_added" value=""/>

<#if concurrencyIsNotPredefined>
<#-- Если тип бизнес-процесса не задан через SCC, то дадим пользователю возможность выбора -->
	<div style="margin: 5px 0;">
		<div class="form-field">
			<label for="${radioWorkflowTypeId}">Тип бизнес-процесса:</label>
			<span id="${radioWorkflowTypeId}"></span>
			<input type="hidden" id="${concurrencyInputId}" name="${concurrencyInputName}"
				   value="${concurrencyInputInitialValue}"/>
		</div>
	</div>
</#if>

<#if showListSelectMenu>
	<div style="margin: 5px 0;">
		<div class="form-field">
			<label for="${menuContainerId}">Список:</label>
			<span id="${menuContainerId}"></span>
			<span class="save-button">
				<button id="${saveListButtonId}"></button>
			</span>
			<span class="delete-button">
				<button id="${deleteListButtonId}"></button>
			</span>
		</div>
	</div>
</#if>

	<div id='${buttonsContainerId}' style="margin-bottom: 10px;"></div>

	<div class='form-field with-grid'>
	<@grid.datagrid datagridId false />
	</div>

<#if isRoute>
	<div class='form-field'>
		Срок <input type="text" id="${daysToCompleteFieldId}" style="width: 3em; float: none;"> дней после отправки по маршруту
	</div>
</#if>
</div>

<script>
	(function() {
		LogicECM.CurrentModules = LogicECM.CurrentModules || {};

		var loader = new YAHOO.util.YUILoader({
			require: [
				"lecmDatagrid",
				"lecmWorkflowAssigneesControl",
				"lecmWorkflowAssigneesControlCSS"
			],
			skin: {}
		});

		loader.addModule({
			name: 'lecmDatagrid',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/components/lecm-datagrid.js'
		});

		loader.addModule({
			name: 'lecmWorkflowAssigneesControl',
			type: 'js',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-workflow/workflow-assignee-control.js'
		});

		loader.addModule({
			name: 'lecmWorkflowAssigneesControlCSS',
			type: 'css',
			fullpath: Alfresco.constants.URL_RESCONTEXT + 'css/lecm-workflow/result-list.css'
		});

		loader.onSuccess = createControl;
		loader.insert();

		function createControl() {
			var workflowList, initOptions = {
				allowedBusinessRoleId: '${allowedBusinessRoleId}',
				buttonsContainerId: '${buttonsContainerId}',
				concurrency: '${concurrencyShareConfig}',
				concurrencyInputId: '${concurrencyInputId}',
				containerId: '${containerId}',
				datagridId: '${datagridId}',
				daysToCompleteFieldId: '${daysToCompleteFieldId}',
				deleteListButtonId: '${deleteListButtonId}',
				dimmerId: '${dimmerId}',
				dueDateId: '${dueDateId}',
				formId: '${formId}',
				formItemType: '${itemType}',
				htmlId: '${htmlId}',
				isRoute: ${isRoute?string},
				listNodeRefInput: '${listNodeRefInput}',
				menuContainerId: '${menuContainerId}',
				namespaceId: '${namespaceId}',
				radioWorkflowTypeId: '${radioWorkflowTypeId}',
				routeRef: '${routeRef}',
				saveListButtonId: '${saveListButtonId}',
				showComputeTermsButton: ${showComputeTermsButton?string},
				showListSelectMenu: ${showListSelectMenu?string},
				workflowType: '${workflowTypeShareConfig}',
				formAddAssigneeTitle: '${formAddAssigneeTitle}'
			};
			LogicECM.CurrentModules['${containerId}'] = new LogicECM.module.Workflow.WorkflowList(initOptions);
			Alfresco.util.PopupManager.zIndex += 100;
		}
	})();
</script>
