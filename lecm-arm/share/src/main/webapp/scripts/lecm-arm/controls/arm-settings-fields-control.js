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

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.SettingsAddFields = function (htmlId) {
		LogicECM.module.ARM.SettingsAddFields.superclass.constructor.call(this, "LogicECM.module.ARM.SettingsAddFields", htmlId);
		this.selectedItems = {};
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.SettingsAddFields, Alfresco.component.Base,
		{
			controlId: null,

			selectedItems: null,

			options: {
				parentNodeRef: null,

				disabled: false,

				selectedValue: null,

				currentValue: "",

				addFieldsFormId: null
			},

			onReady: function () {
				this.controlId = this.id + '-cntrl';


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
			},

			showAddFromModelDialog: function() {
				this.selectedItems = {};

				var url = Alfresco.constants.PROXY_URI + "/lecm/arm/settings/fields?nodeRef=" + encodeURIComponent(this.options.parentNodeRef);
				this.widgets.dataSource = new YAHOO.util.DataSource(url,
					{
						responseType: YAHOO.util.DataSource.TYPE_JSON,
						connXhrMode: "queueRequests",
						responseSchema:
						{
							resultsList: "items"
						}
					});

				var columnDefinitions = [
					{key: "", label: "", sortable: false, formatter: this.fnRenderCellSelected, width: 16, maxAutoWidth: 16},
					{key: "title", label: "Описание", sortable: false, width: 200, maxAutoWidth: 300},
					{key: "name", label: "Название", sortable: false, width: 300, maxAutoWidth: 400},
					{key: "type", label: "Тип", sortable: false, width: 100, maxAutoWidth: 200}];

				this.widgets.dataTable = new YAHOO.widget.DataTable(this.widgets.dialog.id + "-content", columnDefinitions, this.widgets.dataSource);

				this.widgets.dataTable.subscribe("checkboxClickEvent", function (e) {
					var id = e.target.value;
					this.selectedItems[id] = e.target.checked;
				}, this, true);

				this.widgets.dialog.show();
			},

			fnRenderCellSelected: function (elCell, oRecord, oColumn, oData) {
				Dom.setStyle(elCell, "width", oColumn.width + "px");
				Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

				elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oRecord.getData("name") + '">';
			},

			addFields: function() {
				var me = this;
				Alfresco.util.Ajax.jsonPost(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/arm/settings/addFields",
						dataObj: {
							parentNodeRef: this.options.parentNodeRef,
							fields: this.getSelectedItems()
						},
						successCallback: {
							fn: function (response) {
								var nodeRefs = [];
								if (response.json != null) {
									for (var i = 0; i < response.json.length; i++) {
										nodeRefs.push(response.json[i].nodeRef);
									}
								}
								YAHOO.Bubbling.fire("selectedItemAdded",
									{
										id: me.id,
										nodeRefs: nodeRefs
									});

								this.widgets.dialog.hide();
							},
							scope: this
						},
						failureMessage: "message.failure"
					});
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
							name: record.getData("name"),
							title: record.getData("title")
						});
					}
				}

				return items;
			}
		});
})();
