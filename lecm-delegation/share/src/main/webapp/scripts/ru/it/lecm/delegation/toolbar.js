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
 * @class LogicECM.module.Delegation.Toolbar
 */
(function () {

	LogicECM.module.Delegation.Toolbar = function (containerId) {
		return LogicECM.module.Delegation.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Toolbar",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Toolbar, Alfresco.component.Base, {

		self:null,

		_createProcuracyBtnClick: function (event) {
			Alfresco.util.PopupManager.displayMessage({text: "createProcuracyBtnClick"});
		},

		_listProcuraciesBtnClick: function (event) {
			Alfresco.util.PopupManager.displayMessage({text: "listProcuraciesBtnClick"});
		},

		_onToolbarReady: function () {
			var container = YAHOO.util.Dom.get(self.id);
			Alfresco.util.createYUIButton(container, "btnCreateProcuracy", self._createProcuracyBtnClick, {label: "создать доверенность"});
			Alfresco.util.createYUIButton(container, "btnListProcuracies", self._listProcuraciesBtnClick, {label: "список доверенностей"});
		},

		onReady: function () {
			self = this;

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Toolbar has been created");
			self._onToolbarReady ();
//			YAHOO.util.Event.onContentReady(self.id, self._onToolbarReady);
			YAHOO.util.Dom.setStyle (self.id + "-body", "visibility", "visible");
		}
	});
})();
