/* global Alfresco, YAHOO */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {
	var ACUtils = LogicECM.module.AssociationComplexControl.Utils,
		BaseUtil = LogicECM.module.Base.Util,
		Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector;

	LogicECM.module.AssociationComplexControl.Picker = function (control, options, messages) {
		this.added = Alfresco.util.deepCopy(this.added);
		this.removed = Alfresco.util.deepCopy(this.removed);
		this.original = Alfresco.util.deepCopy(this.original);
		this.selected = Alfresco.util.deepCopy(this.selected);
		LogicECM.module.AssociationComplexControl.Picker.superclass.constructor.call(this, 'LogicECM.module.AssociationComplexControl.Picker', control.id + '-picker');
		this.options = Alfresco.util.deepCopy(LogicECM.module.AssociationComplexControl.Picker.prototype.options, {
			copyFunctions: true
		});
		this.setOptions(options);
		this.setMessages(messages);
		this.control = control;

		this.originalItemsDeferred = new Alfresco.util.Deferred(LogicECM.module.AssociationComplexControl.Utils.getItemKeys(this.options.itemsOptions), {
			scope: this,
			fn: this._onOriginalItemsDeferred
		});

		Bubbling.on('addSelectedItemToPicker', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);
		Bubbling.on('removeSelectedItemFromPicker', this.onRemoveSelectedItem, this);
		Bubbling.on('loadOriginalItems', this.onLoadOriginalItems, this);
		Bubbling.on('afterChange', this.onAfterChange, this);
		Bubbling.on('restorePreviousValues', this.onRestorePreviousValues, this);
		Bubbling.on('reInitializeControlPicker', this.onReInitializeControlPicker, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationComplexControl.Picker, Alfresco.component.Base, {

		control: null,

		added: {},
		removed: {},
		original: {},
		selected: {},

		options: {
			disabled: null,
			isComplex: null,
			pickerParams: {
				width: '800px',
				close: false
			},
			pickerCustom: {
				render: true,
				type: YAHOO.widget.Panel
			},
			sortSelected: false
		},

		widgets: {
			picker: null,
			okButton: null,
			cancelButton: null
		},

		optionsMap: {},

		_renderSelectedItems: function (selectedItems) {

			function onAddListener(params) {
				Event.on(params.id, 'click', this.onRemove, params, this);
			}
			if (this.options.sortSelected) {
				selectedItems.sort(LogicECM.module.AssociationComplexControl.Utils.sortByName);
			}
			selectedItems.forEach(function (selected) {
				var displayName,
					elementName,
					elem = document.createElement('div'),
					id = selected.nodeRef.replace(/:|\//g, '_'),
					itemId = this.id + '-' + id,
					notSelected = !Selector.query('[id="' + itemId + '"]', this.widgets.items, true),
					options = this.optionsMap[selected.key] || this.options;

				if (notSelected) {

					if (options.plane || !options.showPath) {
						displayName = selected.selectedName;
					} else {
						displayName = selected.simplePath + selected.selectedName;
					}

					if (this.options.disabled) {
						if ('lecm-orgstr:employee' === options.itemType) {
							elem.innerHTML = BaseUtil.getCroppedItem(BaseUtil.getControlEmployeeView(selected.nodeRef, displayName));
						} else {
							elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, selected));
						}
						elem.firstChild.id = itemId;
					} else {
						Event.onAvailable(itemId, onAddListener, { id: itemId, nodeData: selected }, this);
						if ('lecm-orgstr:employee' === options.itemType) {
							elementName = ACUtils.getEmployeeAbsenceMarkeredHTML(selected.nodeRef, displayName, true, options.employeeAbsenceMarker, []);
							elem.innerHTML = BaseUtil.getCroppedItem(elementName, ACUtils.getRemoveButtonHTML(this.id, selected));
						} else {
							elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, selected), ACUtils.getRemoveButtonHTML(this.id, selected));
						}
					}
					if (!this.options.disabled) {
						this.widgets.items.appendChild(elem.firstChild);
					}
				}
			}, this);
			return this.widgets.items;
		},

		_onOriginalItemsDeferred: function () {
			var selected = [],
				prop;

			for (prop in this.original) {
				selected.push(this.original[prop]);
			}

			selected.sort(LogicECM.module.AssociationComplexControl.Utils.sortByIndex);
			this._renderSelectedItems(selected);
			this.fire('loadAllOriginalItems', {
				selected: selected,
				optionsMap: this.optionsMap
			});
			this.fire('afterChange', {});
		},

		_clearObjectPropsByKey: function (obj, key) {
			Object.keys(obj).forEach(function (prop) {
				if (obj[prop].key == key) {
					delete obj[prop];
				}
			}, this);
		},

		_onCancelDeferred: function() {
			var selectedItems = [], ind;
			for (ind in this.selected) {
				selectedItems.push(this.selected[ind]);
			}
			selectedItems.sort(LogicECM.module.AssociationComplexControl.Utils.sortByIndex);
			this._renderSelectedItems(selectedItems);
		},

		onAddSelectedItem: function (layer, args) {
			var nodeData, key;
			if (Alfresco.util.hasEventInterest(this, args)) {
					nodeData = args[1].added;
					key = args[1].key;
				if (this.removed.hasOwnProperty(nodeData.nodeRef)) {
                    delete this.removed[nodeData.nodeRef];
                }
                if (this.original.hasOwnProperty(nodeData.nodeRef)) {
                    delete this.added[nodeData.nodeRef];
                } else {
                    this.added[nodeData.nodeRef] = nodeData;
                }
				this.selected[nodeData.nodeRef] = nodeData;
				this.selected[nodeData.nodeRef].key = key;
				this.selected[nodeData.nodeRef].index = Object.keys(this.selected).length - 1;
				this._renderSelectedItems([nodeData]);
				this.fire('addItemToControlItems', args[1]);
			}
		},

		onLoadOriginalItems: function(layer, args) {
			var original, options, prop, key;
			if (Alfresco.util.hasEventInterest(this, args)) {
				original = args[1].original;
				options = args[1].options;
				key = args[1].key;
				for (prop in original) {
					original[prop].key = key;
				}
				this.original = YAHOO.lang.merge(this.original, original);
				this.selected = YAHOO.lang.merge(this.selected, original);

				if (!this.optionsMap[key]) {
					this.optionsMap[key] = options;
				}
				this.originalItemsDeferred.fulfil(key);
			}
		},

		onRestorePreviousValues: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				var original = args[1].original,
					selected = args[1].selected,
					key = args[1].key,
					originalKeys, selectedKeys, addedKeys, removedKeys;

				this._clearObjectPropsByKey(this.selected, key);
				this._clearObjectPropsByKey(this.removed, key);
				this._clearObjectPropsByKey(this.added, key);

				this.selected = YAHOO.lang.merge(this.selected, selected);
				this.added = {};
				this.removed = {};

				originalKeys = Object.keys(original);
				selectedKeys = Object.keys(selected);

				addedKeys = selectedKeys.filter(function (prop) {
					return !this.original[prop];
				}, this);

				removedKeys = originalKeys.filter(function (prop) {
					return !this.selected[prop];
				}, this);

				addedKeys.forEach(function (prop) {
					this.added[prop] = this.selected[prop];
				}, this);

				removedKeys.forEach(function (prop) {
					this.removed[prop] = this.original[prop];
				}, this);

				this.cancelDeferred.fulfil(key);
			}
		},

		onRemove: function(evt, params) {
			this.fire('removeSelectedItemFromPicker', { /* Bubbling.fire */
				removed: params.nodeData
			});
		},

		onRemoveSelectedItem: function (layer, args) {
			var nodeData, id, prop;
			if (Alfresco.util.hasEventInterest(this, args)) {
				nodeData = args[1].removed;
				id = this.id + '-' + nodeData.nodeRef.replace(/:|\//g, '_');
				if (this.selected.hasOwnProperty(nodeData.nodeRef)) {

					delete this.selected[nodeData.nodeRef];

					if (this.added.hasOwnProperty(nodeData.nodeRef)) {
						delete this.added[nodeData.nodeRef];
					} else if (this.original.hasOwnProperty(nodeData.nodeRef)) {
						this.removed[nodeData.nodeRef] = nodeData;
					}

					for (prop in this.selected) {
						if (this.selected[prop].index > nodeData.index) {
							this.selected[prop].index--;
						}
					}

					Selector.query('[id="' + id + '"]', this.widgets.items).forEach(function (el) {
						Event.removeListener(el, 'click');
						this.widgets.items.removeChild(el.parentNode.parentNode);
					}, this);
				}



			}
		},

		onAfterChange: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				if (this.options.changeItemsFireAction) {
					var selectedItems = Object.keys(this.selected);

					var params = {
						selectedItems: this.selected,
						marker: null,
						formId: this.options.formId || null,
						fieldId: this.options.fieldId || null
					};

					if (selectedItems.length === 1) {
						params.marker = this.selected[selectedItems[0]].key;
					}

					Bubbling.fire(this.options.changeItemsFireAction, params);
				}
				Bubbling.fire("mandatoryControlValueUpdated", this);
			}
		},

		onOkButtonClick: function (evt, params) {
			this.hide();
		},

		onCancelButtonClick: function (evt, params) {
            this.widgets.picker.hide();
			Dom.getChildren(this.widgets.items).forEach(function(elItem) {
				Event.removeListener(elItem, 'click');
				this.widgets.items.removeChild(elItem);
			}, this);
			this.cancelDeferred = new Alfresco.util.Deferred(LogicECM.module.AssociationComplexControl.Utils.getItemKeys(this.options.itemsOptions), {
				scope: this,
				fn: this._onCancelDeferred
			});
			this.fire('hide', {
				reset: true
			});
		},

		onSelectButtonClick: function (type, args, menuItem) {
			this.widgets.selectButton.set('label', Alfresco.util.message(menuItem.value.options.label));
			this.fire('hide', {});
			this.fire('show', {
				itemKey: menuItem.value.itemKey
			});
		},

		render: function (appendToNode) {
			this.widgets.picker.render(appendToNode);
		},

		show: function () {
			var menuItem;
			if (this.widgets.selectButton) {
				menuItem = this.widgets.selectButton.getMenu().getItem(0);
				menuItem.clickEvent.fire();
			} else {
				this.fire('show', { /* Bubbling.fire */
					itemKey: this.options.itemsOptions[0].itemKey
				});
			}
			this.widgets.picker.show();
		},

		hide: function () {
			this.widgets.picker.hide();
			this.fire('pickerClosed', { /* Bubbling.fire */
				added: this.added,
				removed: this.removed,
				selected: this.selected
			});
			this.fire('hide', {
				reset: false
			});
		},

		onReady: function () {
			var menu;

			this.widgets.picker = Alfresco.util.createYUIPanel(this.id, this.options.pickerParams, this.options.pickerCustom);
			this.widgets.okButton = Alfresco.util.createYUIButton(this, 'ok', this.onOkButtonClick, {
				disabled: this.options.disabled,
				type: 'push'
			});
			this.widgets.cancelButton = Alfresco.util.createYUIButton(this, 'cancel', this.onCancelButtonClick, {
				disabled: this.options.disabled,
				type: 'push'
			});
			if (this.options.isComplex) {

				menu = this.options.itemsOptions.map(function (obj) {
					return {
						text: Alfresco.util.message(obj.options.label),
						value: obj,
						onclick: {
							scope: this,
							fn: this.onSelectButtonClick
						}
					};
				}, this);

				this.widgets.selectButton = Alfresco.util.createYUIButton(this, 'select', null, {
					disabled: this.options.disabled,
					label: 'Тип данных',
					type: 'menu',
					menu: menu,
					lazyloadmenu: false
				});
				this.widgets.selectButton.addClass('button-select');
			}

			this.widgets.items = Dom.get(this.id + '-items');
			this.fire('pickerReady', {});
		},
		onReInitializeControlPicker: function(layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				var options = args[1].options;
				if (options) {
					this.setOptions(options);
				}
				this.added = {};
				this.removed = {};
				this.original = {};
				this.selected = {};
				this.originalItemsDeferred = new Alfresco.util.Deferred(LogicECM.module.AssociationComplexControl.Utils.getItemKeys(this.options.itemsOptions), {
					scope: this,
					fn: this._onOriginalItemsDeferred
				});
			}
		}
	}, true);
})();
