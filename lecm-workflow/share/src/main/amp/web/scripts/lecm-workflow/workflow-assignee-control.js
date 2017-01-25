/* global Alfresco, LogicECM, YAHOO */
if (typeof LogicECM === 'undefined' || !LogicECM) {
	LogicECM = {};
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
			var html = "", i, ii, datalistColumn, data;
			// Если у нас нет списка разрешённых согласующих, то ничего не делаем
			if (grid.options.allowedNodes && grid.options.allowedNodes.length !== 0) {
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
			}

			if (oRecord && oColumn) {
				if (!oData) {
					oData = oRecord.getData("itemData")[oColumn.field];
				}

				if (oData) {
					datalistColumn = grid.datagridColumns[oColumn.key];
					if (datalistColumn) {
						oData = YAHOO.lang.isArray(oData) ? oData : [oData];
						for (i = 0, ii = oData.length, data; i < ii; i++) {
							data = oData[i];

							switch (datalistColumn.name) { //  меняем отрисовку для конкретных колонок
								case "lecm-workflow:assignee-due-date":
									html = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), "dd.mm.yyyy");
									break;
								default:
									break;
							}
						}
					}
				}
			}
			return html ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
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
							text: Alfresco.util.message('label.move.item.fail')
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
		},
		onActionDelete: function(p_items, owner, actionsConfig, fnDeleteComplete) {
			this.onDelete(p_items, owner, actionsConfig, this.refresh, null);
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
						this.options.concurrency = r.json.concurrency.toLowerCase();
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
							text: Alfresco.util.message('lable.get.temp.approve.list.noderef.fail')
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
					showCancelButton: 'true',
					showCaption: false
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader(Alfresco.util.message('title.save.list.as'));
					}
				},
				doBeforeAjaxRequest: {
					fn: function(config, form) {
						var form = workflowList.widgets.dialogSaveList.form;
						var title = form.getFormData()['prop_cm_title'];

						workflowList._saveList(title);

						workflowList.widgets.dialogSaveList.hide();

						return false;
					},
					obj: this.widgets.dialogSaveList.form
				},
				onFailure: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('message.open.save.list.form.fail')
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
							text: Alfresco.util.message('message.list.saved.success')
						});
					}
				},
				failureCallback: {
					fn: function(response) {
						var text;

						Alfresco.util.PopupManager.zIndex++;

						// TERNARY? NO!
						if (response.serverResponse.status === 418) {
							text = Alfresco.util.message('message.same-name.list.saved.fail');
						} else {
							text = Alfresco.util.message('message.save.list.server.error');
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
								text: Alfresco.util.message('message.get.employees.by.role.fail') + ' ' + workflowList.options.allowedBusinessRoleId
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
					label: Alfresco.util.message('label.select.list')
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
				label: Alfresco.util.message('label.add.employee'),
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
					{label: Alfresco.util.message('label.sequential'), value: 'sequential', checked: true},
					{label: Alfresco.util.message('label.parallel'), value: 'parallel'}
				]);
				this.widgets.radioWorkflowType.getButton(0).setStyle('margin-left', '1px');

				this.options.concurrency = 'sequential';
			}

			if (this.options.showComputeTermsButton) {
				this.widgets.btnComputeTerms = new YAHOO.widget.Button({
					container: this.options.buttonsContainerId,
					type: 'push',
					label: Alfresco.util.message('label.calculate.terms'),
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
							text: Alfresco.util.message('message.calculate.dates.fail')
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

			if (input && currentConcurrency.concurrency.toUpperCase() !== input.value) {
				if (this.widgets.radioWorkflowType) {
					this.widgets.radioWorkflowType.unsubscribe('valueChange', this._onConcurrencyChange);
					this.widgets.radioWorkflowType.check(buttonToCheck);
					this.widgets.radioWorkflowType.subscribe('valueChange', this._onConcurrencyChange, null, this);
				}
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
				this._setConcurrency(this.options.concurrency, false);
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
							text: Alfresco.util.message('message.save.list.type.fail')
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
			if (this.options.concurrency === 'parallel' || !this.widgets.datagrid || !this.widgets.datagrid.widgets.dataTable) {
				return;
			}

			var tableRows = this.widgets.datagrid.widgets.dataTable.getRecordSet().getRecords(),
				propDaysToComplete = "prop_lecm-workflow_assignee-days-to-complete",
				i, tableRow, daysToComplete = 0;

			// Перебираем все строки датагрида
			for (var i = 0; i < tableRows.length; i++) {
				tableRow = tableRows[i].getData("itemData");
				if (tableRow[propDaysToComplete]) {
					daysToComplete += tableRow[propDaysToComplete].value;
				}
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
							text: Alfresco.util.message('message.save.term.value.fail')
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
				this.widgets.simpleDialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {
					moduleId: this.options.htmlId,
					callback: function() {
						var isVl = YAHOO.lang.isValue;
						var Bubb = YAHOO.Bubbling;
						var datagrid = this.widgets.datagrid;
						var rs = datagrid.widgets.dataTable.getRecordSet();

						Bubb.unsubscribe('activeGridChanged', datagrid.onGridTypeChanged, datagrid);
						Bubb.unsubscribe('dataItemCreated', datagrid.onDataItemCreated, datagrid);
						Bubb.unsubscribe('dataItemUpdated', datagrid.onDataItemUpdated, datagrid);
						Bubb.unsubscribe('dataItemsDeleted', datagrid.onDataItemsDeleted, datagrid);
						Bubb.unsubscribe('datagridRefresh', datagrid.onDataGridRefresh, datagrid);
						Bubb.unsubscribe('archiveCheckBoxClicked', datagrid.onArchiveCheckBoxClicked, datagrid);
						rs.unsubscribeAll();

						if (isVl(this.widgets.calendar)) {
							this.widgets.calendar.selectEvent.unsubscribeAll();
							this.widgets.calendar.deselectEvent.unsubscribeAll();
						}
					}
				}, this);
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

			function isworkflowListValidator(vldtn) {
				return vldtn.handler === LogicECM.module.Workflow.workflowListValidator;
			}

			if (this.validationHacked) {
				return;
			}

			var formsRuntime = this.widgets.form.formsRuntime;
            if (formsRuntime != null) {
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
                            null); // message
                    }

                    // Каждый экземпляр WorkflowList добавляет свой валидатор в коллекцию FormsRuntime, передавая себя (this)
                    // через validationArgs. Это позволит валидировать именно ту таблицу, которая относится к текущему
                    // экземпляру.
                    if (!validations.some(isworkflowListValidator)) {
                        formsRuntime.addValidation(this.options.listNodeRefInput,
                            LogicECM.module.Workflow.workflowListValidator,
                            {workflowListControl: this},
                            'change',
                            null);
                    }
                }
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
							text: Alfresco.util.message('message.remove.list.fail')
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
								title = Alfresco.util.message('title.default.list');
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
							text: Alfresco.util.message('message.get.list.of.lists.fail')
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
						label: Alfresco.util.message('message.move.up'),
						evaluator: function() {
							return this.options.concurrency.toLowerCase() === 'sequential';
						}
					}, {
						type: 'datagrid-action-link-' + this.options.datagridId + '-label',
						id: 'onMoveDown',
						permission: 'edit',
						label: Alfresco.util.message('message.move.down'),
						evaluator: function() {
							return this.options.concurrency.toLowerCase() === 'sequential';
						}
					}, {
						type: 'datagrid-action-link-' + this.options.datagridId + '-label',
						id: 'onActionDelete',
						permission: 'delete',
						label: Alfresco.util.message('label.remove')
					}],
				datagridMeta: {
					useFilterByOrg: false,
					datagridFormId: this._getDatagridFormId(),
					itemType: 'lecm-workflow:assignee',
					useChildQuery: true,
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
				return 'route-' + this.options.concurrency;
			}

			return this.options.concurrency;
		},
		/**
		 * Обработчик клика по кнопке 'Добавить сотрудника' и 'Добавить должность'
		 * Показывает форму создания типа, который отображает датагрид. Destination для формы тот же, что у датагрида.
		 */
		_onAddAssigneeButtonClick: function() {
			var ignoreNodesString = this._getIgnoreNodes().join();
			var allowedNodesString = this._getAllowedNodes().join();
			var templateRequestParams = {
				itemKind: 'type',
				itemId: 'lecm-workflow:assignee',
				destination: this.options.currentListRef,
				formId: this._getFormId(),
				mode: 'create',
				submitType: 'json',
				showCancelButton: 'true',
				showCaption: false
			};
			if (ignoreNodesString) {
				templateRequestParams.ignoreNodes = ignoreNodesString;
			}
			if (allowedNodesString) {
				templateRequestParams.allowedNodes = allowedNodesString;
			}

			this.widgets.formAddAssignee = new Alfresco.module.SimpleDialog(this.options.namespaceId + '-form-add-assignee');

			this.widgets.formAddAssignee.setOptions({
				width: '50em',
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: templateRequestParams,
				destroyOnHide: true,
				doBeforeDialogShow: {
					scope: this,
					fn: function(form, simpleDialog) {
						simpleDialog.dialog.setHeader(this.options.formAddAssigneeTitle);
					}
				},
				onSuccess: {
					scope: this,
					fn: this._refreshDatagrid
				},
				onFailure: {
					scope: this,
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('message.adding.item.fail')
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
