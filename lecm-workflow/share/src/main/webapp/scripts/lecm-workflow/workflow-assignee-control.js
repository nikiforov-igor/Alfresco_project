/* global Alfresco, LogicECM, YAHOO */
if (typeof LogicECM === 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Workflow = LogicECM.module.Workflow || {};

(function() {
	LogicECM.module.Workflow.WorkflowDatagrid = function(htmlId, namespaceId) {
		LogicECM.module.Workflow.WorkflowDatagrid.superclass.constructor.call(this, htmlId);

		this.setOptions({
			bubblingLabel: namespaceId,
			concurrency: 'sequential'
		});

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Workflow.WorkflowDatagrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Workflow.WorkflowDatagrid.prototype, {
		refresh: function(nodeRef) {
			if (typeof nodeRef === 'string') {
				this.options.datagridMeta.nodeRef = nodeRef;
			}

			YAHOO.Bubbling.fire('activeGridChanged', {
				bubblingLabel: this.options.bubblingLabel,
				datagridMeta: this.options.datagridMeta
			});
		},
		getCustomCellFormatter: function(grid, elCell, oRecord, oColumn, oData) {
			// Если у нас нет списка разрешённых согласующих, то ничего не делаем
			if (!grid.options.allowedNodes || grid.options.allowedNodes.length === 0) {
				return null;
			}

			var ASSOC_EMPLOYEE = 'assoc_lecm-workflow_assignee-employee-assoc';
			var allowed = grid.options.allowedNodes;
			var currentEmployeeRef = oRecord.getData('itemData')[ASSOC_EMPLOYEE].value;

			if (allowed.indexOf(currentEmployeeRef) < 0) {
				// Если текущий сотрудник не входит в бизнес-роль
				elCell.parentElement.parentElement.style.backgroundColor = '#f99';
			} else {
				// Если текущий сотрудник входит в бизнес-роль
				elCell.parentElement.parentElement.style.backgroundColor = '';
			}

			// Если getCustomCellFormatter возвращает null, то datagrid добавляет свои правила отрисовки
			return null;
		},
		_moveTo: function(direction, nodeRef) {
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
		},
		onMoveUp: function(items) {
			this._moveTo('up', items.nodeRef);
		},
		onMoveDown: function(items) {
			this._moveTo('down', items.nodeRef);
		}

	}, true);


	LogicECM.module.Workflow.WorkflowList = function(initOptions) {

		LogicECM.module.Workflow.WorkflowList.superclass.constructor.call(this, 'LogicECM.module.Workflow.WorkflowList', initOptions.containerId, null);

		YAHOO.lang.augmentObject(initOptions, {
			allowedNodes: [],
			currentListRef: null
		});

		this.setOptions(initOptions);

		YAHOO.Bubbling.on('datagridVisible', this._hackTheRecordSet, this);
		YAHOO.Bubbling.on('datagridVisible', this._initAllowedNodes, this);
		YAHOO.Bubbling.on('GridRendered', this._setInitialConcurrency, this);
		YAHOO.Bubbling.on('GridRendered', this._signalDatagridReady, this);
		if (this.options.isRoute) {
			YAHOO.Bubbling.on('GridRendered', this._calculateDayToComplete, this);
		}

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Workflow.WorkflowList, Alfresco.component.Base);

	YAHOO.lang.augmentObject(LogicECM.module.Workflow.WorkflowList.prototype, {
		initialConcurrencySetted: false,
		calendarHacked: false,
		destructorHacked: false,
		validationHacked: false,
		_initControl: function() {
			var dimmer = YAHOO.util.Dom.get(this.options.dimmerId);

			var dataObj = {
				workflowType: this.options.workflowType.toUpperCase(),
				concurrency: this.options.concurrency.toUpperCase()
			};

			if (this.options.isRoute) {
				dataObj.routeRef = this.options.routeRef;
				this.widgets.daysToCompleteField = YAHOO.util.Dom.get(this.options.daysToCompleteFieldId);
				YAHOO.util.Event.on(this.widgets.daysToCompleteField, "change", this._onDaysToCompleteChange, this, true);
			}

			this.widgets.currentListRefInput = YAHOO.util.Dom.get(this.options.listNodeRefInput);
			this.widgets.form = Alfresco.util.ComponentManager.get(this.options.formId);
			this.widgets.simpleDialog = Alfresco.util.ComponentManager.get(this.options.htmlId);

			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/getDefaultAssigneesList',
				dataObj: dataObj,
				successCallback: {
					fn: function(r) {
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
		},
		_refreshDatagrid: function() {
			var datagrid = this.widgets.datagrid;
			var nodeRef = this.options.currentListRef;

			if (datagrid !== null || datagrid !== undefined) {
				if (nodeRef !== null || nodeRef !== undefined) {
					datagrid.refresh(nodeRef);
				}
			}
		},
		_onConcurrencyChange: function(event) {
			var newConcurrencyValue = event.newValue;
			this._setConcurrency(newConcurrencyValue, true);
			if (this.options.isRoute) {
				this._onDaysToCompleteChange();
			}
			this.validateForm();
		},
		_onSaveListButtonClick: function() {
			var workflowList = this;

			this.widgets.dialogSaveList = new Alfresco.module.SimpleDialog(this.options.namespaceId + '-form-save-list');

			this.widgets.dialogSaveList.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: {
					itemKind: 'type',
					itemId: 'lecm-workflow:workflow-assignees-list',
					formId: 'save',
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
					}
				}
			});

			this.widgets.dialogSaveList.show();
		},
		_getIgnoreNodes: function() {
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
		},
		_getAllowedNodes: function() {
			return this.options.allowedNodes;
		},
		_saveList: function(title) {
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
		},
		_initAllowedNodes: function() {
			var workflowList = this;

			YAHOO.Bubbling.unsubscribe('datagridVisible', this._initAllowedNodes, this);

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
							workflowList.options.allowedNodes = response.json.employees;
							workflowList.widgets.datagrid.options.allowedNodes = response.json.employees;
							workflowList.widgets.datagrid.refresh();
						}
					},
					failureCallback: {
						fn: function() {
							Alfresco.util.PopupManager.displayMessage({
								text: 'Не удалось получить сотрудников бизнес-роли ' + workflowList.options.allowedBusinessRoleId
							});
						}
					}
				});
			}
		},
		/**
		 * Метод создаёт кнопки и привязывает обработчики
		 */
		_initButtons: function() {
			if (this.options.showListSelectMenu) {
				// Кнопка 'Выберите список'
				this.widgets.btnSelectList = new YAHOO.widget.Button({
					container: this.options.menuContainerId,
					type: 'menu',
					menu: [
						{}
					],
					label: 'Выберите список'
				});

				this.widgets.btnSelectList.getMenu().subscribe('click', this._onListsMenuClick, null, this);
				this.widgets.btnSelectList.setStyle('margin-left', '1px');
			}

			// Кнопка 'Создать новый список'
			this.widgets.btnSaveList = new YAHOO.widget.Button(this.options.saveListButtonId, {
				type: 'push',
				onclick: {
					fn: this._onSaveListButtonClick,
					scope: this
				}
			});

			// Кнопка 'Удалить выбранный список'
			this.widgets.btnDeleteList = new YAHOO.widget.Button(this.options.deleteListButtonId, {
				type: 'push',
				onclick: {
					fn: this._deleteList,
					scope: this
				}
			});

			// Кнопка 'Добавить сотрудника'
			this.widgets.btnAddAssignee = new YAHOO.widget.Button({
				container: this.options.buttonsContainerId,
				type: 'push',
				label: 'Добавить сотрудника',
				onclick: {
					fn: this._onAddAssigneeButtonClick,
					obj: {type: 'assignee'},
					scope: this
				}
			});

			// Кнопка 'Добавить должность'
//		this.widgets.btnAddPosition = new YAHOO.widget.Button({
//			container: this.options.buttonsContainerId,
//			type: 'push',
//			label: 'Добавить должность',
//			onclick: {
//				fn: this._onAddAssigneeButtonClick,
//				obj: { type: 'position' },
//				scope: this
//			}
//		});

			// Радио-кнопки для типа бизнес-процесса
			if (this.options.concurrency === 'user') {
				this.widgets.radioWorkflowType = new YAHOO.widget.ButtonGroup({
					id: 'workflow-type-radio-buttons',
					name: 'workflow-type-radio-buttons',
					container: this.options.radioWorkflowTypeId
				});
				this.widgets.radioWorkflowType.subscribe('valueChange', this._onConcurrencyChange, null, this);
				this.widgets.radioWorkflowType.addButtons([
					{label: 'Последовательное', value: 'sequential', checked: true},
					{label: 'Параллельное', value: 'parallel'}
				]);
				this.widgets.radioWorkflowType.getButton(0).setStyle('margin-left', '1px');

				this.options.concurrency = 'sequential';
			}

			if (this.options.showComputeTermsButton) {
				this.widgets.btnComputeTerms = new YAHOO.widget.Button({
					container: this.options.buttonsContainerId,
					type: 'push',
					label: 'Рассчитать сроки',
					onclick: {
						fn: this._onComputeTermsButtonClick,
						scope: this
					}
				});
			}
		},
		_onComputeTermsButtonClick: function() {
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
		},
		_setConcurrency: function(value, needsPersisting) {
			var workflowList = this, currentConcurrency, buttonToCheck;
			var datagrid = workflowList.widgets.datagrid;
			var dataTable = datagrid.widgets.dataTable;

			var SEQUENTIAL = {concurrency: 'sequential'};
			var PARALLEL = {concurrency: 'parallel'};

			var btnComputeTerms = this.widgets.btnComputeTerms;

			var input = YAHOO.util.Dom.get(this.options.concurrencyInputId);

			if (value === 'user' || value === 'sequential') {
				currentConcurrency = SEQUENTIAL;

				buttonToCheck = 0;

				dataTable.showColumn(0);
				dataTable.showColumn(1);

				if (btnComputeTerms !== null && btnComputeTerms !== undefined) {
					btnComputeTerms.setStyle('display', '');
				}
			} else {
				currentConcurrency = PARALLEL;

				buttonToCheck = 1;

				dataTable.hideColumn(0);
				dataTable.hideColumn(1);

				if (btnComputeTerms !== null && btnComputeTerms !== undefined) {
					btnComputeTerms.setStyle('display', 'none');
				}
			}

			workflowList.setOptions(currentConcurrency);
			datagrid.setOptions(currentConcurrency);

			this.options.concurrency = currentConcurrency.concurrency;

			if (this.widgets.radioWorkflowType !== null && this.widgets.radioWorkflowType !== undefined) {
				this.widgets.radioWorkflowType.check(buttonToCheck);
			}

			if (input !== null && input !== undefined) {
				input.value = currentConcurrency.concurrency.toUpperCase();
			}
			if (needsPersisting) {
				this._persistConcurrency(currentConcurrency.concurrency.toUpperCase());
			}

			if (this.options.isRoute) {
				this._switchDaysToCompleteFieldRO();
			}
		},
		_setInitialConcurrency: function() {
			if (!this.initialConcurrencySetted && this.widgets.datagrid && this.widgets.datagrid.widgets.dataTable) {
				this._setConcurrency(this.options.concurrency, true);
				this.initialConcurrencySetted = true;
			}
		},
		_persistConcurrency: function(concurrencyStr) {
			var listRef = this.options.currentListRef;
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/setAssigneesListConcurrency',
				dataObj: {
					nodeRef: listRef,
					concurrency: concurrencyStr
				},
				failureCallback: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось сохранить тип списка'
						});
					}
				}
			});
		},
		_switchDaysToCompleteFieldRO: function() {
			if (this.options.concurrency === 'parallel') {
				this.widgets.daysToCompleteField.readOnly = false;
				// раскомментируй, если надо очищать поле при переключении
//				this.widgets.daysToCompleteField.value = "";
			} else {
				this.widgets.daysToCompleteField.readOnly = true;
				this._calculateDayToComplete();
			}
		},
		_calculateDayToComplete: function() {
			if (!this.widgets.datagrid || !this.widgets.datagrid.widgets.dataTable) {
				return;
			}

			var tableRows = this.widgets.datagrid.widgets.dataTable.getRecordSet().getRecords(),
					propDaysToComplete = "prop_lecm-workflow_assignee-days-to-complete",
					i, tableRow, value, daysToComplete = 0;

			// Перебираем все строки датагрида
			for (var i = 0; i < tableRows.length; i++) {
				tableRow = tableRows[i].getData("itemData");
				value = tableRow[propDaysToComplete].value;
				daysToComplete += value;
			}
			this.widgets.daysToCompleteField.value = daysToComplete;

		},
		_onDaysToCompleteChange: function() {
			var daysToCompleteValue = this.widgets.daysToCompleteField.value,
					listRef = this.options.currentListRef;

			if (isNaN(daysToCompleteValue) || daysToCompleteValue < 0) {
				return;
			}

			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/setDaysToComplete',
				dataObj: {
					nodeRef: listRef,
					daysToComplete: daysToCompleteValue
				},
				failureCallback: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось сохранить значение срока'
						});
					}
				}
			});
		},
		_onListsMenuClick: function(eventType, args) {
			var text, value, concurrency;
			var event = args[0];
			var menuItem = args[1];

			if (menuItem === null || menuItem === undefined) {
				return;
			}

			text = menuItem.cfg.getProperty('text');
			concurrency = menuItem.concurrency;
			value = menuItem.value;

			this.widgets.btnSelectList.set('label', text);

			this._setCurrentListRef(value, concurrency);
			this._refreshDatagrid();
		},
		_hackTheDestructor: function(layer, args) {
			if (this.destructorHacked) {
				return;
			}

			this.destructorHacked = true;

			if (this.widgets.simpleDialog) {
				this.widgets.simpleDialog.dialog.unsubscribeAll('destroy');
				this.widgets.simpleDialog.dialog.subscribe('destroy', this._destructor, null, this);
			}
		},
		/**
		 * Метод будет вызван столько раз, сколько раз контрол будет определён на форме. TODO...
		 */
		_hackTheCalendar: function() {
			function getYahooDateString(date) {
				return (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();
			}

			if (this.calendarHacked) {
				return;
			}

			var zeroDate, todayDate, restrictedRangeString;
			var cm = Alfresco.util.ComponentManager;

			var datePickers = cm.find({name: 'LogicECM.DatePicker'});
			var datePickersLength = datePickers.length;

			for (var i = 0; i < datePickersLength; i++) {
				var currentDatePicker = datePickers[i];
				if (currentDatePicker.id === this.options.dueDateId + '-cntrl') {
					this.calendarHacked = true;

					zeroDate = new Date(0);
					todayDate = new Date();
					restrictedRangeString = getYahooDateString(zeroDate) + '-' + getYahooDateString(todayDate);

					this.widgets.calendar = currentDatePicker.widgets.calendar;
					this.widgets.calendar.addRenderer(restrictedRangeString, this.widgets.calendar.renderCellStyleHighlight3);

					this.widgets.calendar.selectEvent.subscribe(this.validateForm, this, true);
					this.widgets.calendar.deselectEvent.subscribe(this.validateForm, this, true);

					this.widgets.calendar.render();
				}
			}
		},
		_hackTheValidation: function() {
			function isDueDateValidator(vldtn) {
				return vldtn.handler === LogicECM.module.Workflow.workflowDueDateValidator;
			}

			if (this.validationHacked) {
				return;
			}

			var formsRuntime = this.widgets.form.formsRuntime;
			var validations = formsRuntime.validations;

			if (formsRuntime.formId === this.options.formId) {
				this.validationHacked = true;

				// На afterFormRuntimeInit подписывается каждый экземпляр WorkflowList, и каждый пытается inject-ить этот
				// валидатор. Чтобы не создавать копии одного и тоже валидатора, inject-им только при отсутствии.
				if (!validations.some(isDueDateValidator)) {
					formsRuntime.addValidation(this.options.dueDateId, // fieldId
							LogicECM.module.Workflow.workflowDueDateValidator, // validationHandler
							null, // validationArgs
							'change', // when
							null // message
							);
				}

				// Каждый экземпляр WorkflowList добавляет свой валидатор в коллекцию FormsRuntime, передавая себя (this)
				// через validationArgs. Это позволит валидировать именно ту таблицу, которая относится к текущему
				// экземпляру.
				formsRuntime.addValidation(this.options.listNodeRefInput, LogicECM.module.Workflow.workflowListValidator, {
					workflowListControl: this
				}, 'change', null);
			}
		},
		_hackTheRecordSet: function(layer, args) {
			var rs;

			var argsDg = args[1];
			var thisDg = this.widgets.datagrid;

			if (thisDg === null || thisDg === undefined) {
				return;
			}

			if (thisDg.options.bubblingLabel === argsDg.options.bubblingLabel) {
				YAHOO.Bubbling.unsubscribe('datagridVisible', this._hackTheRecordSet);

				rs = this.widgets.datagrid.widgets.dataTable.getRecordSet();

				rs.unsubscribe('recordAddEvent', this.validateForm, this, true);
				rs.unsubscribe('recordUpdateEvent', this.validateForm, this, true);
				rs.unsubscribe('recordSetEvent', this.validateForm, this, true);
				rs.unsubscribe('recordsAddEvent', this.validateForm, this, true);
				rs.unsubscribe('recordsSetEvent', this.validateForm, this, true);
				rs.unsubscribe('recordDeleteEvent', this.validateForm, this, true);
				rs.unsubscribe('recordsDeleteEvent', this.validateForm, this, true);

				rs.subscribe('recordAddEvent', this.validateForm, this, true);
				rs.subscribe('recordUpdateEvent', this.validateForm, this, true);
				rs.subscribe('recordSetEvent', this.validateForm, this, true);
				rs.subscribe('recordsAddEvent', this.validateForm, this, true);
				rs.subscribe('recordsSetEvent', this.validateForm, this, true);
				rs.subscribe('recordDeleteEvent', this.validateForm, this, true);
				rs.subscribe('recordsDeleteEvent', this.validateForm, this, true);
			}
		},
		validateForm: function() {
			this.widgets.form.formsRuntime.updateSubmitElements();
		},
		_setCurrentListRef: function(ref, concurrency) {
			this.options.currentListRef = ref;
			this.widgets.currentListRefInput.value = ref;
			if (concurrency) {
				this._setConcurrency(concurrency, false);
			}
		},
		_setCurrentEmployee: function(ref) {
			this.options.currentEmployee = ref;
		},
		/**
		 * Удаляет текущий выбранный список, либо переданный
		 */
		_deleteList: function() {
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
		},
		/**
		 * Заполняет меню выбора списка бизнес-процесса
		 * @argument {string} refToSelect NodeRef на список участников, который должен быть выбран
		 */
		_updateListsMenu: function(refToSelect) {
			var workflowList = this;

			if (this.widgets.btnSelectList === null || this.widgets.btnSelectList === undefined) {
				return;
			}

			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/getAllLists',
				dataObj: {
					workflowType: workflowList.options.workflowType.toUpperCase(),
					concurrency: workflowList.options.concurrency.toUpperCase()
				},
				successCallback: {
					fn: function(response) {
						var i, title, items, currentList, addedMenuItem;
						var lists = response.json.lists;
						var listsLg = lists.length;

						var btnSelectList = workflowList.widgets.btnSelectList;
						var menu = btnSelectList.getMenu();

						menu.clearContent();

						if (refToSelect === null || refToSelect === undefined) {
							refToSelect = response.json.defaultList;
						}

						for (i = 0; i < listsLg; i++) {
							currentList = lists[i];
							// TERNARY? NO!
							if (currentList.nodeRef === response.json.defaultList) {
								title = 'Список по умолчанию';
							} else {
								title = currentList.title;
							}

							addedMenuItem = menu.addItem({
								text: title,
								value: currentList.nodeRef
							});

							addedMenuItem.concurrency = currentList.concurrency ? currentList.concurrency.toLowerCase() : null;

							items = menu.getItems();

							if (refToSelect === currentList.nodeRef) {
								btnSelectList.set('label', items[items.length - 1].cfg.getProperty('text'));
							}
						}

						menu.render(workflowList.options.menuContainerId);
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
		},
		/**
		 * Метод создаёт датагрид
		 */
		_initDatagrid: function() {

			this.widgets.datagrid = new LogicECM.module.Workflow.WorkflowDatagrid(this.options.datagridId, this.options.namespaceId);

			this.widgets.datagrid.setOptions({
				concurrency: this.options.concurrency,
				bubblingLabel: this.options.datagridId + '-label',
				usePagination: false,
				showExtendSearchBlock: false,
				showCheckboxColumn: false,
				searchShowInactive: false,
				forceSubscribing: true,
				showActionColumn: true,
				overrideSortingWith: false,
				actions: [{
						type: 'datagrid-action-link-' + this.options.datagridId + '-label',
						id: 'onMoveUp',
						permission: 'edit',
						label: 'Переместить вверх',
						evaluator: function() {
							return this.options.concurrency.toLowerCase() === 'sequential';
						}
					}, {
						type: 'datagrid-action-link-' + this.options.datagridId + '-label',
						id: 'onMoveDown',
						permission: 'edit',
						label: 'Переместить вниз',
						evaluator: function() {
							return this.options.concurrency.toLowerCase() === 'sequential';
						}
					}, {
						type: 'datagrid-action-link-' + this.options.datagridId + '-label',
						id: 'onActionDelete',
						permission: 'delete',
						label: 'Удалить'
					}],
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
		},
		_getDatagridFormId: function() {
			var formItemType = this.options.formItemType;

			if (formItemType === 'route') {
				return 'datagrid-route';
			}

			return 'datagrid';
		},
		_getFormId: function() {
			var formItemType = this.options.formItemType;

			if (formItemType === 'route') {
				return 'route';
			}

			return this.options.concurrency;
		},
		/**
		 * Обработчик клика по кнопке 'Добавить сотрудника' и 'Добавить должность'
		 * Показывает форму создания типа, который отображает датагрид. Destination для формы тот же, что у датагрида.
		 */
		_onAddAssigneeButtonClick: function() {
			var ignoreNodesArray = this._getIgnoreNodes();
			var ignoreNodesString = ignoreNodesArray.join();

			var allowedNodesArray = this._getAllowedNodes();
			var allowedNodesString = allowedNodesArray.join();

			this.widgets.formAddAssignee = new Alfresco.module.SimpleDialog(this.options.namespaceId + '-form-add-assignee');

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
					}
				}
			});

			this.widgets.formAddAssignee.show();
		},

		_signalDatagridReady: function(event, args) {
			YAHOO.Bubbling.unsubscribe('GridRendered', this._signalDatagridReady, this);
			YAHOO.Bubbling.fire('assigneesListDatagridReady', {
				bubblingLabel: this.options.datagridId
			});
		},

		_destructor: function() {
			function removeAllBubbles(obj) {
				var event;
				var bubble = YAHOO.Bubbling.bubble;

				for (event in bubble) {
					if (bubble.hasOwnProperty(event)) {
						bubble[event].subscribers.forEach(function(s) {
							if (s.obj === obj) {
								YAHOO.Bubbling.unsubscribe(event, s.fn, s.obj);
							}
						});
					}
				}
			}

			var k, w;

			var isFn = YAHOO.lang.isFunction;
			var isVl = YAHOO.lang.isValue;
			var Bubb = YAHOO.Bubbling;

			var comMan = Alfresco.util.ComponentManager;
			var components = comMan.list();

			var form = comMan.get(this.options.formId);
			var formIndex = components.indexOf(form); // IE9+

			var widgets = this.widgets;
			var datagrid = widgets.datagrid;
			var rs = datagrid.widgets.dataTable.getRecordSet();

			if (this.widges.simpleDialog) {
				this.widgets.simpleDialog.dialog.unsubscribe('destroy', this._destructor);
			}

			Bubb.unsubscribe('activeGridChanged', datagrid.onGridTypeChanged, datagrid);
			Bubb.unsubscribe('dataItemCreated', datagrid.onDataItemCreated, datagrid);
			Bubb.unsubscribe('dataItemUpdated', datagrid.onDataItemUpdated, datagrid);
			Bubb.unsubscribe('dataItemsDeleted', datagrid.onDataItemsDeleted, datagrid);
			Bubb.unsubscribe('datagridRefresh', datagrid.onDataGridRefresh, datagrid);
			Bubb.unsubscribe('archiveCheckBoxClicked', datagrid.onArchiveCheckBoxClicked, datagrid);
			Bubb.unsubscribe('changeFilter', datagrid.onFilterChanged, datagrid);

			rs.unsubscribeAll();

			if (isVl(this.widgets.calendar)) {
				this.widgets.calendar.selectEvent.unsubscribeAll();
				this.widgets.calendar.deselectEvent.unsubscribeAll();
			}

			for (k in this.widgets) {
				if (widgets.hasOwnProperty(k)) {
					w = widgets[k];

					if (w.hasOwnProperty('nodeName') && w.hasOwnProperty('tagName')) {
						$(w).remove();
						continue;
					}

					if (isFn(w.get)) {
						if (w.get('element') === null) {
							continue;
						}
					}

					if (isFn(w.destroy)) {
						w.destroy();
					}
				}
			}

			while (components.length > formIndex) { // Не оптимизируй...
				removeAllBubbles(components[formIndex]);
				comMan.unregister(components[formIndex]);
			}
		},
		/**
		 * Метод onReady вызывается внутренними механизмами Alfreso и YUI, когда все модули, от которых зависит данный
		 * модуль будут загружены и готовы. Так как зависимостей у этого модуля нет, этот метод будет вызван 'на месте'.
		 */
		onReady: function() {
			this._initButtons();
			this._initControl();
			this._hackTheDestructor();
			this._hackTheCalendar();
			this._hackTheValidation();
		}
	}, true);
})();
