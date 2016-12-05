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
				LogicECM.module.Base.Util.readonlyControl(this.options.formId, "lecm-os:nomenclature-case-to-archive", true);
			}
		},
		prepare: function() {
			this.updateActions.call(this.grid, this);
		},
		updateActions: function (srcContext) {

			if(srcContext.buttons) {
				srcContext.buttons.forEach(function(el) {
					el.destroy();
				});
			}

			srcContext.buttons = [];

			if (this.options.actions != null) {

				var oData;
				this.widgets.dataTable.getRecordSet().getRecords().some(function (el)
				{
					if (srcContext.options.itemId == el.getData().nodeRef) {
						oData = el.getData();
						return true;
					}
				}, this);

				for (var i = 0; i < this.options.actions.length; i++) {
					var showAction = true; // по умолчанию - показывать
					var action = this.options.actions[i];
					var actionId = action.id;

					if (srcContext.options.excludeActions.indexOf(actionId) < 0) {

						var evaluator = action.evaluator;
						if (evaluator != null && typeof evaluator == "function") {
							showAction = evaluator.call(this, oData);
						}

						var fakeOwner = {};
						fakeOwner.className = actionId;

						if (showAction) {
							var fnActionHandler = function DataGrid_fnActionHandler(event, obj) {
								var fakeOwner = obj.fakeOwner;
								var oData = obj.oData;
								var grid = obj.grid;
								var control = obj.srcContex;
								if (typeof grid[fakeOwner.className] == "function") {
									var confirmFunction = obj.confirmFn;
									grid[fakeOwner.className].call(grid, oData, fakeOwner, grid.datagridMeta.actionsConfig, confirmFunction);
								}
								grid.afterDataGridUpdate.push(control.updateActions.bind(grid, control));
								return true;
							};

							var btn = new YAHOO.widget.Button({
								label: action.label,
								id: action.id,
								container: srcContext.controlId + "-actions-container",
								onclick: {
									fn: fnActionHandler,
									obj: {
										confirmFn: action.confirmFunction,
										grid: this,
										srcContex: srcContext,
										fakeOwner: fakeOwner,
										oData: oData
									}
								}
							});

							srcContext.buttons.push(btn);

						}
					}

				}
			}

		}

	});

})();
