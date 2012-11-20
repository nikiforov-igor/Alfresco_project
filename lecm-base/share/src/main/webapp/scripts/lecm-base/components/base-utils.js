(function(){
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    //Set block height
    function setHeight() {
        var block = Dom.get('lecm-page');
        var footer = Dom.getAncestorByClassName('alf-ft', 'sticky-footer');

        Dom.setStyle(block, 'height', 'auto');

        var h = Dom.getY(footer) - parseInt(Dom.getStyle(footer, 'margin-top')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom'));

        Dom.setStyle(block, 'height', h + 'px');
    }

    // Recalculate the vertical size on a browser window resize event
    Event.on(window, "resize", function(e) {
        setHeight();
    }, this, true);

    Event.onDOMReady(function() {
        setHeight();
    });

})();