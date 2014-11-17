/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
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
 * @class LogicECM.module.AllDictionary.AllDictionary
 */
LogicECM.module.AllDictionary = LogicECM.module.AllDictionary || {};

/**
 * AllDictionary module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.AllDictionary
 */
(function () {

	LogicECM.module.AllDictionary.Menu = function (htmlId) {
		return LogicECM.module.AllDictionary.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.AllDictionary.Menu",
			htmlId,
			["button"]);
	};

	YAHOO.extend(LogicECM.module.AllDictionary.Menu, Alfresco.component.Base, {
		onReady:function Menu_onReady () {
			var onDictionariesClick = function (e) {
				window.location.href = window.location.protocol + "//" + window.location.host +
					Alfresco.constants.URL_PAGECONTEXT + "allDictionary";
			};
			this.widgets.dictionariesButton = Alfresco.util.createYUIButton(this, "dictionariesBtn", onDictionariesClick, {
                disabled: true
            });
		}
	});
})();
