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
		Event = YAHOO.util.Event;

	var IDENT_CREATE_NEW = "~CREATE~NEW~";

	LogicECM.module.AssociationComplexControl.Item = function (containerId, key, options, fieldValues, parentControl) {
		this.currentState = Alfresco.util.deepCopy(this.currentState); // Initialise default prototype properties
		LogicECM.module.AssociationComplexControl.Item.superclass.constructor.call(this, 'LogicECM.module.AssociationComplexControl.Item', containerId);
		this.setOptions(options);
		this.key = key;
		this.parentControl = parentControl;
		this.loadHelper = Alfresco.util.Deferred(['rootNode', 'searchProperties', 'ready', 'show'], {
			scope: this,
			fn: this.loadData
		});
		this._loadRootNode();
		this._loadSearchProperties();
		this._loadOriginalValues(fieldValues);

		Bubbling.on('show', this.onShow, this);
		Bubbling.on('hide', this.onHide, this);
		Bubbling.on('addItemToControlItems', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);
		Bubbling.on('removeSelectedItemFromPicker', this.onRemoveSelectedItemFromPicker, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationComplexControl.Item, Alfresco.component.Base, {

		key: null,

		loadHelper: null,

		rootNodeData: null,

		searchProperties: null,

		currentState: {
			original: {},
			selected: {}, //выбранные элементы, текущее состояние
			temporarySelected: {}, //выбранные в пикере (до нажатия ОК)
			nodeData: null,
			skipItemsCount: null,
			searchTerm: null,
			exSearchFilter: null,
			exSearchFormId: null,
			loadingInProcess: false
		},

		options: {
			disabled: null,
			ignoreNodesInTreeView: true,
			itemType: 'cm:content',
			showParentNodeInTreeView: true,
			childrenDataSource: 'lecm/forms/picker',
			treeBranchesDatasource: 'lecm/components/association-tree',
			treeNodeSubstituteString: '',
			treeNodeTitleSubstituteString: '',
			treeItemType: null,
			ignoreNodes: null,
			allowedNodes: null,
			allowedNodesScript: null,
			minSearchTermLength: 3,
			sortProp: 'cm:name',
			nameSubstituteString: 'cm:name',
			additionalFilter: '',
			multipleSelectMode: false,
			useObjectDescription: false,
			checkType: true,
			pickerItemsScript: 'lecm/forms/picker/items',
			showCreateNewLink: false,
			hasAspects: null,
			hasNoAspects: null,
			showExSearch: false,
			fillFormFromSearch: false,
			showEditButton: false
		},

		widgets: {
			search: null,
			exSearch: null,
			searchButton: null,
			exSearchButton: null,
			exSearchClearButton: null,
			searchListener: null,
			exSearchListener: null,
			treeView: null,
			datatable: null,
			datasource: null
		},

		stateParams: {
			doubleClickLock: false,
			isSearch: false,
			alreadyShowCreateNewLink: false
		},

		_nameFormatter: function (elCell, record, column, data) {
			/* форматтер для колонки датагрида, где отображаются данные */
			/* this == this.widgets.datatable */
			function renderItem (item, template) {
				return YAHOO.lang.substitute(template, item, function (key, value, metadata) {
					return Alfresco.util.encodeHTML(value);
				});
			}

			var template = '',
				msg;

			// Create New item cell type
			if (IDENT_CREATE_NEW === record.getData('type')) {
				msg = this.owner.options.createNewMessage ? this.owner.options.createNewMessage : this.owner.msg('form.control.object-picker.create-new');
				elCell.innerHTML = '<a id="' + this.owner.id + '-create-new-row' + '" href="javascript:void(0);" title="' + msg + '" class="create-new-row create-new-item-' + this.owner.eventGroup + '" >' + msg + '</a>';

				YAHOO.util.Event.onContentReady(this.owner.id + '-create-new-row', function (record) {
					var createLink = YAHOO.util.Dom.get(this.owner.id + '-create-new-row');
					if (createLink) {
						var tr = this.getTrEl(record);
						if (tr) {
							tr.hidden =  !ACUtils.canItemBeSelected(IDENT_CREATE_NEW, this.owner.options, this.owner.currentState.temporarySelected, this.owner.parentControl);
						}
						YAHOO.util.Event.on(createLink, 'click', this.owner._fnCreateNewItemHandler, this.owner, true);
					}
				}, record, this, true);

				return;
			}

			if ('lecm-orgstr:employee' === record.getData('type')) {
				template += '<h3 class="item-name">' + BaseUtil.getControlEmployeeView(record.getData('nodeRef'), '{name}', true) + '</h3>';
			} else {
				if (this.owner.options.showAssocViewForm) {
					template += '<h3 class="item-name">' + BaseUtil.getControlValueView(record.getData('nodeRef'), '{name}', '{name}') + '</h3>';
				} else {
					template += '<h3 class="item-name">{name}</h3>';
				}
			}

			if (!this.owner.options.compactMode) {
				template += '<div class="description">{description}</div>';
			}

			elCell.innerHTML = renderItem(record.getData(), template);
		},

		_addFormatter: function (elCell, record, column, data) {
			/* форматтер для колонки датагрида, где отображаются действия */
			/* this == this.widgets.datatable */
			var nodeRef, style = '', hidden;
//			Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

//			var containerId = Alfresco.util.generateDomId();

			if (record.getData('selectable')) {
				nodeRef = record.getData('nodeRef');
				hidden = ACUtils.canItemBeSelected(nodeRef, this.owner.options, this.owner.currentState.temporarySelected, this.owner.parentControl) ? '' : ' hidden ';

				elCell.innerHTML = '<a href="javascript:void(0);"' + hidden + 'title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><i class="icon-plus"></i></a>';

//				elCell.innerHTML = '<a href="javascript:void(0);" ' + style + ' class="add-item add-' + this.owner.eventGroup + '" title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
//				elCell.innerHTML = '<a href="javascript:void(0);" ' + style + ' class="add-item" title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
//				scope.addItemButtons[nodeRef] = containerId;
			}
		},

		_editFormatter: function (elCell, record, column, data) {
			var nodeRef, hidden;
			if (record.getData('selectable') && record.getData("hasWritePermission")) {
				nodeRef = record.getData('nodeRef');
				hidden = ACUtils.canItemBeSelected(nodeRef, this.owner.options, this.owner.currentState.temporarySelected, this.owner.parentControl) ? '' : ' hidden ';

				elCell.innerHTML = '<a href="javascript:void(0);"' + hidden + 'title="' + this.owner.msg('actions.edit') + '" tabindex="0"><i class="icon-edit"></i></a>';
			}
		},

		_loadRootNode: function () {
			/* получение информации по основной ноде (по основному пути на который настроен контрол) */
			function onSuccess (successResponse) {
				var oResults = successResponse.json;
				this.rootNodeData = {
					label: oResults.title,
					nodeRef: oResults.nodeRef,
					isLeaf: oResults.isLeaf,
					type: oResults.type,
					isContainer: oResults.isContainer,
					hasPermAddChildren: oResults.hasPermAddChildren,
					displayPath: oResults.displayPath,
					path: oResults.path,
					simplePath: oResults.simplePath,
					renderHidden: true
				};
				this.loadHelper.fulfil('rootNode');
			}

			function onFailure (failureResponse) {
				this.widgets.datatable.set('MSG_ERROR', failureResponse.json.message);
				this.widgets.datatable.showTableMessage(failureResponse.json.message, YAHOO.widget.DataTable.CLASS_ERROR);
			}

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/forms/node/search',
				dataObj: {
					titleProperty: 'cm:name', //this.options.treeRoteNodeTitleProperty
					xpath: this.options.rootLocation
				},
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureCallback: {
					scope: this,
					fn: onFailure
				}
			});
		},

		_loadNodes: function (node, fnLoadComplete) {
			/* получение данных для дерева */
			function onSuccess (successResponse) {
				var oResults = successResponse.json;
				oResults.forEach(function (item) {
					var nodeRef = item.nodeRef;
					var ignore = this.options.ignoreNodesInTreeView && this.options.ignoreNodes.indexOf(nodeRef) >= 0;
					if (!ignore) {
						new YAHOO.widget.TextNode({
							label: item.label,
							title: item.title,
							nodeRef: item.nodeRef,
							isLeaf: item.isLeaf,
							type: item.type,
							isContainer: item.isContainer,
							hasPermAddChildren: item.hasPermAddChildren,
							renderHidden:true
						}, node).setDynamicLoad(this.bind(this._loadNodes));
					}
				}, this);
				if (YAHOO.lang.isFunction(fnLoadComplete)) {
					fnLoadComplete.call(node);
				}
			}

			function onFailure (failureResponse) {
				this.widgets.datatable.set('MSG_ERROR', failureResponse.json.message);
				this.widgets.datatable.showTableMessage(failureResponse.json.message, YAHOO.widget.DataTable.CLASS_ERROR);
				if (YAHOO.lang.isFunction(fnLoadComplete)) {
					fnLoadComplete.call(node);
				}
			}

			var params = {
				nodeSubstituteString: this.options.treeNodeSubstituteString,
				nodeTitleSubstituteString: this.options.treeNodeTitleSubstituteString,
				selectableType: this.options.treeItemType ? this.options.treeItemType : this.options.itemType,
			};

			if (this.options.hasAspects) {
				params.hasAspects = this.options.hasAspects;
			}
			if (this.options.hasNoAspects) {
				params.hasNoAspects = this.options.hasNoAspects;
			}

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + this.options.treeBranchesDatasource + '/' + node.data.nodeRef.replace("://", "/") + '/items',
				dataObj: params,
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureCallback: {
					scope: this,
					fn: onFailure
				}
			});
		},

		_loadSearchProperties: function () {
			/* получение данных для поиска */
			function onSuccess (successResponse) {
				var columns = successResponse.json.columns;
				this.searchProperties = columns.reduce(function (prev, curr) {
					if ('text' === curr.dataType || 'mltext' === curr.dataType) {
						prev.push(curr.name);
					}
					return prev;
				}, []);
				this.loadHelper.fulfil('searchProperties');
				this.fire('searchProperties', { /* Bubbling.fire */
					itemKey: this.key,
					searchProperties: this.searchProperties
				});
			}

			function onFailure (failureResponse) {
//				title: this.msg("message.error.columns.title"),
//				text: this.msg("message.error.columns.description")
				this.widgets.datatable.set('MSG_ERROR', failureResponse.json.message);
				this.widgets.datatable.showTableMessage(failureResponse.json.message, YAHOO.widget.DataTable.CLASS_ERROR);

			}

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/datagrid/config/columns',
				dataObj: {
					formId: 'searchColumn',
					itemType: this.options.itemType
				},
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureCallback: {
					scope: this,
					fn: onFailure
				}
			});
		},

		_loadOriginalValues: function (fieldValues) {

			function onSuccess(successResponse) {
				var items = successResponse.json.data.items,
					checkType = this.options.checkType,
					itemType = this.options.itemType;

				this.currentState.original = {};

				items.forEach(function (item, i) {
					if (!checkType || item.type === itemType) {
						item.index = i;
						this.currentState.original[item.nodeRef] = item;
					}
				}, this);

				this.currentState.selected = YAHOO.lang.merge(this.currentState.original);
				this.currentState.temporarySelected = YAHOO.lang.merge(this.currentState.original);
				this.fire('loadOriginalItems', {
					original: this.currentState.temporarySelected,
					options: this.options,
					key: this.key
				});
			}

			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI_RELATIVE + this.options.pickerItemsScript,
				dataObj: {
					items: fieldValues,
					itemValueType: 'nodeRef',
					itemNameSubstituteString: this.options.nameSubstituteString,
					selectedItemsNameSubstituteString: ACUtils.getSelectedItemsNameSubstituteString(this.options),
					pathRoot: this.options.rootLocation,
					pathNameSubstituteString: this.options.treeNodeSubstituteString,
					useObjectDescription: this.options.useObjectDescription
				},
				successCallback: {
					scope: this,
					fn: onSuccess
				}
			});
		},

		loadData: function () {
			/* наполнение дерева и датагрида данным при открытии пикера */
			this.currentState.nodeData = this.rootNodeData;
			if (this.options.showSearch) {
				this.widgets.searchListener.enable();
			}
			if (this.options.showExSearch) {
				this.widgets.exSearchListener.enable();
			}
			if (!this.options.plane) {
				this.widgets.treeRoot.data = this.rootNodeData;
				if (this.options.showParentNodeInTreeView) {
					this.widgets.treeRoot.setUpLabel(this.rootNodeData);
				}
				this.widgets.treeView.draw();
				this.widgets.treeView.onEventToggleHighlight(this.widgets.treeRoot);
			}
			this.widgets.datatable.set('MSG_EMPTY', this.msg('label.loading'));
			this.widgets.datatable.showTableMessage(this.msg('label.loading'), YAHOO.widget.DataTable.CLASS_EMPTY);
			this.loadTableData(true, '');

		},

		loadTableData: function (initializeTable, searchTerm, exSearchFilter) {
			/* заполнение датагрида данными */
			function onSuccess(sRequest, oResponse, oArgument) {
				var initializeTable = oArgument.initializeTable,
					oPayload = oArgument.oPayload;
				this.currentState.skipItemsCount += oResponse.results.length;
				this.widgets.datatable.set('MSG_EMPTY', this.msg('form.control.object-picker.items-list.empty'));
				if (initializeTable) {
					this.widgets.datatable.onDataReturnInitializeTable(sRequest, oResponse, oPayload);
				} else {
					this.widgets.datatable.onDataReturnAppendRows(sRequest, oResponse, oPayload);
				}
				this.stateParams.alreadyShowCreateNewLink = true;
				this.currentState.loadingInProcess = false;
			}

			function onFailure(sRequest, oResponse) {
				var response;

				this.currentState.loadingInProcess = false;
				if (401 === oResponse.status) {
					window.location.reload();
				} else {
					try {
						response = JSON.parse(oResponse.responseText);
						this.widgets.datatable.set('MSG_ERROR', response.message);
						this.widgets.datatable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
						this.widgets.dataTable.onDataReturnInitializeTable(sRequest, oResponse); //???
					} catch (e) {

					}
				}
			}
			var params;
			if (initializeTable) {
				this.stateParams.alreadyShowCreateNewLink = false;
			}
			this.currentState.skipItemsCount = initializeTable ? 0 : this.currentState.skipItemsCount;
			this.currentState.searchTerm = searchTerm;
			this.currentState.exSearchFilter = exSearchFilter;
			params = ACUtils.generateChildrenUrlParams(this.options, this.currentState.searchTerm, this.currentState.skipItemsCount, false, this.currentState.exSearchFilter);
			this.widgets.datatable.load({
				request: this.currentState.nodeData.nodeRef.replace('://', '/') + '/children' + params,
				callback: {
					scope: this,
					argument: {
						initializeTable: initializeTable,
						oPayload: this.widgets.datatable.getState()
					},
					success: onSuccess,
					failure: onFailure
				}
			});
		},

		onExSearch: function () {
			var obj, strname, event;

			if (arguments.length > 1) {
				obj = arguments[1];
				strname = Object.prototype.toString.call(obj);
				if (strname.indexOf('Event') >= 0) {
					event = obj;
				}
				if (strname.indexOf('Array') >= 0 && obj.length === 2) {
					event = obj[1];
				}
			}

			var exSearchFilter = '';
			if (this.widgets.exSearch && this.currentState.exSearchFormId) {
				var currentForm = Dom.get(this.currentState.exSearchFormId);
				if (currentForm) {
					exSearchFilter = this._fnGetExtSearchQuery(currentForm);
				}
			}

			if (exSearchFilter) {
				this.stateParams.isSearch = true;
				this.loadTableData(true, this.currentState.searchTerm, exSearchFilter);
			} else if ('' === exSearchFilter) {
				this.stateParams.isSearch = !this.currentState.searchTerm;
				this.loadTableData(true, this.currentState.searchTerm, '');
			}

			if (event) {
				Event.stopEvent(event);
			}
		},

		onExSearchClear: function() {
			this._loadSearchForm();
			this.loadTableData(true, this.currentState.searchTerm, '');
		},

		onSearch: function () {
			/* обработка поиска */
			/* если нажали на кнопку, то arguments это MouseEvent, YAHOO.widget.Button */
			/* если нажали enter, то argyments это "keyPressed", Array[2], LogicECM.module.AssociationComplexControl.Item */
			var obj, strname, event,
				searchData = this.widgets.search.value,
				searchTerm;

			if (arguments.length > 1) {
				obj = arguments[1];
				strname = Object.prototype.toString.call(obj);
				if (strname.indexOf('Event') >= 0) {
					event = obj;
				}
				if (strname.indexOf('Array') >= 0 && obj.length === 2) {
					event = obj[1];
				}
			}

			if (searchData && searchData.length >= this.options.minSearchTermLength) {
				this.stateParams.isSearch = true;
				searchTerm = this.searchProperties.reduce(function (prev, curr) {
					return prev + (prev.length ? '#' : '') + curr + ':' + searchData.replace(/#/g, '');
				}, '');
				searchTerm = searchTerm ? searchTerm : 'cm:name:' + searchData;
				this.loadTableData(true, searchTerm, this.currentState.exSearchFilter);
			} else if ('' === searchData) {
				this.stateParams.isSearch = !this.currentState.exSearchFilter;
				this.loadTableData(true, '', this.currentState.exSearchFilter);
			} else {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('form.control.object-picker.search.enter-more', this.options.minSearchTermLength)
				}, YAHOO.util.Dom.get(this.id));
			}

			if (event) {
				Event.stopEvent(event);
			}
		},

		onExpandComplete: function (node, tree) {
			/* обработка распахивания дерева */
		},

		onTreeNodeClicked: function (obj, tree) {
			/* выбор элемента в дереве и обновление датагрида */
			this.currentState.nodeData = obj.node.data;
			this.loadTableData(true, '');
			return tree.onEventToggleHighlight(obj);
		},

		doBeforeParseData: function (sRequest, oFullResponse) {
			/* препроцессинг данных для датагрида */
			//maxSearchResults
			//createNew
			//categories tags
			//employeeAbsenceMarker
			var updatedResponse = oFullResponse;

			if (oFullResponse) {
				var items = oFullResponse.data.items;

				// Add the special "Create new" record if required
				if (this.options.showCreateNewLink && this.currentState.nodeData != null
					&& this.currentState.nodeData.isContainer && this.currentState.nodeData.hasPermAddChildren
					&& (!this.stateParams.isSearch || this.options.plane)
					&& !this.stateParams.alreadyShowCreateNewLink) {
					items = [{type: IDENT_CREATE_NEW}].concat(items);
				}

				// we need to wrap the array inside a JSON object so the DataTable is happy
				updatedResponse =
				{
					parent: oFullResponse.data.parent,
					items: items
				};
			}

			return updatedResponse;
		},

		onDatatableScroll: function (oArgs) {
			/* обработка подгрузки новой порции данных */
			if (oArgs.target.scrollTop + oArgs.target.clientHeight === oArgs.target.scrollHeight && !this.currentState.loadingInProcess) {
				//подумать над тем, что если у нас вернулось 0 данных, то больше ничего не грузить
				this.currentState.loadingInProcess = true;
				this.loadTableData(false, this.currentState.searchTerm, this.currentState.exSearchFilter);
			}

		},

		onClick: function (event) {
			/* обработка добавления элемента в выбранные */
			var column = this.widgets.datatable.getColumn(event.target),
				record = this.widgets.datatable.getRecord(event.target);
			if (!event.target.firstChild.firstChild.hidden) {
				if ('add' === column.key) {
					//просигналить пикеру что эту ноду надо нарисовать в selectedItems
					this.fire('addSelectedItemToPicker', { /* Bubbling.fire */
						added: record.getData(),
						options: this.options,
						key: this.key
					});
					return false;
				} else if ('edit' === column.key) {
					this._onRecordEdit(record);
					return false;
				}
			}
		},

		onAddSelectedItem: function (layer, args) {
			var options, nodeData, key, records;
			if (Alfresco.util.hasEventInterest(this, args)) {
				options = args[1].options;
				nodeData = args[1].added;
				key = args[1].key;
				if (!key || key === this.key) {
					if (this.widgets.datatable) {
						records = this.widgets.datatable.getRecordSet().getRecords();
						this.currentState.temporarySelected[nodeData.nodeRef] = nodeData;
						records.forEach(function (record) {
							var tdEls = [];
							tdEls.push(this.widgets.datatable.getTdEl({
								column: this.widgets.datatable.getColumn('add'),
								record: record
							}));
							if (this.options.showEditButton) {
								tdEls.push(this.widgets.datatable.getTdEl({
									column: this.widgets.datatable.getColumn('edit'),
									record: record
								}));
							}
							if (IDENT_CREATE_NEW !== record.getData('type')) {
								tdEls.forEach(function (tdEl) {
									if (tdEl.firstChild.firstChild) {
										tdEl.firstChild.firstChild.hidden = !ACUtils.canItemBeSelected(record.getData('nodeRef'), options, this.currentState.temporarySelected, this.parentControl);
									}
								}, this);
							} else {
								if (tdEls[0].parentElement)	{
									tdEls[0].parentElement.hidden = !ACUtils.canItemBeSelected(IDENT_CREATE_NEW, options, this.currentState.temporarySelected, this.parentControl);
								}
							}
						}, this);
					}
				}
			}
		},

		onRemoveSelectedItem: function (layer, args) {
			if (Alfresco.util.hasEventInterest (this, args)) {
				this.removeSelectedItem(args[1].removed, true);
			}
		},

		onRemoveSelectedItemFromPicker: function (layer, args) {
			if (Alfresco.util.hasEventInterest (this, args)) {
				this.removeSelectedItem(args[1].removed, false);
			}
		},

		removeSelectedItem: function(nodeData, fireChangeAction) {
			var records, removeHappens = false, i;
			records = this.widgets.datatable.getRecordSet().getRecords();
			//удаляем из всех пикеров, иначе некорректно отрисуются плюсики\
			for (i = 0; !removeHappens && (i < this.parentControl.options.itemsOptions.length); i++) {
				var item = this.parentControl.widgets[this.parentControl.options.itemsOptions[i].itemKey];
				if (item.currentState.temporarySelected.hasOwnProperty(nodeData.nodeRef)) {
					delete item.currentState.temporarySelected[nodeData.nodeRef];
					removeHappens = true;
				}
			}

			records.forEach(function (record) {
				var tdEls = [];
				tdEls.push(this.widgets.datatable.getTdEl({
					column: this.widgets.datatable.getColumn('add'),
					record: record
				}));
				if (this.options.showEditButton) {
					tdEls.push(this.widgets.datatable.getTdEl({
						column: this.widgets.datatable.getColumn('edit'),
						record: record
					}));
				}
				if (IDENT_CREATE_NEW !== record.getData('type')) {
					tdEls.forEach(function (tdEl) {
						if (tdEl.firstChild.firstChild) {
							tdEl.firstChild.firstChild.hidden = !ACUtils.canItemBeSelected(record.getData('nodeRef'), this.options, this.currentState.temporarySelected, this.parentControl);
						}
					}, this);
				} else {
					if (tdEls[0].parentElement)	{
						tdEls[0].parentElement.hidden = !ACUtils.canItemBeSelected(IDENT_CREATE_NEW, this.options, this.currentState.temporarySelected, this.parentControl);
					}
				}
			}, this);

			for (var prop in item.currentState.temporarySelected) {
				if (item.currentState.temporarySelected[prop].index > nodeData.index) {
					item.currentState.temporarySelected[prop].index--;
				}
			}

			if (fireChangeAction && removeHappens) {
				this.fire('afterChange', {});
			}
		},

		onShow: function(layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				if (this.key === args[1].itemKey) {
					this.show();
				}
			}
		},

		show: function () {
			/* отображение контрола */
			if (this.loadHelper.fulfil('show')) {
				/* NOP */
			} else if (this.widgets.datatable) {
				this.widgets.datatable.render();
			}
			Dom.removeClass(this.id, 'hidden');
		},

		onHide: function (layer, args) {
			if (Alfresco.util.hasEventInterest(this, args)) {
				this.hide(args[1].reset);
			}
		},

		hide: function (reset) {
			var prop;
			if (reset) {
				this.currentState.temporarySelected = {};
				for (prop in this.currentState.selected) {
					this.currentState.temporarySelected[prop] = YAHOO.lang.merge(this.currentState.selected[prop]);
				}

				this.fire('restorePreviousValues', {
					original: this.currentState.original,
					selected: this.currentState.temporarySelected,
					options: this.options,
					key: this.key
				});

			} else {
				this.currentState.selected = {};
				for (prop in this.currentState.temporarySelected) {
					this.currentState.selected[prop] = YAHOO.lang.merge(this.currentState.temporarySelected[prop]);
				}
			}
			/* скрытие контрола */
			Dom.addClass(this.id, 'hidden');
		},

		onReady: function () {
			if (this.options.showSearch) {
				this.widgets.search = Dom.get(this.id + '-search-text');
				this.widgets.searchButton = Alfresco.util.createYUIButton(this, 'search', this.onSearch, {
					label: '<i class="icon-search"></i>',
					type: 'push'
				});
				this.widgets.searchListener = new YAHOO.util.KeyListener(this.widgets.search, {
					keys: [ YAHOO.util.KeyListener.KEY.ENTER ]
				}, {
					scope: this,
					correctScope:true,
					fn: this.onSearch
				}, 'keydown');
			}
			if (this.options.showExSearch) {
				this.widgets.exSearch = Dom.get(this.id + '-search-form');
				this.widgets.exSearchButton = Alfresco.util.createYUIButton(this, 'ex-search', this.onExSearch, {
					type: 'push'
				});
				this.widgets.exSearchClearButton = Alfresco.util.createYUIButton(this, 'ex-search-clear', this.onExSearchClear, {
					type: 'push'
				});
				this.widgets.exSearchListener = new YAHOO.util.KeyListener(this.widgets.exSearch, {
					keys: [ YAHOO.util.KeyListener.KEY.ENTER ]
				}, {
					scope: this,
					correctScope:true,
					fn: this.onExSearch
				}, 'keydown');

				this._loadSearchForm();
			}
			if (!this.options.plane) {
				this.widgets.treeView = new YAHOO.widget.TreeView(this.id + '-tree');
				this.widgets.treeView.singleNodeHighlight = true;
				this.widgets.treeView.subscribe('expandComplete', this.onExpandComplete, this.widgets.treeView, this);
				this.widgets.treeView.subscribe('clickEvent', this.onTreeNodeClicked, this.widgets.treeView, this);
				if (this.options.showParentNodeInTreeView) {
					this.widgets.treeRoot = new YAHOO.widget.TextNode({ expanded: true }, this.widgets.treeView.getRoot());
				} else {
					this.widgets.treeRoot = this.widgets.treeView.getRoot();
				}
				this.widgets.treeRoot.setDynamicLoad(this.bind(this._loadNodes));
				Dom.addClass(this.widgets.treeView.id, 'treeview');
			}
			this.widgets.datasource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI_RELATIVE + this.options.childrenDataSource + '/node/', {
				responseType: YAHOO.util.DataSource.TYPE_JSON,
				connXhrMode: 'queueRequests',
				responseSchema: {
					resultsList: 'items',
					metaFields: {
						parent: 'parent'
					}
				}
			});
			this.widgets.datasource.doBeforeParseData = this.bind(this.doBeforeParseData);
			var columnDefinitions =
				[
					{key: 'name', label: 'Item', sortable: false, formatter: this._nameFormatter, className: 'name-td'},
					{key: 'add', label: 'Add', sortable: false, minWidth: 16, width: 16, formatter: this._addFormatter, className: 'add-td'}
				];
			if (this.options.showEditButton) {
				columnDefinitions.push({
					key: "edit",
					label: "Edit",
					sortable: false,
					formatter: this._editFormatter,
					width: 16,
					className: 'edit-td'
				});
			}
			this.widgets.datatable = new YAHOO.widget.ScrollingDataTable(this.id + '-datatable', columnDefinitions, this.widgets.datasource, {
				height: '150px',
				renderLoopSize: 100,
				initialLoad: false,
				MSG_EMPTY: this.msg('logicecm.base.select-tree-element')
			});
			this.widgets.datatable.owner = this;
