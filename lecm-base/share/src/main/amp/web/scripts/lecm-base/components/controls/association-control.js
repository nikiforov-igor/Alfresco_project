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

	LogicECM.module.AssociationComplexControl = function (containerId, fieldValue, options, messages) {
		this.fieldValues = Alfresco.util.deepCopy(this.fieldValues);
		this.searchProperties = Alfresco.util.deepCopy(this.searchProperties);
		LogicECM.module.AssociationComplexControl.superclass.constructor.call(this, 'LogicECM.module.AssociationComplexControl', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		if (!this.options.isComplex) {
			this.options = YAHOO.lang.merge(this.options, this.options.itemsOptions[0].options);
		}
		this.eventGroup = BaseUtil.uuid();
		if (fieldValue) {
			this.fieldValues = fieldValue.split(',');
		}
		this.createAssociationControlAutocompleteHelper();
		this.createAssociationControlPicker(options, messages);
		this.createAssociationControlItems(messages);

		Bubbling.on("refreshItemList", this.onRefreshItemList, this);
		Bubbling.on('searchProperties', this.onItemSearchProperties, this);
		Bubbling.on('addSelectedItem', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);
		Bubbling.on('pickerClosed', this.onPickerClosed, this);
        Bubbling.on('loadAllOriginalItems', this.onLoadAllOriginalItems, this);
        Bubbling.on('readonlyControl', this.onReadonlyControl, this);
		Bubbling.on('disableControl', this.onDisableControl, this);
		Bubbling.on('enableControl', this.onEnableControl, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationComplexControl, Alfresco.component.Base, {

		readonly: null,

		fieldValues: [],

		defaultValues: [],

		searchProperties: [],

		autocompleteHelper: null,

		options: {
			fieldId: null,
			formId: null,
			disabled: null,
			changeItemsFireAction: null,
			additionalFilter: '',
			isComplex: null,
			autocompleteDataSource: 'lecm/autocomplete/complex/picker',
			autocompleteDataSourceMethodPost: false,
			dataSourceLogic: 'AND',
			maxSearchAutocompleteResults: 10,
			showAutocomplete: null,
			pickerButtonTitle: null,
			pickerButtonLabel: null,
			endpointMany: false,
			itemsOptions: [],
			sortSelected: false
		},

		widgets: {
			autocomplete: null,
			autoCompleteListener: null,
			datasource: null
		},

        optionsMap: {},

		_renderSelectedItems: function (selectedItems) {
			var ACUtils = LogicECM.module.AssociationComplexControl.Utils;

			function onAddListener(params) {
				Event.on(params.id, 'click', this.onRemove, params, this);
			}

			function existing(item) {
				var itemId, notSelected;
				if (item) {
					itemId = this.id + '-' + item.nodeRef.replace(/:|\//g, '_');
					notSelected = !Selector.query('[id="' + itemId + '"]', this.widgets.selected, true);
				}
				return !!item && notSelected;
			}

			function render(item) {
				var elem = document.createElement('div'),
					options = this.optionsMap[item.key] || this.options,
					itemId = this.id + '-' + item.nodeRef.replace(/:|\//g, '_'),
					displayName = (options.plane || !options.showPath) ? item.selectedName : item.simplePath + item.selectedName,
					elementName;

				if (this.options.disabled || this.readonly) {
					if ('lecm-orgstr:employee' === item.type) {
						elem.innerHTML = BaseUtil.getCroppedItem(BaseUtil.getControlEmployeeView(item.nodeRef, displayName));
					} else {
						elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, item));
					}
					elem.firstChild.id = itemId;
				} else {
					Event.onAvailable(itemId, onAddListener, {id: itemId, nodeData: item}, this);
					if ('lecm-orgstr:employee' === item.type) {
						elementName = ACUtils.getEmployeeAbsenceMarkeredHTML(item.nodeRef, displayName, true, options.employeeAbsenceMarker, []);
						elem.innerHTML = BaseUtil.getCroppedItem(elementName, ACUtils.getRemoveButtonHTML(this.id, item));
					} else {
						elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, item), ACUtils.getRemoveButtonHTML(this.id, item));
					}
				}
				this.widgets.selected.appendChild(elem.firstChild);
			}

			var count, fn;

			if (this.options.sortSelected) {
				selectedItems.filter(existing, this).sort(ACUtils.sortByName).forEach(render, this);
			} else {
				selectedItems.filter(existing, this).forEach(render, this);
			}
			count = this.widgets.selected.childElementCount;
			if (this.widgets.autocomplete) {
				fn = (!this.options.endpointMany && count) ? Dom.addClass : Dom.removeClass;
				fn.call(Dom, this.widgets.autocomplete.getInputEl(), 'hidden');
			}
			return count;
		},

		createAssociationControlAutocompleteHelper: function () {
			var ACUtils = LogicECM.module.AssociationComplexControl.Utils;
			if (this.options.showAutocomplete) {
				this.autocompleteHelper = new Alfresco.util.Deferred(ACUtils.getItemKeys(this.options.itemsOptions), {
					scope: this,
					fn: this.enableAutocomplete
				});
			}
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

				//переопределение методов из options
				if(YAHOO.lang.isFunction(obj.options.getExtSearchQueryFunction)) {
					this.widgets[obj.itemKey]['_fnGetExtSearchQuery'] = obj.options.getExtSearchQueryFunction;
				}
				if(YAHOO.lang.isFunction(obj.options.getArgumentsFromFormFunction)) {
					this.widgets[obj.itemKey]['_fnGetArgumentsFromForm'] = obj.options.getArgumentsFromFormFunction;
				}
			}, this);
			for (i in this.widgets) {
				this.widgets[i].setMessages(messages);
			}
		},

		onDisableControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				if (this.widgets.pickerButton != null) {
					this.widgets.pickerButton.set('disabled', true);
				}
				if (this.widgets.picker) {
					this.widgets.picker.hide();
				}
				if (this.widgets.createNewButton != null) {
					this.widgets.createNewButton.set('disabled', true);
				}
				if (this.widgets.autocomplete) {
					autocompleteInput = this.widgets.autocomplete.getInputEl();
					autocompleteInput.setAttribute('disabled', '');
				}
				if (this.widgets.added) {
					this.widgets.added.set('disabled', true);
				}
				if (this.widgets.removed) {
					this.widgets.added.set('disabled', true);
				}
				var input = Dom.get(this.id);
				if (input) {
					input.disabled = true;
				}
				this.tempDisabled = true;
			}
		},

		onEnableControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				if (!this.options.disabled) {
					if (this.widgets.pickerButton != null) {
						this.widgets.pickerButton.set('disabled', false);
					}
					if (this.widgets.picker) {
						this.widgets.picker.hide();
					}
					if (this.widgets.createNewButton != null) {
						this.widgets.createNewButton.set('disabled', false);
					}
					if (this.widgets.autocomplete) {
						autocompleteInput = this.widgets.autocomplete.getInputEl();
						autocompleteInput.removeAttribute('disabled');
					}
					if (this.widgets.added) {
						this.widgets.added.set('disabled', false);
					}
					if (this.widgets.removed) {
						this.widgets.added.set('disabled', false);
					}
					var input = Dom.get(this.id);
					if (input) {
						input.disabled = false;
					}
				}
				this.tempDisabled = false;
			}
		},
		onReadonlyControl: function(layer, args) {
			var autocompleteInput, fn;
			if (!this.options.disabled && this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				this.readonly = args[1].readonly;
				this.widgets.pickerButton.set('disabled', args[1].readonly);
				if (this.widgets.createNewButton != null) {
					this.widgets.createNewButton.set('disabled', args[1].readonly);
				}
				if (this.widgets.autocomplete) {
					autocompleteInput = this.widgets.autocomplete.getInputEl();
					fn = args[1].readonly ? autocompleteInput.setAttribute : autocompleteInput.removeAttribute;
					fn.call(autocompleteInput, 'disabled', '');
				}
			}
		},

		onLoadAllOriginalItems: function(layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
                this.optionsMap = args[1].optionsMap;
                this._renderSelectedItems(args[1].selected);
			}
		},

		onItemSearchProperties: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args) && this.options.showAutocomplete) {
				Array.prototype.push.apply(this.searchProperties, args[1].searchProperties);
				this.autocompleteHelper.fulfil(args[1].itemKey);
			}
		},

		onAddSelectedItem: function (layer, args) {
			var nodeData, count;
			if (Alfresco.util.hasEventInterest(this, args)) {
				nodeData = args[1].added,
				count = this._renderSelectedItems([nodeData]);
				this.fire('addSelectedItemToPicker', { /* Bubbling.fire */
					added: nodeData,
					key: nodeData.itemKey
				});

				Dom.get(this.id).value = Alfresco.util.encodeHTML(Object.keys(this.widgets.picker.selected).join(','));
				if (this.widgets.added) {
					this.widgets.added.value = Alfresco.util.encodeHTML(Object.keys(this.widgets.picker.added).join(','));
				}
				
				this.fire('afterChange', {});
			}
		},

		onRemoveSelectedItem: function (layer, args) {
			var nodeData, id, el, value, idx, added=[], removed=[], item, index;
			if (Alfresco.util.hasEventInterest(this, args) && !this.options.disabled && !this.readonly) {
				nodeData = args[1].removed;
				id = this.id + '-' + nodeData.nodeRef.replace(/:|\//g, '_');
				Selector.query('[id="' + id + '"]', this.widgets.selected).forEach(function (el) {
					Event.removeListener(el, 'click');
					this.widgets.selected.removeChild(el.parentNode.parentNode);
				}, this);
				if (this.widgets.autocomplete) {
					if (!this.options.endpointMany && Dom.getChildren(this.widgets.selected).length) {
						Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					} else {
						Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					}
				}
				el = Dom.get(this.id);
				value = el.value.split(',');
				idx = value.indexOf(nodeData.nodeRef);
				if (idx > -1) {
					value.splice(idx, 1);
					el.value = value.join(',');
				}
                for (index in this.fieldValues) {
                    if (this.fieldValues.hasOwnProperty(index)) {
                        item = this.fieldValues[index];
                        if (value.indexOf(item) === -1) {
                            removed.push(item);
                        }
                    }
                }
                this.widgets.removed.value = removed.join(',');
                for (index in value) {
                    if (value.hasOwnProperty(index)) {
                        item = value[index];
                        if (this.fieldValues.indexOf(item) === -1) {
                            added.push(item);
                        }
                    }
                }
                this.widgets.added.value = added.join(',');
			}
		},

		onRemove: function(evt, params) {
			this.fire('removeSelectedItem', {
				removed: params.nodeData
			});
		},

		onPickerClosed: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
                var ACUtils = LogicECM.module.AssociationComplexControl.Utils,
					selectedValues = [],
                    selectedKeys = Object.keys(args[1].selected),
                    removedKeys = Object.keys(args[1].removed),
                    addedKeys = Object.keys(args[1].added);

                selectedValues = selectedKeys.map(function(key) {
					return this[key];
				}, args[1].selected).sort(ACUtils.sortByIndex);

				this.widgets.selected.innerHTML = '';
				this._renderSelectedItems(selectedValues);

                addedKeys.sort(function (a, b) {
                    return args[1].added[a].index - args[1].added[b].index;
                });

                if (this.widgets.added) {
                    this.widgets.added.value = Alfresco.util.encodeHTML(addedKeys.join(','));
                }
                if (this.widgets.removed) {
                    this.widgets.removed.value = Alfresco.util.encodeHTML(removedKeys.join(','));
                }

                Dom.get(this.id).value = Alfresco.util.encodeHTML(selectedKeys.join(','));

				if (this.widgets.autocomplete) {
					if (!this.options.endpointMany && Dom.getChildren(this.widgets.selected).length) {
						Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					} else {
						Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					}
				}

                this.fire('afterChange', {});
			}
		},

		doBeforeParseData: function (sRequest, oFullResponse) {
			/* препроцессинг данных для autocomplete */
			var updatedResponse = oFullResponse,
				items, index, item;

			if (oFullResponse) {
				items = oFullResponse.data.items;

				if (this.options.maxSearchAutocompleteResults > -1 && items.length > this.options.maxSearchAutocompleteResults) {
					items = items.slice(0, this.options.maxSearchAutocompleteResults - 1);
				}

				for (index in items) {
					if (items.hasOwnProperty(index)) {
						item = items[index];
						if (item.type === 'cm:category' && item.displayPath.indexOf('/categories/Tags') !== -1) {
							item.type = 'tag';
							oFullResponse.data.parent.type = 'tag';
						}
					}
				}

				updatedResponse = {
					parent: oFullResponse.data.parent,
					items: items
				};
			}

			return updatedResponse;
		},

		generateRequest: function (sQuery) {
			var ACUtils = LogicECM.module.AssociationComplexControl.Utils,
				decodedQuery = decodeURIComponent(sQuery),
				searchTerm = this.searchProperties.reduce(function (prev, curr) {
				return prev + (prev.length ? '#' : '') + curr + ':' + decodedQuery;
			}, '');
			Dom.addClass(this.widgets.autocomplete.getInputEl(), 'wait-for-load');
			searchTerm = searchTerm ? searchTerm : 'cm:name:' + decodedQuery;

			// Форсирование заголовков
			this.widgets.datasource.connMgr.setDefaultPostHeader(Alfresco.util.Ajax.JSON);
			this.widgets.datasource.connMgr.setDefaultXhrHeader(Alfresco.util.Ajax.JSON);
			this.widgets.datasource.connMgr.initHeader("Content-Type", Alfresco.util.Ajax.JSON);

			return ACUtils.generateRequest(this, searchTerm, 0, true);
		},

		formatResult: function (oResultData, sQuery, sResultMatch) {
			var name = oResultData[1].name,
				path;
			if (!this.options.plane) {
				path = oResultData[1].path + name;
				return '<div title="' + path + '">' + name + '</div>';
			} else {
				return name;
			}
		},

		doBeforeLoadData: function (sQuery , oResponse , oPayload) {
			var res,
				results = oResponse.results,
				node;

			// Если после нажатия enter возращается только один результат, то он сразу подставляется в поле
			if (this.enterPressed && results && results.length == 1) {
				this.enterPressed = false;
				node = results[0];
				this.fire('addSelectedItem', { /* Bubbling.fire */
					added: node
				});
				this.widgets.autocomplete.getInputEl().value = '';
				if (!this.options.endpointMany) {
					Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
				}
				res = false;
			} else {
				res = true;
			}
			Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'wait-for-load');
			return res;
		},

		onItemSelect: function (sEvent, aArgs) {
			var node = aArgs[2][1];
			this.fire('addSelectedItem', { /* Bubbling.fire */
				added: node
			});
			//надо как-то понять что делать с единичным выбором
			this.widgets.autocomplete.getInputEl().value = '';
			if (!this.options.endpointMany && Dom.getChildren(this.widgets.selected).length) {
				Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
			} else {
				Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
			}
			if (this.widgets.autocomplete._nDelayID != -1) {
				clearTimeout(this.widgets.autocomplete._nDelayID);
			}
		},

		onAutocomplete: function (sEvent, aArgs, scope) {
			var event = aArgs[1];
			var text = event.target.value;

			if (text && text.length > 2) {
				this.enterPressed = true;
				if (this.widgets.autocomplete._nDelayID != -1) {
					clearTimeout(this.widgets.autocomplete._nDelayID);
				}
				this.widgets.autocomplete.sendQuery(text);
			}
			Event.stopEvent(event);
		},
		onRefreshItemList: function (layer, args)
		{
			// Check the event is directed towards this instance
			if (Alfresco.util.hasEventInterest(this, args) || (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId)) {
				var obj = args[1];
				if (obj) {
					if (obj.additionalFilter) {
						this.options.additionalFilter = obj.additionalFilter;
					}
					if (obj.childrenDataSource) {
						this.options.childrenDataSource = obj.childrenDataSource;
					}
                    var itemParamObjects = [];
                    var controlItemsOptions = this.options.itemsOptions;
                    if (obj.itemsOptions && obj.itemsOptions.length) {
                        itemParamObjects = obj.itemsOptions.filter(function(argOpt) {
                            return controlItemsOptions.some(function(opt){
                                return opt.itemKey == argOpt.itemKey;
                            })
                        })
                    }
                    if (!this.options.isComplex) {
                        var itemObj = obj;
                        itemObj.itemKey = controlItemsOptions[0].itemKey;
                        itemParamObjects.push(itemObj);
                    }
					if (itemParamObjects.length){
                        for (var i = 0; i < itemParamObjects.length; i++) {
                            var item = itemParamObjects[i];
                            item.formId = args[1].formId;
                            item.fieldId = args[1].fieldId;
                            this.fire('refreshControlItemList', item);
                        }
					}

				}
			}
		},
		enableAutocomplete: function () {
			this.widgets.autoCompleteListener.enable();
		},

		onPickerButtonClick: function (evt, target) {
            this.widgets.picker.show();
		},

		onReady: function () {
			/* загрузка данных по field.value и передача их в picker и в items. Отрисовка selectedItems здесь и в пикере */
			this.widgets.added = Dom.get(this.id + '-added');
			if (this.widgets.added && this.widgets.added.value) {
				this.defaultValues = this.widgets.added.value.split(',');
			}
			this.widgets.removed = Dom.get(this.id + '-removed');
			this.widgets.selected = Dom.get(this.id + '-displayed');
			this.widgets.pickerButton = new Alfresco.util.createYUIButton(this, 'btn-pick', this.onPickerButtonClick, {
				disabled: this.options.disabled,
				title: this.options.pickerButtonTitle,
				label: this.options.pickerButtonLabel ? this.options.pickerButtonLabel : '...',
				type: 'push'
			});

			if (this.options.showAutocomplete) {
				this.widgets.datasource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI_RELATIVE + this.options.autocompleteDataSource + '/node/children', {
					connMethodPost: this.options.autocompleteDataSourceMethodPost,
					responseType: YAHOO.util.DataSource.TYPE_JSON,
					connXhrMode: 'cancelStaleRequests',
					responseSchema: {
						resultsList: 'items'
					}
				});
				this.widgets.datasource.doBeforeParseData = this.bind(this.doBeforeParseData);

				this.widgets.autocomplete = new YAHOO.widget.AutoComplete(this.id + '-autocomplete', this.id + '-autocomplete-container', this.widgets.datasource, {
					queryDelay: 3,
					minQueryLength: 3,
					prehighlightClassName: 'yui-ac-prehighlight',
					useShadow: true,
					forceSelection: true
//					_bFocused: true
				});
				this.widgets.autocomplete.generateRequest = this.bind(this.generateRequest);
				this.widgets.autocomplete.formatResult = this.bind(this.formatResult);
				this.widgets.autocomplete.doBeforeLoadData = this.bind(this.doBeforeLoadData);
				this.widgets.autocomplete.itemSelectEvent.subscribe(this.onItemSelect, null, this);

				this.widgets.autoCompleteListener = new YAHOO.util.KeyListener(this.widgets.autocomplete.getInputEl(), {
					keys: [ YAHOO.util.KeyListener.KEY.ENTER ]
				}, {
					scope: this,
					correctScope:true,
					fn: this.onAutocomplete
				}, 'keydown');
			}

			BaseUtil.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
		}
	}, true);
})();
