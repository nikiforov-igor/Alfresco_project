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

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        KeyListener = YAHOO.util.KeyListener,
        Util = LogicECM.module.Base.Util;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationAutoComplete = function LogicECM_module_AssociationAutoComplete(fieldHtmlId) {
        LogicECM.module.AssociationAutoComplete.superclass.constructor.call(this, "LogicECM.module.AssociationAutoComplete", fieldHtmlId);
        YAHOO.Bubbling.on("refreshAutocompleteItemList_" + fieldHtmlId, this.onRefreshAutocompleteItemList, this);
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
	    YAHOO.Bubbling.on("disableControl", this.onDisableControl, this);
	    YAHOO.Bubbling.on("enableControl", this.onEnableControl, this);
	    YAHOO.Bubbling.on("reInitializeControl", this.onReInitializeControl, this);

        this.controlId = fieldHtmlId + "-cntrl";
	    this.currentValueHtmlId = fieldHtmlId;
        this.dataArray = [];
        this.selectedItems = {};
        this.allowedNodes = null;
        this.allowedNodesScript = null;
	    this.searchProperties = {};
        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationAutoComplete, Alfresco.component.Base,
        {
            options:{
                disabled: false,

                lazyLoading: false,

	            mandatory:false,

                startLocation: null,

                parentNodeRef:"",

                currentValue: "",

                multipleSelectMode: false,

                itemType:"cm:content",

                itemFamily:"node",

                selectedValueNodeRef:"",

                maxSearchResults:10,

                nameSubstituteString:"{cm:name}",

                sortProp: "cm:name",

	            selectedItemsNameSubstituteString: null,

				additionalFilter: "",

                useStrictFilterByOrg: false,
                doNotCheckAccess: false,

                ignoreNodes: [],

	            childrenDataSource: "lecm/forms/picker",

                defaultValue: null,

	            defaultValueDataSource: null,

                allowedNodes:null,

                allowedNodesScript: null,

	            useDynamicLoading: false,

                showAssocViewForm: false,

	            changeItemsFireAction: null,

	            fieldId: null,

	            formId: false,

                loadDefault: true,

				checkType: true
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

	        searchProperties: null,

			tempDisabled: false,

			readonly: false,

            setMessages:function AssociationAutoComplete_setMessages(obj) {
                LogicECM.module.AssociationAutoComplete.superclass.setMessages.call(this, obj);
                return this;
            },

            onReady:function AssociationAutoComplete_onReady() {
                if (!this.options.disabled && !this.options.lazyLoading) {
                    this.populateDataWithAllowedScript();
                    if (this.options.loadDefault) {
                        this.loadDefaultValue();
                    }
	                if (this.options.useDynamicLoading) {
	                    this._loadSearchProperties();
	                }
                }
	            var input = Dom.get(this.controlId + "-autocomplete-input");
	            if (input != null) {
		            input.disabled = this.options.disabled || this.options.lazyLoading;
	            }
	            LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
                YAHOO.Bubbling.fire("associationAutoCompleteControlReady", {
                    formId: this.options.formId,
                    fieldId: this.options.fieldId,
                    itemType: this.options.itemType
                });
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

	            if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
		            YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
			            selectedItems: this.selectedItems,
			            formId: this.options.formId,
			            fieldId: this.options.fieldId
		            });
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
                        if (!this.options.checkType || item.type == this.options.itemType) {
                            this.selectedItems[item.nodeRef] = item;
                        }
                    }

                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateFormFields(true);
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

            makeAutocomplete: function () {
                var me = this;
                var oDS;
                me.byEnter = false;
                if (!this.options.lazyLoading) {
                    if (me.options.useDynamicLoading) {
                        var url = Alfresco.constants.PROXY_URI + this.options.childrenDataSource + "/" + this.options.itemFamily + this._generateChildrenUrlPath(this.options.parentNodeRef);
                        oDS = new YAHOO.util.XHRDataSource(url);
                        oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
                        oDS.responseSchema = {
                            resultsList: "items",
                            fields: ["name", "selectedName", "nodeRef"]
                        };
                        oDS.doBeforeParseData = this._doBeforeParseData();
                    } else {
                        oDS = new YAHOO.util.LocalDataSource(this.dataArray);
                        oDS.responseSchema = {fields: ["name", "selectedName", "nodeRef"]};
                    }

                    var oAC = new YAHOO.widget.AutoComplete(this.controlId + "-autocomplete-input", this.controlId + "-autocomplete-container", oDS);
                    if (me.options.useDynamicLoading) {
                        oAC.generateRequest = function (sQuery) {
                            var searchData = "";

                            Dom.addClass(me.controlId + "-autocomplete-input", "wait-for-load");

                            for (var column in me.searchProperties) {
                                searchData += column + ":" + decodeURIComponent(sQuery) + "#";
                            }
                            if (searchData != "") {
                                searchData = searchData.substring(0, (searchData.length) - 1);
                            } else {
                                searchData = "cm:name" + ":" + decodeURIComponent(sQuery);
                            }

                            return me._generateChildrenUrlParams(searchData);
                        };
                        oAC.doBeforeLoadData = function(sQuery , oResponse , oPayload) {
                            var results = oResponse.results;

                            // Если после нажатия enter возращается только один результат, то он сразу подставляется в поле
							var res;
                            if (me.byEnter && results && results.length == 1) {
                                me.byEnter = false;
                                var result = results[0];
                                var node = {
                                    name: result.name,
                                    selectedName: result.selectedName,
                                    nodeRef: result.nodeRef
                                };

                                me.selectedItems[node.nodeRef] = node;

                                me.updateSelectedItems();
                                me.updateFormFields();
                                me.updateInputUI();
                                res = false;
                            } else {
                                res = true;
                            }
                            Dom.removeClass(me.controlId + "-autocomplete-input", "wait-for-load");
							return res;
                        };

                        oAC.queryDelay = 0.5;
                    }
                    oAC.minQueryLength = 3;
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

                    // Register the "enter" event on the autocomplete text field
                    var input = Dom.get(this.controlId + "-autocomplete-input");
                    new KeyListener(input,
                        {
                            keys: 13
                        },
                        {
                            fn: function(eventName, args) {
                                var e = args[1];
                                var text = input.value;

                                if (text && text != "") {
                                    me.byEnter = true;
	                                clearTimeout(oAC._nDelayID);
                                    oAC.sendQuery(text);
                                }
                                Event.stopEvent(e);
                            },
                            scope: this,
                            correctScope: true
                        }, "keydown").enable();
                }
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

                this.dataSource.doBeforeParseData = this._doBeforeParseData();
            },

	        _doBeforeParseData: function() {
		        var me = this;

		        return function (oRequest, oFullResponse) {
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

                    if (item.selectedName && item.selectedName != "") {
                        itemName = item.selectedName;
                    }
	                if (this.options.itemType == "lecm-orgstr:employee") {
		                el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(item.nodeRef, itemName), this.getRemoveButtonHTML(item));
	                } else {
                        if (this.options.showAssocViewForm) {
                            el.innerHTML += Util.getCroppedItem(Util.getControlValueView(item.nodeRef, itemName, itemName), this.getRemoveButtonHTML(item));
                        } else {
                            el.innerHTML += Util.getCroppedItem(Util.getControlDefaultView(itemName), this.getRemoveButtonHTML(item));
                        }
	                }
                    YAHOO.util.Event.onAvailable("ac-" + this.controlId + item.nodeRef, this.attachRemoveItemClickListener, item, this);
                }
            },

            getRemoveButtonHTML: function (node) {
                return Util.getControlItemRemoveButtonHTML("ac-" + this.controlId + node.nodeRef);
            },

            attachRemoveItemClickListener: function AssociationAutoComplete_attachRemoveItemClickListener(node)
            {
                YAHOO.util.Event.on("ac-" + this.controlId + node.nodeRef, 'click', this.removeSelectedElement, node, this);
            },

            removeSelectedElement: function AssociationAutoComplete_removeSelectedElement(event, node)
            {
	            if (!this.tempDisabled || !this.readonly) {
		            delete this.selectedItems[node.nodeRef];
		            this.singleSelectedItem = null;
		            this.updateSelectedItems();
		            this.updateFormFields();
		            this.updateInputUI();
	            }
            },

            canInputShow: function() {
                return this.options.multipleSelectMode || (Object.keys(this.selectedItems).length == 0);
            },

            canCurrentValuesShow: function() {
                return Object.keys(this.selectedItems).length > 0;
            },

            updateInputUI: function() {
	            if (Dom.get(this.controlId + "-autocomplete-input") != null) {
		            Dom.get(this.controlId + "-autocomplete-input").value = "";
		            Dom.setStyle(Dom.get(this.controlId + "-autocomplete-input"), "display", this.canInputShow() ? "block" : "none");
	            }
                Dom.setStyle(Dom.get(this.controlId + "-currentValueDisplay"), "display", this.canCurrentValuesShow() ? "block" : "none");
            },

            updateCurrentDisplayValue: function() {
                var el = Dom.get(this.controlId + "-currentValueDisplay");
                Dom.addClass(el, "auto-complete");
                el.innerHTML = '';

                var num = 0;
                for (var i in this.selectedItems) {
                    if (this.options.itemType == "lecm-orgstr:employee") {
                        el.innerHTML += Util.getCroppedItem(Util.getControlEmployeeView(this.selectedItems[i].nodeRef, this.selectedItems[i].name));
                    } else {
                        if (this.options.showAssocViewForm) {
                            el.innerHTML += Util.getCroppedItem(Util.getControlValueView(this.selectedItems[i].nodeRef, this.selectedItems[i].name, this.selectedItems[i].name));
                        } else {
                            el.innerHTML += Util.getCroppedItem(Util.getControlDefaultView(this.selectedItems[i].name));
                        }
                    }
                }
            },

            // Updates all form fields
            updateFormFields: function AssociationAutoComplete_updateFormFields(first)
            {
                // Just element
                var el;

                var addItems = this.getAddedItems();

                // Update added fields in main form to be submitted
                el = Dom.get(this.controlId + "-added");
	            if (el != null) {
		            el.value = '';
		            for (var i in addItems) {
			            el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
		            }
	            }

                var removedItems = this.getRemovedItems();

                // Update removed fields in main form to be submitted
                el = Dom.get(this.controlId + "-removed");
	            if (el != null) {
		            el.value = '';
		            for (i in removedItems) {
			            el.value += (i < removedItems.length - 1 ? removedItems[i] + ',' : removedItems[i]);
		            }
	            }

                var selectedItems = this.getSelectedItems();

                // Update selectedItems fields in main form to pass them between popup and form
                el = Dom.get(this.controlId + "-selectedItems");
	            if (el != null) {
		            el.value = '';
		            for (i in selectedItems) {
			            el.value += (i < selectedItems.length - 1 ? selectedItems[i] + ',' : selectedItems[i]);
		            }
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
	            if (first == null || !first) {
		            if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
			            YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
				            selectedItems: this.selectedItems,
				            formId: this.options.formId,
				            fieldId: this.options.fieldId
			            });
		            }
	            }
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
	                            if (context.options.useDynamicLoading) {
		                            context.makeAutocomplete();
	                            } else {
		                            context.populateData();
	                            }
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
            },

	        _loadSearchProperties: function AssociationTreeViewer__loadSearchProperties() {
		        Alfresco.util.Ajax.jsonGet(
			        {
				        url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "/lecm/components/datagrid/config/columns?formId=searchColumns&itemType=" + encodeURIComponent(this.options.itemType)),
				        successCallback:
				        {
					        fn: function (response) {
						        var columns = response.json.columns;
						        for (var i = 0; i < columns.length; i++) {
							        var column = columns[i];
							        if (column.dataType == "text" || column.dataType == "mltext") {
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

			onReadonlyControl: function (layer, args) {
				var autocompleteInput;
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				this.readonly = args[1].readonly;
					autocompleteInput = Dom.get(this.controlId + '-autocomplete-input');
					if (autocompleteInput) {
						autocompleteInput.disabled = args[1].readonly;
					}
				}
			},

	        onDisableControl: function (layer, args) {
		        if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			        var autocomplete = Dom.get(this.controlId + "-autocomplete-input");
			        if (autocomplete) {
				        autocomplete.disabled = true;
			        }
			        this.tempDisabled = true;

					var input = Dom.get(this.id);
					if (input) {
						input.disabled = true;
					}
			        var added = Dom.get(this.controlId + "-added");
			        if (added) {
				        added.disabled = true;
			        }
			        var removed = Dom.get(this.controlId + "-removed");
			        if (removed) {
				        removed.disabled = true;
			        }
		        }
	        },

	        onEnableControl: function (layer, args) {
		        if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			        if (!this.options.disabled) {
				        var autocomplete = Dom.get(this.controlId + "-autocomplete-input");
				        if (autocomplete) {
					        autocomplete.disabled = false;
				        }
						var input = Dom.get(this.id);
						if (input) {
							input.disabled = false;
						}
				        var added = Dom.get(this.controlId + "-added");
				        if (added) {
					        added.disabled = false;
				        }
				        var removed = Dom.get(this.controlId + "-removed");
				        if (removed) {
					        removed.disabled = false;
				        }
			        }
			        this.tempDisabled = false;
		        }
	        },

	        onReInitializeControl: function (layer, args) {
		        if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
			        var options = args[1].options;
			        if (options != null) {
				        this.setOptions(options);
			        }

			        this.dataArray = [];
			        this.selectedItems = {};
			        this.allowedNodes = null;
			        this.allowedNodesScript = null;
			        this.searchProperties = {};

			        this.onReady();
		        }
	        }
        });
})();
