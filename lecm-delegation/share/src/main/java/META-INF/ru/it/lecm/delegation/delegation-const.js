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
 * @class LogicECM.module.Delegation.Const
 */
(function () {

	LogicECM.module.Delegation.Const = function (htmlId) {
		return LogicECM.module.Delegation.Const.superclass.constructor.call(this, "LogicECM.module.Delegation.Const", htmlId, []);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Const, Alfresco.component.Base, {

		self: null,

		onReady: function () {
			self = this;
			Alfresco.logger.info ("A new LogicECM.module.Delegation.Const has been created");

			LogicECM.module.Delegation.Config = LogicECM.module.Delegation.Config || {};

			LogicECM.module.Delegation.Config.ON_AJAX_SUCCESS = "onAjaxSuccessEvent";
			LogicECM.module.Delegation.Config.ON_AJAX_FAILURE = "onAjaxFailureEvent";
		}
	});

	new LogicECM.module.Delegation.Const("bd");
})();
