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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.Dictionary = LogicECM.module.Dictionary || {};

/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

	LogicECM.module.Dictionary.Menu = function (htmlId) {
		return LogicECM.module.Dictionary.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.Dictionary.Menu",
			htmlId,
			["button"]);
	};

	YAHOO.extend(LogicECM.module.Dictionary.Menu, Alfresco.component.Base, {
		onReady:function Menu_onReady () {
			var onDictionariesClick = function (e) {
				window.location.href = window.location.protocol + "//" + window.location.host +
					Alfresco.constants.URL_PAGECONTEXT + "allDictionary";
			};
			this.widgets.dictionariesButton = Alfresco.util.createYUIButton(this, "dictionariesBtn", onDictionariesClick, {});
		}
	});
})();
