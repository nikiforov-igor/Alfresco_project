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
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};


(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.Base.FilteredTitle = function(htmlId)
	{
		LogicECM.module.Base.FilteredTitle.superclass.constructor.call(this, "LogicECM.module.Base.FilteredTitle", htmlId, ["container"]);

		/**
		 * Decoupled event listeners
		 */
		Bubbling.on("showFilteredLabel", this.onShowFilteredLabel, this);
		Bubbling.on("hideFilteredLabel", this.onHideFilteredLabel, this);

		return this;
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.extend(LogicECM.module.Base.FilteredTitle, Alfresco.component.Base);

		/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.Base.FilteredTitle.prototype,
		{
			onShowFilteredLabel: function FilteredTitle_onShowFilteredLabel(layer, args)
			{
				Dom.setStyle("filtered-label", "visibility", "visible");
			},

			onHideFilteredLabel: function FilteredTitle_onHideFilteredLabel(layer, args)
			{
				Dom.setStyle("filtered-label", "visibility", "hidden");
			}

		}, true);
})();