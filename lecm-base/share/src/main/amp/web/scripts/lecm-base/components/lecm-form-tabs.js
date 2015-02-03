/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

/**
 * NumberRange component.
 *
 * @namespace LogicECM
 * @class LogicECM.NumberRange
 */
(function () {

	function setTabbingOrder(formId, tabs) {
		var parent = Dom.get(formId + '-fields');
		var buttons = Selector.query('.form-buttons span.yui-button button', formId);
		var inDialog = Selector.test(Dom.get(formId), '.yui-dialog form');

		if (parent) {
			var tabNavs = Selector.query('.yui-nav li a', parent);
			var tabContents = Selector.query('.yui-content .tab', parent);
			var elements;

			for (var i = 0; i < tabNavs.length; i++) {
				var tabNav = tabNavs[i];
				var tabContent = tabContents[i];

				if (tabContent) {
					elements = Selector.query('div.control', tabContent);
					elements.unshift(tabNav);

					if (buttons && !buttons.isEmpty) {
						if (i == 0) {
							for (var j = 0; j < buttons.length; j++) {
								elements.push(buttons[j]);
							}
						} else {
							//из последнего контрола таба переходить на кнопки
							var lastControl = elements[elements.length - 1];

							new KeyListener(lastControl, {keys: KeyListener.KEY.TAB},
								{
									fn: function (a, args) {
										var e = args[1];

										buttons[0].focus();
										e.preventDefault();
										e.stopPropagation();
									},
									scope: this,
									correctScope: true
								}, KeyListener.KEYDOWN).enable();
						}

						// по табу на последней кнопке уходить на первый таб
						new KeyListener(buttons[buttons.length - 1], {keys: KeyListener.KEY.TAB},
							{
								fn: function (a, args) {
									tabs.selectTab(0);
								},
								scope: this,
								correctScope: true
							}, KeyListener.KEYDOWN).enable();
					}


					LogicECM.module.Base.Util.setElementsTabbingOrder(elements, inDialog, i == 0);

					if (i < tabNavs.length - 1) {
						// на следующюю вкладку
						new KeyListener(tabNav, {keys: KeyListener.KEY.RIGHT},
							{
								fn: function (a, args) {
									var ind = tabs.get('activeIndex') + 1;

									tabNavs[ind].focus();
									tabs.selectTab(ind);
								},
								scope: this,
								correctScope: true
							}, KeyListener.KEYDOWN).enable();
					}
					if (i > 0) {
						// на предыдущую вкладку
						new KeyListener(tabNav, {keys: KeyListener.KEY.LEFT},
							{
								fn: function (a, args) {
									var ind = tabs.get('activeIndex') - 1;

									tabNavs[ind].focus();
									tabs.selectTab(ind);
								},
								scope: this,
								correctScope: true
							}, KeyListener.KEYDOWN).enable();
					}
				}
			}
		}
	}

	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Selector = YAHOO.util.Selector,
		KeyListener = YAHOO.util.KeyListener;

	LogicECM.BaseFormTabs = function (htmlId) {
		LogicECM.BaseFormTabs.superclass.constructor.call(this, 'LogicECM.BaseFormTabs', htmlId, ['tabview']);
		this.tabs = new YAHOO.widget.TabView(htmlId);
		Dom.removeClass(htmlId, 'hidden');
		return this;
	};

	YAHOO.extend(LogicECM.BaseFormTabs, Alfresco.component.Base, {
		tabs: null,

		options: {
			formId: null
		},

		onReady: function () {
			var prevTabHeight;
			function onBeforeActive(e) {
				var prev = e.prevValue.get('contentEl');

				prevTabHeight = parseFloat(Dom.getStyle(prev, 'height'));
			}
			function onActive(e) {
				var current = e.newValue.get('contentEl');
				var currentHeight = parseFloat(Dom.getStyle(current, 'height'));

				if ((prevTabHeight > 0) && (currentHeight < prevTabHeight)) {
					Dom.setStyle(current, 'min-height', prevTabHeight + 'px');
				}

				// Для таблиц с фиксированным заголовком
				// установить ширину ячеек хедера
				// т.к. пока таб был неактивен, они схлопнулись (при фиксации заголовка)
				var grids = Selector.query('.grid table.fixedHeader', current);
				if (grids && grids.length > 0) {
					for (var i = 0; i < grids.length; i++) {
						LogicECM.module.Base.Util.setFixedHeaderWidths(grids[i]);
					}
				}

				YAHOO.Bubbling.fire('activeTabChange', e);
			}

			this.tabs.addListener('beforeActiveTabChange', onBeforeActive);
			this.tabs.addListener('activeTabChange', onActive);

			YAHOO.Bubbling.fire('tabsRendered');

			setTabbingOrder(this.options.formId, this.tabs);
		}
	}, true);
})();
