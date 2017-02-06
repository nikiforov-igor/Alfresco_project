/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module.ReportsEditor
 * @class LogicECM.module.ReportsEditor.TemplateEditGrid
 */
(function () {
	LogicECM.module.ReportsEditor.TemplateEditGrid = function(containerId) {
		LogicECM.module.ReportsEditor.TemplateEditGrid.superclass.constructor.call(this, containerId);
		
		return this;
	};
	
	YAHOO.lang.extend(LogicECM.module.ReportsEditor.TemplateEditGrid, LogicECM.module.Base.DataGrid);
	
	YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.TemplateEditGrid.prototype, {
		doubleClickLock: false,
		onActionSave: function(item) {
			if (this.doubleClickLock) {
				return;
			}
			
			this.doubleClickLock = true;
			
			var datagrid = this;
			var meta = {
				itemType: item.nodeRef,
				nodeRef: this.datagridMeta.nodeRef,
				formMode: "edit",
				itemKind: "node",
				formId: "copy-to-dictionaries"
			};
			
			var items = [];
			
			var doBeforeDialogShow = function(p_form, p_dialog) {
				var defaultMsg = datagrid.msg("label.create-template.title");
				Alfresco.util.populateHTML(
						[p_dialog.id + "-form-container_h", defaultMsg]
						);
				
				Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
				
				items = p_dialog.form.validations;
				
				datagrid.doubleClickLock = false;
			};
			
			var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
					{
						itemKind: "node",
						itemId: meta.itemType,
						mode: "edit",
						submitType: "json",
						formId: "copy-to-dictionaries"
					});
			
			var postfix = Alfresco.util.generateDomId();
			var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails-" + postfix);
			createDetails.setOptions(
					{
						width: "50em",
						templateUrl: templateUrl,
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							fn: doBeforeDialogShow,
							scope: this
						},
						doBeforeFormSubmit: {
							fn: function InstantAbsence_doBeforeSubmit() {
								var form = Dom.get(this.id + "-createDetails-" + postfix + "-form");
								form.setAttribute("action", Alfresco.constants.PROXY_URI_RELATIVE + "api/type/lecm-rpeditor%3areportTemplate/formprocessor");
								
								var input = document.createElement('input');
								input.setAttribute("id", this.id + "-createDetails-form-destination");
								input.setAttribute("type", "hidden");
								input.setAttribute("name", "alf_destination");
								input.setAttribute("value", LogicECM.module.ReportsEditor.SETTINGS.templatesContainer);
								form.appendChild(input);
								
								for (var index in items) {
									var htmlItem = Dom.get(items[index].fieldId + "-added");
									if (htmlItem == null) {
										htmlItem = Dom.get(items[index].fieldId + "-cntrl-added");
									}
									var value = Dom.get(items[index].fieldId).value;
									if (htmlItem) {
										htmlItem.setAttribute("value", value);
									}
								}
							},
							scope: this
						},
						onSuccess: {
							fn: function DataGrid_onActionCreate_success(response) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.copy.success")
										});
								this.doubleClickLock = false;
							},
							scope: this
						},
						onFailure: {
							fn: function DataGrid_onActionCreate_failure(response) {
								alert(response.serverResponse.responseText);
								Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.copy.failure")
										});
								this.doubleClickLock = false;
							},
							scope: this
						}
					}).show();
		},
		onActionExport: function(item) {
			document.location.href = Alfresco.constants.PROXY_URI + "/lecm/reports-editor/exportReportTemplate?templateRef=" + item.nodeRef;
		}
	}, true);
})();