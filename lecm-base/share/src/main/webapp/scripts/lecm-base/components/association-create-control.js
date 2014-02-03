/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.AssociationCreateControl = function (htmlId) {
		LogicECM.module.AssociationCreateControl.superclass.constructor.call(this, "AssociationCreateControl", htmlId);

		this.selectedItems = {};
		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationCreateControl, Alfresco.component.Base,
		{
			eventGroup: null,

			singleSelectedItem: null,

			selectedItems: null,

			doubleClickLock: false,

			options: {
				parentNodeRef: null,

				setCurrentValue: true,

				createNewMessage: null, //message id по которому будет сформирован заголовок диалогового окна

				changeItemsFireAction: null,

				selectedValue: null,

				currentValue: "",
				// If control is disabled (has effect in 'picker' mode only)
				disabled: false,
				// If this form field is mandatory
				mandatory: false,
				// If control allows to pick multiple assignees (has effect in 'picker' mode only)
				multipleSelectMode: false,

				initialized: false,

				nameSubstituteString: "{cm:name}",

				// при выборе сотрудника в контроле отображать, доступен ли он в данный момент и если недоступен, то показывать его автоответ
				employeeAbsenceMarker: false,

				createDialogClass: ""
			},

			onReady: function () {
				if (!this.options.initialized) {
					this.options.initialized = true;
					this.init();
				}
			},

			init: function () {
				this.options.controlId = this.id + '-cntrl';
				this.eventGroup = this.options.controlId;

				// Create button if control is enabled
				if (!this.options.disabled) {
					this.widgets.createNewButton = new YAHOO.widget.Button(
						this.options.controlId + "-create-new-button",
						{
							onclick: {
								fn: this.showCreateNewItemWindow,
								obj: null,
								scope: this
							}
						}
					);
				}
				this._loadSelectedItems();
			},

			showCreateNewItemWindow: function () {
				if (this.doubleClickLock) return;
				this.doubleClickLock = true;
				var templateRequestParams = this.generateCreateNewParams(this.options.parentNodeRef, this.options.itemType);
				templateRequestParams["createNewMessage"] = this.options.createNewMessage;

				new Alfresco.module.SimpleDialog("create-new-form-dialog-" + this.eventGroup).setOptions({
					width: "50em",
					templateUrl: "lecm/components/form",
					templateRequestParams: templateRequestParams,
					actionUrl: null,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: this.doBeforeDialogShow,
						scope: this
					},
					onSuccess: {
						fn: function (response) {
							this.addSelectedItem(response.json.persistedObject);
							this.doubleClickLock = false;
						},
						scope: this
					},
					onFailure: {
						fn: function (response) {
							this.doubleClickLock = false;
						},
						scope: this
					}
				}).show();
			},

			_loadSelectedItems: function () {
				var arrItems = "";
				if (this.options.selectedValue != null) {
					arrItems = this.options.selectedValue;
				}
				else if (this.options.currentValue != null && this.isNodeRef(this.options.currentValue)) {
					arrItems = this.options.currentValue;
				}

				var onSuccess = function (response) {
					var items = response.json.data.items,
						item;
					this.selectedItems = {};

					this.singleSelectedItem = null;
					for (var i = 0, il = items.length; i < il; i++) {
						item = items[i];
						if (item.type == this.options.itemType) {
							this.selectedItems[item.nodeRef] = item;

							if (!this.options.multipleSelectMode && this.singleSelectedItem == null) {
								this.singleSelectedItem = item;
							}
						}
					}

					this.updateFormFields();
				};

				var onFailure = function (response) {
					this.selectedItems = null;
				};

				if (arrItems !== "") {
					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
							method: "POST",
							dataObj: {
								items: arrItems.split(","),
								itemValueType: "nodeRef",
								itemNameSubstituteString: this.options.nameSubstituteString,
								selectedItemsNameSubstituteString: this.options.nameSubstituteString
							},
							successCallback: {
								fn: onSuccess,
								scope: this
							},
							failureCallback: {
								fn: onFailure,
								scope: this
							}
						});
				}
				else {
					// if disabled show the (None) message
					this.selectedItems = {};
					this.singleSelectedItem = null;
				}
			},

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

			addSelectedItem: function (nodeRef) {
				var onSuccess = function (response) {
					var items = response.json.data.items,
						item;

					//this.singleSelectedItem = null;
					if (!this.options.multipleSelectMode && items[0]) {
						this.selectedItems = {};
						item = items[0];
						this.selectedItems[item.nodeRef] = item;
						this.singleSelectedItem = items[0];
					} else {
						for (var i = 0, il = items.length; i < il; i++) {
							item = items[i];
							this.selectedItems[item.nodeRef] = item;
						}
					}

					this.updateFormFields();
				};

				var onFailure = function (response) {

				};

				if (nodeRef !== "") {
					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
							method: "POST",
							dataObj: {
								items: nodeRef.split(","),
								itemValueType: "nodeRef",
								itemNameSubstituteString: this.options.nameSubstituteString,
								selectedItemsNameSubstituteString: this.options.nameSubstituteString
							},
							successCallback: {
								fn: onSuccess,
								scope: this
							},
							failureCallback: {
								fn: onFailure,
								scope: this
							}
						});
				}
			},

			generateCreateNewParams: function (nodeRef, itemType) {
				return {
					itemKind: "type",
					itemId: itemType,
					destination: nodeRef,
					mode: "create",
					submitType: "json",
					formId: "association-create-new-node-form",
					showCancelButton: true
				};
			},

			doBeforeDialogShow: function (p_form, p_dialog) {
				var message;
				if (this.options.createNewMessage) {
					message = this.options.createNewMessage;
				} else {
					message = this.msg("dialog.createNew.title");
				}
				p_dialog.dialog.setHeader(message);

				Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
				if (this.options.createDialogClass != "") {
					Dom.addClass(p_dialog.id + "-form-container", this.options.createDialogClass);
				}
				this.doubleClickLock = false;
			},

			removeNode: function (event, params) {
				delete this.selectedItems[params.node.nodeRef];
				this.singleSelectedItem = null;
				if (params.updateForms) {
					this.updateFormFields();
				}
			},

			getEmployeeView: function (employeeNodeRef, displayValue) {
				return "<span class='person'><a href='javascript:void(0);' onclick=\"viewAttributes(\'" + employeeNodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
			},

			getDefaultView: function (displayValue) {
				return "<span class='not-person'>" + displayValue + "</span>";
			},

			getRemoveButtonHTML: function (node, dopId) {
				if (!dopId) {
					dopId = "";
				}
				return '<a href="javascript:void(0);" class="remove-item" id="t-' + this.options.controlId + node.nodeRef + dopId + '"></a>';
			},

			attachRemoveClickListener: function (params) {
				YAHOO.util.Event.on("t-" + this.options.controlId + params.node.nodeRef + params.dopId, 'click', this.removeNode, {
					node: params.node,
					updateForms: params.updateForms
				}, this);
			},

			// Updates all form fields
			updateFormFields: function () {
				var el;
				el = Dom.get(this.options.controlId + "-currentValueDisplay");
				if (el != null) {
					el.innerHTML = '';
					var num = 0;
					for (var i in this.selectedItems) {
						var displayName = this.selectedItems[i].selectedName;

						var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
						if (this.options.disabled) {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML += '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) + ' ' + '</div>';
							} else {
								el.innerHTML += '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + '</div>';
							}
						} else {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML
									+= '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) +
									(this.options.employeeAbsenceMarker ? this.getEmployeeAbsenceMarkerHTML(this.selectedItems[i].nodeRef) : ' ') + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
							} else {
								el.innerHTML
									+= '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
							}
							YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);
						}
					}
				}

				if (!this.options.disabled) {
					var addItems = this.getAddedItems();

					// Update added fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-added");
					if (el != null) {
						el.value = '';
						for (i in addItems) {
							el.value += ( i < addItems.length - 1 ? addItems[i] + ',' : addItems[i] );
						}
					}

					var removedItems = this.getRemovedItems();

					// Update removed fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-removed");
					if (el != null) {
						el.value = '';
						for (i in removedItems) {
							el.value += (i < removedItems.length - 1 ? removedItems[i] + ',' : removedItems[i]);
						}
					}

					var selectedItems = this.getSelectedItems();

					// Update selectedItems fields in main form to pass them between popup and form
					el = Dom.get(this.options.controlId + "-selectedItems");
					if (el != null) {
						el.value = '';
						for (i in selectedItems) {
							el.value += (i < selectedItems.length - 1 ? selectedItems[i] + ',' : selectedItems[i]);
						}
					}

					if (this.options.setCurrentValue && Dom.get(this.id) != null) {
						Dom.get(this.id).value = selectedItems.toString();
					}

					if (this.options.mandatory) {
						YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
					}

					YAHOO.Bubbling.fire("formValueChanged",
						{
							eventGroup: this,
							addedItems: addItems,
							removedItems: removedItems,
							selectedItems: selectedItems,
							selectedItemsMetaData: Alfresco.util.deepCopy(this.selectedItems)
						});
				}
				if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
					YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
						selectedItems: this.selectedItems
					});
				}
			},

			getAddedItems: function () {
				var addedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						if (!(item in currentItems)) {
							addedItems.push(item);
						}
					}
				}
				return addedItems;
			},

			getRemovedItems: function () {
				var removedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in currentItems) {
					if (currentItems.hasOwnProperty(item)) {
						if (!(item in this.selectedItems)) {
							removedItems.push(item);
						}
					}
				}
				return removedItems;
			},

			getSelectedItems: function () {
				var selectedItems = [];

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				return selectedItems;
			},

			getEmployeeAbsenceMarkerHTML: function (nodeRef) {
				var result = '';
				if (this.employeesAvailabilityInformation) {
					var employeeData = this.employeesAvailabilityInformation[nodeRef];
					if (employeeData) {
						if (employeeData.isEmployeeAbsent) {
							var absenceEnd = Alfresco.util.fromISO8601(employeeData.currentAbsenceEnd);
							result += ' <span class="employee-unavailable" title="Будет доступен с ' + leadingZero(absenceEnd.getDate()) + "." + leadingZero(absenceEnd.getMonth() + 1) + "." + absenceEnd.getFullYear() + '"';
						} else {
							result += ' <span class="employee-available"';
							var nextAbsenceStr = employeeData.nextAbsenceStart;
							if (nextAbsenceStr) {
								nextAbsenceDate = Alfresco.util.fromISO8601(nextAbsenceStr);
								result += 'title="Будет недоступен с ' + leadingZero(nextAbsenceDate.getDate()) + "." + leadingZero(nextAbsenceDate.getMonth() + 1) + "." + nextAbsenceDate.getFullYear() + '"';
							}
						}
						result += ">&nbsp;</span>"
					}
				}
				return result;

				function leadingZero(value) {
					var valueStr = value + "";
					if (valueStr.length == 1) {
						return '0' + valueStr;
					} else {
						return valueStr;
					}
				}

			},
			getEmployeesAbsenceInformation: function (items) {
				var requestObj = [];
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					if (item.type === "lecm-orgstr:employee") {
						requestObj.push({"nodeRef": item.nodeRef});
					}
				}

				if (requestObj.length > 0) {
					Alfresco.util.Ajax.request({
						method: "POST",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/getEmployeesAvailabilityInformation",
						requestContentType: "application/json",
						responseContentType: "application/json",
						dataObj: requestObj,
						successCallback: {
							fn: function (response) {
								var result = response.json;
								this.employeesAvailabilityInformation = result;
							},
							scope: this
						}
					});
				}
			},
			showEmployeeAutoAnswerPromt: function (item) {
				var me = this;
				var nodeRef = item.nodeRef;
				var autoAnswerText = this.employeesAvailabilityInformation[nodeRef].answerExtended;
				if (autoAnswerText) {
					Alfresco.util.PopupManager.displayPrompt({
						title: this.msg("title.absence.auto-answer.title"),
						text: autoAnswerText,
						noEscape: true,
						close: false,
						modal: true,
						buttons: [
							{
								text: this.msg("button.ok"),
								handler: function () {
									this.destroy();
								},
								isDefault: true
							},
							{
								text: this.msg("button.cancel"),
								handler: function () {
									this.destroy();
									me.removeNode(null, {
										node: item,
										updateForms: true
									});
								}
							}

						]
					});
				}
			}
		});
})();