//			this.widgets.datatable.on('renderEvent', this.onDatatableRendered, null, this);
			this.widgets.datatable.on('cellClickEvent', this.onClick, null, this);
			this.widgets.datatable.on('tableScrollEvent', this.onDatatableScroll, null, this);

			this.loadHelper.fulfil('ready');
		},

		_loadSearchForm: function () {
			if (this.options.showExSearch && this.widgets.exSearch) {
				// load the form component for the appropriate type
				var formUrl = Alfresco.constants.URL_SERVICECONTEXT + "/components/form";

				var formData = {
					htmlid: this.widgets.exSearch.id + "-" + Alfresco.util.generateDomId(),
					itemKind: "type",
					itemId: this.options.itemType,
					formId: "ex-control-search",
					mode: "edit",
					showSubmitButton: false,
					showCancelButton: false
				};
				this.currentState.exSearchFormId = formData.htmlid + "-form";

				Alfresco.util.Ajax.request(
					{
						url: formUrl,
						dataObj: formData,
						successCallback: {
							fn: function (response) {
								var markupAndScripts = Alfresco.util.Ajax.sanitizeMarkup(response.serverResponse.responseText),
									markup = markupAndScripts[0],
									scripts = markupAndScripts[1];
								this.widgets.exSearch.innerHTML = markup;
								setTimeout(scripts, 0);
							},
							scope: this
						},
						scope: this,
						execScripts: true
					});
			}
		},

		_generateCreateNewParams: function (nodeRef, itemType) {
			var args = {};
			if (this.options.fillFormFromSearch && this.widgets.exSearch && this.currentState.exSearchFormId) {
				var currentForm = Dom.get(this.currentState.exSearchFormId);
				if (currentForm) {
					args = this._fnGetArgumentsFromForm(currentForm);
				}
			}
			return {
				itemKind: "type",
				itemId: itemType,
				destination: nodeRef,
				mode: "create",
				submitType: "json",
				formId: "association-create-new-node-form",
				showCancelButton: true,
				args: JSON.stringify(args)
			};
		},

		_fnCreateNewItemHandler: function() {
			if (this.stateParams.doubleClickLock) return;
			this.stateParams.doubleClickLock = true;

			var templateRequestParams = this._generateCreateNewParams(this.currentState.nodeData.nodeRef, this.options.itemType);
			templateRequestParams["createNewMessage"] = this.options.createNewMessage;

			new Alfresco.module.SimpleDialog("create-form-dialog-" + this.eventGroup).setOptions({
				width:"50em",
				templateUrl: "lecm/components/form",
				templateRequestParams: templateRequestParams,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn: function (p_form, p_dialog) {
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
						this.stateParams.doubleClickLock = false;
					},
					scope: this
				},
				onSuccess:{
					fn:function (response) {

						Alfresco.util.Ajax.jsonPost({
							url: Alfresco.constants.PROXY_URI_RELATIVE + this.options.pickerItemsScript,
							dataObj: {
								items: [response.json.persistedObject],
								itemValueType: 'nodeRef',
								itemNameSubstituteString: this.options.nameSubstituteString,
								selectedItemsNameSubstituteString: ACUtils.getSelectedItemsNameSubstituteString(this.options),
								pathRoot: this.options.rootLocation,
								pathNameSubstituteString: this.options.treeNodeSubstituteString,
								useObjectDescription: this.options.useObjectDescription
							},
							successCallback: {
								scope: this,
								fn: function (successResponse) {
									var items =successResponse.json.data.items;

									if (items && items[0]) {
										this.fire('addSelectedItemToPicker', { /* Bubbling.fire */
											added: items[0],
											options: this.options,
											key: this.key
										});
									}
								}
							},
							failureCallback: {
								scope: this,
								fn: function () {}
							}
						});

						this.stateParams.doubleClickLock = false;
					},
					scope:this
				},
				onFailure: {
					fn:function (response) {
						this.stateParams.doubleClickLock = false;
					},
					scope:this
				}
			}).show();
			return true;
		},

		_onRecordEdit: function (record) {
			if (this.doubleClickLock) return;
			this.doubleClickLock = true;
			if (record) {
				var nodeRef = record.getData("nodeRef");

				if (nodeRef) {
					new Alfresco.module.SimpleDialog("edit-form-dialog-" + this.eventGroup).setOptions({
						width: "50em",
						templateUrl: "lecm/components/form",
						templateRequestParams: {
							itemKind: "node",
							itemId: nodeRef,
							mode: "edit",
							submitType: "json",
							showCancelButton: true
						},
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							fn: function (p_form, p_dialog) {
								var message;
								if (this.options.editMessage) {
									message = this.options.editMessage;
								} else {
									message = this.msg("dialog.edit.title");
								}
								p_dialog.dialog.setHeader(message);

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id,  force: true}, this);

								Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
								if (this.options.editDialogClass) {
									Dom.addClass(p_dialog.id + "-form-container", this.options.editDialogClass);
								}
								this.doubleClickLock = false;
							},
							scope: this
						},
						onSuccess: {
							fn: function (response) {
								this.doubleClickLock = false;
								this.loadTableData(true, this.currentState.searchTerm, this.currentState.exSearchFilter)
							},
							scope: this
						},
						onFailure: {
							fn: function () {
								this.doubleClickLock = false;
							},
							scope: this
						}
					}).show();
				}
			}
			return true;
		},

		_fnGetExtSearchQuery: function (currentForm) {
			var exSearchFilter = '',
				propNamePrefix = '@',
				first = true;

			for (var i = 0; i < currentForm.elements.length; i++) {
				var element = currentForm.elements[i],
					propName = element.name,
					propValue = YAHOO.lang.trim(element.value);

				if (propName && propValue && propValue.length) {
					if (propName.indexOf("prop_") == 0) {
						propName = propName.substr(5);
						if (propName.indexOf("_") !== -1) {
							propName = propName.replace("_", ":");
							if (propName.match("-range$") == "-range") {
								var from, to, sepindex = propValue.indexOf("|");
								if (propName.match("-date-range$") == "-date-range") {
									propName = propName.substr(0, propName.length - "-date-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, 10));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1, 10));
								} else {
									propName = propName.substr(0, propName.length - "-number-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, sepindex));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1));
								}
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':"' + from + '".."' + to + '"';
								first = false;
							} else {
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':' + this.applySearchSettingsToTerm(this.escape(propValue), 'MATCHES');
								first = false;
							}
						}
					} else if (propName.indexOf("assoc_") == 0) {
						var assocName = propName.substring(6);
						if (assocName.indexOf("_") !== -1) {
							assocName = assocName.replace("_", ":") + "-ref";
							exSearchFilter += (first ? '(' : ' AND (');
							var assocValues = propValue.split(",");
							var firstAssoc = true;
							for (var k = 0; k < assocValues.length; k++) {
								var assocValue = assocValues[k];
								if (!firstAssoc) {
									exSearchFilter += " OR ";
								}
								exSearchFilter += this.escape(assocName) + ':"' + this.applySearchSettingsToTerm(this.escape(assocValue), 'CONTAINS') + '"';
								firstAssoc = false;
							}
							exSearchFilter += ") ";
							first = false;
						}
					}
				}
			}
			return exSearchFilter;
		},

		_fnGetArgumentsFromForm: function (currentForm) {
			var args = {};
			for (var i = 0; i < currentForm.elements.length; i++) {
				var element = currentForm.elements[i],
					propName = element.name,
					propValue = YAHOO.lang.trim(element.value);

				if (propName && (propName.indexOf("prop_") == 0 || propName.indexOf("assoc_") == 0)) {
					if (propValue) {
						args[propName] = propValue;
					}
				}
			}
			return args;
		}

	}, true);
})();
