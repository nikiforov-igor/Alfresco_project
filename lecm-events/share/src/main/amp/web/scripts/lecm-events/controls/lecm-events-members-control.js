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
		firstColumnWidth: 140,
		selector: null,
		startDate: null,
		endDate: null,
		selectorBorderWidth: 4,
		prevStartIndex: null,
		prevEndIndex: null,
		dragEnabled: false,
		dragDelta: 0,
		maxIndex: 1,
		headerIsReady: false,
		startDateField: null,
		endDateField: null,
		keyIndex: [],
		busytime: {},
		busyInterval: [],

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
			result += "<a href='javascript:void(0);' " + " onclick=\"viewAttributes(\'" + item.nodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a>";
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
				this.fillBusyTime();
				this.drawSelector();
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
			this.headerIsReady = false;
			this.drawDiagramHeader();
			this.drawDiagram();
			this.drawSelector();
		},

		/**
		 * Обработчик изменения полей назначения времени
		 */
		dateFieldUpdated: function dateFieldUpdated_function(layer, args) {
			if (this.options.disabled) return;
			var control = args[1];
			if (control && control.options.fieldId) {
				var fieldId = control.options.fieldId;
				//Данные из поля "Начало"
				if (fieldId == "lecm-events:from-date") {
					var valueField = Dom.get(control.currentValueHtmlId);
					var prevDate = this.startDate ? this.formatDate(this.startDate) : null;
					this.startDate = Alfresco.util.fromISO8601(valueField.value);
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
					this.draw();
					if (this.formatDate(this.startDate) != prevDate) {
						this.requestMembersTime();
					}
					//Данные из поля завершение
				} else if (fieldId == "lecm-events:to-date") {
					var valueField = Dom.get(control.currentValueHtmlId);
					var prevDate = this.endDate ? this.formatDate(this.endDate) : null;
					this.endDate = Alfresco.util.fromISO8601(valueField.value);
					this.endDateField = {
						id: control.id,
						formId: control.options.formId,
						configName: control.options.fieldId
					};
					this.draw();
					if (this.formatDate(this.endDate) != prevDate) {
						this.requestMembersTime();
					}
				}
			}
		},

		/**
		 *  Смена даты из диаграммы
		 */
		setMemberCalendarDate: function setMemberCalendarDate(layer, args) {
			var date = Alfresco.util.fromISO8601(args[1].date);
			Dom.get(this.startDateField.id + "-date").value = this.formatDate(date);
			Bubbling.fire("handleFieldChange", {
				fieldId: this.startDateField.configName,
				formId: this.startDateField.formId
			});
		},

		/**
		 * Запрос занятости участников
		 */
		requestMembersTime: function requestMembersTime_function() {
			var items = Object.keys(this.selectedItems).join(",");
			if (items == "" || this.startDate == null) return;

			Alfresco.util.Ajax.jsonRequest(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/members/busytime",
					method: "GET",
					dataObj: {
						items: items,
						date: Alfresco.util.formatDate(this.startDate, "yyyy-mm-dd"),
						exclude: this.isNodeRef(this.options.eventNodeRef) ? this.options.eventNodeRef : ""
					},
					successCallback:
					{
						fn: function(response) {
							if (response.json) {
								this.busytime = response.json;
								this.fillBusyTime(response.json);
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
			Dom.removeClass(this.options.controlId + "-diagram", "hidden1");
			var content = Dom.get(this.options.controlId + "-diagram-content");
			content.innerHTML = "";
			var itemNum = 1;
			this.keyIndex = [];
			for (var key in this.selectedItems) {
				var item = this.selectedItems[key];
				var firstColumn = document.createElement("div");
				firstColumn.className = "member-control-diagram-first-cell";
				firstColumn.style.width = this.firstColumnWidth + "px";
				content.appendChild(firstColumn);
				var textCell = document.createElement("div");
				textCell.className = "member-control-diagram-first-cell-text";
				textCell.innerHTML = item.selectedName;
				textCell.title = item.selectedName;
				firstColumn.appendChild(textCell);
				var removeButton = document.createElement("div");
				removeButton.className = "member-control-diagram-first-cell-remove"
				firstColumn.appendChild(removeButton);

				this._drawHourCells(content, itemNum, false);
				this.keyIndex[key] = itemNum;
				itemNum++;
			}
		},

		/**
		 * Рисуем заголовок диаграммы
		 */
		drawDiagramHeader: function drawDiagramHeader_function() {
			if (this.headerIsReady) return;
			var header = Dom.get(this.options.controlId + "-diagram-header");
			this._drawHourCells(header, 0, true);
			this.headerIsReady = true;
			//Устанавливаем размер контейнера
			var cellBounds = this._calculateCell();
			var hours = this.endHour - this.startHour + 1;
			var width = this.firstColumnWidth + hours * cellBounds.width + hours;
			Dom.get(this.options.controlId + "-diagram-container").style.width = width + "px";
		},

		/**
		 * Создаем рамку выбора времени
		 */
		drawSelector: function drawSelector() {
			var startTime = this.startDate;
			var endTime = this.endDate;
			var control = Dom.get(this.options.controlId + "-diagram");
			var region = Dom.getRegion(control);
			if (!this.selector) {
				this.selector = document.createElement("div");
				this.selector.className = "member-control-diagram-selector";
				control.appendChild(this.selector);

				var selectorHead = document.createElement("div");
				selectorHead.style.height = "24px";
				selectorHead.innerHTML = "&nbsp;";
				Dom.addClass(selectorHead, "member-control-diagram-selector-grab");
				selectorHead.addEventListener("mousedown", this.dragStart.bind(this));
				document.body.addEventListener("mouseup", this.dragEnd.bind(this));
				document.body.addEventListener("mousemove", this.moveSelector.bind(this));
				document.body.addEventListener("selectstart", this.selectStart.bind(this));
				this.selector.appendChild(selectorHead);

			} else {
				this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex, true);
			}
			this.selector.style.height = region.height + "px";

			//Расчет индексов старта и конца
			var startHour = startTime.getHours();
			var startMinutes = startTime.getMinutes();
			var endHour = endTime.getHours();
			var endMinutes = endTime.getMinutes();
			var cellBounds = this._calculateCell();
			var startIndex = (startHour - this.startHour) * cellBounds.cellByHour + Math.round(startMinutes / this.timeStep) + 1;

			this.maxIndex = (this.endHour - this.startHour + 1) * cellBounds.cellByHour;
			var days = this.dateDiffInDays(endTime, startTime);
			var endIndex = days * this.maxIndex + (endHour - this.startHour) * cellBounds.cellByHour + Math.round(endMinutes / this.timeStep);

			//Устанавливаем размер и положение рамки
			var left = Dom.getRegion(this.options.controlId + "-diagram_0_"+ startIndex).left;
			var fillIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			var right = Dom.getRegion(this.options.controlId + "-diagram_0_"+ fillIndex).right;
			this.selector.style.left = (left - region.left - 1) + "px";
			this.selector.style.width = (right - left) + "px";

			//расскрашиваем рамку
			this._drawSelectorBorder(startIndex, endIndex);
			this.prevStartIndex = startIndex;
			this.prevEndIndex = endIndex;
		},

		/**
		 * Обработка начала перемещения рамки
		 * @param ev
		 */
		dragStart: function(ev) {
			var head = this.selector.firstChild;
			Dom.removeClass(head, "member-control-diagram-selector-grab");
			Dom.addClass(head, "member-control-diagram-selector-grabbing");
			this.dragEnabled = true;
			this.dragDelta = ev.offsetX;

		},

		/**
		 * Обработка окончания перемещения рамки
		 * @param ev
		 */
		dragEnd: function(ev) {
			if (this.dragEnabled) {
				//Передаем данные в контролы
				var startMinutes = (this.prevStartIndex - 1) * this.timeStep;
				var days = Math.floor(this.prevEndIndex / this.maxIndex);
				var endIndex = this.prevEndIndex - days * this.maxIndex;
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
				var start = new Date(this.startDate.getTime());
				this.startDate = null;
				this.endDate = null;

				//Начало
				Dom.get(this.startDateField.id + "-date").value = this.formatDate(start);
				Dom.get(this.startDateField.id + "-time").value = ('0' + startHour).slice(-2) + ":" + ('0' + startMinutes).slice(-2);
				Bubbling.fire("handleFieldChange", {
					fieldId: this.startDateField.configName,
					formId: this.startDateField.formId
				});

				//Окончание
				start.setHours(this.startHour)
				start.setMinutes(0);
				start.setSeconds(0);
				var endDate = new Date(start.getTime() + days * 24 * 60 * 60 * 1000);
				Dom.get(this.endDateField.id + "-date").value = this.formatDate(endDate);
				Dom.get(this.endDateField.id + "-time").value = ('0' + endHour).slice(-2) + ":" + ('0' + endMinutes).slice(-2);
				Bubbling.fire("handleFieldChange", {
					fieldId: this.endDateField.configName,
					formId: this.endDateField.formId
				});
			}
			var head = this.selector.firstChild;
			Dom.removeClass(head, "member-control-diagram-selector-grabbing");
			Dom.addClass(head, "member-control-diagram-selector-grab");
			this.dragEnabled = false;
		},

		/**
		 * Метод для отмены выделения текста при перетаскивания рамки в диаграмме (не работает)
		 * @param ev
		 * @returns {boolean}
		 */
		selectStart: function(ev) {
			return !this.dragEnabled;
		},

		/**
		 * Действия при перемещении рамки
		 * @param ev
		 */
		moveSelector: function moveSelector_function(ev) {
			if (this.dragEnabled) {
				var cellBounds = this._calculateCell();
				var diagramBounds = Dom.getRegion(this.options.controlId + "-diagram");
				var x = ev.clientX;
				var newX = x - this.dragDelta - this.firstColumnWidth - diagramBounds.x;
				var startIndex = Math.floor((newX + Math.floor(newX / cellBounds.width)) / cellBounds.cellWidth);
				if (startIndex != this.prevStartIndex) {
					var endIndex = startIndex + (this.prevEndIndex - this.prevStartIndex);
					if (startIndex > 0 && startIndex <= this.maxIndex) {
						this._clearSelectorBorder(this.prevStartIndex, this.prevEndIndex);
						this._drawSelectorBorder(startIndex, endIndex);
						var left = Dom.getRegion(this.options.controlId + "-diagram_0_"+ startIndex).left;
						this.selector.style.left = (left - diagramBounds.left - 1) + "px";

						this.prevStartIndex = startIndex;
						this.prevEndIndex = endIndex;
					}
				}
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
			var startDayDate = new Date(this.startDate.getTime());
			startDayDate.setHours(startHour);
			startDayDate.setMinutes(0);
			startDayDate.setSeconds(0);

			var endDayDate = new Date(this.startDate.getTime());;
			endDayDate.setHours(endHour);
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
						startIndex = 0;
					} else {
						var startHour = start.getHours();
						var startMinutes = start.getMinutes();
						startIndex = (startHour - this.startHour) * cellSettings.cellByHour + Math.round(startMinutes / this.timeStep) + 1;
					}

					if (endDayDate >= end) {
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
						cell.style.backgroundColor = "red";
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
		 * Удаление рамки предыдущего выделенного периода
		 * @param startIndex
		 * @param endIndex
		 * @param onlyHeader
		 * @private
		 */
		_clearSelectorBorder: function(startIndex, endIndex, onlyHeader) {
			var fillIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			for (var i = startIndex; i <= fillIndex; i++) {
				var cell = Dom.get(this.options.controlId + "-diagram_0_" + i)
				cell.style.backgroundColor = "transparent";
			}

			if (!onlyHeader) {
				var keysLen = Object.keys(this.selectedItems).length;
				for (var i = 1; i <= keysLen; i++) {
					var leftCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + startIndex);
					leftCell.style.borderLeft = "none";
					leftCell.style.width = (parseInt(leftCell.style.width) + this.selectorBorderWidth) + "px";
					if (endIndex <= this.maxIndex) {
						var rightCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + endIndex);
						rightCell.style.borderRight = "none";
						rightCell.style.width = (parseInt(rightCell.style.width) + this.selectorBorderWidth) + "px";
					}

					if (i == keysLen) {
						for (var j = startIndex; j <= endIndex; j++) {
							if (j > this.maxIndex) {
								break;
							}
							var cell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + j);
							cell.style.borderBottom = "none";
							cell.style.height = (Dom.getRegion(cell).height + this.selectorBorderWidth) + "px";
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
			for (var key in this.busyInterval) {
				var interval = this.busyInterval[key];
				if ((startIndex >= interval.start && startIndex <= interval.end) ||
					(endIndex >= interval.start && endIndex <= interval.end) ||
					(startIndex <= interval.start && endIndex >= interval.end)) {
					isBusy = true;
					break;
				}
			}

			var color = isBusy ? "#FFC90E" : "#22B14C";
			var headerIndex = endIndex > this.maxIndex ? this.maxIndex : endIndex;
			for (var i = startIndex; i <= headerIndex; i++) {
				var cell = Dom.get(this.options.controlId + "-diagram_0_" + i)
				cell.style.backgroundColor = color;
			}

			var keysLen = Object.keys(this.selectedItems).length;
			for (var i = 1; i <= keysLen; i++) {
				var leftCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + startIndex);
				leftCell.style.borderLeft = this.selectorBorderWidth + "px solid " + color;
				leftCell.style.width = (parseInt(leftCell.style.width) - this.selectorBorderWidth) + "px";
				if (endIndex <= this.maxIndex) {
					var rightCell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + endIndex);
					rightCell.style.borderRight = this.selectorBorderWidth + "px solid " + color;
					rightCell.style.width = (parseInt(rightCell.style.width) - this.selectorBorderWidth) + "px";
				}
				if (i == keysLen) {
					for (var j = startIndex; j <= endIndex; j++) {
						if (j > this.maxIndex) {
							break;
						}
						var cell = Dom.get(this.options.controlId + "-diagram_" + i + "_" + j);
						cell.style.height = (Dom.getRegion(cell).height - this.selectorBorderWidth) + "px";
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
					left += cellWidth;
					cell.className = "member-control-diagram-empty-cell";
					cell.innerHTML = "&nbsp";
					cell.id = this.options.controlId + "-diagram_" + lineNum + "_" + ((hour - this.startHour) * cellByHour + i);
					column.appendChild(cell);
				}

				if (isHeader) {
					var textColumn = document.createElement("div");
					textColumn.style.width = (cellWidth * cellByHour) + "px";
					textColumn.className = "member-control-diagram-header-text-cell";
					textColumn.innerHTML = ('0' + hour).slice(-2) + ":00";
					column.appendChild(textColumn);
				}
			}
		},

		/**
		 * Расчет параметров ячеке для отрисовки
		 * @returns {{width: number, cellByHour: number, cellWidth: number}}
		 */
		_calculateCell: function calculateCell_function() {
			var region = Dom.getRegion(this.options.controlId + "-diagram");
			var width = region.width - this.firstColumnWidth;
			var bordersWidth = this.endHour - this.startHour + 1;
			width = Math.floor((width - bordersWidth) / (this.endHour - this.startHour + 1));
			var cellByHour = Math.round(60 / this.timeStep);
			var cellWidth = Math.floor(width / cellByHour);
			return {
				width: width,
				cellByHour: cellByHour,
				cellWidth: cellWidth
			};
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
		}
	}, true);
})();