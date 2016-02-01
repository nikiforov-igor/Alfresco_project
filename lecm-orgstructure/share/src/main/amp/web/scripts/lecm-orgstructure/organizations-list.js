/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

(function() {
	var Bubbling = YAHOO.Bubbling,
	Dom = YAHOO.util.Dom;

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
	}, true);
})();
