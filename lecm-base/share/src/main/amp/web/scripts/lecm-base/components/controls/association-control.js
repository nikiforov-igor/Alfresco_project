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
		this.createAssociationControlAutocompleteHelper(options);
		this.createAssociationControlPicker(options, messages);
		this.createAssociationControlItems(messages);

		Bubbling.on('searchProperties', this.onItemSearchProperties, this);
		Bubbling.on('addSelectedItem', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);
		Bubbling.on('loadOriginalItems', this.onLoadOriginalItems, this);
		Bubbling.on('pickerClosed', this.onPickerClosed, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationComplexControl, Alfresco.component.Base, {

		fieldValues: [],

		searchProperties: [],

		autocompleteHelper: null,

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
			autocomplete: null,
			autoCompleteListener: null,
			datasource: null
		},

		_renderSelectedItems: function (selectedItems, options) {

			var ACUtils = LogicECM.module.AssociationComplexControl.Utils;

			function onAddListener(params) {
				Event.on(params.id, 'click', this.onRemove, params, this);
			}

            options = options || this.options;

			selectedItems.forEach(function(selected) {
                if (selected) {
                    var displayName,
                        elementName,
                        elem = document.createElement('div'),
                        id = selected.nodeRef.replace(/:|\//g, '_'),
                        itemId = this.id + '-' + id,
                        notSelected = !Selector.query('[id="' + itemId + '"]', this.widgets.selected, true);

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
                            Event.onAvailable(itemId, onAddListener, {id: itemId, nodeData: selected}, this);
                            if ('lecm-orgstr:employee' === options.itemType) {
                                elementName = ACUtils.getEmployeeAbsenceMarkeredHTML(selected.nodeRef, displayName, true, options.employeeAbsenceMarker, []);
                                elem.innerHTML = BaseUtil.getCroppedItem(elementName, ACUtils.getRemoveButtonHTML(this.id, selected));
                            } else {
                                elem.innerHTML = BaseUtil.getCroppedItem(ACUtils.getDefaultView(options, displayName, selected), ACUtils.getRemoveButtonHTML(this.id, selected));
                            }
                        }
                        this.widgets.selected.appendChild(elem.firstChild);
                    }
                }
			}, this);
            var index, item, removed = [], added = [],
                el = Dom.get(this.id),
                value = el.value.split(',');

            if (this.widgets.removed) {
                for (index in this.fieldValues) {
                    if (this.fieldValues.hasOwnProperty(index)) {
                        item = this.fieldValues[index];
                        if (value.indexOf(item) === -1) {
                            removed.push(item);
                        }
                    }
                }
                this.widgets.removed.value = removed.join(',');
            }
            if (this.widgets.added) {
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
			return this.widgets.selected.childElementCount;
		},

		createAssociationControlAutocompleteHelper: function (options) {
			var conditions;
			if (this.options.showAutocomplete) {
				conditions = this.options.itemsOptions.map(function (item) {
					return item.itemKey;
				});
				this.autocompleteHelper = new Alfresco.util.Deferred(conditions, {
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
			}, this);
			for (i in this.widgets) {
				this.widgets[i].setMessages(messages);
			}
            this.fire('resetOriginalValues', {
                /* Bubbling.fire */
                fieldValues: Dom.get(this.id).value.split(',')
            });
		},

		onLoadOriginalItems: function(layer, args) {
			var original, options, prop, selected = [];
			if (Alfresco.util.hasEventInterest(this, args)) {
				original = args[1].original,
				options = args[1].options;
				for (prop in original) {
					selected.push(original[prop]);
				}
				this._renderSelectedItems(selected, options);
			}
		},

		onItemSearchProperties: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args) && this.options.showAutocomplete) {
				Array.prototype.push.apply(this.searchProperties, args[1].searchProperties);
				this.autocompleteHelper.fulfil(args[1].itemKey);
			}
		},

		onAddSelectedItem: function (layer, args) {
			var nodeData, options, count;
			if (Alfresco.util.hasEventInterest(this, args)) {
				nodeData = args[1].added,
				options = args[1].options,
				count = this._renderSelectedItems([nodeData], options);
				if (this.widgets.autocomplete) {
					if (!this.options.endpointMany && count) {
						Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					} else {
						Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					}
				}
				//посылаем событие, передаем selectedItems либо из однго элемента либо пустой
			}
		},

		onRemoveSelectedItem: function (layer, args) {
			var nodeData, id, el, value, idx, added=[], removed=[], item, index;
			if (Alfresco.util.hasEventInterest(this, args)) {
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
			this.fire('removeSelectedItem', { /* Bubbling.fire */
				removed: params.nodeData
			});
		},

		onPickerClosed: function (layer, args) {
			var removedKeys, selectedArray = [], selectedKeys, i;
            var options = args[1].options;
			if (Alfresco.util.hasEventInterest(this, args)) {
				removedKeys = Object.keys(args[1].removed);
                selectedKeys =Object.keys(args[1].selected);
                for (i = 0; i < removedKeys.length; i++) {
                    this.onRemove(null, {nodeData: args[1].removed[removedKeys[i]]});
                }
                for (i = 0; i < selectedKeys.length; i++) {
                    selectedArray.push(args[1].selected[selectedKeys[i]]);
                }
                Dom.get(this.id).value = Alfresco.util.encodeHTML(selectedKeys.join(','));
                this._renderSelectedItems(selectedArray, options);
				if (this.widgets.autocomplete) {
					if (!this.options.endpointMany && Dom.getChildren(this.widgets.selected).length) {
						Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					} else {
						Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
					}
				}

				//заполняем selected
				/* this.renderSelected(selected) */
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
			var decodedQuery = decodeURIComponent(sQuery),
				searchTerm = this.searchProperties.reduce(function (prev, curr) {
				return prev + (prev.length ? '#' : '') + curr + ':' + decodedQuery;
			}, '');
			Dom.addClass(this.widgets.autocomplete.getInputEl(), 'wait-for-load');
			searchTerm = searchTerm ? searchTerm : 'cm:name:' + decodedQuery;

			return LogicECM.module.AssociationComplexControl.Utils.generateChildrenUrlParams(this.options, searchTerm, 0, true);
		},

		formatResult: function (oResultData, sQuery, sResultMatch) {
			if (!this.options.plane) {
				var name = sResultMatch;
				var path = oResultData[3] + sResultMatch;
				return '<div title="' + path + '">' + name + '</div>';
			} else {
				return sResultMatch;
			}
		},

		doBeforeLoadData: function (sQuery , oResponse , oPayload) {
			var res,
				results = oResponse.results,
				node;

			// Если после нажатия enter возращается только один результат, то он сразу подставляется в поле
			if (this.enterPressed && results && results.length == 1) {
				this.enterPressed = false;
				node = {
					name: results[0].name,
					selectedName: results[0].selectedName,
					nodeRef: results[0].nodeRef,
					path: results[0].path,
					simplePath: results[0].simplePath
				};
				this.fire('addSelectedItem', { /* Bubbling.fire */
					added: node,
					options: this.options
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
			var node = {
				name: aArgs[2][0],
				selectedName: aArgs[2][1],
				nodeRef: aArgs[2][2],
				path: aArgs[2][3],
				simplePath: aArgs[2][4]
			};
			this.fire('addSelectedItem', { /* Bubbling.fire */
				added: node,
				options: this.options
			});
			//надо как-то понять что делать с единичным выбором
			if (this.options.endpointMany) {
				this.widgets.autocomplete.getInputEl().value = '';
			} else {
				if (!this.options.endpointMany && Dom.getChildren(this.widgets.selected).length) {
					Dom.addClass(this.widgets.autocomplete.getInputEl(), 'hidden');
				} else {
					Dom.removeClass(this.widgets.autocomplete.getInputEl(), 'hidden');
				}
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

		enableAutocomplete: function () {
			this.widgets.autoCompleteListener.enable();
		},

		onPickerButtonClick: function (evt, target) {
            this.fire('resetOriginalValues', {
                /* Bubbling.fire */
                fieldValues: Dom.get(this.id).value.split(',')
            });
            this.widgets.picker.show();
		},

		onReady: function () {
			/* загрузка данных по field.value и передача их в picker и в items. Отрисовка selectedItems здесь и в пикере */
			this.widgets.added = Dom.get(this.id + '-added');
			this.widgets.removed = Dom.get(this.id + '-removed');
			this.widgets.selected = Dom.get(this.id + '-displayed');
			this.widgets.pickerButton = new Alfresco.util.createYUIButton(this, 'btn-pick', this.onPickerButtonClick, {
				disabled: this.options.disabled,
				title: this.options.pickerButtonTitle,
				label: this.options.pickerButtonLabel ? this.options.pickerButtonLabel : '...',
				type: 'push'
			});

			if (this.options.showAutocomplete) {
				this.widgets.datasource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI_RELATIVE + this.options.childrenDataSource + '/node/children', {
					responseType: YAHOO.util.DataSource.TYPE_JSON,
					connXhrMode: 'cancelStaleRequests',
					responseSchema: {
						resultsList: 'items',
						fields: ['name', 'selectedName', 'nodeRef', 'path', 'simplePath']
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
		}
	}, true);
})();
