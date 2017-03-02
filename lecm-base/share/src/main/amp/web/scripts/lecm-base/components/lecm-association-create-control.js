/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Util = LogicECM.module.Base.Util;

	LogicECM.module.AssociationCreateControl = function (htmlId) {
		LogicECM.module.AssociationCreateControl.superclass.constructor.call(this, "AssociationCreateControl", htmlId);

		YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemsAdded, this);

		this.selectedItems = [];
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

				sortProp: "cm:name",

				// при выборе сотрудника в контроле отображать, доступен ли он в данный момент и если недоступен, то показывать его автоответ
				employeeAbsenceMarker: false,

				createDialogClass: "",

				fullDelete: false,

				itemTypes: null,

				//определяет можно ли будет менять местами поля
				orderEnabled: false,

				dataSource: "lecm/forms/picker/items"
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
					this.renderCreateMenu();
				}

				this._loadSelectedItems();
			},

			renderCreateMenu: function () {
					if (this.options.itemTypes != null && this.options.itemTypes.length == 1) {
						this.widgets.createNewButton = new YAHOO.widget.Button(
							this.options.controlId + "-create-new-button",
							{
								onclick: {
									fn: this.showCreateDialog,
									obj: this.options.itemTypes[0],
									scope: this
								}
							}
						);
					} else if (this.options.itemTypes.length > 1) {
						Alfresco.util.Ajax.jsonRequest({
							url:Alfresco.constants.PROXY_URI + "lecm/base/types/titles",
							method:Alfresco.util.Ajax.POST,
							dataObj:{
								types: this.options.itemTypes
							},
							successCallback:{
								fn:function(response){
									if (response.json && response.json.length) {
										var menu = [];
										for (var i = 0; i < response.json.length; i++) {
											var type = response.json[i];
											menu.push({
												text: type.title,
												value: type.name,
												onclick: {
													fn: this.onClickMenuButton,
													scope: this
												}
											});
										}
										this.widgets.createNewButton = new YAHOO.widget.Button(
											this.options.controlId + "-create-new-button",
											{
												type: "menu",
												menu: menu
											}
										);
									}
								},
								scope: this
							},
							failureMessage: this.msg("message.failure")
						});
					}
			},

			onClickMenuButton: function (p_sType, p_aArgs, p_oItem) {
				this.showCreateDialog(null, p_oItem.value);
			},

			showCreateDialog: function (e, type) {
				if (this.doubleClickLock) return;

				this.doubleClickLock = true;

				var templateRequestParams = this.generateCreateNewParams(this.options.parentNodeRef, type);
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
					this.selectedItems = [];

					this.singleSelectedItem = null;
					for (var i = 0, il = items.length; i < il; i++) {
						item = items[i];

						this.selectedItems.push(item);

						if (!this.options.multipleSelectMode && this.singleSelectedItem == null) {
							this.singleSelectedItem = item;
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
							url: Alfresco.constants.PROXY_URI + this.options.dataSource,
							method: "POST",
							dataObj: {
								items: arrItems.split(","),
								itemValueType: "nodeRef",
								itemNameSubstituteString: this.options.nameSubstituteString,
								sortProp: this.options.sortProp,
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
					this.selectedItems = [];
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


			onSelectedItemsAdded: function (layer, args) {
				var obj = args[1];
				if (obj && obj.id == this.id && obj.nodeRefs != null) {
			 	    this.addSelectedItem(obj.nodeRefs);
				}
			},

			addSelectedItem: function (nodeRefs) {
				var onSuccess = function (response) {
					var items = response.json.data.items,
						item;

					//this.singleSelectedItem = null;
					if (!this.options.multipleSelectMode && items[0]) {
						this.selectedItems = [];
						item = items[0];
						this.selectedItems.push(item);
						this.singleSelectedItem = items[0];
					} else {
						for (var i = 0, il = items.length; i < il; i++) {
							item = items[i];
							this.selectedItems.push(item);
						}
					}

					this.updateFormFields();
				};

				var onFailure = function (response) {

				};

				if (nodeRefs !== null) {
					nodeRefs = YAHOO.lang.isArray(nodeRefs) ? nodeRefs : [nodeRefs];

					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + this.options.dataSource,
							method: "POST",
							dataObj: {
								items: nodeRefs,
								itemValueType: "nodeRef",
								itemNameSubstituteString: this.options.nameSubstituteString,
								sortProp: this.options.sortProp,
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

				p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);

				Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
				if (this.options.createDialogClass != "") {
					Dom.addClass(p_dialog.id + "-form-container", this.options.createDialogClass);
				}
				this.doubleClickLock = false;
			},

			removeNode: function (event, params) {
				if (params.node != null) {
					var me = this;

					var fnActionDeleteConfirm = function (nodeRef) {
						var url = Alfresco.constants.PROXY_URI + "lecm/base/action/delete?alf_method=delete";
						if (me.options.fullDelete) {
							url += "&full=true&trash=false";
						}

						Alfresco.util.Ajax.jsonRequest(
							{
								method: Alfresco.util.Ajax.POST,
								url: url,
								dataObj: {
									nodeRefs: [nodeRef]
								},
								responseContentType: Alfresco.util.Ajax.JSON,
								successCallback: {
									fn: function (response) {

										for (var i = 0; i < me.selectedItems.length; i++) {
											if (me.selectedItems[i].nodeRef == nodeRef) {
												me.selectedItems.splice(i, 1)
											}
										};

										me.singleSelectedItem = null;
										if (params.updateForms) {
											me.updateFormFields();
										}
									}
								},
								failureMessage: "message.delete.failure",
								execScripts: true
							});
					};

					Alfresco.util.PopupManager.displayPrompt(
						{
							title:this.msg("message.confirm.delete.title"),
							text: this.msg("message.confirm.delete.description"),
							buttons:[
								{
									text:this.msg("button.delete"),
									handler:function () {
										this.destroy();
										fnActionDeleteConfirm.call(me, params.node.nodeRef);
									}
								},
								{
									text:this.msg("button.cancel"),
									handler:function () {
										this.destroy();
									},
									isDefault:true
								}
							]
						});
				}
			},

			upNode: function (event, params) {
				if (params.node != null) {
					var nr = params.node.nodeRef;
					for (var i = 0; i < this.selectedItems.length; i++) {
						if (this.selectedItems[i].nodeRef == nr) {
							if (i != 0) {

								Alfresco.util.Ajax.jsonPost(
									{
										url: Alfresco.constants.PROXY_URI + "/lecm/arm/settings/swapOrders",
										dataObj: {
											firstNodeRef: this.selectedItems[i].nodeRef,
											secondNodeRef: this.selectedItems[i - 1].nodeRef
										},
										failureMessage: "message.failure"
									});



								var temp = this.selectedItems[i];
								this.selectedItems[i] = this.selectedItems[i - 1];
								this.selectedItems[i - 1] = temp;
							}
						}
					};

					this.updateFormFields();
				}
			},

			downNode: function (event, params) {
				if (params.node != null) {
					var nr = params.node.nodeRef;
					for (var i = 0; i < this.selectedItems.length; i++) {
						if (this.selectedItems[i].nodeRef == nr) {
							if (i != this.selectedItems.length - 1) {


								Alfresco.util.Ajax.jsonPost(
									{
										url: Alfresco.constants.PROXY_URI + "/lecm/arm/settings/swapOrders",
										dataObj: {
											firstNodeRef: this.selectedItems[i].nodeRef,
											secondNodeRef: this.selectedItems[i + 1].nodeRef
										},
										failureMessage: "message.failure"
									});

								var temp = this.selectedItems[i];
								this.selectedItems[i] = this.selectedItems[i + 1];
								this.selectedItems[i + 1] = temp;
								break;
							}
						}
					};
					this.updateFormFields();
				}
			},

			getDefaultView: function (node) {
				if (!this.options.disabled) {
					var result = "<span class='not-person'><a href='javascript:void(0);' id='ed-" +
						this.options.controlId + node.nodeRef + "'>" +
						node.selectedName + "</a></span>";

					YAHOO.util.Event.onAvailable("ed-" + this.options.controlId + node.nodeRef, this.attachEditClickListener, node, this);
					return result;
				} else {
					return "<span class='not-person'>" + node.selectedName + "</span>";
				}
			},

			attachEditClickListener: function (node) {
				YAHOO.util.Event.on("ed-" + this.options.controlId + node.nodeRef, 'click', this.editNode, node, this);
			},

			editNode: function (event, node) {
				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
				var templateRequestParams = {
					itemKind:"node",
					itemId: node.nodeRef,
					mode:"edit",
					submitType:"json",
					formId: "",
					showCancelButton: true
				};
				new Alfresco.module.SimpleDialog("arm-element-edit-form").setOptions({
					width:"50em",
					templateUrl: templateUrl,
					templateRequestParams: templateRequestParams,
					actionUrl:null,
					destroyOnHide:true,
					doBeforeDialogShow:{
						fn: function(p_form, p_dialog) {
							p_dialog.dialog.setHeader(this.msg("dialog.edit.title"));

							p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);

							Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
						},
						scope: this
					},
					onSuccess:{
						fn:function (response) {
							this.addSelectedItem(response.json.persistedObject);
						},
						scope:this
					}
				}).show();
			},

			getRemoveButtonHTML: function (node, dopId) {
				if (!dopId) {
					dopId = "";
				}
				return Util.getControlItemRemoveButtonHTML("t-" + this.options.controlId + node.nodeRef + dopId);
			},

			getUpButtonHTML: function(node, dopId) {
				if (!dopId) {
					dopId = "";
				}
				return Util.getControlItemUpButtonHTML("u-" + this.options.controlId + node.nodeRef + dopId);
			},

			getDownButtonHTML: function(node, dopId) {
				if (!dopId) {
					dopId = "";
				}
				return Util.getControlItemDownButtonHTML("d-" + this.options.controlId + node.nodeRef + dopId);
			},

			attachDownClickListener: function (params) {
				YAHOO.util.Event.on("d-" + this.options.controlId + params.node.nodeRef + params.dopId, 'click', this.downNode, {
					node: params.node,
					updateForms: params.updateForms
				}, this);
			},

			attachRemoveClickListener: function (params) {
				YAHOO.util.Event.on("t-" + this.options.controlId + params.node.nodeRef + params.dopId, 'click', this.removeNode, {
					node: params.node,
					updateForms: params.updateForms
				}, this);
			},

			attachUpClickListener: function (params) {
				YAHOO.util.Event.on("u-" + this.options.controlId + params.node.nodeRef + params.dopId, 'click', this.upNode, {
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
					for (var i = 0; i < this.selectedItems.length; i++) {
						if (this.options.disabled) {
							el.innerHTML += Util.getCroppedItem(this.getDefaultView(this.selectedItems[i]));
						} else {
							var UpDownButtons = "";

							if(this.options.orderEnabled) {
								if (i != 0)
									UpDownButtons += this.getUpButtonHTML(this.selectedItems[i], "_c");
								if (i != this.selectedItems.length - 1)
								 	UpDownButtons += this.getDownButtonHTML(this.selectedItems[i], "_c");
							}

							el.innerHTML += Util.getCroppedItem(this.getDefaultView(this.selectedItems[i]), this.getRemoveButtonHTML(this.selectedItems[i], "_c") + UpDownButtons);
							YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);

							if(this.options.orderEnabled) {
								if (i != 0)
									YAHOO.util.Event.onAvailable("u-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c", this.attachUpClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);
								if (i != this.selectedItems.length - 1)
									YAHOO.util.Event.onAvailable("d-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c", this.attachDownClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);
							}
						}
					}
				}

				if (!this.options.disabled) {
					var addItems = this.getAddedItems();

					// Update added fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-added");
					if (el != null) {
						el.value = '';
						for (var i = 0; i < addItems.length; i++) {
							el.value += ( i < addItems.length - 1 ? addItems[i] + ',' : addItems[i] );
						}
					}

					var removedItems = this.getRemovedItems();

					// Update removed fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-removed");
					if (el != null) {
						el.value = '';
						for (var i = 0; i < removedItems.length; i++) {
							el.value += (i < removedItems.length - 1 ? removedItems[i] + ',' : removedItems[i]);
						}
					}

					var selectedItems = this.getSelectedItems();

					// Update selectedItems fields in main form to pass them between popup and form
					el = Dom.get(this.options.controlId + "-selectedItems");
					if (el != null) {
						el.value = '';
						for (var i = 0; i < selectedItems.length; i++) {
							el.value += (i < selectedItems.length - 1 ? selectedItems[i] + ',' : selectedItems[i]);
						}
					}

					if (this.options.setCurrentValue && Dom.get(this.id) != null) {
						Dom.get(this.id).value = selectedItems.toString();
					}

					if (this.options.mandatory) {
						YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
					}

					var tempSelObject = {};

					for (var i = 0; i < this.selectedItems.length; i++) {
						tempSelObject[this.selectedItems[i].nodeRef] = this.selectedItems[i];
					}

					YAHOO.Bubbling.fire("formValueChanged",
						{
							eventGroup: this,
							addedItems: addItems,
							removedItems: removedItems,
							selectedItems: tempSelObject,
							selectedItemsMetaData: Alfresco.util.deepCopy(this.selectedItems)
						});
				}
				if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
					YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
						selectedItems: tempSelObject
					});
				}
			},

			getAddedItems: function () {
				var addedItems = [],
					currentItems = this.options.currentValue.split(",");
				var extists = false;

				for (var i = 0; i < this.selectedItems.length; i++) {
					exists = false;
					for (var j = 0; j < currentItems.length; j++) {
						if(this.selectedItems[i].nodeRef == currentItems[j])
						{
							exists = true;
						}
					}
					if (!exists)
						addedItems.push(this.selectedItems[i].nodeRef);
				}
	
				return addedItems;
			},

			getRemovedItems: function () {
				var removedItems = [],
					currentItems = this.options.currentValue.split(",");

				var extists = false;

				for (var j = 0; j < currentItems.length; j++) {
					exists = false;
					for (var i = 0; i < this.selectedItems.length; i++) {
						if(this.selectedItems[i].nodeRef == currentItems[j])
						{
							exists = true;
						}
					}
					if (!exists)
						removedItems.push(currentItems[j]);
				}

				return removedItems;
			},

			getSelectedItems: function () {
				var selectedItems = [];


				for (var i = 0; i < this.selectedItems.length; i++) {
					selectedItems.push(this.selectedItems[i].nodeRef);
				};

				return selectedItems;
			}
		});
})();
