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
 * NumberRange component.
 *
 * @namespace LogicECM
 * @class LogicECM.NumberRange
 */
(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom;

	LogicECM.BaseFormTabs = function (htmlId) {
		LogicECM.BaseFormTabs.superclass.constructor.call(this, "LogicECM.BaseFormTabs", htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.BaseFormTabs, Alfresco.component.Base,
		{
			options: {
				formId: null
			},

			onReady: function () {
				var parent = Dom.get(this.options.formId + "-fields");
				var tabs = new YAHOO.widget.TabView(Dom.getElementsByClassName('yui-navset', 'div', parent)[0]);
				var prevTabHeight;
				function onBeforeActive(e) {
					var prev = e.prevValue.get("contentEl");

					prevTabHeight = parseFloat(Dom.getStyle(prev, 'height'));
				}
				function onActive(e) {
					var current = e.newValue.get("contentEl");
					var currentHeight = parseFloat(Dom.getStyle(current, 'height'));

					if ((prevTabHeight > 0) && (currentHeight < prevTabHeight)) {
						Dom.setStyle(current, 'height', prevTabHeight + 'px');
					}
					setTimeout(function () {
						LogicECM.module.Base.Util.setHeight();
					}, 10);

					YAHOO.Bubbling.fire("activeTabChange", e);
				}

				tabs.addListener('beforeActiveTabChange', onBeforeActive);
				tabs.addListener('activeTabChange', onActive);
				LogicECM.module.Base.Util.setHeight();
			}
		});
})();