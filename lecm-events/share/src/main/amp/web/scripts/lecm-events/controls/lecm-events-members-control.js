if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function()
{
	/**
	 * Alfresco Slingshot aliases
	 */
	var Dom = YAHOO.util.Dom,
		Bubbling = YAHOO.Bubbling;
	Util = LogicECM.module.Base.Util;

	LogicECM.module.Calendar.MembersControl = function (htmlId)
	{
		this.id = htmlId;
		var module = LogicECM.module.Calendar.MembersControl.superclass.constructor.call(this, htmlId);
		window.addEventListener("resize", module.redraw.bind(module));
		Bubbling.on("mandatoryControlValueUpdated", module.dateFieldUpdated, module);
		Bubbling.on("setMemberCalendarDate", module.setMemberCalendarDate, module);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.MembersControl, LogicECM.module.AssociationTokenControl, {});

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.MembersControl.prototype, {
		defaultMandatory: true,
		startHour: 6,
		endHour: 22,
		timeStep: 15,
		firstColumnWidth: null,
		columnCalendarWidth: null,
		startDate: null,
		initialStartDate: null,
		endDate: null,
		selectedDate: null,
		initialEndDate: null,
		selectorBorderWidth: 4,
		prevStartIndex: null,
		prevEndIndex: null,
		prevSelectionStartIndex: null,
		prevSelectionEndIndex: null,
		maxIndex: 1,
		period: 0,
		headerIsReady: false,
		contentIsReady: false,
		startDateField: null,
		endDateField: null,
		keyIndex: [],
		busytime: [],
		busyInterval: [],
		busyColor: null,
		notBusyColor: null,
		needDraw: false,
		allDay: false,
		allDayBusy: false,
		cellBounds: null,

		onReady: function ConsoleGroups_onReady() {
			LogicECM.module.AssociationTokenControl.prototype.onReady.call(this);
			var prevDate = Dom.get(this.id + "-date-cntrl-prevDate");
			if (prevDate) {
				prevDate.addEventListener("click", this.prevDay.bind(this));
			}
			var nextDate = Dom.get(this.id + "-date-cntrl-nextDate");
			if (nextDate) {
				nextDate.addEventListener("click", this.nextDay.bind(this));
			}
			var pointDate = Dom.get(this.id + "-date-cntrl-pointDate");
			if (pointDate) {
				pointDate.addEventListener("click", this.resetDate.bind(this));
			}
			var components = Alfresco.util.ComponentManager.list();
			components.forEach(function(component) {
				this._formControlUpdated(component);
			}, this);
		},

		_loadSelectedItems: function (clearCurrentDisplayValue, updateForms) {
			var arrItems = "";
			if (!this.options.resetValue) {
				if (this.options.selectedValue != null) {
					arrItems = this.options.selectedValue;
				}
				else if (this.options.currentValue != null && this.isNodeRef(this.options.currentValue)) {
					arrItems = this.options.currentValue;
				}

				if (arrItems == "" && this.defaultValue != null) {
					arrItems += this.defaultValue;
				}
			}

			var onSuccess = function (response)
			{
				var items = response.json.data.items,
					item;
				this.selectedItems = {};

				this.singleSelectedItem = null;
				for (var i = 0, il = items.length; i < il; i++) {
					item = items[i];
					if (!this.options.checkType || item.type == this.options.itemType) {
						this.selectedItems[item.nodeRef] = item;

						if (!this.options.multipleSelectMode && this.singleSelectedItem == null) {
							this.singleSelectedItem = item;
						}
					}
				}

				if(!this.options.disabled)
				{
					this.updateSelectedItems();
					this.updateAddButtons();
				}
				if (updateForms) {
					this.updateFormFields(clearCurrentDisplayValue);
				}
			};

			var onFailure = function (response)
			{
				this.selectedItems = {};
			};

			if (arrItems !== "")
			{
				Alfresco.util.Ajax.jsonRequest(
					{
						url: Alfresco.constants.PROXY_URI + this.options.pickerItemsScript,
						method: "POST",
						dataObj:
						{
							items: arrItems.split(","),
							itemValueType: "nodeRef",
							itemNameSubstituteString: this.options.nameSubstituteString,
							sortProp: this.options.sortProp,
							selectedItemsNameSubstituteString: this.getSelectedItemsNameSubstituteString(),
							pathRoot: this.options.rootLocation,
							pathNameSubstituteString: this.options.treeNodeSubstituteString,
							eventNodeRef: this.options.eventNodeRef
						},
						successCallback:
						{
							fn: onSuccess,
							scope: this
						},
						failureCallback:
						{
							fn: onFailure,
							scope: this
						}
					});
			}
			else
			{
				// if disabled show the (None) message
				this.selectedItems = {};
				this.singleSelectedItem = null;
				if (!this.options.disabled) {
					this.updateSelectedItems();
					this.updateAddButtons();
				} else if (Dom.get(this.options.controlId + "-currentValueDisplay") != null && Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML.trim() === "") {
					Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
				}
				if (updateForms) {
					this.updateFormFields(clearCurrentDisplayValue);
				}
			}
		},

		updateFormFields: function (clearCurrentDisplayValue) {
			// Just element
			if (clearCurrentDisplayValue == null) {
				clearCurrentDisplayValue = true;
			}

			var el;
			el = Dom.get(this.options.controlId + "-currentValueDisplay");
			var autocompleteInput = Dom.get(this.options.controlId + "-autocomplete-input");

			if (autocompleteInput != null) {
				autocompleteInput.value = "";
				Dom.setStyle(autocompleteInput, "display", this.canAutocompleteInputShow() ? "block" : "none");
			}
			Dom.setStyle(el, "display", this.canCurrentValuesShow() ? "block" : "none");

			if (el != null) {
				var me = this;
				var items = this.getSelectedItems(!!this.options.sortSelected);

				if (clearCurrentDisplayValue) {
					el.innerHTML = '';
				}
				items.forEach(function(item, index, array){
					var displayName = me.selectedItems[item].selectedName;

					if(me.options.disabled) {
						//if (this.options.itemType == "lecm-orgstr:employee") {
						//	el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(this.this.selectedItems[item].nodeRef, displayName));
						//} else {
						el.innerHTML += Util.getCroppedItem(me.getMemberView(displayName, me.selectedItems[item]), me.getMandatoryCheckboxHTML(me.selectedItems[item], true) + me.getMemberStatusHTML(me.selectedItems[item]));
						//}
					} else {
						el.innerHTML += Util.getCroppedItem(me.getMemberView(displayName, me.selectedItems[item]), me.getMandatoryCheckboxHTML(me.selectedItems[item], false) + me.getMemberStatusHTML(me.selectedItems[item]) + me.getRemoveButtonHTML(me.selectedItems[item], "_c"));

						YAHOO.util.Event.onAvailable("t-" + me.options.prefixPickerId + me.selectedItems[item].nodeRef, me.attachRemoveClickListener, {node: me.selectedItems[item], dopId: "_c", updateForms: true}, me);
						YAHOO.util.Event.onAvailable(me.getMandatoryCheckboxId(me.selectedItems[item]), me.attachMandatoryCheckboxClickListener, me.selectedItems[item], me);
					}
				});
			}

			//Рисуем диаграмму, если это не форма просмотра, а редактирования
			el = Dom.get(this.options.controlId + "-diagram");
			if (el && !this.options.disabled) {
				this.needDraw = true;
				this._reset();
				this.draw();
				this.requestMembersTime();
			}

			if(!this.options.disabled)
			{
				var addItems = this.getAddedItems();

				// Update added fields in main form to be submitted
				el = Dom.get(this.options.controlId + "-added");
				if (el != null) {
					if (clearCurrentDisplayValue) {
						el.value = '';
					}
					for (i in addItems) {
						el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
					}
				}

				var selectedItems = this.getSelectedItems();
				var removedItems = this.getRemovedItems();

				// Update removed fields in main form to be submitted
				var removedEl = Dom.get(this.options.controlId + "-removed");
				if (removedEl != null) {
					removedEl.value = '';
					for (i in removedItems) {
						removedEl.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
					}
				}


				// Update selectedItems fields in main form to pass them between popup and form
				el = Dom.get(this.options.controlId + "-selectedItems");
				if (el != null) {
					if (clearCurrentDisplayValue) {
						el.value = '';
					}
					for (i in selectedItems) {
						el.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
					}

					//убираем selected из removed
					if (removedEl != null) {
						for (var k in Alfresco.util.arrayToObject(el.value.split(","))) {
							if (k.length > 0) {
								removedEl.value = removedEl.value.replace(k + ',', '');
								removedEl.value = removedEl.value.replace(k, '');
							}
						}
					}
					if (this.options.setCurrentValue && Dom.get(this.id) != null) {
						Dom.get(this.id).value = el.value;
					}
				}


				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
				}

				YAHOO.Bubbling.fire("formValueChanged",
					{
						eventGroup:this,
						addedItems:addItems,
						removedItems:removedItems,
						selectedItems:selectedItems,
						selectedItemsMetaData:Alfresco.util.deepCopy(this.selectedItems)
					});
			}

			this.updateJsonField();

			if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
				YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
					selectedItems: this.selectedItems,
					formId: this.options.formId,
					fieldId: this.options.fieldId,
					control: this
				});
			}
		},

		getMemberView: function (displayValue, item) {
			var result = "<span class='not-person'>";
			result += "<a href='javascript:void(0);' " + " onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + item.nodeRef + "\',title: \'logicecm.employee.view\' })\">" + displayValue + "</a>";
			result += "</span>";

			return result;
		},

		getMandatoryCheckboxId: function(node) {
			return "mchbx-" + this.options.prefixPickerId + node.nodeRef
		},

		getMandatoryCheckboxHTML: function (node, disabled) {
			var checked = "";

			if (node.memberMandatory == null) {
				node.memberMandatory = this.defaultMandatory;
			}

			if (node.memberMandatory) {
				checked = ' checked="checked"';
			}

			var disabledStr = "";
			if (disabled != null && disabled) {
				disabledStr = ' disabled="disabled"';
			}

			return '<input type="checkbox" class="members-mandatory" title="' + (node.memberMandatory ? this.msg("label.events.participant.mandatory") : this.msg("label.events.participant.not_mandatory")) + '"' + checked + disabledStr + ' id="' + this.getMandatoryCheckboxId(node) + '"/>';
		},

		getMemberStatusHTML: function (node) {
			var img = "";
			var title = "";
			if (node.memberStatus == "EMPTY") {
				img = "alf_waiting_grey_16.png";
				title = this.msg("label.events.participation.not_confirmed_yet");
			} else if (node.memberStatus == "CONFIRMED") {
				img = "alf_thumbUp_green_16.png";
				title = this.msg("label.events.participation.confirmed");
			} else if (node.memberStatus == "DECLINED") {
				img = "alf_thumbDown_red_16.png";
				title = this.msg("label.events.participation.rejected") + ": " + node.memberDeclineReason;
			} else if (node.memberStatus == "REQUEST_NEW_TIME") {
				img = "alf_clock_yellow_16.jpg";
				title = this.msg("label.events.participation.another_time") + ": " + Alfresco.util.formatDate(new Date(node.memberFromDate), this.msg("lecm.date-format.datetime"));
			}

			if (img.length > 0) {
				return '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-events/' + img + '" class="members-status" title="' + title + '"/>';
			} else {
				return "";
			}
		},

		attachMandatoryCheckboxClickListener: function (node) {
			YAHOO.util.Event.on(this.getMandatoryCheckboxId(node), 'click', this.mandatoryCheckboxClick, node, this);
		},

		mandatoryCheckboxClick: function (event, node) {
			this.defaultMandatory = event.target.checked;
			event.target.checked ?
				event.target.title = this.msg("label.events.participant.mandatory") :
				event.target.title = this.msg("label.events.participant.not_mandatory");

			this.selectedItems[node.nodeRef].memberMandatory = event.target.checked;
			this.updateJsonField();
		},

		updateJsonField: function() {
			var members = [];

			var selectedItems = this.getSelectedItems();
			if (selectedItems != null) {
				for (var i = 0; i < selectedItems.length; i++) {
					var mandatory = false;
					if (this.selectedItems[selectedItems[i]].memberMandatory != null) {
						mandatory = this.selectedItems[selectedItems[i]].memberMandatory;
					}

					members.push({
						nodeRef: selectedItems[i],
						mandatory: mandatory
					})
				}
			}

			var jsonFieldName = "prop_" + this.options.fieldId.replace(":", "_") + "-json";
			var form = Dom.get(this.options.formId +"-form");
			if (form != null && form[jsonFieldName] != null) {
				form[jsonFieldName].value = JSON.stringify(members);
			}
		},

		/**
		 * Создаем диаграмму занятости
		 */
		draw: function draw_function() {
			if (this.startDate && this.endDate && this.startDate <= this.endDate) {
				this.drawDiagramHeader();
				this.drawDiagram();
				this.drawSelector();
				this.drawCurrentState();
				this.fillBusyTime();
			}
		},

		/**
		 * Перерисовываем диаграмму занятости при изменении окна браузера
		 */
		redraw: function redraw_function() {
			var header = Dom.get(this.options.controlId + "-diagram-header");
			var len = header.children.length;
			for (var i = 1; i < len; i++) {
				header.removeChild(header.children[1]);
			}
			this.cellBounds = null;
			this.headerIsReady = false;
			this.contentIsReady = false;
			this.draw();
		},

		/**
		 * Обработчик изменения полей назначения времени
		 */
		dateFieldUpdated: function dateFieldUpdated_function(layer, args) {
			if (this.options.disabled) return;
			var control = args[1];
			this._formControlUpdated(control);
		},

		/**
		 *  Смена даты из диаграммы
		 */
		setMemberCalendarDate: function setMemberCalendarDate(layer, args) {
			var date = Alfresco.util.fromISO8601(args[1].date);
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			this.selectDate(date);
		},

		/**
		 * Смена состояния диаграммы при изменнии текущей отображаемой даты
		 * @param date
		 */
		selectDate: function selectDate_function(date) {
			var requestMemberTime = this.formatDate(date) != this.formatDate(this.selectedDate);
			this.selectedDate = date;
			if (this.needDraw) {
				if (requestMemberTime) {
					this._reset();
					this.draw();
					this.requestMembersTime();
				} else {
					this.draw();
				}
			}
		},

		/**
		 * Смена выбранной даты на следующий день
		 */
		nextDay: function nextDay_function() {
			var date = new Date(this.selectedDate.getTime() + 24 * 60 * 60 * 1000);
			Dom.get(this.id + "-date-cntrl-date").value = this.formatDate(date);
			this.selectDate(date);
		},

		/**
		 * Смена выбранной даты на предыдущий день
		 */
		prevDay: function prevDay_function() {
			var date = new Date(this.selectedDate.getTime() - 24 * 60 * 60 * 1000);
			Dom.get(this.id + "-date-cntrl-date").value = this.formatDate(date);
			this.selectDate(date);
		},

		/**
		 * Сброс даты на начальную
		 */
		resetDate: function resetDate_function() {
			if (this.prevStartIndex && this.prevEndIndex) {
				this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex);
			}
			this.prevStartIndex = null;
			this.prevEndIndex = null;
			this._reset();

			//Начало
			this.needDraw = false;
			Dom.get(this.startDateField.id + "-date").value = this.formatDate(this.initialStartDate);
			Dom.get(this.startDateField.id + "-time").value = ('0' + this.initialStartDate.getHours()).slice(-2) + ":" + ('0' + this.initialStartDate.getMinutes()).slice(-2);
			Bubbling.fire("handleFieldChange", {
				fieldId: this.startDateField.configName,
				formId: this.startDateField.formId
			});

			//Окончание
			this.needDraw = true;
			Dom.get(this.endDateField.id + "-date").value = this.formatDate(this.initialEndDate);
			Dom.get(this.endDateField.id + "-time").value = ('0' + this.initialEndDate.getHours()).slice(-2) + ":" + ('0' + this.initialEndDate.getMinutes()).slice(-2);
			Bubbling.fire("handleFieldChange", {
				fieldId: this.endDateField.configName,
				formId: this.endDateField.formId
			});
			this.requestMembersTime();
		},


		/**
		 * Запрос занятости участников
		 */
		requestMembersTime: function requestMembersTime_function() {
			var items = Object.keys(this.selectedItems).join(",");
			if (!items || !this.startDate) return;

			Alfresco.util.Ajax.jsonRequest(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/members/busytime",
					method: "GET",
					dataObj: {
						items: items,
						date: Alfresco.util.formatDate(this.selectedDate, "yyyy-mm-dd"),
						exclude: this.isNodeRef(this.options.eventNodeRef) ? this.options.eventNodeRef : ""
					},
					successCallback:
					{
						fn: function(response) {
							if (response.json && response.config.dataObj.date == Alfresco.util.formatDate(this.selectedDate, "yyyy-mm-dd")) {
								this.busytime = response.json;
								this.fillBusyTime(response.json);
							}
							if (this.prevStartIndex && this.prevEndIndex) {
								this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex);
								this._drawSelectorBorder(this.prevStartIndex, this.prevEndIndex);
							}
						},

						scope: this
					}
				});
		},

		requestAllDayMembersTime: function requestMembersTime_function() {
			var items = Object.keys(this.selectedItems).join(",");
			if (!items || !this.startDate) return;

			Alfresco.util.Ajax.jsonRequest(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/members/alldaybusytime",
					method: "GET",
					dataObj: {
						items: items,
						startDate: Alfresco.util.toISO8601(this.startDate),
						endDate: Alfresco.util.toISO8601(this.endDate),
						exclude: this.isNodeRef(this.options.eventNodeRef) ? this.options.eventNodeRef : ""
					},
					successCallback:
					{
						fn: function(response) {
							if (response.json && response.config.dataObj.startDate == Alfresco.util.toISO8601(this.startDate)) {
								this.allDayBusy = response.json.isBusy;
							}
							if (this.prevStartIndex && this.prevEndIndex) {
								this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex);
								this._drawSelectorBorder(this.prevStartIndex, this.prevEndIndex);
							}
						},

						scope: this
					}
				});
		},

		/**
		 * Диаграмма для выбора времени проведения мероприятия/совещания на редактировании
		 */
		drawDiagram: function drawDiagram_function() {
			if (this.contentIsReady) return;
			Dom.removeClass(this.options.controlId + "-diagram", "hidden1");
			var content = Dom.get(this.options.controlId + "-diagram-content");
			content.innerHTML = "";
			var itemNum = 1;
			this.keyIndex = [];
			var cellBounds = this._calculateCell();
			var delta = cellBounds.firstColumnWidth - this.firstColumnWidth;
			var items = this.getSelectedItems(!!this.options.sortSelected);
			items.forEach(function(key){
				var item = this.selectedItems[key];
				var row = document.createElement("div");
				row.className = "member-control-diagram-row";
				content.appendChild(row);
				var firstColumn = document.createElement("div");
				firstColumn.className = "member-control-diagram-first-cell";
				firstColumn.style.width = cellBounds.firstColumnWidth + "px";
				row.appendChild(firstColumn);

				//Обязательность сотрудника
				var mandatoryCheckbox = document.createElement("input");
				mandatoryCheckbox.type = "checkbox";
				if (item.memberMandatory == null) {
					item.memberMandatory = this.defaultMandatory;
				}
				mandatoryCheckbox.checked = item.memberMandatory;
				mandatoryCheckbox.title = item.memberMandatory ? this.msg("label.events.participant.mandatory") : this.msg("label.events.participant.not_mandatory");
				mandatoryCheckbox.className = "member-control-diagram-first-cell-mandatory";
				firstColumn.appendChild(mandatoryCheckbox);
				YAHOO.util.Event.on(mandatoryCheckbox, 'click', this.mandatoryCheckboxClick, item, this);

				//Имя сотрудника
				var textCell = document.createElement("div");
				textCell.className = "member-control-diagram-first-cell-text";
				textCell.innerHTML = item.selectedName;
				textCell.title = item.selectedName;
				firstColumn.appendChild(textCell);
				var textBounds = Dom.getRegion(textCell);
				textCell.style.width = (delta + textBounds.width) + "px";

				//Кнопка удаления
				var removeButton = document.createElement("div");
				removeButton.className = "member-control-diagram-first-cell-remove";
				firstColumn.appendChild(removeButton);
				YAHOO.util.Event.on(removeButton, 'click', this.removeDiagramItem, {
					node: item,
					updateForms: true
				}, this);

				this._drawHourCells(row, itemNum, false);
				this.keyIndex[key] = itemNum;
				itemNum++;
			}, this);
			this.contentIsReady = true;
		},

		/**
		 * Удаление записи из диаграммы времени
		 * @param ev
		 * @param params
		 */
		removeDiagramItem: function removeDiagramItem_function(ev, params) {
			var diagram = Dom.get(this.options.controlId + "-diagram");
			for (var key in this.selectedItems) {
				if (this.selectedItems.hasOwnProperty(key)) {
					var item = this.selectedItems[key];
					var rowIndex = this.keyIndex[item.nodeRef];
					var id = this.options.controlId + "-diagram-member-status-" + rowIndex;
					var prevIcon = Dom.get(id);
					if (prevIcon) {
						diagram.removeChild(prevIcon);
					}
				}
			}
			this._reset();
			this.removeNode(ev, params);
		},

		/**
		 * Рисуем заголовок диаграммы
		 */
		drawDiagramHeader: function drawDiagramHeader_function() {
			if (this.headerIsReady) return;
			if (!this.firstColumnWidth) {
				var calendarCellBounds = Dom.getRegion(this.id + "-date-cntrl-cal-cell");
				this.firstColumnWidth = calendarCellBounds.width;
				var calendarBounds = Dom.getRegion(this.id + "-date-cntrl-date");
				this.columnCalendarWidth = calendarBounds.width;
			}
			//Устанавливаем размер контейнера
			var cellBounds = this._calculateCell();
			var hours = this.endHour - this.startHour + 1;
			var width = cellBounds.firstColumnWidth + 2 + hours * cellBounds.width + hours;
			var header = Dom.get(this.options.controlId + "-diagram-header");
			this._drawHourCells(header, 0, true);
			Dom.get(this.id + "-date-cntrl-cal-cell").style.width = cellBounds.firstColumnWidth + "px";
			var delta = cellBounds.firstColumnWidth - this.firstColumnWidth - 6;

			Dom.get(this.id + "-date-cntrl-date").style.width = (delta + this.columnCalendarWidth) + "px";
			this.headerIsReady = true;
			Dom.get(this.options.controlId + "-diagram-container").style.width = width + "px";
		},

		/**
		 * Создаем рамку выбора времени
		 */
		drawSelector: function drawSelector() {
			var startTime = this.startDate;
			var endTime = this.endDate;
			if (this.prevStartIndex && this.prevEndIndex) {
				this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex, false);
			}
			var forAllDayPeriod = false;
			if (this.allDay) {
				var beginStartDate = new Date(startTime.getTime());
				beginStartDate.setHours(0);
				beginStartDate.setMinutes(0);
				beginStartDate.setSeconds(0);
				var beginEndDate = new Date(endTime.getTime());
				beginEndDate.setHours(0);
				beginEndDate.setMinutes(0);
				beginEndDate.setSeconds(0);
				forAllDayPeriod = this.allDay && this.selectedDate >= beginStartDate && this.selectedDate <= beginEndDate;
			}
			//Расчет индексов старта и конца
			var startHour = startTime.getHours();
			var startMinutes = startTime.getMinutes();
			var endHour = endTime.getHours();
			var endMinutes = endTime.getMinutes();
			var cellBounds = this._calculateCell();
			var startIndex = (startHour - this.startHour) * cellBounds.cellByHour + Math.round(startMinutes / this.timeStep) + 1;

			this.maxIndex = (this.endHour - this.startHour + 1) * cellBounds.cellByHour;
			var days = 0;
			if (!forAllDayPeriod) {
				days = this.dateDiffInDays(endTime, startTime);
			}
			var endIndex = days * this.maxIndex + (endHour - this.startHour) * cellBounds.cellByHour + Math.round(endMinutes / this.timeStep);

			//расскрашиваем рамку
			if (this.dateDiffInDays(this.selectedDate, this.startDate) == 0 || forAllDayPeriod) {
				this._drawSelectorBorder(startIndex, endIndex);
				this.prevStartIndex = startIndex;
				this.prevEndIndex = endIndex;
				this.period = endIndex - startIndex;
			} else {
				this.prevStartIndex = null;
				this.prevEndIndex = null;
			}
		},

		/**
		 * Приведение даты к формату mm.dd.yyyy
		 * @param date
		 * @returns {string}
		 */
		formatDate: function formatDate_function(date) {
			var mm = ("0" + (date.getMonth()+1)).slice(-2);
			var dd = ("0" + date.getDate()).slice(-2);
			return dd + "." + mm + "." + date.getFullYear();
		},

		/**
		 * Заполнение занятости сотрудника
		 */
		fillBusyTime: function fillBusyTime_function() {
			this.busyInterval = [];
			var startDayDate = new Date(this.selectedDate.getTime());
			startDayDate.setHours(this.startHour);
			startDayDate.setMinutes(0);
			startDayDate.setSeconds(0);

			var endDayDate = new Date(this.selectedDate.getTime());
			endDayDate.setHours(this.endHour);
			endDayDate.setMinutes(0);
			endDayDate.setSeconds(0);

			var cellSettings = this._calculateCell();
			for (var i in this.busytime) {
				var times = this.busytime[i].busytime;
				for (var key in times) {
					var time = times[key];
					var start = Alfresco.util.fromISO8601(time.start);
					var end = Alfresco.util.fromISO8601(time.end);
					var employee = this.busytime[i].employee;
					var startIndex, endIndex;
					if (start <= startDayDate) {
						startIndex = 1;
					} else {
						var startHour = start.getHours();
						var startMinutes = start.getMinutes();
						startIndex = (startHour - this.startHour) * cellSettings.cellByHour + Math.round(startMinutes / this.timeStep) + 1;
					}

					if (endDayDate <= end) {
						endIndex = this.maxIndex;
					} else {
						var endHour = end.getHours();
						var endMinutes = end.getMinutes();
						var days = this.dateDiffInDays(end, start);
						endIndex = days * this.maxIndex + (endHour - this.startHour) * cellSettings.cellByHour + Math.round(endMinutes / this.timeStep);
					}

					var rowIndex = this.keyIndex[employee];
					var fillIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
					for (var j = startIndex; j <= fillIndex; j++) {
						var cell = Dom.get(this.options.controlId + "-diagram_" + rowIndex + "_" + j);
						Dom.removeClass(cell, "member-control-diagram-current");
						Dom.addClass(cell, "member-control-diagram-busy-cell");
						cell.title = time.title;
					}
					this.busyInterval.push({
						start: startIndex,
						end: endIndex
					});
				}
			}
		},

		/**
		 * Отображаем текущее состояние состояние выбранного времени
		 */
		drawCurrentState: function drawCurrentState_function() {
			//Не отображаем если это не дата начала
			if (this.selectedDate.getYear() != this.initialStartDate.getYear() ||
				this.selectedDate.getMonth() != this.initialStartDate.getMonth() ||
				this.selectedDate.getDate() != this.initialStartDate.getDate() ||
				!this.isNodeRef(this.options.currentValue)) {

				var diagram = Dom.get(this.options.controlId + "-diagram");
				for (var key in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(key)) {
						var item = this.selectedItems[key];
						var rowIndex = this.keyIndex[item.nodeRef];
						var id = this.options.controlId + "-diagram-member-status-" + rowIndex;
						var prevIcon = Dom.get(id);
						if (prevIcon) {
							diagram.removeChild(prevIcon);
						}
					}
				}
				return;
			}
			var startDayDate = new Date(this.initialStartDate.getTime());
			startDayDate.setHours(this.startHour);
			startDayDate.setMinutes(0);
			startDayDate.setSeconds(0);

			var endDayDate = new Date(this.initialEndDate.getTime());
			endDayDate.setHours(this.endHour);
			endDayDate.setMinutes(0);
			endDayDate.setSeconds(0);

			var cellSettings = this._calculateCell();
			var startIndex, endIndex;
			if (this.initialStartDate <= startDayDate) {
				startIndex = 0;
			} else {
				var startHour = this.initialStartDate.getHours();
				var startMinutes = this.initialStartDate.getMinutes();
				startIndex = (startHour - this.startHour) * cellSettings.cellByHour + Math.round(startMinutes / this.timeStep) + 1;
			}

			if (endDayDate <= this.initialEndDate) {
				endIndex = this.maxIndex;
			} else {
				var endHour = this.initialEndDate.getHours();
				var endMinutes = this.initialEndDate.getMinutes();
				var days = this.dateDiffInDays(this.initialEndDate, this.initialStartDate);
				endIndex = days * this.maxIndex + (endHour - this.startHour) * cellSettings.cellByHour + Math.round(endMinutes / this.timeStep);
			}

			startIndex = startIndex > 0 ? startIndex : 1;
			var fillIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			var diagram = Dom.get(this.options.controlId + "-diagram");
			var diagramBounds = Dom.getRegion(diagram);
			for (var key in this.selectedItems) {
				if (this.selectedItems.hasOwnProperty(key)) {
					var item = this.selectedItems[key];
					var rowIndex = this.keyIndex[item.nodeRef];
					for (var i = startIndex; i <= fillIndex; i++) {
						var cell = Dom.get(this.options.controlId + "-diagram_" + rowIndex + "_" + i);
						Dom.addClass(cell, "member-control-diagram-current");
					}
					//Добавляем иконку
					var id = this.options.controlId + "-diagram-member-status-" + rowIndex;
					var prevIcon = Dom.get(id);
					if (prevIcon) {
						diagram.removeChild(prevIcon);
					}
					if (startIndex < fillIndex) {
						var iconItem = this.getMemberStatusHTML(item);
						if (iconItem) {
							var icon = document.createElement("div");
							icon.id = id;
							icon.innerHTML = iconItem;
							icon.className = "member-control-diagram-member-status";

							if (item.memberStatus == "REQUEST_NEW_TIME") {
								icon.style.cursor = "pointer";
								var me = this;
								var requestDate = new Date(item.memberFromDate);
								var requestIndex = this.keyIndex[item.nodeRef];
								YAHOO.util.Event.on(icon, "click", function (ev, obj) {
									me.selectByDate(obj.requestDate, obj.requestIndex);
								}, {
									requestDate: requestDate,
									requestIndex: requestIndex
								});
							}

							var firstCell = Dom.get(this.options.controlId + "-diagram_" + rowIndex + "_" + startIndex);
							var firstCellRegion = Dom.getRegion(firstCell);
							var lastCell = Dom.get(this.options.controlId + "-diagram_" + rowIndex + "_" + fillIndex);
							var lastCellRegion = Dom.getRegion(lastCell);
							var left = firstCellRegion.left + (Math.round((lastCellRegion.left + cellSettings.cellWidth - firstCellRegion.left) / 2) - 10);

							icon.style.left = (left - diagramBounds.left) + "px";
							icon.style.top = (firstCellRegion.top - diagramBounds.top + 2) + "px";
							diagram.appendChild(icon);
						}
					}
				}
			}
		},


		/**
		 * Очистка маркера выбора заголовка таблицы
		 * @param ev
		 */
		clearSelectionHeader: function clearSelectionHeader_function(ev) {
			if (this.allDay) return;
			var el = ev.target;
			if (!Dom.hasClass(el, "member-control-diagram-empty-cell")) return;
			if (this.prevSelectionStartIndex && this.prevSelectionEndIndex) {
				for (var i = this.prevSelectionStartIndex; i <= this.prevSelectionEndIndex; i++) {
					Dom.removeClass(this.options.controlId + "-diagram-select-layer_0_" + i, "member-control-diagram-selection");
				}
			}
		},

		/**
		 * Отрисовка маркера выбора в заголовке таблицы
		 * @param ev
		 */
		drawSelectionHeader: function drawSelectionHeader_function(ev) {
			if (this.allDay) return;
			var el = ev.target;
			if (!Dom.hasClass(el, "member-control-diagram-empty-cell")) return;

			var cellIndex = el.cellIndex - Math.floor(this.period / 2);
			cellIndex = cellIndex <= 0 ? 1 : cellIndex;
			var lastCellIndex = cellIndex + this.period;
			if (lastCellIndex > this.maxIndex) {
				lastCellIndex = this.maxIndex;
				cellIndex = lastCellIndex - this.period;
			}
			for (var i = cellIndex; i <= lastCellIndex; i++) {
				Dom.addClass(this.options.controlId + "-diagram-select-layer_0_" + i, "member-control-diagram-selection");
			}
			this.prevSelectionStartIndex = cellIndex;
			this.prevSelectionEndIndex = lastCellIndex;
		},

		/**
		 * Выбор времени из заголовка таблицы
		 * @param ev
		 */
		selectHeaderTime: function selectHeaderTime_function(ev) {
			if (this.allDay) return;
			var el = ev.target;
			if (!Dom.hasClass(el, "member-control-diagram-empty-cell")) return;

			var cellIndex = el.cellIndex - Math.floor(this.period / 2);
			cellIndex = cellIndex <= 0 ? 1 : cellIndex;
			var lastCellIndex = cellIndex + this.period;
			if (lastCellIndex > this.maxIndex) {
				lastCellIndex = this.maxIndex;
				cellIndex = lastCellIndex - this.period;
			}
			this.selectTime(cellIndex);
			this.draw();
			this.prevStartIndex = cellIndex;
			this.prevEndIndex = lastCellIndex;
		},

		/**
		 * Выбор времени в таблице по дате
		 * @param requestDate
		 */
		selectByDate: function selectRequestTime_function(requestDate, requestIndex) {
			this.selectedDate = requestDate;

			var startHour = this.selectedDate.getHours();
			var startMinutes = this.selectedDate.getMinutes();
			var cellBounds = this._calculateCell();
			var startIndex = (startHour - this.startHour) * cellBounds.cellByHour + Math.round(startMinutes / this.timeStep) + 1;
			this.selectTime(startIndex);
			this._reset();
			this.draw();
			var lastCellIndex = startIndex + this.period;
			if (lastCellIndex > this.maxIndex) {
				lastCellIndex = this.maxIndex;
				startIndex = lastCellIndex - this.period;
			}
			this.prevStartIndex = startIndex;
			this.prevEndIndex = lastCellIndex;
			this.requestMembersTime();
			if (requestIndex) {
				var diagram = Dom.get(this.options.controlId + "-diagram");
				var diagramBounds = Dom.getRegion(diagram);
				var cellSettings = this._calculateCell();
				var item;
				for (var key in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(key)) {
						var item = this.selectedItems[key];
						if (requestIndex == this.keyIndex[item.nodeRef]) {
							break;
						}
					}
				}
				//Добавляем иконку
				var id = this.options.controlId + "-diagram-member-status-" + requestIndex;
				var prevIcon = Dom.get(id);
				if (prevIcon) {
					diagram.removeChild(prevIcon);
				}

				var img = "alf_clock_green_16.jpg";
				var title = this.msg("label.events.participation.another_time_suggested");
				var iconItem = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-events/' + img + '" class="members-status" title="' + title + '"/>';

				var icon = document.createElement("div");
				icon.id = id;
				icon.innerHTML = iconItem;
				icon.className = "member-control-diagram-member-status";

				var firstCell = Dom.get(this.options.controlId + "-diagram_" + requestIndex + "_" + startIndex);
				var firstCellRegion = Dom.getRegion(firstCell);
				var lastCell = Dom.get(this.options.controlId + "-diagram_" + requestIndex + "_" + (startIndex + this.period));
				var lastCellRegion = Dom.getRegion(lastCell);
				var left = firstCellRegion.left + (Math.round((lastCellRegion.left + cellSettings.cellWidth - firstCellRegion.left) / 2) - 10);

				icon.style.left = (left - diagramBounds.left) + "px";
				icon.style.top = (firstCellRegion.top - diagramBounds.top + 2) + "px";
				diagram.appendChild(icon);
			}
		},

		/**
		 * Выбор времени в таблице по индексу
		 * @param cellIndex
		 */
		selectTime: function selectTime_function (cellIndex) {
			var lastCellIndex = cellIndex + this.period;

			//Передаем данные в контролы
			this.needDraw = false;
			var startMinutes = (cellIndex - 1) * this.timeStep;
			var days = Math.floor(lastCellIndex / this.maxIndex);
			var endIndex = lastCellIndex - days * this.maxIndex;
			if (endIndex == 0) {
				endIndex = this.maxIndex;
				days--;
			}
			var endMinutes = endIndex * this.timeStep;
			var startHour = Math.floor(startMinutes / 60);
			var endHour = Math.floor(endMinutes / 60);
			startMinutes = startMinutes - startHour * 60;
			endMinutes = endMinutes - endHour * 60;
			startHour = startHour + this.startHour;
			endHour = endHour + this.startHour;
			var start = new Date(this.selectedDate.getTime());

			//Обновляем состояние диаграммы и полей времени
			this.startDate = null;
			this.endDate = null;

			//Сбрасываем значения контролов
			Dom.get(this.startDateField.id + "-date").value = "";
			Dom.get(this.startDateField.id + "-time").value = "";
			Dom.get(this.endDateField.id + "-date").value = "";
			Dom.get(this.endDateField.id + "-time").value = "";
			Bubbling.fire("handleFieldChange", {
				fieldId: this.startDateField.configName,
				formId: this.startDateField.formId
			});
			Bubbling.fire("handleFieldChange", {
				fieldId: this.endDateField.configName,
				formId: this.endDateField.formId
			});

			//Начало
			Dom.get(this.startDateField.id + "-date").value = this.formatDate(start);
			Dom.get(this.startDateField.id + "-time").value = ('0' + startHour).slice(-2) + ":" + ('0' + startMinutes).slice(-2);
			Bubbling.fire("handleFieldChange", {
				fieldId: this.startDateField.configName,
				formId: this.startDateField.formId
			});

			//Окончание
			start.setHours(this.startHour);
			start.setMinutes(0);
			start.setSeconds(0);
			var endDate = new Date(start.getTime() + days * 24 * 60 * 60 * 1000);
			Dom.get(this.endDateField.id + "-date").value = this.formatDate(endDate);
			Dom.get(this.endDateField.id + "-time").value = ('0' + endHour).slice(-2) + ":" + ('0' + endMinutes).slice(-2);
			Bubbling.fire("handleFieldChange", {
				fieldId: this.endDateField.configName,
				formId: this.endDateField.formId
			});
			this.needDraw = true;
			this.startDate = new Date(this.selectedDate.getTime());
			this.startDate.setHours(startHour);
			this.startDate.setMinutes(startMinutes);
			this.endDate = new Date(this.selectedDate.getTime());
			this.endDate.setHours(endHour);
			this.endDate.setMinutes(endMinutes);

		},

		/**
		 * Удаление рамки предыдущего выделенного периода
		 * @param startIndex
		 * @param endIndex
		 * @param onlyHeader
		 * @private
		 */
		_clearSelectorBorder: function(startIndex, endIndex, onlyHeader) {
			var fillIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			for (var i = startIndex; i <= fillIndex; i++) {
				var cell = Dom.get(this.options.controlId + "-diagram_0_" + i);
				cell.style.backgroundColor = "transparent";
			}

			if (!onlyHeader) {
				var keysLen = Object.keys(this.selectedItems).length;
				var cellBounds = this._calculateCell();
				for (var i = 1; i <= keysLen; i++) {
					var leftCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + startIndex);
					leftCell.style.borderLeft = "none";
					leftCell.style.width = (parseInt(leftCell.style.width) + this.selectorBorderWidth) + "px";
					if (endIndex <= this.maxIndex) {
						var rightCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + endIndex);
						rightCell.style.borderRight = "none";
						rightCell.style.width = cellBounds.cellWidth + "px";
					}

					if (i == keysLen) {
						for (var j = startIndex; j <= endIndex; j++) {
							if (j > this.maxIndex) {
								break;
							}
							var cell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + j);
							cell.style.borderBottom = "none";
							cell.style.height = cellBounds.height + "px";
						}

					}
				}
			}
		},

		/**
		 * Отрисовка текущего выделенного периода
		 * @param startIndex
		 * @param endIndex
		 * @private
		 */
		_drawSelectorBorder: function(startIndex, endIndex) {
			var isBusy = false;
			if (this.allDay) {
				isBusy = this.allDayBusy;
			} else {
				for (var key in this.busyInterval) {
					var interval = this.busyInterval[key];
					if ((startIndex >= interval.start && startIndex <= interval.end) ||
						(endIndex >= interval.start && endIndex <= interval.end) ||
						(startIndex <= interval.start && endIndex >= interval.end)) {
						isBusy = true;
						break;
					}
				}
			}

			if (!this.busyColor) {
				var style = this._getStyle(".member-control-diagram-selector-busy");
				this.busyColor = style.style.color;
			}
			if (!this.notBusyColor) {
				var style = this._getStyle(".member-control-diagram-selector-not-busy");
				this.notBusyColor = style.style.color;
			}
			var color = isBusy ? this.busyColor : this.notBusyColor;
			var headerIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			for (var i = startIndex; i <= headerIndex; i++) {
				var cell = Dom.get(this.options.controlId + "-diagram_0_" + i);
				cell.style.backgroundColor = color;
			}

			var cellBounds = this._calculateCell();
			var keysLen = Object.keys(this.selectedItems).length;
			for (var i = 1; i <= keysLen; i++) {
				var leftCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + startIndex);
				leftCell.style.borderLeft = this.selectorBorderWidth + "px solid " + color;
				leftCell.style.width = (parseInt(leftCell.style.width) - this.selectorBorderWidth) + "px";
				if (endIndex <= this.maxIndex) {
					var rightCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + endIndex);
					rightCell.style.borderRight = this.selectorBorderWidth + "px solid " + color;
					rightCell.style.width = (cellBounds.cellWidth - this.selectorBorderWidth) + "px";
				}
				if (i == keysLen) {
					for (var j = startIndex; j <= endIndex; j++) {
						if (j > this.maxIndex) {
							break;
						}
						var cell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + j);
						cell.style.height = (cellBounds.height - this.selectorBorderWidth) + "px";
						cell.style.borderBottom = this.selectorBorderWidth + "px solid " + color;
					}

				}
			}
		},

		/**
		 * Отрисовка ячеек равных промежутку timeStep
		 * @param container
		 * @param lineNum
		 * @param isHeader
		 */
		_drawHourCells: function drawHourCells_function(container, lineNum, isHeader) {
			var cellBounds = this._calculateCell();
			var cellByHour = cellBounds.cellByHour;
			var cellWidth = cellBounds.cellWidth;
			for (var hour = this.startHour; hour <= this.endHour; hour++) {
				var column = document.createElement("div");
				column.style.width = (cellWidth * cellByHour) + "px";
				if (isHeader) {
					column.className = "member-control-diagram-header-cell";
				} else {
					column.className = "member-control-diagram-cell";
				}
				column.innerHTML = "&nbsp;";
				container.appendChild(column);
				var left = 0;
				for (var i = 1; i <= cellByHour; i++) {
					var cell = document.createElement("div");
					cell.style.width = cellWidth + "px";
					cell.style.left = left + "px";
					cell.className = "member-control-diagram-empty-cell";
					cell.innerHTML = "&nbsp";
					cell.id = this.options.controlId + "-diagram_" + lineNum + "_" + ((hour - this.startHour) * cellByHour + i);
					column.appendChild(cell);
					if (isHeader) {
						cell = document.createElement("div");
						cell.style.width = cellWidth + "px";
						cell.style.left = left + "px";
						cell.className = "member-control-diagram-empty-cell";
						cell.innerHTML = "&nbsp";
						cell.id = this.options.controlId + "-diagram-select-layer_" + lineNum + "_" + ((hour - this.startHour) * cellByHour + i);
						column.appendChild(cell);
					}
					left += cellWidth;
				}

				if (isHeader) {
					var textColumn = document.createElement("div");
					textColumn.style.width = (cellWidth * cellByHour) + "px";
					textColumn.className = "member-control-diagram-header-text-cell";
					textColumn.innerHTML = hour;
					column.appendChild(textColumn);
					var left = 0;
					for (var i = 1; i <= cellByHour; i++) {
						var cell = document.createElement("div");
						cell.style.width = cellWidth + "px";
						cell.style.left = left + "px";
						left += cellWidth;
						cell.className = "member-control-diagram-empty-cell";
						cell.style.cursor = "pointer";
						cell.innerHTML = "&nbsp";
						cell.cellIndex = (hour - this.startHour) * cellByHour + i;
						column.appendChild(cell);
						cell.addEventListener("mouseover", this.drawSelectionHeader.bind(this));
						cell.addEventListener("mouseout", this.clearSelectionHeader.bind(this));
						cell.addEventListener("click", this.selectHeaderTime.bind(this));
					}
				}
			}
		},

		/**
		 * Расчет параметров ячеке для отрисовки
		 * @returns {{width: number, cellByHour: number, cellWidth: number}}
		 */
		_calculateCell: function calculateCell_function() {
			if (!this.cellBounds) {
				var region = Dom.getRegion(this.options.controlId + "-diagram");
				var width = region.width - this.firstColumnWidth;
				var bordersWidth = this.endHour - this.startHour + 1;
				width = Math.floor((width - bordersWidth) / (this.endHour - this.startHour + 1));
				var cellByHour = Math.round(60 / this.timeStep);
				var cellWidth = Math.floor(width / cellByHour);
				width = cellWidth * cellByHour;

				//member-control-diagram-empty-cell
				var style = this._getStyle(".member-control-diagram-empty-cell");
				this.cellBounds = {
					height: parseInt(style.style.height),
					width: width,
					cellByHour: cellByHour,
					cellWidth: cellWidth,
					firstColumnWidth: region.width - width * (this.endHour - this.startHour + 1) - bordersWidth
				};
			}
			return this.cellBounds;
		},

		/**
		 * Проверяем является ли строка nodeRef
		 * @param value
		 * @returns {boolean}
		 */
		isNodeRef: function (value) {
			var regexNodeRef = new RegExp(/^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/);
			var result = false;
			try {
				result = regexNodeRef.test(String(value));
			}
			catch (e) {
			}
			return result;
		},

		/**
		 * Расчет разницы между двумя датами в днях
		 * @param bigDate
		 * @param smallDate
		 * @returns {number}
		 */
		dateDiffInDays: function (bigDate, smallDate) {
			var first = new Date(smallDate.getTime());
			first.setHours(0);
			first.setMinutes(0);
			first.setSeconds(0);
			var second = new Date(bigDate.getTime());
			second.setHours(0);
			second.setMinutes(0);
			second.setSeconds(0);
			return Math.round((second-first) / (1000 * 60 * 60 * 24));
		},

		/**
		 * Выборка правила из таблицы стилей
		 * @param className_
		 * @returns {CssRule}
		 */
		_getStyle: function getStyle_function(className_) {
			var styleSheets = window.document.styleSheets;
			var styleSheetsLength = styleSheets.length;
			for (var i = 0; i < styleSheetsLength; i++) {
				var classes = styleSheets[i].rules || styleSheets[i].cssRules;
				if (!classes)
					continue;
				var classesLength = classes.length;
				for (var x = 0; x < classesLength; x++) {
					if (classes[x].selectorText == className_) {
						return classes[x];
					}
				}
			}
		},

		/**
		 * Метод обработки контролов при инициализации и изменении их состояния
		 * @param control
		 * @private
		 */
		_formControlUpdated: function _formControlUpdated(control) {
			if (control && control.options && control.options.fieldId && control.options.formId == this.options.formId) {
				var fieldId = control.options.fieldId;
				//Данные из поля "Начало"
				if (fieldId == this.options.fromDateConfigName) {
					this._updateStartDate(control);
					//Данные из поля завершение
				} else if (fieldId == this.options.toDateConfigName) {
					this._updateEndDate(control);
					//Флаг "Весь день"
				} else if (fieldId == this.options.allDayConfigName) {
					this._updateAllDay(control);
				}
			}
		},

		/**
		 * Обработка изменения состояния контрола даты начала
		 * @param control
		 * @private
		 */
		_updateStartDate: function _updateStartDate_function(control) {
			var valueField = Dom.get(control.currentValueHtmlId);
			var prevDate = this.startDate ? this.formatDate(this.startDate) : null;
			var date = Alfresco.util.fromISO8601(valueField.value);
			if (!this.initialStartDate) {
				this.initialStartDate = new Date(date.getTime());
			}
			this.selectedDate = new Date(date.getTime());
			this.startDate = date;
			this.startDateField = {
				id: control.id,
				formId: control.options.formId,
				configName: control.options.fieldId
			};
			Dom.get(this.id + "-date-cntrl-date").value = Dom.get(control.id + "-date").value;
			Bubbling.fire("handleFieldChange", {
				fieldId: this.options.fieldId,
				formId: this.options.formId
			});
			if (this.needDraw) {
				if (this.formatDate(this.startDate) != prevDate) {
					this._reset();
					this.draw();
					this.requestMembersTime();
					if (this.allDay) {
						this.requestAllDayMembersTime();
					}
				} else {
					this.draw();
				}
			}
			if (!this.allDay && this.endDateField ) {
				Dom.get(this.endDateField.id + "-date").value = this.formatDate(this.startDate);
				Bubbling.fire("handleFieldChange", {
					fieldId: this.endDateField.configName,
					formId: this.endDateField.formId
				});
			}
		},

		/**
		 * Обработка изменения состояния контрола даты окончания
		 * @param control
		 * @private
		 */
		_updateEndDate: function _updateEndDate_function(control) {
			var valueField = Dom.get(control.currentValueHtmlId);
			var prevDate = this.endDate ? this.formatDate(this.endDate) : null;

			var date = Alfresco.util.fromISO8601(valueField.value);
			if (!this.initialEndDate) {
				this.initialEndDate = new Date(date.getTime());
			}
			this.endDate = date;
			this.endDateField = {
				id: control.id,
				formId: control.options.formId,
				configName: control.options.fieldId
			};
			if (this.needDraw) {
				if (this.formatDate(this.endDate) != prevDate) {
					this._reset();
					this.draw();
					this.requestMembersTime();
					if (this.allDay) {
						this.requestAllDayMembersTime();
					}
				} else {
					this.draw();
				}
			}
		},

		/**
		 * Обработка изменения состояния контрола "Весь день"
		 * @param control
		 * @private
		 */
		_updateAllDay: function _updateAllDay(control) {
			if (control.checkbox) {
				this.allDay = control.checkbox.checked;
			}
			if (!this.allDay && this.endDateField ) {
				Dom.get(this.endDateField.id + "-date").value = this.formatDate(this.startDate);
				Bubbling.fire("handleFieldChange", {
					fieldId: this.endDateField.configName,
					formId: this.endDateField.formId
				});
			} else if (this.allDay) {
				this.requestAllDayMembersTime();
			}
		},

		_reset: function () {
			this.busytime = [];
			this.contentIsReady = false;
			if (this.prevStartIndex && this.prevEndIndex) {
				this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex, true);
				this.prevStartIndex = null;
				this.prevEndIndex = null;
			}
		}
		///////////////////////////////////////
	}, true);
})();