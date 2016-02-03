/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
LogicECM.module.OrgStructure.OrganizationsList = LogicECM.module.OrgStructure.OrganizationsList || {};

(function () {
	var Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom;

	LogicECM.module.OrgStructure.OrganizationsList.Toolbar = function (containerId, options, messages) {
		LogicECM.module.OrgStructure.OrganizationsList.Toolbar.superclass.constructor.call(this, 'LogicECM.module.OrgStructure.OrganizationsList', containerId);
		this.setMessages(messages);
		this.setOptions(options);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.OrgStructure.OrganizationsList.Toolbar, LogicECM.module.Base.Toolbar, {
		_initButtons: function () {
			this.toolbarButtons.defaultActive = Alfresco.util.createYUIButton(this, 'newRowButton', this.onNewRow);
			this.toolbarButtons.defaultActive.searchButton = Alfresco.util.createYUIButton(this, 'searchButton', this.onSearchClick);
			this.toolbarButtons.defaultActive.exSearchButton = Alfresco.util.createYUIButton(this, 'extendSearchButton', this.onExSearchClick);
		},
		onNewRow: function (e, p_obj) {
			var dataGrid = this.modules.dataGrid;
			if (dataGrid && dataGrid.datagridMeta && dataGrid.datagridMeta.nodeRef.indexOf(":") > 0) {
				dataGrid.showCreateDialog(dataGrid.datagridMeta);
			}
		},
	}, true);
})();
