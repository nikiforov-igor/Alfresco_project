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
		KeyListener = YAHOO.util.KeyListener,
        tree;

    var $html = Alfresco.util.encodeHTML,
        $combine = Alfresco.util.combinePaths,
        $hasEventInterest = Alfresco.util.hasEventInterest;

    var IDENT_CREATE_NEW = "~CREATE~NEW~";

    LogicECM.module.AssociationTreeViewer = function(htmlId)
	{
        LogicECM.module.AssociationTreeViewer.superclass.constructor.call(this, "AssociationTreeViewer", htmlId);
        YAHOO.Bubbling.on("refreshItemList", this.onRefreshItemList, this);
        YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);

        this.eventGroup = htmlId;
        this.selectedItems = {};
        this.addItemButtons = {};
        this.searchProperties = {};
        this.currentNode = null;
        this.rootNode = null;
        this.isSearch = false;

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationTreeViewer, Alfresco.component.Base,
	{
        eventGroup: null,

        singleSelectedItem: null,

        selectedItems: null,

        addItemButtons: null,

        currentNode: null,

        rootNode: null,

        searchProperties: null,

        isSearch: false,

		options:
		{
            selectedValue: null,

            currentValue: "",
			// If control is disabled (has effect in 'picker' mode only)
			disabled: false,
			// If this form field is mandatory
			mandatory: false,
			// If control allows to pick multiple assignees (has effect in 'picker' mode only)
			multipleSelectMode: false,

			initialized: false,

            createNewItemIcon: "",

            rootNodeRef: '',

            itemType: "cm:content",

            maxSearchResults: 100,

            treeRoteNodeTitleProperty: "cm:name",

            treeNodeTitleProperty: "cm:name",

            nameSubstituteString: "{cm:name}",

            openSubstituteSymbol: "{",

            closeSubstituteSymbol: "}"
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
			this.options.pickerId = this.id + '-cntrl-picker';

            this._loadSelectedItems();

            // Create button if control is enabled
            if(!this.options.disabled)
            {
                // Create picker button
                this.widgets.pickerButton =  new YAHOO.widget.Button(
                            this.id + "-cntrl-orgchart-picker-button",
                            { onclick: { fn: this.showTreePicker, obj: null, scope: this } }
                    );

                this.createPickerDialog();
                this._createSelectedControls();
                this._loadSearchProperties();
            }
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

        _loadSelectedItems: function AssociationTreeViewer__loadSelectedItems()
        {
            var arrItems = "";
            if (this.options.selectedValue)
            {
                arrItems = this.options.selectedValue;
            }
            else
            {
                arrItems = this.options.currentValue;
            }

            var onSuccess = function AssociationTreeViewer__loadSelectedItems_onSuccess(response)
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
                }
                if(!this.options.disabled)
                {
                    this.updateSelectedItems();
                }
                this.updateFormFields();
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
                            itemOpenSubstituteSymbol: this.options.openSubstituteSymbol,
                            itemCloseSubstituteSymbol: this.options.closeSubstituteSymbol
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
                if (this.options.disabled && this.options.displayMode == "items")
                {
                    Dom.get(this.id + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
                }
            }
        },

        createPickerDialog: function()
        {
            var me = this;

            this.widgets.ok = new YAHOO.widget.Button(this.options.controlId + "-ok",
                { onclick: { fn: this.onOk, obj: null, scope: this } });
            this.widgets.cancel = new YAHOO.widget.Button(this.options.controlId + "-cancel",
                { onclick: { fn: this.onCancel, obj: null, scope: this } });

            this.widgets.dialog = Alfresco.util.createYUIPanel(this.options.pickerId,
                {
                    width: "974px"
                });
            this.widgets.dialog.hideEvent.subscribe(this.onCancel, null, this);

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

            // Create tree in the dialog
            this.fillPickerDialog();

            Dom.addClass(this.options.pickerId, "object-finder");
        },

        onOk: function(e, p_obj)
        {
            // Close dialog
            this.widgets.escapeListener.disable();
            this.widgets.dialog.hide();
            this.widgets.pickerButton.set("disabled", false);
            if (e) {
                Event.preventDefault(e);
            }
            // Update parent form
            this.updateFormFields();
            if(this.options.mandatory)
                YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
        },

        onCancel: function(e, p_obj)
        {
            this.widgets.escapeListener.disable();
            this.widgets.dialog.hide();
            if( this.widgets.pickerButton )
                this.widgets.pickerButton.set("disabled", false);
            if (e) {
                Event.preventDefault(e);
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

            // Show the dialog
            this.widgets.dialog.show();

            Event.preventDefault(e);
        },

        // Fill tree view group selector with node data
        fillPickerDialog: function AssociationTreeViewer_fillPickerDialog()
        {
            tree = new YAHOO.widget.TreeView(this.options.pickerId + "-groups");
            tree.singleNodeHighlight = true;
            tree.setDynamicLoad(this._loadNode.bind(this));

            tree.subscribe('clickEvent', function(event) {
                this.treeViewClicked(event.node);
                tree.onEventToggleHighlight(event);
                return false;
            }.bind(this));

            this._loadRootNode();
        },

        _loadRootNode: function AssociationTreeViewer__loadRootNode() {
            var root = tree.getRoot();

            Alfresco.util.Ajax.jsonGet(
                {
                    url: Alfresco.constants.PROXY_URI + "slingshot/node/" + this.options.rootNodeRef.replace("://", "/"),
                    successCallback:
                    {
                        fn: function (response) {
                            var data = response.json;
                            var properties = data.properties;
                            for (var i = 0; i < properties.length; i++) {
                                var prop = properties[i];
                                if (prop.name && (prop.name.prefixedName == this.options.treeRoteNodeTitleProperty)) {
                                    var newNode = {
                                        label: prop.values[0].value,
                                        nodeRef: data.nodeRef,
                                        isLeaf: data.children.length == 0,
                                        isContainer: true,
                                        renderHidden:true,
                                        expanded: true
                                    };
                                    this.rootNode = new YAHOO.widget.TextNode(newNode, root);
                                    tree.render();
                                    this.treeViewClicked(this.rootNode);
                                    tree.onEventToggleHighlight(this.rootNode);
                                    break;
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
                        scope: this
                    }
                });
        },

        _loadNode:function AssociationTreeViewer__loadNode(node, fnLoadComplete) {
            var sUrl = this._generateItemsUrlPath(node.data.nodeRef) + this._generateItemsUrlParams();

            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        node.children = [];
                        for (var nodeIndex in oResults) {
                            var newNode = {
                                label:oResults[nodeIndex].title,
                                nodeRef:oResults[nodeIndex].nodeRef,
                                isLeaf:oResults[nodeIndex].isLeaf,
                                type:oResults[nodeIndex].type,
                                isContainer: oResults[nodeIndex].isContainer,
                                renderHidden:true
                            };
                            new YAHOO.widget.TextNode(newNode, node);
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
                    tree:tree,
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
            return "?nodeTitleProperty=" + encodeURIComponent(this.options.treeNodeTitleProperty);
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
            var pickerChildrenUrl = Alfresco.constants.PROXY_URI + "lecm/forms/picker/node";
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
                    if (me.currentNode != null && me.currentNode.data.isContainer && !me.isSearch)
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
                    { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderItemIcon(), width: this.options.compactMode ? 10 : 26 },
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
                        YAHOO.Bubbling.fire("selectedItemAdded",
                            {
                                eventGroup: me,
                                item: record.getData(),
                                highlight: true
                            });
                    }
                }
                return true;
            };
            YAHOO.Bubbling.addDefaultAction("add-" + this.eventGroup, fnAddItemHandler, true);

            // Hook create new item action click events (for Compact mode)
            var fnCreateNewItemHandler = function AssociationTreeViewer__createControls_fnCreateNewItemHandler(layer, args)
            {
                var templateUrl = me.generateCreateNewUrl(me.currentNode.data.nodeRef, me.options.itemType);

                new Alfresco.module.SimpleDialog("form-dialog").setOptions({
                    width:"40em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn:me.setCreateNewFormDialogTitle
                    },
                    onSuccess:{
                        fn:function () {
                            me._updateItems(me.currentNode.data.nodeRef, "");
                        },
                        scope:this
                    }
                }).show();
                return true;
            };
            YAHOO.Bubbling.addDefaultAction("create-new-item-" + this.eventGroup, fnCreateNewItemHandler, true);

            // Hook navigation action click events
            var fnNavigationHandler = function AssociationTreeViewer__createControls_fnNavigationHandler(layer, args)
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
                        YAHOO.Bubbling.fire("parentChanged",
                            {
                                eventGroup: me,
                                label: record.getData("name"),
                                nodeRef: record.getData("nodeRef")
                            });
                    }
                }
                return true;
            };
            YAHOO.Bubbling.addDefaultAction("parent-" + this.eventGroup, fnNavigationHandler, true);
        },

        generateCreateNewUrl: function AssociationTreeViewer_generateCreateNewUrl(nodeRef, itemType) {
            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true";
            return YAHOO.lang.substitute(templateUrl, {
                itemKind: "type",
                itemId: itemType,
                destination: nodeRef,
                mode: "create",
                submitType: "json",
                formId: "association-create-new-node-form"
            });
        },

        setCreateNewFormDialogTitle: function (p_form, p_dialog) {
            var fileSpan = '<span class="light">Create new</span>';
            Alfresco.util.populateHTML(
                [ p_dialog.id + "-form-container_h", fileSpan]
            );
        },

        /**
         * Returns Icon datacell formatter
         *
         * @method fnRenderItemIcon
         */
        fnRenderItemIcon: function AssociationTreeViewer_fnRenderItemIcon()
        {
            var scope = this;

            return function AssociationTreeViewer_renderItemIcon(elCell, oRecord, oColumn, oData)
            {
                var iconSize = scope.options.compactMode ? 16 : 32;

                oColumn.width = iconSize - 6;
                Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                // Create New item cell type
                if (oRecord.getData("type") == IDENT_CREATE_NEW)
                {
                    Dom.addClass(this.getTrEl(elCell), "create-new-row");
                    var obj =
                    {
                        type: scope.options.createNewItemIcon,
                        description: scope.msg("form.control.object-picker.create-new")
                    };
                    elCell.innerHTML = scope.renderItem(obj, iconSize, '<div class="icon' + iconSize + '"><span class="new-item-overlay"></span>{icon}</div>');
                    return;
                }

                elCell.innerHTML = scope.renderItem(oRecord.getData(), iconSize, '<div class="icon' + iconSize + '">{icon}</div>');
            };
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
                    elCell.innerHTML = '<a href="#" title="' + scope.msg("form.control.object-picker.create-new") + '" class="create-new-item-' + scope.eventGroup + '" >' + scope.msg("form.control.object-picker.create-new") + '</a>';
                    return;
                }

                if (oRecord.getData("isContainer") ||
                    (!oRecord.getData("isContainer") && (scope.options.allowNavigationToContentChildren || oRecord.getData("type") == "cm:category")))
                {
                    template += '<h3 class="item-name"><a href="#" class="theme-color-1 parent-' + scope.eventGroup + '">{name}</a></h3>';
                }
                else
                {
                    template += '<h3 class="item-name">{name}</h3>';
                }

                if (!scope.options.compactMode)
                {
                    template += '<div class="description">{description}</div>';
                }

                elCell.innerHTML = scope.renderItem(oRecord.getData(), 0, template);
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

        renderItem: function AssociationTreeViewer_renderItem(item, iconSize, template)
        {
            var me = this;

            var renderHelper = function AssociationTreeViewer_renderItem_renderHelper(p_key, p_value, p_metadata)
            {
                if (p_key.toLowerCase() == "icon")
                {
                    return '<img src="' + me.getIconURL(item, iconSize) + '" width="' + iconSize + '" alt="' + $html(item.description) + '" title="' + $html(item.name) + '" />';
                }
                return $html(p_value);
            };

            return YAHOO.lang.substitute(template, item, renderHelper);
        },

        getIconURL: function AssociationTreeViewer_getIconURL(item, size)
        {
            return Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(item.name, item.type, size);
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
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("logicecm.base.select-tree-element"));
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            var successHandler = function AssociationTreeViewer__updateItems_successHandler(sRequest, oResponse, oPayload)
            {
                this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : nodeRef;
                this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
                if (this.currentNode != null && this.currentNode.data.isContainer && !this.isSearch)
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
                "&openSubstituteSymbol=" + encodeURIComponent(this.options.openSubstituteSymbol) +
                "&closeSubstituteSymbol=" + encodeURIComponent(this.options.closeSubstituteSymbol);
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

                    this.updateSelectedItems();
                    this.updateAddButtons();
                }
            }
        },

        removeNode: function AssociationTreeViewer_removeNode(event, node)
        {
            delete this.selectedItems[node.nodeRef];
            this.singleSelectedItem = null;
            this.updateSelectedItems();
            this.updateAddButtons();
        },

        updateSelectedItems: function AssociationTreeViewer_updateSelectedItems() {
            var items = this.selectedItems;
            var fieldId = this.options.pickerId + "-selected-elements";
            Dom.get(fieldId).innerHTML = '';
            for (i in items) {
                Dom.get(fieldId).innerHTML
                    += '<div><img src="/share/res/components/images/filetypes/generic-file-16.png" '
                    + 'width="16" alt="" title="' + items[i].name + '"> ' + items[i].name + ' '
                    + this.getRemoveButtonHTML(items[i]) + '</div>';
                YAHOO.util.Event.onAvailable(items[i].nodeRef, this.attachRemoveClickListener, items[i], this);
            }
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

        getRemoveButtonHTML: function AssociationTreeViewer_getRemoveButtonHTML(node)
        {
            return '<a href="#" class="remove-item" id="' + node.nodeRef
                + '"><img src="/share/res/components/images/remove-icon-16.png" width="16"/></a>';
        },

        attachRemoveClickListener: function AssociationTreeViewer_attachRemoveClickListener(node)
        {
            YAHOO.util.Event.on(node.nodeRef, 'click', this.removeNode, node, this);
        },

        // Updates all form fields
        updateFormFields: function AssociationTreeViewer_updateFormFields()
        {
            // Just element
            var el;

            el = Dom.get(this.options.controlId + "-currentValueDisplay");
            el.innerHTML = '';
            for (i in this.selectedItems) {
                el.innerHTML += '<div><img src="/share/res/components/images/filetypes/generic-file-16.png" '
                    + 'width="16" alt="" title="' + this.selectedItems[i].name + '"> ' + this.selectedItems[i].name + ' </div>';
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

                var removedItems = this.getRemovedItems();

                // Update removed fields in main form to be submitted
                el = Dom.get(this.options.controlId + "-removed");
                el.value = '';
                for (i in removedItems) {
                    el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
                }
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
        }
  	});
})();
