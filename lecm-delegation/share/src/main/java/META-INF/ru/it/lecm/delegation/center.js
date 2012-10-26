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

	LogicECM.module.Delegation.Center = function (htmlId) {
		return LogicECM.module.Delegation.Center.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Center",
			htmlId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Center, Alfresco.component.Base, {

		self:null,

		onReady: function () {
			self = this;
			
			Alfresco.logger.info ("A new LogicECM.module.Delegation.Center has been created");
		}
	});
})();
