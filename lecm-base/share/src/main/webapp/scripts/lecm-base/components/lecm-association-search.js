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

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		KeyListener = YAHOO.util.KeyListener;

	var $html = Alfresco.util.encodeHTML,
		$combine = Alfresco.util.combinePaths,
		$hasEventInterest = Alfresco.util.hasEventInterest;

	LogicECM.module.AssociationSearchViewer = function(htmlId)
	{
		LogicECM.module.AssociationSearchViewer.superclass.constructor.call(this, "AssociationSearchViewer", htmlId);
		YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);

		this.eventGroup = htmlId;
		this.selectedItems = {};
		this.notShowedSelectedValue = {};
		this.addItemButtons = {};
		this.searchProperties = {};
		this.currentNode = null;
		this.isSearch = false;
        this.allowedNodes = null;
        this.allowedNodesScript = null;

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationSearchViewer, Alfresco.component.Base,
		{
			eventGroup: null,

			singleSelectedItem: null,

			selectedItems: null,

			addItemButtons: null,

			currentNode: null,

			searchProperties: null,

			isSearch: false,

			options:
			{
                defaultValue: null,

	            defaultValueDataSource: null,
                
				changeItemsFireAction: null,

				selectedValue: null,

				notShowedSelectedValue: null,

				currentValue: "",
				// If control is disabled (has effect in 'picker' mode only)
				disabled: false,
				// If this form field is mandatory
				mandatory: false,
				// If control allows to pick multiple assignees (has effect in 'picker' mode only)
				multipleSelectMode: false,

				initialized: false,

				rootLocation: null,

				itemType: "cm:content",

				maxSearchResults: 100,

				nameSubstituteString: "{cm:name}",

				selectedItemsNameSubstituteString: null,

				// fire bubling методы выполняемые по нажатию определенной кнопки в диалоговом окне
				fireAction:
				{
					// кнопка addItem + в таблице элемента выбора
					addItem: null,
					// кнопка ok при submite
					ok: null,
					// кнопка cancel
					cancel: null,
					// кнопка поиска
					find: null
				},

				additionalFilter: "",

				ignoreNodes: null,

                allowedNodes:null,

                allowedNodesScript: null,

				showSelectedItems: true,

				createDialog: false,

				childrenDataSource: "lecm/forms/picker"
			},

			onReady: function AssociationSearchViewer_onReady()
			{
				if(!this.options.initialized) {
					this.options.initialized = true;
					this.init();
                    if (this.extendedInit) {
                        this.extendedInit();
                    }
				}
			},

			init: function()
			{
				this.options.controlId = this.id + '-cntrl';
				this.options.pickerId = this.id + '-cntrl-picker';

				//this._loadSelectedItems();
				this._loadDefaultValue();

				// Create button if control is enabled
				if(!this.options.disabled)
				{
					if (this.options.createDialog) {
						// Create picker button
						var buttonName = Dom.get(this.options.controlId + "-tree-picker-button").name;
						this.widgets.pickerButton =  new YAHOO.widget.Button(
							this.options.controlId + "-tree-picker-button",
							{ onclick: { fn: this.showTreePicker, obj: null, scope: this } }
						);
						Dom.get(this.options.controlId + "-tree-picker-button-button").name = buttonName;

						Dom.setStyle(this.options.pickerId, "display", "block");
						this.createPickerDialog();
					}

					this.populateDataWithAllowedScript();
					this.createSearchDialog();
					this._loadSearchProperties();
				}
			},

            _loadDefaultValue: function AssociationSearch__loadDefaultValue() {
		        if (this.options.defaultValue != null) {
                     this.defaultValue = this.options.defaultValue;
                     this._loadSelectedItems();
                } else 
                if (this.options.defaultValueDataSource != null) {
			        var me = this;

			        Alfresco.util.Ajax.request(
				        {
					        url: Alfresco.constants.PROXY_URI + this.options.defaultValueDataSource,
					        successCallback: {
						        fn: function (response) {
							        var oResults = eval("(" + response.serverResponse.responseText + ")");
							        if (oResults != null && oResults.nodeRef != null ) {
								        me.defaultValue = oResults.nodeRef;
							        }
							        me._loadSelectedItems();
						        }
					        },
					        failureMessage: "message.failure"
				        });
		        } else {
			        this._loadSelectedItems();
		        }
	        },


			_loadSearchProperties: function AssociationSearchViewer__loadSearchProperties() {
				Alfresco.util.Ajax.jsonGet(
					{
						url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "components/data-lists/config/columns?itemType=" + encodeURIComponent(this.options.itemType)),
						successCallback:
						{
							fn: function (response) {
								var columns = response.json.columns;
								for (var i = 0; i < columns.length; i++) {
									var column = columns[i];
									if (column.dataType == "text") {
										this.searchProperties[column.name] = column.name;
									} else if (column.type == "association"){
                                        this.searchProperties[column.name + "-text-content"] = column.name + "-text-content";
                                    }
								}
							},
							scope: this
						},
						failureCallback:
						{
							fn: function (oResponse) {
								var response = YAHOO.lang.JSON.parse(oResponse.responseText);
								this.widgets.dataTable.set("MSG_ERROR", response.message);
								this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
							},
							obj:
							{
								title: this.msg("message.error.columns.title"),
								text: this.msg("message.error.columns.description")
							},
							scope: this
						}
					});
			},



			_loadSelectedItems: function AssociationSearchViewer__loadSelectedItems()
			{
				var arrItems = "";
				if (this.options.selectedValue != null)
				{
					arrItems = this.options.selectedValue;
				}
				else
				{
					arrItems = this.options.currentValue;
				}
                
                if (arrItems == "" && this.defaultValue != null) {
		            arrItems += this.defaultValue;
	            }

				var onSuccess = function AssociationSearchViewer__loadSelectedItems_onSuccess(response)
				{
					var items = response.json.data.items,
						item;
					this.selectedItems = {};
					//this.singleSelectedItem = null;
					if (!this.options.multipleSelectMode && items[0]) {
						this.singleSelectedItem = items[0];
					}

					for (var i = 0, il = items.length; i < il; i++)
					{
						item = items[i];
						this.selectedItems[item.nodeRef] = item;

						if (!this.options.showSelectedItems) {
							this.notShowedSelectedValue[item.nodeRef] = item;
						}
					}

					if(!this.options.disabled)
					{
						if (this.options.createDialog) {
							this.updateSelectedItems();
						}
						this.updateAddButtons();
					}

					this.updateFormFields();
				};

				var onFailure = function AssociationSearchViewer__loadSelectedItems_onFailure(response)
				{
					this.selectedItems = null;
				};

				if (arrItems !== "")
				{
					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
							method: "POST",
							dataObj:
							{
								items: arrItems.split(","),
								itemValueType: "nodeRef",
								itemNameSubstituteString: this.options.nameSubstituteString,
								selectedItemsNameSubstituteString: this.getSelectedItemsNameSubstituteString()
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
						if (this.options.createDialog) {
							this.updateSelectedItems();
						}
						this.updateFormFields();
						this.updateAddButtons();
					} else {
						Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
					}
				}
			},

			createSearchDialog: function()
			{
				var me = this;

				this.widgets.searchButton = new YAHOO.widget.Button(this.options.pickerId + "-searchButton");
				this.widgets.searchButton.on("click", this.onSearch, this.widgets.searchButton, this);

				// Register the "enter" event on the search text field
				var zinput = Dom.get(this.options.pickerId + "-searchText");
				new YAHOO.util.KeyListener(zinput,
					{
						keys: 13
					},
					{
						fn: me.onSearch,
						scope: this,
						correctScope: true
					}, "keydown").enable();

				Dom.addClass(this.options.pickerId, "object-finder");
                zinput.onkeyup = this.checkSearchField.bind(this);
                this.checkSearchField();
			},


			onSearch: function(e, obj)
			{
                if (!this.widgets.searchButton.get("disabled")) {
    //				var nodeRef = this.options.rootNodeRef;
                    var searchTerm = Dom.get(this.options.pickerId + "-searchText").value;
                    var searchData = "";
                    if (searchTerm != undefined && searchTerm != null && searchTerm != ""){
                        for(var column in this.searchProperties) {
                            searchData += column + ":" + searchTerm + "#";
                        }
                        if (searchData != "") {
                            searchData = searchData.substring(0,(searchData.length)-1);
                        }
                    }
                    this.isSearch = true;
                    this._updateItems(searchData);
                }
				if (obj && obj[1]) {
					obj[1].preventDefault();
				}
			},

			_createSelectedControls: function AssociationSearchViewer__createSelectedControls()
			{
				var me = this;

				// DataSource definition
				var pickerChildrenUrl = Alfresco.constants.PROXY_URI + this.options.childrenDataSource;
				this.widgets.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl,
					{
						responseType: YAHOO.util.DataSource.TYPE_JSON,
						connXhrMode: "queueRequests",
						responseSchema:
						{
							resultsList: "items",
							metaFields:
							{
								parent: "parent"
							}
						}
					});

				this.widgets.dataSource.doBeforeParseData = function AssociationSearchViewer_doBeforeParseData(oRequest, oFullResponse)
				{
					var updatedResponse = oFullResponse;

					if (oFullResponse)
					{
						var items = oFullResponse.data.items;

						// Crop item list to max length if required
						if (me.options.maxSearchResults > -1 && items.length > me.options.maxSearchResults)
						{
							items = items.slice(0, me.options.maxSearchResults-1);
						}

						// Special case for tags, which we want to render differently to categories
						var index, item;
						for (index in items)
						{
							if (items.hasOwnProperty(index))
							{
								item = items[index];
								if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
								{
									item.type = "tag";
									// Also set the parent type to display the drop-down correctly. This may need revising for future type support.
									oFullResponse.data.parent.type = "tag";
								}
							}
						}

						var ignoreItems = me.options.ignoreNodes;
						if (ignoreItems != null) {
							var tempItems = [];
							var k = 0;
							for (index in items) {
								item = items[index];
								var ignore = false;
								for (var i = 0; i < ignoreItems.length; i++) {
									if (ignoreItems[i] == item.nodeRef) {
										ignore = true;
									}
								}
								if (!ignore) {
									tempItems[k] = item;
									k++;
								}
							}
							items = tempItems;
						}

						var allowedNodes = me.options.allowedNodes;
						if(YAHOO.lang.isArray(allowedNodes) && (allowedNodes.length > 0) && allowedNodes[0]) {
							for(i = 0; item = items[i]; i++) {
								if(allowedNodes.indexOf(item.nodeRef) < 0) {
									items.splice(i--, 1);
								}
							}
						}

						// we need to wrap the array inside a JSON object so the DataTable is happy
						updatedResponse =
						{
							parent: oFullResponse.data.parent,
							items: items
						};
					}

					return updatedResponse;
				};

				// DataTable column defintions
				var columnDefinitions =
					[
						{ key: "name", label: "Item", sortable: false, formatter: this.fnRenderItemName() },
						{ key: "add", label: "Add", sortable: false, formatter: this.fnRenderCellAdd(), width: 16 }
					];

				var initialMessage = this.msg("logicecm.base.message.enter-search-text");

				this.widgets.dataTable = new YAHOO.widget.DataTable(this.options.pickerId + "-group-members", columnDefinitions, this.widgets.dataSource,
					{
						renderLoopSize: 100,
						initialLoad: false,
						MSG_EMPTY: initialMessage
					});

				// Hook add item action click events (for Compact mode)
				var fnAddItemHandler = function AssociationSearchViewer__createControls_fnAddItemHandler(layer, args)
				{
					var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
					if (owner !== null)
					{
						var target, rowId, record;

						target = args[1].target;
						rowId = target.offsetParent;
						record = me.widgets.dataTable.getRecord(rowId);
						if (record)
						{
							YAHOO.Bubbling.fire("selectedItemAdded",
								{
									eventGroup: me,
									item: record.getData(),
									highlight: true
								});
							if (me.options.fireAction.addItem != null) {
								var fireName = me.options.fireAction.addItem.split(",");
								for (var i in fireName){
									YAHOO.Bubbling.fire(fireName[i],
										{
											nodeRef: record.getData().nodeRef
										});
								}
							}
						}
					}
					return true;
				};
				YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddItemHandler, true);
 			},

			/**
			 * Returns Name datacell formatter
			 *
			 * @method fnRenderItemName
			 */
			fnRenderItemName: function AssociationSearchViewer_fnRenderItemName()
			{
				var scope = this;

				return function AssociationSearchViewer_renderItemName(elCell, oRecord, oColumn, oData)
				{
					var template = '';

					template += '<h3 class="item-name">{name}</h3>';

					if (!scope.options.compactMode)
					{
						template += '<div class="description">{description}</div>';
					}

					if (oRecord.getData("type") == "lecm-orgstr:employee")
					{
						elCell.innerHTML = scope.getEmployeeView(oRecord.getData("nodeRef"), scope.renderItem(oRecord.getData(), template));
					} else {
						elCell.innerHTML = scope.renderItem(oRecord.getData(), template);
					}
				};
			},

			/**
			 * Returns Add button datacell formatter
			 *
			 * @method fnRenderCellAdd
			 */
			fnRenderCellAdd: function AssociationSearchViewer_fnRenderCellAdd()
			{
				var scope = this;

				return function AssociationSearchViewer_renderCellAdd(elCell, oRecord, oColumn, oData)
				{
					Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

					var containerId = Alfresco.util.generateDomId();

					if (oRecord.getData("selectable"))
					{
						var nodeRef = oRecord.getData("nodeRef"),
							style = "";

						if (!scope.canItemBeSelected(nodeRef))
						{
							style = 'style="display: none"';
						}

						elCell.innerHTML = '<a id="' + containerId + '" href="#" ' + style + ' class="add-item add-' + scope.eventGroup + '" title="' + scope.msg("form.control.object-picker.add-item") + '" tabindex="0"><span class="addIcon">&nbsp;</span></a>';
						scope.addItemButtons[nodeRef] = containerId;
					}
				};
			},

			renderItem: function AssociationSearchViewer_renderItem(item, template)
			{
				var me = this;

				var renderHelper = function AssociationSearchViewer_renderItem_renderHelper(p_key, p_value, p_metadata)
				{
					return p_value;
				};

				return YAHOO.lang.substitute(template, item, renderHelper);
			},

			canItemBeSelected: function AssociationSearchViewer_canItemBeSelected(id)
			{
				if (!this.options.multipleSelectMode && this.singleSelectedItem !== null)
				{
					return false;
				}
				return (this.selectedItems[id] === undefined);
			},

			_updateItems: function AssociationSearchViewer__updateItems(searchTerm)
			{
				// Empty results table - leave tag entry if it's been rendered
				this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.loading"));
				this.widgets.dataTable.showTableMessage(this.msg("label.loading"), YAHOO.widget.DataTable.CLASS_EMPTY);
				this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

				var successHandler = function AssociationSearchViewer__updateItems_successHandler(sRequest, oResponse, oPayload)
				{
					this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : null;
					this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
					this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
				};

				var failureHandler = function AssociationSearchViewer__updateItems_failureHandler(sRequest, oResponse)
				{
					if (oResponse.status == 401)
					{
						// Our session has likely timed-out, so refresh to offer the login page
						window.location.reload();
					}
					else
					{
						try
						{
							var response = YAHOO.lang.JSON.parse(oResponse.responseText);
							this.widgets.dataTable.set("MSG_ERROR", response.message);
							this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
						}
						catch(e)
						{
						}
					}
				};

				// build the url to call the pickerchildren data webscript
				var url = "/node/children" + this._generateChildrenUrlParams(searchTerm);

				if (Alfresco.logger.isDebugEnabled())
				{
					Alfresco.logger.debug("Generated pickerchildren url fragment: " + url);
				}

				// call the pickerchildren data webscript
				this.widgets.dataSource.sendRequest(url,
					{
						success: successHandler,
						failure: failureHandler,
						scope: this
					});

				// the start location is now resolved
				this.startLocationResolved = true;
			},

			_generateChildrenUrlParams: function AssociationSearchViewer__generatePickerChildrenUrlParams(searchTerm)
			{
				var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
					"&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
					"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
					"&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);

					if (this.options.rootLocation && this.options.rootLocation.charAt(0) == "/")
				{
					params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
				}

				return params;
			},

			onSelectedItemAdded: function AssociationSearchViewer_onSelectedItemAdded(layer, args)
			{
				// Check the event is directed towards this instance
				if ($hasEventInterest(this, args))
				{
					var obj = args[1];
					if (obj && obj.item)
					{
						this.selectedItems[obj.item.nodeRef] = obj.item;
						this.singleSelectedItem = obj.item;

						if (this.options.createDialog) {
							this.updateSelectedItems();
						} else {
							this.updateFormFields();
						}
						this.updateAddButtons();
					}
				}
			},

			removeNode: function AssociationSearchViewer_removeNode(event, params)
			{
				delete this.selectedItems[params.node.nodeRef];
				this.singleSelectedItem = null;
				if (this.options.createDialog) {
					this.updateSelectedItems();
				}
				this.updateAddButtons();
				if (params.updateForms) {
					this.updateFormFields();
				}
			},

			getEmployeeView: function (employeeNodeRef, displayValue) {
				return "<span class='person'><a href='javascript:void(0);' onclick=\"viewAttributes(\'" + employeeNodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
			},

            getDefaultView: function (displayValue, width100) {
				return "<span class='not-person" + (width100 ? " width100" : "") + "'>" + displayValue + "</span>";
			},

			updateAddButtons: function AssociationSearchViewer_updateAddButtons() {
				var button;
				for (var id in this.addItemButtons)
				{
					if (this.addItemButtons.hasOwnProperty(id))
					{
						button = this.addItemButtons[id];
						Dom.setStyle(button, "display", this.canItemBeSelected(id) ? "inline" : "none");
					}
				}
			},

			getRemoveButtonHTML: function AssociationSearchViewer_getRemoveButtonHTML(node, dopId)
			{
				if (!dopId) {
					dopId = "";
				}
				return '<a href="javascript:void(0);" class="remove-item" id="t-' + this.options.controlId + node.nodeRef + dopId + '"></a>';
			},

			attachRemoveClickListener: function AssociationSearchViewer_attachRemoveClickListener(params)
			{
				YAHOO.util.Event.on("t-" + this.options.controlId + params.node.nodeRef + params.dopId, 'click', this.removeNode, {
					node: params.node,
					updateForms: params.updateForms
				}, this);
			},

			// Updates all form fields
			updateFormFields: function AssociationSearchViewer_updateFormFields()
			{
				// Just element
				var el;

				el = Dom.get(this.options.controlId + "-currentValueDisplay");
				el.innerHTML = '';
				var num = 0;
				for (var i in this.selectedItems) {
					if (this.notShowedSelectedValue[i] == null) {
						var displayName = this.selectedItems[i].selectedName;

						var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
						if(this.options.disabled) {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML += '<div class="' + divClass + '"> ' +  this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) + ' ' + '</div>';
							} else {
								el.innerHTML += '<div class="' + divClass + '">' + this.getDefaultView(displayName, true) + ' ' + '</div>';
							}
						} else {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML
									+= '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
							} else {
								el.innerHTML
									+= '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
							}
							YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);
						}
					}
				}

				if(!this.options.disabled)
				{
					var addItems = this.getAddedItems();

					// Update added fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-added");
					el.value = '';
					for (i in addItems) {
						el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
					}

					var removedItems = [];
					if (this.options.showSelectedItems) {
						removedItems = this.getRemovedItems();
					}

					// Update removed fields in main form to be submitted
					el = Dom.get(this.options.controlId + "-removed");
					el.value = '';
					for (i in removedItems) {
						el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
					}

					var selectedItems = this.getSelectedItems();

					// Update selectedItems fields in main form to pass them between popup and form
					el = Dom.get(this.options.controlId + "-selectedItems");
					el.value = '';
					for (i in selectedItems) {
						el.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
					}

					Dom.get(this.eventGroup).value = selectedItems.toString();

					if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
						YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
							selectedItems: this.selectedItems
						});
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
			},

			getAddedItems: function AssociationSearchViewer_getAddedItems()
			{
				var addedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in this.selectedItems)
				{
					if (this.selectedItems.hasOwnProperty(item))
					{
						if (!(item in currentItems))
						{
							addedItems.push(item);
						}
					}
				}
				return addedItems;
			},

			getRemovedItems: function AssociationSearchViewer_getRemovedItems()
			{
				var removedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in currentItems)
				{
					if (currentItems.hasOwnProperty(item))
					{
						if (!(item in this.selectedItems))
						{
							removedItems.push(item);
						}
					}
				}
				return removedItems;
			},

			getSelectedItems:function AssociationSearchViewer_getSelectedItems() {
				var selectedItems = [];

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				return selectedItems;
			},

			getSelectedItemsNameSubstituteString:function AssociationSearchViewer_getSelectedItemsNameSubstituteString() {
				var result = this.options.nameSubstituteString;
				if (this.options.selectedItemsNameSubstituteString != null) {
					result = this.options.selectedItemsNameSubstituteString;
				}
				return result;
			},

            populateDataWithAllowedScript: function AssociationSelectOne_populateSelect() {
                var context = this;
                if (this.options.allowedNodesScript && this.options.allowedNodesScript != "") {
                    Alfresco.util.Ajax.request({
                        method: "GET",
                        requestContentType: "application/json",
                        responseContentType: "application/json",
                        url: Alfresco.constants.PROXY_URI_RELATIVE + this.options.allowedNodesScript,
                        successCallback: {
                            fn: function (response) {
                                context.options.allowedNodes = response.json.nodes;
                                context._createSelectedControls();
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function onFailure(response) {
                                context.options.allowedNodes = null;
                                context._createSelectedControls();
                            },
                            scope: this
                        },
                        execScripts: true
                    });

                } else {
                    context._createSelectedControls();
                }
            },

			createPickerDialog: function()
			{
				this.widgets.ok = new YAHOO.widget.Button(this.options.controlId + "-ok",
					{ onclick: { fn: this.onOk, obj: null, scope: this } });
				this.widgets.cancel = new YAHOO.widget.Button(this.options.controlId + "-cancel",
					{ onclick: { fn: this.onCancel, obj: null, scope: this } });

				this.widgets.dialog = Alfresco.util.createYUIPanel(this.options.pickerId,
					{
						width: "500px"
					});
				this.widgets.dialog.hideEvent.subscribe(this.onCancel, null, this);

				Dom.addClass(this.options.pickerId, "object-finder");
			},

			onOk: function(e, p_obj)
			{
				Dom.setStyle(Dom.get(this.widgets.dialog.id),"display", "none");
				// Close dialog
				this.widgets.escapeListener.disable();
				this.widgets.dialog.hide();
				this.widgets.pickerButton.set("disabled", false);
				if (e) {
					Event.preventDefault(e);
				}
				// Update parent form
				this.updateFormFields();

				if (this.options.fireAction.ok != null) {
					var fireName = this.options.fireAction.ok.split(",");
					for (var i in fireName){
						YAHOO.Bubbling.fire(fireName[i], this);
					}
				}
			},

			onCancel: function(e, p_obj)
			{
				Dom.setStyle(Dom.get(this.widgets.dialog.id),"display", "none");
				this.widgets.escapeListener.disable();
				this.widgets.dialog.hide();
				if( this.widgets.pickerButton )
					this.widgets.pickerButton.set("disabled", false);
				if (e) {
					Event.preventDefault(e);
				}
				if (this.options.fireAction.cancel != null) {
					var fireName = this.options.fireAction.cancel.split(",");
					for (var i in fireName){
						YAHOO.Bubbling.fire(fireName[i], this);
					}
				}
			},

			showTreePicker: function AssociationTreeViewer_showTreePicker(e, p_obj)
			{
				if(!this.widgets.dialog) {
					return;
				}

				// Enable esc listener
				if (!this.widgets.escapeListener)
				{
					this.widgets.escapeListener = new KeyListener(this.options.pickerId,
						{
							keys: KeyListener.KEY.ESCAPE
						},
						{
							fn: function(eventName, keyEvent)
							{
								this.onCancel();
								Event.stopEvent(keyEvent[1]);
							},
							scope: this,
							correctScope: true
						});
				}
				this.widgets.escapeListener.enable();

				// Disable picker button to prevent double dialog call
				this.widgets.pickerButton.set("disabled", true);

				Dom.setStyle(Dom.get(this.widgets.dialog.id),"display", "block");
				// Show the dialog
				this.widgets.dialog.show();
				if (Dom.get(this.options.controlId + "-selectedItems") != null) {
					this.options.selectedValue = Dom.get(this.options.controlId + "-selectedItems").value;
				}

				Event.preventDefault(e);
			},

			updateSelectedItems: function AssociationTreeViewer_updateSelectedItems() {
				var items = this.selectedItems;
				var fieldId = this.options.pickerId + "-selected-elements";
				Dom.get(fieldId).innerHTML = '';
				Dom.get(fieldId).className = 'currentValueDisplay';

				var num = 0;
				for (i in items) {
					if (this.notShowedSelectedValue[i] == null) {
						var displayName = this.selectedItems[i].selectedName;

						var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
						if (this.options.itemType == "lecm-orgstr:employee") {
							Dom.get(fieldId).innerHTML
								+= '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
						} else {
							Dom.get(fieldId).innerHTML
								+= '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i]) + '</div>';
						}
						YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef, this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "", updateForms: false}, this);
					}
				}
			},

            checkSearchField: function AssociationTreeViewer_checkSearchField() {
                var term = Dom.get(this.options.pickerId + "-searchText").value;
                if (term == null || term == "") {
                    this.widgets.searchButton.set("disabled", true);
                } else {
                    this.widgets.searchButton.set("disabled", false);
                }
            }
		});
})();
