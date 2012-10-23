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
 * @class LogicECM.module.Delegation.View
 */
(function () {

	LogicECM.module.Delegation.View = function (htmlId) {
		return LogicECM.module.Delegation.View.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.View",
			htmlId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.View, Alfresco.component.Base, {

		init: function () {
			alert("invoke init");
		},

		initListeners: function (htmlId, buttons) {

			buttons = buttons || [];

			function onButtonsReady() {

				for (var i = 0; i < buttons.length; i++) {
					var button = new YAHOO.widget.Button(buttons[i]);
					var onButtonClick = function (e) {
						alert(e.target);
					};

					button.on("click", onButtonClick);
				}
			};

			YAHOO.util.Event.onContentReady(htmlId, onButtonsReady);
		}
	});
})();
