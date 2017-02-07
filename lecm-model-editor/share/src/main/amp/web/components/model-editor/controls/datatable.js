/* global YAHOO, Alfresco, IT */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	var lang   = YAHOO.lang,
    util   = YAHOO.util,
    widget = YAHOO.widget,
    
    Dom    = util.Dom,
    Ev     = util.Event,
    DT     = widget.DataTable;
	
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
			mode: null,
			ns: null,
			data: null
		},

		formatBoolean: function (el, oRecord, oColumn, oData, oDataTable) {
			var oDT = oDataTable || this;
			var bChecked = (oData==='true'||oData==true) ? ' checked="checked"' : '';

			el.innerHTML = '<input type="checkbox"' + bChecked + ' class="' + YAHOO.widget.DataTable.CLASS_CHECKBOX + '" '+(this.configs.mode==='view'?'disabled="true"':'')+'/>';
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
				markup = '<input style="width:' + oColumn.width + 'px;" type="text" value="' + value + '" title="' + value + '" '+(this.configs.mode==='view'?'disabled="true"':'')+'/>';
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
			if(this.configs.mode==='view') {
				
			} else {
				if(oColumn.key==='copy') {
					var deleteLink = document.createElement('a');
					//deleteLink.id = Dom.generateId();
					YAHOO.util.Dom.addClass(deleteLink, 'copy');
					deleteLink.innerHTML = '&nbsp;';
					el.appendChild(deleteLink);
				}
				if(oColumn.key==='delete') {
					var deleteLink = document.createElement('a');
					//deleteLink.id = Dom.generateId();
					YAHOO.util.Dom.addClass(deleteLink, 'delete');
					deleteLink.innerHTML = '&nbsp;';
					el.appendChild(deleteLink);
				}
			}
		},
		
		formatDropdown : function(el, oRecord, oColumn, oData, oDataTable) {
	        var oDT = oDataTable || this,
	            selectedValue = (lang.isValue(oData)) ? oData : oRecord.getData(oColumn.field),
	            options = (lang.isArray(oColumn.dropdownOptions)) ?
	                oColumn.dropdownOptions : null,

	            selectEl,
	            collection = el.getElementsByTagName("select");

	        // Create the form element only once, so we can attach the onChange listener
	        if(collection.length === 0) {
	            // Create SELECT element
	            selectEl = document.createElement("select");
	            selectEl.className = DT.CLASS_DROPDOWN;
	            selectEl = el.appendChild(selectEl);

	            // Add event listener
	            Ev.addListener(selectEl,"change",oDT._onDropdownChange,oDT);
	        }

	        selectEl = collection[0];

	        // Update the form element
	        if(selectEl) {
	            // Clear out previous options
	            selectEl.innerHTML = "";
	            if(this.configs.mode==='view') selectEl.disabled = true;
	            // We have options to populate
	            if(options) {
	                // Create OPTION elements
	                for(var i=0; i<options.length; i++) {
	                    var option = options[i];
	                    var optionEl = document.createElement("option");
	                    optionEl.value = (lang.isValue(option.value)) ?
	                            option.value : option;
	                    // Bug 2334323: Support legacy text, support label for consistency with DropdownCellEditor
	                    optionEl.innerHTML = (lang.isValue(option.text)) ?
	                            option.text : (lang.isValue(option.label)) ? option.label : option;
	                    optionEl = selectEl.appendChild(optionEl);
	                    if (optionEl.value == selectedValue) {
	                        optionEl.selected = true;
	                    }
	                }
	            }
	            // Selected value is our only option
	            else {
	                selectEl.innerHTML = "<option selected value=\"" + selectedValue + "\">" + selectedValue + "</option>";
	            }
	        }
	        else {
	            el.innerHTML = lang.isValue(oData) ? oData : "";
	        }
	    },

		deleteRow: function(oArgs) {
			var target = oArgs.target;
			var column = this.getColumn(target);
			if (column.key == 'delete') {
				if (confirm(Alfresco.util.message('lecm.meditor.msg.approve.delete.row'))) {
					this.deleteRow(target);
					YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
				}
			} else if (column.key == 'copy') {
				var nRecordIndex = (lang.isNumber(target)) ? target : this.getRecordIndex(target);
			    if(lang.isNumber(nRecordIndex)) {
			    	var oRecord = this.getRecord(nRecordIndex);
			    	var textField = document.createElement('textarea');
			        textField.innerText = ""+this.configs.ns+":"+oRecord._oData._name;
			        document.body.appendChild(textField);
			        textField.select();
			        document.execCommand('copy');
			        textField.remove();
			        alert(""+this.configs.ns+":"+oRecord._oData._name+" скопирован в буфер");
			    }
			} else {
				//this.onEventShowCellEditor(oArgs);
			}
		},

		onReady: function () {
			this.widgets.datasource = new YAHOO.util.DataSource(this.options.data, {
				responseSchema: this.options.responseSchema
			});

			this.widgets.datatable = new YAHOO.widget.DataTable(this.id + '-datatable', this.options.columnDefinitions, this.widgets.datasource,{"mode":this.options.mode,"ns":this.options.ns});
			if(this.options.mode==='view'){} else {
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
		}
	}, true);
})();
