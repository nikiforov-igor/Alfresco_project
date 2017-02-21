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

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
	var Dom = YAHOO.util.Dom,
	Util = LogicECM.module.Base.Util;

	LogicECM.module.ARM.SettingsAddTypes = function (htmlId) {
		LogicECM.module.ARM.SettingsAddTypes.superclass.constructor.call(this, "LogicECM.module.ARM.SettingsAddTypes", htmlId);
		this.selectedItems = {};
		this.choosenItems = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.SettingsAddTypes, Alfresco.component.Base,
		{
			controlId: null,

			selectedItems: null,

			choosenItems: null,

			options: {
				parentNodeRef: null,

				disabled: false,

				selectedValue: null,

				currentValue: "",

				currentValues: null,

				itemId: null,

				destination: null,
				
				dataSource: null,

				addFieldsFormId: null,

				multipleSelectMode: null
			},

			onReady: function () {
				this.controlId = this.id + "-cntrl";


				if (!this.options.disabled) {
					this.widgets.dialog = Alfresco.util.createYUIPanel(this.options.addFieldsFormId,
						{
							width: "60em"
						});

					Alfresco.util.createYUIButton(this, "-cntrl-addFields-add", this.addFields, {}, Dom.get(this.options.addFieldsFormId + "-add"));

					Alfresco.util.createYUIButton(this, "-cntrl-addFields-cancel", this.closeDialog, {}, Dom.get(this.options.addFieldsFormId + "-cancel"));

					this.widgets.addFromModel = new YAHOO.widget.Button(
						this.controlId + "-add-from-model-button",
						{
							onclick: {
								fn: this.showAddFromModelDialog,
								scope: this
							}
						}
					);


				}

				if (this.options.currentValues[0])
					this.choosenItems = this.options.currentValues;

				var url = Alfresco.constants.PROXY_URI + this.options.dataSource + (this.options.dataSource.indexOf("?") != -1 ? "&" : "?") + "itemId=" + encodeURIComponent(this.options.itemId);
				if (this.options.destination) {
					url += "&destination=" + encodeURIComponent(this.options.destination);
				}
				Alfresco.util.Ajax.jsonGet({
					url: url,
					successCallback: {
						fn: function (response) {
							var items = response.json.items;
							if (items) {
								this.allItems = items;
								this.showAlreadySelectedItems();
							}
						},
						scope: this
					},
					failureMessage: this.msg("message.failure")
				});
			},

			showAddFromModelDialog: function() {
				this.selectedItems = {};

				var url = Alfresco.constants.PROXY_URI + this.options.dataSource + (this.options.dataSource.indexOf("?") != -1 ? "&" : "?") + "itemId=" + encodeURIComponent(this.options.itemId);
				if (this.options.destination != null) {
					url += "&destination=" + encodeURIComponent(this.options.destination);
				}
				
				this.widgets.dataSource = new YAHOO.util.DataSource(url,
					{
						responseType: YAHOO.util.DataSource.TYPE_JSON,
						connXhrMode: "queueRequests",
						responseSchema:
						{
							resultsList:"items"
						}
					});

				var columnDefinitions = [
					{key: "", label: "", sortable: false, formatter: this.fnRenderCellSelected.bind(this), width: 16, maxAutoWidth: 16},
					{key: "name", label: "name", sortable: false, width: 200, maxAutoWidth: 400},
					{key: "value", label: "value", sortable: false, width: 100, maxAutoWidth: 200}];

				this.widgets.dataTable = new YAHOO.widget.DataTable(this.widgets.dialog.id + "-content", columnDefinitions, this.widgets.dataSource);

				this.widgets.dataTable.subscribe("checkboxClickEvent", function (e) {
					var id = e.target.value;
					this.selectedItems[id] = e.target.checked;
				}, this, true);

				this.widgets.dataTable.subscribe("radioClickEvent", function (e) {
					var id = e.target.value;
					this.selectedItems[id] = e.target.checked;
				}, this, true);

				this.widgets.dialog.show();
			},

			fnRenderCellSelected: function (elCell, oRecord, oColumn, oData) {
				Dom.setStyle(elCell, "width", oColumn.width + "px");
				Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

				if(this.options.multipleSelectMode)
					if (this.choosenItems.indexOf(oRecord.getData("value")) == -1)
						elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oRecord.getData("name") + '">';
					else 
					 	elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oRecord.getData("name") + '" checked >';
				else
					if (this.choosenItems.indexOf(oRecord.getData("value")) == -1)
						elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="radio" name="fileChecked" value="'+ oRecord.getData("name") + '">';
					else 
					 	elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="radio" name="fileChecked" value="'+ oRecord.getData("name") + '" checked >';


			},

			addFields: function() {			
				var itemsFromControl = this.getSelectedItems();

				if (!this.options.multipleSelectMode)
					this.choosenItems = [];

				for (var i = 0; i < itemsFromControl.length; i++) {
					if(this.choosenItems.indexOf(itemsFromControl[i].name) == -1) {
						this.choosenItems.push(itemsFromControl[i].name);
					}
				}

				this.rewriteItemsInHiddenInput();

				this.selectedItems = itemsFromControl;
				
				this.showAlreadySelectedItems();

				this.widgets.dialog.hide();
			},

			rewriteItemsInHiddenInput : function () {
				var itemsString = "";
				for (var i = 0; i < this.choosenItems.length; i++) {
					itemsString += this.choosenItems[i];
					
					if(i < this.choosenItems.length - 1)
						itemsString += ",";
				}

				Dom.get(this.id).value = itemsString;
			},

			closeDialog: function() {
				this.widgets.dialog.hide();
			},

			getSelectedItems: function() {
				var items = [],
					recordSet = this.widgets.dataTable.getRecordSet(),
					record;
				for (var i = 0; i <= recordSet.getRecords().length; i++) {
					record = recordSet.getRecord(i);
					if (record && this.selectedItems[record.getData("name")]) {
						items.push({
							name: record.getData("value"),
							title: record.getData("name")
						});
					}
				}

				return items;
			},

			//получает вид строки в контроле
			getDefaultView: function (node) {
				return "<span class='not-person'>" + node.title +" (" + node.name + ")</span>";
			},

			getRemoveButtonHTML: function (node, dopId) {
				if (!dopId) {
					dopId = "";
				}
				return Util.getControlItemRemoveButtonHTML("t-" + this.controlId + node.name + dopId);
			},

			//обработка нажатия -
			removeType: function (event, params) {
				if (params.node != null) {
					var indexForDelete = this.choosenItems.indexOf(params.node.name);
					this.choosenItems.splice(indexForDelete, 1);
					this.rewriteItemsInHiddenInput();
					this.showAlreadySelectedItems();
				}
			},

			attachRemoveClickListener: function (params) {
				YAHOO.util.Event.on("t-" + this.controlId + params.node.name + params.dopId, 'click', this.removeType, {
					node: params.node,
					updateForms: params.updateForms
				}, this);
			},

			//перерисовка выбранных элементов
			showAlreadySelectedItems: function () {
				el = Dom.get(this.controlId + "-currentValueDisplay");
				el.innerHTML = '';

				if (el != null) {
					for (var i = 0; i < this.choosenItems.length; i++) {
						for (var j = 0; j < this.allItems.length; j++) {
							if(this.choosenItems[i] == this.allItems[j].value) {
								var itemForAdd = {
									name : this.allItems[j].value,
									title : this.allItems[j].name
								}

								el.innerHTML += Util.getCroppedItem(this.getDefaultView(itemForAdd), this.getRemoveButtonHTML(itemForAdd, "_c"));
								YAHOO.util.Event.onAvailable("t-" + this.controlId + itemForAdd.name + "_c", this.attachRemoveClickListener, {node: itemForAdd, dopId: "_c", updateForms: true}, this);
							}
						}
					}
				}
			}
		

		});
})();