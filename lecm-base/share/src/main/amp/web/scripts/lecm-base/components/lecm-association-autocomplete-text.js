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

	LogicECM.module.AssociationAutoCompleteText = function (fieldHtmlId) {
		LogicECM.module.AssociationAutoCompleteText.superclass.constructor.call(this, "LogicECM.module.AssociationAutoCompleteText", fieldHtmlId, [ "container", "datasource"]);
		YAHOO.Bubbling.on("refreshAutocompleteItemList_" + fieldHtmlId, this.onRefreshAutocompleteItemList, this);

		this.controlId = fieldHtmlId + "-cntrl";
		this.currentValueHtmlId = fieldHtmlId;
		this.dataArray = [];
        this.allowedNodes = null;
        this.allowedNodesScript = null;
		this.searchProperties = {};
		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationAutoCompleteText, Alfresco.component.Base,
		{
			options:{
				disabled: false,

				mandatory:false,

				startLocation: null,

				parentNodeRef:"",

				itemType:"cm:content",

				itemFamily:"node",

				maxSearchResults:10,

				nameSubstituteString:"{cm:name}",

                sortProp: "cm:name",

				additionalFilter: "",

				ignoreNodes: [],

				childrenDataSource: "lecm/forms/picker",

                allowedNodes:null,

                allowedNodesScript: null,

				useDynamicLoading: false
			},

			dataArray: null,

			controlId:"",

			currentValueHtmlId: "",

			dataSource:null,

			searchProperties: null,

			onReady:function () {
				if (!this.options.disabled) {
					this.populateDataWithAllowedScript();
					if (this.options.useDynamicLoading) {
						this._loadSearchProperties();
					}
				}
			},

			onRefreshAutocompleteItemList: function (layer, args)
			{
				var changeValue = "";
				var selectedItems = args[1].selectedItems;
				for (var i in selectedItems) {
					changeValue = selectedItems[i].name;
					break;
				}

				if (changeValue != "") {
					Dom.get(this.currentValueHtmlId).value = changeValue;
				}
			},

			makeAutocomplete: function() {
				var me = this;
				var oDS;

				if (me.options.useDynamicLoading) {
					var url = Alfresco.constants.PROXY_URI + this.options.childrenDataSource + "/" + this.options.itemFamily + this._generateChildrenUrlPath(this.options.parentNodeRef);
					oDS = new YAHOO.util.XHRDataSource(url);
					oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
					oDS.responseSchema = {
						resultsList: "data.items",
						fields:["name", "selectedName", "nodeRef"]
					};
				} else {
					oDS = new YAHOO.util.LocalDataSource(this.dataArray);
					oDS.responseSchema = {fields:["name", "nodeRef"]};
				}

				var oAC = new YAHOO.widget.AutoComplete(this.currentValueHtmlId, this.controlId + "-autocomplete-container", oDS);
				if (me.options.useDynamicLoading) {
					oAC.generateRequest = function(sQuery) {
						var searchData = "";
						for(var column in me.searchProperties) {
							searchData += column + ":" + decodeURIComponent(sQuery) + "#";
						}
						if (searchData != "") {
							searchData = searchData.substring(0,(searchData.length)-1);
						}

						return me._generateChildrenUrlParams(searchData);
					};
					oAC.queryDelay = 1;
				}

				oAC.minQueryLength = 3;
				oAC.prehighlightClassName = "yui-ac-prehighlight";
				oAC.useShadow = true;
				oAC.forceSelection = false;
				oAC._bFocused = true;

				var selectItemHandler = function (sType, aArgs) {
					if (this.options.mandatory) {
						YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
					}
				}.bind(this);
				oAC.itemSelectEvent.subscribe(selectItemHandler);
			},

			populateData: function () {
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

			_createDataSource: function () {
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

			_generateChildrenUrlPath: function (nodeRef)
			{
				return $combine("/", nodeRef.replace("://", "/"), "children");
			},

            _generateChildrenUrlParams: function (searchTerm) {
                var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
                    "&sortProp=" + encodeURIComponent(this.options.sortProp) +
                    "&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);

                if (this.options.startLocation && this.options.startLocation.charAt(0) == "/") {
                    params += "&xpath=" + encodeURIComponent(this.options.startLocation);
                } else if (this.options.xPathLocation) {
                    params += "&xPathLocation=" + encodeURIComponent(this.options.xPathLocation);
                    if (this.options.xPathLocationRoot != null) {
                        params += "&xPathRoot=" + encodeURIComponent(this.options.xPathLocationRoot);
                    }
                }
                // has a rootNode been specified?
                if (this.options.rootNode) {
                    var rootNode = null;

                    if (this.options.rootNode.charAt(0) == "{") {
                        if (this.options.rootNode == "{companyhome}") {
                            rootNode = "alfresco://company/home";
                        }
                        else if (this.options.rootNode == "{userhome}") {
                            rootNode = "alfresco://user/home";
                        }
                        else if (this.options.rootNode == "{siteshome}") {
                            rootNode = "alfresco://sites/home";
                        }
                    }
                    else {
                        // rootNode is either an xPath expression or a nodeRef
                        rootNode = this.options.rootNode;
                    }
                    if (rootNode !== null) {
                        params += "&rootNode=" + encodeURIComponent(rootNode);
                    }
                }

                return params;
            },

            destroy: function () {
                LogicECM.module.AssociationAutoCompleteText.superclass.destroy.call(this);
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
	                            if (context.options.useDynamicLoading) {
		                            context.makeAutocomplete();
	                            } else {
		                            context.populateData();
	                            }
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
			}
        });
})();