if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Deputy = LogicECM.module.Deputy || {};


(function () {
	"use strict";
	LogicECM.module.Deputy.Grid = function (containerId) {
		return LogicECM.module.Deputy.Grid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.Deputy.Grid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.Deputy.Grid.prototype, {
		onActionEdit: function (item) {
			if (this.editDialogOpening) {
				return;
			}
			this.editDialogOpening = true;
			var me = this;

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: 'node',
				itemId: item.nodeRef,
				mode: 'edit',
				formId: 'employee-deputy-edit',
				submitType: 'json',
				showCancelButton: true,
				showCaption: false
			};

			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions(
					{
						width: this.options.editFormWidth,
						templateUrl: templateUrl,
						templateRequestParams: templateRequestParams,
						destroyOnHide: true,
						actionUrl: Alfresco.constants.PROXY_URI + "lecm/deputyFull/" + Alfresco.util.NodeRef(item.nodeRef).uri + "/edit",
						nodeRef: item.nodeRef,
						doBeforeDialogShow: {
							fn: function (p_form, p_dialog) {
								var contId = p_dialog.id + "-form-container";
								if (item.type && item.type != "") {
									Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
								}
								p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
								this.editDialogOpening = false;

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							},
							scope: this
						},
						onSuccess: {
							fn: function DataGrid_onActionEdit_success(response) {
								// Reload the node's metadata
								YAHOO.Bubbling.fire("datagridRefresh",
										{
											bubblingLabel: me.options.bubblingLabel
										});
								Alfresco.util.PopupManager.displayMessage({
									text: this.msg("message.details.success")
								});
								this.editDialogOpening = false;
							},
							scope: this
						},
						onFailure: {
							fn: function DataGrid_onActionEdit_failure(response) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.details.failure")
										});
								this.editDialogOpening = false;
							},
							scope: this
						}
					}).show();
		}
	}, true);
})();
