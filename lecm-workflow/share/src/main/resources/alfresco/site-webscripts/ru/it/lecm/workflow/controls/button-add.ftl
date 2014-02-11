<!-- Форма должна сабмитить
1. Тип процесса: послед или пар.
2. нодРефу на текущий список
-->

<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#-- Контейнеры -->
<#assign htmlId = args.htmlid>
<#assign containerId = args.htmlid + '-control-container'>
<#assign dimmerId = args.htmlid + '-dimmer'>
<#assign btnsAddItemId = htmlId + '-buttons-container'>
<#assign radioWorkflowTypeId = htmlId + '-radio-container'>
<#assign menuContainerId = htmlId + '-menu-container'>

<#-- Контролы -->
<#assign btnsControlId = htmlId + '-buttons-cntrl'>

<#-- Экземпляры -->
<#assign createListButtonId = htmlId + "-save-list-button">
<#assign deleteListButtonId = htmlId + "-delete-list-button">
<#assign datagridId = htmlId + '-datagrid'>

<#-- SCC -->
<#assign workflowTypeShareConfig = field.control.params.workflowType?lower_case>
<#assign concurrencyShareConfig = field.control.params.concurrency!'user'?lower_case>

<#-- Отправка данных -->
<#assign concurrencyInputId = htmlId + "-concurrency-input">
<#assign concurrencyInputName = field.control.params.concurrencyInputName>
<#assign concurrencyInputInitialValue = (concurrencyShareConfig == 'user') ? string('SEQUENTIAL', concurrencyShareConfig)>

<#assign listNodeRefInput = htmlId + "-workflow-type-input">


