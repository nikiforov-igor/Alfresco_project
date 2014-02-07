<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#assign htmlId = args.htmlid>
<#assign containerId = args.htmlid + '-control-container'>
<#assign btnsAddItemId = htmlId + '-buttons-container'>
<#assign radioWorkflowTypeId = htmlId + '-radio-container'>
<#assign menuContainerId = htmlId + '-menu-container'>
<#assign btnsControlId = htmlId + '-buttons-cntrl'>
<#assign datagridId = htmlId + '-datagrid'>

<#assign workflowType = field.control.params.workflowType?lower_case>
<#assign concurrency = field.control.params.concurrency!'user'?lower_case>

<div id='${containerId}'>
	<div id='${radioWorkflowTypeId}'></div>
	<div id='${btnsAddItemId}'></div>
	<div class='form-field with-grid'>
		<@grid.datagrid datagridId false />
	</div>
</div>

<script>
/* global Alfresco, LogicECM, YAHOO */
(function() {

	/**
	 * Конструктор датагрида, который используется на форме, расширяет экземпляр каждого датагрида кастомными
	 * опциями, которые понядобяться в дальнейшем.
	 */
	function WorkflowDatagrid(htmlId) {
		WorkflowDatagrid.superclass.constructor.call(this, htmlId);

		/**
		 * Тип бизнес-процесса для которого предназначен список, отображаемый текущим датагридом.
		 * Используется в evaluator-ах для action-ов - в зависимости от типа экшены либо показываются, либо нет.
		 * Например, для параллельного процесса порядок не имет значения. См. _initDatagrid!
		 */
		this.options.concurrency = 'sequential';

		return this;
	}

	YAHOO.lang.extend(WorkflowDatagrid, LogicECM.module.Base.DataGrid);

	/**
	 * Метод создаёт событие с bubblingLabel от экземпляра для которого этот метод вызывается, событие заставляет
	 * датагрид обновится. Кроме того, датагрид получает объект datagridMeta и "начинает играть по новым правилам".
	 */
	WorkflowDatagrid.prototype.refresh = function(nodeRef) {
		YAHOO.Bubbling.fire('activeGridChanged', {
			bubblingLabel: this.options.bubblingLabel,
			datagridMeta: {
				nodeRef: nodeRef,
				itemType: 'lecm-workflow:assignee',
				sort: 'lecm-workflow:assignee-order|true',
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			}
		});
	};

	/**
	 * Метод перемещает элемент вверх по списку.
	 * Метод привязывается к кнопкам датагрида, при его создании, в методе _initDatagrid объекта WorkflowList, через
	 * action.id
	 */
	WorkflowDatagrid.prototype.onMoveUp = function(items) {
		function onAjaxFailure() {
			Alfresco.util.PopupManager.displayMessage({
				text: 'Не удалось переместить элемент вверх'
			});
		}

		// TODO: Переделать
		var currentNodeRef = items.nodeRef, // {string}
				dataObj = { 'assigneeItemNodeRef': currentNodeRef, 'moveDirection': 'up' };

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/approval/changeOrder', // TODO: Переделать
			dataObj: dataObj,
			successCallback: {
				fn: this.refresh // TODO: Переделать
			},
			failureCallback: {
				fn: onAjaxFailure
			}
		});
	};

	/**
	 * Перемещает элемент списка вниз и обновляет таблицу
	 * Метод привязывается к кнопкам датагрида, при его создании, в методе _initDatagrid объекта WorkflowList, через
	 * action.id
	 */
	WorkflowDatagrid.prototype.onMoveDown = function AssigneesGrid_onMoveDown(items) {
		function onAjaxFailure() {
			Alfresco.util.PopupManager.displayMessage({
				text: 'Не удалось переместить элемент вниз'
			});
		}

		var currentNodeRef = items.nodeRef, // {string}
				dataObj = {
					'assigneeItemNodeRef': currentNodeRef,
					'moveDirection': 'down'
				};

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/approval/changeOrder',
			dataObj: dataObj,
			successCallback: {
				fn: this.refresh
			},
			failureCallback: {
				fn: onAjaxFailure
			}
		});
	};

	function WorkflowList(htmlId) {
		WorkflowList.superclass.constructor.call(this, 'LogicECM.module.WorkflowList', htmlId, null);

		// Инициализируется методом __getDefaultList__
		this.options.nodeRef = null;

		this.options.concurrency = null;
		this.options.workflowType = null;
	}

	YAHOO.lang.extend(WorkflowList, Alfresco.component.Base);

	WorkflowList.prototype._refreshDatagrid = function() {
		var datagrid = this.widgets.datagrid;
		var nodeRef = this.options.nodeRef;

		if(datagrid !== null || datagrid !== undefined) {
			if(nodeRef !== null || nodeRef !== undefined) {
				datagrid.refresh(nodeRef);
			}
		}
	};

	/**
	 * Метод получает nodeRef-у, на которую будет отображён datagrid
	 */
	WorkflowList.prototype.__getDefaultList__ = function() {
		function onAjaxSuccess(response) {
			var defaultAssigneesList = response.json.defaultAssigneesList;

			this.options.nodeRef = defaultAssigneesList;
			this._refreshDatagrid();
		}

		function onAjaxFailure() {
			Alfresco.util.PopupManager.displayMessage({
				text: 'Не удалось получить nodeRef на временный список согласования'
			});
		}

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/workflow/getDefaultAssigneesList',
			dataObj: {
				workflowType: this.options.workflowType.toUpperCase(),
				concurrency: this.options.concurrency.toUpperCase()
			},
			successCallback: {
				fn: onAjaxSuccess,
				scope: this
			},
			failureCallback: {
				fn: onAjaxFailure
			}
		});
	};

	/**
	 * Метод создаёт кнопки и привязывает обработчики
	 */
	WorkflowList.prototype._initButtons = function() {
		// Кнопка-список 'Список'
		this.widgets.btnSelectList = new YAHOO.widget.Button({
			container: '${btnsAddItemId}',
			type: 'menu',
			label: 'Выберите список'
		});

		// Радио-кнопки для типа бизнес-процесса
		if(this.options.concurrency === 'user') {
			this.widgets.radioWorkflowType = new YAHOO.widget.ButtonGroup({
				id: 'workflow-type-radio-buttons',
				name: 'workflow-type-radio-buttons',
				container: '${radioWorkflowTypeId}'
			});

			this.widgets.radioWorkflowType.addButtons([
				{ label: 'Последовательное', value: 'sequential', checked: true },
				{ label: 'Параллельное', value: 'parallel' }
			]);

			this.widgets.radioWorkflowType.getButton(0).setStyle('margin-left', '1px');

			this.options.concurrency = 'sequential';
		}

		// Кнопка 'Добавить сотрудника'
		this.widgets.btnAddAssignee = new YAHOO.widget.Button({
			container: '${btnsAddItemId}',
			type: 'push',
			label: 'Добавить сотрудника',
			onclick: {
				fn: this._onAddAssigneeButtonClick,
				obj: { type: 'assignee' },
				scope: this
			}
		});

		// Кнопка 'Добавить должность'
		this.widgets.btnAddPosition = new YAHOO.widget.Button({
			container: '${btnsAddItemId}',
			type: 'push',
			label: 'Добавить должность',
			onclick: {
				fn: this._onAddAssigneeButtonClick,
				obj: { type: 'position' },
				scope: this
			}
		});
	};

	/**
	 * Заполняет меню выбора списка бизнес-процесса
	 */
	WorkflowList.prototype._fillMenu = function() {

	};

	/**
	 * Метод создаёт датагрид
	 */
	WorkflowList.prototype._initDatagrid = function(options) {
		debugger;

		this.widgets.datagrid = new WorkflowDatagrid('${datagridId}');

		this.widgets.datagrid.setOptions({
			concurrency: this.options.concurrency,

			bubblingLabel: '${datagridId}-label',
			usePagination: false,
			showExtendSearchBlock: false,
			showCheckboxColumn: false,
			searchShowInactive: false,
			forceSubscribing: true,
			showActionColumn: true,
			overrideSortingWith: false,

			actions: [
				{
					type: 'datagrid-action-link-${datagridId}-label',
					id: 'onMoveUp',
					permission: 'edit',
					label: 'Переместить вверх',
					evaluator: function() {
						return this.options.concurrency === 'sequential';
					}
				},
				{
					type: 'datagrid-action-link-${datagridId}-label',
					id: 'onMoveDown',
					permission: 'edit',
					label: 'Переместить вниз',
					evaluator: function() {
						return this.options.concurrency === 'sequential';
					}
				},
				{
					type: 'datagrid-action-link-${datagridId}-label',
					id: 'onActionDelete',
					permission: 'delete',
					label: 'Удалить'
				}
			],

			datagridMeta: {
				itemType: 'lecm-workflow:assignee', // TODO: Забрать из SCC!
				nodeRef: '', // Пусто блять...
				sort: 'lecm-workflow:assignee-order|true', // TODO: Забрать из SCC!
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			}
		});

		this.widgets.datagrid.draw();
	};

	/**
	 * Обработчик клика по кнопке 'Добавить сотрудника' и 'Добавить должность'
	 * Показывает форму создания типа, который отображает датагрид. Destination для формы тот же, что у датагрида.
	 */
	WorkflowList.prototype._onAddAssigneeButtonClick = function(options) {
		this.widgets.formAddAssignee = new Alfresco.module.SimpleDialog('${htmlId}-form-add-assignee');

		this.widgets.formAddAssignee.setOptions({
			width: '50em',
			templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
			templateRequestParams: {
				itemKind: 'type',
				itemId: 'lecm-workflow:assignee', // TODO: Переделать
				destination: this.options.nodeRef,
				mode: 'create',
				submitType: 'json',
				showCancelButton: 'true'//,
//				ignoreNodes: this.getCurrentNodeRefs().join(), // TODO: Должен быть мето, который даёт эту строку
//				allowedNodes: this.constants.ALLOWED_ASSIGNEES.join() // TODO: Должен быть мето, который даёт эту строку
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function(form, simpleDialog) {
					simpleDialog.dialog.setHeader('Добавить элемент списка бизнес-процесса');
				}
			},
			onSuccess: {
				fn: this._refreshDatagrid,
				scope: this
			},
			onFailure: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'При добавлении элемента произошла ошибка, попробуйте переоткрыть форму'
					});
				},
			}
		});

		this.widgets.formAddAssignee.show();
	};

	/**
	 * Метод onReady вызывается внутренними механизмами Alfreso и YUI, когда все модули, от которых зависит данный
	 * модуль будут загружены и готовы. Так как зависимостей у этого модуля нет, этот метод будет вызван 'на месте'.
	 */
	WorkflowList.prototype.onReady = function() {
		this._initButtons();
		this._initDatagrid();

		this.__getDefaultList__();
	};

	var workflowList = new WorkflowList('${containerId}');
	workflowList.setOptions({
		concurrency: '${concurrency}',
		workflowType: '${workflowType}'
	});

	Alfresco.util.PopupManager.zIndex = 9000;

})();
</script>