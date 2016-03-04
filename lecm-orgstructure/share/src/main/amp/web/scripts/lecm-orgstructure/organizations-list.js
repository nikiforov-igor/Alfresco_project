/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

(function() {
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.OrgStructure.OrganizationsList = function(containerId, options, datagridMeta, messages) {
		LogicECM.module.OrgStructure.OrganizationsList.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.OrgStructure.OrganizationsList';
		this.setMessages(messages);
		this.setOptions(options);
		this.datagridMeta = datagridMeta;
		Bubbling.on('initDatagrid', this._initOrganizationsListDatagrid, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.OrgStructure.OrganizationsList, LogicECM.module.Base.DataGrid, {
		_initOrganizationsListDatagrid: function(layer, args) {
			if (this.options.bubblingLabel == args[1].datagrid.options.bubblingLabel) {
				Bubbling.unsubscribe(layer, this._initOrganizationsListDatagrid);
				Bubbling.fire('activeGridChanged', {
					bubblingLabel: this.options.bubblingLabel,
					datagridMeta: this.datagridMeta
				});
			}
		},
		onActionDelete: function (p_items, owner, actionsConfig, fnDeleteComplete) {
			var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

			for (var i = 0; i < items.length; i++) {
				Alfresco.util.Ajax.jsonGet(
					{
						url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/deletevalidation",
						dataObj: {
							nodeRef: items[i].nodeRef
						},
						successCallback: {
							fn: function (response) {
								if (response.json.containsOrgUnits) {
									Alfresco.util.PopupManager.displayMessage({
										text:this.msg("message.delete.unit.failure.has.children")
									});
								} else if (response.json.containsStaffLists) {
									Alfresco.util.PopupManager.displayMessage({
										text:this.msg("message.delete.unit.failure.has.composition")
									});
								} else {
									this.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
								}
							},
							scope: this
						},
						failureMessage: "message.failure"
					}
				);
			}



		}
	}, true);
})();
