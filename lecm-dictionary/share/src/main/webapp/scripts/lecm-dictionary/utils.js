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

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));
        var footerHeight = parseInt(Dom.getStyle(Dom.get('lecm-content-ft'),'height'))*2;

        block = Dom.get('dictionary');
        Dom.setStyle(block, 'height', h-footerHeight - 16 +'px');
    }

    function setScroll(){
        var w = Dom.getElementsByClassName('datalists tree')[0].scrollWidth;
        var footer = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
        Dom.setStyle(footer,'width',w+'px');
    }

    // Recalculate the vertical size on a browser window resize event
    Event.on(window, "resize", function(e) {
        setHeight();
    }, this, true);


    Event.onDOMReady(function() {
        setHeight();
//        setScroll();
        Event.on(Dom.getElementsByClassName("datalists tree"),"scroll",function(e){
            setScroll();
        },this,true);
    });

})();