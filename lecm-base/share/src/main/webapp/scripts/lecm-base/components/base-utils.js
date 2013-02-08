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