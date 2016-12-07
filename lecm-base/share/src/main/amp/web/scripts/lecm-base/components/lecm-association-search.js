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

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
		KeyListener = YAHOO.util.KeyListener,
		Util = LogicECM.module.Base.Util;

	var $html = Alfresco.util.encodeHTML,
		$combine = Alfresco.util.combinePaths,
		$hasEventInterest = Alfresco.util.hasEventInterest,
		$siteURL = Alfresco.util.siteURL;

	LogicECM.module.AssociationSearchViewer = function(htmlId)
	{
		LogicECM.module.AssociationSearchViewer.superclass.constructor.call(this, "AssociationSearchViewer", htmlId);
		YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
		YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
		YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);

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

			readonly: false,

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

				titleNameSubstituteString: null,

                sortProp: "cm:name",

				sortSelected: false,

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

                useStrictFilterByOrg: false,
				doNotCheckAccess: false,

				ignoreNodes: null,

                allowedNodes:null,

                allowedNodesScript: null,

				showSelectedItems: true,

				createDialog: false,

				childrenDataSource: "lecm/forms/picker",

				viewUrl: null,

				checkSearchColumnDataType: true,

				fieldId: null,

				formId: false
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

                if (!this.options.disabled) {
    				this._loadDefaultValue();
                } else {
                    this._loadSelectedItems();
                }

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
				LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
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
				var me = this;
				Alfresco.util.Ajax.jsonGet(
					{
						url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "/lecm/components/datagrid/config/columns?formId=searchColumns&itemType=" + encodeURIComponent(this.options.itemType)),
						successCallback:
						{
							fn: function (response) {
								var columns = response.json.columns;
								for (var i = 0; i < columns.length; i++) {
									var column = columns[i];
									if (!me.options.checkSearchColumnDataType || column.dataType == "text" || column.dataType == "mltext") {
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
								sortProp: this.options.sortProp,
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
                    if (searchTerm != undefined && searchTerm != null && searchTerm.trim() != ""){
                        for(var column in this.searchProperties) {
                            searchData += column + ":" + searchTerm.trim() + "#";
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

					if (scope.options.viewUrl != null) {
						if (scope.options.titleNameSubstituteString != null) {
							template += '<h3 class="item-name" title="{title}"><a href="' + scope.options.viewUrl + '" target="blank">{name}</a></h3>';
						} else {
							template += '<h3 class="item-name"><a href="' + scope.options.viewUrl + '" target="blank">{name}</a></h3>';
						}
					} else {
						if (scope.options.titleNameSubstituteString != null) {
							template += '<h3 class="item-name" title="{title}">{name}</h3>';
						} else {
							template += '<h3 class="item-name">{name}</h3>';
						}
					}

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
							//var response = YAHOO.lang.JSON.parse(oResponse.responseText);
							var response = {
								message:this.msg("search.document.filter.error")
							};
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
				var additionalFilter = this.options.additionalFilter;
				var allowedNodesFilter = "";
				var notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i;
				var singleNotQuery;

				if (this.options.allowedNodes) {
					if (this.options.allowedNodes.length) {
						for (var i in this.options.allowedNodes) {
							if (allowedNodesFilter.length > 0) {
								allowedNodesFilter += " OR ";
							}
							allowedNodesFilter += "ID:\"" + this.options.allowedNodes[i] + "\"";
						}
					} else {
						allowedNodesFilter = '(ISNULL:"sys:node-dbid" OR NOT EXISTS:"sys:node-dbid")';
					}

					if (additionalFilter != null && additionalFilter.length > 0) {
						singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);
						additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND (" + allowedNodesFilter + ")";
					} else {
						additionalFilter = allowedNodesFilter;
					}
				}

				if (this.options.ignoreNodes != null && this.options.ignoreNodes.length > 0) {
					var ignoreNodesFilter = "";
					for (var i = 0; i < this.options.ignoreNodes.length; i++) {
						if (ignoreNodesFilter !== "") {
							ignoreNodesFilter += " AND ";
						}
						ignoreNodesFilter += "NOT ID:\"" + this.options.ignoreNodes[i] + "\"";
					}

					var addBrackets = this.options.ignoreNodes.length > 1;
					if (additionalFilter != null && additionalFilter.length > 0) {
						singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);
						additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND " + (addBrackets ? "(" : "") + ignoreNodesFilter + (addBrackets ? ")" : "");
					} else {
						additionalFilter = ignoreNodesFilter;
					}
				}

				var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
					"&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
                    "&sortProp=" + encodeURIComponent(this.options.sortProp) +
					"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
					"&additionalFilter=" + encodeURIComponent(additionalFilter) +
                    "&onlyInSameOrg=" + encodeURIComponent("" + this.options.useStrictFilterByOrg) +
                    "&doNotCheckAccess=" + encodeURIComponent("" + this.options.doNotCheckAccess);

				if (this.options.rootLocation && this.options.rootLocation.charAt(0) == "/") {
					params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
				}

				if (this.options.titleNameSubstituteString != null) {
					params += "&titleNameSubstituteString=" + encodeURIComponent(this.options.titleNameSubstituteString);
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

            getDefaultView: function (displayValue, width100, item) {
				var result = "<span class='not-person" + (width100 ? " width100" : "") + "'>";
	            if (this.options.viewUrl != null && item != null && item.nodeRef != null) {
		            var href = YAHOO.lang.substitute(this.options.viewUrl, {
			            nodeRef: item.nodeRef
		            });

		            result += "<a href='" + href + "' target='blank'>" + displayValue + "</a>";
	            } else {
		            result += displayValue;
	            }
	            result += "</span>";
	            return result;
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

			getRemoveButtonHTML: function AssociationSearchViewer_getRemoveButtonHTML(node, dopId) {
				return Util.getControlItemRemoveButtonHTML("t-" + this.options.controlId + node.nodeRef + dopId);
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

				var items = this.getSelectedItems(!!this.options.sortSelected);
				items.forEach(function(i, index, array){
					if (this.notShowedSelectedValue[i] == null) {
						var displayName = this.selectedItems[i].selectedName;

						if(this.options.disabled) {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(this.selectedItems[i].nodeRef, displayName));
							} else {
								el.innerHTML += Util.getCroppedItem(this.getDefaultView(displayName, false, this.selectedItems[i]));
							}
						} else {
							if (this.options.itemType == "lecm-orgstr:employee") {
								el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(this.selectedItems[i].nodeRef, displayName), this.getRemoveButtonHTML(this.selectedItems[i], "_c1"));
							} else {
								el.innerHTML += Util.getCroppedItem(this.getDefaultView(displayName, false, this.selectedItems[i]), this.getRemoveButtonHTML(this.selectedItems[i], "_c1"));
							}
							YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c1", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c1", updateForms: true}, this);
						}
					}
				}, this);

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

			getSelectedItems:function AssociationSearchViewer_getSelectedItems(sort) {
				var selectedItems = [], me = this;

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				if(sort){
					selectedItems = selectedItems.sort(function (a, b) {
						return me.selectedItems[a].name.localeCompare(me.selectedItems[b].name);
					});
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
                this.backToControl();
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
                this.backToControl();
            },

            // после закрытия диалога вернуть фокус в исходный контрол
            backToControl: function() {
                var controlBtn = Dom.get(this.options.controlId + "-tree-picker-button-button");
                if (controlBtn) {
                    controlBtn.focus();
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

                this.setTabbingOrder();

                Event.preventDefault(e);
			},

            // Set properly tabbing order
            setTabbingOrder: function() {
                this.activeClass = "active";
                this.firstTabbed = null;

                var me = this;
                var dialog = Dom.get(this.widgets.dialog.id);

                if (dialog && dialog.offsetHeight > 0) {
                    var tabindex = (++LogicECM.module.Base.Util.tabNum) * 100; // подразумеваем, что на основной странице tabindex, равный этому значению, не был достигнут

                    var search = Selector.query(".control", dialog, true);
                    if (search) {
                        var input = Selector.query("input", search, true);
                        Dom.setAttribute(input, "tabindex", ++tabindex);
                        this.firstTabbed = input.id;
                    }

                    var tables = Selector.query("div.picker-items, div.currentValueDisplay", dialog);
                    for (var i = 0; i < tables.length; i++) {
                        // Для всех
                        var table = tables[i];
                        Dom.setAttribute(table, 'tabindex', ++tabindex);
                        if (!this.firstTabbed) {
                            this.firstTabbed = table.id;
                        }

                        Event.on(table, "focusout", function(e) {
                            var activeEl = me.activeElement;
                            if (activeEl) {
                                Dom.removeClass(activeEl, me.activeClass);
                            }
                        });

                        new KeyListener(table, {keys: KeyListener.KEY.DOWN},
                            {
                                fn: this.focusToNext,
                                scope: this,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                        new KeyListener(table, {keys: KeyListener.KEY.UP},
                            {
                                fn: this.focusToPrevious,
                                scope: this,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();

                        // Таблица с элементами для выбора
                        if (Dom.hasClass(table, "picker-items") && !Dom.hasClass(table, "tree-items")) {
                            Event.on(table, "focusin", function (e) {
                                var rows = Selector.query("tbody tr.yui-dt-rec", e.target);
                                if (rows && rows.length > 0) {
                                    Dom.addClass(rows[0], me.activeClass);
                                    me.activeElement = rows[0];
                                    e.target.scrollTop = 0;
                                }
                            });

                            new KeyListener(table, {keys: KeyListener.KEY.ENTER},
                                {
                                    fn: function () {
                                        var activeEl = this.activeElement;
                                        if (activeEl) {
                                            var addIcon = Selector.query("td a.add-item", activeEl, true);
                                            if (addIcon) {
                                                addIcon.click();
                                            }
                                        }
                                    },
                                    scope: this,
                                    correctScope: true
                                }, KeyListener.KEYDOWN).enable();
                            // Выбранные элементы
                        } else if (Dom.hasClass(table, "currentValueDisplay")) {
                            Event.on(table, "focusin", function (e) {
                                var rows = Selector.query("div.cropped-item", e.target);
                                if (rows && rows.length > 0) {
                                    Dom.addClass(rows[0], me.activeClass);
                                    me.activeElement = rows[0];
                                    e.target.scrollTop = 0;
                                }
                            });

                            new KeyListener(table, {keys: KeyListener.KEY.ENTER},
                                {
                                    fn: function () {
                                        var activeEl = this.activeElement;
                                        if (activeEl) {
                                            var removeIcon = Selector.query("div a.remove-item", activeEl, true);
                                            if (removeIcon) {
                                                removeIcon.click();

                                                var selectedElsTable = Selector.query("div.currentValueDisplay", dialog, true);
                                                var rows = Selector.query("div.cropped-item", selectedElsTable);
                                                if (rows && rows.length > 0) {
                                                    selectedElsTable.focus();
                                                    Dom.addClass(rows[0], me.activeClass);
                                                    me.activeElement = rows[0];
                                                } else {
                                                    Selector.query("div.picker-items", dialog, true).focus();
                                                }
                                            }
                                        }
                                    },
                                    scope: this,
                                    correctScope: true
                                }, KeyListener.KEYDOWN).enable();
                        }
                    }
                    // Футер, кнопки
                    var footer = Selector.query("div.bdft", dialog, true);
                    if (footer) {
                        var btns = Selector.query("span.yui-button button", footer);
                        for (var i = 0; i < btns.length; i++) {
                            Dom.setAttribute(btns[i], "tabindex", ++tabindex);
                        }
                        new KeyListener(btns[btns.length - 1], {keys: KeyListener.KEY.TAB},
                            {
                                fn: function() {
                                    if (me.firstTabbed) {
                                        Dom.get(me.firstTabbed).focus();
                                    }
                                },
                                scope: this,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                    }

                    if (this.firstTabbed) {
                        Dom.get(this.firstTabbed).focus();
                    }
                }
            },

            focusToNext: function(a, args) {
                var activeEl = this.activeElement;
                if (activeEl) {
                    var next = Dom.getNextSibling(activeEl);
                    if (next) {
                        Dom.removeClass(activeEl, this.activeClass);
                        Dom.addClass(next, this.activeClass);
                        this.activeElement = next;

                        // делаем, чтобы скроллинг списка работал, но только когда это нужно
                        var e = args[1],
                            target = args[1].target;
                        activeEl = this.activeElement;
                        var scrollEnded = target.scrollHeight - target.scrollTop <= target.offsetHeight;
                        if ((Dom.getY(activeEl) < Dom.getY(target) + target.offsetHeight / 2) || scrollEnded) {
                            e.preventDefault();
                        }
                    }
                    e.stopImmediatePropagation();
                    e.stopPropagation();
                }
            },

            focusToPrevious: function(a, args) {
                var activeEl = this.activeElement;
                if (activeEl) {
                    var prev = Dom.getPreviousSibling(activeEl);
                    if (prev) {
                        Dom.removeClass(activeEl, this.activeClass);
                        Dom.addClass(prev, this.activeClass);
                        this.activeElement = prev;

                        // делаем, чтобы скроллинг списка работал, но только когда это нужно
                        var e = args[1],
                            target = args[1].target;
                        activeEl = this.activeElement;
                        var scrollEnded = target.scrollTop == 0;
                        if ((Dom.getY(activeEl) > Dom.getY(target) + target.offsetHeight / 2) || scrollEnded) {
                            e.preventDefault();
                        }
                    }
                    e.stopImmediatePropagation();
                    e.stopPropagation();
                }
            },

            updateSelectedItems: function AssociationTreeViewer_updateSelectedItems() {
				var items = this.getSelectedItems(!!this.options.sortSelected);
				var fieldId = this.options.pickerId + "-selected-elements";
				Dom.get(fieldId).innerHTML = '';
				Dom.get(fieldId).className = 'currentValueDisplay';

				items.forEach(function(i, index, array){
					if (this.notShowedSelectedValue[i] == null) {
						var displayName = this.selectedItems[i].selectedName;

						if (this.options.itemType == "lecm-orgstr:employee") {
							Dom.get(fieldId).innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(this.selectedItems[i].nodeRef, displayName), this.getRemoveButtonHTML(this.selectedItems[i], "_c2"));
						} else {
							Dom.get(fieldId).innerHTML += Util.getCroppedItem(this.getDefaultView(displayName, false, this.selectedItems[i]), this.getRemoveButtonHTML(this.selectedItems[i], "_c2"));
						}
						YAHOO.util.Event.onAvailable("t-" + this.options.controlId + this.selectedItems[i].nodeRef + "_c2", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c2", updateForms: false}, this);
					}
				}, this);
			},

            checkSearchField: function AssociationTreeViewer_checkSearchField() {
                var term = Dom.get(this.options.pickerId + "-searchText").value;
                if (term == null || term == "") {
                    this.widgets.searchButton.set("disabled", true);
                } else {
                    this.widgets.searchButton.set("disabled", false);
                }
            },

			onReadonlyControl: function (layer, args) {
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					if (this.widgets.pickerButton) {
						this.widgets.pickerButton.set('disabled', args[1].readonly);
					}
					//непонятно зачем скрывать диалог и активировать поля, которые не были деактивированы...
					if (!args[1].readonly) {
						if (this.widgets.dialog) {
							this.widgets.dialog.hide();
						}
					}
				}
			},

			onDisableControl: function (layer, args) {
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					if (this.widgets.pickerButton) {
						this.widgets.pickerButton.set('disabled', true);
					}
					var input = Dom.get(this.id);
					if (input) {
						input.disabled = true;
					}
					var added = Dom.get(this.options.controlId + "-added");
					if (added) {
						added.disabled = true;
					}
					var removed = Dom.get(this.options.controlId + "-removed");
					if (removed) {
						removed.disabled = true;
					}
					this.tempDisabled = true;
				}
			},

			onEnableControl: function (layer, args) {
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					if (!this.options.disabled) {
						if (this.widgets.pickerButton) {
							this.widgets.pickerButton.set('disabled', false);

							if (this.widgets.dialog) {
								this.widgets.dialog.hide();
							}
						}
						var input = Dom.get(this.id);
						if (input) {
							input.disabled = false;
						}
						var added = Dom.get(this.options.controlId + "-added");
						if (added) {
							added.disabled = false;
						}
						var removed = Dom.get(this.options.controlId + "-removed");
						if (removed) {
							removed.disabled = false;
						}
					}
					this.tempDisabled = false;
				}
			}
		});
})();
