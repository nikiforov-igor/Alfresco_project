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

/**
 * LogicECM Base Util
 *
 * @namespace LogicECM
 */
LogicECM.module.Base.Util = {
	/*
	 * Set common block height
	 */
	setHeight: function() {
		var Dom = YAHOO.util.Dom,
				Bubbling = YAHOO.Bubbling;
		var bd = Dom.get('bd');
		var block = Dom.get('lecm-page');
		var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

		Bubbling.fire("HeightSetting");
		Dom.setStyle(block, 'height', 'auto');

		var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
				- parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));

		Dom.setStyle(block, 'min-height', h + 'px');
		Bubbling.fire("HeightSetted");
	},
	/*
	 * Set document page height
	 */
	setDocPageHeight: function() {
		var Dom = YAHOO.util.Dom;
		var doc = Dom.get('doc-bd');
		var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

		Dom.setStyle(doc, 'height', 'auto');

		var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(doc)
				- parseInt(Dom.getStyle(doc, 'margin-bottom'))
				- parseInt(Dom.getStyle(doc, 'border-top-width')) - parseInt(Dom.getStyle(doc, 'border-bottom-width'));

		Dom.setStyle(doc, 'min-height', h + 'px');
	},
	setDashletsHeight: function(dashletsBlockId) {
		var Dom = YAHOO.util.Dom;
		var page = Dom.get('lecm-page');
		var dashletsBlock = Dom.get(dashletsBlockId);

		if (!dashletsBlock) {
			return;
		}
		var pageHeight = parseInt(Dom.getStyle(page, 'height')) || parseInt(Dom.getStyle(page, 'min-height'));

		pageHeight = pageHeight - parseInt(Dom.getElementsByClassName('header-bar', 'div', page)[0].offsetHeight)
				- parseInt(Dom.get('lecm-content-ft').offsetHeight);
		Dom.setStyle(dashletsBlock, 'height', 'auto');
		Dom.setStyle(dashletsBlock, 'min-height', pageHeight + 'px');

		var dashlets = Dom.getElementsByClassName('dashlet', 'div', dashletsBlock);
		var dashletsCount = Math.round(dashlets.length / 2);
		var dashlet = dashlets[0];
		var dashletHeader = Dom.getElementsByClassName('dashlet-title', 'div', dashlet);
		var dashletMarginTop = parseInt(Dom.getStyle(dashlet, 'margin-top'));
		var dashletMarginBottom = parseInt(Dom.getStyle(dashlet, 'margin-bottom'));

		var h = (pageHeight - dashletMarginTop - dashletMarginBottom - (dashletsCount - 1) * Math.max(dashletMarginTop, dashletMarginBottom)) / dashletsCount;
		h = h - parseInt(parseInt(dashletHeader[0].offsetHeight));

		for (var i = 0; i < dashlets.length; i++) {
			dashlet = dashlets[i];
			Dom.setStyle(Dom.getElementsByClassName('dashlet-body', 'div', dashlet), 'height', Math.floor(h) + 'px');
		}
	},
	/**
	 * Get the URL parameter by key
	 * @param key - string : the key of parameter
	 */
	getUrlParam: function(key) {
		var query = document.location.search.split("+").join(" ");

		var params = {}, tokens,
				re = /[?&]?([^=]+)=([^&]*)/g;

		while (tokens = re.exec(query)) {
			params[decodeURIComponent(tokens[1])]
					= decodeURIComponent(tokens[2]);
		}

		return params[key];
	},
	/**
	 * Add a URL parameter (or changing it if it already exists)
	 * @param {search} string  this is typically document.location.search
	 * @param {key}    string  the key to set
	 * @param {val}    string  value
	 */
	addUrlParam: function(search, key, val) {
		var newParam = key + '=' + val,
				params = '?' + newParam;


		// If the "search" string exists, then build params from it
		if (search) {
			if (search.match(key + '[^&]*') != null) {
				// Try to replace an existance instance
				params = search.replace(new RegExp(key + '[^&]*'), newParam);
			} else {
				// If nothing was replaced, then add the new param to the end
				params = search + '&' + newParam;
			}
		}
		location.search = params;
	},
	/**
	 * Найти на странице компонент, имеющий определенный bubbling label.
	 * @param {string} p_sName название компонента. Например, "LogicECM.module.Base.DataGrid"
	 * @param {string} bubblingLabel bullbling label компонента
	 */
	findComponentByBubblingLabel: function(p_sName, bubblingLabel) {
		var components = [];
		var found = [];
		var bMatch, component;

		components = Alfresco.util.ComponentManager.list();

		for (var i = 0, j = components.length; i < j; i++) {
			component = components[i];
			bMatch = true;
			if (component['name'].search(p_sName) == -1) {
				bMatch = false;
			}
			if (bMatch) {
				found.push(component);
			}
		}
		if (bubblingLabel) {
			for (i = 0, j = found.length; i < j; i++) {
				component = found[i];
				if (typeof component == "object" && component.options.bubblingLabel) {
					if (component.options.bubblingLabel == bubblingLabel) {
						return component;
					}
				}
			}
		} else {
			return (typeof found[0] == "object" ? found[0] : null);
		}
		return null;
	},
	printReport: function(nodeRef, reportId) {
		Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/report/" + reportId + "?ID=" + encodeURI(nodeRef),
			successCallback: {
				fn: function(response) {
					window.open(window.location.protocol + "//" + window.location.host + response.serverResponse.responseText,
							"report", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");

					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("message.report.success")
					});
				}
			},
			failureCallback: {
				fn: function(response) {
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("message.report.failure")
					});
				}
			}
		});
	},
	/**
	 * Деструктор для форм
	 * @param {Object} event собитие, на которое мы срегировали (обычно, destroy)
	 * @param {Object} args дополнительные параметры, переданные при иницировании события. обычно, null
	 * @param {Object} params объект, имеющий следующие атрибуты:
	 *  {String} moduleId - ID модуля, по которому его можно найти в ComponentManager, обязательный
	 *  {Function} callback - функция, выполняющаяся перед основной работой деструктора, опциональный
	 *  {Object} callbackArg объект, передающийся параметром в callback, опциональный
	 */
	formDestructor: function(event, args, params) {
		var formRelatedElementsSelectorTemplate = "[id^='{moduleId}']";
		var moduleId = params.moduleId, callback = params.callback, callbackArg = params.callbackArg;

		var k, w;

		var isFn = YAHOO.lang.isFunction;

		var comMan = Alfresco.util.ComponentManager;
		var components = comMan.list();

		if (isFn(callback)) {
			callback.call(this, callbackArg);
		}

		var form = comMan.get(moduleId);
		var formIndex = components.indexOf(form); // IE9+
		var widgets = (form) ? form.widgets : [];

		for (k in widgets) {
			if (widgets.hasOwnProperty(k)) {
				w = widgets[k];

				if (w.hasOwnProperty('nodeName') && w.hasOwnProperty('tagName')) {
					$(w).remove();
					continue;
				}

				if (isFn(w.get)) {
					if (w.get('element') === null) {
						continue;
					}
				}

				if (isFn(w.destroy)) {
					w.destroy();
				}
			}
		}

		if (formIndex > -1) {
			while (components.length > formIndex) { // Не оптимизируй...
				if (components[formIndex].name != "Alfresco.HtmlUpload" &&
					components[formIndex].name != "LogicECM.DndUploader" &&
					components[formIndex].name != "Alfresco.FlashUpload" &&
					components[formIndex].name != "Alfresco.FileUpload") {
					LogicECM.module.Base.Util.removeAllBubbles(components[formIndex]);
//					if (components[formIndex].destroy != undefined) {
//						components[formIndex].destroy();
//					}
					comMan.unregister(components[formIndex]);
				} else {
					formIndex++;
				}
			}
		}

		// жесточайшим образом убить все элементы, ID которых начинается c moduleId
		$(YAHOO.lang.substitute(formRelatedElementsSelectorTemplate, {
			moduleId: moduleId
		})).remove();
	},

	removeAllBubbles: function (obj) {
		var event;
		var bubble = YAHOO.Bubbling.bubble;

		for (event in bubble) {
			if (bubble.hasOwnProperty(event)) {
				bubble[event].subscribers.forEach(function(s) {
					if (s.obj === obj) {
						YAHOO.Bubbling.unsubscribe(event, s.fn, s.obj);
					}
				});
			}
		}
	},

	destroyForm: function(formId) {
		var comMan = Alfresco.util.ComponentManager;
		var components = comMan.list();

		for (var i = components.length - 1; i >= 0; i--) {
			var component = components[i];
			if (component.id != null &&
				component.id.indexOf(formId) == 0 &&
				component.name != "Alfresco.HtmlUpload" &&
				component.name != "LogicECM.DndUploader" &&
				component.name != "Alfresco.FlashUpload" &&
				component.name != "Alfresco.FileUpload") {
					this.removeAllBubbles(component);
					if (typeof component.destroy == "function") {
						component.destroy();
					}
					comMan.unregister(component);
			}
		}
	}
};

