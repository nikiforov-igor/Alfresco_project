<#import '/ru/it/lecm/base-share/components/lecm-datagrid.ftl' as grid/>

<#-- Контейнеры -->
<#assign htmlId = args.htmlid>
<#assign containerId = args.htmlid + '-control-container'>
<#assign dimmerId = args.htmlid + '-dimmer'>
<#assign buttonsContainerId = htmlId + '-buttons-container'>
<#assign radioWorkflowTypeId = htmlId + '-radio-container'>
<#assign menuContainerId = htmlId + '-menu-container'>

<#-- Контролы -->
<#assign btnsControlId = htmlId + '-buttons-cntrl'>

<#-- Экземпляры -->
<#assign saveListButtonId = htmlId + '-save-list-button'>
<#assign deleteListButtonId = htmlId + '-delete-list-button'>
<#assign datagridId = htmlId + '-datagrid'>

<#-- SCC -->
<#assign workflowTypeShareConfig = field.control.params.workflowType ? lower_case>
<#assign concurrencyShareConfig = ((field.control.params.concurrency) ! 'user') ? lower_case>
<#assign allowedBusinessRoleId = (field.control.params.allowedBusinessRoleId) ! ''>

<#assign shouldInitAllowed = allowedBusinessRoleId ? has_content>

<#-- Отправка данных -->
<#assign concurrencyInputId = htmlId + '-concurrency-input'>
<#assign concurrencyInputName = field.control.params.concurrencyInputName>
<#assign concurrencyInputInitialValue = (concurrencyShareConfig == 'user') ? string('SEQUENTIAL', concurrencyShareConfig)>

<#assign listNodeRefInput = htmlId + '-workflow-type-input'>

<#-- Состояния -->
<#assign controlItemType = args.itemId>

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
			<input type="hidden" id="${concurrencyInputId}" name="${concurrencyInputName}"
				   value="${concurrencyInputInitialValue}"/>
		</div>
	</div>

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

	<div id='${buttonsContainerId}' style="margin-bottom: 10px;"></div>

	<div class='form-field with-grid'>
	<@grid.datagrid datagridId false />
	</div>
</div>

