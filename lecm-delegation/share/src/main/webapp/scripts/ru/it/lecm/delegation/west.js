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
 * @class LogicECM.module.Delegation.West
 */ (function() {

	LogicECM.module.Delegation.West = function(htmlId) {
		return LogicECM.module.Delegation.West.superclass.constructor.call(
		this, "LogicECM.module.Delegation.West", htmlId, ["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.West, Alfresco.component.Base, {

		scope: null,

		onReady: function() {
			scope = this;

			Alfresco.logger.info("A new LogicECM.module.Delegation.West has been created");
			scope.initListeners();
			YAHOO.util.Dom.setStyle (scope.id + "-body", "visibility", "visible");
		},

		initListeners: function() {

			var container = YAHOO.util.Dom.get(scope.id);
			Alfresco.util.createYUIButton(container, "myButton", function(event) {
				Alfresco.logger.info(event.toString());
				Alfresco.util.Ajax.jsonGet({
					method: "GET",// GET, POST, PUT or DELETE, default is GET
					url: Alfresco.constants.PROXY_URI + "logicecm/generateTestUnit",// the url to send the request to, mandatory
					dataObj: {dummy: new Date().getTime()},
					//successMessage: "success happend!", // Will be displayed using Alfresco.util.PopupManager.displayMessage if successCallback isn't provided
					//failureMessage: "shit happend!", // Will be displayed by Alfresco.util.displayPrompt if no failureCallback isn't provided
					successCallback: {fn: function(successResult) { // Callback for successful request, should have the following form: {fn: successHandler, scope: scopeForSuccessHandler}
							Alfresco.logger.info("get responce " + successResult.json.nodeRef);
							YAHOO.Bubbling.fire(LogicECM.module.Delegation.Const.ON_AJAX_SUCCESS, "success happend!");
							YAHOO.Bubbling.fire("activeDataListChanged", {
								dataList: {itemType: "lecm-dlg:test-unit"},
								scrollTo: true
							});
						},scope: scope},
					failureCallback: {fn: function(failureResult) { // Callback for failed request, should have the following form: {fn: failureHandler, scope: scopeForFailureHandler}
							debugger;
							YAHOO.Bubbling.fire(LogicECM.module.Delegation.Const.ON_AJAX_FAILURE, "shit happend!");
						},scope: scope}
				});
			}, {label: "кнопка label", name: "кнопка name", title: "кнопка title"});
		}
	});
})();