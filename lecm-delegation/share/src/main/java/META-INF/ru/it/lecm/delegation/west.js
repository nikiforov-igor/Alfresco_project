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

		logger:null,

		init: function () {
			alert("invoke init");

			this.logger = new YAHOO.widget.LogWriter(this.toString());

			// Now you can write log messages from your LogWriter
			this.logger.log("A new MyClass has been created","info");

		},

		initListeners: function (htmlId, buttons) {

			buttons = buttons || [];

			var context = this;

			function onButtonsReady() {

				var onButtonClick = function (e) {
					var sUrl = Alfresco.constants.PROXY_URI + "logicecm/generateTestUnit";
					var callback = {
						success:function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
//							alert ("get responce " + oResults.nodeRef);
							context.logger.log ("get responce " + oResults.nodeRef, "info", "west.js");
						},
						failure:function (oResponse) {
//							alert ("Failed to get responce. " + "[" + oResponse.statusText + "]");
							context.logger.log ("Failed to get responce. " + "[" + oResponse.statusText + "]", "error", "west.js");
						}
					};
					YAHOO.util.Connect.asyncRequest("GET", sUrl, callback);
				};

				for (var i = 0; i < buttons.length; i++) {
					var button = new YAHOO.widget.Button(buttons[i]);
					button.on("click", onButtonClick);
				}
			}

			YAHOO.util.Event.onContentReady(htmlId, onButtonsReady);
		}
	});
})();
