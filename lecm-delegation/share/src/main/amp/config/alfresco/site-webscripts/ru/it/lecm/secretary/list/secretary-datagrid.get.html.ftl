<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = id + "-dtgrd">
<script>
(function(){
	var secretaryDatagrid = new LogicECM.module.Base.DataGrid('${datagridId}');
	secretaryDatagrid.setMessages(${messages});
	secretaryDatagrid.setOptions({
		overrideSortingWith: false,
		usePagination: false,
		dataSource: 'lecm/secretary/list',
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: '${datagridId}',
		expandable: false,
		showActionColumn: true,
		actions:[
			{
				type: 'datagrid-action-link-${datagridId}',
				id: 'canReceiveTasks',
				permission: 'edit',
				label: '${msg("label.secretary.canReceiveTasks")}',
				evaluator:function (rowData) {
					var itemData = rowData.itemData['assoc_lecm-secretary-aspects_can-receive-tasks-from-chiefs'];
					return !(itemData && itemData.length && itemData[0].value);
				}
			}
		]
	});
	secretaryDatagrid.getCustomCellFormatter = function (grid, elCell, oRecord, oColumn, oData) {
		var html = '';
		if (!oRecord) {
			oRecord = this.getRecord(elCell);
		}
		if (!oColumn) {
			oColumn = this.getColumn(elCell.parentNode.cellIndex);
		}

		if (oRecord && oColumn) {
			if (!oData) {
				oData = oRecord.getData('itemData')[oColumn.field];
			}

			if (oData) {
				if ('assoc_lecm-secretary-aspects_can-receive-tasks-from-chiefs' == oColumn.key && grid.datagridColumns[oColumn.key]) {
					for (var i = 0, ii = oData.length, data; i < ii; i++) {
						data = oData[i];
						if (data.value) {
							html += '<div class="centered">';
							html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/complete-16.png' + '" width="16" alt="' + Alfresco.util.encodeHTML(data.displayValue) + '" title="' + Alfresco.util.encodeHTML(data.displayValue) + '" />';
							html += '</div>';
						}
						if (i < ii - 1) {
							html += '<br />';
						}
					}
				}
			}
		}
		return html.length > 0 ? html : null;  // возвращаем NULL чтобы выызвался основной метод отрисовки
	};
	secretaryDatagrid.canReceiveTasks = function(item) {
		var secretary = new Alfresco.util.NodeRef(item.nodeRef);
		var urlTemplate = '{proxyUri}lecm/secretary/{secretaryUri}/receiveTasks?chief={chief}';
		var url = YAHOO.lang.substitute(urlTemplate, {
				proxyUri: Alfresco.constants.PROXY_URI,
				secretaryUri: secretary.uri,
				chief: this.datagridMeta.nodeRef
		});
		var successCallback = {
			scope:this,
			fn: function(serverResponse) {
				YAHOO.Bubbling.fire('datagridRefresh', {
					bubblingLabel: this.options.bubblingLabel
				});
			}
		};
		Alfresco.util.Ajax.jsonPost({
			url: url,
			failureMessage: this.msg('message.failure'),
			successCallback: successCallback
		});
	};

	YAHOO.util.Event.onContentReady('${datagridId}', function() {
		YAHOO.Bubbling.fire('activeGridChanged', {
			datagridMeta: {
				sort: 'lecm-orgstr:employee-last-name|true',
				useFilterByOrg: false,
				itemType: '${itemType}',
				nodeRef: '${nodeRef}',
				datagridFormId: 'secretary-nested-datagrid'
			},
			bubblingLabel: '${datagridId}'
		});
	}, this, true);

	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules['${id}'] = secretaryDatagrid;
})();
</script>

<div id='${datagridId}' class='secretary-datagrid'>
	<@grid.datagrid datagridId false />
</div>
