(function(){
    /**
     * YUI Library aliases
     */
    var Event = YAHOO.util.Event;
    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;

    //Set block height
    function setTreeHeight() {

        var bd = Dom.get('bd');
        var block = Dom.get('lecm-page');
        var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));
        // Высота верхнего footer-a
        var footerDivTopHeight = parseInt(Dom.getStyle(Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2', 'div'), 'height'));
        // Высота нижнего footer-a
        var footerDivBottomHeight = parseInt(Dom.getStyle(Dom.get('lecm-content-ft'), 'height'));
        block = Dom.get('dictionary');
        var height = h - footerDivTopHeight - footerDivBottomHeight - 16;
        // Выставляем высоту, чтобы scrollbar был внизу перед нижним footer-ом
        Dom.setStyle(block, 'height', height +'px');
        Dom.setStyle(block, "position", "inherit");
        Dom.setStyle(block, 'width', 'auto');
    }

    function setHeaderWidth(){
        var headerWidth = parseInt(Dom.getStyle(Dom.getElementsByClassName('datalists tree')[0], 'width'));
        var footer = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
        var resizer = parseInt(Dom.getStyle(Dom.getElementsByClassName("yui-resize-handle yui-resize-handle-r"),"width"));
        // Удлиняем верхний footer при перемещении ползунка resize-ра
        Dom.setStyle(footer,'width',headerWidth+resizer+'px');
    }

    Event.onDOMReady(function() {
        Bubbling.on("GridRendered", setTreeHeight, this);
        Bubbling.on("HeightSetted", setTreeHeight, this);
        Bubbling.on("SetHeaderWidth",setHeaderWidth, this);
        Event.on(Dom.getElementsByClassName("datalists tree"),"scroll",function(e){
            var headerWidth = Dom.getElementsByClassName('datalists tree')[0].scrollWidth;
            var footer = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
            var resizer = parseInt(Dom.getStyle(Dom.getElementsByClassName("yui-resize-handle yui-resize-handle-r"),"width"));
            // Удлиняем верхний footer при перемещении ползунка scrollbar-а
            Dom.setStyle(footer,'width',headerWidth+resizer+'px');
        },this,true);
    });

})();