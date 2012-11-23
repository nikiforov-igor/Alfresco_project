(function(){
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    //Set block height
    function setHeight() {
        var bd = Dom.get('bd');
        var block = Dom.get('lecm-page');
        var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

        Dom.setStyle(block, 'height', 'auto');

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));

        Dom.setStyle(block, 'height', h + 'px');

        block = Dom.get('dictionary');
        Dom.setStyle(block, 'height', h-78+'px');
    }

    // Recalculate the vertical size on a browser window resize event
    Event.on(window, "resize", function(e) {
        setHeight();
    }, this, true);

    Event.onDOMReady(function() {
        setHeight();
    });

})();