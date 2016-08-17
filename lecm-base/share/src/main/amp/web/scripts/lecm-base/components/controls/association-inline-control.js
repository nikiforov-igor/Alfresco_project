/* global Alfresco, YAHOO */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function () {
	var BaseUtil = LogicECM.module.Base.Util,
		Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector;

	LogicECM.module.AssociationInlineControl = function (containerId, fieldValue, options, messages) {
		this.fieldValues = Alfresco.util.deepCopy(this.fieldValues);
		LogicECM.module.AssociationInlineControl.superclass.constructor.call(this, 'LogicECM.module.AssociationInlineControl', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		if (!this.options.isComplex) {
			this.options = YAHOO.lang.merge(this.options, this.options.itemsOptions[0].options);
		}
		this.eventGroup = BaseUtil.uuid();
		if (fieldValue) {
			this.fieldValues = fieldValue.split(',');
		}
		this.createAssociationControlPicker(options, messages);
		this.createAssociationControlItems(messages);

		Bubbling.on('pickerReady', this.onPickerReady, this);
		Bubbling.on('addSelectedItemToPicker', this.onAddRemoveSelectedItem, this);
		Bubbling.on('removeSelectedItemFromPicker', this.onAddRemoveSelectedItem, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationInlineControl, Alfresco.component.Base, {

		fieldValues: [],

		options: {
			disabled: null,
			changeItemsFireAction: null,
			additionalFilter: '',
			isComplex: null,
			childrenDataSource: 'lecm/forms/picker',
			maxSearchAutocompleteResults: 10,
			showAutocomplete: null,
			pickerButtonTitle: null,
			pickerButtonLabel: null,
			multipleSelectMode: false,
			itemsOptions: []
		},

		widgets: {
		},

		createAssociationControlPicker: function (options, messages) {
			this.widgets.picker = new LogicECM.module.AssociationComplexControl.Picker(this, options, messages);
			this.widgets.picker.eventGroup = this.eventGroup;
		},

		createAssociationControlItems: function (messages) {
			var i;
			this.options.itemsOptions.forEach(function (obj) {
				this.widgets[obj.itemKey] = new LogicECM.module.AssociationComplexControl.Item(this.id + '-picker-' + obj.itemKey, obj.itemKey, obj.options, this.fieldValues, this);
				this.widgets[obj.itemKey].eventGroup = this.eventGroup;
			}, this);
			for (i in this.widgets) {
				this.widgets[i].setMessages(messages);
			}
		},

		onPickerReady: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				this.widgets.picker.render(Dom.get(this.widgets.picker.id).parentNode.parentNode);
				this.widgets.picker.show();
			}
		},

		onAddRemoveSelectedItem: function (layer, args) {
			var picker, addedKeys, removedKeys, selectedKeys;
			if (Alfresco.util.hasEventInterest(this, args)) {
				picker = args[1].eventGroup;
				addedKeys = Object.keys(picker.added);
				removedKeys = Object.keys(picker.removed);
				selectedKeys = Object.keys(picker.selected);
				addedKeys.sort(function (a, b) {
					return picker.added[a].index - picker.added[b].index;
				});
				if (this.widgets.added) {
					this.widgets.added.value = Alfresco.util.encodeHTML(addedKeys.join(','));
				}
				if (this.widgets.removed) {
					this.widgets.removed.value = Alfresco.util.encodeHTML(removedKeys.join(','));
				}
				Dom.get(this.id).value = Alfresco.util.encodeHTML(selectedKeys.join(','));
				this.fire('afterChange', {});
			}
		},

		onReady: function () {
			this.widgets.added = Dom.get(this.id + '-added');
			this.widgets.removed = Dom.get(this.id + '-removed');
		}
	}, true);
})();
