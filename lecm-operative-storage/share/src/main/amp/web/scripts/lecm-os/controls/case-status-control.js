if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OS = LogicECM.module.OS || {};

(function () {

	LogicECM.module.OS.StatusControl = function (htmlId) {
		LogicECM.module.OS.StatusControl.superclass.constructor.call(this, "LogicECM.module.OS.StatusControl", htmlId);
		this.controlId = htmlId;

		this.grid = Alfresco.util.ComponentManager.find({name: "LogicECM.module.Base.DataGrid_nomenclature"})[0];
		YAHOO.Bubbling.on('datagridVisible', this.updateActions.bind(this));
		return this;
	}

	YAHOO.extend(LogicECM.module.OS.StatusControl, Alfresco.component.Base, {
		controlId: null,
		grid: null,
		options: {
			formId: null,
			fieldId: null,
			value: null,
			excludeActions: []
		},
		actions: [],
		buttons: [],
		updateArchiveCheckBox: function () {
			if (this.options.value != 'CLOSED') {
				LogicECM.module.Base.Util.disableControl(this.options.formId, "lecm-os:nomenclature-case-to-archive");
			}
		},
		updateActions: function (layer, args) {

			if(args) {
				var gridId = args[1].id;
				if(gridId != this.grid.id) {
					return;
				}
			}

			if(this.buttons) {
				this.buttons.forEach(function(el) {
					el.destroy();
				});
			}

			this.buttons = [];

			if (this.grid.options.actions != null) {

				var oData;
				this.grid.widgets.dataTable.getRecordSet().getRecords().some(function (el)
				{
					if (this.options.itemId == el.getData().nodeRef) {
						oData = el.getData();
						return true;
					}
				}, this);

				for (var i = 0; i < this.grid.options.actions.length; i++) {
					var showAction = true; // по умолчанию - показывать
					var action = this.grid.options.actions[i];
					var actionId = action.id;

					if (this.options.excludeActions.indexOf(actionId) < 0) {

						var evaluator = action.evaluator;
						if (evaluator != null && typeof evaluator == "function") {
							showAction = evaluator.call(this.grid, oData);
						}

						var fakeOwner = {};
						fakeOwner.className = actionId;

						if (showAction) {
							var fnActionHandler = function DataGrid_fnActionHandler(event, obj) {
								var fakeOwner = obj.fakeOwner;
								var oData = obj.oData;
								var grid = obj.grid;
								if (typeof grid[fakeOwner.className] == "function") {
									var confirmFunction = obj.confirmFn;
									grid[fakeOwner.className].call(grid, oData, fakeOwner, grid.datagridMeta.actionsConfig, confirmFunction);
								}
								return true;
							};

							var btn = new YAHOO.widget.Button({
								label: action.label,
								id: action.id,
								container: this.controlId + "-actions-container",
								onclick: {
									fn: fnActionHandler,
									obj: {
										confirmFn: action.confirmFunction,
										grid: this.grid,
										fakeOwner: fakeOwner,
										oData: oData
									}
								}
							});

							this.buttons.push(btn);

						}
					}

				}
			}

		}

	});

})();