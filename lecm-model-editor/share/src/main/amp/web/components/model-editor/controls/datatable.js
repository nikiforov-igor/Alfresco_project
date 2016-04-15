/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.DatatableControl = function (name, containerId, options, messages) {
		LogicECM.module.ModelEditor.DatatableControl.superclass.constructor.call(this, name, containerId);
		this.setOptions(options);
		this.setMessages(messages);
		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.DatatableControl, Alfresco.component.Base, {

		options: {
			columnDefinitions: null,
			dialogElements: null,
			responseSchema: null,
			data: null
		},

		formatBoolean: function (el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var bChecked = (oData==='true') ? ' checked="checked"' : '';

			el.innerHTML = '<input type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '"/>';
			YAHOO.util.Event.addListener(el,'change', function(e, oSelf) {
				var elTarget = YAHOO.util.Event.getTarget(e);
				oSelf.fireEvent('valueChangeEvent', {
					event:e,
					target:elTarget
				});
			},oDT);
		},

		formatText: function (el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var value = (YAHOO.lang.isValue(oData)) ? YAHOO.lang.escapeHTML(oData.toString()) : '',
				markup = '<input style="width:' + oColumn.width + 'px;" type="text" value="' + value + '" title="' + value + '" />';
			el.innerHTML = markup;

			YAHOO.util.Event.addListener(el,'keyup',function(e, oSelf) {
				var elTarget = YAHOO.util.Event.getTarget(e);
				oSelf.fireEvent('valueChangeEvent', {
					event:e,
					target:elTarget
			});
			},oDT);
		},

		formatActions: function (el, oRecord, oColumn, oData, oDataTable) {
			var deleteLink = document.createElement('a');
			//deleteLink.id = Dom.generateId();
			YAHOO.util.Dom.addClass(deleteLink, 'delete');
			deleteLink.innerHTML = '&nbsp;';
			el.appendChild(deleteLink);
		},

		deleteRow: function(oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			if (column.key == 'delete') {
				if (confirm(Alfresco.util.message('lecm.meditor.msg.approve.delete.row'))) {
					this.deleteRow(target);
					YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
				}
			} else {
				//this.onEventShowCellEditor(oArgs);
			}
		},

		onReady: function () {
			this.widgets.datasource = new YAHOO.util.DataSource(this.categoryArray, {
				responseSchema: this.options.responseSchema
			});

			this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.options.columnDefinitions, this.widgets.datasource);
			this.widgets.datatable.subscribe('cellClickEvent', this.deleteRow);
			this.widgets.datatable.on('valueChangeEvent', function(args) {
				var e = args.event,
					t = args.target,
					r = this.getRecord(t),
					c = this.getColumn(this.getCellIndex(t.parentNode));
				r.setData(c.key, t.value);
			});
			this.widgets.datatable.on('dropdownChangeEvent', function(args) {
				var e = args.event,
					t = args.target,
					r = this.getRecord(t),
					c = this.getColumn(this.getCellIndex(t.parentNode));
				r.setData(c.key, t.value);
			});

			this.widgets.buttonAdd = new YAHOO.widget.Button(this.id + '-button-add', {
				type: 'button',
				label: this.msg('lecm.meditor.lbl.add')
			});
			this.widgets.buttonAdd.set('onclick', {
				scope: this,
				fn: function(evt, obj) {
					YAHOO.Bubbling.fire(this.id + '-dialogshowEditDialog');
				}
			});

			this.widgets.dialog = new IT.widget.Dialog(this.id + '-dialog', this.widgets.datatable, {
				width: '300px',
				modal: true,
				fixedcenter: true,
				constraintoviewport: true,
				underlay: 'shadow',
				close: true,
				visible: false,
				draggable: false,
				elements: this.options.dialogElements
			});
			this.widgets.dialog.render();
		}
	}, true);
})();
