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

        this.controlId = fieldHtmlId;
        this.dataArray = [];
        this.selectedItems = {};
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationAutoComplete, Alfresco.component.Base,
        {
            options:{
                disabled: false,

                startLocation: null,

                parentNodeRef:"",

                currentValue: "",

                multipleSelectMode: false,

                itemType:"cm:content",

                itemFamily:"node",

                selectedValueNodeRef:"",

                maxSearchResults:1000,

                nameSubstituteString:"{cm:name}",

                openSubstituteSymbol:"{",

                closeSubstituteSymbol:"}"
            },

            selectedItems: null,

            dataArray: null,

            dataObject: null,

            controlId:"",

            currentDisplayValueId:null,

            selectItem:null,

            currentDisplayValueElement:null,

            dataSource:null,

            setMessages:function AssociationAutoComplete_setMessages(obj) {
                LogicECM.module.AssociationAutoComplete.superclass.setMessages.call(this, obj);
                return this;
            },

            onReady:function AssociationAutoComplete_onReady() {
                if (!this.options.disabled) {
                    this.populateData();
                }
                this.loadSelectedItems();
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

                var onSuccess = function AssociationTreeViewer__loadSelectedItems_onSuccess(response)
                {
                    var items = response.json.data.items,
                        item;
                    this.selectedItems = {};

                    for (var i = 0, il = items.length; i < il; i++)
                    {
                        item = items[i];
                        this.selectedItems[item.nodeRef] = item;
                    }

                    if (!this.options.disabled) {
                        this.updateSelectedItems();
                        this.updateFormFields();
                        this.updateInputUI();
                    } else {
                        this.updateCurrentDisplayValue()
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
            },

            makeAutocomplete: function() {
                var oDS = new YAHOO.util.LocalDataSource(this.dataArray);
                oDS.responseSchema = {fields:["name", "nodeRef"]};
                var oAC = new YAHOO.widget.AutoComplete(this.controlId + "-autocomplete-input", this.controlId + "-autocomplete-container", oDS);
                oAC.delimChar = this.options.delimChar;
                oAC.prehighlightClassName = "yui-ac-prehighlight";
                oAC.useShadow = true;
                oAC.forceSelection = true;

                var selectItemHandler = function (sType, aArgs) {
                    var node = {
                        name: aArgs[2][0],
                        nodeRef: aArgs[2][1]
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

                var pickerChildrenUrl = Alfresco.constants.PROXY_URI + "lecm/forms/picker/" + this.options.itemFamily;
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
                    "&openSubstituteSymbol=" + encodeURIComponent(this.options.openSubstituteSymbol) +
                    "&closeSubstituteSymbol=" + encodeURIComponent(this.options.closeSubstituteSymbol);

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

            updateSelectedItems: function AssociationTreeViewer_updateSelectedItems() {
                var items = this.selectedItems;
                var fieldId = this.controlId + "-selected-elements";
                Dom.get(fieldId).innerHTML = '';
                for (var i in items) {
                    Dom.get(fieldId).innerHTML
                        += '<div><img src="/share/res/components/images/filetypes/generic-file-16.png" '
                        + 'width="16" alt="" title="' + items[i].name + '"> ' + items[i].name + ' '
                        + this.getRemoveButtonHTML(items[i]) + '</div>';
                    YAHOO.util.Event.onAvailable(items[i].nodeRef, this.attachRemoveClickListener, items[i], this);
                }
            },

            getRemoveButtonHTML: function AssociationTreeViewer_getRemoveButtonHTML(node)
            {
                return '<a href="#" class="remove-item" id="' + node.nodeRef
                    + '"><img src="/share/res/components/images/remove-icon-16.png" width="16"/></a>';
            },

            attachRemoveClickListener: function AssociationTreeViewer_attachRemoveClickListener(node)
            {
                YAHOO.util.Event.on(node.nodeRef, 'click', this.removeSelectedElement, node, this);
            },

            removeSelectedElement: function AssociationTreeViewer_removeSelectedElement(event, node)
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

            updateInputUI: function() {
                Dom.get(this.controlId + "-autocomplete-input").value = "";
                Dom.setStyle(Dom.get(this.controlId + "-autocomplete"), "display", this.canInputShow() ? "block" : "none");
            },

            updateCurrentDisplayValue: function() {
                var el;

                el = Dom.get(this.controlId + "-currentValueDisplay");
                el.innerHTML = '';
                for (var i in this.selectedItems) {
                    el.innerHTML += '<div><img src="/share/res/components/images/filetypes/generic-file-16.png" '
                        + 'width="16" alt="" title="' + this.selectedItems[i].name + '"> ' + this.selectedItems[i].name + ' </div>';
                }
            },

            // Updates all form fields
            updateFormFields: function AssociationTreeViewer_updateFormFields()
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

            destroy:function AssociationAutoComplete_destroy() {
                LogicECM.module.AssociationAutoComplete.superclass.destroy.call(this);
            }
        });
})();