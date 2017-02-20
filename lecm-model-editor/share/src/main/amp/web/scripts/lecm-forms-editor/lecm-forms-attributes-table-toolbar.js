/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function () {
	var DEFAULT_URI = 'lecm/docforms/fields';

	LogicECM.module.FormsEditor.AttributesTableToolbar = function (htmlId) {
		LogicECM.module.FormsEditor.AttributesTableToolbar.superclass.constructor.call(this, 'LogicECM.module.FormsEditor.AttributesTableToolbar', htmlId, ['datasource', 'datatable']);
		this.selectedItems = [];
		return this;
	};

	YAHOO.extend(LogicECM.module.FormsEditor.AttributesTableToolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.AttributesTableToolbar.prototype,
		{
			selectedItems: null,

			_initButtons: function () {
				this.widgets.dialog = Alfresco.util.createYUIPanel(this.id + '-addFieldsForm',
					{
						width: '60em'
					});
				this.toolbarButtons['defaultActive'].newRowButton = Alfresco.util.createYUIButton(this, 'newRowButton', this.createDataTable,
					{
						value: 'create'
					});
				this.toolbarButtons['defaultActive'].newFakeRowButton = Alfresco.util.createYUIButton(this, 'newFakeRowButton', this.onNewRow,
					{
						value: 'create'
					});

				Alfresco.util.createYUIButton(this, '-addFieldsForm-add', this.addFields, {}, Dom.get(this.id + '-addFieldsForm-add'));

				Alfresco.util.createYUIButton(this, '-addFieldsForm-cancel', this.closeDialog, {}, Dom.get(this.id + '-addFieldsForm-cancel'));
			},

			createDataTable: function() {
				var datasource = this.options.datasourceUri ? this.options.datasourceUri : DEFAULT_URI;

				var url = Alfresco.constants.PROXY_URI + datasource + '?formNodeRef=' + encodeURIComponent(this.options.itemNodeRef);
				this.widgets.dataSource = new YAHOO.util.DataSource(url,
					{
						responseType: YAHOO.util.DataSource.TYPE_JSON,
						connXhrMode: 'queueRequests',
						responseSchema:
						{
							resultsList: 'items'
						}
					});

				var columnDefinitions = [
					{key: '', label: '<input type="checkbox" id="' + this.id + '-select-all-records">', sortable: false, formatter: this.fnRenderCellSelected, width: 16, maxAutoWidth: 16},
					{key: 'title', label: Alfresco.util.message('lecm.meditor.lbl.description'), sortable: false, width: 200, maxAutoWidth: 300},
					{key: 'name', label: Alfresco.util.message('lecm.meditor.lbl.title.name'), sortable: false, width: 300, maxAutoWidth: 400},
					{key: 'type', label: Alfresco.util.message('lecm.meditor.lbl.type'), sortable: false, width: 100, maxAutoWidth: 200}];

				this.widgets.dataTable = new YAHOO.widget.DataTable(this.widgets.dialog.id + '-content', columnDefinitions, this.widgets.dataSource);

				YAHOO.util.Event.onAvailable(this.id + '-select-all-records', function () {
					YAHOO.util.Event.on(this.id + '-select-all-records', 'click', this.selectAllClick, this, true);
				}, this, true);

				this.widgets.dataTable.subscribe('checkboxClickEvent', function (e) {
					var id = e.target.value;
					this.selectedItems[id] = e.target.checked;

					var checks = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
						len = checks.length, i;

					var allChecked = true;
					for (i = 0; i < len; i++) {
						if (!checks[i].checked) {
							allChecked = false;
							break;
						}
					}
					Dom.get(this.id + '-select-all-records').checked = allChecked;
				}, this, true);

				Dom.removeClass(this.widgets.dialog.id, 'hidden');
				this.widgets.dialog.show();
			},

			fnRenderCellSelected: function (elCell, oRecord, oColumn, oData) {
				Dom.setStyle(elCell, 'width', oColumn.width + 'px');
				Dom.setStyle(elCell.parentNode, 'width', oColumn.width + 'px');

				elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oRecord.getData('name') + '">';
			},

			selectAllClick: function() {
				var selectAllElement = Dom.get(this.id + '-select-all-records');
				if (selectAllElement.checked) {
					this.selectItems('selectAll');
				} else {
					this.selectItems('selectNone');
				}
			},

			selectItems: function(p_selectType)
			{
				var recordSet = this.widgets.dataTable.getRecordSet(),
					checks = Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
					aPageRecords,
					startRecord,
					len = checks.length,
					record, i, fnCheck;
				if (this.widgets.paginator) {
					aPageRecords = this.widgets.paginator.getPageRecords();
					startRecord = aPageRecords[0];
				} else {
					startRecord = 0;
				}
				switch (p_selectType)
				{
					case 'selectAll':
						fnCheck = function(assetType, isChecked)
						{
							return true;
						};
						break;

					case 'selectNone':
						fnCheck = function(assetType, isChecked)
						{
							return false;
						};
						break;

					default:
						fnCheck = function(assetType, isChecked)
						{
							return isChecked;
						};
				}

				for (i = 0; i < len; i++)
				{
					record = recordSet.getRecord(i + startRecord);
					this.selectedItems[record.getData('name')] = checks[i].checked = fnCheck(record.getData('type'), checks[i].checked);
				}
			},

			getSelectedItems: function()
			{
				var items = [],
					recordSet = this.widgets.dataTable.getRecordSet(),
					record;
				for (var i = 0; i <= recordSet.getRecords().length; i++) {
					record = recordSet.getRecord(i);
					if (record && this.selectedItems[record.getData('name')])
					{
						items.push({
								name: record.getData('name'),
								title: record.getData('title')
							});
					}
				}

				return items;
			},

			addFields: function() {
				var me = this;
				Alfresco.util.Ajax.jsonPost(
					{
						url: Alfresco.constants.PROXY_URI + '/lecm/docforms/addAttributes',
						dataObj: {
							formNodeRef: this.options.itemNodeRef,
							attributes: this.getSelectedItems()
						},
						successCallback: {
							fn: function (response) {
								YAHOO.Bubbling.fire('datagridRefresh',
									{
										bubblingLabel:me.options.bubblingLabel
									});

								this.widgets.dialog.hide();
							},
							scope: this
						},
						failureMessage: 'message.failure'
					});
			},

			closeDialog: function() {
				this.widgets.dialog.hide();
			}
		}, true);
})();
