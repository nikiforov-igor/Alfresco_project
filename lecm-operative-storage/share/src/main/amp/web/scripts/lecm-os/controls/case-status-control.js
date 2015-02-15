if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OS = LogicECM.module.OS || {};

(function() {

	LogicECM.module.OS.StatusControl = function(htmlId) {
		LogicECM.module.OS.StatusControl.superclass.constructor.call(this, "LogicECM.module.OS.StatusControl", htmlId);
		this.controlId = htmlId;

		this.grid = Alfresco.util.ComponentManager.find({name:"LogicECM.module.Base.DataGrid_nomenclature"})[0];

		return this;
	}

	YAHOO.extend(LogicECM.module.OS.StatusControl, Alfresco.component.Base, {

		controlId: null,
		grid: null,
		options: {
			formId: null,
			fieldId: null,
			value: null
		},

		actions: [],
		buttons: [],

		updateArchiveCheckBox: function() {
			if(this.options.value != 'CLOSED') {
				LogicECM.module.Base.Util.disableControl(this.options.formId, "lecm-os:nomenclature-case-to-archive");
			}
		},

		setActions: function() {
			switch(this.options.value) {
				case 'PROJECT':
					this.actions.push({
						label: 'Открытие номенклатурного дела',
						id: 'open-case-actions'
					});
					break;
				case 'OPEN':
					this.actions.push({
						label: 'Закрытие номенклатурного дела',
						id: 'close-case-actions'
					});
					break;
				case 'CLOSED':
					this.actions.push(
						{
							label: 'Открытие номенклатурного дела',
							id: 'open-case-actions'
						},
						{
							label: 'Выделение номенклатурного дела к уничтожению',
							id: 'mark-to-destroy-case-actions'
						},
						{
							label: 'Передача номенклатурного дела в архив',
							id: 'archive-case-actions'
						}
					);
					break;
				case 'MARK_TO_DESTROY':
					this.actions.push({
						label: 'Уничтожение номенклатурного дела',
						id: 'destroy-case-actions'
					});
					break;
			}
				
			this.actions.forEach(function(action) {
				var btn = new YAHOO.widget.Button({
					label: action.label,
					id: action.id,
					container: this.controlId + "-actions-container",
					onclick: {
						fn: this.onActionsClickProxy,
						obj: {
							actionId: action.label,
							item: {nodeRef: this.options.itemId},
							dataGrid: this.grid
						}
					}
				});

				this.buttons.push(btn);

			}, this);
		},

		onActionsClickProxy: function(p_sType, p_aArgs, p_oItem) {
			var actionId = p_aArgs.actionId;
			p_aArgs.dataGrid.ActionsClickAdapter(p_aArgs.item, actionId);
		},





	});
	
})();