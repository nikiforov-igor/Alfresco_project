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
		Dom = YAHOO.util.Dom;

	LogicECM.module.AssociationComplexControl.Item = function (containerId, key, options, fieldValues) {
		this.currentState = Alfresco.util.deepCopy(this.currentState); // Initialise default prototype properties
		LogicECM.module.AssociationComplexControl.Item.superclass.constructor.call(this, 'LogicECM.module.AssociationComplexControl.Item', containerId);
		this.setOptions(options);
		this.key = key;
		this.loadHelper = Alfresco.util.Deferred(['rootNode', 'searchProperties', 'ready', 'show'], {
			scope: this,
			fn: this.loadData
		});
		this._loadRootNode();
		this._loadSearchProperties();
		this._loadOriginalValues(fieldValues);

		Bubbling.on('show', this.onShow, this);
		Bubbling.on('hide', this.onHide, this);
		Bubbling.on('addSelectedItem', this.onAddSelectedItem, this);
		Bubbling.on('removeSelectedItem', this.onRemoveSelectedItem, this);

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
			nodeData: null,
			skipItemsCount: null
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
			checkType: true
		},

		widgets: {
			search: null,
			searchButton: null,
			searchListener: null,
			treeView: null,
			datatable: null,
			datasource: null
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
			if ('~CREATE~NEW~' === record.getData('type')) {
				msg = this.owner.options.createNewMessage ? this.owner.options.createNewMessage : this.owner.msg('form.control.object-picker.create-new');
				elCell.innerHTML = '<a href="javascript:void(0);" title="' + msg + '" class="create-new-row create-new-item-' + this.owner.eventGroup + '" >' + msg + '</a>';
				return;
			}

			if ('lecm-orgstr:employee' === record.getData('type')) {
				template += '<h3 class="item-name">' + BaseUtil.getControlEmployeeView('{nodeRef}', '{name}', true) + '</h3>';
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
				hidden = ACUtils.canItemBeSelected(nodeRef, this.owner.options, this.owner.currentState.selected) ? '' : ' hidden ';

				elCell.innerHTML = '<a href="javascript:void(0);"' + hidden + 'title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><i class="icon-plus"></i></a>';

//				elCell.innerHTML = '<a href="javascript:void(0);" ' + style + ' class="add-item add-' + this.owner.eventGroup + '" title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
//				elCell.innerHTML = '<a href="javascript:void(0);" ' + style + ' class="add-item" title="' + this.owner.msg('form.control.object-picker.add-item') + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
//				scope.addItemButtons[nodeRef] = containerId;
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

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + this.options.treeBranchesDatasource + '/' + node.data.nodeRef.replace("://", "/") + '/items',
				dataObj: {
					nodeSubstituteString: this.options.treeNodeSubstituteString,
					nodeTitleSubstituteString: this.options.treeNodeTitleSubstituteString,
					selectableType: this.options.treeItemType ? this.options.treeItemType : this.options.itemType
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

		_loadSearchProperties: function () {
			/* получение данных для поиска */
			function onSuccess (successResponse) {
				var columns = successResponse.json.columns;
				this.searchProperties = columns.map(function (column) {
					return column.name;
				}, this);
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
				var items =successResponse.json.data.items,
					checkType = this.options.checkType,
					itemType = this.options.itemType;
				this.currentState.original = items.reduce(function (prev, curr) {
					if (!checkType || curr.type === itemType) {
						prev[curr.nodeRef] = curr;
					}
					return prev;
				}, {});
				this.currentState.selected = YAHOO.lang.merge(this.currentState.original);
				this.fire('loadOriginalItems', { /* Bubbling.fire */
					original: this.currentState.selected,
					options: this.options
				});
			}

			function onFailure(failureResponse) {
			}

			Alfresco.util.Ajax.jsonPost({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/forms/picker/items', /* this.options.pickerItemsScript */
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
				},
				failureCallback: {
					scope: this,
					fn: onFailure
				}
			});
		},

		loadData: function () {
			/* наполнение дерева и датагрида данным при открытии пикера */
			this.currentState.nodeData = this.rootNodeData;
			if (this.options.showSearch) {
				this.widgets.searchListener.enable();
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
			this.loadTableData(true);

		},

		loadTableData: function (initializeTable, searchTerm) {
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

			}

			function onFailure(sRequest, oResponse) {
				var response;

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

			this.currentState.skipItemsCount = initializeTable ? 0 : this.currentState.skipItemsCount;
			params = ACUtils.generateChildrenUrlParams(this.options, searchTerm, this.currentState.skipItemsCount);
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

		onSearch: function (evt, target) {
			/* обработка поиска */
			var searchData = this.widgets.search.value,
				searchTerm;

			if (searchData && searchData.length >= this.options.minSearchTermLength) {
				searchTerm = this.searchProperties.reduce(function (prev, curr) {
					return prev + (prev.length ? '#' : '') + curr + ':' + searchData.replace(/#/g, '');
				}, '');
				searchTerm = searchTerm ? searchTerm : 'cm:name:' + searchData;
				this.loadTableData(true, searchTerm);
			} else if ('' === searchData) {
				this.loadTableData(true);
			} else {
				Alfresco.util.PopupManager.displayMessage({
					text: this.msg('form.control.object-picker.search.enter-more', this.options.minSearchTermLength)
				}/* parentElement ???? */);
			}
		},

		onExpandComplete: function (node, tree) {
			/* обработка распахивания дерева */
		},

		onTreeNodeClicked: function (obj, tree) {
			/* выбор элемента в дереве и обновление датагрида */
			this.currentState.nodeData = obj.node.data;
			this.loadTableData(true);
			return tree.onEventToggleHighlight(obj);
		},

		doBeforeParseData: function (sRequest, oFullResponse) {
			/* препроцессинг данных для датагрида */
			//maxSearchResults
			//createNew
			//categories tags
			//employeeAbsenceMarker
			return {
				parent: oFullResponse.data.parent,
				items: oFullResponse.data.items
			};
		},

//		onDatatableRendered: function () {
//			/* обработка отрисовки датагрида */
//			var hasRecords = this.widgets.datatable.getRecordSet().getLength();
//			if (hasRecords) {
//				Dom.removeClass(this.id + '-datatable', 'hidden');
//			} else {
//				Dom.addClass(this.id + '-datatable', 'hidden');
//			}
//		},

		onDatatableScroll: function (oArgs) {
			/* обработка подгрузки новой порции данных */
			if (oArgs.target.scrollTop + oArgs.target.clientHeight === oArgs.target.scrollHeight) {
				//подумать над тем, что если у нас вернулось 0 данных, то больше ничего не грузить
				this.loadTableData();
			}
		},

		onAdd: function (event) {
			/* обработка добавления элемента в выбранные */
			var column = this.widgets.datatable.getColumn(event.target),
				record = this.widgets.datatable.getRecord(event.target);

			if ('add' === column.key && !event.target.firstChild.firstChild.hidden) {
				//просигналить пикеру что эту ноду надо нарисовать в selectedItems
				this.fire('addSelectedItem', { /* Bubbling.fire */
					added: record.getData(),
					options: this.options,
					key: this.key
				});
				return false;
			}
		},

		onAddSelectedItem: function (layer, args) {
			var options, nodeData, key, records;
			if (Alfresco.util.hasEventInterest(this, args)) {
				options = args[1].options;
				nodeData = args[1].added;
				key = args[1].key;
				if (!key || key === this.key) {
					records = this.widgets.datatable.getRecordSet().getRecords();
					this.currentState.selected[nodeData.nodeRef] = nodeData;
					records.forEach(function (record) {
						var tdEl = this.widgets.datatable.getTdEl({
							column: this.widgets.datatable.getColumn('add'),
							record: record
						});
						tdEl.firstChild.firstChild.hidden = !ACUtils.canItemBeSelected(record.getData('nodeRef'), options, this.currentState.selected);
					}, this);
					this.fire('afterChange', {
						key: this.key
					});
				}
			}
		},

		onRemoveSelectedItem: function (layer, args) {
			var nodeData, records, removeHappend;

			if (Alfresco.util.hasEventInterest (this, args)) {
				nodeData = args[1].removed;
				records = this.widgets.datatable.getRecordSet().getRecords();
				if (this.currentState.selected.hasOwnProperty(nodeData.nodeRef)) {
					delete this.currentState.selected[nodeData.nodeRef];
					removeHappend = true;
				}
				records./*filter(function (record) {
					return record.getData('nodeRef') === this.nodeRef;
				}, nodeData).*/forEach(function (record) {
					var tdEl = this.widgets.datatable.getTdEl({
						column: this.widgets.datatable.getColumn('add'),
						record: record
					});
					tdEl.firstChild.firstChild.hidden = !ACUtils.canItemBeSelected(record.getData('nodeRef'), this.options, this.currentState.selected);
				}, this);
				if (removeHappend) {
					this.fire('afterChange', {
						key: this.key
					});
				}
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
			if (reset) {
				this.currentState.selected = YAHOO.lang.merge(this.currentState.original);
			}
			/* скрытие контрола */
			Dom.addClass(this.id, 'hidden');
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
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
			this.widgets.datatable = new YAHOO.widget.ScrollingDataTable(this.id + '-datatable', [
				{ key: 'name', label: 'Item', sortable: false, formatter: this._nameFormatter, className: 'name-td' },
				{ key: 'add', label: 'Add', sortable: false, minWidth: 16, width: 16, formatter: this._addFormatter, className: 'add-td' }
			], this.widgets.datasource, {
				height: '150px',
				renderLoopSize: 100,
				initialLoad: false,
				MSG_EMPTY: this.msg('logicecm.base.select-tree-element')
			});
			this.widgets.datatable.owner = this;
//			this.widgets.datatable.on('renderEvent', this.onDatatableRendered, null, this);
			this.widgets.datatable.on('cellClickEvent', this.onAdd, null, this);
			this.widgets.datatable.on('tableScrollEvent', this.onDatatableScroll, null, this);

			this.loadHelper.fulfil('ready');
		}
	}, true);
})();
