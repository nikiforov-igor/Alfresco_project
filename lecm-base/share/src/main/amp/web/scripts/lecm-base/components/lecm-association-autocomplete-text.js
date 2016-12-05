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
	var Dom = YAHOO.util.Dom;

	var $combine = Alfresco.util.combinePaths;

	LogicECM.module.AssociationAutoCompleteText = function (fieldHtmlId) {
		LogicECM.module.AssociationAutoCompleteText.superclass.constructor.call(this, "LogicECM.module.AssociationAutoCompleteText", fieldHtmlId, [ "container", "datasource"]);
		YAHOO.Bubbling.on("refreshAutocompleteItemList_" + fieldHtmlId, this.onRefreshAutocompleteItemList, this);
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);

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

                useStrictFilterByOrg: false,
				doNotCheckAccess: false,

				ignoreNodes: [],

				childrenDataSource: "lecm/forms/picker",

                allowedNodes:null,

                allowedNodesScript: null,

				useDynamicLoading: false,

				formId: null,

				fieldId: null
			},

			widgets: {
				autocomplete: null
			},

			dataArray: null,

			controlId:"",

			currentValueHtmlId: "",

			dataSource:null,

			searchProperties: null,

			readonly: false,

			onReady:function () {
				if (!this.options.disabled) {
					this.populateDataWithAllowedScript();
					if (this.options.useDynamicLoading) {
						this._loadSearchProperties();
					}
				}
			},

			onReadonlyControl: function (layer, args) {
				var input, fn, autocompleteInput;
				if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					input = Dom.get(this.id);
					if (input) {
						fn = args[1].readonly ? input.setAttribute : input.removeAttribute;
						fn.call(input, 'readonly', '');
					}
					if (this.widgets.autocomplete) {
						autocompleteInput = this.widgets.autocomplete.getInputEl();
						fn = args[1].readonly ? autocompleteInput.setAttribute : autocompleteInput.removeAttribute;
						fn.call(autocompleteInput, 'disabled', '');
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

				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
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

				this.widgets.autocomplete = new YAHOO.widget.AutoComplete(this.currentValueHtmlId, this.controlId + "-autocomplete-container", oDS);
				if (me.options.useDynamicLoading) {
					this.widgets.autocomplete.generateRequest = function(sQuery) {
						var searchData = "";

						Dom.addClass(me.controlId + "-autocomplete-input", "wait-for-load");

						for(var column in me.searchProperties) {
							searchData += column + ":" + decodeURIComponent(sQuery) + "#";
						}
						if (searchData != "") {
							searchData = searchData.substring(0,(searchData.length)-1);
						}

						return me._generateChildrenUrlParams(searchData);
					};
					this.widgets.autocomplete.doBeforeLoadData = function(sQuery , oResponse , oPayload) {
						Dom.removeClass(me.controlId + "-autocomplete-input", "wait-for-load");
						return true;
					};

					this.widgets.autocomplete.queryDelay = 1;

				}

				this.widgets.autocomplete.minQueryLength = 3;
				this.widgets.autocomplete.prehighlightClassName = "yui-ac-prehighlight";
				this.widgets.autocomplete.useShadow = true;
				this.widgets.autocomplete.forceSelection = false;
				this.widgets.autocomplete._bFocused = true;

				var selectItemHandler = function (sType, aArgs) {
					if (this.options.mandatory) {
						YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
					}
				}.bind(this);
				this.widgets.autocomplete.itemSelectEvent.subscribe(selectItemHandler);
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
                    "&additionalFilter=" + encodeURIComponent(additionalFilter) +
                    "&onlyInSameOrg=" + encodeURIComponent("" + this.options.useStrictFilterByOrg) +
                    "&doNotCheckAccess=" + encodeURIComponent("" + this.options.doNotCheckAccess)

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
			}
        });
})();
