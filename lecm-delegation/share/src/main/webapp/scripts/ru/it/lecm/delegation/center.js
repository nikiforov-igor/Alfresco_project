/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Delegation module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Delegation
 */
LogicECM.module.Delegation = LogicECM.module.Delegation || {};

/**
 * Delegation module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Delegation.Center
 */
(function () {

	LogicECM.module.Delegation.Center = function (containerId) {
		return LogicECM.module.Delegation.Center.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Center",
			containerId,
			["button", "container", "connection", "json", "selector", "datasource", "datatable"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Center, Alfresco.component.Base, {

		scope:null,

		_onAjaxSuccess: function (event, eventData) {
			var object = eventData[1];
			Alfresco.util.PopupManager.displayMessage({text: object});
		},

		_onAjaxFailure: function (event, eventData) {
			var object = eventData[1];
			Alfresco.util.PopupManager.displayMessage({text: object});
		},

		onReady: function () {
			scope = this;

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Center has been created");

			YAHOO.Bubbling.on (LogicECM.module.Delegation.Const.ON_AJAX_SUCCESS, scope._onAjaxSuccess, scope);
			YAHOO.Bubbling.on (LogicECM.module.Delegation.Const.ON_AJAX_FAILURE, scope._onAjaxFailure, scope);

			var dsLocalJSON = new YAHOO.util.LocalDataSource({
				found: 3,
				total: 20,
				results: [
					{name: "apples", type:"fruit", color: "red"},
					{name: "broccoli", type:"veg", color: "green"},
					{name: "cherries", type:"fruit", color: "red"}
				]
			});

/*
* @param c.dataTable                        {Object} (Required)
* @param c.dataTable.container              {String|HTMLElement} (Required) 1st argument in YAHOO.widget.DataTable constructor
* @param c.dataTable.columnDefinitions      {Array}              (Required) 2nd argument in YAHOO.widget.DataTable constructor
* @param c.dataTable.config                 {Object}  (Optional)
* @param c.dataTable.config.dynamicData     {boolean} (Optional) Default: true,
* @param c.dataTable.config.initialLoad     {boolean} (Optional) Default: false
* @param c.dataTable.config.MSG_EMPTY       {String}  (Optional) Default: Alfresco.util.message("message.datatable.empty")
* @param c.dataTable.config.MSG_ERROR       {String}  (Optional) Default: Alfresco.util.message("message.datatable.error")
* @param c.dataTable.config.MSG_LOADING     {String}  (Optional) Default: Alfresco.util.message("message.datatable.loading")
* @param c.dataTable.config.className       {String}  (Optional) Default: "alfresco-datatable" Default class will hide column headers and cell borders
*/
			var dataTableConfig = {
				dataSource: {
					url: Alfresco.constants.PROXY_URI + "logicecm/generateTestUnit",
					config: {}
				},
				dataTable: {
					container: "dataTableContainer",
					columnDefinitions: [
						{key: "id"},
						{key:"title"},
						{key:"name"},
						{key:"date"}
					]
				}
			};

			new Alfresco.util.DataTable (dataTableConfig);

			YAHOO.util.Dom.setStyle (scope.id + "-body", "visibility", "visible");
		}
	});
})();
