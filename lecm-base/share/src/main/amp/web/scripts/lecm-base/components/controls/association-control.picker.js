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
		this.setOptions(options);
		this.setMessages(messages);
		this.control = control;

		Bubbling.on('addSelectedItem', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);
		Bubbling.on('loadOriginalItems', this.onLoadOriginalItems, this);
		Bubbling.on('afterChange', this.onAfterChange, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationComplexControl.Picker, Alfresco.component.Base, {

		control: null,

		added: {},
		removed: {},
		original: {},
		selected: {},

		options: {
			isComplex: null
		},

		widgets: {
			picker: null,
			okButton: null,
			cancelButton: null
		},

		_renderSelectedItems: function (selectedItems, options) {

			function onAddListener(params) {
				Event.on(params.id, 'click', this.onRemove, params, this);
			}

			selectedItems.forEach(function(selected) {
				var displayName,
				elementName,
				elem = document.createElement('div'),
				id = selected.nodeRef.replace(/:|\//g, '_'),
				itemId = this.id + '-' + id;

				if (options.plane || !options.showPath) {
					displayName = selected.selectedName;
				} else {
					displayName = selected.simplePath + selected.selectedName;
				}

				Event.onAvailable(itemId, onAddListener, { id: itemId, nodeData: selected }, this);

				if ('lecm-orgstr:employee' === options.itemType) {
					elementName = ACUtils.getEmployeeAbsenceMarkeredHTML(selected.nodeRef, displayName, true, options.employeeAbsenceMarker, []);
					elem.innerHTML = BaseUtil.getCroppedItem(elementName, ACUtils.getRemoveButtonHTML(this.id, selected));
					this.widgets.items.appendChild(elem.firstChild);
				} else {
					elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, selected), ACUtils.getRemoveButtonHTML(this.id, selected));
					this.widgets.items.appendChild(elem.firstChild);
				}
			}, this);
			return this.widgets.items;
		},

		onAddSelectedItem: function (layer, args) {
			var nodeData, options, isNew;
			if (Alfresco.util.hasEventInterest(this, args)) {
					nodeData = args[1].added,
					options = args[1].options,
					isNew = !this.original.hasOwnProperty(nodeData.nodeRef);
				if (isNew) {
					this.added[nodeData.nodeRef] = nodeData;
				}
				this.selected[nodeData.nodeRef] = nodeData;
				this._renderSelectedItems([nodeData], options);
			}
		},

		onLoadOriginalItems: function(layer, args) {
			var original, options, prop, selected = [];
			if (Alfresco.util.hasEventInterest(this, args)) {
				original = args[1].original,
				options = args[1].options;
				this.original = YAHOO.lang.merge(this.original, original);
				this.selected = YAHOO.lang.merge(this.selected, original);
				for (prop in original) {
					selected.push(original[prop]);
				}
				this._renderSelectedItems(selected, options);
			}
		},

		onRemove: function(evt, params) {
			this.fire('removeSelectedItem', { /* Bubbling.fire */
				removed: params.nodeData
			});
		},

		onRemoveSelectedItem: function (layer, args) {
			var nodeData, id;
			if (Alfresco.util.hasEventInterest(this, args)) {
				nodeData = args[1].removed;
				id = this.id + '-' + nodeData.nodeRef.replace(/:|\//g, '_');
				if (this.added.hasOwnProperty(nodeData.nodeRef)) {
					delete this.added[nodeData.nodeRef];
				}
				if (this.selected.hasOwnProperty(nodeData.nodeRef)) {
					delete this.selected[nodeData.nodeRef];
				}
				if (this.original.hasOwnProperty(nodeData.nodeRef)) {
					this.removed[nodeData.nodeRef] = nodeData;
				}
				Selector.query('a[id="' + id + '"]', this.widgets.items).forEach(function (el) {
					Event.removeListener(el, 'click');
					this.widgets.items.removeChild(el.parentNode.parentNode);
				}, this);
			}
		},

		onAfterChange: function (layer, args) {
			var key, selectedItems, markers = {};
			if (Alfresco.util.hasEventInterest(this, args)) {
				if (this.options.changeItemsFireAction) {
					key = args[1].key;
					selectedItems = Object.keys(this.selected);
					if (selectedItems.length === 1) {
						markers[selectedItems[0]] = key;
					}
					Bubbling.fire(this.options.changeItemsFireAction, {
						selectedItems: this.selected,
						markers: markers
					});
				}
			}
		},

		onOkButtonClick: function (evt, params) {
			this.hide();
		},

		onCancelButtonClick: function (evt, params) {
			Dom.getChildren(this.widgets.items).forEach(function(elItem) {
				Event.removeListener(elItem, 'click');
				this.widgets.items.removeChild(elItem);
				this.added = {};
				this.removed = {};
				this.selected = YAHOO.lang.merge(this.original);
			}, this);
			this.fire('hide', { /* Bubbling.fire */
				reset: true
			});
			this.hide();
		},

		onSelectButtonClick: function (type, args, menuItem) {
			var i;
			this.widgets.selectButton.set('label', menuItem.value.options.label);
			this.fire('hide', {}); /* Bubbling.fire */
			this.fire('show', { /* Bubbling.fire */
				itemKey: menuItem.value.itemKey
			});
//			for (i in this.control.widgets) {
//				if ('LogicECM.module.AssociationComplexControl.Item' === this.control.widgets[i].name) {
//					this.control.widgets[i].hide();
//				}
//			}
//			this.control.widgets[menuItem.value.itemKey].show();
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
		},

		onReady: function () {
			var menu;

			console.log(this.name + '[' + this.id + '] is ready');
			this.widgets.picker = Alfresco.util.createYUIPanel(this.id, {
				width: '800px',
				close: false
			}, {
				render: true,
				type: YAHOO.widget.Panel
			});
			this.widgets.okButton = Alfresco.util.createYUIButton(this, 'ok', this.onOkButtonClick, {
				type: 'push'
			});
			this.widgets.cancelButton = Alfresco.util.createYUIButton(this, 'cancel', this.onCancelButtonClick, {
				type: 'push'
			});
			if (this.options.isComplex) {

				menu = this.options.itemsOptions.map(function (obj) {
					return {
						text: obj.options.label,
						value: obj,
						onclick: {
							scope: this,
							fn: this.onSelectButtonClick
						}
					};
				}, this);

				this.widgets.selectButton = Alfresco.util.createYUIButton(this, 'select', null, {
					label: 'Тип данных',
					type: 'menu',
					menu: menu,
					lazyloadmenu: false
				});
				this.widgets.selectButton.addClass('button-select');
			}

			this.widgets.items = Dom.get(this.id + '-items');
		}
	}, true);
})();
