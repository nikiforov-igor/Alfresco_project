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
    var Dom = YAHOO.util.Dom;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationSelectOne = function LogicECM_module_AssociationSelectOne(fieldHtmlId)
    {
        LogicECM.module.AssociationSelectOne.superclass.constructor.call(this, "LogicECM.module.AssociationSelectOne", fieldHtmlId, [ "container", "resize", "datasource"]);
        this.selectItemId = fieldHtmlId + "-added";
        this.removedItemId = fieldHtmlId + "-removed";
        this.controlId = fieldHtmlId;
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

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

                maxSearchResults: 1000,

                nameSubstituteString: "{cm:name}",

                openSubstituteSymbol: "{",

                closeSubstituteSymbol: "}",

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
                this.selectItem = Dom.get(this.selectItemId);
                if (this.selectItem) {
                    this.populateSelect();
                }
	            YAHOO.util.Event.on(this.selectItemId, "change", this.onSelectChange, this, true);

                this.currentDisplayValueElement = Dom.get(this.currentDisplayValueId);
                if (this.currentDisplayValueElement) {
                    this.populateCurrentValue();
                }
                if (this.options.showCreateNewButton) {
                    this.createNewButton =  new YAHOO.widget.Button(
                        this.controlId + "-selectone-create-new-button",
                        { onclick: { fn: this.showCreateNewItemWindow, obj: null, scope: this } }
                    );
                }

            },

	        onSelectChange: function AssociationTreeViewer_onSelectChange() {
	            Dom.get(this.controlId).value = this.selectItem.value;

		        if (this.options.mandatory) {
			        YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
		        }

		        YAHOO.Bubbling.fire("formValueChanged",
			        {
				        eventGroup:this,
				        addedItems:this.selectItem.value,
				        removedItems:Dom.get(this.removedItemId).value,
				        selectedItems:this.selectItem.value,
				        selectedItemsMetaData:Alfresco.util.deepCopy(this.selectItem.value)
			        });
                if (this.options.primaryCascading) {
                        YAHOO.Bubbling.fire("changeDropDown",{bubblingLabel: this.options.fieldId});
                }
	        },

            showCreateNewItemWindow: function AssociationTreeViewer_showCreateNewItemWindow() {
                var templateUrl = this.generateCreateNewUrl(this.options.parentNodeRef, this.options.itemType);

                new Alfresco.module.SimpleDialog("create-new-form-dialog-" + this.eventGroup).setOptions({
                    width:"40em",
                    templateUrl:templateUrl,
                    actionUrl:null,
                    destroyOnHide:true,
                    doBeforeDialogShow:{
                        fn:this.setCreateNewFormDialogTitle
                    },
                    onSuccess:{
                        fn:function (response) {
                            this.options.selectedValueNodeRef = response.json.persistedObject;
                            this.populateSelect();
                        },
                        scope:this
                    }
                }).show();
            },

            setCreateNewFormDialogTitle: function (p_form, p_dialog) {
                var fileSpan = '<span class="light">Create new</span>';
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-form-container_h", fileSpan]
                );
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

            destroy: function AssociationSelectOne_destroy()
            {
                LogicECM.module.AssociationSelectOne.superclass.destroy.call(this);
            },

            populateSelect: function AssociationSelectOne_populateSelect() {
                this._createDataSource();

                var successHandler = function (sRequest, oResponse, oPayload)
                {
                    var emptyOption = this.selectItem.options[0];
	                var emptOpt = document.createElement('option');
	                emptOpt.innerHTML = emptyOption.innerHTML;
	                emptOpt.value = emptyOption.value;

                    this.selectItem.innerHTML = "";
                    this.selectItem.appendChild(emptOpt);

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

            populateCurrentValue: function AssociationSelectOne_populateCurrentValue() {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.PROXY_URI + "slingshot/node/" + this.options.selectedValueNodeRef.replace("://", "/"),
                        successCallback:
                        {
                            fn: function (response) {
                                var properties = response.json.properties;
                                var name = this.options.nameSubstituteString;
                                for (var i = 0; i < properties.length; i++) {
                                    var prop = properties[i];
                                    if (prop.name && prop.values[0]) {
                                        var propSubstName = this.options.openSubstituteSymbol + prop.name.prefixedName + this.options.closeSubstituteSymbol;
                                        if (name.indexOf(propSubstName) != -1) {
                                            name = name.replace(propSubstName, prop.values[0].value);
                                        }
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
                var params =  "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString);

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
            }
         });
})();