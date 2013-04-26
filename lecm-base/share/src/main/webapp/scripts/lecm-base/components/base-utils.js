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
        var Dom = YAHOO.util.Dom;
        var Bubbling = YAHOO.Bubbling;
        var bd = Dom.get('bd');
        var block = Dom.get('lecm-page');
        var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');
        var minHeight = parseInt(Dom.getStyle(block, 'min-height'));
        Bubbling.fire("HeightSetting");
        Dom.setStyle(block, 'height', 'auto');

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));

        Dom.setStyle(block, 'min-height', h + 'px');
        Bubbling.fire("HeightSetted");
    },

    setDashletsHeight: function(dashletsBlockId, numberDashlets) {
        var Dom = YAHOO.util.Dom;

        var bd = Dom.get('doc-bd');
        var dashletsBlock = Dom.get(dashletsBlockId);

        var bdHeight = parseInt(Dom.getStyle(bd, 'height'));
        Dom.setStyle(dashletsBlock, 'height', 'auto');
        Dom.setStyle(dashletsBlock, 'min-height', bdHeight + 'px');

        var dashlets = Dom.getElementsByClassName('dashlet', 'div');
        var dashletsMarginTop = parseInt(Dom.getStyle(dashlets, 'margin-top'));
        var dashletsMarginBottom = parseInt(Dom.getStyle(dashlets, 'margin-bottom'));

        var h = (bdHeight - dashletsMarginTop - dashletsMarginBottom - (numberDashlets - 1) * Math.max(dashletsMarginTop, dashletsMarginBottom))/numberDashlets;

        Dom.setStyle(dashlets, 'height', 'auto');
        Dom.setStyle(dashlets, 'min-height', h + 'px');
    },

    /**
     * Add a URL parameter (or changing it if it already exists)
     * @param {search} string  this is typically document.location.search
     * @param {key}    string  the key to set
     * @param {val}    string  value
     */
    addUrlParam: function(search, key, val){
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
	}
};

(function(){
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
        onResize: function(width){
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
                Dom.setStyle(this.options.divRight, "margin-left", 8 + width + "px");
            }
            YAHOO.Bubbling.fire("SetHeaderWidth");
        }
    });

})();