<script>
/* global Alfresco, LogicECM, YAHOO */
(function() {
	function WorkflowDatagrid(htmlId) {
		WorkflowDatagrid.superclass.constructor.call(this, htmlId);

		this.options.concurrency = 'sequential';

		return this;
	}

	YAHOO.lang.extend(WorkflowDatagrid, LogicECM.module.Base.DataGrid);

	WorkflowDatagrid.prototype.refresh = function(nodeRef) {
		if (typeof nodeRef === 'string') {
			this.options.datagridMeta.nodeRef = nodeRef;
		}

		YAHOO.Bubbling.fire('activeGridChanged', {
			bubblingLabel: this.options.bubblingLabel,
			datagridMeta: this.options.datagridMeta
		});
	};

	WorkflowDatagrid.prototype._moveTo = function(direction, nodeRef) {
		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/changeOrder',
			dataObj: dataObj = {
				assigneeItemNodeRef: nodeRef,
				moveDirection: direction
			},
			successCallback: {
				fn: this.refresh,
				scope: this
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось переместить элемент'
					});
				}
			}
		});
	};

	WorkflowDatagrid.prototype.onMoveUp = function(items) {
		this._moveTo('up', items.nodeRef);
	};

	WorkflowDatagrid.prototype.onMoveDown = function(items) {
		this._moveTo('down', items.nodeRef);
	};

	function WorkflowList(htmlId) {
		WorkflowList.superclass.constructor.call(this, 'LogicECM.module.WorkflowList', htmlId, null);

		this.setOptions({
			allowedBusinessRoleId: '${allowedBusinessRoleId}',
			allowedNodes: [],
			concurrency: '${concurrencyShareConfig}',
			controlItemType: '${controlItemType}',
			currentListRef: null,
			workflowType: '${workflowTypeShareConfig}'
		});
	}

	YAHOO.lang.extend(WorkflowList, Alfresco.component.Base);

	WorkflowList.prototype._initControl = function() {
		var dimmer = YAHOO.util.Dom.get('${dimmerId}');

		this.widgets.currentListRefInput = YAHOO.util.Dom.get('${listNodeRefInput}');

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/getDefaultAssigneesList',
			dataObj: {
				workflowType: this.options.workflowType.toUpperCase(),
				concurrency: this.options.concurrency.toUpperCase()
			},
			successCallback: {
				fn: function(r) {
					debugger;

					this._setCurrentListRef(r.json.defaultList);
					this._setCurrentEmployee(r.json.currentEmployee);

					this._initDatagrid();
					this._updateListsMenu();

					dimmer.style.display = 'none';
				},
				scope: this
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось получить nodeRef на временный список согласования'
					});
				}
			}
		});
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

	WorkflowList.prototype._onSaveListButtonClick = function() {
		var workflowList = this;

		this.widgets.dialogSaveList = new Alfresco.module.SimpleDialog('${htmlId}-form-save-list');

		this.widgets.dialogSaveList.setOptions({
			width: '50em',
			templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
			templateRequestParams: {
				itemKind: 'type',
				itemId: 'lecm-workflow:workflow-assignees-list',
				formId: 'save',
				//destination: '',
				mode: 'create',
				submitType: 'json',
				showCancelButton: 'true'
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function(form, simpleDialog) {
					simpleDialog.dialog.setHeader('Сохранить список как...');
				}
			},
			doBeforeAjaxRequest: {
				fn: function(config, form) {
					var form = workflowList.widgets.dialogSaveList.form;
					var title = form.getFormData()['prop_cm_title'];

					workflowList._saveList(title);

					return false;
				},
				obj: this.widgets.dialogSaveList.form
			},
			onFailure: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось открыть форму сохранения списка'
					});
				},
			}
		});

		this.widgets.dialogSaveList.show();
	};

	WorkflowList.prototype._getIgnoreNodes = function() {
		var i;

		var datagrid = this.widgets.datagrid;
		var dataTable = datagrid.widgets.dataTable;
		var recordSet = dataTable.getRecordSet();
		var recordSetLg = recordSet.getLength();

		var result = [];

		result.push(this.options.currentEmployee);

		if (recordSetLg === 0) {
			return result;
		}

		for (i = 0; i < recordSetLg; i++) {
			result.push(recordSet.getRecord(i).getData('itemData')['assoc_lecm-workflow_assignee-employee-assoc'].value);
		}

		return result;
	};

	WorkflowList.prototype._getAllowedNodes = function() {
		return this.options.allowedNodes;
	};

	WorkflowList.prototype._saveList = function(title) {
		var workflowList = this;

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/save',
			dataObj: {
				nodeRef: workflowList.options.currentListRef,
				title: title
			},
			successCallback: {
				fn: function() {
					workflowList._updateListsMenu(workflowList.options.currentListRef);

					Alfresco.util.PopupManager.displayMessage({
						text: 'Список успешно сохранён'
					});
				}
			},
			failureCallback: {
				fn: function(response) {
					var text;

					Alfresco.util.PopupManager.zIndex++;

					// TERNARY? NO!
					if (response.serverResponse.status === 418) {
						text = 'Не удалось сохранить список: список с таким именем уже существует';
					} else {
						text = 'Не удалось сохранить список из-за внутренней ошибки сервера';
					}

					Alfresco.util.PopupManager.displayMessage({
						text: text
					});
				}
			}
		});
	};

	WorkflowList.prototype._initAllowedNodes = function() {
		var workflowList = this;

		if (this.options.allowedBusinessRoleId.length > 0) { // Trim?
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/orgstructure/ds/getEmployeesByBusinessRoleId',
				dataObj: {
					businessRoleId: this.options.allowedBusinessRoleId,
					withDelegation: true
				},
				successCallback: {
					fn: function(response) {
						workflowList.setOptions({ allowedNodes: response.json.employees });
					}
				},
				failureCallback: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось получить сотрудников бизнес-роли ' + this.options.allowedBusinessRoleId
						});
					}
				}
			});
		}
	};

	/**
	 * Метод создаёт кнопки и привязывает обработчики
	 */
	WorkflowList.prototype._initButtons = function() {
		// Кнопка 'Список'
		this.widgets.btnSelectList = new YAHOO.widget.Button({
			container: '${menuContainerId}',
			type: 'menu',
			menu: [
				{}
			],
			label: 'Выберите список'
		});

		this.widgets.btnSelectList.getMenu().subscribe('click', this._onListsMenuClick, null, this);

		this.widgets.btnSelectList.setStyle('margin-left', '1px');

		// Кнопка 'Создать новый список'
		this.widgets.btnSaveList = new YAHOO.widget.Button('${saveListButtonId}', {
			type: 'push',
			onclick: {
				fn: this._onSaveListButtonClick,
				scope: this
			}
		});

		// Кнопка 'Удалить выбранный список'
		this.widgets.btnDeleteList = new YAHOO.widget.Button('${deleteListButtonId}', {
			type: 'push',
			onclick: {
				fn: this._deleteList,
				scope: this
			}
		});

		// Кнопка 'Добавить сотрудника'
		this.widgets.btnAddAssignee = new YAHOO.widget.Button({
			container: '${buttonsContainerId}',
			type: 'push',
			label: 'Добавить сотрудника',
			onclick: {
				fn: this._onAddAssigneeButtonClick,
				obj: { type: 'assignee' },
				scope: this
			}
		});

	<#--// Кнопка 'Добавить должность'-->
	<#--this.widgets.btnAddPosition = new YAHOO.widget.Button({-->
	<#--container: '${buttonsContainerId}',-->
	<#--type: 'push',-->
	<#--label: 'Добавить должность',-->
	<#--onclick: {-->
	<#--fn: this._onAddAssigneeButtonClick,-->
	<#--obj: { type: 'position' },-->
	<#--scope: this-->
	<#--}-->
	<#--});-->

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

		// Кнопка 'Рассчитать сроки согласования'
		if (this.options.controlItemType !== 'lecm-workflow:route') {
			if (this.options.concurrency === 'sequential') {
				this.widgets.btnComputeTerms = new YAHOO.widget.Button({
					container: '${buttonsContainerId}',
					type: 'push',
					label: 'Рассчитать сроки',
					onclick: {
						fn: this._onComputeTermsButtonClick,
						scope: this
					}
				});
			}
		}
	};

	WorkflowList.prototype._onComputeTermsButtonClick = function() {
		var currentList = this.options.currentListRef;
		var currentDate = this.widgets.calendar.getSelectedDates()[0];

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/setDueDates',
			dataObj: {
				assigneeListNodeRef: currentList,
				workflowDueDate: Alfresco.util.toISO8601(currentDate)
			},
			successCallback: {
				fn: this._refreshDatagrid,
				scope: this
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось рассчитать сроки, попробуйте еще раз'
					});
				}
			}
		});
	};

	WorkflowList.prototype._setConcurrency = function(value) {
		var workflowList = this;
		var datagrid = workflowList.widgets.datagrid;
		var dataTable = datagrid.widgets.dataTable;

		var SEQUENTIAL = { concurrency: 'sequential' };
		var PARALLEL = { concurrency: 'parallel' };

		var btnComputeTerms = this.widgets.btnComputeTerms;

		var input = YAHOO.util.Dom.get('${concurrencyInputId}');

		if (value === 'user' || value === 'sequential') {
			input.value = 'SEQUENTIAL';

			workflowList.setOptions(SEQUENTIAL);
			datagrid.setOptions(SEQUENTIAL);
			dataTable.showColumn(0);
			dataTable.showColumn(1);

			if (btnComputeTerms !== null && btnComputeTerms !== undefined) {
				btnComputeTerms.setStyle('display', '');
			}
		} else {
			input.value = 'PARALLEL';

			workflowList.setOptions(PARALLEL);
			datagrid.setOptions(PARALLEL);
			dataTable.hideColumn(0);
			dataTable.hideColumn(1);

			if (btnComputeTerms !== null && btnComputeTerms !== undefined) {
				btnComputeTerms.setStyle('display', 'none');
			}
		}
	};

	WorkflowList.prototype._onListsMenuClick = function(eventType, args) {
		var text, value;
		var event = args[0];
		var menuItem = args[1];

		if (menuItem === null || menuItem === undefined) {
			return;
		}

		text = menuItem.cfg.getProperty('text');
		value = menuItem.value;

		this.widgets.btnSelectList.set('label', text);

		this._setCurrentListRef(value);
		this._refreshDatagrid();
	};

	WorkflowList.prototype._hookCalendar = function(layer, args) {
		function getYahooDateString(date) {
			return (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
		}

		var zeroDate, todayDate, restrictedRangeString;
		var cm = Alfresco.util.ComponentManager;

		YAHOO.Bubbling.unsubscribe('registerValidationHandler', this._hookCalendar);

		if (args[1].fieldId === '${htmlId}_prop_bpm_workflowDueDate-cntrl-date') {
			zeroDate = new Date(0);
			todayDate = new Date();
			restrictedRangeString = getYahooDateString(zeroDate) + '-' + getYahooDateString(todayDate);

			this.widgets.calendar = cm.get('${htmlId}_prop_bpm_workflowDueDate-cntrl').widgets.calendar;
			this.widgets.calendar.addRenderer(restrictedRangeString, this.widgets.calendar.renderCellStyleHighlight3);

			// Подписываем валидацию на select и deselect, так как в this._initValidation календарь не будет доступен.
			// Подробнее в this._initValidation.
			//this.widgets.calendar.selectEvent.subscribe(this.validateForm, this, true);
			//this.widgets.calendar.deselectEvent.subscribe(this.validateForm, this, true);
		}
	};

	WorkflowList.prototype._setCurrentListRef = function(ref) {
		this.options.currentListRef = ref;
		this.widgets.currentListRefInput.value = ref;
	};

	WorkflowList.prototype._setCurrentEmployee = function(ref) {
		this.options.currentEmployee = ref;
	};

	/**
	 * Удаляет текущий выбранный список, либо переданный
	 */
	WorkflowList.prototype._deleteList = function() {
		var workflowList = this;
		var refToDelete = this.options.currentListRef;

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/deleteList',
			dataObj: {
				nodeRef: refToDelete
			},
			successCallback: {
				fn: function onSuccess(response) {
					var defaultList = response.json.defaultList;

					workflowList._setCurrentListRef(defaultList);
					workflowList._updateListsMenu(defaultList);
					workflowList._refreshDatagrid();
				}
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось удалить список'
					});
				}
			}
		});
	};

	/**
	 * Заполняет меню выбора списка бизнес-процесса
	 */
	WorkflowList.prototype._updateListsMenu = function(refToSelect) {
		var workflowList = this;

		Alfresco.util.Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/getAllLists',
			dataObj: {
				workflowType: workflowList.options.workflowType.toUpperCase(),
				concurrency: workflowList.options.concurrency.toUpperCase()
			},
			successCallback: {
				fn: function(response) {
					var i, title, items;
					var lists = response.json.lists;
					var listsLg = lists.length;

					var btnSelectList = workflowList.widgets.btnSelectList;
					var menu = btnSelectList.getMenu();

					menu.clearContent();

					if (refToSelect === null || refToSelect === undefined) {
						refToSelect = response.json.defaultList;
					}

					for (i = 0; i < listsLg; i++) {
						// TERNARY? NO!
						if (lists[i].nodeRef === response.json.defaultList) {
							title = 'Список по умолчанию';
						} else {
							title = lists[i].title;
						}

						menu.addItem({
							text: title,
							value: lists[i].nodeRef
						});

						items = menu.getItems();

						if (refToSelect === lists[i].nodeRef) {
							btnSelectList.set('label', items[items.length - 1].cfg.getProperty('text'));
						}
					}

					menu.render('${menuContainerId}');
				}
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не удалось получить перечень списков'
					});
				}
			}
		});
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
						return this.options.concurrency.toLowerCase() === 'sequential';
					}
				},
				{
					type: 'datagrid-action-link-${datagridId}-label',
					id: 'onMoveDown',
					permission: 'edit',
					label: 'Переместить вниз',
					evaluator: function() {
						return this.options.concurrency.toLowerCase() === 'sequential';
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
				datagridFormId: this._getDatagridFormId(),
				itemType: 'lecm-workflow:assignee',
				nodeRef: this.options.currentListRef,
				sort: 'lecm-workflow:assignee-order|true',
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			}
		});

		this.widgets.datagrid.draw();
	};

	WorkflowList.prototype._getDatagridFormId = function() {
		var controlItemType = this.options.controlItemType;

		if (controlItemType === 'lecm-workflow:route') {
			return 'datagrid-route';
		}

		return 'datagrid';
	};

	WorkflowList.prototype._getFormId = function() {
		var controlItemType = this.options.controlItemType;

		if (controlItemType === 'lecm-workflow:route') {
			return 'route';
		}

		return this.options.concurrency;
	};

	/**
	 * Обработчик клика по кнопке 'Добавить сотрудника' и 'Добавить должность'
	 * Показывает форму создания типа, который отображает датагрид. Destination для формы тот же, что у датагрида.
	 */
	WorkflowList.prototype._onAddAssigneeButtonClick = function(options) {
		var ignoreNodesArray = this._getIgnoreNodes();
		var ignoreNodesString = ignoreNodesArray.join();

		var allowedNodesArray = this._getAllowedNodes();
		var allowedNodesString = allowedNodesArray.join();

		this.widgets.formAddAssignee = new Alfresco.module.SimpleDialog('${htmlId}-form-add-assignee');

		this.widgets.formAddAssignee.setOptions({
			width: '50em',
			templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
			templateRequestParams: {
				itemKind: 'type',
				itemId: 'lecm-workflow:assignee',
				destination: this.options.currentListRef,
				formId: this._getFormId(),
				mode: 'create',
				submitType: 'json',
				showCancelButton: 'true',
				ignoreNodes: ignoreNodesString,
				allowedNodes: allowedNodesArray
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
		this._initAllowedNodes();
		this._initControl();

		YAHOO.Bubbling.on('registerValidationHandler', this._hookCalendar, this);
	};

	var workflowList = new WorkflowList('${containerId}');

	Alfresco.util.PopupManager.zIndex += 100;

})();
</script>