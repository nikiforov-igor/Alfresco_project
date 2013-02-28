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


/**
 * LogicECM Connection module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Connection
 */
LogicECM.module.Connection = LogicECM.module.Connection || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Bubbling = YAHOO.Bubbling,
		Event = YAHOO.util.Event;


	LogicECM.module.Connection.TypeSelect = function LogicECM_module_Connection_TypeSelect(fieldHtmlId)
	{
		LogicECM.module.Connection.TypeSelect.superclass.constructor.call(this, "LogicECM.module.Connection.TypeSelect", fieldHtmlId, [ "container", "datasource"]);
		this.selectItemId = fieldHtmlId + "-added";
		this.removedItemId = fieldHtmlId + "-removed";
		this.controlId = fieldHtmlId;
		this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";
		this.chbxShowAllId = fieldHtmlId + "-show-all";

		Bubbling.on("changeConnectedDocument", this.changeConnectedDocument, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Connection.TypeSelect, Alfresco.component.Base,
		{
			options: {
				formId: null,
				primaryDocumentInputId: null,
				connectedDocumentInputId: null,
				mandatory: false,
				notSelectedOptionShow: false
			},

			id: null,
			controlId: null,
			createNewButton: null,
			selectItemId: null,
			chbxShowAllId: null,
			removedItemId: null,
			currentDisplayValueId: null,
			selectItem: null,
			currentDisplayValueElement: null,
			allConnectionTypes: null,
			primaryDocumentNodeRef: null,
			connectedDocumentNodeRef: null,
			defaultSelectedValue: null,
			typeSelectElements: null,
			existConnectionTypes: null,

			onReady: function()
			{
				this.selectItem = Dom.get(this.selectItemId);
				Event.on(this.selectItemId, "change", this.onSelectChange, this, true);
				Event.on(this.chbxShowAllId, "click", this.populateSelect, this, true);
				this.loadAllConnectionTypes();
			},

			onSelectChange: function () {
				if (this.selectItem != null) {
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
				}
			},

			changeConnectedDocument: function(layer, args) {
				this.primaryDocumentNodeRef = null;
				var primaryDocumentInput = Dom.get(this.options.primaryDocumentInputId);
				if (primaryDocumentInput != null && primaryDocumentInput.value.length > 0) {
					this.primaryDocumentNodeRef = primaryDocumentInput.value;
				}
				this.connectedDocumentNodeRef = null;
				var connectedDocumentInput = Dom.get(this.options.connectedDocumentInputId);
				if (connectedDocumentInput != null && connectedDocumentInput.value.length > 0) {
					this.connectedDocumentNodeRef = connectedDocumentInput.value;
				}
				this.loadConnectionTypes();
			},

			populateSelect: function() {
				var chbxShowAll = Dom.get(this.chbxShowAllId);
				var selectItems = null;
				if (chbxShowAll.checked) {
					selectItems = this.allConnectionTypes;
				} else {
					selectItems = this.typeSelectElements;
				}

				this.clearSelect();
				if (selectItems != null) {
					for (var i = 0; i < selectItems.length; i++) {
						var node = selectItems[i];
						var opt = document.createElement('option');
						opt.innerHTML = node.name;
						opt.value = node.nodeRef;
                        var exist = false;
                        if (this.existConnectionTypes != null) {
                            for (var j = 0; j < this.existConnectionTypes.length; j++) {
                                if (node.nodeRef == this.existConnectionTypes[j].nodeRef) {
                                    exist = true;
                                }
                            }
                        }
                        if (exist) {
                            opt.disabled = "disabled";
                        }
						if (!exist && this.defaultSelectedValue != null && node.nodeRef == this.defaultSelectedValue) {
							opt.selected = true;
						}
						this.selectItem.appendChild(opt);
					}

					this.onSelectChange();
				}
			},

			clearSelect: function() {
				if (this.options.notSelectedOptionShow && this.selectItem != null) {
					var emptyOption = this.selectItem.options[0];
					var emptOpt = document.createElement('option');
					emptOpt.innerHTML = emptyOption.innerHTML;
					emptOpt.value = emptyOption.value;

					this.selectItem.innerHTML = "";
					this.selectItem.appendChild(emptOpt);
				}
			},

			loadConnectionTypes: function() {
				if (this.primaryDocumentNodeRef != null && this.connectedDocumentNodeRef != null) {
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/connections/types/available?primaryDocumentNodeRef=" +
						this.primaryDocumentNodeRef + "&connectedDocumentNodeRef=" + this.connectedDocumentNodeRef;
					var me = this;
					var callback = {
						success:function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
							if (oResults != null && me.selectItem != null) {
								me.defaultSelectedValue = oResults.defaultConnectionType;
								me.existConnectionTypes = oResults.existConnectionTypes;
								me.typeSelectElements = oResults.availableConnectionTypes;

								if (me.typeSelectElements == null) {
									me.typeSelectElements = me.allConnectionTypes;
								}
								me.populateSelect();
							}
						},
						failure:function (oResponse) {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						},
						argument:{
							context:this
						},
						timeout:10000
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				}
			},

			loadAllConnectionTypes: function() {
				var sUrl = Alfresco.constants.PROXY_URI + "/lecm/connections/types/all";
				var me = this;
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults != null && oResults.connectionTypes != null) {
							me.allConnectionTypes = oResults.connectionTypes;
						}
					},
					failure:function (oResponse) {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					},
					argument:{
						context:this
					},
					timeout:10000
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			}
		});
})();