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

(function () {
    var Dom = YAHOO.util.Dom;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationAutoComplete = function LogicECM_module_AssociationAutoComplete(fieldHtmlId) {
        LogicECM.module.AssociationAutoComplete.superclass.constructor.call(this, "LogicECM.module.AssociationAutoComplete", fieldHtmlId, [ "container", "resize", "datasource"]);
        YAHOO.Bubbling.on("refreshAutocompleteItemList_" + fieldHtmlId, this.onRefreshAutocompleteItemList, this);

        this.controlId = fieldHtmlId + "-cntrl";
	    this.currentValueHtmlId = fieldHtmlId;
        this.dataArray = [];
        this.selectedItems = {};
        this.allowedNodes = null;
        this.allowedNodesScript = null;
        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationAutoComplete, Alfresco.component.Base,
        {
            options:{
                disabled: false,

	            mandatory:false,

                startLocation: null,

                parentNodeRef:"",

                currentValue: "",

                multipleSelectMode: false,

                itemType:"cm:content",

                itemFamily:"node",

                selectedValueNodeRef:"",

                maxSearchResults:1000,

                nameSubstituteString:"{cm:name}",

	            selectedItemsNameSubstituteString: null,

				additionalFilter: "",

                ignoreNodes: [],

	            childrenDataSource: "lecm/forms/picker",

                defaultValue: null,

	            defaultValueDataSource: null,

                allowedNodes:null,

                allowedNodesScript: null
            },

            selectedItems: null,

            dataArray: null,

            dataObject: null,

            controlId:"",

	        currentValueHtmlId: "",

            selectItem:null,

            currentDisplayValueElement:null,

            dataSource:null,

            defaultValue: null,

            setMessages:function AssociationAutoComplete_setMessages(obj) {
                LogicECM.module.AssociationAutoComplete.superclass.setMessages.call(this, obj);
                return this;
            },

            onReady:function AssociationAutoComplete_onReady() {
                if (!this.options.disabled) {
                    this.populateDataWithAllowedScript();
                    this.loadDefaultValue();
                }
            },

            onRefreshAutocompleteItemList: function AssociationAutoComplete_onRefreshItemList(layer, args)
            {
                this.selectedItems = args[1].selectedItems;

                if (!this.options.disabled) {
                    this.updateSelectedItems();
                    this.updateInputUI();
                } else {
                    this.updateCurrentDisplayValue();
                }
            },

            loadSelectedItems: function AssociationAutoComplete__loadSelectedItems()
            {
                var arrItems = "";
                if (this.options.selectedValueNodeRef)
                {
                    arrItems = this.options.selectedValueNodeRef;
                }
                else
                {
                    arrItems = this.options.currentValue;
                }

	            if (arrItems == "" && this.defaultValue != null) {
		            arrItems += this.defaultValue;
	            }

                var onSuccess = function AssociationAutoComplete__loadSelectedItems_onSuccess(response)
                {
                    var items = response.json.data.items,
                        item;
                    this.selectedItems = {};

                    for (var i = 0, il = items.length; i < il; i++)
                    {
                        item = items[i];
                        if (item.type == this.options.itemType) {
                            this.selectedItems[item.nodeRef] = item;
                        }
                    }

                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateFormFields();
                        this.updateInputUI();
                    } else {
                        this.updateCurrentDisplayValue()
                    }
                };

                var onFailure = function AssociationAutoComplete__loadSelectedItems_onFailure(response)
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
            },

	        loadDefaultValue: function AssociationAutoComplete__loadDefaultValue() {
		        if (this.options.defaultValue != null) {
                     this.defaultValue = this.options.defaultValue;
                     this.loadSelectedItems();
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
							        me.loadSelectedItems();
						        }
					        },
					        failureMessage: "message.failure"
				        });
		        } else {
			        this.loadSelectedItems();
		        }
	        },

            makeAutocomplete: function() {
                var oDS = new YAHOO.util.LocalDataSource(this.dataArray);
                oDS.responseSchema = {fields:["name", "selectedName", "nodeRef"]};
                var oAC = new YAHOO.widget.AutoComplete(this.controlId + "-autocomplete-input", this.controlId + "-autocomplete-container", oDS);
                oAC.prehighlightClassName = "yui-ac-prehighlight";
                oAC.useShadow = true;
                oAC.forceSelection = true;
                oAC._bFocused = true;

                var selectItemHandler = function (sType, aArgs) {
                    var node = {
                        name: aArgs[2][0],
	                    selectedName: aArgs[2][1],
                        nodeRef: aArgs[2][2]
                    };
                    this.selectedItems[node.nodeRef] = node;
                    this.updateSelectedItems();
                    this.updateFormFields();
                    this.updateInputUI();
                }.bind(this);
                oAC.itemSelectEvent.subscribe(selectItemHandler);
            },

            populateData: function AssociationSelectOne_populateSelect() {
                this._createDataSource();

                var successHandler = function (sRequest, oResponse, oPayload)
                {
                    var results = oResponse.results;
                    for (var i = 0; i < results.length; i++) {
                        var node = results[i];
                        if (node.selectable) {
                            this.dataArray.push({
                                name: node.name,
	                            selectedName: node.selectedName,
                                nodeRef: node.nodeRef
                            });
                        }
                    }
                    this.makeAutocomplete();
                }.bind(this);

                var failureHandler = function (sRequest, oResponse)
                {
                    if (oResponse.status == 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else
                    {
                        //todo show failure message
                    }
                }.bind(this);

                var url = this._generateChildrenUrlPath(this.options.parentNodeRef) + this._generateChildrenUrlParams("");

                this.dataSource.sendRequest(url,
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            _createDataSource: function AssociationSelectOne__createDataSource() {
                var me = this;

                var pickerChildrenUrl = Alfresco.constants.PROXY_URI + this.options.childrenDataSource + "/" + this.options.itemFamily;
                this.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl,
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

                this.dataSource.doBeforeParseData = function (oRequest, oFullResponse)
                {
                    var updatedResponse = oFullResponse;

                    if (oFullResponse)
                    {
                        var items = oFullResponse.data.items;

                        if (me.options.maxSearchResults > -1 && items.length > me.options.maxSearchResults)
                        {
                            items = items.slice(0, me.options.maxSearchResults-1);
                        }

                        var index, item;
                        for (index in items)
                        {
                            if (items.hasOwnProperty(index))
                            {
                                item = items[index];
                                if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                                {
                                    item.type = "tag";
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

                        updatedResponse =
                        {
                            parent: oFullResponse.data.parent,
                            items: items
                        };
                    }

                    return updatedResponse;
                };
            },

            _generateChildrenUrlPath: function AssociationSelectOne__generateChildrenUrlPath(nodeRef)
            {
                return $combine("/", nodeRef.replace("://", "/"), "children");
            },

            _generateChildrenUrlParams: function AssociationSelectOne__generateChildrenUrlParams(searchTerm)
            {
                var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
	                "&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
					"&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);

                if (this.options.startLocation && this.options.startLocation.charAt(0) == "/")
                {
                    params += "&xpath=" + encodeURIComponent(this.options.startLocation);
                } else if (this.options.xPathLocation)
                {
                    params += "&xPathLocation=" + encodeURIComponent(this.options.xPathLocation);
                    if (this.options.xPathLocationRoot != null) {
                        params += "&xPathRoot=" + encodeURIComponent(this.options.xPathLocationRoot);
                    }
                }
                // has a rootNode been specified?
                if (this.options.rootNode)
                {
                    var rootNode = null;

                    if (this.options.rootNode.charAt(0) == "{")
                    {
                        if (this.options.rootNode == "{companyhome}")
                        {
                            rootNode = "alfresco://company/home";
                        }
                        else if (this.options.rootNode == "{userhome}")
                        {
                            rootNode = "alfresco://user/home";
                        }
                        else if (this.options.rootNode == "{siteshome}")
                        {
                            rootNode = "alfresco://sites/home";
                        }
                    }
                    else
                    {
                        // rootNode is either an xPath expression or a nodeRef
                        rootNode = this.options.rootNode;
                    }
                    if (rootNode !== null)
                    {
                        params += "&rootNode=" + encodeURIComponent(rootNode);
                    }
                }

                return params;
            },

            updateSelectedItems: function AssociationAutoComplete_updateSelectedItems() {
                var items = this.selectedItems;
                var el = Dom.get(this.controlId + "-currentValueDisplay");
                Dom.addClass(el, "auto-complete");
                el.innerHTML = '';

                var num = 0;
                for (var i in items) {
                    var item = items[i];
                    var itemName = item.name;
                    var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";

                    if (item.selectedName && item.selectedName != "") {
                        itemName = item.selectedName;
                    }
	                if (this.options.itemType == "lecm-orgstr:employee") {
		                el.innerHTML += '<div class="' + divClass + '"> ' + this.getEmployeeView(item.nodeRef, itemName) + ' ' + this.getRemoveButtonHTML(item) + '</div>';
	                } else {
		                el.innerHTML += '<div class="' + divClass + '"> ' + this.getDefaultView(itemName) + ' ' + this.getRemoveButtonHTML(item) + '</div>';
	                }
                    YAHOO.util.Event.onAvailable("ac-" + this.controlId + item.nodeRef, this.attachRemoveItemClickListener, item, this);
                }
            },

	        getEmployeeView: function DataGrid_getSortFunction(employeeNodeRef, displayValue) {
		        return "<span class='person'><a href='javascript:void(0);' title='" + displayValue + "' onclick=\"viewAttributes(\'" + employeeNodeRef + "\', null, \'logicecm.employee.view\')\">" + displayValue + "</a></span>";
	        },

            getDefaultView: function (displayValue) {
                return "<span class='not-person' title='" + displayValue + "'>" + displayValue + "</span>";
            },

            getRemoveButtonHTML: function AssociationAutoComplete_getRemoveButtonHTML(node)
            {
                return '<a href="javascript:void(0);" class="remove-item" id="ac-' + this.controlId + node.nodeRef + '"></a>';
            },

            attachRemoveItemClickListener: function AssociationAutoComplete_attachRemoveItemClickListener(node)
            {
                YAHOO.util.Event.on("ac-" + this.controlId + node.nodeRef, 'click', this.removeSelectedElement, node, this);
            },

            removeSelectedElement: function AssociationAutoComplete_removeSelectedElement(event, node)
            {
                delete this.selectedItems[node.nodeRef];
                this.singleSelectedItem = null;
                this.updateSelectedItems();
                this.updateFormFields();
                this.updateInputUI();
            },

            canInputShow: function() {
                return this.options.multipleSelectMode || (Object.keys(this.selectedItems).length == 0);
            },

            canCurrentValuesShow: function() {
                if (this.options.multipleSelectMode) {
                    return true;
                }

                return Object.keys(this.selectedItems).length > 0;
            },

            updateInputUI: function() {
                Dom.get(this.controlId + "-autocomplete-input").value = "";
                Dom.setStyle(Dom.get(this.controlId + "-autocomplete"), "display", this.canInputShow() ? "block" : "none");
                Dom.setStyle(Dom.get(this.controlId + "-currentValueDisplayDiv"), "display", this.canCurrentValuesShow() ? "block" : "none");
            },

            updateCurrentDisplayValue: function() {
                var el = Dom.get(this.controlId + "-currentValueDisplay");
                Dom.addClass(el, "auto-complete");
                el.innerHTML = '';

                var num = 0;
                for (var i in this.selectedItems) {
                    var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
	                if (this.options.itemType == "lecm-orgstr:employee") {
		                el.innerHTML += '<div class="' + divClass + '"> ' + this.getEmployeeView(this.selectedItems[i].nodeRef, this.selectedItems[i].name) + ' </div>';
	                } else {
		                el.innerHTML += '<div class="' + divClass + '"> ' + this.getDefaultView(this.selectedItems[i].name) + ' </div>';
	                }
                }

            },

            // Updates all form fields
            updateFormFields: function AssociationAutoComplete_updateFormFields()
            {
                // Just element
                var el;

                var addItems = this.getAddedItems();

                // Update added fields in main form to be submitted
                el = Dom.get(this.controlId + "-added");
                el.value = '';
                for (var i in addItems) {
                    el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
                }

                var removedItems = this.getRemovedItems();

                // Update removed fields in main form to be submitted
                el = Dom.get(this.controlId + "-removed");
                el.value = '';
                for (i in removedItems) {
                    el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
                }

                var selectedItems = this.getSelectedItems();

                // Update selectedItems fields in main form to pass them between popup and form
                el = Dom.get(this.controlId + "-selectedItems");
                el.value = '';
                for (i in selectedItems) {
                    el.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
                }

	            Dom.get(this.currentValueHtmlId).value = selectedItems.toString();

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
            },

            getAddedItems: function AssociationAutoComplete_getAddedItems()
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

            getRemovedItems: function AssociationAutoComplete_getRemovedItems()
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

	        getSelectedItems:function AssociationAutoComplete_getSelectedItems() {
		        var selectedItems = [];

		        for (var item in this.selectedItems) {
			        if (this.selectedItems.hasOwnProperty(item)) {
				        selectedItems.push(item);
			        }
		        }
		        return selectedItems;
	        },

            destroy:function AssociationAutoComplete_destroy() {
                LogicECM.module.AssociationAutoComplete.superclass.destroy.call(this);
            },

	        getSelectedItemsNameSubstituteString:function AssociationTreeViewer_getSelectedItemsNameSubstituteString() {
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
                                context.populateData();
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function onFailure(response) {
                                context.options.allowedNodes = null;
                                context.populateData();
                            },
                            scope: this
                        },
                        execScripts: true
                    });

                } else {
                    context.populateData();
                }
            }
        });
})();