(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event;

	// Recalculate the vertical size on a browser window resize event
	Event.on(window, "resize", function(e) {
		LogicECM.module.Base.Util.setHeight();
	}, this, true);

	Event.onDOMReady(function() {
		LogicECM.module.Base.Util.setHeight();
	});

	/**
	 * Base resizer
	 */
	LogicECM.module.Base.Resizer = function(name) {
		LogicECM.module.Base.Resizer.superclass.constructor.call(this, name);
		return this;
	};

	YAHOO.extend(LogicECM.module.Base.Resizer, Alfresco.widget.Resizer, {
		marginLeft: 8,
		onResize: function(width) {
			var cn = Dom.get(this.options.divLeft).childNodes,
					handle = cn[cn.length - 1];

			Dom.setStyle(this.options.divLeft, "height", "auto");
			Dom.setStyle(handle, "height", "");

			var h = Dom.getY("lecm-content-ft") - Dom.getY("lecm-content-main");

			if (h < this.MIN_FILTER_PANEL_HEIGHT) {
				h = this.MIN_FILTER_PANEL_HEIGHT;
			}

			Dom.setStyle(handle, "height", h + "px");

			if (width !== undefined) {
				// 8px breathing space for resize gripper
				Dom.setStyle(this.options.divRight, "margin-left", this.marginLeft + width + "px");
			}
			YAHOO.Bubbling.fire("SetHeaderWidth");
		}
	});

})();