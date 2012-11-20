(function(){
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom;
    var resizer = new Alfresco.widget.Resizer("BaseResizer");

    resizer.onResize = function(width) {
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
    };

})();