/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.RODatatableControl = function (name, containerId, options, messages) {
		LogicECM.module.ModelEditor.RODatatableControl.superclass.constructor.call(this, name, containerId);
		this.setOptions(options);
		this.setMessages(messages);
		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.RODatatableControl, Alfresco.component.Base, {

		options: {
			columnDefinitions: null,
//			dialogElements: null,
			responseSchema: null,
			url: null,
			data: null
		},
		
		formatBoolean: function (el, oRecord, oColumn, oData, oDataTable) {
			var bChecked = (oData===true) ? ' checked="checked"' : '';
			el.innerHTML = '<input disabled="true" type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
		},
		
		formatDropdown: function (el, oRecord, oColumn, oData, oDataTable) {
			var select = new IT.widget.Select({ name: "parentRef", options: oColumn.dropdownOptions, value: oData, showdefault: true, disabled: true });
			select.render(el);
		},

		onReady: function () {
			Alfresco.util.Ajax.request({
				url: this.options.url,
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.options.data = successResponse.json.data;
						this.widgets.datasource = new YAHOO.util.DataSource(this.options.data, {
							responseSchema: this.options.responseSchema
						});

						this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.options.columnDefinitions, this.widgets.datasource);
					}
				},
				failureMessage: this.msg('Не удалось получить данные')
			});
		}
	}, true);
})();
