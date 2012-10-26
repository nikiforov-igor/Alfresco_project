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
 */
(function () {

	LogicECM.module.Delegation.West = function (htmlId) {
		return LogicECM.module.Delegation.West.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.West",
			htmlId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.West, Alfresco.component.Base, {

		self: null,

		onReady: function () {
			self = this;

			this.initListeners("buttons");
			Alfresco.logger.info ("A new LogicECM.module.Delegation.West has been created");
		},

		initListeners: function (buttonId) {

			YAHOO.util.Event.onContentReady(buttonId, function () {

				var buttonContainer = YAHOO.util.Dom.get (buttonId);
				Alfresco.logger.info ("button container: " + buttonContainer);

				Alfresco.util.createYUIButton (buttonContainer, "myButton", function (event) {
					Alfresco.logger.info (event.toString());
					Alfresco.util.Ajax.jsonGet ({
						method: "GET", // GET, POST, PUT or DELETE, default is GET
						url: Alfresco.constants.PROXY_URI + "logicecm/generateTestUnit", // the url to send the request to, mandatory
						dataObj: {dummy: new Date().getTime()},
//						successMessage: "success happend!", // Will be displayed using Alfresco.util.PopupManager.displayMessage if successCallback isn't provided
//						failureMessage: "shit happend!", // Will be displayed by Alfresco.util.displayPrompt if no failureCallback isn't provided
						successCallback: {fn: function (successResult){  // Callback for successful request, should have the following form: {fn: successHandler, scope: scopeForSuccessHandler}
							Alfresco.logger.info ("get responce " + successResult.json.nodeRef);
							YAHOO.Bubbling.fire (LogicECM.module.Delegation.Config.ON_AJAX_SUCCESS, "success happend!");
						}, scope: self},
						failureCallback: {fn: function (failureResult) {
							debugger;
							YAHOO.Bubbling.fire (LogicECM.module.Delegation.Config.ON_AJAX_FAILURE, "shit happend!");
						}, scope: self} // Callback for failed request, should have the following form: {fn: failureHandler, scope: scopeForFailureHandler}
					});
				}, {label: "кнопка label", name: "кнопка name", title: "кнопка title"});
			});

//			function onButtonsReady() {
//
//				var onButtonClick = function (e) {
//					var sUrl = Alfresco.constants.PROXY_URI + "logicecm/generateTestUnit";
//					var callback = {
//						success:function (oResponse) {
//							var oResults = Alfresco.util.parseJSON (oResponse.responseText, true);
//							Alfresco.logger.info ("get responce " + oResults.nodeRef);
//						},
//						failure:function (oResponse) {
//							Alfresco.logger.info ("Failed to get responce. " + "[" + oResponse.statusText + "]");
//						}
//					};
//					YAHOO.util.Connect.asyncRequest("GET", sUrl, callback);
//				};
//
//				for (var i = 0; i < buttons.length; i++) {
//					var button = new YAHOO.widget.Button(buttons[i]);
//					button.on("click", onButtonClick);
//				}
//			}
		}
	});
})();
