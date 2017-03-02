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

	LogicECM.module.AssociationCheckboxes = function LogicECM_module_AssociationCheckboxes(fieldHtmlId)
	{
		LogicECM.module.AssociationCheckboxes.superclass.constructor.call(this, "LogicECM.module.AssociationCheckboxes", fieldHtmlId, [ "container", "datasource"]);
		this.currentValueHtmlId = fieldHtmlId;
		this.controlId = fieldHtmlId + "-cntrl";
		this.addedItemId = this.controlId + "-added";
		this.removedItemId = this.controlId + "-removed";
		this.currentDisplayValueId = this.controlId + "-currentValueDisplay";
		this.checkboxesContainerId = this.controlId + "-checkboxes";
		this.dataArray = [];
		this.selectedItems = {};

		return this;
	};

	YAHOO.extend(LogicECM.module.AssociationCheckboxes, Alfresco.component.Base,
		{
			options:
			{
				disabled: false,

				parentNodeRef: "",

				startLocation: null,

				itemType: "cm:content",

				itemFamily:"node",

				mandatory: false,

				currentValue: "",

				maxSearchResults: 1000,

				nameSubstituteString: "{cm:name}",

                sortProp: "cm:name",

				defaultValuesDataSource: null,

				mode: null
			},

			rootNode: null,

			controlId: null,

			addedItemId: null,

			removedItemId: null,

			currentDisplayValueId: null,

			currentValueHtmlId: null,

			checkboxesContainerId: null,

			dataSource:null,

			dataArray: null,

			selectedItems: null,

			defaultValues: null,

			onReady: function AssociationCheckboxes_onReady()
			{
				this.loadSelectedItems();
			},

			loadSelectedItems: function AssociationCheckboxes_loadSelectedItems()
			{
				var arrItems = this.options.currentValue;

				var onSuccess = function AssociationCheckboxes__loadSelectedItems_onSuccess(response)
				{
					var items = response.json.data.items,
						item;
					this.selectedItems = {};

					for (var i = 0, il = items.length; i < il; i++)
					{
						item = items[i];
						this.selectedItems[item.nodeRef] = item;
					}

					this.loadDefaultValues();
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
								itemNameSubstituteString: this.options.nameSubstituteString
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
				} else {
					this.loadDefaultValues();
				}
			},

			loadDefaultValues: function () {
				if (this.options.defaultValuesDataSource) {
					Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.PROXY_URI + this.options.defaultValuesDataSource,
						successCallback: {
							fn: function (response) {
								var oResults = response.json;
								if (oResults) {
									this.defaultValues = [];
									for (var i = 0; i < oResults.length; i++) {
										this.defaultValues.push(oResults[i].nodeRef);
									}
								}
								this.loadData();
							},
							scope: this
						},
						failureMessage: this.msg("message.failure")
					});
				} else {
					this.loadData();
				}
			},

			loadData: function AssociationCheckboxes_loadData() {
				this._createDataSource();

				var successHandler = function (sRequest, oResponse, oPayload)
				{
					var results = oResponse.results;
					for (var i = 0; i < results.length; i++) {
						var node = results[i];
						if (node.selectable) {
							var select = false;
//							var defaultSelect = node[this.options.defaultSelectProperty.replace(":", "_")];
//							if (defaultSelect != null) {
//								select = defaultSelect;
//							}
							if (this.defaultValues != null) {
								for (var j = 0; j < this.defaultValues.length; j++) {
									if (node.nodeRef == this.defaultValues[j]) {
										select = true;
										break;
									}
								}
							}

							this.dataArray.push({
								name: node.name,
								nodeRef: node.nodeRef,
								inputId: "assoc-chbx-" + node.nodeRef,
								defaultSelect: select
							});
						}
					}
					this.populateCheckboxes();
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

			populateCheckboxes: function AssociationCheckboxes_populateCheckboxes() {
				if (this.dataArray != null) {
					var content = "";
					for (var i = 0; i < this.dataArray.length; i++) {
						content += '<li><input id="' + this.dataArray[i].inputId + '" type="checkbox" value="'
							+ this.dataArray[i].nodeRef + '"';

						if (this.selectedItems.hasOwnProperty(this.dataArray[i].nodeRef)) {
							content += 'checked="checked"';
						} else if (this.dataArray[i].defaultSelect && this.options.mode == "create") {
							content += 'checked="checked"';
							this.selectedItems[this.dataArray[i].nodeRef] = this.dataArray[i];
						}
						if (this.options.disabled) {
							content += 'disabled="disabled"';
						}
						content += '>';
						content += '<label class="checkbox" for="' + this.dataArray[i].inputId + '">' + this.dataArray[i].name + '</label></li>';
						YAHOO.util.Event.onAvailable(this.dataArray[i].inputId, this.attachCheckboxClickListener, this.dataArray[i], this);
						this.updateFormFields();
					}
					Dom.get(this.checkboxesContainerId).innerHTML = content;
				}
			},

			attachCheckboxClickListener: function AssociationCheckboxes_attachCheckboxClickListener(node)
			{
				YAHOO.util.Event.on(node.inputId, 'click', this.checkboxOnClick, node, this);
			},

			checkboxOnClick: function AssociationCheckboxes_checkboxOnClick(event, node)
			{
				var checkbox = Dom.get(node.inputId);
				if (checkbox.checked) {
					this.selectedItems[node.nodeRef] = node;
				} else {
					delete this.selectedItems[node.nodeRef];
				}
				this.updateFormFields();
			},

			_createDataSource: function AssociationCheckboxes__createDataSource() {
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

			_generateChildrenUrlPath: function AssociationCheckboxes__generateChildrenUrlPath(nodeRef)
			{
				return $combine("/", nodeRef.replace("://", "/"), "children");
			},

			_generateChildrenUrlParams: function AssociationCheckboxes__generateChildrenUrlParams(searchTerm)
			{
				var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
					"&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
                    "&sortProp=" + encodeURIComponent(this.options.sortProp);

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

			// Updates all form fields
			updateFormFields: function AssociationCheckboxes_updateFormFields()
			{
				if(!this.options.disabled)
				{
					var el;

					var addItems = this.getAddedItems();

					// Update added fields in main form to be submitted
					el = Dom.get(this.controlId + "-added");
					el.value = '';
					for (i in addItems) {
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
			}
		});
})();