<div id='${containerId}' class="workflow-list-control-container">
<#-- Элемент-затемнитель контрола, который показывается на время его инициализации -->
	<div id="${dimmerId}" class="workflow-list-control-dimmer">
		<div class="workflow-list-control-dimmer__inner">Пододжите, пожалуйста...</div>
	</div>

	<div style="margin: 5px 0;">
		<div class="form-field">
			<label for="${radioWorkflowTypeId}">NodeRef на папку:</label>
			<input type="text" id="${listNodeRefInput}" name="${field.name}_added" value=""/>
		</div>
	</div>

	<div style="margin: 5px 0;">
		<div class="form-field">
			<label for="${radioWorkflowTypeId}">Тип бизнес-процесса:</label>
			<span id="${radioWorkflowTypeId}"></span>
			<input type="hidden" id="${concurrencyInputId}" name="${concurrencyInputName}" value="${concurrencyInputInitialValue}"/>
		</div>
	</div>

	<div style="margin: 5px 0;">
		<div class="form-field">
			<label for="${menuContainerId}">Список:</label>
			<span id="${menuContainerId}"></span>
			<span class="create-new-button">
				<button id="${createListButtonId}"></button>
			</span>
			<span class="delete-button">
				<button id="${deleteListButtonId}"></button>
			</span>
		</div>
	</div>

	<div id='${btnsAddItemId}' style="margin-bottom: 10px;"></div>

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
	 * датагрид обновится. Кроме того, датагрид получает объект datagridMeta и 'начинает играть по новым правилам'.
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

		var workflowList = this;

		// Инициализируется методом _initControl
		workflowList.options.currentListRef = null;

		workflowList.setOptions({
			concurrency: '${concurrencyShareConfig}',
			workflowType: '${workflowTypeShareConfig}'
		});
	}

	YAHOO.lang.extend(WorkflowList, Alfresco.component.Base);

	/**
	 * Метод вызывается самым первым, должен сходить на сервер, получить все данные (желательно, за один запрос),
	 * необходимые для формы и "включить" форму. На время выполнения запроса, контрол должен быть неактивен. На момент
	 * написания этого комментария был оформлен в виде заглушки с таймаутом.
	 */
	WorkflowList.prototype._initControl = function() {
		function turnOnTheControl() {
			// На момент написания комментария, тут стоит заглушка, которая вызывается, как будто бы на ajaxSuccess,
			// получая все данные необходимые для контрола.
			//
			// Должны прийти следующие данные:
			// 1. Списки согласования текущего пользователя и дефолтный тоже;
			// 2. nodeRef для кнопки создания нового списка (nodeRef указывает на папку, где хранятся списки).
			this.widgets.datagrid.setOptions({
			});

			dimmer.style.display = 'none';
		}

		var dimmer = YAHOO.util.Dom.get('${dimmerId}');

		setTimeout(turnOnTheControl.bind(this), 3000);
	};

	WorkflowList.prototype._refreshDatagrid = function() {
		var datagrid = this.widgets.datagrid;
		var nodeRef = this.options.currentListRef;

		if (datagrid !== null || datagrid !== undefined) {
			if (nodeRef !== null || nodeRef !== undefined) {
				datagrid.refresh(nodeRef);
			}
		}
	};

	WorkflowList.prototype._onConcurrencyChange = function(event) {
		var newConcurrencyValue = event.newValue;
		this._setConcurrency(newConcurrencyValue);
	};

	/**
	 * Метод получает nodeRef-у, на которую будет отображён datagrid
	 */
	WorkflowList.prototype.__getDefaultList__ = function() {
		function onAjaxSuccess(response) {
			var defaultAssigneesList = response.json.defaultAssigneesList;
			var listRefInput = YAHOO.util.Dom.get('${listNodeRefInput}');

			listRefInput.value = defaultAssigneesList;

			this.options.currentListRef = defaultAssigneesList;
			this._refreshDatagrid();
		}

		function onAjaxFailure() {
			Alfresco.util.PopupManager.displayMessage({
				text: 'Не удалось получить nodeRef на временный список согласования'
			});
		}

		// Получаем nodeRef на временный список, создающийся на основе workflowType и concurrency
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

	WorkflowList.prototype._onNewAssigneesListButtonClick = function() {
		if (this.constants.LISTS_FOLDER_REF === null) {
			return false;
		}

		this.widgets.addAssigneesListForm = new Alfresco.module.SimpleDialog('-form-add-assignees-list');

		this.widgets.addAssigneesListForm.setOptions({
			width: '50em',
			templateUrl: Alfresco.constants.URL_SERVICECONTEXT + this.constants.URL_FORM,
			templateRequestParams: {
				itemKind: 'type',
				itemId: 'lecm-al:assignees-list',
				destination: this.constants.LISTS_FOLDER_REF,
				mode: 'create',
				submitType: 'json',
				showCancelButton: 'true'
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function(form, simpleDialog) {
					simpleDialog.dialog.setHeader('Новый список элементов бизнес-процесса');
				}
			},
			onSuccess: {
				fn: this.fillApprovalListMenu,
				scope: this
			},
			onFailure: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось создать новый список, попробуйте переоткрыть форму'
					});
				},
				scope: this
			}
		});

		this.widgets.addAssigneesListForm.show();

		return true;
	};

	/**
	 * Метод создаёт кнопки и привязывает обработчики
	 */
	WorkflowList.prototype._initButtons = function() {
		// Кнопка 'Список'
		this.widgets.btnSelectList = new YAHOO.widget.Button({
			container: '${menuContainerId}',
			type: 'menu',
			label: 'Выберите список'
		});

		this.widgets.btnSelectList.setStyle('margin-left', '1px');

		// Кнопка 'Создать новый список'
		this.widgets.btnCreateList = new YAHOO.widget.Button('${createListButtonId}', {
			type: 'push',
			onclick: {
				//fn: this._onAddAssigneeButtonClick,
				scope: this
			}
		});

		// Кнопка 'Удалить выбранный список'
		this.widgets.btnCreateList = new YAHOO.widget.Button('${deleteListButtonId}', {
			type: 'push',
			onclick: {
				//fn: this._onAddAssigneeButtonClick,
				scope: this
			}
		});

		// Радио-кнопки для типа бизнес-процесса
		if (this.options.concurrency === 'user') {
			this.widgets.radioWorkflowType = new YAHOO.widget.ButtonGroup({
				id: 'workflow-type-radio-buttons',
				name: 'workflow-type-radio-buttons',
				container: '${radioWorkflowTypeId}'
			});

			this.widgets.radioWorkflowType.subscribe('valueChange', this._onConcurrencyChange, null, this);

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

	WorkflowList.prototype._setConcurrency = function(value) {
		var workflowList = this;
		var datagrid = workflowList.widgets.datagrid;
		var dataTable = datagrid.widgets.dataTable;

		var SEQUENTIAL = { concurrency: 'sequential' };
		var PARALLEL = { concurrency: 'parallel' };

		var input = YAHOO.util.Dom.get('${concurrencyInputId}');

		if (value === 'user' || value === 'sequential') {
			input.value = 'SEQUENTIAL';

			workflowList.setOptions(SEQUENTIAL);
			datagrid.setOptions(SEQUENTIAL);
			dataTable.showColumn(0);
			dataTable.showColumn(1);
		} else {
			input.value = 'PARALLEL';

			workflowList.setOptions(PARALLEL);
			datagrid.setOptions(PARALLEL);
			dataTable.hideColumn(0);
			dataTable.hideColumn(1);
		}
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
				itemId: 'lecm-workflow:assignee',
				destination: this.options.currentListRef,
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
		this._initControl();

		this._initButtons();
		this._initDatagrid();

		this.__getDefaultList__();
	};

	var workflowList = new WorkflowList('${containerId}');

	Alfresco.util.PopupManager.zIndex = 9000;

})();
</script>