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

LogicECM.module.Eds = LogicECM.module.Eds || {};

LogicECM.module.Eds.GlobalSettings = LogicECM.module.Eds.GlobalSettings || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		KeyListener = YAHOO.util.KeyListener;

	var $html = Alfresco.util.encodeHTML,
		$combine = Alfresco.util.combinePaths,
		$hasEventInterest = Alfresco.util.hasEventInterest;

	LogicECM.module.Eds.GlobalSettings.PotentialRolesTreeViewer = function(htmlId)
	{
		LogicECM.module.Eds.GlobalSettings.PotentialRolesTreeViewer.superclass.constructor.call(this, htmlId);
		YAHOO.Bubbling.on("refreshItemList", this.onRefreshItemList, this);
		YAHOO.Bubbling.on("selectedItemAdded", this.onSelectedItemAdded, this);
		YAHOO.Bubbling.on("formSubmit", this.saveAllChanges, this);

		this.selectedItems = {};
		this.addItemButtons = {};
		this.searchProperties = {};
		this.currentNode = null;
		this.rootNode = null;
		this.tree = null;
		this.isSearch = false;
		this.allowedNodes = null;
		this.allowedNodesScript = null;
		return this;
	};

	YAHOO.extend(LogicECM.module.Eds.GlobalSettings.PotentialRolesTreeViewer, LogicECM.module.AssociationTreeViewer, {});

	YAHOO.lang.augmentObject(LogicECM.module.Eds.GlobalSettings.PotentialRolesTreeViewer.prototype,
		{
			selectedEmployeesMap: {},
			options:
			{
				ignoreNodesInTreeView: true,
				prefixPickerId: null,
				showCreateNewLink: true,
				setCurrentValue: true,
				showSearch: true,
				showSelectedItemsPath: true,
				changeItemsFireAction: null,
				selectedValue: null,
				plane: false,
				currentValue: false,
				// If control is disabled (has effect in 'picker' mode only)
				disabled: false,
				// If this form field is mandatory
				mandatory: false,
				// If control allows to pick multiple assignees (has effect in 'picker' mode only)
				multipleSelectMode: false,
				initialized: false,
				rootLocation: null,
				rootNodeRef: "",
				itemType: "cm:content",
				treeItemType: null,
				maxSearchResults: 1000,
				treeRoteNodeTitleProperty: "cm:name",
				treeNodeSubstituteString: "{cm:name}",
				treeNodeTitleSubstituteString: "",
				nameSubstituteString: "{cm:name}",
				selectedItemsNameSubstituteString: null,
				employeeAbsenceMarker: false,
				fireAction:
				{
					addItem: null,
					ok: null,
					cancel: null,
					find: null
				},
				additionalFilter: "",
				ignoreNodes: null,
				childrenDataSource: "",
				allowedNodes: null,
				allowedNodesScript: null,
				createDialogClass: "",
				clearFormsOnStart: true,
				pickerButtonLabel: null,
				pickerButtonTitle: null,
				businessRoleId: ""
			},

			init: function() {
				var me = this;
				this.options.controlId = this.id + '-cntrl';
				if (this.options.prefixPickerId == null) {
					this.options.prefixPickerId = this.options.controlId;
				}
				this.eventGroup = this.options.prefixPickerId;

				this.options.pickerId = this.options.prefixPickerId + '-picker';
				Dom.setStyle(this.options.pickerId, "display", "block");

				// Create button if control is enabled
				if(!this.options.disabled)
				{
					// Create picker button
					var bottonGroup = new YAHOO.widget.ButtonGroup(this.options.prefixPickerId,
						{
							name: this.options.prefixPickerId + "-radiofield"
						}),
						onButton = new YAHOO.widget.Button(
							{
								id: this.options.prefixPickerId + "-on",
								label: this.msg("label.button.on"),
								title: this.msg("label.button.on"),
								type: "radio",
								checked: this.options.currentValue,
								value: true,
								container: this.options.prefixPickerId,
								onclick: {
									fn: function(e) {this.updateControlValue(true);},
									obj: null,
									scope: this
								}
							}),
						offButton = new YAHOO.widget.Button(
							{
								id: this.options.prefixPickerId + "-off",
								label: this.msg("label.button.off"),
								title: this.msg("label.button.off"),
								type: "radio",
								checked: !this.options.currentValue,
								value: false,
								container: this.options.prefixPickerId,
								onclick: {
									fn: function(e) {this.updateControlValue(false);},
									obj: null,
									scope: this
								}
							});

					bottonGroup.addButtons([onButton, offButton]);

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
                                    context._createSelectedControls();
                                    context.fillPickerDialog();
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function onFailure(response) {
                                    context.options.allowedNodes = null;
                                    context._createSelectedControls();
                                    context.fillPickerDialog();
                                },
                                scope: this
                            },
                            execScripts: true
                        });

                    } else {
                        this._createSelectedControls();
                        this.fillPickerDialog();
                    }
					this._loadSearchProperties();
					this.updateControlValue(this.options.currentValue);
				} else {
					this.updateViewForm();
				}
			},

			updateControlValue: function PotentialRolesTreeViewer_updateControlValue(value) {
				this.options.currentValue = value;
				Dom.get(this.id).value = value;
				this.updatePickerVisibility(!value);
			},

			updatePickerVisibility: function PotentialRolesTreeViewer_updatePickerVisibility(doShow) {
				var pickerObj = Dom.get(this.options.pickerId);
				Dom.setStyle(pickerObj,"display", (doShow) ? "block" : "none");
				this._loadSelectedItems(true, false);
			},

			_updateItems: function PotentialRolesTreeViewer__updateItems(nodeRef, searchTerm)
			{
				// Empty results table - leave tag entry if it's been rendered
				this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.loading"));
				this.widgets.dataTable.showTableMessage(this.msg("label.loading"), YAHOO.widget.DataTable.CLASS_EMPTY);
				this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

				var successHandler = function PotentialRolesTreeViewer__updateItems_successHandler(sRequest, oResponse, oPayload)
				{
					this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : nodeRef;
					this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
					if (this.options.showCreateNewLink && this.currentNode != null && this.currentNode.data.isContainer && (!this.isSearch || this.options.plane))
					{
						this.widgets.dataTable.onDataReturnAppendRows.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
					}
					else
					{
						this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
					}
				};

				var failureHandler = function PotentialRolesTreeViewer__updateItems_failureHandler(sRequest, oResponse)
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
				var url = this._generateEmployeesDatasourceUrlParams(nodeRef);

				if (Alfresco.logger.isDebugEnabled())
				{
					Alfresco.logger.debug("Generated pickerchildren url fragment: " + url);
				}

				// call the pickerchildren data webscript
				this.widgets.dataSource.liveData = Alfresco.constants.PROXY_URI + "lecm/eds/global-settings/api/employeesDatasource";
				this.widgets.dataSource.sendRequest(url,
					{
						success: successHandler,
						failure: failureHandler,
						scope: this
					});

				// the start location is now resolved
				this.startLocationResolved = true;

				this.updateSelectedItems();
			},

			_generateEmployeesDatasourceUrlParams: function PotentialRolesTreeViewer__generateEmployeesDatasourceUrl(organizationElementRef) {
				return "?businessRoleId=" + this.options.businessRoleId +
						"&orgElement=" + organizationElementRef +
						"&withDelegation=false" + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
						"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString());
			},

			updateSelectedItems: function PotentialRolesTreeViewer_updateSelectedItems() {
				var items = []; //this.selectedItems;
				var fieldId = this.options.pickerId + "-selected-elements";
				Dom.get(fieldId).innerHTML = '';
				Dom.get(fieldId).className = 'currentValueDisplay';

				if (this.currentNode) {
					items = this.selectedEmployeesMap[this.currentNode.data.nodeRef];
					if (items) {
						var num = 0;
						for (i in items) {
							if (typeof(items[i]) != "function") {
								if (this.options.plane || !this.options.showSelectedItemsPath) {
									var displayName = items[i].selectedName;
								} else {
									displayName = items[i].displayPath + "/" + items[i].selectedName;
									if (this.rootNode !== null && this.rootNode.data.displayPath !== null) {
										var rootNodeDisplayName = this.rootNode.data.displayPath + "/" + this.rootNode.label + "/";
										if (rootNodeDisplayName !== "") {
											displayName = displayName.replace(rootNodeDisplayName, "");
										}
									}
								}

								var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";

								Dom.get(fieldId).innerHTML
									+= '<div class="' + divClass + '"> ' + this.getDefaultView(displayName) + ' ' + this.getRemoveButtonHTML(items[i]) + '</div>';

								YAHOO.util.Event.onAvailable("t-" + this.options.prefixPickerId + items[i].nodeRef, this.attachRemoveClickListener, {node: items[i], dopId: "", updateForms: false}, this);
							}
						}
					} else {
						this.getPotentialWorkers(this.currentNode.data.nodeRef);
					}
				}

			},

			getPotentialWorkers: function(orgElementRef) {
				var currentOrgElement = this.currentNode.data.nodeRef;
				var onSuccess = function PotentialRolesTreeViewer_getPotentialWorkers_onSuccess(response)
				{
					var items = response.json.data.items,
						item,
						workers = {};

					//this.singleSelectedItem = null;
					for (var i = 0, il = items.length; i < il; i++) {
						item = items[i];
						workers[item.nodeRef] = item;
					}

					this.selectedEmployeesMap[currentOrgElement] = workers;
					if (items.length > 0) {
						this.updateSelectedItems();
					}
				};

				var onFailure = function PotentialRolesTreeViewer_getPotentialWorkers_onFailure(response)
				{
					//this.selectedItems = null;
				};
				Alfresco.util.Ajax.jsonRequest(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/eds/global-settings/api/getPotentialWorkers?businessRole=" + this.options.businessRoleId +
						"&organizationElement=" + this.currentNode.data.nodeRef + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
						"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()),
						method: "GET",
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
			},

			onSelectedItemAdded: function PotentialRolesTreeViewer_onSelectedItemAdded(layer, args)
			{
				// Check the event is directed towards this instance
				if ($hasEventInterest(this, args))
				{
					var obj = args[1];
					if (obj && obj.item)
					{
						if (!this.selectedEmployeesMap[this.currentNode.data.nodeRef])
							this.selectedEmployeesMap[this.currentNode.data.nodeRef] = {};

						this.selectedEmployeesMap[this.currentNode.data.nodeRef][obj.item.nodeRef] = obj.item;

						this.updateSelectedItems();
						this.updateAddButtons();
					}
				}
			},

			canItemBeSelected: function PotentialRolesTreeViewer_canItemBeSelected(id)
			{
				if (!(this.currentNode && this.currentNode.data && this.currentNode.data.type &&
					this.currentNode.data.type == this.options.treeItemType)) {
					return false;
				}

				var currentOrgElementWorkers = this.selectedEmployeesMap[this.currentNode.data.nodeRef];
				return !(currentOrgElementWorkers && currentOrgElementWorkers[id]);
			},

			removeNode: function PotentialRolesTreeViewer_removeNode(event, params)
			{
				if (this.currentNode) {
					delete this.selectedEmployeesMap[this.currentNode.data.nodeRef][params.node.nodeRef];
					this.updateSelectedItems();
					this.updateAddButtons();
					if (params.updateForms) {
						this.updateFormFields();
					}
				}
			},

			saveAllChanges: function(layer, args) {
				var onSuccess = function PotentialRolesTreeViewer_saveAllChanges_onSuccess(response) {
					Alfresco.util.PopupManager.displayMessage(
						{
							text: this.msg("message.details.success")
						}
					);
				}
				var onFailure = function PotentialRolesTreeViewer_saveAllChanges_onSuccess(response) {
					Alfresco.util.PopupManager.displayMessage(
						{
							text: this.msg("message.details.failure")
						}
					);
				}
				if (!this.options.currentValue && this.options.businessRoleId && this.selectedEmployeesMap) {
					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/eds/global-settings/api/savePotentialWorkers",
							method: "POST",
							dataObj: {
								potentialRolesMaps: [
									{
										businessRoleId: this.options.businessRoleId,
										employeesMap: this.selectedEmployeesMap
									}
								]
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
						}
					);
				}
			}
		}, true);
})();