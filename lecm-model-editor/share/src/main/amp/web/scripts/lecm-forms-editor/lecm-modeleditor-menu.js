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
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

/**
 * AllDictionary module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.AllDictionary
 */
(function () {

	LogicECM.module.ModelEditor.Menu = function (htmlId) {
		return LogicECM.module.ModelEditor.Menu.superclass.constructor.call(
			this,
			"LogicECM.module.AllDictionary.Menu",
			htmlId,
			["button"]);
	};

	YAHOO.extend(LogicECM.module.ModelEditor.Menu, Alfresco.component.Base, {
		onReady:function Menu_onReady () {
			var onHomeClick = function (e) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + "doc-model-list";
			};
			this.widgets.dictionariesButton = Alfresco.util.createYUIButton(this, "modelEditorHomeBtn", onHomeClick, {});
		}
	});
})();
