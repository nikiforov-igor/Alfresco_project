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

    var IDENT_CREATE_NEW = "~CREATE~NEW~";

    LogicECM.module.AssociationTreeViewer = function(htmlId)
	{
        LogicECM.module.AssociationTreeViewer.superclass.constructor.call(this, "AssociationTreeViewer", htmlId);
        YAHOO.Bubbling.on("refreshItemList", this.onRefreshItemList, this);
        YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);

        this.selectedItems = {};
        this.addItemButtons = {};
        this.searchProperties = {};
        this.currentNode = null;
        this.rootNode = null;
        this.tree = null;
        this.isSearch = false;
        this.allowedNodes = null;
        this.allowedNodesScript = null;
		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationTreeViewer, Alfresco.component.Base,
	{
        tree: null,

        eventGroup: null,

        singleSelectedItem: null,

        selectedItems: null,

        addItemButtons: null,

        currentNode: null,

        rootNode: null,

        searchProperties: null,

        isSearch: false,

        doubleClickLock: false,

		options:
		{
			// скрывать ли игнорируемые ноды в дереве
			ignoreNodesInTreeView: true,
			prefixPickerId: null,

            showCreateNewLink: true,

            setCurrentValue: true,

			createNewMessage: null, //message id по которому будет сформирован заголовок диалогового окна

			showSearch: true,

			showSelectedItemsPath: true,

            changeItemsFireAction: null,

            selectedValue: null,

            plane: false,

            currentValue: "",
			// If control is disabled (has effect in 'picker' mode only)
			disabled: false,
			// If this form field is mandatory
			mandatory: false,
			// If control allows to pick multiple assignees (has effect in 'picker' mode only)
			multipleSelectMode: false,

			initialized: false,

            rootLocation: null,

            rootNodeRef: "",

            itemType: "cm:content",

			treeItemType: null,

            maxSearchResults: 1000,

            treeRoteNodeTitleProperty: "cm:name",

            treeNodeSubstituteString: "{cm:name}",

            treeNodeTitleSubstituteString: "",

            nameSubstituteString: "{cm:name}",

			selectedItemsNameSubstituteString: null,
			// при выборе сотрудника в контроле отображать, доступен ли он в данный момент и если недоступен, то показывать его автоответ
			employeeAbsenceMarker: false,

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

            defaultValue: null,

	        defaultValueDataSource: null,

            ignoreNodes: null,

			childrenDataSource: "lecm/forms/picker",

            allowedNodes: null,

            allowedNodesScript: null,

            createDialogClass: "",

            clearFormsOnStart: true,

			pickerButtonLabel: null,

			pickerButtonTitle: null
		},

		onReady: function AssociationTreeViewer_onReady()
		{
			if(!this.options.initialized) {
				this.options.initialized = true;
				this.init();
			}
		},

		init: function()
		{
			this.options.controlId = this.id + '-cntrl';
			if (this.options.prefixPickerId == null) {
				this.options.prefixPickerId = this.options.controlId;
			}
			this.eventGroup = this.options.prefixPickerId + Dom.generateId();

			this.options.pickerId = this.options.prefixPickerId + '-picker';
			Dom.setStyle(this.options.pickerId, "display", "block");

            // Create button if control is enabled
            if(!this.options.disabled)
            {
	            var buttonOptions = {
		            onclick: {
			            fn: this.showTreePicker,
			            obj: null,
			            scope: this
		            },
		            title: this.options.pickerButtonTitle
	            };
	            if (this.options.pickerButtonLabel != null) {
		            buttonOptions.label = this.options.pickerButtonLabel;
	            }

                // Create picker button
	            var buttonName = Dom.get(this.options.prefixPickerId + "-tree-picker-button").name;
                this.widgets.pickerButton =  new YAHOO.widget.Button(this.options.prefixPickerId + "-tree-picker-button", buttonOptions);

	            Dom.get(this.options.prefixPickerId + "-tree-picker-button-button").name = buttonName;

                if (this.options.showCreateNewLink) {
                    this.widgets.createNewButton =  new YAHOO.widget.Button(
	                    this.options.prefixPickerId + "-tree-picker-create-new-button",
                        { onclick: { fn: this.showCreateNewItemWindow, obj: null, scope: this } }
                        );
                }

                this.populateDataWithAllowedScript();
                this.createPickerDialog();
                this._loadSearchProperties();
                this.loadDefaultValue();
            } else {
	            this.updateViewForm();
            }
		},

        loadDefaultValue: function AssociationAutoComplete__loadDefaultValue() {
		        if (this.options.defaultValue != null) {
                     this.defaultValue = this.options.defaultValue;
                     this.updateViewForm();
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
							        me.updateViewForm();
						        }
					        },
					        failureMessage: "message.failure"
				        });
		        }
	    },

        showCreateNewItemWindow: function AssociationTreeViewer_showCreateNewItemWindow() {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
            var templateRequestParams = this.generateCreateNewParams(this.options.rootNodeRef, this.options.itemType);
			templateRequestParams["createNewMessage"] = this.options.createNewMessage;

            new Alfresco.module.SimpleDialog("create-new-form-dialog-" + this.eventGroup).setOptions({
                width:"50em",
                templateUrl: "lecm/components/form",
				templateRequestParams: templateRequestParams,
                actionUrl:null,
                destroyOnHide:true,
                doBeforeDialogShow:{
                    fn:this.doBeforeDialogShow,
                    scope:this
                },
                onSuccess:{
                    fn:function (response) {
                        this.addSelectedItem(response.json.persistedObject);
                        this._updateItems(this.options.rootNodeRef, "");
                        this.doubleClickLock = false;
                    },
                    scope:this
                },
                onFailure: {
                    fn:function (response) {
                        this.doubleClickLock = false;
                    },
                    scope:this
                }
            }).show();
        },

        _loadSearchProperties: function AssociationTreeViewer__loadSearchProperties() {
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

        _loadSelectedItems: function AssociationTreeViewer__loadSelectedItems(clearCurrentDisplayValue, updateForms)
        {
            var arrItems = "";
            if (this.options.selectedValue != null)
            {
                arrItems = this.options.selectedValue;
            }
            else if (this.options.currentValue != null && this.isNodeRef(this.options.currentValue))
            {
                arrItems = this.options.currentValue;
            }

            if (arrItems == "" && this.defaultValue != null) {
		        arrItems += this.defaultValue;
	        }

            var onSuccess = function AssociationTreeViewer__loadSelectedItems_onSuccess(response)
            {
                var items = response.json.data.items,
                    item;
                this.selectedItems = {};

	            this.singleSelectedItem = null;
                for (var i = 0, il = items.length; i < il; i++) {
                    item = items[i];
	                if (item.type == this.options.itemType) {
                        this.selectedItems[item.nodeRef] = item;

		                if (!this.options.multipleSelectMode && this.singleSelectedItem == null) {
			                this.singleSelectedItem = item;
		                }
	                }
                }

                if(!this.options.disabled)
                {
                    this.updateSelectedItems();
                    this.updateAddButtons();
                }
	            if (updateForms) {
                    this.updateFormFields(clearCurrentDisplayValue);
                }
            };

            var onFailure = function AssociationTreeViewer__loadSelectedItems_onFailure(response)
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
	                this.updateSelectedItems();
	                this.updateAddButtons();
	            } else if (Dom.get(this.options.controlId + "-currentValueDisplay") != null) {
                    Dom.get(this.options.controlId + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
                }
            }
        },

		isNodeRef: function (value)	{
			var regexNodeRef = new RegExp(/^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/);
			var result = false;
			try {
				result = regexNodeRef.test(String(value));
			}
			catch (e){}
			return result;
		},

        addSelectedItem: function AssociationTreeViewer_addSelectedItems(nodeRef) {
            var onSuccess = function AssociationTreeViewer_addSelectedItems_onSuccess(response)
            {
                var items = response.json.data.items,
                    item;

                //this.singleSelectedItem = null;
                if (!this.options.multipleSelectMode && items[0]) {
                    this.selectedItems = {};
                    item = items[0];
                    this.selectedItems[item.nodeRef] = item;
                    this.singleSelectedItem = items[0];
                } else {
                    for (var i = 0, il = items.length; i < il; i++)
                    {
                        item = items[i];
                        this.selectedItems[item.nodeRef] = item;
                    }
                }

                if(!this.options.disabled)
                {
                    this.updateSelectedItems();
                    this.updateAddButtons();
                }
                this.updateFormFields();
            };

            var onFailure = function AssociationTreeViewer_addSelectedItems_onFailure(response) {

            };

            if (nodeRef !== "")
            {
                Alfresco.util.Ajax.jsonRequest(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
                        method: "POST",
                        dataObj:
                        {
                            items: nodeRef.split(","),
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
        },

        createPickerDialog: function()
        {
            var me = this;

            this.widgets.ok = new YAHOO.widget.Button(this.options.prefixPickerId + "-ok",
                { onclick: { fn: this.onOk, obj: null, scope: this } });
            this.widgets.cancel = new YAHOO.widget.Button(this.options.prefixPickerId + "-cancel",
                { onclick: { fn: this.onCancel, obj: null, scope: this } });

            this.widgets.dialog = Alfresco.util.createYUIPanel(this.options.pickerId,
                {
                    width: "500px"
                });
            this.widgets.dialog.hideEvent.subscribe(this.onCancel, null, this);

	        if (this.options.showSearch) {
	            // Setup search button
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
	        }

            // Create tree in the dialog
            this.fillPickerDialog();

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
            if(this.options.mandatory) {
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            }
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

        onSearch: function()
        {
            var nodeRef = this.options.rootNodeRef;
            if (this.currentNode != null) {
                nodeRef = this.currentNode.data.nodeRef;
            }
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
			if (this.option)
            this.isSearch = true;
            this._updateItems(nodeRef, searchData);
        },

        // Render dialog with tree picker
        showTreePicker: function AssociationTreeViewer_showTreePicker(e, p_obj)
        {
            if( ! this.widgets.dialog )
                return;

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
            this._loadSelectedItems(true, false);

            Event.preventDefault(e);
        },

        // Fill tree view group selector with node data
        fillPickerDialog: function AssociationTreeViewer_fillPickerDialog()
        {
            if (!this.options.plane) {
                this.tree = new YAHOO.widget.TreeView(this.options.pickerId + "-groups");
                this.tree.singleNodeHighlight = true;
                this.tree.setDynamicLoad(this._loadNode.bind(this));

                this.tree.subscribe('clickEvent', function(event) {
                    this.treeViewClicked(event.node);
                    this.tree.onEventToggleHighlight(event);
                    return false;
                }.bind(this));

            }
            this._loadRootNode();
        },

        _loadRootNode: function AssociationTreeViewer__loadRootNode() {
            var sUrl = this._generateRootUrlPath(this.options.rootNodeRef) + this._generateRootUrlParams();

            Alfresco.util.Ajax.jsonGet(
                {
                    url: sUrl,
                    successCallback:
                    {
                        fn: function (response) {
                            var oResults = response.json;
                            if (oResults != null) {
                                if (!this.options.plane) {
                                    var newNode = {
                                        label:oResults.title,
                                        nodeRef:oResults.nodeRef,
                                        isLeaf:oResults.isLeaf,
                                        type:oResults.type,
                                        isContainer: oResults.isContainer,
                                        displayPath: oResults.displayPath,
                                        renderHidden:true
                                    };
                                    this.rootNode = new YAHOO.widget.TextNode(newNode, this.tree.getRoot());
                                    this.options.rootNodeRef = oResults.nodeRef;

                                    this.tree.render();
                                    this.treeViewClicked(this.rootNode);
                                    this.tree.onEventToggleHighlight(this.rootNode);
                                } else {
                                    this.options.rootNodeRef = oResults.nodeRef;
                                    this.treeViewClicked(
                                        {
                                            data: {
                                                isContainer: true,
                                                nodeRef: this.options.rootNodeRef
                                            }
                                        });
                                }
                                this._loadSelectedItems(this.options.clearFormsOnStart, true);
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
                        scope: this
                    }
                });
        },

        _generateRootUrlPath: function AssociationTreeViewer__generateItemsUrlPath(nodeRef)
        {
            return $combine(Alfresco.constants.PROXY_URI, "/lecm/forms/node/search", nodeRef.replace("://", "/"));
        },

        _generateRootUrlParams: function AssociationTreeViewer__generateItemsUrlParams()
        {
            var params = "?titleProperty=" + encodeURIComponent(this.options.treeRoteNodeTitleProperty);
            if (this.options.rootLocation && this.options.rootLocation.charAt(0) == "/")
            {
                params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
            } else if (this.options.xPathLocation)
            {
                params += "&xPathLocation=" + encodeURIComponent(this.options.xPathLocation);
                if (this.options.xPathLocationRoot != null) {
                    params += "&xPathRoot=" + encodeURIComponent(this.options.xPathLocationRoot);
                }
            }

            return params;
        },

        _loadNode:function AssociationTreeViewer__loadNode(node, fnLoadComplete) {
            var sUrl = this._generateItemsUrlPath(node.data.nodeRef) + this._generateItemsUrlParams();

            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        node.children = [];
                        for (var nodeIndex in oResults) {
                            var nodeRef = oResults[nodeIndex].nodeRef;
                            var ignore = false;
							if (this.argument.context.options.ignoreNodesInTreeView) {
								var ignoreNodes = this.argument.context.options.ignoreNodes;
								if (ignoreNodes != null) {
									for (var i = 0; i < ignoreNodes.length; i++) {
										if (ignoreNodes[i] == nodeRef) {
											ignore = true;
										}
									}
								}
							}

                            if (!ignore) {
                                var newNode = {
                                    label:oResults[nodeIndex].label,
                                    title:oResults[nodeIndex].title,
                                    nodeRef:oResults[nodeIndex].nodeRef,
                                    isLeaf:oResults[nodeIndex].isLeaf,
                                    type:oResults[nodeIndex].type,
                                    isContainer: oResults[nodeIndex].isContainer,
                                    renderHidden:true
                                };

                                new YAHOO.widget.TextNode(newNode, node);
                            }
                        }
                    }

                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    } else {
                        oResponse.argument.tree.render();
                    }
                },
                failure:function (oResponse) {
                    var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                    this.widgets.dataTable.set("MSG_ERROR", response.message);
                    this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                },
                argument:{
                    node:node,
                    fnLoadComplete:fnLoadComplete,
                    tree:this.tree,
                    context: this
                },
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        },

        _generateItemsUrlPath: function AssociationTreeViewer__generateItemsUrlPath(nodeRef)
        {
            return $combine(Alfresco.constants.PROXY_URI, "/lecm/components/association-tree/", nodeRef.replace("://", "/"), "items");
        },

        _generateItemsUrlParams: function AssociationTreeViewer__generateItemsUrlParams()
        {

	        var params = "?nodeSubstituteString=" + encodeURIComponent(this.options.treeNodeSubstituteString) +
		        "&nodeTitleSubstituteString=" + encodeURIComponent(this.options.treeNodeTitleSubstituteString);
	        if (this.options.treeItemType != null) {
		        params += "&selectableType=" + encodeURIComponent(this.options.treeItemType);
	        } else {
		        params += "&selectableType=" + encodeURIComponent(this.options.itemType);
	        }
            return params;
        },

        treeViewClicked: function AssociationTreeViewer_treeViewClicked(node)
        {
            this.currentNode = node;
            this.isSearch = false;
            this._updateItems(node.data.nodeRef, "");
        },

        _createSelectedControls: function AssociationTreeViewer__createSelectedControls()
        {
            var me = this;

            // DataSource definition
            var pickerChildrenUrl = Alfresco.constants.PROXY_URI + this.options.childrenDataSource + "/node";
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

            this.widgets.dataSource.doBeforeParseData = function AssociationTreeViewer_doBeforeParseData(oRequest, oFullResponse)
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

                    // Add the special "Create new" record if required
                    if (me.options.showCreateNewLink && me.currentNode != null && me.currentNode.data.isContainer && (!me.isSearch || me.options.plane))
                    {
                        items = [{ type: IDENT_CREATE_NEW }].concat(items);
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

					if (me.options.employeeAbsenceMarker) {
						me.getEmployeesAbsenceInformation(items);
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

            var initialMessage = this.msg("logicecm.base.select-tree-element");

            this.widgets.dataTable = new YAHOO.widget.DataTable(this.options.pickerId + "-group-members", columnDefinitions, this.widgets.dataSource,
                {
                    renderLoopSize: 100,
                    initialLoad: false,
                    MSG_EMPTY: initialMessage
                });

            // Hook add item action click events (for Compact mode)
            var fnAddItemHandler = function AssociationTreeViewer__createControls_fnAddItemHandler(layer, args)
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
						var recordData = record.getData();
                        YAHOO.Bubbling.fire("selectedItemAdded",
                            {
                                eventGroup: me,
                                item: recordData,
                                highlight: true
                            });
                        if (me.options.fireAction.addItem != null) {
                            var fireName = me.options.fireAction.addItem.split(",");
                            for (var i in fireName){
                                YAHOO.Bubbling.fire(fireName[i],
                                    {
                                        nodeRef: recordData.nodeRef
                                    });
                            }
                        }
						if (me.options.employeeAbsenceMarker && recordData.type === "lecm-orgstr:employee") {
							me.showEmployeeAutoAnswerPromt(recordData);
						}
                    }
                }
                return true;
            };
            YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddItemHandler, true);

            // Hook create new item action click events (for Compact mode)
            var fnCreateNewItemHandler = function AssociationTreeViewer__createControls_fnCreateNewItemHandler(layer, args)
            {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                var templateRequestParams = me.generateCreateNewParams(me.currentNode.data.nodeRef, me.options.itemType);
				templateRequestParams["createNewMessage"] = me.options.createNewMessage;

                new Alfresco.module.SimpleDialog("create-form-dialog-" + me.eventGroup).setOptions({
                    width:"50em",
                    templateUrl: "lecm/components/form",
					templateRequestParams: templateRequestParams,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn: me.doBeforeDialogShow,
						scope: me
                    },
                    onSuccess:{
                        fn:function (response) {
                            me.addSelectedItem(response.json.persistedObject);
                            me._updateItems(me.currentNode.data.nodeRef, "");
                            me.doubleClickLock = false;
                        },
                        scope:this
                    },
                    onFailure: {
                        fn:function (response) {
                            me.doubleClickLock = false;
                        },
                        scope:this
                    }
                }).show();
                return true;
            };
            YAHOO.Bubbling.addDefaultAction("create-new-item-" + this.eventGroup, fnCreateNewItemHandler, true);
        },

        generateCreateNewParams: function AssociationTreeViewer_generateCreateNewParams(nodeRef, itemType) {
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
            if ( this.options.createNewMessage ) {
                message = this.options.createNewMessage;
            } else {
                message = this.msg("dialog.createNew.title");
            }
            p_dialog.dialog.setHeader( message );

            Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
            if (this.options.createDialogClass != "") {
                Dom.addClass(p_dialog.id + "-form-container", this.options.createDialogClass);
            }
            this.doubleClickLock = false;
        },

        /**
         * Returns Name datacell formatter
         *
         * @method fnRenderItemName
         */
        fnRenderItemName: function AssociationTreeViewer_fnRenderItemName()
        {
            var scope = this;

            return function AssociationTreeViewer_renderItemName(elCell, oRecord, oColumn, oData)
            {
                var template = '';

                // Create New item cell type
                if (oRecord.getData("type") == IDENT_CREATE_NEW)
                {
					var msg;
					if ( scope.options.createNewMessage ) {
						msg = scope.options.createNewMessage;
					} else {
						msg = scope.msg("form.control.object-picker.create-new")
					}
                    elCell.innerHTML = '<a href="#" title="' + msg + '" class="create-new-item-' + scope.eventGroup + '" >' + msg + '</a>';
                    return;
                }

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
        fnRenderCellAdd: function AssociationTreeViewer_fnRenderCellAdd()
        {
            var scope = this;

            return function AssociationTreeViewer_renderCellAdd(elCell, oRecord, oColumn, oData)
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

        renderItem: function AssociationTreeViewer_renderItem(item, template)
        {
            var me = this;

            var renderHelper = function AssociationTreeViewer_renderItem_renderHelper(p_key, p_value, p_metadata)
            {
                return $html(p_value);
            };

            return YAHOO.lang.substitute(template, item, renderHelper);
        },

        canItemBeSelected: function AssociationTreeViewer_canItemBeSelected(id)
        {
            if (!this.options.multipleSelectMode && this.singleSelectedItem !== null)
            {
                return false;
            }
            return (this.selectedItems[id] === undefined);
        },

        onRefreshItemList: function AssociationTreeViewer_onRefreshItemList(layer, args)
        {
            // Check the event is directed towards this instance
            if ($hasEventInterest(this, args))
            {
                var searchTerm = "";
                var obj = args[1];
                if (obj && obj.searchTerm)
                {
                    searchTerm = obj.searchTerm;
                }
                this._updateItems(this.options.parentNodeRef, searchTerm);
            }
        },

        _updateItems: function AssociationTreeViewer__updateItems(nodeRef, searchTerm)
        {
            // Empty results table - leave tag entry if it's been rendered
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.loading"));
	        this.widgets.dataTable.showTableMessage(this.msg("label.loading"), YAHOO.widget.DataTable.CLASS_EMPTY);
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            var successHandler = function AssociationTreeViewer__updateItems_successHandler(sRequest, oResponse, oPayload)
            {
                this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : nodeRef;
                this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
                if (this.options.showCreateNewLink && this.currentNode != null && this.currentNode.data.isContainer && (!this.isSearch || this.options.plane))
                {
                    this.widgets.dataTable.onDataReturnAppendRows.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                }
                else
                {
                    this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                }
            };

            var failureHandler = function AssociationTreeViewer__updateItems_failureHandler(sRequest, oResponse)
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
            var url = this._generateChildrenUrlPath(nodeRef) + this._generateChildrenUrlParams(searchTerm);

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

        _generateChildrenUrlPath: function AssociationTreeViewer__generatePickerChildrenUrlPath(nodeRef)
        {
            // generate the path portion of the url
            return $combine("/", nodeRef.replace("://", "/"), "children");
        },

        _generateChildrenUrlParams: function AssociationTreeViewer__generatePickerChildrenUrlParams(searchTerm)
        {
            return "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
	            "&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
				"&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);
        },

        onSelectedItemAdded: function AssociationTreeViewer_onSelectedItemAdded(layer, args)
        {
            // Check the event is directed towards this instance
            if ($hasEventInterest(this, args))
            {
                var obj = args[1];
                if (obj && obj.item)
                {
                    this.selectedItems[obj.item.nodeRef] = obj.item;
                    this.singleSelectedItem = obj.item;

	                this.updateAddedSelectedItem(obj.item);
	                if (!this.options.multipleSelectMode) {
		                this.updateAddButtons();
	                } else if (this.addItemButtons.hasOwnProperty(obj.item.nodeRef)) {
		                var button = this.addItemButtons[obj.item.nodeRef];
		                Dom.setStyle(button, "display", this.canItemBeSelected(obj.item.nodeRef) ? "inline" : "none");
	                }
                }
            }
        },

        removeNode: function AssociationTreeViewer_removeNode(event, params)
        {
            delete this.selectedItems[params.node.nodeRef];
            this.singleSelectedItem = null;
            this.updateSelectedItems();
            this.updateAddButtons();
	        if (params.updateForms) {
		        this.updateFormFields();
	        }
        },

        updateSelectedItems: function AssociationTreeViewer_updateSelectedItems() {
            var items = this.selectedItems;
            var fieldId = this.options.pickerId + "-selected-elements";
            Dom.get(fieldId).innerHTML = '';
            Dom.get(fieldId).className = 'currentValueDisplay';

            var num = 0;
            for (i in items) {
	            if (typeof(items[i]) != "function") {
	                if (this.options.plane || !this.options.showSelectedItemsPath) {
	                    var displayName = items[i].selectedName;
	                } else {
	                    displayName = items[i].displayPath + "/" + items[i].selectedName;
		                      if (this.rootNode !== null && this.rootNode.data.displayPath !== null) {
	                        var rootNodeDisplayName = this.rootNode.data.displayPath + "/" + this.rootNode.label + "/";
	                        if (rootNodeDisplayName !== "") {
	                            displayName = displayName.replace(rootNodeDisplayName, "");
	                        }
	                    }
	                }

	                var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";

		            if (this.options.itemType == "lecm-orgstr:employee") {
			            Dom.get(fieldId).innerHTML
				            += '<div class="' + divClass + '"> ' + this.getEmployeeView(items[i].nodeRef, displayName) +
							(this.options.employeeAbsenceMarker ? this.getEmployeeAbsenceMarkerHTML(items[i].nodeRef) : ' ') + this.getRemoveButtonHTML(items[i]) + '</div>';
		            } else {
			            Dom.get(fieldId).innerHTML
				            += '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(items[i]) + '</div>';
		            }

	                YAHOO.util.Event.onAvailable("t-" + this.options.prefixPickerId + items[i].nodeRef, this.attachRemoveClickListener, {node: items[i], dopId: "", updateForms: false}, this);
	            }
            }
        },

		updateAddedSelectedItem: function(item) {
			var fieldId = this.options.pickerId + "-selected-elements";
			var num = Object.keys(this.selectedItems).length + 1;
			if (this.options.plane || !this.options.showSelectedItemsPath) {
				var displayName = item.selectedName;
			} else {
				displayName = item.displayPath + "/" + item.selectedName;
				if (this.rootNode !== null && this.rootNode.data.displayPath !== null) {
					var rootNodeDisplayName = this.rootNode.data.displayPath + "/" + this.rootNode.label + "/";
					if (rootNodeDisplayName !== "") {
						displayName = displayName.replace(rootNodeDisplayName, "");
					}
				}
			}

			var divClass = (num) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";

			if (this.options.itemType == "lecm-orgstr:employee") {
				Dom.get(fieldId).innerHTML
					+= '<div class="' + divClass + '"> ' + this.getEmployeeView(item.nodeRef, displayName) +
					(this.options.employeeAbsenceMarker ? this.getEmployeeAbsenceMarkerHTML(item.nodeRef) : ' ') + this.getRemoveButtonHTML(item) + '</div>';
			} else {
				Dom.get(fieldId).innerHTML
					+= '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(item) + '</div>';
			}

			var items = this.selectedItems;
			for (i in items) {
				if (typeof(items[i]) != "function") {
					YAHOO.util.Event.onAvailable("t-" + this.options.prefixPickerId + item.nodeRef, this.attachRemoveClickListener, {node: items[i], dopId: "", updateForms: false}, this);
				}
			}
		},

		getEmployeeView: function DataGrid_getSortFunction(employeeNodeRef, displayValue) {
			return "<span class='person'><a href='javascript:void(0);' title='" + displayValue + "' onclick=\"viewAttributes(\'" + employeeNodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
		},

        getDefaultView: function (displayValue) {
            return "<span class='not-person' title='" + displayValue + "'>" + displayValue + "</span>";
        },

        updateAddButtons: function AssociationTreeViewer_updateAddButtons() {
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

        getRemoveButtonHTML: function AssociationTreeViewer_getRemoveButtonHTML(node, dopId)
        {
	        if (!dopId) {
		        dopId = "";
	        }
            return '<a href="javascript:void(0);" class="remove-item" id="t-' + this.options.prefixPickerId + node.nodeRef + dopId + '"></a>';
        },

        attachRemoveClickListener: function AssociationTreeViewer_attachRemoveClickListener(params)
        {
            YAHOO.util.Event.on("t-" + this.options.prefixPickerId + params.node.nodeRef + params.dopId, 'click', this.removeNode, {
	            node: params.node,
	            updateForms: params.updateForms
            }, this);
        },

        // Updates all form fields
        updateFormFields: function AssociationTreeViewer_updateFormFields(clearCurrentDisplayValue)
        {
            // Just element
	        if (clearCurrentDisplayValue == null) {
		        clearCurrentDisplayValue = true;
	        }

            var el;
            el = Dom.get(this.options.controlId + "-currentValueDisplay");
	        if (el != null) {
		        if (clearCurrentDisplayValue) {
	                el.innerHTML = '';
		        }
	            var num = 0;
	            for (var i in this.selectedItems) {
	                if (this.options.plane || !this.options.showSelectedItemsPath) {
	                    var displayName = this.selectedItems[i].selectedName;
	                } else {
	                    displayName = this.selectedItems[i].displayPath + "/" + this.selectedItems[i].selectedName;
	                    if (this.rootNode !== null && this.rootNode.data.displayPath !== null) {
	                        var rootNodeDisplayName = this.rootNode.data.displayPath + "/" + this.rootNode.label + "/";
	                        if (rootNodeDisplayName !== "") {
	                            displayName = displayName.replace(rootNodeDisplayName, "");
	                        }
	                    }
	                }

	                var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
		            if(this.options.disabled) {
			            if (this.options.itemType == "lecm-orgstr:employee") {
				            el.innerHTML += '<div class="' + divClass + '"> ' +  this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) + ' ' + '</div>';
			            } else {
				            el.innerHTML += '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + '</div>';
			            }
		            } else {
			            if (this.options.itemType == "lecm-orgstr:employee") {
				            el.innerHTML
					            += '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, displayName) +
								(this.options.employeeAbsenceMarker ? this.getEmployeeAbsenceMarkerHTML(this.selectedItems[i].nodeRef) : ' ') + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
			            } else {
				            el.innerHTML
					            += '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(this.selectedItems[i], "_c") + '</div>';
			            }
			            YAHOO.util.Event.onAvailable("t-" + this.options.prefixPickerId + this.selectedItems[i].nodeRef + "_c", this.attachRemoveClickListener, {node: this.selectedItems[i], dopId: "_c", updateForms: true}, this);
		            }
	            }
	        }

            if(!this.options.disabled)
            {
                var addItems = this.getAddedItems();

                // Update added fields in main form to be submitted
                el = Dom.get(this.options.controlId + "-added");
	            if (el != null) {
		            if (clearCurrentDisplayValue) {
			            el.value = '';
		            }
	                for (i in addItems) {
	                    el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
	                }
	            }

                var selectedItems = this.getSelectedItems();
                var removedItems = this.getRemovedItems();

                // Update removed fields in main form to be submitted
                var removedEl = Dom.get(this.options.controlId + "-removed");
	            if (removedEl != null) {
                    removedEl.value = '';
	                for (i in removedItems) {
                        removedEl.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
	                }
	            }


                // Update selectedItems fields in main form to pass them between popup and form
                el = Dom.get(this.options.controlId + "-selectedItems");
	            if (el != null) {
		            if (clearCurrentDisplayValue) {
	                    el.value = '';
		            }
	                for (i in selectedItems) {
	                    el.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
	                }

                    //убираем selected из removed
                    if (removedEl != null) {
                        for (var k in Alfresco.util.arrayToObject(el.value.split(","))) {
                            removedEl.value = removedEl.value.replace(k + ',', '');
                            removedEl.value = removedEl.value.replace(k, '');
                        }
                    }
                    if (this.options.setCurrentValue && Dom.get(this.id) != null) {
                        Dom.get(this.id).value = el.value;
                    }
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
            if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
                YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
                    selectedItems: this.selectedItems
                });
            }
        },

        getAddedItems: function AssociationTreeViewer_getAddedItems()
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

        getRemovedItems: function AssociationTreeViewer_getRemovedItems()
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

		getSelectedItems:function AssociationTreeViewer_getSelectedItems() {
			var selectedItems = [];

			for (var item in this.selectedItems) {
				if (this.selectedItems.hasOwnProperty(item)) {
					selectedItems.push(item);
				}
			}
			return selectedItems;
		},

		getSelectedItemsNameSubstituteString:function AssociationTreeViewer_getSelectedItemsNameSubstituteString() {
			var result = this.options.nameSubstituteString;
			if (this.options.selectedItemsNameSubstituteString != null) {
				result = this.options.selectedItemsNameSubstituteString;
			}
			return result;
		},

		updateViewForm: function AssociationTreeViewer_getSelectedItemsNameSubstituteString() {
			var sUrl = this._generateRootUrlPath(this.options.rootNodeRef) + this._generateRootUrlParams();

			Alfresco.util.Ajax.jsonGet(
				{
					url: sUrl,
					successCallback:
					{
						fn: function (response) {
							var oResults = response.json;
							if (oResults != null) {
								this.rootNode =  {
									label:oResults.title,
									data: {
										nodeRef:oResults.nodeRef,
										type:oResults.type,
										displayPath: oResults.displayPath
									}
								};
								this.options.rootNodeRef = oResults.nodeRef;
								this._loadSelectedItems(this.options.clearFormsOnStart, true);
							}
						},
						scope: this
					},
					failureCallback:
					{
						fn: function (oResponse) {
							alert(YAHOO.lang.JSON.parse(oResponse.responseText));
						},
						scope: this
					}
				});
		},
		getEmployeeAbsenceMarkerHTML: function AssociationTreeViewer_getEmployeeAbsenceMarkerHTML(nodeRef) {
			var result = '';
			if (this.employeesAvailabilityInformation) {
				var employeeData = this.employeesAvailabilityInformation[nodeRef];
				if (employeeData) {
					if (employeeData.isEmployeeAbsent) {
						var absenceEnd = Alfresco.util.fromISO8601(employeeData.currentAbsenceEnd);
						result += ' <span class="employee-unavailable" title="Будет доступен с ' + leadingZero(absenceEnd.getDate()) + "." + leadingZero(absenceEnd.getMonth() + 1) + "." + absenceEnd.getFullYear() + '"';
					} else {
						result += ' <span class="employee-available"';
						var nextAbsenceStr = employeeData.nextAbsenceStart;
						if (nextAbsenceStr) {
							nextAbsenceDate = Alfresco.util.fromISO8601(nextAbsenceStr);
							result += 'title="Будет недоступен с ' + leadingZero(nextAbsenceDate.getDate()) + "." + leadingZero(nextAbsenceDate.getMonth() + 1) + "." + nextAbsenceDate.getFullYear() + '"';
						}
					}
					result += ">&nbsp;</span>"
				}
			}
			return result;

			function leadingZero(value) {
				var valueStr = value + "";
				if (valueStr.length == 1) {
					return '0' + valueStr;
				} else {
					return valueStr;
				}
			}

		},
		getEmployeesAbsenceInformation: function AssociationTreeViewer_getEmployeesAbsenceInformation(items) {
			var requestObj = [];
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				if (item.type === "lecm-orgstr:employee") {
					requestObj.push({"nodeRef": item.nodeRef});
				}
			}

			if (requestObj.length > 0) {
				Alfresco.util.Ajax.request({
					method: "POST",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/getEmployeesAvailabilityInformation",
					requestContentType: "application/json",
					responseContentType: "application/json",
					dataObj: requestObj,
					successCallback: {
						fn: function(response) {
							var result = response.json;
							this.employeesAvailabilityInformation = result;
						},
						scope: this
					}
				});
			}
		},
		showEmployeeAutoAnswerPromt: function AssociationTreeView_showEmployeeAutoAnswer(item) {
			var me = this;
			var nodeRef = item.nodeRef;
			var autoAnswerText = this.employeesAvailabilityInformation[nodeRef].answerExtended;
			if (autoAnswerText) {
				Alfresco.util.PopupManager.displayPrompt({
					title: this.msg("title.absence.auto-answer.title"),
					text: autoAnswerText,
                                        noEscape: true,
					close: false,
					modal: true,
					buttons: [
                                                {
							text: this.msg("button.ok"),
							handler: function AssociationTreeView_showEmployeeAutoAnswer_acceptSelection() {
								this.destroy();
							},
							isDefault: true
						},
                                                {
							text: this.msg("button.cancel"),
							handler: function AssociationTreeView_showEmployeeAutoAnswer_denySelection() {
								this.destroy();
								me.removeNode(null, {
									node: item,
									updateForms: true
								});
							}
						}

					]
				});
			}
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
        }
	});
})();
