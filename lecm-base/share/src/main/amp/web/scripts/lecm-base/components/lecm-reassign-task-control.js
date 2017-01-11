/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.module.ReassignTaskControl = function (fieldHtmlId)
	{
		LogicECM.module.ReassignTaskControl.superclass.constructor.call(this, "LogicECM.module.ReassignTaskControl", fieldHtmlId, [ "container", "datasource"]);
		return this;
	};

	YAHOO.extend(LogicECM.module.ReassignTaskControl, Alfresco.component.Base,
		{
			options: {
				taskId: null,
				reassignReload: false
			},

			onReady: function() {
				this.widgets.showDialogButton = Alfresco.util.createYUIButton(this, "reassign-task-btn", this.onShowReassignDialog);
			},

			onShowReassignDialog: function() {
				if (this.options.taskId != null) {
					var me = this;
					new Alfresco.module.SimpleDialog("reassign-task-form" + Alfresco.util.generateDomId()).setOptions({
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
							taskIds: this.options.taskId,
							showCaption: false
						},
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							fn: function (p_form, p_dialog) {
								var contId = p_dialog.id + "-form-container";
								var dialogName = me.msg("title.reassignTask");
								Alfresco.util.populateHTML(
									[contId + "_h", dialogName]
								);

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);

								var cancelButton = Dom.get( p_dialog.id + "-form-cancel");
								Event.removeListener(cancelButton, "click");
								Event.addListener(cancelButton, "click", this.hide, this, true);
							}
						},
						onSuccess: {
							fn: function (response) {
								if (me.options.reassignReload) {
									document.location.reload();
								} else {
									document.location.href = document.referrer;
								}
							},
							scope: this
						}
					}).show();
				}
			}
		});
})();