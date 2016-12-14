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
    var Dom = YAHOO.util.Dom;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationSelectOne = function LogicECM_module_AssociationSelectOne(fieldHtmlId)
    {
        LogicECM.module.AssociationSelectOne.superclass.constructor.call(this, "LogicECM.module.AssociationSelectOne", fieldHtmlId, [ "container", "resize", "datasource"]);
        this.selectItemId = fieldHtmlId;
        this.removedItemId = fieldHtmlId + "-removed";
        this.addedItemId = fieldHtmlId + "-added";
        this.oldItemId = fieldHtmlId + "-old";
        this.controlId = fieldHtmlId;
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);

        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationSelectOne, Alfresco.component.Base,
        {
            options:
            {
                showCreateNewButton: true,

                parentNodeRef: "",

                startLocation: null,

                itemType: "cm:content",

                itemFamily: "node",

                mandatory: false,

                selectedValueNodeRef: "",

                oldValue: "",

                maxSearchResults: 1000,

                nameSubstituteString: "{cm:name}",

                sortProp: "cm:name",

                openSubstituteSymbol: "{",

                closeSubstituteSymbol: "}",

                primaryCascading: false,

                fieldId: null,

                notSelectedOptionShow: false,

                notSelectedText: "",

                additionalFilter: "",

                multipleSelect: false,

                disabled: false,

                defaultValue: null,

                defaultValueDataSource: null,

                changeItemsFireAction: null,

                customDatasource: null
            },

            rootNode: null,

            controlId: null,

            createNewButton: null,

            selectItemId: null,

            removedItemId: null,

            addedItemId: null,

            currentDisplayValueId: null,

            selectItem: null,

            currentDisplayValueElement: null,

            dataSource: null,

            defaultValue: null,

            doubleClickLock: false,

			readonly: false,

            setOptions: function AssociationSelectOne_setOptions(obj)
            {
                LogicECM.module.AssociationSelectOne.superclass.setOptions.call(this, obj);
                YAHOO.Bubbling.fire("afterOptionsSet",
                    {
                        eventGroup: this
                    });
                return this;
            },

            onReady: function AssociationSelectOne_onReady()
            {
                this._loadParentNode();

                if (!this.options.disabled) {
                    this.selectItem = Dom.get(this.selectItemId);
                    if (this.selectItem) {
                        this.populateSelect();
                    }
                    YAHOO.util.Event.on(this.selectItemId, "change", this.onSelectChange, this, true);
                }

                this.currentDisplayValueElement = Dom.get(this.currentDisplayValueId);
                if (this.currentDisplayValueElement) {
                    this.populateCurrentValue();
                }
                if (!this.options.disabled && this.options.showCreateNewButton) {
                    this.createNewButton =  new YAHOO.widget.Button(
                        this.controlId + "-selectone-create-new-button",
                        {
                            onclick: { fn: this.showCreateNewItemWindow, obj: null, scope: this },
                            disabled: true
                        }
                    );
                }

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },

            onSelectChange: function AssociationTreeViewer_onSelectChange() {
                var selectValue = null;

                if (!this.options.multipleSelect) {
                    selectValue = this.selectItem.value;
                } else {
                    if (this.selectItem !== null) {
                        var values = new Array();
                        for (var j = 0, jj = this.selectItem.options.length; j < jj; j++) {
                            if (this.selectItem.options[j].selected) {
                                values.push(this.selectItem.options[j].value);
                            }
                        }

                        selectValue = values.join(",");
                    }
                }
                var addedItem = "";
                var removedItem = "";

                if (selectValue != this.options.oldValue) {
                    removedItem = this.options.oldValue;
                    addedItem = selectValue;
                }
                Dom.get(this.removedItemId).value = removedItem;
                Dom.get(this.addedItemId).value = addedItem;

                if (this.options.mandatory) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }

                YAHOO.Bubbling.fire("formValueChanged",
                    {
                        eventGroup:this,
                        addedItems:addedItem,
                        removedItems:removedItem,
                        selectedItems:selectValue,
                        selectedItemsMetaData:Alfresco.util.deepCopy(this.selectItem.value)
                    });
                if (this.options.primaryCascading) {
                    YAHOO.Bubbling.fire("changeDropDown",{bubblingLabel: this.options.fieldId});
                }

                if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
                    YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
                        selectedItems: ((selectValue && selectValue.length > 0) ?[selectValue] : []),
                        fieldId: this.options.fieldId,
                        formId: this.options.formId
                    });
                }
            },

            showCreateNewItemWindow: function AssociationTreeViewer_showCreateNewItemWindow() {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
                var templateRequestParams = {
                    itemKind: "type",
                    itemId: this.options.itemType,
                    destination: this.options.parentNodeRef,
                    mode: "create",
                    submitType: "json",
                    formId: "association-create-new-node-form",
                    showCancelButton: true
                };

                new Alfresco.module.SimpleDialog("create-new-form-dialog-" + this.eventGroup).setOptions({
                    width:"40em",
                    templateUrl:templateUrl,
                    templateRequestParams: templateRequestParams,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn: this.setCreateNewFormDialogTitle,
                        scope: this
                    },
                    onSuccess:{
                        fn:function (response) {
                            this.options.selectedValueNodeRef = response.json.persistedObject;
                            this.populateSelect();
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

            setCreateNewFormDialogTitle: function (p_form, p_dialog) {
                var message;
                if ( this.options.createNewMessage ) {
                    message = this.options.createNewMessage;
                } else {
                    message = this.msg("dialog.createNew.title");
                }
                p_dialog.dialog.setHeader(message);
                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                this.doubleClickLock = false;
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
                                    if (this.options.showCreateNewButton && this.createNewButton != null) {
                                        this.createNewButton.set("disabled", !oResults.hasPermAddChildren);
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

            destroy: function AssociationSelectOne_destroy()
            {
                LogicECM.module.AssociationSelectOne.superclass.destroy.call(this);
            },

            loadDefaultValue: function AssociationSelectOne__loadDefaultValue() {
                if (this.options.defaultValue != null) {
                    this.defaultValue = this.options.defaultValue;
                    this.fillContent();
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
                                    me.fillContent();
                                }
                            },
                            failureMessage: "message.failure"
                        });
                } else {
                    this.fillContent();
                }
            },

            fillContent: function AssociationSelectOne_populateSelect() {
                var url = this._generateChildrenUrlPath(this.options.parentNodeRef) + this._generateChildrenUrlParams("");

                function successHandler(sRequest, oResponse, oPayload) {
					var results = oResponse.results,
						opts = [].slice.call(this.selectItem.children);

					opts.forEach(function(elem, idx) {
						if (!this.options.notSelectedOptionShow || idx > 0) {
							this.selectItem.removeChild(elem);
						}
					}, this);

					results.forEach(function(node) {
                        var opt = document.createElement('option');
                        opt.innerHTML = node.name;
                        opt.value = node.nodeRef;
						opt.selected = (node.nodeRef == this.options.selectedValueNodeRef || node.nodeRef == this.defaultValue);
                        this.selectItem.appendChild(opt);
					}, this);

                    this.onSelectChange();
                }

                function failureHandler (sRequest, oResponse) {
                    if (oResponse.status == 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else
                    {
                        //todo show failure message
                    }
                }

                this.dataSource.sendRequest(url,
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            populateSelect: function AssociationSelectOne_populateSelect() {
                this._createDataSource();
                if (!this.options.disabled) {
                    this.loadDefaultValue();
                } else {
                    this.fillContent();
                }
            },

            populateCurrentValue: function AssociationSelectOne_populateCurrentValue() {
                if (this.options.selectedValueNodeRef != null && this.options.selectedValueNodeRef.length > 0) {
                    Alfresco.util.Ajax.jsonGet(
                        {
                            url: Alfresco.constants.PROXY_URI + "lecm/node/" + this.options.selectedValueNodeRef.replace("://", "/"),
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
                } else if (this.options.notSelectedOptionShow){
                    if (this.options.notSelectedText.length > 0) {
                        this.currentDisplayValueElement.innerHTML = this.options.notSelectedText;
                    } else {
                        this.currentDisplayValueElement.innerHTML = "&nbsp;";
                    }
                }
            },

            _createDataSource: function AssociationSelectOne__createDataSource() {
                var  me = this,
                    pickerChildrenUrl = Alfresco.constants.PROXY_URI +
                        (this.options.customDatasource ? this.options.customDatasource : "lecm/forms/picker/") + this.options.itemFamily;

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
                var additionalFilter = this.options.additionalFilter;
                var params =  "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
                    "&sortProp=" + encodeURIComponent(this.options.sortProp) +
                    "&additionalFilter=" + encodeURIComponent(additionalFilter);

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

			onReadonlyControl: function (layer, args) {
				var addedInput, removedInput, selectInput, fn;
				if (!this.options.disabled && this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					if (this.createNewButton) {
						this.createNewButton.set('disabled', args[1].readonly);
					}
					selectInput = Dom.get(this.selectItemId);
					if (selectInput) {
						fn = args[1].readonly ? selectInput.setAttribute : selectInput.removeAttribute;
						fn.call(selectInput, "disabled", "");
					}
					if (!args[1].readonly) {
						addedInput = Dom.get(this.addedItemId);
						if (addedInput) {
							addedInput.disabled = false;
						}
						removedInput = Dom.get(this.removedItemId);
						if (removedInput) {
							removedInput.disabled = false;
						}
					}
				}
			}
        });
})();
