if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Deputy = LogicECM.module.Deputy || {};


(function () {
	"use strict";
	LogicECM.module.Deputy.RecomendGrid = function (containerId) {
		var grid = LogicECM.module.Deputy.RecomendGrid.superclass.constructor.call(this, containerId);
		YAHOO.Bubbling.on("disableControl", grid.onDisableControl, grid);
	    YAHOO.Bubbling.on("enableControl", grid.onEnableControl, grid);
		return grid;
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.Deputy.RecomendGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.Deputy.RecomendGrid.prototype, {
		onDisableControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				this.cancelSendToDeputy();
				var disabler = document.getElementById(this.id + '-grid-disable-overlay');
				disabler.classList.remove('hidden');
			}
		},
		onEnableControl: function (layer, args) {
			if (this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
				var disabler = document.getElementById(this.id + '-grid-disable-overlay');
				disabler.classList.add('hidden');
			}
		},
		onDataGridColumns: function (response) {
			LogicECM.module.Deputy.RecomendGrid.superclass.onDataGridColumns.call(this, response);
			LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
		},
		draw: function() {
			if(!this.options.useCurrentUser) {
				YAHOO.Bubbling.on(this.options.targetEvent, this.onEmployeeSelected.bind(this));
			}

			document.getElementById(this.id).onclick = this.onCheck.bind(this);
			document.getElementById(this.id + '-cancel-link').onclick = this.cancelSendToDeputy.bind(this);

			if(this.options.targetField) {
				this.options.targetControlId = this.options.formId + '_' + this.options.targetField + '-cntrl';
				this.options.targetFieldFormInputId = this.options.formId + '_' + this.options.targetField;
			}

			this.datagridMeta = this.options.datagridMeta;
			this.deferredListPopulation.fulfil("onGridTypeChanged");

			this.onReady();
		},
		onEmployeeSelected: function(layer, args) {
			var obj = args[1];
			var nodeRef;
			if(obj.selectedItems) {
				for (var prop in obj.selectedItems) {
					nodeRef = prop;
				}
			}

			if(!nodeRef) {
				document.getElementById(this.id).classList.add('hidden');
				return;
			}
			this.options.datagridMeta.nodeRef = nodeRef;
			this.populateDataGrid();

		},
		getCustomCellFormatter: function (grid, elCell, oRecord, oColumn, oData) {

			if(grid.totalRecords == 0) {
				return;
			}

			document.getElementById(grid.id).classList.remove('hidden');

			var html = "";
			var columnContent = "";
            if (!oRecord) {
                oRecord = this.getRecord(elCell);
            }

            if (!oColumn) {
                oColumn = this.getColumn(elCell.parentNode.cellIndex);
            }

			if(oColumn.field == 'prop_checkboxtable') {
				var employeeName = oRecord. getData('itemData')["prop_lecm-orgstr_employee-short-name"].value;
				var columnContent = "<div class='hover-select'><div class='centered'><input type='radio' name='selectEmployee' node='" + oRecord._oData.nodeRef + "' empname='" + employeeName + "'/><span>Направить заместителю</span></div></div>"
			}

			if (columnContent != "") {
				html += columnContent;
			}

			var highlitable = oRecord.getData('itemData')['highlightable'];
			if(highlitable && highlitable.value) {
				elCell.parentElement.parentElement.classList.add('highlight');
			}

			return html.length > 0 ? html : null;
		},
		cancelSendToDeputy: function(ev) {
			var messageContainer = document.getElementById('send-to-employee-message');
			var radios = document.getElementsByName('selectEmployee');

			for(var i = 0; i < radios.length; i++) {
				var btn = radios[i];
				btn.checked = false;
				btn.parentElement.parentElement.classList.add('hover-select');
			}

			messageContainer.classList.add('hidden');

			if(this.options.sourceAddresse) {
				var hiddenValueDiv = document.getElementsByName(this.options.targetField)[0];
				var hiddenValueAddedDiv = document.getElementsByName(this.options.targetField + '_added')[0];

				hiddenValueDiv.value = this.options.sourceAddresse;
				hiddenValueAddedDiv.value = this.options.sourceAddresse;
			}


		},
		onCheck: function(ev) {

			var radioButton = ev.target;
			var buttonContainer = radioButton.parentElement.parentElement;

			if(radioButton.name != 'selectEmployee') {
				return;
			}

			var message = document.getElementById('employeeName');
			var messageContainer = document.getElementById('send-to-employee-message');

			function showMessage(text) {
				messageContainer.classList.remove('hidden');
				message.textContent = ' ' + text + ' ';
			}

			function hideMessage() {
				messageContainer.classList.add('hidden');
			}

			if(!buttonContainer.classList.contains('hover-select')) {
				radioButton.checked = false;
				hideMessage();
				buttonContainer.classList.add('hover-select');
				return;
			}


			var radios = document.getElementsByName('selectEmployee');

			for(var i = 0; i < radios.length; i++) {
				var btn = radios[i];
				btn.checked = false;
				btn.parentElement.parentElement.classList.add('hover-select');
			}

			buttonContainer.classList.remove('hover-select');
			radioButton.checked = true;
			var nodeRef = radioButton.attributes.node;

			var inputElement = document.getElementById(this.options.targetFieldFormInputId);
			var hiddenValueAddedDiv = document.getElementById(this.options.targetControlId + '-added');

			if(!this.options.sourceAddresse) {
				this.options.sourceAddresse = hiddenValueAddedDiv.value;
			}


			var employeeName = radioButton.attributes.empname.value;
			showMessage(employeeName);

			inputElement.disabled = false;
			inputElement.value = nodeRef.value;
			hiddenValueAddedDiv.disabled = false;
			hiddenValueAddedDiv.value = nodeRef.value;
		}
	}, true);
})();
