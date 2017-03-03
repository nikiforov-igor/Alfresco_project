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
        Bubbling = YAHOO.Bubbling;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationCascadingSelectOne = function LogicECM_module_AssociationCascadingSelectOne(fieldHtmlId)
    {
        LogicECM.module.AssociationCascadingSelectOne.superclass.constructor.call(this, "LogicECM.module.AssociationCascadingSelectOne", fieldHtmlId, [ "container", "resize", "datasource"]);
        this.selectItemId = fieldHtmlId + "-added";
        this.removedItemId = fieldHtmlId + "-removed";
        this.controlId = fieldHtmlId;
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

        Bubbling.on("changeDropDown", this.onChangeDropDown, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationCascadingSelectOne, Alfresco.component.Base,
        {
            options:
            {

                parentNodeRef: "",

                startLocation: null,

                itemType: "cm:content",

                itemFamily: "node",

                mandatory: false,

                selectedValueNodeRef: "",

                maxSearchResults: 1000,

                nameSubstituteString: "{cm:name}",

                sortProp: "cm:name",

                openSubstituteSymbol: "{",

                closeSubstituteSymbol: "}",

                dependentFieldName: null,

                dependentFieldArgKey: null,

                webScriptUrl: null,

                htmlId: null,

                disableDropDown: false,

                defaultLoadData: false,

                showDefaultOptions: true,

                primaryCascading: false,

                fieldId: null
            },

            rootNode: null,

            controlId: null,

            createNewButton: null,

            selectItemId: null,

            removedItemId: null,

            currentDisplayValueId: null,

            selectItem: null,

            currentDisplayValueElement: null,

            dataSource: null,

            defaultText: "Empty",

            disabled: false,

            setOptions: function AssociationCascadingSelectOne_setOptions(obj)
            {
                LogicECM.module.AssociationCascadingSelectOne.superclass.setOptions.call(this, obj);
                YAHOO.Bubbling.fire("afterOptionsSet",
                    {
                        eventGroup: this
                    });
                return this;
            },

            onReady: function AssociationCascadingSelectOne_onReady()
            {
                this._loadParentNode();
                this.selectItem = Dom.get(this.selectItemId);
                if (this.selectItem) {
                    if(this.selectItem.options.length > 0){
                        this.defaultText = this.selectItem.options[0].text;
                    }
                    this.disabled = this.selectItem.disabled;
                    this.populateSelect();
                }
                YAHOO.util.Event.on(this.selectItemId, "change", this.onSelectChange, this, true);

                this.currentDisplayValueElement = Dom.get(this.currentDisplayValueId);
                if (this.currentDisplayValueElement) {
                    this.populateCurrentValue();
                }
            },

            onSelectChange: function AssociationTreeViewer_onSelectChange() {
                Dom.get(this.controlId).value = this.selectItem.value;
                this.options.selectedValueNodeRef = this.selectItem.value;
                if (this.options.primaryCascading) {
                    YAHOO.Bubbling.fire("changeDropDown",{bubblingLabel: this.options.fieldId});
                }
            },

            setCreateNewFormDialogTitle: function (p_form, p_dialog) {
				var message;
				if ( this.options.createNewMessage ) {
					message = this.options.createNewMessage;
				} else {
					message = this.msg("dialog.createNew.title");
				}
				p_dialog.dialog.setHeader(message);
            },

            _loadParentNode: function AssociationTreeViewer__loadRootNode() {
                var sUrl = this._generateParentUrlPath(this.options.parentNodeRef) + this._generateParentUrlParams();

                Alfresco.util.Ajax.jsonGet(
                    {
                        url: sUrl,
                        successCallback:
                        {
                            fn: function (response) {
                                var oResults = response.json;
                                if (oResults != null) {
                                    this.rootNode = {
                                        label:oResults.title,
                                        nodeRef:oResults.nodeRef,
                                        type:oResults.type,
                                        isContainer: oResults.isContainer,
                                        displayPath: oResults.displayPath
                                    };
                                    if (this.options.parentNodeRef === "") {
                                        this.options.parentNodeRef = oResults.nodeRef;
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

            _generateParentUrlPath: function AssociationTreeViewer__generateItemsUrlPath(nodeRef)
            {
                return $combine(Alfresco.constants.PROXY_URI, "/lecm/forms/node/search", nodeRef.replace("://", "/"));
            },

            _generateParentUrlParams: function AssociationTreeViewer__generateItemsUrlParams()
            {
                var params = "?titleProperty=" + encodeURIComponent("cm:name");
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

                return params;
            },

            destroy: function AssociationCascadingSelectOne_destroy()
            {
                LogicECM.module.AssociationSelectOne.superclass.destroy.call(this);
            },

            populateSelect: function AssociationCascadingSelectOne_populateSelect() {
                if (this.options.defaultLoadData) {
                    this._createDataSource();

                    var successHandler = function (sRequest, oResponse, oPayload) {
                        var emptyOptions = this.selectItem.options[0];
                        this.selectItem.innerHTML = "";
                        this.selectItem.appendChild(emptyOptions);

                        var results = oResponse.results;
                        for (var i = 0; i < results.length; i++) {
                            var node = results[i];
                            var opt = document.createElement('option');
                            opt.innerHTML = node.name;
                            opt.value = node.nodeRef;
                            if (node.nodeRef == this.options.selectedValueNodeRef) {
                                opt.selected = true;
                            }
                            this.selectItem.appendChild(opt);
                        }

                        this.onSelectChange();
                    }.bind(this);

                    var failureHandler = function (sRequest, oResponse) {
                        if (oResponse.status == 401) {
                            // Our session has likely timed-out, so refresh to offer the login page
                            window.location.reload();
                        }
                        else {
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
                }
            },

            populateCurrentValue: function AssociationCascadingSelectOne_populateCurrentValue() {
	            if (this.options.selectedValueNodeRef != null && this.options.selectedValueNodeRef.length > 0) {
	                Alfresco.util.Ajax.jsonGet(
	                    {
	                        url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/" + this.options.selectedValueNodeRef.replace("://", "/"),
	                        successCallback:
	                        {
	                            fn: function (response) {
                                    var properties = response.json.item.node.properties;
                                    var name = this.options.nameSubstituteString;
                                    for (var prop in properties) {
                                        var propSubstName = this.options.openSubstituteSymbol + prop + this.options.closeSubstituteSymbol;
                                        if (name.indexOf(propSubstName) != -1) {
                                            name = name.replace(propSubstName, properties[prop]);
                                        }
                                    }
                                    this.currentDisplayValueElement.innerHTML = name;
	                            },
	                            scope: this
	                        },
	                        failureCallback:
	                        {
	                            fn: function (response) {
	                                //todo show error message
	                            },
	                            scope: this
	                        }
	                    });
	            }
            },

            _createDataSource: function AssociationCascadingSelectOne__createDataSource() {
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

            _generateChildrenUrlPath: function AssociationCascadingSelectOne__generateChildrenUrlPath(nodeRef)
            {
                return $combine("/", nodeRef.replace("://", "/"), "children");
            },

            _generateChildrenUrlParams: function AssociationCascadingSelectOne__generateChildrenUrlParams(searchTerm)
            {
                var params =  "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) + "&sortProp=" + encodeURIComponent(this.options.sortProp);

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
            onChangeDropDown: function Change_Drop_Down(layer, args){
                var param = args[1];
                var success = false;
                for (obj in  this.options.dependentFieldName){
                    if (param.bubblingLabel.indexOf(this.options.dependentFieldName[obj]) != -1){
                        success = true; break;
                    }
                }
                if (success) {
                    var url = "";
                    this.selectItemId = this.options.htmlId + "_" + this.options.fieldId + "-added";
                    for (obj in  this.options.dependentFieldName) {
                        var value = null;
                        // ищем элементы в диалоговом окне
                        var elementId = this.options.htmlId + "_assoc_" + this.options.dependentFieldName[obj];
                        if (Dom.get(elementId) != null){
                            value = Dom.get(elementId).value;
                        }
                        if (value == null) {
                            elementId = this.options.htmlId + "_prop_" + this.options.dependentFieldName[obj];
                            if (Dom.get(elementId) != null){
                                value = Dom.get(elementId).value;
                            }
                        }
                        if (value != null){
                            url = url + (this.options.dependentFieldArgKey[obj] != null ? this.options.dependentFieldArgKey[obj] : this.options.dependentFieldName[obj]) + "=" + value + "&";
                        }
                    }

                    if (url != "") {
                        this.selectItem.disabled = false;
                        Alfresco.util.Ajax.jsonGet(
                            {
                                url: Alfresco.constants.PROXY_URI + this.options.webScriptUrl + "?" + url,
                                successCallback: {
                                    fn: function (response) {
                                        var elements = response.json;

                                        // Получаем элемент
                                        var selected = this.selectItem;
                                        //Очищаем элементы списка
                                        selected.options.length = 0;
                                        var first = 0;
                                        if (this.options.showDefaultOptions) {
                                            selected.options[0] = new Option(this.defaultText, "", false, true);
                                            first = 1;
                                        }
                                        if (elements.length > 0) {
                                            for (var i = 0; i < elements.length; i++) {
                                                var prop = elements[i];
                                                selected.options[i+first] = new Option(prop.name, prop.nodeRef, false, (prop.nodeRef == this.options.selectedValueNodeRef));
                                            }
                                        }
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function (response) {
                                        //todo show error message
                                    },
                                    scope: this
                                }
                            });
                    } else {
                        if (this.options.showDefaultOptions) {
                            var selected = this.selectItem;
                            selected.options.length = 0;
                            selected.options[0] = new Option(this.defaultText, "", false, true);
                        }
                        if (!this.disabled) {
                            if (this.options.defaultLoadData) {
                                this.populateSelect();
                            }
                        } else {
                            this.selectItem.disabled = true;
                        }
                    }
                }
            }
        });
})();