if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function () {

	LogicECM.TaskDistributionToolbar = function (containerId) {
		LogicECM.TaskDistributionToolbar.superclass.constructor.call(this, "LogicECM.TaskDistributionToolbar", containerId, ["button", "container", "connection", "json", "selector"]);
		YAHOO.Bubbling.on("selectedTasksChanged", this.onSelectedTasksChanged, this);
		this.selectedTasks = [];
		return this;
	};

	YAHOO.lang.extend(LogicECM.TaskDistributionToolbar, Alfresco.component.Base, {

		selectedTasks: null,

		onReady: function Absence_onReady() {
			this.widgets.reassignAllTasks = Alfresco.util.createYUIButton(this, "btnReassignAllTasks", this._reassignAllTasks, {
				label: this.msg("button.reassign-all-tasks"),
				disabled: true
			});
		},

		_reassignAllTasks: function () {
			var me = this;
			new Alfresco.module.SimpleDialog("reassign-tasks-form" + Alfresco.util.generateDomId()).setOptions({
				width: "50em",
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
				templateRequestParams: {
					submissionUrl: "/lecm/base/action/reassign-tasks",
					itemKind: "type",
					itemId: "bpm:startTask",
					formId: "reassignTask",
					mode: "create",
					submitType: "json",
					showCancelButton: true,
					taskIds: this.selectedTasks.toString(),
					showCaption: false
				},
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (p_form, p_dialog) {
						var contId = p_dialog.id + "-form-container";
						var dialogName = me.msg("title.reassignTasks");
						Alfresco.util.populateHTML(
							[contId + "_h", dialogName]
						);

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				onSuccess: {
					fn: function (response) {
						window.location.reload();
					},
					scope: this
				}
			}).show();
		},

		onSelectedTasksChanged: function(e, args) {
			var selectedTasks = [];
			if (args[1] != null && args[1].selectedTasks != null) {
				selectedTasks = args[1].selectedTasks;
			}
			this.widgets.reassignAllTasks.set("disabled", selectedTasks.length == 0);
			this.selectedTasks = selectedTasks;
		}
	});
})();
